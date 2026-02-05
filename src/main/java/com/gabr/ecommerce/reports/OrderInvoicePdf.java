package com.gabr.ecommerce.reports;

import com.gabr.ecommerce.entity.OrderItem;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderInvoicePdf {
    public void createOrderInvoicePdf(Long orderId, Double total, List<OrderItem> items, OutputStream outputStream) throws Exception {
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();

        // ===== Fonts =====
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
        Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.DARK_GRAY);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);

        // ===== Logo =====
        try {
            Image logo = Image.getInstance("src/main/resources/static/logo.png");
            logo.scaleAbsolute(90, 90);
            logo.setAlignment(Image.ALIGN_LEFT);
            document.add(logo);
        } catch (Exception e) {
            System.out.println("⚠️ Logo not found, skipping...");
        }

        // ===== QR Code =====
        String orderUrl = "http://localhost:8080/api/orders/" + orderId;
        BarcodeQRCode qrCode = new BarcodeQRCode(orderUrl, 150, 150, null);
        Image qrImage = qrCode.getImage();
        qrImage.setAbsolutePosition(440, 720);
        document.add(qrImage);

        // ===== Title =====
        Paragraph title = new Paragraph("E-Commerce App - Order Invoice", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);

        // ===== Order Info =====
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingBefore(10f);
        infoTable.setWidths(new float[]{1, 2});

        infoTable.addCell(makeInfoCell("Order ID:", boldFont));
        infoTable.addCell(makeInfoCell(String.valueOf(orderId), textFont));
        infoTable.addCell(makeInfoCell("Date:", boldFont));
        infoTable.addCell(makeInfoCell(LocalDate.now().toString(), textFont));
        infoTable.addCell(makeInfoCell("Order Link:", boldFont));
        infoTable.addCell(makeInfoCell(orderUrl, textFont));

        document.add(infoTable);
        document.add(Chunk.NEWLINE);

        // ===== Table Header =====
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4, 1, 2, 2});

        BaseColor headerColor = new BaseColor(60, 90, 150); // أزرق رمادي أنيق

        table.addCell(makeHeaderCell("Product", headerFont, headerColor));
        table.addCell(makeHeaderCell("Qty", headerFont, headerColor));
        table.addCell(makeHeaderCell("Price ($)", headerFont, headerColor));
        table.addCell(makeHeaderCell("Subtotal ($)", headerFont, headerColor));

        // ===== Table Body =====
        for (OrderItem item : items) {
            table.addCell(makeBodyCell(item.getProduct().getNameEn(), textFont));
            table.addCell(makeBodyCell(String.valueOf(item.getQuantity()), textFont));
            table.addCell(makeBodyCell(String.format("%.2f", item.getPrice()), textFont));
            double subtotal = item.getQuantity() * item.getPrice();
            table.addCell(makeBodyCell(String.format("%.2f", subtotal), textFont));
        }

        document.add(table);
        document.add(Chunk.NEWLINE);

        // ===== Total =====
        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(40);
        totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

        PdfPCell totalLabel = new PdfPCell(new Phrase("Total", boldFont));
        totalLabel.setBackgroundColor(new BaseColor(230, 230, 230));
        totalLabel.setBorderColor(BaseColor.LIGHT_GRAY);
        totalTable.addCell(totalLabel);

        PdfPCell totalValue = new PdfPCell(new Phrase("$" + String.format("%.2f", total), boldFont));
        totalValue.setBackgroundColor(BaseColor.WHITE);
        totalValue.setBorderColor(BaseColor.LIGHT_GRAY);
        totalTable.addCell(totalValue);

        document.add(totalTable);
        document.add(Chunk.NEWLINE);

        // ===== Footer =====
        LineSeparator separator = new LineSeparator();
        separator.setLineColor(BaseColor.LIGHT_GRAY);
        document.add(new Chunk(separator));

        Paragraph footer = new Paragraph("Thank you for shopping with us ❤️", textFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        Paragraph support = new Paragraph("Need help? Contact: support@ecommerce.com", textFont);
        support.setAlignment(Element.ALIGN_CENTER);
        document.add(support);

        document.close();
        writer.close();
    }

    // ===== Helper Methods =====
    private PdfPCell makeHeaderCell(String text, Font font, BaseColor bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(8f);
        cell.setBorderColor(BaseColor.WHITE);
        return cell;
    }

    private PdfPCell makeBodyCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6f);
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        return cell;
    }

    private PdfPCell makeInfoCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6f);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

}
