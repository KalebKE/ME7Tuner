import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogParser {
    private int timeColumnIndex = -1;
    private int rpmColumnIndex = -1;
    private int stftColumnIndex = -1;
    private int ltftColumnIndex = -1;
    private int mafVoltageIndex = -1;
    private int throttlePlateAngleIndex = -1;
    private int lambdaControlActiveIndex = -1;

    public Map<String, List<Double>> parseLogFile() {
        Map<String, List<Double>> map = generateMap();

        File directory = new File("/Users/kaleb/Desktop/scaling");

        for(File file: directory.listFiles()) {
            resetIndices();
            System.out.println(file.getAbsolutePath());
            try {
                int skipRows = 0;
                boolean headersFound = false;
                Reader in = new FileReader(file);
                Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);
                for (CSVRecord record : records) {
                    for (int i = 0; i < record.size(); i++) {
                        if (!headersFound) {
                            switch (record.get(i).trim()) {
                                case LogFileContract.TIME_COLUMN_HEADER:
                                    timeColumnIndex = i;
                                    break;
                                case LogFileContract.RPM_COLUMN_HEADER:
                                    rpmColumnIndex = i;
                                    break;
                                case LogFileContract.STFT_COLUMN_HEADER:
                                    stftColumnIndex = i;
                                    break;
                                case LogFileContract.LTFT_COLUMN_HEADER:
                                    ltftColumnIndex = i;
                                    break;
                                case LogFileContract.MAF_VOLTAGE_HEADER:
                                    mafVoltageIndex = i;
                                    break;
                                case LogFileContract.THROTTLE_PLATE_ANGLE_HEADER:
                                    throttlePlateAngleIndex = i;
                                    break;
                                case LogFileContract.LAMBDA_CONTROL_ACTIVE_HEADER:
                                    lambdaControlActiveIndex = i;
                                    break;
                            }

                            headersFound = headersFound();
                        } else {
                            if (skipRows > 0) {
                                map.get(LogFileContract.TIME_COLUMN_HEADER).add(Double.parseDouble(record.get(timeColumnIndex)));
                                map.get(LogFileContract.RPM_COLUMN_HEADER).add(Double.parseDouble(record.get(rpmColumnIndex)));
                                map.get(LogFileContract.STFT_COLUMN_HEADER).add(Double.parseDouble(record.get(stftColumnIndex)));
                                map.get(LogFileContract.LTFT_COLUMN_HEADER).add(Double.parseDouble(record.get(ltftColumnIndex)));
                                map.get(LogFileContract.MAF_VOLTAGE_HEADER).add(Double.parseDouble(record.get(mafVoltageIndex)));
                                map.get(LogFileContract.THROTTLE_PLATE_ANGLE_HEADER).add(Double.parseDouble(record.get(throttlePlateAngleIndex)));
                                map.get(LogFileContract.LAMBDA_CONTROL_ACTIVE_HEADER).add(Double.parseDouble(record.get(lambdaControlActiveIndex)));
                            }
                        }
                    }

                    if (headersFound) {
                        skipRows++;
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        return map;
    }

    private void resetIndices() {
        timeColumnIndex = -1;
        rpmColumnIndex = -1;
        stftColumnIndex = -1;
        ltftColumnIndex = -1;
        mafVoltageIndex = -1;
        throttlePlateAngleIndex = -1;
        lambdaControlActiveIndex = -1;
    }

    private boolean headersFound() {
        return timeColumnIndex != -1 && rpmColumnIndex != -1 && stftColumnIndex != -1 && ltftColumnIndex != -1 && mafVoltageIndex != -1 && throttlePlateAngleIndex != -1 && lambdaControlActiveIndex != -1;
    }

    private Map<String, List<Double>> generateMap() {
        Map<String, List<Double>> map = new HashMap<>();
        map.put(LogFileContract.TIME_COLUMN_HEADER, new ArrayList<>());
        map.put(LogFileContract.RPM_COLUMN_HEADER, new ArrayList<>());
        map.put(LogFileContract.STFT_COLUMN_HEADER, new ArrayList<>());
        map.put(LogFileContract.LTFT_COLUMN_HEADER, new ArrayList<>());
        map.put(LogFileContract.MAF_VOLTAGE_HEADER, new ArrayList<>());
        map.put(LogFileContract.THROTTLE_PLATE_ANGLE_HEADER, new ArrayList<>());
        map.put(LogFileContract.LAMBDA_CONTROL_ACTIVE_HEADER, new ArrayList<>());

        return map;
    }
}
