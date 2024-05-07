package zip.ootd.ootdzip.userblock.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "User Block 컨트롤러", description = "사용자 차단 기능 관련 컨트롤러")
@RequestMapping("/api/v1/user-block")
public class UserBlockController {
}
