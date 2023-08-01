package zip.ootd.ootdzip.clothes.service;

import org.springframework.web.multipart.MultipartFile;
import zip.ootd.ootdzip.clothes.data.ClothesResponseDto;
import zip.ootd.ootdzip.clothes.data.ClothesSaveDto;

import java.util.List;

public interface ClothesService {
    /**
     * 옷을 저장하고, 저장된 옷을 반환한다.
     * @param clothesSaveDto
     * @return
     */
    public ClothesResponseDto saveClothes(ClothesSaveDto clothesSaveDto, List<MultipartFile> clothesImageList);
}
