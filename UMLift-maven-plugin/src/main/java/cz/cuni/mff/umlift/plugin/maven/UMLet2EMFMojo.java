package cz.cuni.mff.umlift.plugin.maven;

import cz.cuni.mff.umlift.core.config.GenerationConfig;
import cz.cuni.mff.umlift.core.emf.GenModelGenerator;
import cz.cuni.mff.umlift.core.util.HelperUtil;
import cz.cuni.mff.umlift.core.emf.ModelToCodeGenerator;
import cz.cuni.mff.umlift.core.transformation.UMLetToEcoreTransformer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.ecore.EPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOG = LoggerFactory.getLogger(UMLet2EMFMojo.class);

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

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

    @Parameter(property = "ecoreFileName", defaultValue = "ecore")
    private String ecoreFileName;

    @Parameter(property = "genModelFileName", defaultValue = "genmodel")
    private String genModelFileName;

    @Parameter(property = "basePackageName", defaultValue = "org.example")
    private String basePackageName;

    @Parameter(property = "genJDKLevel", defaultValue = "JDK210") // GenJDKLevel.JDK210
    private String genJDKLevel;

    /**
     * Executes the Maven Mojo to transform a UMLet project file into an Ecore model
     * and subsequently generate a GenModel.
     * <p>
     * This method is the main entry point for the UMLet2EMF Maven plugin. It leverages
     * the internal transformation logic to process a UMLet file, transforming it into
     * an Ecore model and generating the associated GenModel. Informational messages are
     * logged throughout the process. Any issues encountered during execution will
     * result in a thrown exception.
     * <p>
     * Logs the transformation process, including the target UMLet file, model directory,
     * project details, and output filenames for the Ecore model and GenModel.
     *
     * @throws MojoExecutionException if an unexpected error occurs during file transformation
     *                                or output generation, such as IO failures or invalid paths.
     * @throws MojoFailureException   if the transformation process fails due to issues
     *                                such as invalid UMLet input or configuration errors.
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        LOG.info("""
                        Transforming {}...
                        Model Directory: {}
                        Project Name: {}, NsPrefix: {}, NsURI: {}
                        Ecore File Name: {}, GenModel File Name: {}""",
                umletFile, modelDir, projectName, NsPrefix, NsURI, ecoreFileName, genModelFileName);
        transformUMLetFile();
        LOG.info("Transformation complete.");
    }

    private void transformUMLetFile() throws MojoExecutionException, MojoFailureException {
        UMLetToEcoreTransformer transformer = new UMLetToEcoreTransformer.EcoreConfigBuilder()
                .setEPackageName(projectName)
                .setEPackageNsPrefix(NsPrefix)
                .setEPackageNsURI(NsURI)
                .build();

        EPackage ePackage = transformer.transform(Path.of(umletFile));
        if (ePackage == null) {
            throw new MojoExecutionException("Could not transform UMLet file: " + umletFile);
        }
        final Path ecoreFilePath = Path.of(modelDir + File.separator + ecoreFileName);
        try {
            HelperUtil.saveEcoreModel(ePackage, ecoreFilePath);
        } catch (IOException e) {
            throw new MojoExecutionException("Could not save EcoreModel", e);
        }

        GenerationConfig.getInstance()
                .setGenJDKLevel(GenJDKLevel.valueOf(genJDKLevel + "_LITERAL"))
                .setBasePackage(basePackageName);
        GenModelGenerator genModelGenerator = new GenModelGenerator();
        GenModel genModel = genModelGenerator.generateGenModelFromEcore(ecoreFilePath);

        if (genModel == null) {
            throw new MojoExecutionException("Could not generate GenModel");
        }

        final Path genmodelFilePath = Path.of(modelDir + File.separator + genModelFileName);

        try {
            HelperUtil.saveGenModel(genModel, genmodelFilePath);
        } catch (IOException e) {
            throw new MojoExecutionException("Could not save GenModel", e);
        }
    }
}
