package zip.ootd.ootdzip;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import zip.ootd.ootdzip.clothes.controller.ClothesController;
import zip.ootd.ootdzip.clothes.service.ClothesService;
import zip.ootd.ootdzip.ootd.controller.OotdController;
import zip.ootd.ootdzip.ootd.service.OotdService;
import zip.ootd.ootdzip.report.controller.ReportController;
import zip.ootd.ootdzip.report.service.ReportService;
import zip.ootd.ootdzip.user.service.UserService;

@WebMvcTest(controllers = {
        ClothesController.class,
        ReportController.class,
        OotdController.class
})
public abstract class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @MockBean
    protected ClothesService clothesService;

    @MockBean
    protected UserService userService;

    @MockBean
    protected ReportService reportService;

    @MockBean
    protected OotdService ootdService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
}
