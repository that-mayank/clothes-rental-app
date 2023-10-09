package com.nineleaps.leaps.service.implementation;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.nineleaps.leaps.model.User;

import com.nineleaps.leaps.service.DashboardServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.*;


import java.lang.reflect.Method;
import java.time.YearMonth;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.nineleaps.leaps.config.MessageStrings.TOTAL_INCOME;
import static com.nineleaps.leaps.config.MessageStrings.TOTAL_NUMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
@Tag("unit_tests")
@DisplayName("PDF Service Tests")
class PdfServiceImplTest {

    @Mock
    private DashboardServiceInterface dashboardService;

    @InjectMocks
    private PdfServiceImpl pdfService;
    @Captor
    private ArgumentCaptor<PdfPTable> captor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    @DisplayName("Get PDF should return Document")
    void getPdf_shouldReturnDocument() {
        User user = new User(); // Create a sample user
        when(dashboardService.analytics(user)).thenReturn(null); // Mock analytics service

        Document pdf = pdfService.getPdf(user);

        assertNotNull(pdf);
    }


    @Test
    @DisplayName("Add header should add heading paragraph to document")
    void addHeader_shouldAddHeadingParagraphToDocument() throws Exception {
        // Mock the Document
        Document document = mock(Document.class);

        // Create an instance of the class containing the private method
        PdfServiceImpl pdfService = new PdfServiceImpl(dashboardService);

        // Obtain the private method using reflection
        Method privateMethod = PdfServiceImpl.class.getDeclaredMethod("addHeader", Document.class);

        // Make the private method accessible for invocation
        privateMethod.setAccessible(true);

        // Call the private method
        privateMethod.invoke(pdfService, document);

        // Verify the behavior of the private method
        verify(document).add(any()); // Assuming paragraph is added
    }

    @Test
    @DisplayName("Add subheading should add subheading paragraph to document")
    void addSubheading_shouldAddSubheadingParagraphToDocument() throws Exception {
        // Mock the Document
        Document document = mock(Document.class);
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");

        // Create an instance of the class containing the private method
        PdfServiceImpl pdfService = new PdfServiceImpl(dashboardService);

        // Obtain the private method using reflection
        Method privateMethod = PdfServiceImpl.class.getDeclaredMethod("addSubheading", Document.class, User.class);

        // Make the private method accessible for invocation
        privateMethod.setAccessible(true);

        // Call the private method
        privateMethod.invoke(pdfService, document, user);

        // Verify the behavior of the private method
        verify(document).add(any()); // Assuming paragraph is added
    }




    @Test
    @DisplayName("Add dashboard data should add table to document")
    void addDashboardData_shouldAddTableToDocument() throws Exception {
        // Mock dashboard data for non-empty case
        Map<String, Object> monthData = new HashMap<>();
        monthData.put(TOTAL_INCOME, 1000.0);  // Use the constant TOTAL_INCOME
        monthData.put(TOTAL_NUMBER, 10);  // Use the constant TOTAL_NUMBER

        Map<YearMonth, Map<String, Object>> dashboardData = new HashMap<>();
        dashboardData.put(YearMonth.now(), monthData);

        // Mock the behavior of dashboardService.analytics()
        when(dashboardService.analytics(any())).thenReturn(dashboardData);

        // Mock the Document
        Document document = mock(Document.class);

        // Obtain the private method using reflection
        Method privateMethod = PdfServiceImpl.class.getDeclaredMethod("addDashboardData", Document.class, User.class);

        // Make the private method accessible for invocation
        privateMethod.setAccessible(true);

        // Call the private method
        privateMethod.invoke(pdfService, document, new User());

        // Capture the PdfPTable added to the document
        ArgumentCaptor<PdfPTable> captor = ArgumentCaptor.forClass(PdfPTable.class);
        verify(document).add(captor.capture());
        PdfPTable capturedTable = captor.getValue();

        // Verify the structure of the table (number of rows and columns)
        assertEquals(2, capturedTable.getRows().size()); // Header + data row
        assertEquals(3, capturedTable.getNumberOfColumns());

        // Verify the content of the table (header and sample data)
        assertEquals("Month", capturedTable.getRow(0).getCells()[0].getPhrase().getContent());
        assertEquals("Total Earnings", capturedTable.getRow(0).getCells()[1].getPhrase().getContent());
        assertEquals("Number of Items Sold", capturedTable.getRow(0).getCells()[2].getPhrase().getContent());

        assertEquals(YearMonth.now().toString(), capturedTable.getRow(1).getCells()[0].getPhrase().getContent());
        assertEquals("1000.0", capturedTable.getRow(1).getCells()[1].getPhrase().getContent());
        assertEquals("10", capturedTable.getRow(1).getCells()[2].getPhrase().getContent());
    }


    @Test
    @DisplayName("Add bar chart should add chart image to document")
    void addBarChart_shouldAddChartImageToDocument() throws Exception {
        // Mock dashboard data
        Map<YearMonth, Map<String, Object>> dashboardData = new HashMap<>();
        dashboardData.put(YearMonth.now(), Collections.singletonMap("Total Earnings", 1000.0));


        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");

        // Mock the behavior of dashboardService.analytics()
        when(dashboardService.analytics(any())).thenReturn(dashboardData);

        // Mock the Document
        Document document = mock(Document.class);

        // Obtain the private method using reflection
        Method privateMethod = PdfServiceImpl.class.getDeclaredMethod("addBarChart", Document.class, User.class);

        // Make the private method accessible for invocation
        privateMethod.setAccessible(true);

        // Call the private method
       privateMethod.invoke(pdfService,document,user);

        // Capture the Image added to the document
        ArgumentCaptor<Image> imageCaptor = ArgumentCaptor.forClass(Image.class);
        verify(document).add(imageCaptor.capture());
        Image capturedImage = imageCaptor.getValue();

        // Verify the Image dimensions
        assertEquals(500f, capturedImage.getWidth(), 0.01f);
        assertEquals(300f, capturedImage.getHeight(), 0.01f);
    }

    @Test
    @DisplayName("Add empty line should add empty line to document")
    void addEmptyLine_shouldAddEmptyLineToDocument() throws Exception {
        // Mock the Document
        Document document = mock(Document.class);

        // Obtain the private method using reflection
        Method privateMethod = PdfServiceImpl.class.getDeclaredMethod("addEmptyLine", Document.class);

        // Make the private method accessible for invocation
        privateMethod.setAccessible(true);

        // Call the private method
        privateMethod.invoke(pdfService,document);

        // Capture the Paragraph added to the document
        ArgumentCaptor<Paragraph> paragraphCaptor = ArgumentCaptor.forClass(Paragraph.class);
        verify(document).add(paragraphCaptor.capture());
        Paragraph capturedParagraph = paragraphCaptor.getValue();

        // Verify the content of the paragraph (should be a single space)
        assertEquals(" ", capturedParagraph.getContent());
    }

    @Test
    @DisplayName("Add content should call expected methods")
    void addContent_shouldCallExpectedMethods() throws Exception {
        // Mock dashboard data
        Map<String, Object> monthData = new HashMap<>();
        monthData.put(TOTAL_INCOME, 1000.0);
        monthData.put(TOTAL_NUMBER, 10);

        Map<YearMonth, Map<String, Object>> dashboardData = new HashMap<>();
        dashboardData.put(YearMonth.now(), monthData);
        User user = new User();
        // Mock the behavior of dashboardService.analytics()
        when(dashboardService.analytics(any())).thenReturn(dashboardData);
        Document document = mock(Document.class);
        // Create a spy for the pdfService
        PdfServiceImpl pdfServiceSpy = Mockito.spy(pdfService);

        // Obtain the private method using reflection
        Method privateMethod = PdfServiceImpl.class.getDeclaredMethod("addContent", Document.class, User.class);

        // Make the private method accessible for invocation
        privateMethod.setAccessible(true);

        // Call the private method on the spy
        privateMethod.invoke(pdfServiceSpy, document, user);

        // Verify that the expected methods are called with the correct parameters
        verify(pdfServiceSpy, times(1)).addHeader(document);
        verify(pdfServiceSpy, times(1)).addSubheading(document, user);
        verify(pdfServiceSpy, times(1)).addEmptyLine(document);
        verify(pdfServiceSpy, times(1)).addDashboardData(document, user);
        verify(pdfServiceSpy, times(1)).addBarChart(document,user);
    }


}
