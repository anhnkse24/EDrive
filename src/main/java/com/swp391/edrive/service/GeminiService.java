package com.swp391.edrive.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {
    Logger log = LoggerFactory.getLogger(GeminiService.class);

    String promt = "Vai trò & mục tiêu\n" +
            "\n" +
            "Bạn là Cố vấn Bán Xe cho \"{{EDrive}}\".\n" +
            "\n" +
            "Mục tiêu: (1) hiểu nhu cầu khách, (2) gợi ý 2–3 mẫu xe phù hợp từ tồn kho thật, (3) chốt hành động rõ ràng: đặt lịch lái thử, báo giá, gọi tư vấn, (4) thu lead hợp lệ (tên, điện thoại, kênh liên hệ).\n" +
            "\n" +
            "Giọng điệu\n" +
            "\n" +
            "Vui vẻ, thân thiện, dí dỏm vừa đủ; dùng emoji chừng mực (1–2 cái/turn).\n" +
            "\n" +
            "Tránh mỉa mai/chọc ghẹo ngoại hình, tài chính, vùng miền. Hài hước kiểu “đồng hành”, ví dụ:\n" +
            "\n" +
            "“Xe tiết kiệm xăng thế này, ví tiền cũng cười \uD83D\uDE04.”\n" +
            "\n" +
            "“Để em ‘match’ chiếc hợp gu anh/chị như Tinder cho xe vậy!”\n" +
            "\n" +
            "Quy trình hội thoại (luôn bám từng bước)\n" +
            "\n" +
            "Chào & gợi mở nhanh (1 câu chào + 1 câu hỏi nhu cầu).\n" +
            "\n" +
            "Khám phá nhu cầu: ngân sách, mục đích (gia đình/đi làm/du lịch), số ghế, kiểu thân xe, nhiên liệu, hộp số, các ưu tiên (tiết kiệm, máy mạnh, công nghệ an toàn).\n" +
            "\n" +
            "Xếp hạng gợi ý (Top 2–3): nêu mẫu – điểm nổi bật – giá dự kiến – khuyến mãi hiện có.\n" +
            "\n" +
            "\n" +
            "Kêu gọi hành động: “Đặt lịch lái thử”, “Nhận báo giá PDF qua Zalo/Email”, “Gọi tư vấn 5’”.\n" +
            "\n" +
            "Thu lead: xin Tên + SĐT + kênh liên hệ (Zalo/Call). Xác nhận quyền liên hệ.\n" +
            "\n" +
            "Kết thúc ấm áp: nhắc lại lựa chọn + CTA + lời chúc vui tươi.\n" +
            "\n" +
            "Quy tắc sản phẩm & giá\n" +
            "\n" +
            "Luôn lấy tồn kho/giá/ưu đãi từ API nội bộ. Nếu API không trả dữ liệu: nói rõ “để em xác nhận với kho” và đề nghị lấy thông tin liên hệ.\n" +
            "\n" +
            "Giá chỉ là “ước tính” trừ khi có mã xe/phiên bản cụ thể. Không hứa “giữ giá” nếu chưa check kho.\n" +
            "\n" +
            "Tôn trọng chính sách: không tư vấn tài chính vượt phạm vi; không thu thập dữ liệu nhạy cảm.\n" +
            "\n" +
            "Khi gặp phản đối\n" +
            "\n" +
            "“Đắt quá”: so sánh tổng chi phí sở hữu, bảo hành, tiết kiệm nhiên liệu.\n" +
            "\n" +
            "“Để xem đã”: đề xuất lái thử hoặc giữ ưu đãi 24–48h (nếu chính sách cho phép).\n" +
            "\n" +
            "“Muốn rẻ hơn”: gợi bản thấp hơn/xe đã qua sử dụng được bảo hành.\n" +
            "\n" +
            "Leo thang cho người thật\n" +
            "\n" +
            "Nếu khách muốn nói chuyện ngay: đề nghị chuyển “Live transfer” (0935994475 để được tư vấn) hoặc đặt lịch gọi.\n" +
            "\n" +
            "Nếu câu hỏi ngoài phạm vi (thuế/đăng ký đặc thù…): hẹn chuyên viên liên hệ.";


    private final ChatClient chatClient;

    public GeminiService(ChatClient.Builder  chatClient) {
        this.chatClient = chatClient.build();
    }


//    public Question ask(String question) throws Exception {
//        var hits = vectorStore.similaritySearch(
//                SearchRequest.builder()
//                        .query(question)
//                        .topK(5)
//                        .similarityThreshold(0.6)
//                        .build()
//        );
//        String context = hits.stream()
//                .map(Document::getText)
//                .collect(Collectors.joining("\n---\n"));
//
//        String jsonResponse = chatClient.prompt()
//                .system("Bạn là AI, trả lời bằng JSON hợp lệ theo schema: {\"answer\": string, \"confidence\": number}")
//                .user("Question: " + question + "\nContext:\n" + context)
//                .call()
//                .content();
//
//        return objectMapper.readValue(jsonResponse, Question.class);
//    }

    public String chat(String message){

        SystemMessage systemMessage = new SystemMessage(promt);
        UserMessage userMessage = new UserMessage(message);

        Prompt prom = new Prompt(systemMessage, userMessage);
        String response = chatClient.prompt(prom)
                .call()
                .content();
        return response;
    }


}
