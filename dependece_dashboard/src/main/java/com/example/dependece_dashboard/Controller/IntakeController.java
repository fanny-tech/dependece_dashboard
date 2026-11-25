package com.example.dependece_dashboard.Controller;

import com.example.dependece_dashboard.Model.RepositoryRecord;
import com.example.dependece_dashboard.service.IntakeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class IntakeController {

    private final IntakeService intakeService;

    public IntakeController(IntakeService intakeService) {
        this.intakeService = intakeService;
    }

    @GetMapping("/repositories/{id}")
    public ResponseEntity<?> getRepositoryById(@PathVariable Long id) {
        Optional<RepositoryRecord> repo = intakeService.findRepositoryById(id);
        if (repo.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Repository not found with id: " + id));
        }
        return ResponseEntity.ok(repo.get());
    }

    @PostMapping("/repositories")
    public ResponseEntity<?> createRepository(@RequestBody Map<String, Object> body) {
        String name = textOrDefault(body.get("name"), "sample-service");
        Object rawUrl = body.get("repo_url") != null ? body.get("repo_url") : body.get("url");
        String repoUrl = textOrDefault(rawUrl, "https://github.com/example/sample-service");
        Object rawSource = body.get("source_type") != null ? body.get("source_type") : body.get("sourceType");
        String sourceType = textOrDefault(rawSource, "git");

        RepositoryRecord created = intakeService.createRepository(name, repoUrl, sourceType);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", created.getId(),
                "name", created.getName(),
                "repo_url", created.getRepoUrl(),
                "source_type", created.getSourceType(),
                "status", "CREATED"
        ));
    }

    @PostMapping("/intake/lockfile")
    public ResponseEntity<?> ingestLockfile(@RequestBody Map<String, Object> payload) {
        Object repoIdObj = payload.get("repositoryId") != null
                ? payload.get("repositoryId") : payload.get("repository_id");
        Object lockNameObj = payload.get("lockfileName") != null
                ? payload.get("lockfileName") : payload.get("lockfile_name");

        if (repoIdObj == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "'repositoryId' is required."));
        }

        Long repositoryId;
        try {
            repositoryId = Long.valueOf(repoIdObj.toString().trim());
        } catch (NumberFormatException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", "'repositoryId' must be a valid number."));
        }

        if (intakeService.findRepositoryById(repositoryId).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Repository not found: " + repositoryId));
        }

        String lockfileName = textOrDefault(lockNameObj, "requirements.txt");
        String content = textOrDefault(payload.get("content"), "flask==2.3.3\nrequests==2.32.0");

        Map<String, Object> result = intakeService.ingestLockfile(repositoryId, lockfileName, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/repositories/{id}/dependency-graph")
    public ResponseEntity<?> getDependencyGraph(@PathVariable Long id) {
        if (intakeService.findRepositoryById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Repository not found: " + id));
        }
        return ResponseEntity.ok(intakeService.getDependencyGraph(id));
    }

    private String textOrDefault(Object value, String defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        String trimmed = value.toString().trim();
        return trimmed.isEmpty() ? defaultValue : trimmed;
    }
}