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

import java.io.IOException;
import java.util.Collections;

/**
 * A utility class for generating GenModels from Ecore models.
 *
 * <p>The {@code GenModelGenerator} class provides methods for creating a {@link GenModel} instance
 * from an Ecore model. It initializes a {@link ResourceSet} to handle the loading of Ecore models
 * and saves the resulting GenModel to a file. This class serves as a bridge between Ecore models
 * and GenModel, which is used in the Eclipse Modeling Framework for code generation.</p>
 */
public class GenModelGenerator {
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
     * Generates a GenModel for the specified project using the provided Ecore model.
     *
     * <p>This method loads an Ecore model from the project's resources, creates a GenModel instance
     * based on the Ecore model, and saves the GenModel to a file. The method sets the project name
     * and directory for the GenModel, initializes it with the Ecore package, and configures the JDK
     * compliance level for code generation.</p>
     *
     * <p>Steps performed:
     * <ul>
     *   <li>Loads the Ecore model from a specified file.</li>
     *   <li>Creates and initializes a GenModel using the Ecore model.</li>
     *   <li>Saves the generated GenModel to a file in the project's resources directory.</li>
     * </ul>
     * </p>
     *
     * @param   projectDir the directory where the project is located
     * @param   projectName the name of the project, used to set the GenModel properties
     * @return  the generated {@link GenModel} instance
     * @throws  IOException if an error occurs while reading or writing the Ecore or GenModel files
     */
    public GenModel generateProjectGenModel(String projectDir, String projectName) throws IOException {
        final String resourcesPath = projectDir + "/" + projectName + "/src/main/resources";

        // Load the Ecore model
        URI ecoreURI = URI.createFileURI(resourcesPath + "/ecore.ecore");// todo maybe change file name
        Resource ecoreResource = resourceSet.getResource(ecoreURI, true);
        EPackage ecorePackage = (EPackage) ecoreResource.getContents().getFirst();

        // Create a GenModel instance
        GenModel genModel = GenModelFactory.eINSTANCE.createGenModel();
        genModel.setModelName(projectName);
        genModel.setModelDirectory(projectName + "/java");
        genModel.setComplianceLevel(GenJDKLevel.JDK220_LITERAL);
        genModel.initialize(Collections.singleton(ecorePackage));

        // Save the GenModel to a file
        URI genmodelURI = URI.createFileURI(resourcesPath + "/genmodel.genmodel");// todo maybe change file name
        final XMIResourceImpl genModelResource = new XMIResourceImpl(genmodelURI);
        genModelResource.getDefaultSaveOptions().put(XMIResource.OPTION_ENCODING, "UTF-8");
        genModelResource.getContents().add(genModel);
        genModelResource.save(Collections.EMPTY_MAP);

        System.out.println("Genmodel created successfully.");
        return genModel;
    }

    /**
     * Generates a GenModel from the specified Ecore model file.
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

        System.out.println("Genmodel created successfully.");
        return genModel;
    }

    /**
     * Saves the specified GenModel to a file in the given directory.
     *
     * <p>This method serializes the provided {@link GenModel} instance to an XMI file format and saves
     * it to the specified directory. The file is named {@code genmodel.genmodel}, and it is saved using
     * UTF-8 encoding.</p>
     *
     * @param genModel the {@link GenModel} instance to save
     * @param dirToSave the directory where the GenModel file will be saved
     * @throws IOException if an error occurs while saving the GenModel file
     */
    public void saveGenModel(GenModel genModel, String dirToSave) throws IOException {
        URI genmodelURI = URI.createFileURI(dirToSave + "/genmodel.genmodel"); // todo test
        final XMIResourceImpl genModelResource = new XMIResourceImpl(genmodelURI);
        genModelResource.getDefaultSaveOptions().put(XMIResource.OPTION_ENCODING, "UTF-8");
        genModelResource.getContents().add(genModel);
        genModelResource.save(Collections.EMPTY_MAP);
    }
}
