// Data Transfer Object
package com.example.intelligent_issue_router.dto;

public class AiAnalysis
{
    private String category;
    private String priority;
    private String recommendedTeam;
    private String summary;

    public String getCategory()
    {
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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
