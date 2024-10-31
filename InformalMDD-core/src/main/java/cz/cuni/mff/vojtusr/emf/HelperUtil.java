package cz.cuni.mff.vojtusr.emf;

import org.eclipse.emf.ecore.*;

public class HelperUtil {
    /**
     * Prints content of an Ecore Package - For debugging purposes
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
