package zip.ootd.ootdzip.images.scheduler;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import zip.ootd.ootdzip.images.service.ImagesService;

/**
 * 실패한 이미지를 재업로드하는 스케줄러입니다.
 * 다시 이미지를 올리는데 성공하면 파일을 지웁니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ImagesScheduler {

    private final ImagesService imagesService;

    private final String tempImageFolder = System.getProperty("user.dir") + "/" + "temp";

    /**
     * 30 분마다 주기적으로 실행
     */
    @Scheduled(cron = "0 0,30 * * * ?") // 30분마다 실행
    @Async // 비동기 실행
    public void uploadImageToS3() {
        List<File> imageFiles = getImageFiles();
        imageFiles.forEach(f -> imagesService.uploadToS3(f, f.getName()));
    }

    public List<File> getImageFiles() {
        File folder = new File(tempImageFolder);

        // 이미지 파일 필터
        FilenameFilter imageFilter = (dir, name) -> {
            String lowercaseName = name.toLowerCase();
            return lowercaseName.endsWith(".jpg") || lowercaseName.endsWith(".jpeg") || lowercaseName.endsWith(".png");
        };

        // 이미지 파일 리스트 가져오기
        File[] imageFiles = folder.listFiles(imageFilter);

        if (imageFiles == null) {
            return new ArrayList<>();
        }

        return List.of(imageFiles);
    }
}
