package cz.cuni.mff.vojtusr.emf;

import cz.cuni.mff.vojtusr.transformation.UMLetTransformer;
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
 * Generates Java code from provided Ecore and GenModel files, using EMF libraries
 */
public class JavaGenerator {
    public JavaGenerator() {}

    /**
     * Generates Java code from an Ecore model by performing transformations, creating a GenModel,
     * and generating the corresponding Java files.
     *
     * @param projectDir  The directory of the project where resources and generated code will be placed.
     * @param projectName The name of the project to be used in paths and transformations.
     * @throws IOException           If there is an issue creating directories or accessing files.
     * @throws TransformerException  If there is an issue during the UMLet transformation process.
     */
    public void generate(String projectDir, String projectName) throws IOException, TransformerException {
        // Construct the root and resources paths
        final String rootPath = projectDir + "/" + projectName;
        final String resourcesPath = rootPath + "/src/main/resources";
        final String outputFileEcore = resourcesPath + "/ecore.ecore";
        Path outputPath = Path.of(outputFileEcore);

        // Transform the UML diagram into an Ecore model using UMLetTransformer
        UMLetTransformer transformer = new UMLetTransformer(projectName);
        transformer.transform(outputFileEcore);

        Files.createDirectories(outputPath.getParent());

        // Generate the GenModel based on the Ecore model
        GenModelGenerate genModelGenerate = new GenModelGenerate();
        GenModel genModel = genModelGenerate.generate(projectDir, projectName);

        // Generate Java code from the GenModel
        generateCode(genModel, rootPath + "/src/main/");
    }

    /**
     * Generates Java code from a provided GenModel using EMF libraries.
     *
     * @param genModel The GenModel instance used to generate Java code.
     * @param rootPath The root path where the generated Java files will be saved.
     */
    private void generateCode(GenModel genModel, String rootPath) {
        // Register the GenModel resource factory to handle .genmodel files
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("genmodel", new XMIResourceFactoryImpl());
        
        // Register the GenModel package to the EPackage registry
        EPackage.Registry.INSTANCE.put(GenModelPackage.eNS_URI, GenModelPackage.eINSTANCE);

        // Add the descriptor for the GenModel adapter factory
        GeneratorAdapterFactory.Descriptor.Registry.INSTANCE.addDescriptor(
                GenModelPackage.eNS_URI, GenModelGeneratorAdapterFactory.DESCRIPTOR
        );

        // Reconcile the GenModel to ensure it is up-to-date with the underlying Ecore model
        genModel.reconcile();

        // Configure GenModel properties for code generation
        genModel.setCanGenerate(true);
        genModel.setValidateModel(true);
        genModel.setForceOverwrite(true);

        // Map the GenModel's root container to the specified root path
        final String rootContainer = genModel.getModelName();
        EcorePlugin.getPlatformResourceMap().put(rootContainer, URI.createFileURI(rootPath));

        // Create a generator instance for processing the GenModel
        Generator generator = new Generator();
        generator.setInput(genModel);

        // Perform code generation for the model project
        Diagnostic diagnostic = generator.generate
                (genModel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE, "model project",
                        new BasicMonitor.Printing(System.out));

        // Check for generation errors and print appropriate messages
        if (diagnostic.getSeverity() == Diagnostic.ERROR) {
            System.err.println(diagnostic);
        }
        else {
            System.out.println("Code generation complete.");
        }
    }
}
