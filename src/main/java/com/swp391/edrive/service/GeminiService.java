package com.swp391.edrive.service;

import com.swp391.edrive.entity.*;
import com.swp391.edrive.repository.*;
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
    private final DealerRepository dealerRepository;
    private final AdditionalServicesRepository additionalServicesRepository;
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
               - Bước 1: Bạn tạo đơn hàng và chờ hãng duyệt).
               - Bước 2: Hãng duyệt và tạo hợp đồng (Đại lý không được hủy lúc này).
               - Bước 3: Hãng và bạn sẽ kí hợp đồng.
               - Bước 4: Sau khi kí hợp đồng xong bạn vui lòng upload hoá đơn thanh toán.
               - Bước 5: Sau khi hãng biết bạn đã thanh toán hãng sẽ xác nhận và thông báo cho bạn.
               - Bước 6: Khi nào giao xe hãng sẽ không báo cho bạn biết.
               - Lưu ý: Chỉ được huỷ đơn khi hãng chưa duyệt.
            
            3. TRA CỨU SẢN PHẨM (CATALOG):
               - Giá hiển thị là "Giá bán lẻ đề xuất" (Retail Price).
               - Pin và Sạc đi kèm xe, bảo hành theo chính sách hãng.
            """;

    // --- 2. PROMPT TƯ DUY (BRAIN) ---
    private static final String OPERATIONAL_PROMPT = """
            VAI TRÒ: Bạn là Trợ lý Vận hành AI nội bộ (Internal Operations Assistant).
            NGƯỜI DÙNG (USER) ĐANG CHAT VỚI BẠN LÀ: QUẢN LÝ ĐẠI LÝ (DEALER MANAGER).
            MỐI QUAN HỆ: Bạn là nhân viên ảo, User là sếp của bạn.
            
            NHIỆM VỤ:
            1. Trả lời các câu hỏi về thông tin đại lý, tồn kho, đơn hàng và danh mục sản phẩm.
            2. KHI KHÁCH HỎI "DANH SÁCH TẤT CẢ CÁC XE":
               - Hãy liệt kê đầy đủ các mẫu xe có trong mục [C. DANH MỤC XE].
               - Trình bày thông tin rõ ràng, ngắn gọn (Tên xe, Phiên bản, Màu, Giá).
               - Nếu danh sách dài, hãy nhóm theo Dòng xe (Ví dụ: Nhóm E8, Nhóm E3...).
            
            DỮ LIỆU THỰC TẾ CỦA ĐẠI LÝ (ID: {{DEALER_ID}}):
            ----------------------------------------------------
            [0. HỒ SƠ ĐẠI LÝ CỦA SẾP] (Thông tin User đang quản lý):
            {{DEALER_PROFILE}}   <--- QUAN TRỌNG: Đừng xóa dòng này, đây là chỗ điền tên Đại lý
            
            [A. KHO XE HIỆN TẠI] (Hàng có sẵn tại đại lý):
            {{MY_INVENTORY}}
            
            [B. ĐƠN NHẬP HÀNG] (Hàng đang về):
            {{MY_ORDERS}}
            
            [C. DANH MỤC XE TOÀN HỆ THỐNG] (Catalog đầy đủ để tra cứu/đặt hàng):
            {{MANUFACTURER_CATALOG}}
            
            [D. DỊCH VỤ & PHỤ KIỆN BỔ SUNG] (Các gói dịch vụ, bảo hiểm, phụ kiện đang kinh doanh):
                        {{ADDITIONAL_SERVICES}}
            ----------------------------------------------------
            
            HƯỚNG DẪN TƯ DUY VÀ TRẢ LỜI:
            1. ĐỊNH DANH NGƯỜI DÙNG:
               - Nếu User hỏi "Tôi là ai?", "Đây là đại lý nào?", "Thông tin của tôi?":
               - TUYỆT ĐỐI KHÔNG trả lời "Bạn là khách hàng".
               - HÃY ĐỌC mục [0. HỒ SƠ ĐẠI LÝ] và trả lời: "Dạ, anh/chị là Quản lý của đại lý [Tên Đại Lý] ạ." kèm theo địa chỉ và hotline.
            
            2. TƯ DUY BÁN HÀNG & KHO:
               - Dữ liệu ở mục [C] là toàn bộ xe hãng có. Dù kho [A] hết hàng, bạn vẫn phải trả lời được thông tin xe (giá, màu, pin) dựa trên mục [C].
               - Khi liệt kê xe, hãy dùng Emoji (🚗, 💰, 🔋) cho sinh động.
            
            3. TƯ VẤN DỊCH VỤ:
                           - Khi khách hỏi "Có dịch vụ gì?", "Gói bảo dưỡng nào?", hãy tra cứu mục [D].
                           - Cung cấp Tên, Giá và Mô tả ngắn gọn.
            """;

    public GeminiService(ChatClient.Builder chatClientBuilder,
                         VehicleRepository vehicleRepository,
                         OrderRepository orderRepository,
                         DealerInventoryRepository dealerInventoryRepository, DealerRepository dealerRepository, AdditionalServicesRepository additionalServicesRepository) {
        this.vehicleRepository = vehicleRepository;
        this.orderRepository = orderRepository;
        this.dealerInventoryRepository = dealerInventoryRepository;
        this.chatClient = chatClientBuilder.build();
        this.dealerRepository = dealerRepository;
        this.additionalServicesRepository = additionalServicesRepository;
    }

    /**
     * Hàm xử lý chat chính.
     * Sử dụng @Transactional(readOnly = true) để tránh lỗi LazyInitializationException khi đọc OrderItems.
     */
    @Transactional(readOnly = true)
    public String chat(String userMessage, String userId, Long dealerId) {

        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new RuntimeException("Dealer not found"));

        String dealerProfileStr = String.format(
                "- Tên đại lý: %s\n- Email: %s\n- Hotline: %s\n- Địa chỉ: %s, %s, %s, %s",
                dealer.getDealerName(),
                dealer.getDealerEmail(),
                dealer.getPhone(), // Hoặc contactPhone tùy entity của bạn
                dealer.getHouseNumberAndStreet(),
                dealer.getWardOrCommune(),
                dealer.getDistrict(),
                dealer.getProvinceOrCity()
        );

        // BƯỚC 1: LẤY DỮ LIỆU KHO (Real-time Inventory)
        List<DealerInventory> inventories = dealerInventoryRepository.findByDealer_DealerId(dealerId);
        String inventoryStr = formatInventory(inventories);

        // BƯỚC 2: LẤY ĐƠN HÀNG CỦA ĐẠI LÝ (Real-time Orders)
        List<Order> orders = orderRepository.findByDealer_DealerId(dealerId);
        String orderStr = formatOrders(orders);

        // BƯỚC 3: LẤY CATALOG (Product Knowledge)
        List<Vehicle> vehicles = vehicleRepository.findAll();
        String catalogStr = formatCatalog(vehicles);

        List<AdditionalServices> services = additionalServicesRepository.findByIsActiveTrue();
        String servicesStr = formatServices(services);

        // BƯỚC 4: GHÉP DỮ LIỆU VÀO PROMPT
        String finalPrompt = OPERATIONAL_PROMPT
                .replace("{{DEALER_PROFILE}}", dealerProfileStr)
                .replace("{{DEALER_ID}}", String.valueOf(dealerId))
                .replace("{{MY_INVENTORY}}", inventoryStr)
                .replace("{{MY_ORDERS}}", orderStr)
                .replace("{{MANUFACTURER_CATALOG}}", catalogStr)
                .replace("{{ADDITIONAL_SERVICES}}", servicesStr)
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

        return list.stream()
                .map(v -> String.format(
                        "- 🆔 ID:%d | 🚗 %s %s | 🎨 Màu: %s | 🔋 Pin: %dkWh (%dkm) | 💰 Giá: %s VNĐ | 🏁 Status: %s",
                        v.getVehicleId(),
                        v.getModelName(),
                        v.getVersion(),
                        (v.getColor() != null ? v.getColor().getColorName() : "Tiêu chuẩn"),
                        v.getBatteryCapacityKwh(),
                        v.getRangeKm(),
                        v.getPriceRetail().toPlainString(), // Hiển thị giá đầy đủ
                        v.getStatus()
                ))
                .collect(Collectors.joining("\n"));
    }

    private String formatServices(List<AdditionalServices> list) {
        if (list == null || list.isEmpty()) return "(Không có dịch vụ bổ sung nào đang kích hoạt)";

        return list.stream()
                .map(s -> String.format(
                        "- 🛠 %s | 💵 Giá: %s VNĐ | ℹ️ %s | 📂 Loại: %s",
                        s.getServiceName(), // Tên dịch vụ
                        (s.getPrice() != null ? s.getPrice().toPlainString() : "Liên hệ"), // Giá
                        s.getDescription(), // Mô tả
                        s.getCategory()     // Loại (nếu có)
                ))
                .collect(Collectors.joining("\n"));
    }
}