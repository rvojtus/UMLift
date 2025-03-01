package cz.cuni.mff.umlift.core.emf;

import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * A utility class for working with EMF Resources. Provides static methods to save models to specified file paths.
 */
public class HelperUtil {
    private static final Logger LOG = Logger.getLogger(HelperUtil.class.getName());

    /**
     * Saves an Ecore model to a specified file using the XMI format.
     *
     * @param ePackage        the EPackage to be saved
     * @param outputEcoreFile the name of the file where the Ecore model will be saved
     * @throws IOException when an error occurs while saving the Ecore model
     */
    public static void saveEcoreModel(EPackage ePackage, Path outputEcoreFile) throws IOException {
        // Register the XMI resource factory for handling Ecore models
        Resource.Factory.Registry.INSTANCE.getProtocolToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());

        // Create a resource set for managing resources
        ResourceSet resourceSet = new ResourceSetImpl();

        // Create a new resource for the specified file
        Resource resource = resourceSet.createResource(URI.createURI(outputEcoreFile.toAbsolutePath().toString()));

        // Add the EPackage to the resource's contents
        resource.getContents().add(ePackage);

        // Save the resource, which writes the Ecore model to the file
        resource.save(Collections.EMPTY_MAP);

        LOG.info("Saved Ecore to: " + outputEcoreFile);
    }

    /**
     * Saves the provided GenModel to a file in the given directory.
     *
     * @param genModel  the {@link GenModel} instance to save
     * @param outputDir directory where to save the generated GenModel to
     * @return An instance of {@link File} representing the saved GenModel
     * @throws IOException if an error occurs while saving the GenModel model
     */
    public static File saveGenModel(GenModel genModel, Path outputDir) throws IOException {
        return saveGenModel(genModel, HelperUtil.addFileTypeSuffix(outputDir, ".genmodel"), "");
    }

    public static File saveGenModel(GenModel genModel, Path outputDir, String fileName) throws IOException {
        URI genmodelURI = URI.createFileURI(outputDir + fileName);
        final XMIResourceImpl genModelResource = new XMIResourceImpl(genmodelURI);
        genModelResource.getDefaultSaveOptions().put(XMIResource.OPTION_ENCODING, "UTF-8");
        genModelResource.getContents().add(genModel);
        genModelResource.save(Collections.EMPTY_MAP);
        LOG.info("GenModel saved successfully to: " + genmodelURI.toString());
        return new File(genmodelURI.path());
    }

    /**
     * Checks if the provided {@code filePath} contains the required {@code suffix} and if not, appends it
     *
     * @param filePath path to check
     * @param suffix   to add
     * @return original path if suffix is already present, modified path otherwise
     */
    public static Path addFileTypeSuffix(Path filePath, final String suffix) {
        if (filePath.getFileName().toString().endsWith(suffix)) {
            return filePath;
        }
        return filePath.resolveSibling(filePath.getFileName() + suffix);
    }

    /**
     * Prints content of an Ecore Package - For debugging purposes
     *
     * @param ePackage Ecore model to print the contents of
     */
    public static void printEPackageContents(EPackage ePackage) {
        System.out.println("EPackage Name: " + ePackage.getName());
        System.out.println("EPackage Namespace URI: " + ePackage.getNsURI());
        System.out.println("EPackage Namespace Prefix: " + ePackage.getNsPrefix());
        System.out.println();

        for (EClassifier eClassifier : ePackage.getEClassifiers()) {
            if (eClassifier instanceof EClass eClass) {
                System.out.println("EClass Name: " + eClass.getName());

                // Print super types
                for (EClass superType : eClass.getESuperTypes()) {
                    System.out.println("  Super Type: " + superType.getName());
                }

                // Print structural features
                for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
                    System.out.println("  Feature: " + feature.getName() + " (Type: " + feature.getEType().getName() + ")");
                    if (feature instanceof EReference reference) {
                        System.out.println("    Reference: " + reference.getName() + " -> " + reference.getEReferenceType().getName() + " -> " + reference.getEType());
                    }
                }

                System.out.println();
            }
        }
    }
}
