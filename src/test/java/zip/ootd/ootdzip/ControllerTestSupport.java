package zip.ootd.ootdzip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import zip.ootd.ootdzip.clothes.controller.ClothesController;
import zip.ootd.ootdzip.clothes.service.ClothesService;
import zip.ootd.ootdzip.user.service.UserService;

@WebMvcTest(controllers = {
        ClothesController.class
}
)
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

}
