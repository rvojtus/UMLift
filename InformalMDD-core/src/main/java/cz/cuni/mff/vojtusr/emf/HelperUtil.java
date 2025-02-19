package cz.cuni.mff.vojtusr.emf;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.logging.Logger;

public class HelperUtil {
    private static final Logger LOG = Logger.getLogger(HelperUtil.class.getName());

    /**
     * Saves an Ecore model to a specified file using the XMI format.
     *
     * @param ePackage        the EPackage to be saved
     * @param outputEcoreFile the name of the file where the Ecore model will be saved
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
     * Prints content of an Ecore Package - For debugging purposes
     *
     * @param ePackage
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
