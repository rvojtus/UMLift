package cz.cuni.mff.vojtusr.transformation;

import com.baselet.control.basics.geom.Point;
import com.baselet.element.elementnew.uml.Class;
import com.baselet.element.interfaces.GridElement;
import com.baselet.element.relation.Relation;
import com.baselet.element.sticking.PointDoubleIndexed;
import com.baselet.gui.CurrentGui;
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

    private final String interfaceRegex = "^<<\\s*[Ii]+nterface\\s*>>$";
    private final String enumRegex = "^<<\\s*[Ee]num(eration)?\\s*>>$";

    private void processUMLClassElement(Class UMLClass, List<Class> classes) {
        classes.add(UMLClass);
        EClass umlClass = EcoreFactory.eINSTANCE.createEClass();
        String name = getUMLetClassName(UMLClass);
        umlClass.setName(name);
        if (UMLClass.getPanelAttributesAsList().getFirst().matches(interfaceRegex)) { // Interface matching
            umlClass.setInterface(true);
            umlClass.setAbstract(true);
            ePackage.getEClassifiers().add(umlClass);
        } else if (UMLClass.getPanelAttributesAsList().getFirst().matches(enumRegex)) {
            EEnum eEnum = EcoreFactory.eINSTANCE.createEEnum();
            eEnum.setName(name);
            ePackage.getEClassifiers().add(eEnum);
        } else { // Standard Class
            String classNameAttribute = UMLClass.getPanelAttributesAsList().getFirst().trim();
            if (classNameAttribute.matches("/.+/")) {
                umlClass.setAbstract(true);
            }
            ePackage.getEClassifiers().add(umlClass);
        }
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
        String startClassName = getUMLetClassName(startRelationClass);
        String endClassName = getUMLetClassName(endRelationClass);

        if (processUMLetEnum(startRelationClass, classRelation, startClassName, endClassName)) return;
        if (processUMLetEnum(endRelationClass, classRelation, endClassName, startClassName)) return;

        EClass eClassStart = (EClass) ePackage.getEClassifier(startClassName);
        EClass eClassEnd = (EClass) ePackage.getEClassifier(endClassName);

        if (eClassStart == null) {
            System.err.println("ERROR: Class " + startClassName + " not found");
            return;
        }
        else if (eClassEnd == null) {
            System.err.println("ERROR: Class " + endClassName + " not found");
            return;
        }

        final List<String> attributes = relation.getPanelAttributesAsList();

        addEMFClassRelation(classRelation, eClassStart, eClassEnd, attributes);
    }

    private boolean processUMLetEnum(Class startRelationClass, UMLClassRelations classRelation, String startClassName, String endClassName) {
        if (isUMLetClassEnum(startRelationClass)) {
            EEnum eEnum = (EEnum) ePackage.getEClassifier(startClassName);
            if (classRelation != UMLClassRelations.DIRECTED_ASSOCIATION) {
                System.err.println("Wrong type of relation between a Class " + endClassName + " and Enumeration" + startClassName + "!");
                return false;
            }
            EClass eClassEnd = (EClass) ePackage.getEClassifier(endClassName);

            EAttribute eAttribute = EcoreFactory.eINSTANCE.createEAttribute();
            eAttribute.setEType(eEnum);
            eAttribute.setName(startClassName);

            eClassEnd.getEStructuralFeatures().add(eAttribute);
            return true;
        }
        return false;
    }

    private boolean isUMLetClassInterface(Class clazz) {
        return clazz.getPanelAttributesAsList().getFirst().matches(interfaceRegex);
    }

    private boolean isUMLetClassEnum(Class clazz) {
        return clazz.getPanelAttributesAsList().getFirst().matches(enumRegex);
    }

    private String getUMLetClassName(Class clazz) {
        List<String> panelAttributes = clazz.getPanelAttributesAsList();
        if (panelAttributes.isEmpty())
            return "";
        if (isUMLetClassEnum(clazz) ||
            isUMLetClassInterface(clazz)) {
            if (panelAttributes.size() == 1)
                return "DefaultName";
            return panelAttributes.get(1).trim();
        }
        return panelAttributes.getFirst().trim();
    }

    private void addEMFClassRelation(final UMLClassRelations classRelation, EClass eClassStart, EClass eClassEnd, final List<String> attributes) {
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
        EReference parentToChild = directedAssociationRelationHelper(parentClass, childClass, attributes, "m1=");
        EReference childToParent = directedAssociationRelationHelper(childClass, parentClass, attributes, "m2=");

        parentToChild.setEOpposite(childToParent);
        childToParent.setEOpposite(parentToChild);

        parentClass.getEStructuralFeatures().add(parentToChild);
        childClass.getEStructuralFeatures().add(childToParent);
    }

    private EReference directedAssociationRelationHelper(EClass parentClass, EClass childClass, final List<String> attributes, final String delimiter) { // todo rework
        EReference eRef = EcoreFactory.eINSTANCE.createEReference();
        eRef.setEType(childClass);
        eRef.setName(childClass.getName());
        if (attributes.size() == 1) { // no cardinality specified
            eRef.setEType(childClass);
            eRef.setLowerBound(0);
            eRef.setUpperBound(1);
            return eRef;
        }
        final String cardinalityRegEx = "[0-9]+\\.\\.[*n0-9]$";
        final int attributeIndex = Integer.parseInt(String.valueOf(delimiter.charAt(1)));
        for (String attribute : attributes) {
            if (attribute.matches("^" + delimiter + cardinalityRegEx)) {
                processCardinality(eRef, attribute, delimiter);
            } else if (attribute.matches("^r" + attributeIndex + "=" + cardinalityRegEx)) {
                processCardinality(eRef, attribute, "r"+attributeIndex+"=");
            }
        }
        return eRef;
    }

    private void addDirectedAssociationRelation(EClass parentClass, EClass childClass, List<String> attributes) {
        EReference eRef = directedAssociationRelationHelper(parentClass, childClass, attributes, "m1=");
        parentClass.getEStructuralFeatures().add(eRef);
    }

    private void addRealizationRelation(EClass parentClass, EClass childClass) {
        addInheritanceRelation(parentClass, childClass);
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
            for (String attribute : attributes) {
                if (attribute.matches("^m2=[0-9]+\\.\\.[*n0-9]$")) {
                    processCardinality(eRef, attribute, "m2=");
                } else if (attribute.matches("^r2=[0-9]+\\.\\.[*n0-9]$")) {
                    processCardinality(eRef, attribute, "r2=");
                }
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

    private void processCardinality(EReference eRef, final String cardinalityAttribute, final String UMLetCardinalityIdentifier) {
        final String cardinalityChild = cardinalityAttribute.split(UMLetCardinalityIdentifier)[1];
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
