import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MlhfmParser {
    public Map<String, List<Double>> parse() {

        Map<String, List<Double>> map = new HashMap<>();
        map.put(MlhfmFileContract.MAF_VOLTAGE_HEADER, new ArrayList<>());
        map.put(MlhfmFileContract.KILOGRAM_PER_HOUR_HEADER, new ArrayList<>());

        try {
            Reader in = new FileReader("/Users/kaleb/Desktop/scaling/mlhfm.csv");
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader(MlhfmFileContract.MAF_VOLTAGE_HEADER, MlhfmFileContract.KILOGRAM_PER_HOUR_HEADER).parse(in);
            int index = 0;
            for (CSVRecord record : records) {
                if(index++ > 0) {
                    map.get(MlhfmFileContract.MAF_VOLTAGE_HEADER).add(Double.parseDouble(record.get(MlhfmFileContract.MAF_VOLTAGE_HEADER)));
                    map.get(MlhfmFileContract.KILOGRAM_PER_HOUR_HEADER).add(Double.parseDouble(record.get(MlhfmFileContract.KILOGRAM_PER_HOUR_HEADER)));
                }
            }

            return map;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}
