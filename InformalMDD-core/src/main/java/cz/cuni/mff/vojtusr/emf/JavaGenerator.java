package cz.cuni.mff.vojtusr.emf;

import cz.cuni.mff.vojtusr.transformation.UMLetToEcoreTransformer;
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

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A utility class for generating Java code from UMLet and Ecore models.
 *
 * <p>The {@code JavaGenerator} class provides methods to facilitate the transformation of UMLet
 * diagram files into Ecore models, the creation of GenModels from Ecore models, and the generation
 * of Java code from GenModels. It integrates with the Eclipse Modeling Framework (EMF) to support
 * model-driven development workflows.</p>
 *
 * <p>Key features include:
 * <ul>
 *   <li>Transforming UMLet files into Ecore models.</li>
 *   <li>Generating GenModels from Ecore models.</li>
 *   <li>Generating Java code from GenModels and handling diagnostics.</li>
 * </ul>
 * </p>
 *
 * <p>This class is designed for use in projects that require automated code generation from
 * UML models and offers methods to streamline these processes.</p>
 */
public class JavaGenerator {
    public JavaGenerator() {
    }

    /**
     * Generates Java code from an Ecore model by performing transformations, creating a GenModel,
     * and generating the corresponding Java files. Works in-pair with currently open UMLet project.
     *
     * @param projectDir  The directory of the project where resources and generated code will be placed.
     * @param projectName The name of the project to be used in paths and transformations.
     * @throws IOException          If there is an issue creating directories or accessing files.
     * @throws TransformerException If there is an issue during the UMLet transformation process.
     */
    public void generateCodeFromUMLetProject(String projectDir, String projectName) throws IOException, TransformerException {
        // Construct the root and resources paths
        final String rootPath = projectDir + "/" + projectName;
        final String resourcesPath = rootPath + "/src/main/resources";
        final String outputFileEcore = resourcesPath + "/ecore.ecore";
        Path outputPath = Path.of(outputFileEcore);

        // Transform the UML diagram into an Ecore model using UMLetTransformer
        UMLetToEcoreTransformer transformer = new UMLetToEcoreTransformer(projectName);
        transformer.transformCurrentUMLet();
        transformer.saveEcore(outputFileEcore);

        Files.createDirectories(outputPath.getParent());

        // Generate the GenModel based on the Ecore model
        GenModelGenerator genModelGenerator = new GenModelGenerator();
        GenModel genModel = genModelGenerator.generateProjectGenModel(projectDir, projectName);

        // Generate Java code from the GenModel
        generateCodeFromGenModel(genModel, rootPath + "/src/main/");
    }

    /**
     * Generates Java code from an UMLet diagram file.
     *
     * <p>This method processes an UMLet diagram file by transforming it into an Ecore model, generating
     * a GenModel from the Ecore model, and then generating Java code based on the GenModel. The generated
     * files are organized in the specified project directory structure.</p>
     *
     * @param umletFilePath the file path to the UMLet diagram
     * @param projectDir    the directory of the project where the code should be generated
     * @param projectName   the name of the project, used to structure the output paths
     * @throws IOException if an error occurs during file operations
     */
    public void generateCodeFromUMLetFile(String umletFilePath, String projectDir, String projectName) throws IOException {
        final String rootPath = projectDir + "/" + projectName;
        final String resourcesPath = rootPath + "/src/main/resources";
        final String outputFileEcore = resourcesPath + "/ecore.ecore";
        Path outputPath = Path.of(outputFileEcore);

        // Transform the UML diagram into an Ecore model using UMLetTransformer
        UMLetToEcoreTransformer transformer = new UMLetToEcoreTransformer(projectName);
        transformer.transform(umletFilePath);
        transformer.saveEcore(outputFileEcore);

        Files.createDirectories(outputPath.getParent());

        // Generate the GenModel based on the Ecore model
        GenModelGenerator genModelGenerator = new GenModelGenerator();
        GenModel genModel = genModelGenerator.generateGenModelFromEcore(outputFileEcore);

        // Save the generated GenModel to the resources
        genModelGenerator.saveGenModel(genModel, resourcesPath);

        // Generate Java code from the GenModel
        generateCodeFromGenModel(genModel, rootPath + "/src/main/");
    }

    /**
     * Generates Java code from an Ecore model file and saves the generated files in the specified directory.
     *
     * <p>This method performs the following steps:
     * <ul>
     *   <li>Registers the necessary resource factories, packages, and adapter factories for working with GenModels.</li>
     *   <li>Generates a GenModel from the provided Ecore model file.</li>
     *   <li>Saves the GenModel to the specified directory.</li>
     *   <li>Generates Java code from the GenModel and handles any diagnostic issues.</li>
     * </ul>
     * </p>
     *
     * @param ecorePath         the file path to the Ecore model
     * @param generatedFilesDir the directory where generated files will be saved
     * @throws IOException if an error occurs during file operations
     */
    public void generateCodeFromFiles(String ecorePath, String generatedFilesDir) throws IOException {
        // Register the GenModel resource factory to handle .genmodel files
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("genmodel", new XMIResourceFactoryImpl());

        // Register the GenModel package to the EPackage registry
        EPackage.Registry.INSTANCE.put(GenModelPackage.eNS_URI, GenModelPackage.eINSTANCE);

        // Add the descriptor for the GenModel adapter factory
        GeneratorAdapterFactory.Descriptor.Registry.INSTANCE.addDescriptor(
                GenModelPackage.eNS_URI, GenModelGeneratorAdapterFactory.DESCRIPTOR
        );

        // Generate the GenModel based on the Ecore model
        GenModelGenerator genModelGenerator = new GenModelGenerator();
        GenModel genModel = genModelGenerator.generateGenModelFromEcore(ecorePath);

        // Save the generated GenModel to the resources
        genModelGenerator.saveGenModel(genModel, generatedFilesDir);

        Diagnostic diagnostic = generateJavaCodeForGenModel(genModel, generatedFilesDir + "/src/main/");
        // Check for generation errors and print appropriate messages
        if (diagnostic.getSeverity() == Diagnostic.ERROR) {
            System.err.println(diagnostic);
        } else {
            System.out.println("Code generation complete.");
        }
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
     * @param genModel the GenModel instance to generate code from
     * @param rootPath the root directory where the generated code will be saved
     */
    public void generateCodeFromGenModel(GenModel genModel, String rootPath) {
        // Register the GenModel resource factory to handle .genmodel files
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("genmodel", new XMIResourceFactoryImpl());

        // Register the GenModel package to the EPackage registry
        EPackage.Registry.INSTANCE.put(GenModelPackage.eNS_URI, GenModelPackage.eINSTANCE);

        // Add the descriptor for the GenModel adapter factory
        GeneratorAdapterFactory.Descriptor.Registry.INSTANCE.addDescriptor(
                GenModelPackage.eNS_URI, GenModelGeneratorAdapterFactory.DESCRIPTOR
        );

        Diagnostic diagnostic = generateJavaCodeForGenModel(genModel, rootPath);
        // Check for generation errors and print appropriate messages
        if (diagnostic.getSeverity() == Diagnostic.ERROR) {
            System.err.println(diagnostic);
        } else {
            System.out.println("Code generation complete.");
        }
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
    private Diagnostic generateJavaCodeForGenModel(GenModel genModel, String generatedFilesDir) {
        // Reconcile the GenModel to ensure it is up-to-date with the underlying Ecore model
        genModel.reconcile();

        // Configure GenModel properties for code generation
        genModel.setCanGenerate(true);
        genModel.setValidateModel(true);
        genModel.setForceOverwrite(true);

        // Map the GenModel's root container to the specified root path
        final String rootContainer = genModel.getModelName();
        EcorePlugin.getPlatformResourceMap().put(rootContainer, URI.createFileURI(generatedFilesDir));

        // Create a generator instance for processing the GenModel
        Generator generator = new Generator();
        generator.setInput(genModel);

        // Perform code generation for the model project
        return generator.generate
                (genModel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE, "model project",
                        new BasicMonitor.Printing(System.out));
    }
}
