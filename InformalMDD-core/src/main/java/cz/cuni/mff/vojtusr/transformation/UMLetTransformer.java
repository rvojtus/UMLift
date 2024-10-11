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

    private void processRelations(List<Relation> relations, List<Class> classes) {
        for (Relation relation : relations) {
            Collection<PointDoubleIndexed> points = relation.getStickablePoints();
            if (points != null) {
                PointDoubleIndexed firstCoordPoit = null;
                PointDoubleIndexed secondCoordPoint = null;
                Iterator<PointDoubleIndexed> iterator = points.iterator();
                if (iterator.hasNext()) {
                    firstCoordPoit = iterator.next();
                }
                if (iterator.hasNext()) {
                    secondCoordPoint = iterator.next();
                }

                if (firstCoordPoit == null || secondCoordPoint == null) {
                    return;
                }

                int right = firstCoordPoit.getX().intValue();
                int down = firstCoordPoit.getY().intValue();
                int left = secondCoordPoint.getX().intValue();
                int up = secondCoordPoint.getY().intValue();

                int[] directions = new int[]{up, down, left, right};

                int originX = relation.getRectangle().getX() + baseAttributeValue;
                int originY = relation.getRectangle().getY() + baseAttributeValue;

                List<Point> startEndPoints = getStartEndPoints(directions, originX, originY);
                Point pointStart = startEndPoints.get(0);
                Point pointEnd = startEndPoints.get(1);

                //System.out.println(pointStart + " -> " + pointEnd);
                Optional<Class> startRelationClass = classes.stream().filter(item -> item.getRectangle().contains(pointStart)).findFirst();
                Optional<Class> endRelationClass = classes.stream().filter(item -> item.getRectangle().contains(pointEnd)).findFirst();

                if (startRelationClass.isPresent() && endRelationClass.isPresent()) {
                    addClassRelation(startRelationClass.get(), endRelationClass.get());
                }
            }
        }

    }

    private List<Point> getStartEndPoints(int[] directions, int originX, int originY) {
        int up = directions[0];
        int down = directions[1];
        int left = directions[2];
        int right = directions[3];
        Point pointStart;
        Point pointEnd;

        if (up == baseAttributeValue && down == baseAttributeValue) {
            if (left == baseAttributeValue) {
                pointStart = new Point(originX, originY);
                pointEnd = new Point(originX + right, originY);
            }
            else {
                pointStart = new Point(originX + right, originY);
                pointEnd = new Point(originX, originY);
            }
        }
        else if (up == baseAttributeValue && left == baseAttributeValue) {
            pointStart = new Point(originX, originY);
            pointEnd = new Point(originX + right, originY + down);
            //System.out.println("UP LEFT");
        }
        else if (up == baseAttributeValue && right == baseAttributeValue) {
            pointStart = new Point(originX + left, originY);
            pointEnd = new Point(originX, originY + down);
            //System.out.println("UP RIGHT");
        }
        else if (down == baseAttributeValue && left == baseAttributeValue) {
            pointStart = new Point(originX, originY + up);
            pointEnd = new Point(originX + right, originY);
            //System.out.println("DOWN LEFT");
        }
        else if (down == baseAttributeValue && right == baseAttributeValue) {
            pointStart = new Point(originX + left, originY + up);
            pointEnd = new Point(originX, originY);
            //System.out.println("DOWN RIGHT");
        }
        else {
            if (up == baseAttributeValue) {
                pointStart = new Point(originX, originY);
                pointEnd = new Point(originX, originY + down);
            }
            else {
                pointStart = new Point(originX, originY + up);
                pointEnd = new Point(originX, originY);
            }
        }
        return new ArrayList<>(Arrays.asList(pointStart, pointEnd));
    }

    private void addClassRelation(Class startRelationClass, Class endRelationClass) {
        String startClassName = startRelationClass.getPanelAttributes().trim().split("--")[0].trim();
        String endClassName = endRelationClass.getPanelAttributes().trim().split("--")[0].trim();

        EClass eClassStart = (EClass) ePackage.getEClassifier(startClassName);
        EClass eClassEnd = (EClass) ePackage.getEClassifier(endClassName);

        EReference endClassReference = EcoreFactory.eINSTANCE.createEReference();
        endClassReference.setName(endClassName);
        endClassReference.setEType(eClassEnd);
        endClassReference.setUpperBound(ETypedElement.UNBOUNDED_MULTIPLICITY);
        endClassReference.setContainment(true);

        eClassStart.getEStructuralFeatures().add(endClassReference);
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
