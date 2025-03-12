package cz.cuni.mff.umlift.core.emf;

import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelFactory;

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
     * <p>This constructor initializes the {@link ResourceSet} used to load Ecore models.</p>
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
     * @param inputEcorePath the file path to the Ecore file
     * @return the generated {@link GenModel} instance
     */
    public GenModel generateGenModelFromEcore(Path inputEcorePath) {
        // Load the Ecore model
        inputEcorePath = HelperUtil.addFileTypeSuffix(inputEcorePath, ".ecore");
        URI ecoreURI = URI.createFileURI(inputEcorePath.toAbsolutePath().toString());
        Resource ecoreResource = resourceSet.getResource(ecoreURI, true);
        EPackage ecorePackage = (EPackage) ecoreResource.getContents().getFirst();

        // Create a GenModel instance
        GenModel genModel = GenModelFactory.eINSTANCE.createGenModel();
        genModel.setModelName(ecorePackage.getName());
        genModel.setModelDirectory("src"); // todo needs proper documentation
        genModel.setComplianceLevel(GenJDKLevel.JDK220_LITERAL); // todo make it configurable

        GenPackage genPackage = GenModelFactory.eINSTANCE.createGenPackage();
        genPackage.setEcorePackage(ecorePackage);
        genPackage.setGenModel(genModel);

        genModel.getGenPackages().add(genPackage);
        genModel.initialize(Collections.singleton(ecorePackage));


        LOG.info("GenModel created successfully for Ecore: " + inputEcorePath.toAbsolutePath());
        return genModel;
    }

    public void setBasePackage(GenModel genModel, String basePackage) {
        genModel.getGenPackages().getFirst().setBasePackage(basePackage);
    }

}
