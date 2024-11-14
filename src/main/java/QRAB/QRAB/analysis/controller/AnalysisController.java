package QRAB.QRAB.analysis.controller;

import QRAB.QRAB.analysis.dto.CategoryAnalysisResponseDTO;
import QRAB.QRAB.analysis.dto.MonthlyAnalysisResponseDTO;
import QRAB.QRAB.analysis.dto.MonthlySummaryResponseDTO;
import QRAB.QRAB.analysis.dto.WeakCategoryResponseDTO;
import QRAB.QRAB.analysis.service.AnalysisService;
import QRAB.QRAB.analysis.service.CategoryAnalysisService;
import QRAB.QRAB.analysis.service.DailyAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;
    private final DailyAnalysisService dailyAnalysisService;
    private final CategoryAnalysisService categoryAnalysisService;

    public AnalysisController(AnalysisService analysisService, DailyAnalysisService dailyAnalysisService,
                              CategoryAnalysisService categoryAnalysisService) {
        this.analysisService = analysisService;
        this.dailyAnalysisService = dailyAnalysisService;
        this.categoryAnalysisService = categoryAnalysisService;
    }

    // 이번 달 통계 조회
    @GetMapping("/monthly")
    public ResponseEntity<MonthlyAnalysisResponseDTO> getMonthlyStats(
            @RequestParam("year") int year,
            @RequestParam("month") int month
    ) {
        MonthlyAnalysisResponseDTO response = analysisService.getMonthlyAnalysis(year, month);
        return ResponseEntity.ok(response);
    }

    // 월별 학습 기록 조회
    @GetMapping("/monthly-summary")
    public ResponseEntity<MonthlySummaryResponseDTO> getMonthlySummary(
            @RequestParam("year") int year,
            @RequestParam("month") int month) {
        MonthlySummaryResponseDTO response = dailyAnalysisService.getMonthlySummary(year, month);
        return ResponseEntity.ok(response);
    }

    // 카테고리별 학습 분석 조회
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryAnalysisResponseDTO>> getCategoryAnalysis(
            @RequestParam("period") String period) {
        List<CategoryAnalysisResponseDTO> categories = categoryAnalysisService.getCategoryAnalysis(period);
        return ResponseEntity.ok(categories);
    }

    // 취약 카테고리 조회
    @GetMapping(value = "/weak-categories", produces = "application/json")
    public ResponseEntity<WeakCategoryResponseDTO> getWeakCategoryAnalysis() {
        WeakCategoryResponseDTO response = categoryAnalysisService.getWeakCategoryAnalysis();
        System.out.println(response);
        return ResponseEntity.ok(response);
    }
}

