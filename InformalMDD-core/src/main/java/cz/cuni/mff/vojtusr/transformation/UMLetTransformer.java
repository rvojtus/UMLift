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
    private EPackage ePackage;
    private final int baseAttributeValue = 10;
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
                ePackage.getEClassifiers().add(umlClass);
            }
            else if (element instanceof Relation relation) {
                relations.add(relation);
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
                UMLClassRelations classRelation = getClassRelationType(relation);
                if (startRelationClass.isPresent() && endRelationClass.isPresent()) {
                    addClassRelation(startRelationClass.get(), endRelationClass.get(), classRelation);
                }

                System.out.println(classRelation);
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
            case typePrefix + "<<-" -> // inheritance
                    UMLClassRelations.INHERITANCE;
            case typePrefix + "-" -> // association
                    UMLClassRelations.ASSOCIATION;
            case typePrefix + "<<." -> // realization
                    UMLClassRelations.REALIZATION;
            case typePrefix + "<." -> // dependency
                    UMLClassRelations.DEPENDENCY;
            case typePrefix + "<<<<-" -> // aggregation
                    UMLClassRelations.AGGREGATION;
            case typePrefix + "<<<<<-" -> // composition
                    UMLClassRelations.COMPOSITION;
            default -> null;
        };
    }

    private void addClassRelation(Class startRelationClass, Class endRelationClass, UMLClassRelations classRelation) {
        String startClassName = startRelationClass.getPanelAttributes().trim().split("--")[0].trim();
        String endClassName = endRelationClass.getPanelAttributes().trim().split("--")[0].trim();

        EClass eClassStart = (EClass) ePackage.getEClassifier(startClassName);
        EClass eClassEnd = (EClass) ePackage.getEClassifier(endClassName);

        switch (classRelation) {
            case INHERITANCE:
                addInheritanceRelation(eClassStart, eClassEnd);
                return;
            case ASSOCIATION:
                break;
            case REALIZATION:
                break;
            case DEPENDENCY:
                break;
            case AGGREGATION:
                break;
            case COMPOSITION:
                break;
            case null, default:
                break;
        }

//        EReference endClassReference = EcoreFactory.eINSTANCE.createEReference();
//        endClassReference.setName(endClassName);
//        endClassReference.setEType(eClassEnd);
//        endClassReference.setUpperBound(ETypedElement.UNBOUNDED_MULTIPLICITY);
//        endClassReference.setContainment(true);
//
//        eClassStart.getEStructuralFeatures().add(endClassReference);
    }

    private void addInheritanceRelation(EClass parentClass, EClass subClass) {
        subClass.getESuperTypes().add(parentClass);
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
