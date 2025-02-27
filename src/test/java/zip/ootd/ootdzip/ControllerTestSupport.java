package zip.ootd.ootdzip;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import zip.ootd.ootdzip.brandrequest.controller.BrandRequestController;
import zip.ootd.ootdzip.brandrequest.service.BrandRequestService;
import zip.ootd.ootdzip.clothes.controller.ClothesController;
import zip.ootd.ootdzip.clothes.service.ClothesService;
import zip.ootd.ootdzip.comment.controller.CommentController;
import zip.ootd.ootdzip.comment.service.CommentService;
import zip.ootd.ootdzip.notification.controller.NotificationController;
import zip.ootd.ootdzip.notification.service.NotificationService;
import zip.ootd.ootdzip.ootd.controller.OotdController;
import zip.ootd.ootdzip.ootd.service.OotdService;
import zip.ootd.ootdzip.ootdbookmark.controller.OotdBookmarkController;
import zip.ootd.ootdzip.ootdbookmark.service.OotdBookmarkService;
import zip.ootd.ootdzip.report.controller.ReportController;
import zip.ootd.ootdzip.report.service.ReportService;
import zip.ootd.ootdzip.user.controller.UserController;
import zip.ootd.ootdzip.user.service.UserService;
import zip.ootd.ootdzip.userblock.controller.UserBlockController;
import zip.ootd.ootdzip.userblock.service.UserBlockService;

@WebMvcTest(controllers = {
        ClothesController.class,
        ReportController.class,
        OotdController.class,
        CommentController.class,
        OotdBookmarkController.class,
        UserController.class,
        NotificationController.class,
        UserBlockController.class,
        BrandRequestController.class
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

    @MockBean
    protected CommentService commentService;

    @MockBean
    protected OotdBookmarkService ootdBookmarkService;

    @MockBean
    protected NotificationService notificationService;

    @MockBean
    protected UserBlockService userBlockService;

    @MockBean
    protected BrandRequestService brandRequestService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
}
