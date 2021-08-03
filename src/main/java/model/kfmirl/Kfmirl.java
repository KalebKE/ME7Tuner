package model.kfmirl;

public class Kfmirl {
    public static Double[][] getStockKfmirlMap() {
        /*Double[][] map = new Double[][]{
                {7.34, 21.66, 33.61, 47.56, 75.47, 111.96, 159.54, 187.00, 188.00, 189.00, 190.00, 191.00},
                {6.87, 21.28, 31.59, 41.95, 61.48, 91.08, 129.89, 175.32, 188.00, 189.00, 190.00, 191.00},
                {6.77, 20.70, 30.80, 40.55, 59.63, 83.37, 109.85, 148.53, 184.88, 189.00, 190.00, 191.00},
                {6.52, 19.73, 30.26, 40.24, 59.56, 80.39, 101.93, 132.50, 184.55, 189.00, 190.00, 191.00},
                {5.60, 19.06, 29.53, 39.56, 58.88, 78.19, 97.85, 119.93, 171.05, 189.00, 190.00, 191.00},
                {4.50, 18.21, 28.85, 38.74, 57.99, 76.88, 96.38, 116.89, 162.45, 189.00, 190.00, 191.00},
                {4.52, 17.65, 28.50, 38.37, 57.99, 76.97, 95.63, 114.94, 153.47, 186.40, 190.00, 191.00},
                {4.22, 17.18, 27.70, 37.27, 56.60, 76.24, 94.55, 112.64, 141.40, 176.96, 190.00, 191.00},
                {4.08, 17.02, 25.85, 36.00, 56.74, 76.20, 94.08, 111.87, 136.46, 172.48, 190.00, 191.00},
                {4.01, 17.30, 25.81, 35.79, 56.02, 75.89, 93.59, 111.24, 134.84, 167.00, 190.00, 191.00},
                {4.10, 17.30, 25.66, 35.84, 56.34, 75.31, 93.03, 110.46, 133.03, 164.68, 188.91, 191.00},
                {4.05, 17.60, 25.57, 35.65, 55.62, 74.04, 91.90, 109.69, 131.86, 162.17, 185.37, 191.00},
                {4.41, 17.79, 25.48, 35.72, 55.85, 73.31, 90.78, 109.01, 131.60, 162.59, 184.11, 191.00},
                {3.98, 18.00, 25.38, 35.09, 55.06, 72.49, 90.63, 109.08, 132.24, 163.69, 184.97, 191.00},
                {4.34, 18.00, 25.27, 35.06, 55.64, 72.49, 91.27, 109.81, 133.85, 165.71, 187.25, 191.00},
                {4.34, 17.95, 22.43, 30.38, 49.45, 67.90, 86.53, 105.28, 127.46, 156.05, 183.80, 191.00}
        };*/
        Double[][] map = new Double[][]{
                {0.00, 11.70, 23.37, 36.40, 48.12, 60.28, 72.33, 84.54, 97.29, 122.49, 144.85, 162.17, 170.23, 178.36, 184.22, 199.20},
                {0.00, 11.16, 22.34, 33.40, 43.99, 54.63, 65.44, 76.34, 87.70, 111.87, 133.41, 150.82, 159.85, 169.27, 176.49, 196.74},
                {0.00, 10.95, 21.94, 32.20, 42.00, 51.63, 61.67, 71.02, 79.99, 100.78, 121.29, 140.86, 151.20, 160.43, 169.29, 193.46},
                {0.00, 10.73, 21.28, 31.22, 40.83, 50.20, 59.72, 69.21, 78.61, 97.38, 117.49, 140.09, 149.75, 158.51, 168.00, 193.39},
                {0.00, 10.43, 20.63, 30.45, 40.01, 49.31, 58.71, 68.04, 77.18, 96.12, 116.44, 137.28, 147.99, 156.99, 166.71, 193.67},
                {0.00, 10.27, 20.60, 30.05, 39.61, 49.27, 58.48, 67.60, 76.67, 95.60, 115.83, 134.98, 144.45, 153.96, 162.96, 191.00},
                {0.00, 10.10, 20.06, 29.37, 38.86, 48.68, 57.80, 66.82, 75.96, 94.81, 114.89, 134.11, 143.16, 153.19, 162.92, 189.05},
                {0.00, 10.01, 20.37, 29.34, 38.39, 47.60, 57.05, 66.52, 75.35, 93.40, 112.92, 132.66, 141.07, 150.85, 159.80, 187.97},
                {0.00, 10.01, 20.04, 29.18, 38.30, 47.39, 56.70, 66.10, 75.14, 92.96, 112.08, 131.96, 140.44, 149.25, 158.82, 183.96},
                {0.00, 10.01, 20.02, 29.09, 38.16, 47.18, 56.34, 65.58, 74.56, 92.81, 112.06, 131.42, 140.37, 148.85, 158.35, 185.86},
                {0.00, 9.82, 19.27, 28.31, 37.48, 46.95, 56.27, 65.53, 74.46, 92.84, 111.87, 131.23, 140.75, 151.06, 159.68, 185.72},
                {0.00, 9.56, 18.89, 27.94, 37.15, 46.67, 55.90, 64.95, 73.83, 92.35, 111.42, 131.28, 141.80, 151.20, 161.25, 188.72},
                {0.00, 9.23, 18.19, 27.47, 37.08, 46.69, 55.83, 64.71, 73.36, 91.69, 111.05, 131.28, 142.41, 151.34, 162.31, 188.54},
                {0.00, 8.93, 17.91, 27.45, 37.27, 46.24, 55.06, 63.77, 72.56, 90.63, 110.44, 131.07, 141.89, 152.60, 164.32, 191.63},
                {0.00, 8.81, 17.58, 26.81, 36.42, 45.73, 54.75, 63.54, 72.12, 90.05, 110.09, 132.21, 142.41, 153.61, 163.74, 192.68},
                {0.00, 8.55, 16.97, 26.77, 36.73, 45.82, 54.61, 63.07, 71.93, 90.61, 111.35, 132.10, 142.13, 152.42, 163.78, 192.71}
        };

        return map;
    }

    public static Double[][] getStockKfmirlScalerMap() {
        Double[][] kfmirl = getStockKfmirlMap();
        Double[][] scaler = new Double[kfmirl.length][];

        for (int i = 0; i < kfmirl.length; i++) {
            scaler[i] = new Double[kfmirl[i].length];

            for (int j = 0; j < kfmirl[i].length; j++) {
                scaler[i][j] = kfmirl[i][j] / 191.0;
            }
        }

        return scaler;
    }

    public static Double[][] getScaledKfmirlMap(double maxSpecifiedLoad, int minimumLoadIndex) {
        Double[][] kfmirl = getStockKfmirlMap();
        Double[][] scaler = getStockKfmirlScalerMap();
        Double[][] scaledKfmirl = new Double[scaler.length][];

        for (int i = 0; i < scaledKfmirl.length; i++) {
            scaledKfmirl[i] = new Double[scaler[i].length];

            for (int j = 0; j < scaler[i].length; j++) {
                if (j < minimumLoadIndex) {
                    scaledKfmirl[i][j] = kfmirl[i][j];
                } else if (j == minimumLoadIndex) {
                    scaledKfmirl[i][j] = scaler[i][j] * (maxSpecifiedLoad/1.5);
                } else if (j == minimumLoadIndex + 1) {
                    scaledKfmirl[i][j] = scaler[i][j] * (maxSpecifiedLoad/1.25);
                } else
                    scaledKfmirl[i][j] = scaler[i][j] * maxSpecifiedLoad;
            }
        }


        return scaledKfmirl;
    }

    public static Double[] getStockKfmirlXAxis() {
        /*return new Double[]{0d, 10d, 15d, 20d, 30d, 40d, 50d, 60d, 70d, 80d, 90d, 99d};*/
        return new Double[]{0d, 5d, 10d, 15d, 20d, 25d, 30d, 35d, 40d, 50d, 60d, 70d, 75d, 80d, 85d, 99d};
    }

    public static Double[] getStockKfmirlYAxis() {
        return new Double[]{480d, 720d, 1000d, 1240d, 1520d, 1760d, 2000d, 2520d, 3000d, 3520d, 4000d, 4520d, 5000d, 5720d, 6000d, 6720d};
    }
}
