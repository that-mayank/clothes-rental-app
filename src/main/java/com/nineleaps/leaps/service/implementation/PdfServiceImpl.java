package com.nineleaps.leaps.service.implementation;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.DashboardServiceInterface;
import com.nineleaps.leaps.service.PdfServiceInterface;
import lombok.AllArgsConstructor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.YearMonth;
import java.util.Map;

import static com.nineleaps.leaps.config.MessageStrings.TOTAL_INCOME;
import static com.nineleaps.leaps.config.MessageStrings.TOTAL_NUMBER;

@Service
@AllArgsConstructor
public class PdfServiceImpl implements PdfServiceInterface {

    private final DashboardServiceInterface dashboardService;

    private void setCellPadding(PdfPCell cell) {
        cell.setPadding(6);
    }

    @Override
    public Document getPdf(User user) {
        return new Document();
    }

    @Override
    public void addContent(Document document, User user) throws DocumentException, IOException {

        // Add header
        Font headingFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 30, BaseColor.BLACK);
        Chunk chunkHeading = new Chunk("Leaps", headingFont);
        Paragraph headingParagraph = new Paragraph(chunkHeading);
        headingParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(headingParagraph);
        // Add empty line
        document.add(new Paragraph(" "));

        // Add subheading
        Font subheadingFont = FontFactory.getFont(FontFactory.COURIER_OBLIQUE, 18, BaseColor.BLACK);
        Chunk chunkSubheading = new Chunk("Report for " + user.getFirstName() + " " + user.getLastName(), subheadingFont);
        Paragraph subheadingParagraph = new Paragraph(chunkSubheading);
        subheadingParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(subheadingParagraph);

        // Add empty line
        document.add(new Paragraph(" "));
        // Get the dashboard data
        Map<YearMonth, Map<String, Object>> dashboardData = dashboardService.analytics(user);
        // Determine the number of columns based on the data
        int numColumns = dashboardData.isEmpty() ? 0 : dashboardData.values().iterator().next().size();
        // Create table
        PdfPTable table = new PdfPTable(numColumns + 1); // setting columns
        // Set cell alignment
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        // Add table headers
        Font tableHeaderFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 12, BaseColor.BLACK);
        PdfPCell cell1 = new PdfPCell(new Phrase("Month", tableHeaderFont));
        PdfPCell cell2 = new PdfPCell(new Phrase("Total Earnings", tableHeaderFont));
        PdfPCell cell3 = new PdfPCell(new Phrase("Number of Items Sold", tableHeaderFont));
        setCellPadding(cell1);
        setCellPadding(cell2);
        setCellPadding(cell3);
        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);


        // Add the total earnings and total number of items sold per month to the document
        for (Map.Entry<YearMonth, Map<String, Object>> entry : dashboardData.entrySet()) {
            YearMonth month = entry.getKey();
            Map<String, Object> monthData = entry.getValue();
            String monthString = month.toString();
            String earnings = monthData.get(TOTAL_INCOME).toString();
            String numberOfItems = monthData.get(TOTAL_NUMBER).toString();
            table.addCell(monthString);
            table.addCell(earnings);
            table.addCell(numberOfItems);
        }
        document.add(table);
        //add bar chart to pdf for all months of the year

        // Add bar chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // Add data to the dataset
        for (Map.Entry<YearMonth, Map<String, Object>> entry : dashboardData.entrySet()) {
            YearMonth month = entry.getKey();
            Map<String, Object> monthData = entry.getValue();
            String monthString = month.getMonth().toString().substring(0, 3);
            double earnings = Double.parseDouble(monthData.get(TOTAL_INCOME).toString());
            int numberOfItems = Integer.parseInt(monthData.get(TOTAL_NUMBER).toString());

            dataset.addValue(earnings, "Total Earnings", monthString);
            dataset.addValue(numberOfItems, "Number of Items Sold", monthString);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Monthly Performance", // Chart title
                "Month", // X-axis label
                "Value", // Y-axis label
                dataset, // Dataset
                PlotOrientation.VERTICAL, // Plot orientation
                true, // Show legend
                true, // Show tooltips
                false // Show URLs
        );

        // Set the width and height of the chart
        int chartWidth = 500;
        int chartHeight = 300;
        // Convert the chart to an image and add it to the PDF
        ByteArrayOutputStream chartImageStream = new ByteArrayOutputStream();
        ChartUtilities.writeChartAsPNG(chartImageStream, chart, chartWidth, chartHeight);
        Image chartImage = Image.getInstance(chartImageStream.toByteArray());
        document.add(chartImage);

    }

}
