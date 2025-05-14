package com.cg.futurefunds.utility;

import com.cg.futurefunds.dto.InvestmentResponseDTO;
import com.cg.futurefunds.exceptions.FutureFundsException;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class PdfGeneratorUtility {

    public void generateInvestmentPdf(List<InvestmentResponseDTO> investmentPlans, String outputFile) {
        Document document = new Document(PageSize.A4, 36, 36, 54, 36); // Page margins

        try {
            PdfWriter.getInstance(document, new FileOutputStream(outputFile));
            document.open();

            InvestmentResponseDTO userPlan = investmentPlans.get(0);
            String userName = userPlan.getUser().getName();
            String userEmail = userPlan.getUser().getEmail();

            // Create a paragraph for user info
            Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Paragraph userInfo = new Paragraph(userName + " | " + userEmail, infoFont);
            userInfo.setAlignment(Element.ALIGN_RIGHT);
            userInfo.setSpacingAfter(10f);
            document.add(userInfo);

            // Set title font
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph title = new Paragraph("Investment Plans Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f); // Space after title
            document.add(title);

            // Define a table with 6 columns
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100); // Full width
            table.setSpacingBefore(10f); // Space before table
            table.setSpacingAfter(10f);  // Space after table

            // Set column widths (percentage)
            float[] columnWidths = {2f, 1.5f, 2f, 2f, 2f, 2f};
            table.setWidths(columnWidths);

            // Header font
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);

            // Add headers
            String[] headers = {"Investment Name", "Type", "Monthly Amount", "Expected Return", "Target Amount", "Start Date"};
            for (String header : headers) {
                PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
                headerCell.setBackgroundColor(BaseColor.DARK_GRAY);
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPadding(10);
                table.addCell(headerCell);
            }

            // Body font
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);

            // Populate rows
            for (InvestmentResponseDTO plan : investmentPlans) {
                table.addCell(new PdfPCell(new Phrase(plan.getName(), bodyFont)));
                table.addCell(new PdfPCell(new Phrase(plan.getType().toString(), bodyFont)));
                table.addCell(new PdfPCell(new Phrase(String.format("%.2f", plan.getMonthlyAmount()), bodyFont)));
                table.addCell(new PdfPCell(new Phrase(String.format("%.2f%%", plan.getExpectedReturn()), bodyFont)));
                table.addCell(new PdfPCell(new Phrase(String.format("%.2f", plan.getTargetAmount()), bodyFont)));
                table.addCell(new PdfPCell(new Phrase(plan.getStartDate().toString(), bodyFont)));
            }

            // Add table to document
            document.add(table);

        } catch (DocumentException | IOException e) {
            throw new FutureFundsException("Error generating PDF: " + e.getMessage());
        } finally {
            document.close();
        }
    }
}
