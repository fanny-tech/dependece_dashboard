package com.example.dependece_dashboard.service;

import com.example.dependece_dashboard.Model.PackageNode;
import com.example.dependece_dashboard.Model.RepositoryRecord;
import com.example.dependece_dashboard.Repository.RepositoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class IntakeService {

    private final RepositoryRepository repositoryRepository;

    public IntakeService(RepositoryRepository repositoryRepository) {
        this.repositoryRepository = repositoryRepository;
    }

    public Optional<RepositoryRecord> findRepositoryById(Long id) {
        return repositoryRepository.findById(id);
    }

    public RepositoryRecord createRepository(String name, String repoUrl, String sourceType) {
        Long id = repositoryRepository.saveRepository(name, repoUrl, sourceType);
        return new RepositoryRecord(id, name, repoUrl, sourceType);
    }

    @Transactional
    public Map<String, Object> ingestLockfile(Long repositoryId, String lockfileName, String content) {
        String ecosystem = detectEcosystem(lockfileName);
        List<PackageNode> parsedPackages = parseLockfileContent(content, ecosystem);

        Long scanId = repositoryRepository.createScan(repositoryId, "COMPLETED");

        String rootName = "root:" + lockfileName;
        Long rootPackageId = repositoryRepository.savePackage(
                scanId, rootName, "1.0.0", true, "UNKNOWN"
        );

        int edgesCount = 0;
        for (PackageNode pkg : parsedPackages) {
            Long childPackageId = repositoryRepository.savePackage(
                    scanId, pkg.getName(), pkg.getVersion(), true, "UNKNOWN"
            );

            String depPath = rootName + " -> " + pkg.getName();
            repositoryRepository.saveEdge(scanId, rootPackageId, childPackageId, depPath);
            edgesCount++;
        }

        return Map.of(
                "scanId", scanId,
                "repositoryId", repositoryId,
                "lockfileName", lockfileName,
                "ecosystem", ecosystem,
                "packagesSaved", parsedPackages.size(),
                "edgesCreated", edgesCount
        );
    }

    public Map<String, Object> getDependencyGraph(Long repositoryId) {
        return Map.of(
                "repositoryId", repositoryId,
                "nodes", repositoryRepository.findPackagesByRepositoryId(repositoryId),
                "edges", repositoryRepository.findEdgesByRepositoryId(repositoryId)
        );
    }

    private String detectEcosystem(String filename) {
        if (filename == null) {
            return "generic";
        }
        String lower = filename.toLowerCase(Locale.ROOT);
        if (lower.contains("package-lock") || lower.endsWith(".json")) return "npm";
        if (lower.contains("requirements") || lower.endsWith(".txt")) return "pypi";
        if (lower.contains("pom.xml")) return "maven";
        if (lower.contains("go.mod")) return "go";
        return "generic";
    }

    private List<PackageNode> parseLockfileContent(String content, String ecosystem) {
        List<PackageNode> results = new ArrayList<>();
        if (content == null || content.isBlank()) {
            return results;
        }

        String[] lines = content.split("\\r?\\n");
        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                continue;
            }

            String name = line;
            String version = "latest";

            if (line.contains("==")) {
                String[] parts = line.split("==", 2);
                name = parts[0].trim();
                version = parts[1].trim();
            } else if (line.contains(":")) {
                String[] parts = line.split(":", 2);
                name = parts[0].trim().replaceAll("[\"',]", "");
                version = parts[1].trim().replaceAll("[\"',]", "");
            } else if (line.contains("@") && !line.startsWith("@")) {
                int idx = line.lastIndexOf('@');
                name = line.substring(0, idx).trim();
                version = line.substring(idx + 1).trim();
            }

            if (!name.isEmpty()) {
                PackageNode node = new PackageNode();
                node.setName(name);
                node.setVersion(version.isEmpty() ? "latest" : version);
                node.setEcosystem(ecosystem);
                results.add(node);
            }
        }
        return results;
    }
}