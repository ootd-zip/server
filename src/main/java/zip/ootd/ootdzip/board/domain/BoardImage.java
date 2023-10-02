package zip.ootd.ootdzip.board.domain;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zip.ootd.ootdzip.common.entity.BaseEntity;

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
