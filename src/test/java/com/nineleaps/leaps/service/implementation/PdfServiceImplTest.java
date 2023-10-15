package com.nineleaps.leaps.service.implementation;

import com.itextpdf.text.DocumentException;
import com.nineleaps.leaps.dto.dashboard.DashboardAnalyticsDto;
import com.nineleaps.leaps.dto.dashboard.DashboardDto;
import com.nineleaps.leaps.model.User;
import com.nineleaps.leaps.service.DashboardServiceInterface;
import com.nineleaps.leaps.service.PdfServiceInterface;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfPTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PdfServiceImplTest {
    @Mock
    private DashboardServiceInterface dashboardService;

    @InjectMocks
    private PdfServiceImpl pdfService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addContent_WithValidData() throws Exception {
        // Arrange
        User user = new User();
        List<DashboardAnalyticsDto> dashboardData = new ArrayList<>();
        DashboardAnalyticsDto dashboardAnalyticsDto = new DashboardAnalyticsDto();
        dashboardAnalyticsDto.setMonth(YearMonth.now());
        dashboardAnalyticsDto.setTotalOrders(1);
        dashboardAnalyticsDto.setTotalEarnings(10000.00);
        dashboardData.add(dashboardAnalyticsDto);
        when(dashboardService.analytics(user)).thenReturn(dashboardData);
        Document document = new Document();
        document.open();

        // Act
        pdfService.addContent(document, user);

        // Assert
        // Verify that the document was modified as expected
        verify(dashboardService).analytics(user);
    }

    @Test
    void addContent_WithEmptyDashboardData() throws Exception {
        // Arrange
        User user = new User();
        when(dashboardService.analytics(user)).thenReturn(new ArrayList<>());
        Document document = new Document();
        document.open();

        // Act
        pdfService.addContent(document, user);

        // Assert
        // Verify that the document was modified as expected
        verify(dashboardService).analytics(user);
    }

    @Test
    void addContent_WithDocumentException() throws Exception {
        // Arrange
        User user = new User();
        when(dashboardService.analytics(user)).thenReturn(new ArrayList<>());
        Document document = new Document();
        document.open();
        // Simulate an IOException while adding content
        document.add(new PdfPTable(3));
        document.close();

        // Act and Assert
        assertThrows(DocumentException.class, () -> pdfService.addContent(document, user));
    }
}
