package com.swp391.edrive.service.serviceimpl;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.swp391.edrive.entity.*;
import com.swp391.edrive.repository.QuotationRepository;
import com.swp391.edrive.service.QuotationPdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

    // Đường dẫn logo
    private static final String LOGO_PATH = "uploads/logo/logo.jpg";

    // Bảng màu
    private static final DeviceRgb BRAND_COLOR = new DeviceRgb(23, 43, 77); // Xanh đậm
    private static final DeviceRgb ACCENT_COLOR = new DeviceRgb(0, 102, 204); // Xanh dương (Dùng cho thanh tiêu đề)
    private static final DeviceRgb HIGHLIGHT_COLOR = new DeviceRgb(255, 86, 48); // Cam đỏ (Dùng cho tổng tiền)
    private static final DeviceRgb LIGHT_GRAY_BG = new DeviceRgb(244, 245, 247);
    private static final DeviceRgb TEXT_GRAY = new DeviceRgb(100, 100, 100);

    private static final String FONT_PATH = "C:/Windows/Fonts/arial.ttf";

    @Override
    public ByteArrayOutputStream generateQuotationPdf(Long quotationId) {
        Quotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new RuntimeException("Not found quotation: " + quotationId));
        return generateQuotationPdf(quotation);
    }

    @Override
    public ByteArrayOutputStream generateQuotationPdf(Quotation quotation) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(PageSize.A4);

            Document document = new Document(pdfDoc);
            document.setMargins(20, 30, 30, 30);

            PdfFont font = PdfFontFactory.createFont(FONT_PATH, PdfEncodings.IDENTITY_H);
            document.setFont(font);

            // 1. HEADER PHONG CÁCH BANNER (Code mới update ở đây)
            addModernHeader(document, quotation);

            // 2. CÁC PHẦN CÒN LẠI
            addInfoSection(document, quotation);
            addVehicleSpecs(document, quotation);
            addPricingTable(document, quotation);
            document.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return baos;
    }

    // --- CÁC HÀM DỰNG GIAO DIỆN ---

    private void addModernHeader(Document document, Quotation quotation) {
        // PHẦN 1: LOGO VÀ THÔNG TIN CÔNG TY (Hàng trên cùng)
        Table topTable = new Table(UnitValue.createPercentArray(new float[]{1, 2})).useAllAvailableWidth();

        // Cột Trái: Logo
        Cell logoCell = new Cell().setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE);
        try {
            File logoFile = new File(LOGO_PATH);
            if (logoFile.exists()) {
                Image logo = new Image(ImageDataFactory.create(logoFile.getAbsolutePath()));
                // Ép cứng kích thước logo cho đẹp (khoảng 120pt)
                logo.setWidth(UnitValue.createPointValue(120));
                logoCell.add(logo);
            } else {
                logoCell.add(new Paragraph("E-DRIVE AUTO").setBold().setFontSize(20).setFontColor(BRAND_COLOR));
            }
        } catch (Exception e) {
            logoCell.add(new Paragraph("E-DRIVE AUTO").setBold().setFontSize(20).setFontColor(BRAND_COLOR));
        }

        // Cột Phải: Thông tin liên hệ nhỏ (Để cân đối với Logo)
        Cell infoCell = new Cell().setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        infoCell.add(new Paragraph("HỆ THỐNG PHÂN PHỐI XE ĐIỆN CHÍNH HÃNG")
                .setBold().setFontSize(10).setFontColor(BRAND_COLOR));
        infoCell.add(new Paragraph("Hotline: 1900 1234  |  Website: www.edrive.vn")
                .setFontSize(9).setFontColor(TEXT_GRAY));

        topTable.addCell(logoCell);
        topTable.addCell(infoCell);
        document.add(topTable);

        // PHẦN 2: THANH TIÊU ĐỀ MÀU XANH (Blue Banner) - ĐÂY LÀ PHẦN MỚI
        // Tạo khoảng cách một chút
        document.add(new Paragraph(" ").setFontSize(5));

        Table bannerTable = new Table(UnitValue.createPercentArray(new float[]{1})).useAllAvailableWidth();

        Cell bannerCell = new Cell()
                .setBackgroundColor(ACCENT_COLOR) // Nền xanh
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(8)
                .setBorder(Border.NO_BORDER);

        // Chữ trắng nổi bật trên nền xanh
        bannerCell.add(new Paragraph("BÁO GIÁ")
                .setBold().setFontSize(16).setFontColor(ColorConstants.WHITE));

        bannerTable.addCell(bannerCell);
        document.add(bannerTable);

        // PHẦN 3: SỐ BÁO GIÁ & NGÀY (Nằm ngay dưới thanh xanh, căn phải)
        Paragraph metaPara = new Paragraph()
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(10)
                .setFontColor(TEXT_GRAY)
                .setMarginTop(5)
                .setMarginBottom(15);

        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        metaPara.add("Số (No): " + String.format("QT-%06d", quotation.getQuotationId()) + "  |  ");
        metaPara.add("Ngày (Date): " + dateStr);

        document.add(metaPara);
    }

    private void addInfoSection(Document document, Quotation quotation) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1})).useAllAvailableWidth();
        table.setMarginBottom(20);

        Cell dealerCell = new Cell().setBorder(Border.NO_BORDER).setPaddingRight(15);
        dealerCell.add(new Paragraph("THÔNG TIN ĐẠI LÝ").setBold().setFontSize(10).setFontColor(ACCENT_COLOR));
        if (quotation.getDealer() != null) {
            dealerCell.add(new Paragraph(quotation.getDealer().getDealerName().toUpperCase()).setBold().setFontSize(12));
            dealerCell.add(createLightText("Địa chỉ: " + quotation.getDealer().getHouseNumberAndStreet()));
            dealerCell.add(createLightText("Hotline: " + quotation.getDealer().getPhone()));
            dealerCell.add(createLightText("Email: " + quotation.getDealer().getDealerEmail()));
        }

        Cell custCell = new Cell().setBorder(Border.NO_BORDER).setPaddingLeft(15);
        custCell.add(new Paragraph("KHÁCH HÀNG").setBold().setFontSize(10).setFontColor(ACCENT_COLOR));
        if (quotation.getCustomer() != null) {
            custCell.add(new Paragraph(quotation.getCustomer().getFullName().toUpperCase()).setBold().setFontSize(12));
            custCell.add(createLightText("SĐT: " + quotation.getCustomer().getPhone()));
            custCell.add(createLightText("Email: " + quotation.getCustomer().getEmail()));
            custCell.add(createLightText("Địa chỉ: " + quotation.getCustomer().getAddress()));
        }

        table.addCell(dealerCell);
        table.addCell(custCell);
        document.add(table);
    }

    private void addVehicleSpecs(Document document, Quotation quotation) {
        if (quotation.getVehicle() == null) return;
        Vehicle v = quotation.getVehicle();

        // Tiêu đề nhỏ cho phần xe
        document.add(new Paragraph("THÔNG TIN XE")
                .setBold().setFontSize(11)
                .setFontColor(BRAND_COLOR)
                .setBorderBottom(new SolidBorder(BRAND_COLOR, 1f)) // Kẻ chân thay vì nền màu
                .setMarginBottom(10));

        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1, 1})).useAllAvailableWidth();
        table.setMarginBottom(20);

        addSpecItem(table, "Model", v.getModelName());
        addSpecItem(table, "Phiên bản", v.getVersion());
        addSpecItem(table, "Màu xe", v.getColor() != null ? v.getColor().getColorName() : "Tiêu chuẩn");
        addSpecItem(table, "Năm SX", String.valueOf(v.getManufactureYear()));
        addSpecItem(table, "Pin (kWh)", v.getBatteryCapacityKwh() + " kWh");
        addSpecItem(table, "Quãng đường", v.getRangeKm() + " Km");
        addSpecItem(table, "Công suất", v.getMotorPowerKw() + " kW");

        document.add(table);
    }

    private void addPricingTable(Document document, Quotation quotation) {
        // Tiêu đề nhỏ cho phần giá
        document.add(new Paragraph("CHI TIẾT GIÁ")
                .setBold().setFontSize(11)
                .setFontColor(BRAND_COLOR)
                .setBorderBottom(new SolidBorder(BRAND_COLOR, 1f))
                .setMarginBottom(10));

        Table table = new Table(UnitValue.createPercentArray(new float[]{7, 3})).useAllAvailableWidth();

        addPriceHeader(table, "Hạng mục");
        addPriceHeader(table, "Thành tiền");

        int rowCount = 0;

        addPriceRow(table, "1. Giá xe niêm yết:", null, quotation.getUnitPrice(), rowCount++, false);

        BigDecimal discount = quotation.getPromotionDiscountAmount() != null ? quotation.getPromotionDiscountAmount() : BigDecimal.ZERO;
        if (discount.compareTo(BigDecimal.ZERO) > 0) {
            addPriceRow(table, "2. Ưu đãi:", null, discount.negate(), rowCount++, false);
        }

        BigDecimal serviceTotal = BigDecimal.ZERO;
        if (quotation.getQuotationServices() != null) {
            for (QuotationServices qs : quotation.getQuotationServices()) {
                BigDecimal itemTotal = qs.getPriceAtSelection().multiply(BigDecimal.valueOf(qs.getQuantity()));
                String desc = "Dịch vụ: " + qs.getService().getServiceName() + " (x" + qs.getQuantity() + ")";
                addPriceRow(table, desc, null, itemTotal, rowCount++, false);
                serviceTotal = serviceTotal.add(itemTotal);
            }
        }

        table.addCell(new Cell(1, 2).setBorder(Border.NO_BORDER).setHeight(10));

        BigDecimal finalPrice = quotation.getUnitPrice().subtract(discount).add(serviceTotal);

        Cell totalLabel = new Cell().add(new Paragraph("TỔNG CỘNG:").setBold().setFontSize(14))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        Cell totalValue = new Cell().add(new Paragraph(formatCurrency(finalPrice))
                        .setBold().setFontSize(18).setFontColor(HIGHLIGHT_COLOR))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);

        table.addCell(totalLabel);
        table.addCell(totalValue);

        document.add(table);

        document.add(new Paragraph("(Giá đã bao gồm VAT)")
                .setFontSize(9).setItalic().setFontColor(TEXT_GRAY).setTextAlignment(TextAlignment.RIGHT));
    }


    // --- HELPER METHODS ---

    private Paragraph createLightText(String text) {
        return new Paragraph(text).setFontSize(10).setFontColor(TEXT_GRAY).setMarginBottom(2);
    }

    private void addSpecItem(Table table, String label, String value) {
        table.addCell(new Cell().add(new Paragraph(label).setBold().setFontSize(9).setFontColor(TEXT_GRAY))
                .setBorder(Border.NO_BORDER).setPaddingBottom(5));
        table.addCell(new Cell().add(new Paragraph(value != null ? value : "-").setFontSize(10))
                .setBorder(Border.NO_BORDER).setPaddingBottom(5));
    }

    private void addPriceHeader(Table table, String text) {
        table.addHeaderCell(new Cell().add(new Paragraph(text).setBold().setFontSize(10))
                .setBackgroundColor(ColorConstants.WHITE)
                .setBorderTop(new SolidBorder(ColorConstants.GRAY, 1))
                .setBorderBottom(new SolidBorder(ColorConstants.GRAY, 1))
                .setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER)
                .setPadding(8));
    }

    private void addPriceRow(Table table, String text, String subText, BigDecimal amount, int index, boolean isTotal) {
        DeviceRgb bg = (index % 2 == 0) ? new DeviceRgb(255, 255, 255) : LIGHT_GRAY_BG;

        Cell descCell = new Cell().add(new Paragraph(text).setFontSize(10))
                .setBorder(Border.NO_BORDER).setBackgroundColor(bg).setPadding(8);

        Cell priceCell = new Cell().add(new Paragraph(formatCurrency(amount)).setFontSize(10))
                .setBorder(Border.NO_BORDER).setBackgroundColor(bg)
                .setTextAlignment(TextAlignment.RIGHT).setPadding(8);

        table.addCell(descCell);
        table.addCell(priceCell);
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0\u00A0đ";
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(amount) + "\u00A0đ";
    }
}

