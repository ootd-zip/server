package zip.ootd.ootdzip.board.domain;

import jakarta.persistence.*;
import lombok.*;
import zip.ootd.ootdzip.common.entity.BaseEntity;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "board_images")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardImage extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(length = 2048)
    private String imageUrl;

    public static BoardImage createBoardImageBy(String imageUrl) {
        return BoardImage.builder()
                .imageUrl(imageUrl)
                .build();
    }

    public static List<BoardImage> createBoardImagesBy(List<String> imageUrls) {
        return imageUrls.stream()
                .map(BoardImage::createBoardImageBy)
                .collect(Collectors.toList());
    }
}
