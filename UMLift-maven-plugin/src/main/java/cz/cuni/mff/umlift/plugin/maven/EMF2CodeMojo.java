package cz.cuni.mff.umlift.plugin.maven;

import cz.cuni.mff.umlift.core.emf.CodeGenerationConfig;
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
 * Goal to generate code from a provided Ecore Model
 *
 * <p>
 * This plugin processes an Ecore Model file,
 * and generates Java code from it using Eclipse Modeling Framework libraries.
 * </p>
 *
 * @see ModelToCodeGenerator
 * @since 1.0
 */
@Mojo(name = "generate-code-from-ecore")
public class EMF2CodeMojo extends AbstractMojo {
    private static final Logger LOG = LoggerFactory.getLogger(EMF2CodeMojo.class);

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(property = "outputDir", defaultValue = "src/main/java")
    private String outputDir;

    @Parameter(property = "ecoreFile", required = true, readonly = true)
    private String inputEcoreFile;

    @Parameter(property = "package", defaultValue = "org.example")
    private String basePackage;

    @Parameter(property = "jdkLevel", defaultValue = "17") // GenJDKLevel.JDK210
    private String genJDKLevel;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        LOG.info("Generating code from Ecore {}", inputEcoreFile);
        generateCode();
        LOG.info("Generation complete.");
    }

    private void generateCode() throws MojoExecutionException, MojoFailureException {
        CodeGenerationConfig codeGenerationConfig =
                CodeGenerationConfig.getInstance().setGenJDKLevel(GenJDKLevel.valueOf(genJDKLevel)).setBasePackage(basePackage);
        ModelToCodeGenerator generator = new ModelToCodeGenerator(codeGenerationConfig);

        PrintStream originalStream = System.out;
        // Silence STDOUT, to not see output of Code Generation's EMF Generator
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));

        try {
            Diagnostic diagnostic = generator.generateCodeFromEcore(Path.of(inputEcoreFile), Path.of(outputDir));
            if (diagnostic.getSeverity() == Diagnostic.ERROR) {
                throw new MojoFailureException
                        ("Generation failed for Ecore file: " + inputEcoreFile + "\n" + diagnostic.getMessage());
            }
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } finally {
            // Restore default PrintStream
            System.setOut(originalStream);
        }
    }
}
