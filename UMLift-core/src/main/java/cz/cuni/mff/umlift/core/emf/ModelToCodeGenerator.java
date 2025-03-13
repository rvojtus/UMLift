package cz.cuni.mff.umlift.core.emf;

import cz.cuni.mff.umlift.core.transformation.UMLetToEcoreTransformer;
import org.eclipse.emf.codegen.ecore.generator.Generator;
import org.eclipse.emf.codegen.ecore.generator.GeneratorAdapterFactory;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenModelGeneratorAdapterFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

import static cz.cuni.mff.umlift.core.emf.HelperUtil.saveGenModel;

/**
 * A class for generating Java code from UMLet and Ecore models.
 *
 * <p>The {@code JavaGenerator} class provides methods for the transformation of UMLet
 * diagram files into Ecore models, the creation of GenModels from Ecore models, and the generation
 * of Java code from GenModels.</p>
 *
 * <p>Key features include:
 * <ul>
 *   <li>Transforming UMLet files into Ecore models.</li>
 *   <li>Generating GenModels from Ecore models.</li>
 *   <li>Generating Java code from GenModels and handling diagnostics.</li>
 * </ul>
 * </p>
 *
 * @see EPackage
 * @see GenModel
 */
public class ModelToCodeGenerator {
    private static final Logger LOG = Logger.getLogger(ModelToCodeGenerator.class.getName());

    private final CodeGenerationConfig codeGenerationConfig;

    /**
     * @param codeGenerationConfig represent a configuration, see {@link CodeGenerationConfig} for details
     */
    public ModelToCodeGenerator(CodeGenerationConfig codeGenerationConfig) {
        this.codeGenerationConfig = codeGenerationConfig;
    }

    /**
     * Generates Java code from an UMLet diagram file.
     *
     * <p>This method processes an UMLet diagram file by transforming it into an Ecore model, generating
     * a GenModel from the Ecore model, and then generating Java code based on the GenModel. The generated
     * files are organized in the specified project directory structure.</p>
     *
     * @throws IOException if an error occurs during file operations
     */
    public Diagnostic generateCodeFromUMLetFile(Path inputUMLetFilePath) throws IOException {
        // Transform the UML diagram into an Ecore model using UMLetTransformer
        UMLetToEcoreTransformer transformer = new UMLetToEcoreTransformer.EcoreConfigBuilder()
                .setEPackageName(codeGenerationConfig.getProjectName())
                .setEPackageNsPrefix(codeGenerationConfig.getProjectNsPrefix())
                .setEPackageNsURI(codeGenerationConfig.getProjectNsURI())
                .build();
        transformer.transform(inputUMLetFilePath);
        final Path ecoreOutPath = HelperUtil.addFileTypeSuffix(codeGenerationConfig.getOutputEcoreFile(), ".ecore");
        transformer.saveEcore(ecoreOutPath);

        Files.createDirectories(codeGenerationConfig.getOutputEcoreFile().getParent());

        // Generate the GenModel based on the Ecore model
        GenModelGenerator genModelGenerator = new GenModelGenerator(codeGenerationConfig);
        GenModel genModel = genModelGenerator.generateGenModelFromEcore(ecoreOutPath);

        // Save the generated GenModel to the specified Path
        saveGenModel(genModel, codeGenerationConfig.getOutputGenModelFile());

        // Generate Java code from the GenModel
        return generateCodeFromGenModel(genModel, codeGenerationConfig.getGeneratedFilesDir());
    }

    /**
     * Generates Java code from an Ecore model file and saves the generated files in the specified directory.
     *
     * <p>This method performs the following steps:
     * <ul>
     *   <li>Registers the necessary resource factories, packages, and adapter factories for working with GenModels
     *   .</li>
     *   <li>Generates a GenModel from the provided Ecore model file.</li>
     *   <li>Generates Java code from the GenModel and handles any diagnostic issues.</li>
     * </ul>
     * </p>
     *
     * @param inputEcorePath    the file path to the Ecore model
     * @param generatedFilesDir the directory where generated files will be saved
     * @throws IOException if an error occurs during file operations
     */
    public Diagnostic generateCodeFromEcore(Path inputEcorePath, Path generatedFilesDir) throws IOException {
        // Register the GenModel resource factory to handle .genmodel files
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("genmodel", new XMIResourceFactoryImpl());

        // Register the GenModel package to the EPackage registry
        EPackage.Registry.INSTANCE.put(GenModelPackage.eNS_URI, GenModelPackage.eINSTANCE);

        // Add the descriptor for the GenModel adapter factory
        GeneratorAdapterFactory.Descriptor.Registry.INSTANCE.addDescriptor(
                GenModelPackage.eNS_URI, GenModelGeneratorAdapterFactory.DESCRIPTOR
        );

        // Generate the GenModel based on the Ecore model
        GenModelGenerator genModelGenerator = new GenModelGenerator(codeGenerationConfig);
        GenModel genModel = genModelGenerator.generateGenModelFromEcore(inputEcorePath);

        // Save the generated GenModel to the resources
        //genModelGenerator.saveGenModel(genModel, generatedFilesDir); // todo maybe allow it?

        return generateJavaCodeForGenModel(genModel, generatedFilesDir);
    }

    /**
     * Generates Java code from a given GenModel and saves the output in the specified directory.
     *
     * <p>This method performs the necessary setup for handling GenModel resources, including:
     * <ul>
     *   <li>Registering the GenModel resource factory for handling .genmodel files.</li>
     *   <li>Registering the GenModel package to the EPackage registry.</li>
     *   <li>Adding the descriptor for the GenModel adapter factory.</li>
     * </ul>
     * </p>
     *
     * <p>It then generates Java code from the provided GenModel and saves it to the specified root path.
     * Any errors or issues during code generation are logged to the console.</p>
     *
     * @param genModel          the GenModel instance to generate code from
     * @param generatedFilesDir the root directory where the generated code will be saved
     */
    public Diagnostic generateCodeFromGenModel(GenModel genModel, Path generatedFilesDir) {
        // Register the GenModel resource factory to handle .genmodel files
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("genmodel", new XMIResourceFactoryImpl());

        // Register the GenModel package to the EPackage registry
        EPackage.Registry.INSTANCE.put(GenModelPackage.eNS_URI, GenModelPackage.eINSTANCE);

        // Add the descriptor for the GenModel adapter factory
        GeneratorAdapterFactory.Descriptor.Registry.INSTANCE.addDescriptor(
                GenModelPackage.eNS_URI, GenModelGeneratorAdapterFactory.DESCRIPTOR
        );

        return generateJavaCodeForGenModel(genModel, generatedFilesDir);
    }

    /**
     * Generates Java code for a given GenModel and saves it in the specified directory.
     *
     * <p>This method prepares the GenModel for code generation by:
     * <ul>
     *   <li>Reconciling the GenModel to ensure it is up-to-date with the underlying Ecore model.</li>
     *   <li>Configuring properties for code generation, such as validation and overwriting behavior.</li>
     *   <li>Mapping the GenModel's root container to the specified output directory.</li>
     * </ul>
     * </p>
     *
     * <p>It then creates a generator instance to process the GenModel and performs code generation for
     * the model project. The method returns a diagnostic object that provides information about the
     * success or failure of the code generation process.</p>
     *
     * @param genModel          the GenModel to generate code from
     * @param generatedFilesDir the directory where the generated files will be saved
     * @return a {@link Diagnostic} object containing the results of the code generation process
     */
    private Diagnostic generateJavaCodeForGenModel(GenModel genModel, Path generatedFilesDir) {
        // Reconcile the GenModel to ensure it is up-to-date with the underlying Ecore model
        genModel.reconcile();

        // Configure GenModel properties for code generation
        genModel.setCanGenerate(true);
        genModel.setValidateModel(true);
        genModel.setForceOverwrite(true);

        // Map the GenModel's root container to the specified root path
        final String rootContainer = "src";
        EcorePlugin.getPlatformResourceMap().put(rootContainer,
                URI.createFileURI(generatedFilesDir.toAbsolutePath().toString() + "/"));//todo
        LOG.info("Generating code to " + generatedFilesDir.toAbsolutePath());

        // Create a generator instance for processing the GenModel
        Generator generator = new Generator();
        generator.setInput(genModel);

        // Perform code generation for the model project
        return generator.generate
                (genModel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE, "model project",
                        new BasicMonitor.Printing(System.out));
    }
}
