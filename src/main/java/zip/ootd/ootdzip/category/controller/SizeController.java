package zip.ootd.ootdzip.category.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.category.data.SizeReq;
import zip.ootd.ootdzip.category.data.SizeRes;
import zip.ootd.ootdzip.category.service.SizeService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Size 컨트롤러", description = "Size 관련 API입니다.")
@RequestMapping("/api/v1/size")
public class SizeController {

    private final SizeService sizeService;

    @GetMapping("")
    public List<SizeRes> findByCategory(@Valid @RequestParam SizeReq request) {
        return sizeService.findByCategory(request);
    }
}
