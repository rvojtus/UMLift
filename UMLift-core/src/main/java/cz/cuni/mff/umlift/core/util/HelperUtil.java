package cz.cuni.mff.umlift.core.util;

import cz.cuni.mff.umlift.core.config.GenerationConfig;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A utility class for working with EMF Resources. Provides static methods to save models to specified file paths.
 */
public class HelperUtil {
    private static final Logger LOG = Logger.getLogger(HelperUtil.class.getName());

    public static void saveRegisteredEcoreModels(Path ecoreOutPath) throws IOException {
        for (Object obj : EPackage.Registry.INSTANCE.values().toArray()) {
            if (obj instanceof EPackage pkg && pkg.getNsURI().startsWith(GenerationConfig.getInstance().getProjectNsURI())) {
                LOG.log(Level.INFO, "Saving Ecore models for {0}", obj);
                saveEcoreModel(pkg, Path.of(ecoreOutPath + "/" + pkg.getName() + ".ecore"));
            }
        }
    }

    public static void loadEcoreModelsFromDir(Path modelsDir, ResourceSet resourceSet) throws IOException {
        File[] files = modelsDir.toFile().listFiles((dir, name) -> name.endsWith(".ecore"));
        if (files == null) {
            throw new IOException("Cannot find any ecore models in " + modelsDir);
        }
        for (File file : files) {
            LOG.info("Loading ecore model from " + file.getAbsolutePath());
            URI uri = URI.createFileURI(file.getAbsolutePath());
            Resource resource = resourceSet.getResource(uri, true);
            EPackage ePackage = (EPackage) resource.getContents().getFirst();
            EPackage.Registry.INSTANCE.put(ePackage.getNsURI(), ePackage);
            LOG.info("Loaded Ecore model from " + file.getAbsolutePath());
        }
    }

    /**
     * Saves an Ecore model to a specified file using the XMI format.
     *
     * @param ePackage        the EPackage to be saved
     * @param outputEcoreFile the name of the file where the Ecore model will be saved
     * @throws IOException when an error occurs while saving the Ecore model
     */
    public static void saveEcoreModel(EPackage ePackage, Path outputEcoreFile) throws IOException {
        URI ecoreURI = URI.createFileURI(outputEcoreFile.toAbsolutePath().toString());
        XMIResourceImpl ecoreResource = new XMIResourceImpl(ecoreURI);
        ecoreResource.getDefaultSaveOptions().put(XMIResource.OPTION_ENCODING, "UTF-8");
        ecoreResource.getContents().add(ePackage);

        ecoreResource.save(Collections.EMPTY_MAP);

        LOG.info("Saved Ecore to: " + outputEcoreFile);
    }

    /**
     * Saves the provided GenModel to a file in the given directory.
     *
     * @param genModel  the {@link GenModel} instance to save
     * @param modelDir directory where to save the generated GenModel to
     * @return An instance of {@link File} representing the saved GenModel
     * @throws IOException if an error occurs while saving the GenModel model
     */

    public static File saveGenModel(GenModel genModel, Path modelDir) throws IOException {
        URI genmodelURI = URI.createFileURI(modelDir + "/" + GenerationConfig.getInstance().getProjectName() +
                ".genmodel");
        final XMIResourceImpl genModelResource = new XMIResourceImpl(genmodelURI);
        genModelResource.getDefaultSaveOptions().put(XMIResource.OPTION_ENCODING, "UTF-8");
        genModelResource.getContents().add(genModel);
        genModelResource.save(Collections.EMPTY_MAP);
        LOG.info("GenModel saved successfully to: " + genmodelURI.toString());
        return new File(genmodelURI.path());
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
            } else if (eClassifier instanceof EEnum eEnum) {
                System.out.println("EEnum Name: " + eEnum.getName());
                for (EEnumLiteral eEnumLiteral : eEnum.getELiterals())
                    System.out.println("  Literal Name: " + eEnumLiteral.getName());
            }
        }
    }
}
