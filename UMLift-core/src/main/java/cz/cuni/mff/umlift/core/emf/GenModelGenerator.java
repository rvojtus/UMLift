package cz.cuni.mff.umlift.core.emf;

import cz.cuni.mff.umlift.core.config.GenerationConfig;
import cz.cuni.mff.umlift.core.util.HelperUtil;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
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
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore",
                new EcoreResourceFactoryImpl());
    }

    /**
     * Generates a GenModel from the specified Ecore file.
     *
     * <p>This method loads an Ecore model from the provided file path, creates a GenModel instance
     * based on the Ecore model, and initializes it with the Ecore package. The method also sets
     * the model's name, directory for generated Java code, and compliance level.</p>
     *
     * @param modelDirPath the file path to the Ecore file
     * @return the generated {@link GenModel} instance
     */
    public GenModel generateGenModelFromEcore(Path modelDirPath) throws IOException {
        // Load the Ecore models
        try {
            HelperUtil.loadEcoreModelsFromDir(modelDirPath, resourceSet);
        } catch (IOException e) {
            LOG.severe("Could not load Ecore models from directory " + modelDirPath);
            throw e;
        }

        // Create a GenModel instance
        GenModel genModel = GenModelFactory.eINSTANCE.createGenModel();
        genModel.setModelName(GenerationConfig.getInstance().getProjectName());
        genModel.setModelDirectory("src"); // todo needs proper documentation
        genModel.setComplianceLevel(GenerationConfig.getInstance().getGenJDKLevel());

        // Create GenPackages from Ecore models
        Collection<EPackage> ePackages = new ArrayList<>();
        for (Object obj : EPackage.Registry.INSTANCE.values()) {
            if (obj instanceof EPackage pkg && pkg.getNsURI().startsWith(GenerationConfig.getInstance().getProjectNsURI())) {
                LOG.info("Found EPackage " + pkg.getName());
                GenPackage genPackage = createGenPackageFromEcore(pkg, genModel);
                genModel.getGenPackages().add(genPackage);
                ePackages.add(pkg);
            }
        }

        genModel.initialize(ePackages);

        LOG.info("GenModel created successfully for Model Directory: " + modelDirPath.toAbsolutePath());
        return genModel;
    }

    private GenPackage createGenPackageFromEcore(EPackage ePackage, GenModel genModel) {
        GenPackage genPackage = GenModelFactory.eINSTANCE.createGenPackage();
        genPackage.setBasePackage(GenerationConfig.getInstance().getBasePackage());
        genPackage.setEcorePackage(ePackage);
        genPackage.setGenModel(genModel);
        return genPackage;
    }

}
