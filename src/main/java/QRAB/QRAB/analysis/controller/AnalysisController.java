package QRAB.QRAB.analysis.controller;

import QRAB.QRAB.analysis.dto.MonthlyAnalysisResponseDTO;
import QRAB.QRAB.analysis.dto.MonthlySummaryResponseDTO;
import QRAB.QRAB.analysis.service.AnalysisService;
import QRAB.QRAB.analysis.service.DailyAnalysisService;
import QRAB.QRAB.user.domain.User;
import QRAB.QRAB.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;
    private final DailyAnalysisService dailyAnalysisService;

    public AnalysisController(AnalysisService analysisService, DailyAnalysisService dailyAnalysisService) {
        this.analysisService = analysisService;
        this.dailyAnalysisService = dailyAnalysisService;
    }

    @GetMapping("/monthly")
    public ResponseEntity<MonthlyAnalysisResponseDTO> getMonthlyStats(
            @RequestParam("year") int year,
            @RequestParam("month") int month
    ) {
        MonthlyAnalysisResponseDTO response = analysisService.getMonthlyAnalysis(year, month);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/monthly-summary")
    public ResponseEntity<MonthlySummaryResponseDTO> getMonthlySummary(
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        MonthlySummaryResponseDTO response = dailyAnalysisService.getMonthlySummary(year, month);
        return ResponseEntity.ok(response);
    }

}

