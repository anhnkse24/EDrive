package com.swp391.edrive.service.serviceimpl;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.swp391.edrive.entity.*;
import com.swp391.edrive.repository.QuotationRepository;
import com.swp391.edrive.service.QuotationPdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class QuotationPdfServiceImpl implements QuotationPdfService {

    private final QuotationRepository quotationRepository;

    private static final DecimalFormat CURRENCY_FORMAT = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Logo configuration
    private static final String LOGO_PATH = "uploads/logo/edrive-logo.png"; // Đường dẫn logo
    private static final String LOGO_URL = "https://vinfast-thaodien.com/wp-content/uploads/2025/02/vf8eco.webp"; // URL logo dự phòng

    // Colors
    private static final DeviceRgb PRIMARY_COLOR = new DeviceRgb(0, 102, 204); // Blue
    private static final DeviceRgb SECONDARY_COLOR = new DeviceRgb(240, 240, 240); // Light gray
    private static final DeviceRgb ACCENT_COLOR = new DeviceRgb(255, 102, 0); // Orange

    @Override
    public ByteArrayOutputStream generateQuotationPdf(Long quotationId) {
        Quotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy báo giá với ID: " + quotationId));
        return generateQuotationPdf(quotation);
    }

    @Override
    public ByteArrayOutputStream generateQuotationPdf(Quotation quotation) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);

            // Set Unicode encoding for the document
            pdfDoc.setTagged();

            Document document = new Document(pdfDoc);

            // Set margins
            document.setMargins(40, 40, 40, 40);

            // Set font that supports Vietnamese characters
            try {
                // Try to load Windows system font that supports Vietnamese
                // Arial Unicode MS or Times New Roman have good Vietnamese support
                PdfFont font = null;
                try {
                    // Try Arial Unicode MS first (best Vietnamese support)
                    font = PdfFontFactory.createFont("c:/windows/fonts/arialuni.ttf",
                        com.itextpdf.io.font.PdfEncodings.IDENTITY_H);
                } catch (Exception e) {
                    try {
                        // Fallback to Times New Roman (good Vietnamese support)
                        font = PdfFontFactory.createFont("c:/windows/fonts/times.ttf",
                            com.itextpdf.io.font.PdfEncodings.IDENTITY_H);
                    } catch (Exception e2) {
                        try {
                            // Fallback to Arial
                            font = PdfFontFactory.createFont("c:/windows/fonts/arial.ttf",
                                com.itextpdf.io.font.PdfEncodings.IDENTITY_H);
                        } catch (Exception e3) {
                            // Use Helvetica as last resort
                            font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
                        }
                    }
                }
                document.setFont(font);
            } catch (Exception e) {
                System.err.println("Warning: Could not load custom font: " + e.getMessage());
            }

            // Add content
            addHeader(document, quotation);
            addDealerInfo(document, quotation);
            addCustomerInfo(document, quotation);
            addVehicleInfo(document, quotation);

            // Add promotions if any
            if (quotation.getPromotions() != null && !quotation.getPromotions().isEmpty()) {
                addPromotionsSection(document, quotation);
            }

            // Add services if any
            if (quotation.getQuotationServices() != null && !quotation.getQuotationServices().isEmpty()) {
                addServicesSection(document, quotation);
            }

            addPricingSummary(document, quotation);
            addTermsAndConditions(document);
            addFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo PDF: " + e.getMessage(), e);
        }

        return baos;
    }

    private void addHeader(Document document, Quotation quotation) {
        // Create table for header layout: logo (left) + title (center-right)
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 3}))
                .useAllAvailableWidth()
                .setMarginBottom(10);

        // Left cell: Logo
        Cell logoCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        Image logo = loadLogo();

        if (logo != null) {
            // Resize logo to fit nicely (width: 100px, maintain aspect ratio)
            logo.setWidth(100);
            logo.setAutoScale(true);
            logoCell.add(logo);
        } else {
            // Nếu không load được logo, hiển thị text placeholder
            logoCell.add(new Paragraph("E-DRIVE")
                    .setFontSize(16)
                    .setBold()
                    .setFontColor(PRIMARY_COLOR));
        }

        // Right cell: Title and info
        Cell titleCell = new Cell()
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        // Title
        Paragraph title = new Paragraph("BÁO GIÁ XE ĐIỆN")
                .setFontSize(24)
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setTextAlignment(TextAlignment.CENTER);
        titleCell.add(title);

        // Quotation number
        String quotationNumber = String.format("Số: QT-%d-%04d",
                LocalDateTime.now().getYear(),
                quotation.getQuotationId());
        Paragraph number = new Paragraph(quotationNumber)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(5);
        titleCell.add(number);

        // Date
        String dateStr = "Ngày tạo: " + (quotation.getCreatedAt() != null
                ? quotation.getCreatedAt().format(DATE_FORMAT)
                : LocalDateTime.now().format(DATE_FORMAT));
        Paragraph date = new Paragraph(dateStr)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(3);
        titleCell.add(date);

        // Add cells to table
        headerTable.addCell(logoCell);
        headerTable.addCell(titleCell);

        document.add(headerTable);

        // Separator line
        document.add(new Paragraph().setBorder(new SolidBorder(ColorConstants.GRAY, 1)).setMarginBottom(15));
    }

    private void addDealerInfo(Document document, Quotation quotation) {
        Dealer dealer = quotation.getDealer();
        if (dealer == null) return;

        Paragraph sectionTitle = new Paragraph("THÔNG TIN ĐẠI LÝ:")
                .setFontSize(14)
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setMarginBottom(10);
        document.add(sectionTitle);

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                .useAllAvailableWidth()
                .setMarginBottom(15);

        addInfoRow(table, "Tên đại lý:", dealer.getDealerName());

        // Build full address from components
        String dealerAddress = buildAddress(
            dealer.getHouseNumberAndStreet(),
            dealer.getWardOrCommune(),
            dealer.getDistrict(),
            dealer.getProvinceOrCity()
        );
        addInfoRow(table, "Địa chỉ:", dealerAddress);
        addInfoRow(table, "Số điện thoại:", dealer.getPhone());
        addInfoRow(table, "Email:", dealer.getDealerEmail());

        document.add(table);
    }

    private void addCustomerInfo(Document document, Quotation quotation) {
        Customer customer = quotation.getCustomer();
        if (customer == null) return;

        Paragraph sectionTitle = new Paragraph("THÔNG TIN KHÁCH HÀNG:")
                .setFontSize(14)
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setMarginBottom(10);
        document.add(sectionTitle);

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                .useAllAvailableWidth()
                .setMarginBottom(15);

        addInfoRow(table, "Họ và tên:", customer.getFullName());
        addInfoRow(table, "Số điện thoại:", customer.getPhone());
        addInfoRow(table, "Email:", customer.getEmail());
        addInfoRow(table, "Địa chỉ:", customer.getAddress());
        if (customer.getIdCardNo() != null) {
            addInfoRow(table, "CMND/CCCD:", customer.getIdCardNo());
        }

        document.add(table);
    }

    private void addVehicleInfo(Document document, Quotation quotation) {
        Vehicle vehicle = quotation.getVehicle();
        if (vehicle == null) return;

        Paragraph sectionTitle = new Paragraph("THÔNG TIN XE")
                .setFontSize(14)
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setMarginBottom(10);
        document.add(sectionTitle);

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                .useAllAvailableWidth()
                .setMarginBottom(15);

        addInfoRow(table, "Model:", vehicle.getModelName() + " " + (vehicle.getVersion() != null ? vehicle.getVersion() : ""));
        addInfoRow(table, "Năm sản xuất:", String.valueOf(vehicle.getManufactureYear()));
        addInfoRow(table, "Dung lượng pin:", vehicle.getBatteryCapacityKwh() + " kWh");
        addInfoRow(table, "Quãng đường:", vehicle.getRangeKm() + " km");
        addInfoRow(table, "Công suất động cơ:", vehicle.getMotorPowerKw() + " kW");
        addInfoRow(table, "Số chỗ ngồi:", String.valueOf(vehicle.getSeatingCapacity()));

        document.add(table);
    }

    private void addPromotionsSection(Document document, Quotation quotation) {
        Paragraph sectionTitle = new Paragraph("KHUYẾN MÃI ÁP DỤNG")
                .setFontSize(14)
                .setBold()
                .setFontColor(ACCENT_COLOR)
                .setMarginBottom(10);
        document.add(sectionTitle);

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 3, 2, 2}))
                .useAllAvailableWidth()
                .setMarginBottom(15);

        // Header
        addTableHeader(table, "STT");
        addTableHeader(table, "Tên khuyến mãi");
        addTableHeader(table, "Loại");
        addTableHeader(table, "Giá trị");

        int index = 1;
        BigDecimal totalPromotionDiscount = BigDecimal.ZERO;

        for (Promotion promo : quotation.getPromotions()) {
            table.addCell(createCell(String.valueOf(index++)));
            table.addCell(createCell(promo.getTitle()));
            table.addCell(createCell(promo.getDiscountType().name()));

            String value = "";
            if (promo.getDiscountType().name().equals("PERCENTAGE")) {
                value = promo.getDiscountValue() + "%";
            } else {
                value = formatCurrency(BigDecimal.valueOf(promo.getDiscountValue()));
            }
            table.addCell(createCell(value));
        }

        document.add(table);
    }

    private void addServicesSection(Document document, Quotation quotation) {
        Paragraph sectionTitle = new Paragraph("DỊCH VỤ BỔ SUNG:")
                .setFontSize(14)
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setMarginBottom(10);
        document.add(sectionTitle);

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 4, 1, 2, 2}))
                .useAllAvailableWidth()
                .setMarginBottom(15);

        // Header
        addTableHeader(table, "STT");
        addTableHeader(table, "Tên dịch vụ");
        addTableHeader(table, "SL");
        addTableHeader(table, "Đơn giá");
        addTableHeader(table, "Thành tiền");

        int index = 1;
        for (QuotationServices qs : quotation.getQuotationServices()) {
            table.addCell(createCenterCell(String.valueOf(index++)));       // STT
            table.addCell(createCell(qs.getService().getServiceName()));    // Tên dịch vụ (căn trái)
            table.addCell(createCenterCell(String.valueOf(qs.getQuantity()))); // SL
            table.addCell(createCenterCell(formatCurrency(qs.getPriceAtSelection()))); // Đơn giá
            BigDecimal total = qs.getPriceAtSelection().multiply(BigDecimal.valueOf(qs.getQuantity()));
            table.addCell(createCenterCell(formatCurrency(total)));        // Thành tiền
        }

        document.add(table);
    }

    private void addPricingSummary(Document document, Quotation quotation) {
        Paragraph sectionTitle = new Paragraph("TỔNG KẾT GIÁ:")
                .setFontSize(14)
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setMarginBottom(10);
        document.add(sectionTitle);

        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        // Giá xe
        addPriceRow(table, "Giá xe:", quotation.getUnitPrice(), false);

        // Giảm giá khuyến mãi
        if (quotation.getPromotionDiscountAmount() != null && quotation.getPromotionDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            addPriceRow(table, "Giảm giá khuyến mãi:", quotation.getPromotionDiscountAmount().negate(), false);
        }

        // Dịch vụ bổ sung
        BigDecimal servicesTotal = BigDecimal.ZERO;
        if (quotation.getQuotationServices() != null && !quotation.getQuotationServices().isEmpty()) {
            servicesTotal = quotation.getQuotationServices().stream()
                    .map(qs -> qs.getPriceAtSelection().multiply(BigDecimal.valueOf(qs.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            addPriceRow(table, "Dịch vụ bổ sung:", servicesTotal, false);
        }

        // Tổng trước VAT
        BigDecimal subtotal = quotation.getUnitPrice()
                .subtract(quotation.getPromotionDiscountAmount() != null ? quotation.getPromotionDiscountAmount() : BigDecimal.ZERO)
                .add(servicesTotal);
        addPriceRow(table, "Tổng trước VAT:", subtotal, false);

        // VAT
        BigDecimal vat = subtotal.multiply(BigDecimal.valueOf(0.10));
        addPriceRow(table, "VAT (10%):", vat, false);

        Cell spacer = new Cell(1, 2)
                .setBorder(Border.NO_BORDER)
                .setHeight(10);
        table.addCell(spacer);

        // Grand total
        BigDecimal grandTotal = subtotal.add(vat);
        Cell labelCell = new Cell().add(new Paragraph("TỔNG CỘNG:")
                .setBold()
                .setFontSize(14)
                .setFontColor(ACCENT_COLOR))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPaddingRight(10);

        Cell valueCell = new Cell().add(new Paragraph(formatCurrency(grandTotal))
                .setBold()
                .setFontSize(14)
                .setFontColor(ACCENT_COLOR))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);

        table.addCell(labelCell);
        table.addCell(valueCell);

        document.add(table);
    }

    private void addTermsAndConditions(Document document) {

        Paragraph title = new Paragraph("ĐIỀU KHOẢN & ĐIỀU KIỆN:")
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setMarginTop(10)
                .setMarginBottom(5);
        document.add(title);

        String terms =
                "• Báo giá này có hiệu lực trong vòng 07 ngày kể từ ngày phát hành.\n" +
                        "• Giá trên đã bao gồm thuế VAT, nhưng chưa bao gồm lệ phí trước bạ, phí đăng ký, đăng kiểm và các chi phí lăn bánh khác.\n" +
                        "• Các chương trình khuyến mãi (nếu có) được áp dụng theo điều kiện và thời hạn của E-Drive tại thời điểm xuất hóa đơn.\n" +
                        "• Màu sắc xe và phụ kiện có thể có sự chênh lệch nhỏ so với hình ảnh minh họa do điều kiện ánh sáng.\n" +
                        "• Khoản tiền đặt cọc sẽ không được hoàn lại nếu khách hàng đơn phương hủy bỏ giao dịch.\n" +
                        "• Thời gian giao xe dự kiến có thể thay đổi tùy thuộc vào lịch sản xuất của nhà máy và tình hình vận chuyển.\n" +
                        "• Các gói dịch vụ cộng thêm có thể có điều khoản riêng. Vui lòng tham khảo hợp đồng chi tiết.\n" +
                        "• Đây là báo giá tham khảo và không có giá trị pháp lý như một hợp đồng mua bán chính thức.\n" +
                        "• Vui lòng đọc kỹ các điều khoản trước khi quyết định mua xe.";

        Paragraph content = new Paragraph(terms)
                .setFontSize(10)
                .setMarginBottom(15)
                .setTextAlignment(TextAlignment.LEFT)
                .setFixedLeading(14); // giãn dòng
        document.add(content);
    }

    private void addFooter(Document document) {
        document.add(new Paragraph().setMarginTop(30));

    }

    private void addInfoRow(Table table, String label, String value) {
        Cell labelCell = new Cell().add(new Paragraph(label).setBold())
                .setBorder(Border.NO_BORDER)
                .setPaddingBottom(5);

        Cell valueCell = new Cell().add(new Paragraph(value != null ? value : "-"))
                .setBorder(Border.NO_BORDER)
                .setPaddingBottom(5);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addTableHeader(Table table, String text) {
        Cell cell = new Cell().add(new Paragraph(text).setBold())
                .setBackgroundColor(SECONDARY_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        table.addHeaderCell(cell);
    }

    private Cell createCell(String text) {
        return new Cell().add(new Paragraph(text))
                .setPadding(5)
                .setTextAlignment(TextAlignment.LEFT);
    }

    private Cell createCenterCell(String text) {
        return new Cell()
                .add(new Paragraph(text))
                .setPadding(5)
                .setTextAlignment(TextAlignment.CENTER); // Căn giữa
    }

    private void addPriceRow(Table table, String label, BigDecimal amount, boolean isBold) {
        Paragraph labelPara = new Paragraph(label);
        if (isBold) labelPara.setBold();

        Cell labelCell = new Cell().add(labelPara)
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPaddingRight(10)
                .setPaddingTop(5)
                .setPaddingBottom(5);

        Paragraph valuePara = new Paragraph(formatCurrency(amount));
        if (isBold) valuePara.setBold();

        Cell valueCell = new Cell().add(valuePara)
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPaddingTop(5)
                .setPaddingBottom(5);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 ₫";
        return String.format("%,d ₫", amount.longValue());
    }

    /**
     * Load logo from various sources with fallback options
     */
    private Image loadLogo() {
        // Danh sách các file logo có thể có
        String[] possibleLogoPaths = {
            "uploads/logo/edrive-logo.png",
            "uploads/logo/logo.png",
            "uploads/logo/logo.jpg",
            "uploads/logo/logo.jpeg",
            "uploads/logo/edrive-logo.jpg",
            LOGO_PATH
        };

        // Thử load từ các file local
        for (String path : possibleLogoPaths) {
            try {
                java.io.File logoFile = new java.io.File(path);
                if (logoFile.exists()) {
                    System.out.println("Found logo at: " + logoFile.getAbsolutePath());
                    Image logo = new Image(ImageDataFactory.create(logoFile.getAbsolutePath()));
                    System.out.println("Successfully loaded logo from: " + path);
                    return logo;
                }
            } catch (Exception e) {
                System.err.println("Error loading logo from " + path + ": " + e.getMessage());
            }
        }

        // Nếu không có file local, thử load từ URL
        try {
            System.out.println("Trying to load logo from URL: " + LOGO_URL);
            Image logo = new Image(ImageDataFactory.create(LOGO_URL));
            System.out.println("Successfully loaded logo from URL");
            return logo;
        } catch (Exception e) {
            System.err.println("Error loading logo from URL: " + e.getMessage());
        }

        System.err.println("Could not load logo from any source. Using text placeholder.");
        return null;
    }

    /**
     * Build full address from components
     */
    private String buildAddress(String houseNumber, String ward, String district, String province) {
        StringBuilder address = new StringBuilder();

        if (houseNumber != null && !houseNumber.trim().isEmpty()) {
            address.append(houseNumber.trim());
        }

        if (ward != null && !ward.trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(ward.trim());
        }

        if (district != null && !district.trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(district.trim());
        }

        if (province != null && !province.trim().isEmpty()) {
            if (address.length() > 0) address.append(", ");
            address.append(province.trim());
        }

        return address.length() > 0 ? address.toString() : "-";
    }
}

