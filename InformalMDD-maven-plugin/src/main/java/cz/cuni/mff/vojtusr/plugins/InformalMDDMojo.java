package cz.cuni.mff.vojtusr.plugins;

import cz.cuni.mff.vojtusr.emf.JavaGenerator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "InformalMDD", defaultPhase = LifecyclePhase.COMPILE)
public class InformalMDDMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter(property = "ecoreFile")
    private String ecoreFile;

    @Parameter(property = "projectDir")
    private String projectDir;

    @Parameter(property = "UMLetFile")
    private String UMLetFile;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Ecore: " + ecoreFile + " ProjectDir: " + projectDir + " UMLetFile: " + UMLetFile);
    }
}
