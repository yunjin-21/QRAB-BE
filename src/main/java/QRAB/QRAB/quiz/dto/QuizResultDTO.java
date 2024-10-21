package QRAB.QRAB.quiz.dto;

public class QuizResultDTO {
    private String noteTitle;
    private int totalQuestions;
    private String createdDate;
    private String solvedDate;
    private String answerSummary;
    private String categoryName;
    private String parentCategoryName;

    public QuizResultDTO(String noteTitle, int totalQuestions, String createdDate, String solvedDate, String answerSummary, String categoryName, String parentCategoryName) {
        this.noteTitle = noteTitle;
        this.totalQuestions = totalQuestions;
        this.createdDate = createdDate;
        this.solvedDate = solvedDate;
        this.answerSummary = answerSummary;
        this.categoryName = categoryName;
        this.parentCategoryName = parentCategoryName;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getSolvedDate() {
        return solvedDate;
    }

    public void setSolvedDate(String solvedDate) {
        this.solvedDate = solvedDate;
    }

    public String getAnswerSummary() {
        return answerSummary;
    }

    public void setAnswerSummary(String answerSummary) {
        this.answerSummary = answerSummary;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getParentCategoryName() {
        return parentCategoryName;
    }

    public void setParentCategoryName(String parentCategoryName) {
        this.parentCategoryName = parentCategoryName;
    }
}
