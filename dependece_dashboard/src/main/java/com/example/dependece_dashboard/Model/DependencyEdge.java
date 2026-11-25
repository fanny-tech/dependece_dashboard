package com.example.dependece_dashboard.Model;

public class DependencyEdge {
    private Long id;
    private Long repositoryId;
    private Long scanId;
    private String sourcePackage;
    private String targetPackage;

    public DependencyEdge() {
    }

    public DependencyEdge(Long id, Long repositoryId, Long scanId, String sourcePackage, String targetPackage) {
        this.id = id;
        this.repositoryId = repositoryId;
        this.scanId = scanId;
        this.sourcePackage = sourcePackage;
        this.targetPackage = targetPackage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
    }

    public Long getScanId() {
        return scanId;
    }

    public void setScanId(Long scanId) {
        this.scanId = scanId;
    }

    public String getSourcePackage() {
        return sourcePackage;
    }

    public void setSourcePackage(String sourcePackage) {
        this.sourcePackage = sourcePackage;
    }

    public String getTargetPackage() {
        return targetPackage;
    }

    public void setTargetPackage(String targetPackage) {
        this.targetPackage = targetPackage;
    }
}
