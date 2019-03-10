package model.kfzwop;

public class Kfzwop {

    public static Double[] getStockXAxis() {
        return new Double[]{9d, 18d, 27d, 50d, 62d, 75d, 97d, 120d, 144d, 168d, 191d};
    }

    public static Double[] getStockYAxis() {
        return new Double[] {440d, 720d, 1000d, 1240d, 1520d, 1760d, 2000d, 2520d, 3000d, 3520d, 4000d, 4520d, 5000d, 5520d, 6000d, 7000d };
    }


    public static Double[][] getStockMap() {
        return new Double[][]{
                {25.500, 25.500, 19.500, 15.750, 14.250, 12.750, 9.750, 6.750, 5.250, 3.000, 2.250},
                {28.500, 28.500, 23.250, 18.000, 16.500, 15.750, 12.750, 9.750, 9.000, 6.000, 5.250},
                {30.000, 29.250, 27.000, 21.000, 18.750, 17.250, 15.000, 12.750, 11.250, 8.250, 6.750},
                {30.750, 29.250, 28.500, 24.750, 22.500, 18.750, 17.250, 15.000, 14.250, 11.250, 9.000},
                {33.000, 30.000, 29.250, 27.000, 24.000, 21.750, 18.750, 18.000, 17.250, 15.000, 12.750},
                {36.000, 32.250, 30.000, 27.750, 24.750, 23.250, 21.000, 20.250, 18.750, 18.000, 16.500},
                {38.250, 33.750, 32.250, 29.250, 26.250, 24.000, 23.250, 21.750, 21.000, 19.500, 18.000},
                {39.750, 36.750, 35.250, 30.000, 27.750, 26.250, 24.000, 22.500, 21.750, 21.000, 19.500},
                {42.000, 39.000, 36.000, 30.750, 29.250, 27.000, 24.000, 23.250, 21.750, 21.000, 20.250},
                {44.250, 41.250, 36.750, 32.250, 30.000, 27.000, 25.500, 24.000, 23.250, 21.750, 21.000},
                {45.000, 42.000, 36.750, 32.250, 30.000, 27.750, 26.250, 24.750, 24.000, 23.250, 21.750},
                {45.000, 42.000, 36.750, 32.250, 30.000, 27.750, 26.250, 25.500, 24.750, 24.000, 22.500},
                {45.750, 42.000, 36.750, 32.250, 30.750, 28.500, 27.000, 26.250, 25.500, 24.000, 22.500},
                {45.750, 42.000, 36.750, 32.250, 30.750, 28.500, 27.000, 26.250, 25.500, 24.750, 23.250},
                {45.750, 42.000, 36.750, 33.000, 31.500, 29.250, 27.750, 27.000, 26.250, 24.750, 23.250},
                {46.500, 42.000, 36.750, 33.000, 31.500, 29.250, 27.750, 27.000, 26.250, 24.750, 23.250}
        };
    }
}
