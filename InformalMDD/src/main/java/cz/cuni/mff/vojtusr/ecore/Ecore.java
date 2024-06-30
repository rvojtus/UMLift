package cz.cuni.mff.vojtusr.ecore;

import org.eclipse.emf.codegen.ecore.generator.Generator;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import java.util.Collections;

/**
 * Generates Java code from provided Ecore and GenModel files, using EMF libraries
 */
public class Ecore {
    private final String ecore_file;
    private final String genmodel_file;

    public Ecore(String ecore_file, String genmodel_file) {
        this.ecore_file = ecore_file;
        this.genmodel_file = genmodel_file;
    }

    public void generate() throws java.io.IOException {
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());

        // Load the Ecore model
        URI ecoreURI = URI.createFileURI(ecore_file);
        Resource ecoreResource = resourceSet.getResource(ecoreURI, true);
        ecoreResource.load(Collections.EMPTY_MAP);

        // Load the GenModel
        URI genModelURI = URI.createFileURI(genmodel_file);
        Resource genModelResource = resourceSet.getResource(genModelURI, true);
        genModelResource.load(Collections.EMPTY_MAP);

        GenModel genModel = (GenModel) genModelResource.getContents().getFirst();
        genModel.reconcile();
        genModel.setCanGenerate(true);

        // Create the generator
        Generator generator = new Generator();
        generator.setInput(genModel);

        // Generate the model code
        generator.generate
                (genModel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE,
                        new BasicMonitor.Printing(System.out));

        System.out.println("Code generation complete.");
    }
}
