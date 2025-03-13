package cz.cuni.mff.umlift.plugin.maven;

import cz.cuni.mff.umlift.core.emf.CodeGenerationConfig;
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

    @Parameter(property = "jdkLevel", defaultValue = "17") // GenJDKLevel.JDK210
    private String genJDKLevel;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        LOG.info("Transforming {}...\n" +
                        "Model Directory: {}\n" +
                        "Project Name: {}, NsPrefix: {}, NsURI: {}\n" +
                        "Ecore File " + "Name: {}, GenModel File Name: {}",
                inputUMLetFile, modelDir, projectName, NsPrefix, NsURI, ecoreFileName, genmodelFileName);
        transformUMLetFile();
        LOG.info("Transformation complete.");
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

        CodeGenerationConfig codeGenerationConfig =
                CodeGenerationConfig.getInstance().setGenJDKLevel(GenJDKLevel.valueOf(genJDKLevel)).setBasePackage(basePackage);
        GenModelGenerator genModelGenerator = new GenModelGenerator(codeGenerationConfig);
        GenModel genModel = genModelGenerator.generateGenModelFromEcore(ecoreFilePath);

        if (genModel == null) {
            throw new MojoExecutionException("Could not generate GenModel");
        }

        final Path genmodelFilePath = Path.of(modelDir + File.separator + genmodelFileName);

        try {
            HelperUtil.saveGenModel(genModel, genmodelFilePath);
        } catch (IOException e) {
            throw new MojoExecutionException("Could not save GenModel", e);
        }
    }
}
