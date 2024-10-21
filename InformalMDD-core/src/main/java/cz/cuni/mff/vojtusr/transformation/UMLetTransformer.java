package cz.cuni.mff.vojtusr.transformation;

import com.baselet.control.basics.geom.Point;
import com.baselet.element.elementnew.uml.Class;
import com.baselet.element.elementnew.uml.Interface;
import com.baselet.element.interfaces.GridElement;
import com.baselet.element.relation.Relation;
import com.baselet.element.sticking.PointDoubleIndexed;
import com.baselet.gui.CurrentGui;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.impl.EReferenceImpl;
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
    }

    public void transform(String EcoreFilePath) {
        List<GridElement> elements = CurrentGui.getInstance().getGui().getCurrentDiagram().getGridElements();
        List<Relation> relations = new ArrayList<>();
        List<Class> classes = new ArrayList<>();
        for (GridElement element : elements) {
            if (element instanceof Class UMLClass) {
                classes.add(UMLClass);
                EClass umlClass = EcoreFactory.eINSTANCE.createEClass();
                String name = UMLClass.getPanelAttributes().trim().split("--")[0].trim();
                umlClass.setName(name);
                String classNameAttribute = UMLClass.getPanelAttributesAsList().getFirst().trim();
                if (classNameAttribute.matches("/.+/")) {
                    umlClass.setAbstract(true);
                }
                ePackage.getEClassifiers().add(umlClass);
            }
            else if (element instanceof Relation relation) {
                relations.add(relation);
            }
            else if (element instanceof Interface interfaceUML) {

            }
        }

        processRelations(relations, classes);

        saveEcoreModel(ePackage, EcoreFilePath);

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
            if (points != null) {
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
                    return;
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
        System.out.println("Relation Type: " + classRelation);
        String startClassName = startRelationClass.getPanelAttributes().trim().split("--")[0].trim();
        String endClassName = endRelationClass.getPanelAttributes().trim().split("--")[0].trim();

        EClass eClassStart = (EClass) ePackage.getEClassifier(startClassName);
        EClass eClassEnd = (EClass) ePackage.getEClassifier(endClassName);

        System.out.println("Start Class: " + startClassName + " End Class: "+ endClassName);

        final List<String> attributes = relation.getPanelAttributesAsList();

        switch (classRelation) {
            case INHERITANCE -> addInheritanceRelation(eClassEnd, eClassStart);
            case ASSOCIATION -> addAssociationRelation(eClassStart, eClassEnd, attributes);
            case DIRECTED_ASSOCIATION -> addDirectedAssociationRelation(eClassStart, eClassEnd, attributes);
            case REALIZATION -> addRealizationRelation(eClassStart, eClassEnd);
            case DEPENDENCY -> addDependencyRelation(eClassStart, eClassEnd);
            case AGGREGATION -> addAggregationRelation(eClassStart, eClassEnd);
            case COMPOSITION -> addCompositeRelation(eClassEnd, eClassStart, attributes);
            case null, default -> {
            }
        }
    }

    private void addInheritanceRelation(EClass parentClass, EClass childClass) {
        childClass.getESuperTypes().add(parentClass);
    }

    private void addAssociationRelation(EClass parentClass, EClass childClass, final List<String> attributes) {
        EReference eRef = EcoreFactory.eINSTANCE.createEReference();
        if (attributes.size() < 3) { // default implicit relation without specified cardinalities
            eRef.setEType(childClass);
            eRef.setLowerBound(0);
            eRef.setUpperBound(1);
            parentClass.getEStructuralFeatures().add(eRef);
            return;
        }
        final String cardinalityParent = attributes.get(1).trim().split("m1=")[1];
        final String cardinalityChild = attributes.get(2).trim().split("m2=")[1];

        final int cardinalityParent_0 = Integer.parseInt(cardinalityParent.split("..")[0]);
        final String cardinalityParent_1 = cardinalityParent.split("..")[1];
        final int cardinalityChild_0 = Integer.parseInt(cardinalityChild.split("..")[0]);
        final String cardinalityChild_1 = cardinalityChild.split("..")[1];
        // todo needs to be both ways and check if it is both ways
        //System.out.println("cardinalityParent = " + cardinalityParent + "; cardinalityChild = " + cardinalityChild);
    }

    private void addDirectedAssociationRelation(EClass parentClass, EClass childClass, List<String> attributes) {
        EReference eRef = EcoreFactory.eINSTANCE.createEReference();
        eRef.setEType(childClass);
        eRef.setName(childClass.getName());
        if (attributes.size() <= 1) {
            eRef.setEType(childClass);
            eRef.setLowerBound(0);
            eRef.setUpperBound(1);
            parentClass.getEStructuralFeatures().add(eRef);
            return;
        }
        final String cardinalityAttribute = attributes.get(1).trim();
        if (cardinalityAttribute.matches("^m2=[0-9]+\\.\\.[*n0-9]")) {
            processCardinality(eRef, cardinalityAttribute, "m2=", 1);
        }
        else {
            processCardinality(eRef, cardinalityAttribute, " ", 0);
        }
    }

    private void addRealizationRelation(EClass parentClass, EClass subClass) {}

    private void addDependencyRelation(EClass parentClass, EClass subClass) {}

    private void addAggregationRelation(EClass parentClass, EClass childClass) {
        EReference eRef = EcoreFactory.eINSTANCE.createEReference();
        eRef.setEType(childClass);
        eRef.setLowerBound(0);
        eRef.setUpperBound(ETypedElement.UNBOUNDED_MULTIPLICITY);
        eRef.setContainment(false);
        parentClass.getEStructuralFeatures().add(eRef);
    }

    private void addCompositeRelation(EClass parentClass, EClass childClass, final List<String> attributes) {
        EReference eRef = EcoreFactory.eINSTANCE.createEReference();
        eRef.setEType(childClass);
        eRef.setName(childClass.getName());
        eRef.setContainment(true);
        if (attributes.size() <= 1) { // default implicit relation without specified cardinalities
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
        Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl());
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource resource = resourceSet.createResource(URI.createURI(fileName));

        resource.getContents().add(ePackage);
        try {
            resource.save(new HashMap<>());
            System.out.println("Ecore model saved to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
