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

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.YearMonth;
import java.util.Map;

import static com.nineleaps.leaps.config.MessageStrings.TOTAL_INCOME;
import static com.nineleaps.leaps.config.MessageStrings.TOTAL_NUMBER;

@Service
@AllArgsConstructor
@Transactional
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

        addHeader(document);
        addSubheading(document, user);
        addEmptyLine(document);
        addDashboardData(document,user);
        addBarChart(document,user);

    }

    protected void addHeader(Document document) throws DocumentException {
        Font headingFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 30, BaseColor.BLACK);
        Chunk chunkHeading = new Chunk("Leaps", headingFont);
        Paragraph headingParagraph = new Paragraph(chunkHeading);
        headingParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(headingParagraph);
    }

    protected void addSubheading(Document document, User user) throws DocumentException {
        Font subheadingFont = FontFactory.getFont(FontFactory.COURIER_OBLIQUE, 18, BaseColor.BLACK);
        Chunk chunkSubheading = new Chunk("Report for " + user.getFirstName() + " " + user.getLastName(), subheadingFont);
        Paragraph subheadingParagraph = new Paragraph(chunkSubheading);
        subheadingParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(subheadingParagraph);
    }

    protected void addEmptyLine(Document document) throws DocumentException {
        document.add(new Paragraph(" "));
    }

//
protected void addDashboardData(Document document,User user) throws DocumentException {
    Map<YearMonth, Map<String, Object>> dashboardData = dashboardService.analytics(user);
    int numColumns = dashboardData.isEmpty() ? 0 : dashboardData.values().iterator().next().size();
    PdfPTable table = new PdfPTable(numColumns + 1);
    table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

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

    for (Map.Entry<YearMonth, Map<String, Object>> entry : dashboardData.entrySet()) {
        YearMonth month = entry.getKey();
        Map<String, Object> monthData = entry.getValue();
        String monthString = month.toString();
        String earnings = monthData.get(TOTAL_INCOME).toString() !=null ? monthData.get(TOTAL_INCOME).toString() : "";
        String numberOfItems = monthData.get(TOTAL_NUMBER).toString() != null ? monthData.get(TOTAL_NUMBER).toString():"";
        table.addCell(monthString);
        table.addCell(earnings);
        table.addCell(numberOfItems);
    }
    document.add(table);
}

    protected void addBarChart(Document document, User user) throws DocumentException, IOException {
        Map<YearMonth, Map<String, Object>> dashboardData = dashboardService.analytics(user);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<YearMonth, Map<String, Object>> entry : dashboardData.entrySet()) {
            YearMonth month = entry.getKey();
            Map<String, Object> monthData = entry.getValue();
            String monthString = month.getMonth().toString().substring(0, 3);

            // Check for null values and handle accordingly
            String earnings = monthData.get(TOTAL_INCOME) != null ? monthData.get(TOTAL_INCOME).toString() : "0";
            String numberOfItems = monthData.get(TOTAL_NUMBER) != null ? monthData.get(TOTAL_NUMBER).toString() : "0";

            dataset.addValue(Double.parseDouble(earnings), "Total Earnings", monthString);
            dataset.addValue(Integer.parseInt(numberOfItems), "Number of Items Sold", monthString);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Monthly Performance",
                "Month",
                "Value",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        int chartWidth = 500;
        int chartHeight = 300;
        ByteArrayOutputStream chartImageStream = new ByteArrayOutputStream();
        ChartUtilities.writeChartAsPNG(chartImageStream, chart, chartWidth, chartHeight);
        Image chartImage = Image.getInstance(chartImageStream.toByteArray());
        document.add(chartImage);
    }


}
