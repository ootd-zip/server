package zip.ootd.ootdzip.category.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.category.data.ColorRes;
import zip.ootd.ootdzip.category.domain.Color;
import zip.ootd.ootdzip.category.repository.ColorRepository;

@Service
@RequiredArgsConstructor
public class ColorService {

    private final ColorRepository colorRepository;

    public List<ColorRes> getAllColors() {
        //TODO : 페이징 OR 검색조건 넣을 지 확인 필요
        List<Color> colors = colorRepository.findAll();

        return colors.stream()
                .map(ColorRes::new)
                .toList();
    }
}
