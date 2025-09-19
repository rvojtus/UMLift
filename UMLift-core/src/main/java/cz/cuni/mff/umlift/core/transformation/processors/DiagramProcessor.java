package cz.cuni.mff.umlift.core.transformation.processors;

import com.baselet.diagram.DiagramHandler;
import com.baselet.element.elementnew.uml.Class;
import com.baselet.element.elementnew.uml.Package;
import com.baselet.element.interfaces.GridElement;
import com.baselet.element.relation.Relation;

import java.util.ArrayList;
import java.util.List;

public class DiagramProcessor {

    public void processDiagram(DiagramHandler diagram) throws Exception {
        final List<GridElement> elements = diagram.getDrawPanel().getGridElements();

        List<Package> packages = new ArrayList<>();
        List<Class> classes = new ArrayList<>();
        List<Relation> relations = new ArrayList<>();

        for (GridElement element : elements) {
            if (element instanceof Package packageElement) {
                packages.add(packageElement);
            } else if (element instanceof Class classElement) {
                classes.add(classElement);
            } else if (element instanceof Relation relationElement) {
                relations.add(relationElement);
            }
        }

        boolean[] classesVisited = new boolean[classes.size()];

        // Process packages
        PackageProcessor packageProcessor = new PackageProcessor();
        packages.forEach(aPackage -> packageProcessor.processUMLPackage(aPackage, classes, classesVisited));

        // Process classes not in any packages
        packageProcessor.processBasePackage(classes,  classesVisited);

        // Process relations between classes, including cross-package relations
        RelationProcessor relationProcessor = new RelationProcessor();
        relations.forEach(relation -> relationProcessor.processRelation(relation, classes, packages));

    }
}
