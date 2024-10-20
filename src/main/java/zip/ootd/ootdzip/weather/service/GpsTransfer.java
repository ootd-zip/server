package zip.ootd.ootdzip.weather.service;

import zip.ootd.ootdzip.weather.data.Grid;

public class GpsTransfer {

    static final double RE = 6371.00877;
    static final double GRID = 5.0;
    static final double SLAT1 = 30.0;
    static final double SLAT2 = 60.0;
    static final double OLNG = 126.0;
    static final double OLAT = 38.0;
    static final double XO = 43;
    static final double YO = 136;

    static final double DEGRAD = Math.PI / 180.0;

    public static Grid transfer(double lat, double lng) {
        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olng = OLNG * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);

        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;

        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        double ra = Math.tan(Math.PI * 0.25 + lat * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);

        double theta = lng * DEGRAD - olng;
        if (theta > Math.PI)
            theta -= 2.0 * Math.PI;
        if (theta < -Math.PI)
            theta += 2.0 * Math.PI;
        theta *= sn;

        int gridX = (int)Math.floor(ra * Math.sin(theta) + XO + 0.5);
        int gridY = (int)Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);

        return Grid.builder()
                .x(gridX)
                .y(gridY)
                .build();
    }
}
