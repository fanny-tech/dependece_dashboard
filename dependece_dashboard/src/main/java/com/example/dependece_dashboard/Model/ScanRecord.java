package com.example.dependece_dashboard.Model;

public class ScanRecord {
    private Long id;
    private Long repositoryId;
    private String status;

    public ScanRecord() {
    }

    public ScanRecord(Long id, Long repositoryId, String status) {
        this.id = id;
        this.repositoryId = repositoryId;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}