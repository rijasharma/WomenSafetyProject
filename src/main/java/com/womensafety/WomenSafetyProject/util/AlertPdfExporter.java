package com.womensafety.WomenSafetyProject.util;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import com.womensafety.WomenSafetyProject.entity.Alert;

import jakarta.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class AlertPdfExporter {

    private final List<Alert> alertList;

    public AlertPdfExporter(List<Alert> alertList) {
        this.alertList = alertList;
    }

    public void export(HttpServletResponse response) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);
        fontTitle.setColor(Color.BLUE);

        Paragraph title = new Paragraph("User Alerts Report", fontTitle);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(15f);
        table.setWidths(new float[]{1.2f, 2.5f, 2.0f, 2.0f, 1.5f});

        writeTableHeader(table);
        writeTableData(table);

        document.add(table);
        document.close();
    }

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.BLACK);

        cell.setPhrase(new Phrase("ID", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("User", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Phone", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Timestamp", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Status", font));
        table.addCell(cell);
    }

    private void writeTableData(PdfPTable table) {
        for (Alert alert : alertList) {
            table.addCell(String.valueOf(alert.getId()));
            table.addCell(alert.getUser().getName());
            table.addCell(alert.getUser().getPhone());
            table.addCell(alert.getTimestamp().toString());
            table.addCell(alert.getStatus());
        }
    }
}
