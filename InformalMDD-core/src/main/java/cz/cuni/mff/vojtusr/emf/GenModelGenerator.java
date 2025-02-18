package cz.cuni.mff.vojtusr.emf;

import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelFactory;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * A utility class for generating GenModels from Ecore models.
 *
 * <p>The {@code GenModelGenerator} class provides methods for creating a {@link GenModel} instance
 * from an Ecore model. It initializes a {@link ResourceSet} to handle the loading of Ecore models
 * and saves the resulting GenModel to a file. This class serves as a bridge between Ecore models
 * and GenModel, which is used in the Eclipse Modeling Framework for code generation.</p>
 */
public class GenModelGenerator {
    private static final Logger LOG = Logger.getLogger(GenModelGenerator.class.getName());

    private ResourceSet resourceSet;

    /**
     * Initializes a new {@link GenModelGenerator} instance and sets up the resource set.
     *
     * <p>This constructor initializes the {@link ResourceSet} used to load Ecore models. It ensures
     * that the necessary configuration for processing Ecore files is set up.</p>
     */
    public GenModelGenerator() {
        initializeResourceSet();
    }

    /**
     * Initializes the resource set for loading and saving Ecore models.
     */
    private void initializeResourceSet() {
        // Initialize resource set
        resourceSet = new ResourceSetImpl();

        // Register resource set
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
    }

    /**
     * Generates a GenModel from the specified Ecore file.
     *
     * <p>This method loads an Ecore model from the provided file path, creates a GenModel instance
     * based on the Ecore model, and initializes it with the Ecore package. The method also sets
     * the model's name, directory for generated Java code, and compliance level.</p>
     *
     * <p>Steps performed:
     * <ul>
     *   <li>Loads the Ecore model from the specified file.</li>
     *   <li>Creates and initializes a GenModel using the Ecore model's package.</li>
     *   <li>Configures the GenModel with project-specific settings (e.g., model name, directory, JDK compliance).</li>
     * </ul>
     * </p>
     *
     * @param ecoreFilePath the file path to the Ecore model
     * @return the generated {@link GenModel} instance
     */
    public GenModel generateGenModelFromEcore(String ecoreFilePath) {
        // Load the Ecore model
        URI ecoreURI = URI.createFileURI(ecoreFilePath);
        Resource ecoreResource = resourceSet.getResource(ecoreURI, true);
        EPackage ecorePackage = (EPackage) ecoreResource.getContents().getFirst();

        // Create a GenModel instance
        GenModel genModel = GenModelFactory.eINSTANCE.createGenModel();
        genModel.setModelName(ecorePackage.getName());
        genModel.setModelDirectory(ecorePackage.getName() + "/java");
        genModel.setComplianceLevel(GenJDKLevel.JDK220_LITERAL);
        genModel.initialize(Collections.singleton(ecorePackage));

        LOG.info("Genmodel created successfully.");
        return genModel;
    }

    public GenModel generateGenModelFromEcore(File ecoreFile) {
        return generateGenModelFromEcore(ecoreFile.getAbsolutePath());
    }

    /**
     * Saves the specified GenModel to a file in the given directory.
     *
     * <p>This method serializes the provided {@link GenModel} instance to an XMI file format and saves
     * it to the specified directory. The file is named {@code genmodel.genmodel}, and it is saved using
     * UTF-8 encoding.</p>
     *
     * @param genModel  the {@link GenModel} instance to save
     * @param dirToSave the directory where the GenModel file will be saved
     * @throws IOException if an error occurs while saving the GenModel file
     */
    public File saveGenModel(GenModel genModel, String dirToSave) throws IOException {
        return saveGenModel(genModel, dirToSave, "/genmodel.genmodel");
    }

    public File saveGenModel(GenModel genModel, Path filePath) throws IOException {
        return saveGenModel(genModel, filePath.toAbsolutePath().toString(), "");
    }

    public File saveGenModel(GenModel genModel, String dirToSave, String fileName) throws IOException {
        URI genmodelURI = URI.createFileURI(dirToSave + fileName);
        final XMIResourceImpl genModelResource = new XMIResourceImpl(genmodelURI);
        genModelResource.getDefaultSaveOptions().put(XMIResource.OPTION_ENCODING, "UTF-8");
        genModelResource.getContents().add(genModel);
        genModelResource.save(Collections.EMPTY_MAP);
        LOG.info("Genmodel saved successfully to: " + genmodelURI.toString());
        return new File(genmodelURI.path());
    }

}
