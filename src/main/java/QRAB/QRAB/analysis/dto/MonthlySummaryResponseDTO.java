package QRAB.QRAB.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MonthlySummaryResponseDTO {
    private int year;
    private int month;
    private List<DailyRecord> days;

    @Data
    @AllArgsConstructor
    public static class DailyRecord {
        private String date; // yyyy-mm-dd
        private int solvedQuizCount;
        private float dailyAccuracy;
    }
}
