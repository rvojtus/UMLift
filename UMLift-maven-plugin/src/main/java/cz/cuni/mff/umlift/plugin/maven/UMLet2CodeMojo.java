package cz.cuni.mff.umlift.plugin.maven;

import cz.cuni.mff.umlift.core.config.GenerationConfig;
import cz.cuni.mff.umlift.core.emf.ModelToCodeGenerator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;
import org.eclipse.emf.common.util.Diagnostic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

/**
 * Goal to generate Java source code from a provided UMLet Diagram file
 *
 * <p>
 * This plugin processes UMLet Diagram File, transforms it to Eclipse Modeling Framework Models,
 * and generates Java code.
 * </p>
 *
 * @see ModelToCodeGenerator
 * @since 1.0
 */
@Mojo(name = "generate-code-from-umlet")
public class UMLet2CodeMojo extends AbstractMojo {
    private static final Logger LOG = LoggerFactory.getLogger(UMLet2CodeMojo.class);

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

    @Parameter(property = "outputDir", defaultValue = "src-gen/")
    private String outputDir;

    @Parameter(property = "modelDir", defaultValue = "resources/")
    private String modelDir;

    @Parameter(property = "umletFile", required = true)
    private String umletFile;

    @Parameter(property = "projectName", defaultValue = "exampleProjectName")
    private String projectName;

    @Parameter(property = "NsPrefix", defaultValue = "exampleProjectPrefix")
    private String NsPrefix;

    @Parameter(property = "NsURI", defaultValue = "exampleProjectURI")
    private String NsURI;

    @Parameter(property = "basePackageName", defaultValue = "org.example")
    private String basePackageName;

    @Parameter(property = "genJDKLevel", defaultValue = "JDK210") // GenJDKLevel.JDK210
    private String genJDKLevel;

    /**
     * Executes the Maven Mojo to generate code from an UMLet file.
     * <p>
     * This method initializes and invokes the code generation process, taking
     * the input UMLet file to generate corresponding output files. It logs
     * informational messages during the execution and ensures any issues,
     * such as errors in the input UMLet model or configuration, are reported
     * as exceptions to halt the process.
     *
     * @throws MojoExecutionException if an unexpected error occurs during
     *                                initialization or file processing.
     * @throws MojoFailureException   if the generation process fails due to
     *                                errors in the UMLet model or invalid
     *                                configuration.
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        LOG.info("Generating code from UMLet {}", umletFile);
        generateCode();
        LOG.info("Generation complete.");
    }

    private void generateCode() throws MojoExecutionException, MojoFailureException {
        GenerationConfig.getInstance()
                .setInputUMLetFile(Path.of(umletFile))
                .setGeneratedFilesDir(Path.of(outputDir))
                .setProjectName(projectName)
                .setProjectNsPrefix(NsPrefix)
                .setProjectNsURI(NsURI)
                .setBasePackage(basePackageName)
                .setGenJDKLevel(GenJDKLevel.valueOf(genJDKLevel + "_LITERAL"));


        PrintStream originalStream = System.out;
        // Silence STDOUT, to not see output of Code Generation's EMF Generator
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));

        // Run code generation
        ModelToCodeGenerator generator = new ModelToCodeGenerator();
        try {
            Diagnostic diagnostic = generator.generateCodeFromUMLetFile(Path.of(umletFile));
            if (diagnostic.getSeverity() == Diagnostic.ERROR) {
                throw new MojoFailureException
                        ("Generation failed for UMLet file: " + umletFile + "\n" + diagnostic.getMessage());
            }
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } finally {
            // Restore default PrintStream
            System.setOut(originalStream);
        }
    }
}
