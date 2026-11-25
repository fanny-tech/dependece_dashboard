package com.example.dependece_dashboard.Controller;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    private final JdbcTemplate jdbcTemplate;

    public TestController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/api/repositories")
    public List getRepositories() {
        return jdbcTemplate.queryForList("SELECT * FROM repositories");
    }

    @GetMapping("/api/scans-list")
    public List getScans() {
        return jdbcTemplate.queryForList("SELECT * FROM scans");
    }
}