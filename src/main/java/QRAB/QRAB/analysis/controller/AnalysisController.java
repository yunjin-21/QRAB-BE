package QRAB.QRAB.analysis.controller;

import QRAB.QRAB.analysis.domain.DetailedAnalysis;
import QRAB.QRAB.analysis.dto.*;
import QRAB.QRAB.analysis.service.AnalysisService;
import QRAB.QRAB.analysis.service.CategoryAnalysisService;
import QRAB.QRAB.analysis.service.DailyAnalysisService;
import QRAB.QRAB.analysis.service.DetailedAnalysisService;
import QRAB.QRAB.chatgpt.service.ChatgptService;
import QRAB.QRAB.user.util.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;
    private final DailyAnalysisService dailyAnalysisService;
    private final CategoryAnalysisService categoryAnalysisService;
    private final DetailedAnalysisService detailedAnalysisService;
    private final ChatgptService chatgptService;

    public AnalysisController(AnalysisService analysisService, DailyAnalysisService dailyAnalysisService,
                              CategoryAnalysisService categoryAnalysisService, DetailedAnalysisService detailedAnalysisService,
                              ChatgptService chatgptService) {
        this.analysisService = analysisService;
        this.dailyAnalysisService = dailyAnalysisService;
        this.categoryAnalysisService = categoryAnalysisService;
        this.detailedAnalysisService = detailedAnalysisService;
        this.chatgptService = chatgptService;
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

    // 상세 분석 수동 생성
    @PostMapping("/detailed-analysis")
    public ResponseEntity<DetailedAnalysisResponseDTO> createDetailedAnalysis() {
        String username = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new RuntimeException("No authenticated user found"));

        DetailedAnalysisResponseDTO response = detailedAnalysisService.getDetailedAnalysis(username);
        return ResponseEntity.ok(response);
    }

    // 상세 분석 조회
    @GetMapping("/detailed-analysis/latest")
    public ResponseEntity<DetailedAnalysisResponseDTO> getLatestDetailedAnalysis() {
        String username = SecurityUtil.getCurrentUsername()
                .orElseThrow(() -> new RuntimeException("No authenticated user found"));

        DetailedAnalysisResponseDTO response = detailedAnalysisService.getLatestDetailedAnalysis(username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-rag")
    public List<DetailedAnalysisResponseDTO.ReferenceDTO> testRAG() {
        return chatgptService.generateReferences(
                "Data Structures",
                "How to implement a linked list in Java"
        );
    }
}

