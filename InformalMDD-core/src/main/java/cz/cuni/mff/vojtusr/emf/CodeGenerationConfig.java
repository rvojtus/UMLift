package cz.cuni.mff.vojtusr.emf;

import java.nio.file.Path;

public class CodeGenerationConfig {
    private Path inputUMLetFile;
    private Path outputEcoreFile;
    private Path outputGenModelFile;
    private Path generatedFilesDir;

    private String projectName;
    private String projectNsPrefix;
    private String projectNsURI;

    private CodeGenerationConfig() {
    }

    public static CodeGenerationConfig getInstance() {
        return new CodeGenerationConfig();
    }

    public CodeGenerationConfig setInputUMLetFile(Path inputUMLetFile) {
        this.inputUMLetFile = inputUMLetFile;
        return this;
    }

    public Path getInputUMLetFile() {
        return inputUMLetFile;
    }

    public CodeGenerationConfig setOutputEcoreFile(Path outputEcoreFile) {
        this.outputEcoreFile = outputEcoreFile;
        return this;
    }

    public Path getOutputEcoreFile() {
        return outputEcoreFile;
    }

    public CodeGenerationConfig setOutputGenModelFile(Path outputGenModelFile) {
        this.outputGenModelFile = outputGenModelFile;
        return this;
    }

    public Path getOutputGenModelFile() {
        return outputGenModelFile;
    }

    public CodeGenerationConfig setGeneratedFilesDir(Path generatedFilesDir) {
        this.generatedFilesDir = generatedFilesDir;
        return this;
    }

    public Path getGeneratedFilesDir() {
        return generatedFilesDir;
    }

    public CodeGenerationConfig setProjectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    public String getProjectName() {
        return projectName;
    }

    public CodeGenerationConfig setProjectNsPrefix(String projectNsPrefix) {
        this.projectNsPrefix = projectNsPrefix;
        return this;
    }

    public String getProjectNsPrefix() {
        return projectNsPrefix;
    }

    public CodeGenerationConfig setProjectNsURI(String projectNsURI) {
        this.projectNsURI = projectNsURI;
        return this;
    }

    public String getProjectNsURI() {
        return projectNsURI;
    }

    public CodeGenerationConfig build() {
        if (inputUMLetFile == null || outputEcoreFile == null || outputGenModelFile == null) {
            throw new IllegalStateException("Code Generation Config incomplete.");
        }
        return this;
    }
}
