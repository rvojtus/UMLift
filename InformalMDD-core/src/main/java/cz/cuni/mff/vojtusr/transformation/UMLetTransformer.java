package cz.cuni.mff.vojtusr.transformation;

import com.baselet.control.basics.geom.Point;
import com.baselet.element.elementnew.uml.Class;
import com.baselet.element.interfaces.GridElement;
import com.baselet.element.relation.Relation;
import com.baselet.element.sticking.PointDoubleIndexed;
import com.baselet.gui.CurrentGui;
import cz.cuni.mff.vojtusr.emf.JavaGenerator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import java.io.IOException;
import java.util.*;

public class UMLetTransformer {
    private final EPackage ePackage;
    public UMLetTransformer(String projectName) {
        ePackage = EcoreFactory.eINSTANCE.createEPackage();
        ePackage.setName(projectName);
        ePackage.setNsPrefix(projectName);
        ePackage.setNsURI("https://wwww." +projectName);
    }

    public void transform(String EcoreFilePath) {
        List<GridElement> elements = CurrentGui.getInstance().getGui().getCurrentDiagram().getGridElements();
        List<Relation> relations = new ArrayList<>();
        List<Class> classes = new ArrayList<>();
        for (GridElement element : elements) {
            if (element instanceof Class UMLClass) {
                processUMLClassElement(UMLClass, classes);
            }
            else if (element instanceof Relation relation) {
                relations.add(relation);
            }
        }

        processRelations(relations, classes);

        saveEcoreModel(ePackage, EcoreFilePath);
    }

    private void processUMLClassElement(Class UMLClass, List<Class> classes) {
        classes.add(UMLClass);
        EClass umlClass = EcoreFactory.eINSTANCE.createEClass();
        String name = UMLClass.getPanelAttributes().trim().split("--")[0].trim();
        if (UMLClass.getPanelAttributesAsList().getFirst().matches("^<<\\s*[Ii]+nterface\\s*>>$")) { // Interface matching
            String interfaceName = "InterfaceName";
            if (UMLClass.getPanelAttributesAsList().size() > 1)
                interfaceName = UMLClass.getPanelAttributesAsList().get(1).trim();
            umlClass.setInterface(true);
            umlClass.setAbstract(true);
            umlClass.setName(interfaceName);
        }
        else { // Standard Class
            umlClass.setName(name);
            String classNameAttribute = UMLClass.getPanelAttributesAsList().getFirst().trim();
            if (classNameAttribute.matches("/.+/")) {
                umlClass.setAbstract(true);
            }
        }
        ePackage.getEClassifiers().add(umlClass);
    }

    private PointDoubleIndexed getEndCoordinationPoint(final Iterator<PointDoubleIndexed> iter) {
        PointDoubleIndexed lastPoint = iter.next();
        while (iter.hasNext()) {
            lastPoint = iter.next();
        }
        return lastPoint;
    }

    private void processRelations(List<Relation> relations, List<Class> classes) {
        for (Relation relation : relations) {
            Collection<PointDoubleIndexed> points = relation.getStickablePoints();
            if (points == null)
                continue;

            PointDoubleIndexed startCoordPoit = null;
            PointDoubleIndexed endCoordPoint = null;
            Iterator<PointDoubleIndexed> iterator = points.iterator();
            if (iterator.hasNext()) {
                startCoordPoit = iterator.next();
            }
            if (iterator.hasNext()) {
                endCoordPoint = getEndCoordinationPoint(iterator);
            }

            if (startCoordPoit == null || endCoordPoint == null) {
                continue;
            }

            int startOffsetX = startCoordPoit.getX().intValue();
            int startOffsetY = startCoordPoit.getY().intValue();
            int endOffsetX = endCoordPoint.getX().intValue();
            int endOffsetY = endCoordPoint.getY().intValue();

            int originX = relation.getRectangle().getX();
            int originY = relation.getRectangle().getY();

            Point pointStart = new Point(originX + startOffsetX, originY + startOffsetY);
            Point pointEnd = new Point(originX + endOffsetX, originY + endOffsetY);

            Optional<Class> startRelationClass = classes.stream().filter(item -> item.getRectangle().contains(pointStart)).findFirst();
            Optional<Class> endRelationClass = classes.stream().filter(item -> item.getRectangle().contains(pointEnd)).findFirst();

            if (startRelationClass.isPresent() && endRelationClass.isPresent()) {
                addClassRelation(endRelationClass.get(), startRelationClass.get(), relation);
            }
        }

    }

    private UMLClassRelations getClassRelationType(Relation relation) {
        List<String> attributes = relation.getPanelAttributesAsList();
        if (attributes.isEmpty()) {
            return null;
        }
        String relationType = attributes.getFirst();
        final String typePrefix = "lt=";
        return switch (relationType.trim()) {
            case typePrefix + "<<-" -> // inheritance, EMF equivalent - SuperType
                    UMLClassRelations.INHERITANCE;
            case typePrefix + "-" -> // association, EMF equivalent - Bi-directional Reference
                    UMLClassRelations.ASSOCIATION;
            case typePrefix + "<-" -> // EMF equivalent - Reference
                    UMLClassRelations.DIRECTED_ASSOCIATION;
            case typePrefix + "<<." -> // realization
                    UMLClassRelations.REALIZATION;
            case typePrefix + "<." -> // dependency
                    UMLClassRelations.DEPENDENCY;
            case typePrefix + "<<<<-" -> // aggregation
                    UMLClassRelations.AGGREGATION;
            case typePrefix + "<<<<<-" -> // composition, EMF equivalent - Composition
                    UMLClassRelations.COMPOSITION;
            default -> null;
        };
    }

    private void addClassRelation(Class startRelationClass, Class endRelationClass, Relation relation) {
        UMLClassRelations classRelation = getClassRelationType(relation);
        String startClassName = startRelationClass.getPanelAttributes().trim().split("--")[0].trim();
        String endClassName = endRelationClass.getPanelAttributes().trim().split("--")[0].trim();

        EClass eClassStart = (EClass) ePackage.getEClassifier(startClassName);
        EClass eClassEnd = (EClass) ePackage.getEClassifier(endClassName);

        final List<String> attributes = relation.getPanelAttributesAsList();

        switch (classRelation) {
            case INHERITANCE -> addInheritanceRelation(eClassEnd, eClassStart);
            case ASSOCIATION -> addAssociationRelation(eClassStart, eClassEnd, attributes);
            case DIRECTED_ASSOCIATION -> addDirectedAssociationRelation(eClassStart, eClassEnd, attributes);
            case REALIZATION -> addRealizationRelation(eClassEnd, eClassStart);
            case AGGREGATION -> addAggregationRelation(eClassEnd, eClassStart, attributes);
            case COMPOSITION -> addCompositeRelation(eClassEnd, eClassStart, attributes);
            case null, default -> {
            }
        }
    }

    private void addInheritanceRelation(EClass parentClass, EClass childClass) {
        childClass.getESuperTypes().add(parentClass);
    }

    private void addAssociationRelation(EClass parentClass, EClass childClass, final List<String> attributes) {
        EReference parentToChild = directedAssociationRelationHelper(parentClass, childClass, attributes, "m1=", 1);
        EReference childToParent = directedAssociationRelationHelper(childClass, parentClass, attributes, "m2=", 2);

        parentToChild.setEOpposite(childToParent);
        childToParent.setEOpposite(parentToChild);

        parentClass.getEStructuralFeatures().add(parentToChild);
        childClass.getEStructuralFeatures().add(childToParent);
    }

    private EReference directedAssociationRelationHelper(EClass parentClass, EClass childClass, List<String> attributes, final String delimiter, final int attributeIndex) {
        EReference eRef = EcoreFactory.eINSTANCE.createEReference();
        eRef.setEType(childClass);
        eRef.setName(childClass.getName());
        if (attributes.size() == 1) { // no cardinality specified
            eRef.setEType(childClass);
            eRef.setLowerBound(0);
            eRef.setUpperBound(1);
            return eRef;
        }
        final String cardinalityAttribute = attributes.get(attributeIndex).trim();
        if (cardinalityAttribute.matches("^"+delimiter+"[0-9]+\\.\\.[*n0-9]")) {
            processCardinality(eRef, cardinalityAttribute, delimiter, 1);
        }
        else {
            processCardinality(eRef, cardinalityAttribute, " ", 0);
        }
        return eRef;
    }

    private void addDirectedAssociationRelation(EClass parentClass, EClass childClass, List<String> attributes) {
        EReference eRef = directedAssociationRelationHelper(parentClass, childClass, attributes, "m1=", 1);
        parentClass.getEStructuralFeatures().add(eRef);
    }

    private void addRealizationRelation(EClass parentClass, EClass childClass) {

    }

    private void aggregationRelationHelper(EClass parentClass, EClass childClass, List<String> attributes, final boolean containment) {
        EReference eRef = EcoreFactory.eINSTANCE.createEReference();
        eRef.setEType(childClass);
        eRef.setName(childClass.getName());
        eRef.setContainment(containment);
        if (attributes.size() == 1) { // default implicit relation without specified cardinalities
            eRef.setLowerBound(0);
            eRef.setUpperBound(ETypedElement.UNBOUNDED_MULTIPLICITY);
        }
        else {
            final String cardinalityAttribute = attributes.get(1).trim();
            if (cardinalityAttribute.matches("^m2=[0-9]+\\.\\.[*n0-9]")) {
                processCardinality(eRef, cardinalityAttribute, "m2=", 1);
            }
            else {
                processCardinality(eRef, cardinalityAttribute, " ", 0);
            }
        }
        parentClass.getEStructuralFeatures().add(eRef);
    }

    private void addAggregationRelation(EClass parentClass, EClass childClass, List<String> attributes) {
        aggregationRelationHelper(parentClass, childClass, attributes, false);
    }

    private void addCompositeRelation(EClass parentClass, EClass childClass, final List<String> attributes) {
        aggregationRelationHelper(parentClass, childClass, attributes, true);
    }

    private void processCardinality(EReference eRef, final String cardinalityAttribute, final String UMLetCardinalityIdentifier, final int valuePos) {
        final String cardinalityChild = cardinalityAttribute.split(UMLetCardinalityIdentifier)[valuePos];
        final int cardinalityChild_0 = Integer.parseInt(cardinalityChild.split("\\.\\.")[0]);
        final String cardinalityChild_1 = cardinalityChild.split("\\.\\.")[1];
        eRef.setLowerBound(cardinalityChild_0);
        if (cardinalityChild_1.matches("[*nN]")) {
            eRef.setUpperBound(ETypedElement.UNBOUNDED_MULTIPLICITY);
        }
        else {
            try {
                final int cardChild1 = Integer.parseInt(cardinalityChild_1);
                eRef.setUpperBound(cardChild1);
            } catch (NumberFormatException e) {
                eRef.setLowerBound(0);
                eRef.setUpperBound(ETypedElement.UNBOUNDED_MULTIPLICITY);
            }
        }
    }

    private static void saveEcoreModel(EPackage ePackage, String fileName) {
        Resource.Factory.Registry.INSTANCE.getProtocolToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource resource = resourceSet.createResource(URI.createURI(fileName));

        resource.getContents().add(ePackage);
        try {
            resource.save(Collections.EMPTY_MAP);
            System.out.println("Ecore model saved to " + fileName);
        } catch (IOException e) {
            System.out.println("Error saving Ecore model to " + fileName);
        }
    }
}
