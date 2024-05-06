package javafxapplication1;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogFileReader {

    public List<LogRecord> readLogFile(String filePath) throws IOException {
        List<LogRecord> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("-");
                if (parts.length >= 7) {
                    String date = parts[0];
                    String time = parts[1];
                    long timestamp = Long.parseLong(parts[2]);
                    String ipAddress = parts[3];
                    String username = parts[4].equals("anonymous") ? null : parts[4];
                    String role = parts[5].equals("NA") ? null : parts[5];
                    String url = parts[6];
                    String description = parts.length == 8 ? parts[7] : null;

                    LogRecord record = new LogRecord(date, time, timestamp, ipAddress, username, role, url, description);
                    records.add(record);
                }
            }
        }

        Collections.sort(records);
        return records;
    }
}