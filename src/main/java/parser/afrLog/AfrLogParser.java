package parser.afrLog;

import contract.AfrLogFileContract;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class AfrLogParser {

    private int rpmColumnIndex = -1;
    private int tpsColumnIndex = -1;
    private int afrColumnIndex = -1;

    public Map<String, List<Double>> parse() {

        Map<String, List<Double>> map = new HashMap<>();
        map.put(AfrLogFileContract.RPM_HEADER, new ArrayList<>());
        map.put(AfrLogFileContract.AFR_HEADER, new ArrayList<>());
        map.put(AfrLogFileContract.TPS_HEADER, new ArrayList<>());

        try {
            boolean headersFound = false;
            Reader in = new FileReader("/Users/kaleb/Desktop/open_loop_test/afr.csv");
            Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);

            for (CSVRecord record : records) {
                for (int i = 0; i < record.size(); i++) {
                    switch (record.get(i).trim()) {
                        case AfrLogFileContract.TPS_HEADER:
                            tpsColumnIndex = i;
                            break;
                        case AfrLogFileContract.RPM_HEADER:
                            rpmColumnIndex = i;
                            break;
                        case AfrLogFileContract.AFR_HEADER:
                            afrColumnIndex = i;
                            break;
                    }

                    if (headersFound = headersFound()) {
                        break;
                    }
                }

                if (headersFound) {
                    break;
                }
            }


            if (headersFound) {
                for (CSVRecord record : records) {
                    map.get(AfrLogFileContract.TPS_HEADER).add(Double.parseDouble(record.get(tpsColumnIndex)));
                    map.get(AfrLogFileContract.RPM_HEADER).add(Double.parseDouble(record.get(rpmColumnIndex)));
                    map.get(AfrLogFileContract.AFR_HEADER).add(Double.parseDouble(record.get(afrColumnIndex)));
                }
            }

            return map;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    private boolean headersFound() {
        return tpsColumnIndex != -1 && rpmColumnIndex != -1 && afrColumnIndex != -1;
    }
}
