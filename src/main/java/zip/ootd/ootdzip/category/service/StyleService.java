package zip.ootd.ootdzip.category.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.category.data.StyleRes;
import zip.ootd.ootdzip.category.domain.Style;
import zip.ootd.ootdzip.category.repository.StyleRepository;

@Service
@RequiredArgsConstructor
public class StyleService {

    private final StyleRepository styleRepository;

    public List<StyleRes> getAllStyles() {
        // TODO : 페이징 및 검색조건 추가 확인 필요
        List<Style> styles = styleRepository.findAll();

        return styles.stream()
                .map(StyleRes::new)
                .toList();
    }
}
