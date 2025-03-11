package Duong.CV.CheckLogs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CheckLogs {
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    // Pattern để kiểm tra dòng có bắt đầu bằng timestamp không
    private static final Pattern TIMESTAMP_PATTERN =
            Pattern.compile("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}");

    public static void analyzeLogTimeDifferences(String filePath) {
        List<LocalDateTime> timestamps = new ArrayList<>();
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty() && line.length() >= 23 && TIMESTAMP_PATTERN.matcher(line).find()) {
                    try {
                        // Lấy phần thời gian (19 ký tự đầu + 4 ký tự millisecond)
                        String timeStr = line.substring(0, 23);
                        LocalDateTime timestamp = LocalDateTime.parse(timeStr, formatter);
                        timestamps.add(timestamp);
                        lines.add(line.trim());
                    } catch (Exception e) {
                        System.err.println("Không thể parse dòng: " + line);
                    }
                } else {
                    System.out.println("Bỏ qua dòng không có timestamp: " + line);
                }
            }

            int count=0;
            // Tính và kiểm tra khoảng cách thời gian
            for (int i = 1; i < timestamps.size(); i++) {
                long millisDiff = ChronoUnit.MILLIS.between(
                        timestamps.get(i - 1),
                        timestamps.get(i)
                );
                double secondsDiff = millisDiff / 1000.0;

                if (secondsDiff >10 && secondsDiff < 20) {  // Nếu lớn hơn 1 giây
                    System.out.printf("\nKhoảng cách: %.3f giây%n", secondsDiff);
                    System.out.println("Dòng trước: " + lines.get(i - 1));
                    System.out.println("Dòng hiện tại: " + lines.get(i));
                    count++;
                }
            }

            System.out.println("\nTổng số dòng log hợp lệ: " + count);

        } catch (Exception e) {
            System.err.println("Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String filePath = "C:\\Users\\Admin\\Downloads\\logs\\slow.5.log";
        analyzeLogTimeDifferences(filePath);
    }
}