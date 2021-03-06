package model.kfurl;

public class Kfurl {

    public static Double[][] getMap() {
        Double[][] map = new Double[][]{
                {0.109230, 0.104860, 0.105950, 0.105950, 0.107045, 0.104860, 0.103770, 0.105950, 0.110320, 0.105950},
                {0.133260, 0.111415, 0.110320, 0.114690, 0.113600, 0.107045, 0.107045, 0.103770, 0.103770, 0.103770},
                {0.133260, 0.111415, 0.110320, 0.114690, 0.113600, 0.107045, 0.107045, 0.103770, 0.103770, 0.103770},
                {0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000},
                {0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000},
                {0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000}
        };

        return map;
    }

    public static Double[] getXAxis() {
        return new Double[]{680d, 1000d, 1280d, 1520d, 2000d, 2520d, 3000d, 3800d, 5000d, 6000d};
    }

    public static Double[] getYAxis() {
        return new Double[]{2d, 17.99d, 30.498d, 40.997d, 41.997d, 42.997d};
    }


}
