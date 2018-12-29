package writer;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import contract.MlhfmFileContract;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class MlhfmWriter {
    public void write(File file, Map<String, List<Double>> mlhfmMap) {
        try {

            List<Double> voltage = mlhfmMap.get(MlhfmFileContract.MAF_VOLTAGE_HEADER);
            List<Double> kgPerHour = mlhfmMap.get(MlhfmFileContract.KILOGRAM_PER_HOUR_HEADER);

            BufferedWriter writer = Files.newBufferedWriter(Paths.get(file.getAbsolutePath()));

            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("voltage", "kg/hr"));

            for(int i = 0; i < voltage.size(); i++) {
                csvPrinter.printRecord(voltage.get(i), kgPerHour.get(i));
            }

            csvPrinter.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
