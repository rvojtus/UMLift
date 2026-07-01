package cz.cuni.mff.umlift.core.config;

import cz.cuni.mff.umlift.core.emf.ModelToCodeGenerator;
import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;

import java.nio.file.Path;

/**
 * Represents configuration for code generation. Is used in
 * {@link ModelToCodeGenerator#generateCodeFromUMLetFile(Path)}.<br>
 * Contains information about:
 * <ul>
 *     <li>UMLet file to be processed</li>
 *     <li>Directory to output generated files to</li>
 *     <li>Path for a generated Ecore file</li>
 *     <li>Path for a generated GenModel file</li>
 *     <li>Project Name</li>
 *     <li>Project Namespace prefix</li>
 *     <li>Project Namespace URI</li>
 * </ul>
 * <p>
 * The code below shows an example usage of this class:
 * <p>
 * {@snippet :
 * GenerationConfig config = GenerationConfig.getInstance()
 *     .setInputUMLetFile(Path.of("/path/to/UMLet/file/exampleUMLet.uxf"))
 *     .setGeneratedFilesDir(Path.of("/path/to/directory"))
 *     .setProjectName("exampleProject")
 *     .setProjectNsPrefix("exampleProject")
 *     .setProjectNsURI("www.exampleProject.org")
 *     .setBasePackage("com.example")
 *     .setGenJDKLevel(GenJDKLevel.JDK210_LITERAL)
 *     .build();
 * Diagnostic diagnostic = generator.generateCodeFromUMLetFile(config);
 *}
 *
 * @since 1.0
 */
public class GenerationConfig {
    private static final GenerationConfig INSTANCE = new GenerationConfig();

    private Path inputUMLetFile;
    private Path generatedFilesDir;
    private Path ecoreGenModelDir;
    private Path projectRootDir;

    private String projectName;
    private String projectNsPrefix;
    private String projectNsURI;
    private String basePackage;
    private GenJDKLevel genJDKLevel;

    private GenerationConfig() {
    }

    public static GenerationConfig getInstance() {
        return INSTANCE;
    }

    public GenerationConfig setInputUMLetFile(Path inputUMLetFile) {
        this.inputUMLetFile = inputUMLetFile;
        return this;
    }

    public Path getInputUMLetFile() {
        return inputUMLetFile;
    }

    public GenerationConfig setGeneratedFilesDir(Path generatedFilesDir) {
        this.generatedFilesDir = generatedFilesDir;
        return this;
    }

    public Path getGeneratedFilesDir() {
        return generatedFilesDir;
    }

    public GenerationConfig setProjectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    public String getProjectName() {
        return projectName;
    }

    public GenerationConfig setProjectNsPrefix(String projectNsPrefix) {
        this.projectNsPrefix = projectNsPrefix;
        return this;
    }

    public String getProjectNsPrefix() {
        return projectNsPrefix;
    }

    public GenerationConfig setProjectNsURI(String projectNsURI) {
        this.projectNsURI = projectNsURI;
        return this;
    }

    public String getProjectNsURI() {
        return projectNsURI;
    }

    public Path getEcoreGenModelDir() {
        return ecoreGenModelDir;
    }

    public GenerationConfig setEcoreGenModelDir(Path ecoreGenModelDir) {
        this.ecoreGenModelDir = ecoreGenModelDir;
        return this;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public GenerationConfig setBasePackage(String basePackage) {
        this.basePackage = basePackage;
        return this;
    }

    public GenJDKLevel getGenJDKLevel() {
        return genJDKLevel;
    }

    public GenerationConfig setGenJDKLevel(GenJDKLevel genJDKLevel) {
        this.genJDKLevel = genJDKLevel;
        return this;
    }

    public Path getProjectRootDir() {
        return projectRootDir;
    }

    public GenerationConfig setProjectRootDir(Path projectRootDir) {
        this.projectRootDir = projectRootDir;
        return this;
    }

    public String getBaseEPackageURI() {
        return projectNsURI + "/" + projectName;
    }
}
