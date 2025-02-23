package cz.cuni.mff.vojtusr.plugins;

import cz.cuni.mff.vojtusr.emf.CodeGenerationConfig;
import cz.cuni.mff.vojtusr.emf.JavaGenerator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.emf.common.util.Diagnostic;

import java.io.IOException;
import java.nio.file.Path;

@Mojo(name = "UMLet2EMF")
public class UMLet2EMFMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(property = "projectDir")
    private String projectDir;

    @Parameter(property = "ecoreFile")
    private String ecoreFile;

    @Parameter(property = "umletFile")
    private String inputUMLetFile;

    @Parameter(property = "projectName", defaultValue = "projectTest")
    private String projectName;

    @Parameter(property = "NsPrefix", defaultValue = "projectTestPrefix")
    private String NsPrefix;

    @Parameter(property = "NsURI", defaultValue = "projectTestURI")
    private String NsURI;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Ecore: " + ecoreFile + " ProjectDir: " + projectDir +
                " ProjectName: " + projectName + " UMLetFile: " + inputUMLetFile);

        if (ecoreFile == null && inputUMLetFile == null) {
            throw new MojoExecutionException("Need to specify either Ecore File or UMLet File!");
        }
        if (projectDir == null) {
            projectDir = project.getBasedir().getAbsolutePath() + "/";
        }
        if (inputUMLetFile != null) {
            try {
                generateFromUMLet();
            } catch (IOException e) {
                throw new MojoExecutionException("Error while generating java code", e);
            }
            return;
        }
        try {
            generateFromEcore();
        } catch (IOException e) {
            throw new MojoExecutionException("Error while generating java code", e);
        }
    }

    private void generateFromEcore() throws IOException {
        JavaGenerator javaGenerator = new JavaGenerator();
        Diagnostic diagnostic = javaGenerator.generateCodeFromEcore(Path.of(ecoreFile), Path.of(projectDir));
    }

    private void generateFromUMLet() throws IOException {
        JavaGenerator javaGenerator = new JavaGenerator();
        CodeGenerationConfig config = CodeGenerationConfig.getInstance()
                .setInputUMLetFile(Path.of(inputUMLetFile))
                .setGeneratedFilesDir(Path.of(projectDir))
                .setOutputEcoreFile(Path.of(projectDir + "/ecore.ecore"))
                .setOutputGenModelFile(Path.of(projectDir + "/genmodel.genmodel"))
                .setProjectName(projectName)
                .setProjectNsPrefix(NsPrefix)
                .setProjectNsURI(NsURI)
                .build();
        Diagnostic diagnostic = javaGenerator.generateCodeFromUMLetFile(config);
    }
}
