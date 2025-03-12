package cz.cuni.mff.umlift.plugin.maven;

import cz.cuni.mff.umlift.core.emf.GenModelGenerator;
import cz.cuni.mff.umlift.core.emf.HelperUtil;
import cz.cuni.mff.umlift.core.emf.ModelToCodeGenerator;
import cz.cuni.mff.umlift.core.transformation.UMLetToEcoreTransformer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.ecore.EPackage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * A Maven plugin that transforms UMLet Diagrams to Eclipse Modeling Framework Models - Ecore and GenModel.
 *
 * <p>
 * This plugin processes UMLet's ".uxf" files, transforms them to EMF Ecore and GenModel.
 * </p>
 *
 * @see ModelToCodeGenerator
 * @since 1.0
 */
@Mojo(name = "generate-emf-from-umlet")
public class UMLet2EMFMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

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

    @Parameter(property = "package", defaultValue = "org.example")
    private String basePackage;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Transforming " + inputUMLetFile + "...\n" +
                "Model Directory: " + modelDir + "\n" +
                "Project Name: " + projectName + ", NsPrefix: " + NsPrefix + ", NsURI: " + NsURI + "\n" +
                "Ecore File Name: " + ecoreFileName + ", GenModel File Name: " + genmodelFileName
        );
        transformUMLetFile();
    }

    private void transformUMLetFile() throws MojoExecutionException, MojoFailureException {
        UMLetToEcoreTransformer transformer = new UMLetToEcoreTransformer.EcoreConfigBuilder()
                .setEPackageName(projectName)
                .setEPackageNsPrefix(NsPrefix)
                .setEPackageNsURI(NsURI)
                .build();

        EPackage ePackage = transformer.transform(Path.of(inputUMLetFile));
        if (ePackage == null) {
            throw new MojoExecutionException("Could not transform UMLet file: " + inputUMLetFile);
        }
        final Path ecoreFilePath = Path.of(modelDir + File.separator + ecoreFileName);
        try {
            HelperUtil.saveEcoreModel(ePackage, ecoreFilePath);
        } catch (IOException e) {
            throw new MojoExecutionException("Could not save EcoreModel", e);
        }

        GenModelGenerator genModelGenerator = new GenModelGenerator();
        GenModel genModel = genModelGenerator.generateGenModelFromEcore(ecoreFilePath);

        if (genModel == null) {
            throw new MojoExecutionException("Could not generate GenModel");
        }

        genModelGenerator.setBasePackage(genModel, basePackage);
        final Path genmodelFilePath = Path.of(modelDir + File.separator + genmodelFileName);

        try {
            HelperUtil.saveGenModel(genModel, genmodelFilePath);
        } catch (IOException e) {
            throw new MojoExecutionException("Could not save GenModel", e);
        }
    }
}
