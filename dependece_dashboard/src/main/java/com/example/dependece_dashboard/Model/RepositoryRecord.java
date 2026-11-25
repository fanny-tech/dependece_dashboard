package com.example.dependece_dashboard.Model;

public class RepositoryRecord {
    private Long id;
    private String name;
    private String repoUrl;
    private String sourceType;

    public RepositoryRecord() {
    }

    public RepositoryRecord(Long id, String name, String repoUrl, String sourceType) {
        this.id = id;
        this.name = name;
        this.repoUrl = repoUrl;
        this.sourceType = sourceType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
}