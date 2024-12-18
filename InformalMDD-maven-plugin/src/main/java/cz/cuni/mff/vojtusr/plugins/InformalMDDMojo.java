package cz.cuni.mff.vojtusr.plugins;

import cz.cuni.mff.vojtusr.emf.JavaGenerator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;

@Mojo(name = "InformalMDD")
public class InformalMDDMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(property = "ecoreFile")
    private String ecoreFile;

    @Parameter(property = "projectDir")
    private String projectDir;

    @Parameter(property = "umletFile")
    private String UMLetFile;

    @Parameter(property = "projectName", defaultValue = "projectTest")
    private String projectName;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Ecore: " + ecoreFile + " ProjectDir: " + projectDir +
                " ProjectName: " + projectName + " UMLetFile: " + UMLetFile);

        if (ecoreFile == null && UMLetFile == null) {
            throw new MojoExecutionException("Missing ecoreFile and UMLetFile parameters");
        }
        if (projectDir == null) {
            projectDir = project.getBasedir().getAbsolutePath() + "/";
        }
        if (UMLetFile != null) {
            try {
                generateFromUMLet();
            } catch (IOException e) {
                throw new MojoExecutionException("Error while generating java code", e);
            }
            return;
        }
        try {
            generateJava();
        } catch (IOException e) {
            throw new MojoExecutionException("Error while generating java code", e);
        }
    }

    private void generateJava() throws IOException {
        JavaGenerator javaGenerator = new JavaGenerator();
        javaGenerator.generateCodeFromFiles(ecoreFile, projectDir);
    }

    private void generateFromUMLet() throws IOException {
        JavaGenerator javaGenerator = new JavaGenerator();
        javaGenerator.generateCodeFromUMLetFile(UMLetFile, projectDir, projectName);
    }
}
