package cz.cuni.mff.vojtusr.plugins.mavenplugin;

import cz.cuni.mff.vojtusr.emf.ModelToCodeGenerator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.emf.common.util.Diagnostic;

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

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(property = "outputDir", defaultValue = "src-gen/")
    private String outputDir;

    @Parameter(property = "ecoreFile", required = true, readonly = true)
    private String inputEcoreFile;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Generating code from " + inputEcoreFile);
        generateCode();
    }

    private void generateCode() throws MojoExecutionException, MojoFailureException {
        ModelToCodeGenerator generator = new ModelToCodeGenerator();

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
