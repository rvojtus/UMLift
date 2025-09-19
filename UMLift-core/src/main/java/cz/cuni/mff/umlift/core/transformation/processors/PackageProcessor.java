package cz.cuni.mff.umlift.core.transformation.processors;

import com.baselet.element.elementnew.uml.Package;
import com.baselet.element.elementnew.uml.Class;
import cz.cuni.mff.umlift.core.config.GenerationConfig;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class PackageProcessor {

    public PackageProcessor() {
        initBaseEPackage();
    }

    public void processUMLPackage(Package umlPackage, final List<Class> classes, boolean[] visitedClasses) {
        EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
        setUpPackage(umlPackage, ePackage);

        // Process all classes that belong to the package
        ClassProcessor classProcessor = new ClassProcessor();
        AtomicInteger counter = new AtomicInteger();
        classes.forEach(UMLetClass -> {
            if (umlPackage.getRectangle().contains(UMLetClass.getRectangle())) {
                classProcessor.processUMLClassElement(ePackage, UMLetClass);
                visitedClasses[counter.get()] = true;
            }
            counter.incrementAndGet();
        });

        EPackage.Registry.INSTANCE.put(ePackage.getNsURI(), ePackage);
    }

    public void initBaseEPackage() {
        EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
        setUpPackage(ePackage, GenerationConfig.getInstance().getProjectName(), GenerationConfig.getInstance().getBaseEPackageURI());
        EPackage.Registry.INSTANCE.put(ePackage.getNsURI(), ePackage);
    }

    public void processBasePackage(final List<Class> classes, final boolean[] visitedClasses) {
        EPackage baseEPackage = EPackage.Registry.INSTANCE.getEPackage(GenerationConfig.getInstance().getBaseEPackageURI());
        ClassProcessor classProcessor = new ClassProcessor();
        IntStream.range(0, visitedClasses.length).filter(i -> !visitedClasses[i]).forEachOrdered(i -> classProcessor.processUMLClassElement(baseEPackage, classes.get(i)));
    }

    public static String getPackageName(Package umlPackage) {
        final List<String> panelAttributes = umlPackage.getPanelAttributesAsList();

        if (panelAttributes.isEmpty()) {
            return "";
        }

        return panelAttributes.getFirst().trim();
    }

    public static String getPackageURI(Package umlPackage) {
        return GenerationConfig.getInstance().getProjectNsURI() + "/" + getPackageName(umlPackage);
    }

    private void setUpPackage(Package umlPackage, EPackage ePackage) {
        final String packageName = getPackageName(umlPackage).toLowerCase();

        ePackage.setName(packageName);
        ePackage.setNsPrefix(packageName);
        ePackage.setNsURI(GenerationConfig.getInstance().getProjectNsURI() + "/" + packageName);
    }

    private void setUpPackage(EPackage ePackage, String packageName, String packageURI) {
        ePackage.setName(packageName);
        ePackage.setNsPrefix(packageName);
        ePackage.setNsURI(packageURI);
    }

}
