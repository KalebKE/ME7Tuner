package math;


import math.map.Map;

public class Inverse {

    public static Map calculateInverse(Map input, Map output) {
        for(int i = 0; i < input.yAxis.length; i++) {
            for(int j = 0; j < output.xAxis.length; j++) {
                Double[] x = input.data[i];
                Double[] y = input.xAxis;
                Double[] xi = new Double[]{output.xAxis[j]};

                output.data[i][j] = LinearInterpolation.interpolate(x, y , xi)[0];
            }
        }

        return output;
    }
}
