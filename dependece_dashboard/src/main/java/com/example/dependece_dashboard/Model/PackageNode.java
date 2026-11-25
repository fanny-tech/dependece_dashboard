package com.example.dependece_dashboard.Model;

    public class PackageNode {
        private Long id;
        private Long repositoryId;
        private Long scanId;
        private String name;
        private String version;
        private String ecosystem;

        public PackageNode() {
        }

        public PackageNode(Long id, Long repositoryId, Long scanId, String name, String version, String ecosystem) {
            this.id = id;
            this.repositoryId = repositoryId;
            this.scanId = scanId;
            this.name = name;
            this.version = version;
            this.ecosystem = ecosystem;
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getEcosystem() {
            return ecosystem;
        }

        public void setEcosystem(String ecosystem) {
            this.ecosystem = ecosystem;
        }
    }

