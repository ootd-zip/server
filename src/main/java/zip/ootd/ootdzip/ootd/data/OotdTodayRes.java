package zip.ootd.ootdzip.ootd.data;

import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OotdTodayRes {

    @NotNull
    private Double highestTemp;

    @NotNull
    private Double lowestTemp;

    @NotNull
    private List<Ootd> ootdList;

    @Data
    @Builder
    public static class Ootd {

        @NotNull
        private Long id;

        @NotNull
        private String ootdImageUrl;

        @NotNull
        private Clothes taggedClothes;

        @Nullable
        private Clothes similarMyClothes;

        public static Ootd.OotdBuilder withId(Long id) {
            return Ootd.builder().id(id);
        }

        @Data
        @Builder
        public static class Clothes {

            @NotNull
            private Long id;

            @NotNull
            private String largeCategory;

            @NotNull
            private String detailCategory;

            @NotNull
            private String brandName;

            @NotNull
            private String size;

            public static Clothes from(zip.ootd.ootdzip.clothes.domain.Clothes clothes) {
                return Clothes.builder()
                        .id(clothes.getId())
                        .largeCategory(clothes.getCategory().getParentCategoryName())
                        .detailCategory(clothes.getCategory().getName())
                        .brandName(clothes.getBrand().getName())
                        .size(clothes.getSize().getName())
                        .build();
            }
        }
    }
}
