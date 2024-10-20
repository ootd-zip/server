package zip.ootd.ootdzip.weather.data;

import java.util.List;

import lombok.Getter;

@Getter
public class WeatherResponse {

    private Response response;

    public String getResultCode() {
        return response.header.resultCode;
    }

    public String getResultMessage() {
        return response.header.resultMsg;
    }

    public List<ForecastItem> getItems() {
        return response.body.items.item;
    }

    @Getter
    public static class Response {

        private Header header;

        private Body body;

        @Getter
        public static class Header {

            private String resultCode;
            private String resultMsg;
        }

        @Getter
        public static class Body {

            private Item items;

            @Getter
            public static class Item {

                private List<ForecastItem> item;
            }
        }
    }
}