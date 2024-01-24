package zip.ootd.ootdzip.utils;

import java.util.List;

import org.apache.commons.io.FilenameUtils;

public class ImageFileUtil {

    private static final List<String> imageExtensions = List.of("jpg", "jpeg", "png");

    private ImageFileUtil() {
        throw new IllegalArgumentException("Utility Class");
    }

    public static boolean isValidImageUrl(String imageUrl) {
        String extension = FilenameUtils.getExtension(imageUrl);
        return imageExtensions.contains(extension.toLowerCase());
    }
}
