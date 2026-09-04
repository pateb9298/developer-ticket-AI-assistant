package com.example.intelligent_issue_router.model;

public class Issue {
    private Long id;
    private String title;
    private String description;
    private String status;
    private String resolution;
    private String category;
    private String priority;
    private String recommendedTeam;
    private String aiSummary;

    public Issue()
    {

    }

    public Issue (Long id, String title, String description, String status)
    {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getRecommendedTeam() {
        return recommendedTeam;
    }

    public void setRecommendedTeam(String recommendedTeam) {
        this.recommendedTeam = recommendedTeam;
    }

    public String getAiSummary() {
        return aiSummary;
    }

    public void setAiSummary(String aiSummary) {
        this.aiSummary = aiSummary;
    }

}
