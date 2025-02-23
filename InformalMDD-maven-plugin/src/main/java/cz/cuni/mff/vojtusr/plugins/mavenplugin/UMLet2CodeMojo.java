package cz.cuni.mff.vojtusr.plugins.mavenplugin;

import cz.cuni.mff.vojtusr.emf.CodeGenerationConfig;
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
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(property = "outputDir", defaultValue = "src-gen/")
    private String outputDir;

    @Parameter(property = "modelDir", defaultValue = "resources/")
    private String modelDir;

    @Parameter(property = "umletFile", required = true, readonly = true)
    private String inputUMLetFile;

    @Parameter(property = "projectName", defaultValue = "exampleProjectName")
    private String projectName;

    @Parameter(property = "NsPrefix", defaultValue = "exampleProjectPrefix")
    private String NsPrefix;

    @Parameter(property = "NsURI", defaultValue = "exampleProjectURI")
    private String NsURI;

    @Parameter(property = "ecoreFileName", defaultValue = "ecore")
    private String ecoreFileName;

    @Parameter(property = "genmodelFileName", defaultValue = "genmodel")
    private String genmodelFileName;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Generating code from " + inputUMLetFile);
        generateCode();
    }

    private void generateCode() throws MojoExecutionException, MojoFailureException {
        ModelToCodeGenerator generator = new ModelToCodeGenerator();

        PrintStream originalStream = System.out;
        // Silence STDOUT, to not see output of Code Generation's EMF Generator
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));

        // Run code generation
        CodeGenerationConfig codeGenerationConfig = CodeGenerationConfig.getInstance()
                .setInputUMLetFile(Path.of(inputUMLetFile))
                .setGeneratedFilesDir(Path.of(outputDir))
                .setOutputEcoreFile(Path.of(modelDir + ecoreFileName))
                .setOutputGenModelFile(Path.of(modelDir + genmodelFileName))
                .setProjectName(projectName)
                .setProjectNsPrefix(NsPrefix)
                .setProjectNsURI(NsURI)
                .build();
        try {
            Diagnostic diagnostic = generator.generateCodeFromUMLetFile(codeGenerationConfig);
            if (diagnostic.getSeverity() == Diagnostic.ERROR) {
                throw new MojoFailureException
                        ("Generation failed for UMLet file: " + inputUMLetFile + "\n" + diagnostic.getMessage());
            }
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        } finally {
            // Restore default PrintStream
            System.setOut(originalStream);
        }
    }
}
