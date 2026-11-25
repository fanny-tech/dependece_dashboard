package com.example.dependece_dashboard.Repository;

import com.example.dependece_dashboard.Model.RepositoryRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class RepositoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public RepositoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<RepositoryRecord> findById(Long id) {
        List<RepositoryRecord> list = jdbcTemplate.query(
                "SELECT id, name, repo_url, source_type FROM repositories WHERE id = ?",
                (rs, rowNum) -> new RepositoryRecord(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("repo_url"),
                        rs.getString("source_type")
                ),
                id
        );
        return list.stream().findFirst();
    }

    public Long saveRepository(String name, String repoUrl, String sourceType) {
        return insertAndGetId(
                "INSERT INTO repositories (name, repo_url, source_type) VALUES (?, ?, ?)",
                name, repoUrl, sourceType
        );
    }

    public Long createScan(Long repositoryId, String status) {
        return insertAndGetId(
                "INSERT INTO scans (repository_id, status) VALUES (?, ?)",
                repositoryId, status
        );
    }

    public Long savePackage(Long scanId, String name, String version, boolean isDirect, String licenseSpdx) {
        return insertAndGetId(
                "INSERT INTO packages (scan_id, name, version, is_direct, license_spdx) VALUES (?, ?, ?, ?, ?)",
                scanId, name, version, isDirect ? 1 : 0, licenseSpdx
        );
    }

    public void saveEdge(Long scanId, Long parentPackageId, Long childPackageId, String dependencyPath) {
        jdbcTemplate.update(
                "INSERT INTO dependency_edges (scan_id, parent_package_id, child_package_id, dependency_path) VALUES (?, ?, ?, ?)",
                scanId, parentPackageId, childPackageId, dependencyPath
        );
    }

    public List findPackagesByRepositoryId(Long repositoryId) {
        String sql = "SELECT p.id, p.scan_id, p.name, p.version, p.is_direct, p.license_spdx, p.created_at " +
                "FROM packages p " +
                "JOIN scans s ON p.scan_id = s.id " +
                "WHERE s.repository_id = ? " +
                "ORDER BY p.id ASC";
        return jdbcTemplate.queryForList(sql, repositoryId);
    }

    public List findEdgesByRepositoryId(Long repositoryId) {
        String sql = "SELECT e.id, e.scan_id, e.parent_package_id, e.child_package_id, e.dependency_path " +
                "FROM dependency_edges e " +
                "JOIN scans s ON e.scan_id = s.id " +
                "WHERE s.repository_id = ? " +
                "ORDER BY e.id ASC";
        return jdbcTemplate.queryForList(sql, repositoryId);
    }

    private Long insertAndGetId(String sql, Object... params) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to retrieve generated primary key after insert.");
        }
        return key.longValue();
    }
}