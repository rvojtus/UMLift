package cz.cuni.mff.vojtusr.emf;

import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelFactory;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import java.io.IOException;
import java.util.Collections;


public class GenModelGenerate {
    public GenModel generate(String projectDir, String projectName) throws IOException {
        final String resourcesPath = projectDir + "/" + projectName + "/src/main/resources";
        // Initialize resource set
        ResourceSet resourceSet = new ResourceSetImpl();

        // Register resource set
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());

        // Load the Ecore model
        URI ecoreURI = URI.createFileURI(resourcesPath + "/ecore.ecore");// todo maybe change file name
        Resource ecoreResource = resourceSet.getResource(ecoreURI, true);
        EPackage ecorePackage = (EPackage) ecoreResource.getContents().getFirst();

        // Create a GenModel
        GenModel genModel = GenModelFactory.eINSTANCE.createGenModel();
        genModel.setModelName(projectName);
        genModel.setModelDirectory(projectName + "/java");
        genModel.setComplianceLevel(GenJDKLevel.JDK220_LITERAL);
        genModel.initialize(Collections.singleton(ecorePackage));

        // Save the GenModel
        URI genmodelURI = URI.createFileURI(resourcesPath + "/genmodel.genmodel");// todo maybe change file name
        final XMIResourceImpl genModelResource = new XMIResourceImpl(genmodelURI);
        genModelResource.getDefaultSaveOptions().put(XMIResource.OPTION_ENCODING, "UTF-8");
        genModelResource.getContents().add(genModel);
        genModelResource.save(Collections.EMPTY_MAP);

        System.out.println("Genmodel created successfully.");
        return genModel;
    }
}
