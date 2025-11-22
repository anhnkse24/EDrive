package com.swp391.edrive.service;

import com.swp391.edrive.entity.DealerInventory;
import com.swp391.edrive.entity.Order;
import com.swp391.edrive.entity.Vehicle;
import com.swp391.edrive.repository.DealerInventoryRepository;
import com.swp391.edrive.repository.OrderRepository;
import com.swp391.edrive.repository.VehicleRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class GeminiService {

    private final ChatClient chatClient;
    private final VehicleRepository vehicleRepository;
    private final OrderRepository orderRepository;
    private final DealerInventoryRepository dealerInventoryRepository;

    // Bộ nhớ chat: Key = userId (hoặc session), Value = Lịch sử chat
    private final Map<String, List<Message>> chatHistory = new ConcurrentHashMap<>();

    // --- 1. KIẾN THỨC NGHIỆP VỤ (SOP - Standard Operating Procedure) ---
    // Đây là "Luật" mà AI phải tuân theo. Bạn có thể sửa text này để dạy AI quy trình mới.
    private static final String SYSTEM_MANUAL = """
            == CẨM NANG VẬN HÀNH ĐẠI LÝ E-DRIVE ==
            
            1. QUẢN LÝ KHO (INVENTORY):
               - Dữ liệu kho hiển thị bên dưới là hàng ĐANG CÓ SẴN tại đại lý để bán ngay.
               - Nếu kho hết hàng, hãy kiểm tra mục "Đơn nhập hàng" xem có đơn nào đang về không.
            
            2. QUY TRÌNH NHẬP HÀNG (ORDER):
               - Bước 1: Admin tạo đơn nhập -> Trạng thái PENDING (Chờ hãng duyệt).
               - Bước 2: Hãng duyệt -> Trạng thái CONFIRMED (Đại lý không được hủy lúc này).
               - Bước 3: Xe về -> Đại lý bấm "Xác nhận giao hàng" (Confirm Delivery) -> Xe tự động cộng vào kho.
               - Lưu ý: Chỉ được Hủy đơn (Cancel) khi trạng thái là PENDING.
            
            3. TRA CỨU SẢN PHẨM (CATALOG):
               - Giá hiển thị là "Giá bán lẻ đề xuất" (Retail Price).
               - Pin và Sạc đi kèm xe, bảo hành theo chính sách hãng.
            """;

    // --- 2. PROMPT TƯ DUY (BRAIN) ---
    private static final String OPERATIONAL_PROMPT = """
            VAI TRÒ: Bạn là Trợ lý Vận hành AI cao cấp của Đại lý EDrive.
            
            NHIỆM VỤ:
            1. Trả lời các câu hỏi về tồn kho, đơn hàng và danh mục sản phẩm.
            2. KHI KHÁCH HỎI "DANH SÁCH TẤT CẢ CÁC XE":
               - Hãy liệt kê đầy đủ các mẫu xe có trong mục [C. DANH MỤC XE].
               - Trình bày thông tin rõ ràng, ngắn gọn (Tên xe, Phiên bản, Màu, Giá).
               - Nếu danh sách dài, hãy nhóm theo Dòng xe (Ví dụ: Nhóm E8, Nhóm E3...).
            
            DỮ LIỆU THỰC TẾ CỦA ĐẠI LÝ (ID: {{DEALER_ID}}):
            ----------------------------------------------------
            [A. KHO XE HIỆN TẠI] (Hàng có sẵn tại đại lý):
            {{MY_INVENTORY}}
            
            [B. ĐƠN NHẬP HÀNG] (Hàng đang về):
            {{MY_ORDERS}}
            
            [C. DANH MỤC XE TOÀN HỆ THỐNG] (Catalog đầy đủ để tra cứu/đặt hàng):
            {{MANUFACTURER_CATALOG}}
            ----------------------------------------------------
            
            HƯỚNG DẪN TƯ DUY:
            - Dữ liệu ở mục [C] là toàn bộ xe mà hãng sản xuất. Dù kho [A] hết hàng, bạn vẫn phải trả lời được thông tin xe dựa trên mục [C].
            - Sử dụng Emoji phù hợp.
            """;

    public GeminiService(ChatClient.Builder chatClientBuilder,
                         VehicleRepository vehicleRepository,
                         OrderRepository orderRepository,
                         DealerInventoryRepository dealerInventoryRepository) {
        this.vehicleRepository = vehicleRepository;
        this.orderRepository = orderRepository;
        this.dealerInventoryRepository = dealerInventoryRepository;
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Hàm xử lý chat chính.
     * Sử dụng @Transactional(readOnly = true) để tránh lỗi LazyInitializationException khi đọc OrderItems.
     */
    @Transactional(readOnly = true)
    public String chat(String userMessage, String userId, Long dealerId) {

        // BƯỚC 1: LẤY DỮ LIỆU KHO (Real-time Inventory)
        List<DealerInventory> inventories = dealerInventoryRepository.findByDealer_DealerId(dealerId);
        String inventoryStr = formatInventory(inventories);

        // BƯỚC 2: LẤY ĐƠN HÀNG CỦA ĐẠI LÝ (Real-time Orders)
        List<Order> orders = orderRepository.findByDealer_DealerId(dealerId);
        String orderStr = formatOrders(orders);

        // BƯỚC 3: LẤY CATALOG (Product Knowledge)
        List<Vehicle> vehicles = vehicleRepository.findAll();
        String catalogStr = formatCatalog(vehicles);

        // BƯỚC 4: GHÉP DỮ LIỆU VÀO PROMPT
        String finalPrompt = OPERATIONAL_PROMPT
                .replace("{{DEALER_ID}}", String.valueOf(dealerId))
                .replace("{{MY_INVENTORY}}", inventoryStr)
                .replace("{{MY_ORDERS}}", orderStr)
                .replace("{{MANUFACTURER_CATALOG}}", catalogStr)
                + "\n\n" + SYSTEM_MANUAL;

        // BƯỚC 5: XỬ LÝ BỘ NHỚ (MEMORY)
        List<Message> history = chatHistory.getOrDefault(userId, new ArrayList<>());

        // Chuẩn bị tin nhắn gửi lên Google Gemini
        List<Message> messagesToSend = new ArrayList<>();
        messagesToSend.add(new SystemMessage(finalPrompt)); // Luôn gửi System Prompt mới nhất chứa data cập nhật
        messagesToSend.addAll(history);                     // Kèm lịch sử cũ
        messagesToSend.add(new UserMessage(userMessage));   // Câu hỏi mới

        // BƯỚC 6: GỌI AI
        String response = chatClient.prompt(new Prompt(messagesToSend)).call().content();

        // BƯỚC 7: CẬP NHẬT LỊCH SỬ
        history.add(new UserMessage(userMessage));
        history.add(new AssistantMessage(response));

        // Giữ lại 10 cặp hội thoại gần nhất để tiết kiệm bộ nhớ
        if (history.size() > 20) {
            history = history.subList(history.size() - 20, history.size());
        }
        chatHistory.put(userId, history);

        return response;
    }

    // --- CÁC HÀM FORMAT DATA (Biến Object thành Text dễ đọc & Tiết kiệm Token) ---

    private String formatInventory(List<DealerInventory> list) {
        if (list.isEmpty()) return "(Kho hiện tại đang trống)";
        // Chỉ lấy xe có số lượng > 0
        return list.stream()
                .filter(i -> i.getQuantity() > 0)
                .map(i -> String.format("- 📦 %s %s (Màu: %s): Sẵn sàng %d xe",
                        i.getVehicle().getModelName(),
                        i.getVehicle().getVersion(),
                        (i.getVehicle().getColor() != null ? i.getVehicle().getColor().getColorName() : "N/A"),
                        i.getQuantity()))
                .collect(Collectors.joining("\n"));
    }

    private String formatOrders(List<Order> list) {
        if (list.isEmpty()) return "(Chưa có lịch sử nhập hàng)";
        // Sắp xếp đơn mới nhất lên đầu
        list.sort((o1, o2) -> {
            if (o2.getOrderDate() == null || o1.getOrderDate() == null) return 0;
            return o2.getOrderDate().compareTo(o1.getOrderDate());
        });

        // Lấy 5 đơn gần nhất
        return list.stream().limit(5).map(o -> {
            String items = o.getOrderItems().stream()
                    .map(oi -> String.format("%dx %s", oi.getQuantity(), oi.getVehicle().getModelName()))
                    .collect(Collectors.joining(", "));
            return String.format("- 📄 Đơn #%s (%s) | Status: %s | Gồm: [%s]",
                    o.getOrderId(), o.getOrderDate(), o.getStatus(), items);
        }).collect(Collectors.joining("\n"));
    }

    // Trong GeminiService.java

    private String formatCatalog(List<Vehicle> list) {
        if (list.isEmpty()) return "(Danh mục xe trống)";

        // BỎ .limit(20) để lấy tất cả xe
        // Format dạng text rõ ràng để AI dễ đọc
        return list.stream()
                .map(v -> String.format(
                        "- 🆔 ID:%d | 🚗 %s %s | 🎨 Màu: %s | 🔋 Pin: %dkWh (%dkm) | 💰 Giá: %s VNĐ | 🏁 Status: %s",
                        v.getVehicleId(),
                        v.getModelName(),
                        v.getVersion(),
                        (v.getColor() != null ? v.getColor().getColorName() : "N/A"),
                        v.getBatteryCapacityKwh(),
                        v.getRangeKm(),
                        v.getPriceRetail().toPlainString(), // Hiển thị giá đầy đủ
                        v.getStatus()
                ))
                .collect(Collectors.joining("\n"));
    }
}