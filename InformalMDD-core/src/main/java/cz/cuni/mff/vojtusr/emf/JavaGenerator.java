package cz.cuni.mff.vojtusr.emf;

import cz.cuni.mff.vojtusr.transformation.UMLetTransformer;
import cz.cuni.mff.vojtusr.transformation.XSLT;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Generates Java code from provided Ecore and GenModel files, using EMF libraries
 */
public class JavaGenerator {
    public JavaGenerator() {}

    public void generate(String projectDir, String projectName) throws IOException, TransformerException {
        final String rootPath = projectDir + "/" + projectName;
        final String resourcesPath = rootPath + "/src/main/resources";
        final String outputFileEcore = resourcesPath + "/ecore.ecore";
        Path outputPath = Path.of(outputFileEcore);

        UMLetTransformer transformer = new UMLetTransformer(projectName);
        transformer.transform(outputFileEcore);

        Files.createDirectories(outputPath.getParent());

        GenModelGenerate genModelGenerate = new GenModelGenerate();
        GenModel genModel = genModelGenerate.generate(projectDir, projectName);

        JavaGenerator.generateCode(genModel, rootPath + "/src/main/");

    }

    /**
     * Generates Java code from provided GenModel
     * @param genModel
     * @throws java.io.IOException
     */
    public static void generateCode(GenModel genModel, String rootPath) throws java.io.IOException {
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("genmodel", new XMIResourceFactoryImpl());
        EPackage.Registry.INSTANCE.put(GenModelPackage.eNS_URI, GenModelPackage.eINSTANCE);

        GeneratorAdapterFactory.Descriptor.Registry.INSTANCE.addDescriptor(
                GenModelPackage.eNS_URI, GenModelGeneratorAdapterFactory.DESCRIPTOR
        );

        genModel.reconcile();
        genModel.setCanGenerate(true);
        genModel.setValidateModel(true);
        genModel.setForceOverwrite(true);
        final String rootContainer = genModel.getModelName();;
        EcorePlugin.getPlatformResourceMap().put(rootContainer, URI.createFileURI(rootPath));

        // Create the generator
        Generator generator = new Generator();
        generator.setInput(genModel);

        // Generate the model code
        Diagnostic diagnostic = generator.generate
                (genModel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE, "model project",
                        new BasicMonitor.Printing(System.out));

        if (diagnostic.getSeverity() == Diagnostic.ERROR) {
            System.err.println(diagnostic);
        }
        else {
            System.out.println("Code generation complete.");
        }
    }

    /**
     * Prints content of an Ecore Package - For debugging purposes
     * @param ePackage
     */
    private static void printEPackageContents(EPackage ePackage) {
        System.out.println("EPackage Name: " + ePackage.getName());
        System.out.println("EPackage Namespace URI: " + ePackage.getNsURI());
        System.out.println("EPackage Namespace Prefix: " + ePackage.getNsPrefix());
        System.out.println();

        for (EClassifier eClassifier : ePackage.getEClassifiers()) {
            if (eClassifier instanceof EClass) {
                EClass eClass = (EClass) eClassifier;
                System.out.println("EClass Name: " + eClass.getName());

                // Print super types
                for (EClass superType : eClass.getESuperTypes()) {
                    System.out.println("  Super Type: " + superType.getName());
                }

                // Print structural features
                for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
                    System.out.println("  Feature: " + feature.getName() + " (Type: " + feature.getEType().getName() + ")");
                    if (feature instanceof EReference) {
                        EReference reference = (EReference) feature;
                        System.out.println("    Reference: " + reference.getName() + " -> " + reference.getEReferenceType().getName());
                    }
                }

                System.out.println();
            }
        }
    }
}
