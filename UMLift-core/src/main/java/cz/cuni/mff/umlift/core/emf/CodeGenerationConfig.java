package cz.cuni.mff.umlift.core.emf;

import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;

import java.nio.file.Path;

/**
 * Represents configuration for code generation. Is used in
 * {@link ModelToCodeGenerator#generateCodeFromUMLetFile(Path)}.<br>
 * Contains information about:
 * <ul>
 *     <li>UMLet file to be processed</li>
 *     <li>Directory to output generated files to</li>
 *     <li>Path for generated Ecore file</li>
 *     <li>Path for generated GenModel file</li>
 *     <li>Project Name</li>
 *     <li>Project Namespace prefix</li>
 *     <li>Project Namespace URI</li>
 * </ul>
 * <p>
 * The code below shows example usage of this class:
 * <p>
 * {@snippet :
 * CodeGenerationConfig config = CodeGenerationConfig.getInstance()
 *     .setInputUMLetFile(Path.of("/path/to/UMLet/file/exampleUMLet.uxf"))
 *     .setGeneratedFilesDir(Path.of("/path/to/directory"))
 *     .setOutputEcoreFile(Path.of("ecore.ecore"))
 *     .setOutputGenModelFile(Path.of("genmodel.genmodel"))
 *     .setProjectName("exampleProjectName")
 *     .setProjectNsPrefix("exampleProjectNsPrefix")
 *     .setProjectNsURI("exampleProjectNsURI")
 *     .setBasePackage("com.example")
 *     .setGenJDKLevel(GenJDKLevel.JDK210_LITERAL)
 *     .build();
 * Diagnostic diagnostic = generator.generateCodeFromUMLetFile(config);
 *}
 *
 * @since 1.0
 */
public class CodeGenerationConfig {
    private Path inputUMLetFile;
    private Path outputEcoreFile;
    private Path outputGenModelFile;
    private Path generatedFilesDir;
    private Path ecoreGenModelDir;

    private String projectName;
    private String projectNsPrefix;
    private String projectNsURI;
    private String basePackage;
    private GenJDKLevel genJDKLevel;

    private String ecoreFileName;
    private String genModelFileName;


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

    public CodeGenerationConfig setEcoreFileName(String ecoreFileName) {
        this.ecoreFileName = ecoreFileName;
        return this;
    }

    public String getEcoreFileName() {
        return ecoreFileName;
    }

    public CodeGenerationConfig setGenModelFileName(String genModelFileName) {
        this.genModelFileName = genModelFileName;
        return this;
    }

    public String getGenModelFileName() {
        return genModelFileName;
    }

    /**
     * Validates setting of required fields and returns an instance of {@link CodeGenerationConfig}.
     *
     * @return An instance of {@link CodeGenerationConfig} if all required fields have been set
     * @throws IllegalStateException If not all required fields have been set
     */
    public CodeGenerationConfig build() throws IllegalStateException {
        /*
        if (inputUMLetFile == null || outputEcoreFile == null || outputGenModelFile == null) {
            throw new IllegalStateException("Code Generation Config incomplete.");
        }
         */
        return this;
    }

    public Path getEcoreGenModelDir() {
        return ecoreGenModelDir;
    }

    public CodeGenerationConfig setEcoreGenModelDir(Path ecoreGenModelDir) {
        this.ecoreGenModelDir = ecoreGenModelDir;
        return this;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public CodeGenerationConfig setBasePackage(String basePackage) {
        this.basePackage = basePackage;
        return this;
    }

    public GenJDKLevel getGenJDKLevel() {
        return genJDKLevel;
    }

    public CodeGenerationConfig setGenJDKLevel(GenJDKLevel genJDKLevel) {
        this.genJDKLevel = genJDKLevel;
        return this;
    }
}
