package com.swp391.edrive.service.serviceimpl;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
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
            addFooter(document);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo PDF: " + e.getMessage(), e);
        }

        return baos;
    }

    private void addHeader(Document document, Quotation quotation) {
        // Title
        Paragraph title = new Paragraph("BÁO GIÁ XE ĐIỆN")
                .setFontSize(24)
                .setBold()
                .setFontColor(PRIMARY_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
        document.add(title);

        // Quotation number
        String quotationNumber = String.format("Số: QT-%d-%04d",
                LocalDateTime.now().getYear(),
                quotation.getQuotationId());
        Paragraph number = new Paragraph(quotationNumber)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(number);

        // Date
        String dateStr = "Ngày tạo: " + (quotation.getCreatedAt() != null
                ? quotation.getCreatedAt().format(DATE_FORMAT)
                : LocalDateTime.now().format(DATE_FORMAT));
        Paragraph date = new Paragraph(dateStr)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(date);

        // Separator line
        document.add(new Paragraph().setBorder(new SolidBorder(ColorConstants.GRAY, 1)).setMarginBottom(15));
    }

    private void addDealerInfo(Document document, Quotation quotation) {
        Dealer dealer = quotation.getDealer();
        if (dealer == null) return;

        Paragraph sectionTitle = new Paragraph("THÔNG TIN ĐẠI LÝ")
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

        Paragraph sectionTitle = new Paragraph("THÔNG TIN KHÁCH HÀNG")
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
        Paragraph sectionTitle = new Paragraph("DỊCH VỤ BỔ SUNG")
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
            table.addCell(createCell(String.valueOf(index++)));
            table.addCell(createCell(qs.getService().getServiceName()));
            table.addCell(createCell(String.valueOf(qs.getQuantity())));
            table.addCell(createCell(formatCurrency(qs.getPriceAtSelection())));

            BigDecimal total = qs.getPriceAtSelection().multiply(BigDecimal.valueOf(qs.getQuantity()));
            table.addCell(createCell(formatCurrency(total)));
        }

        document.add(table);
    }

    private void addPricingSummary(Document document, Quotation quotation) {
        Paragraph sectionTitle = new Paragraph("TỔNG KẾT GIÁ")
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

        // Separator
        table.addCell(createCell("").setBorder(new SolidBorder(ColorConstants.GRAY, 1)));
        table.addCell(createCell("").setBorder(new SolidBorder(ColorConstants.GRAY, 1)));

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

    private void addFooter(Document document) {
        document.add(new Paragraph().setMarginTop(30));

        // Signatures table
        Table sigTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                .useAllAvailableWidth();

        Cell customerCell = new Cell().add(new Paragraph("KHÁCH HÀNG\n(Ký và ghi rõ họ tên)")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold())
                .setBorder(Border.NO_BORDER);

        Cell dealerCell = new Cell().add(new Paragraph("ĐẠI DIỆN ĐẠI LÝ\n(Ký và ghi rõ họ tên)")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold())
                .setBorder(Border.NO_BORDER);

        sigTable.addCell(customerCell);
        sigTable.addCell(dealerCell);

        document.add(sigTable);

        // Note
        Paragraph note = new Paragraph("\nLưu ý: Báo giá này có hiệu lực trong 30 ngày kể từ ngày phát hành.")
                .setFontSize(9)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(40);
        document.add(note);
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

