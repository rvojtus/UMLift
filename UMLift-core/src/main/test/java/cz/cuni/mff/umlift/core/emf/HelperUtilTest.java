package cz.cuni.mff.umlift.core.emf;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Tests functionality of {@link HelperUtil}.
 */
public class HelperUtilTest {
    private File tmpFile;

    @AfterEach
    void tearDown() throws IOException {
        // Cleanup
        Files.deleteIfExists(tmpFile.toPath());
    }

    @Test
    void givenEcoreModel_whenSaveEcoreModel_thenCorrect() throws IOException {
        tmpFile = File.createTempFile("test", ".ecore");

        // Create an EPackage
        EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();

        // Create an EClass and add it to the EPackage
        EClass eclass = EcoreFactory.eINSTANCE.createEClass();
        eclass.setName("TestClass");
        ePackage.getEClassifiers().add(eclass);

        assertTrue(tmpFile.exists());
        assertNotNull(ePackage);

        // Save the EPackage to an Ecore file
        HelperUtil.saveEcoreModel(ePackage, tmpFile.toPath());

        // Validate
        checkIfEcoreContainsClass(tmpFile, eclass.getName());
    }

    void checkIfEcoreContainsClass(File ecoreFile, final String className) throws IOException {
        EPackage ePackage = getEPackage(ecoreFile);
        assertNotNull(ePackage, "EPackage is null, from file: " + ecoreFile.getAbsolutePath());
        assertNotNull(ePackage.getEClassifiers(), "EClassifiers is null");
        assertNotNull(ePackage.getEClassifier(className), "Class: " + className + " is null");
    }

    EPackage getEPackage(File ecoreFile) throws IOException {
        // Ensure the Ecore resource factory is registered
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
                .put("ecore", new EcoreResourceFactoryImpl());

        // Create a ResourceSet and load the .ecore file
        ResourceSet resourceSet = new ResourceSetImpl();
        URI fileURI = URI.createFileURI(ecoreFile.getAbsolutePath());
        Resource resource = resourceSet.getResource(fileURI, true);

        // Get the root element, which should be an EPackage
        if (!resource.getContents().isEmpty()) {
            EObject rootObject = resource.getContents().getFirst();
            if (rootObject instanceof EPackage) {
                return (EPackage) rootObject;
            }
        }

        throw new IOException("Failed to load EPackage from " + ecoreFile.getAbsolutePath());
    }
}
