package cz.cuni.mff.umlift.core.transformation.processors;

import com.baselet.control.basics.geom.Point;
import com.baselet.element.elementnew.uml.Class;
import com.baselet.element.elementnew.uml.Package;
import com.baselet.element.relation.Relation;
import com.baselet.element.sticking.PointDoubleIndexed;
import cz.cuni.mff.umlift.core.config.GenerationConfig;
import cz.cuni.mff.umlift.core.transformation.UMLClassRelations;
import org.eclipse.emf.ecore.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class RelationProcessor {
    private static final Logger LOG = Logger.getLogger(RelationProcessor.class.getName());


    /**
     * Processes a single relation between classes by calculating the start and end points
     * and determining if they connect two classes. If so, adds the relation between them.
     * It also determines, to which packages the classes belong to.
     *
     * @param relation the relation to be processed
     * @param classes  the list of classes involved in the relations
     * @param packages list of packages present in the diagram
     */
    public void processRelation(Relation relation, final List<Class> classes, final List<Package> packages) {
        // Retrieve the points that define the relation's line (e.g., start and end points)
        Collection<PointDoubleIndexed> points = relation.getStickablePoints();

        // If there are no points, exit early as there’s nothing to process
        if (points == null) {
            LOG.warning("No stickable points found for relation: " + relation);
            return;
        }

        PointDoubleIndexed startCoordPoit = null;
        PointDoubleIndexed endCoordPoint = null;
        Iterator<PointDoubleIndexed> pointIterator = points.iterator();

        // Set the starting point if available
        if (pointIterator.hasNext()) {
            startCoordPoit = pointIterator.next();
        }

        // Set the end point if more points are available
        if (pointIterator.hasNext()) {
            endCoordPoint = getEndCoordinationPoint(pointIterator);
        }

        // If either start or end point is missing, exit as the relation can't be processed fully
        if (startCoordPoit == null || endCoordPoint == null) {
            LOG.warning("No points found for relation: " + relation);
            return;
        }

        // Calculate offsets for the start and end points relative to their coordinates
        int startOffsetX = startCoordPoit.getX().intValue();
        int startOffsetY = startCoordPoit.getY().intValue();
        int endOffsetX = endCoordPoint.getX().intValue();
        int endOffsetY = endCoordPoint.getY().intValue();

        // Get the origin of the relation's bounding rectangle
        int originX = relation.getRectangle().getX();
        int originY = relation.getRectangle().getY();

        // Calculate the absolute start and end points based on the offsets and origin
        Point pointStart = new Point(originX + startOffsetX, originY + startOffsetY);
        Point pointEnd = new Point(originX + endOffsetX, originY + endOffsetY);

        // Find which package contains the points
        Optional<Package> startRelationPackage = packages.stream()
                .filter(aPackage -> aPackage.getRectangle().contains(pointStart))
                .findFirst();

        Optional<Package> endRelationPackage = packages.stream()
                .filter(aPackage -> aPackage.getRectangle().contains(pointEnd))
                .findFirst();

        EPackage startEPackage;
        EPackage endEPackage;

        // If the class doesn't belong to any package, it belongs to the base/default package
        if (startRelationPackage.isEmpty()) {
            startEPackage = EPackage.Registry.INSTANCE.getEPackage(GenerationConfig.getInstance().getBaseEPackageURI());
        } else {
            Package startPackage = startRelationPackage.get();
            startEPackage = EPackage.Registry.INSTANCE.getEPackage(PackageProcessor.getPackageURI(startPackage));
        }
        if (endRelationPackage.isEmpty()) {
            endEPackage = EPackage.Registry.INSTANCE.getEPackage(GenerationConfig.getInstance().getBaseEPackageURI());
        } else {
            Package endPackage = endRelationPackage.get();
            endEPackage = EPackage.Registry.INSTANCE.getEPackage(PackageProcessor.getPackageURI(endPackage));
        }

        // Find the class that contains the start point, if any
        Optional<Class> startRelationClass = classes.stream()
                .filter(item -> item.getRectangle().contains(pointStart))
                .findFirst();

        // Find the class that contains the end point, if any
        Optional<Class> endRelationClass = classes.stream()
                .filter(item -> item.getRectangle().contains(pointEnd))
                .findFirst();

        // If both start and end classes are present, add the relation between them
        if (startRelationClass.isPresent() && endRelationClass.isPresent()) {
            addClassRelation(startEPackage, endEPackage, endRelationClass.get(), startRelationClass.get(), relation);
        }
    }

    /**
     * Retrieves the last point in a sequence of points from the given iterator.
     *
     * @param iter an iterator over PointDoubleIndexed objects
     * @return the last point in the iterator
     */
    private PointDoubleIndexed getEndCoordinationPoint(final Iterator<PointDoubleIndexed> iter) {
        PointDoubleIndexed lastPoint = iter.next();
        // Iterate through all points, updating lastPoint to the current point
        // This loop will leave lastPoint as the final point in the iterator
        while (iter.hasNext()) {
            lastPoint = iter.next();
        }
        // Return the last point in the sequence
        return lastPoint;
    }

    /**
     * Adds a relation between two classes in the Ecore model based on the UML class relation type.
     *
     * @param startRelationClass the starting class of the relation
     * @param endRelationClass   the ending class of the relation
     * @param relation           the relation defining the connection between the classes
     */
    private void addClassRelation(EPackage startEPackage, EPackage endEPackage, Class startRelationClass,
                                  Class endRelationClass,
                                  Relation relation) {
        // Determine the UML class relation type from the given relation
        UMLClassRelations classRelation = getClassRelationType(relation);

        // Get the names of the start and end classes from UMLet
        String startClassName = ClassProcessor.getUMLetClassName(startRelationClass);
        String endClassName = ClassProcessor.getUMLetClassName(endRelationClass);

        // Trim Abstract Class names
        if (startClassName.matches("/.+/")) {
            startClassName = startClassName.substring(1, startClassName.length() - 1);
        }
        if (endClassName.matches("/.+/")) {
            endClassName = endClassName.substring(1, endClassName.length() - 1);
        }

        // Check if the start class is an enum and process it accordingly; exit if handled
        if (processUMLetEnumRelation(startEPackage, startRelationClass, classRelation, startClassName, endClassName)) return;

        // Check if the end class is an enum and process it accordingly; exit if handled
        if (processUMLetEnumRelation(endEPackage, endRelationClass, classRelation, endClassName, startClassName)) return;

        // Retrieve the Ecore model's EClass objects for the start and end classes
        EClass eClassStart = (EClass) startEPackage.getEClassifier(startClassName);
        EClass eClassEnd = (EClass) endEPackage.getEClassifier(endClassName);

        // If either the start or end class is not found in the Ecore model, log an error and exit
        if (eClassStart == null) {
            LOG.severe("ERROR: Class " + startClassName + " not found");
            return;
        } else if (eClassEnd == null) {
            LOG.severe("ERROR: Class " + endClassName + " not found");
            return;
        }

        // Retrieve additional attributes from the relation, if any
        final List<String> attributes = relation.getPanelAttributesAsList();

        // Add the relation between the two classes in the Ecore model
        addEMFClassRelation(classRelation, eClassStart, eClassEnd, attributes);
    }

    /**
     * Processes an enumeration relationship for a UMLet class. If the start class is an enum and the relation is a
     * directed
     * association, it adds an attribute of the enum type to the end class.
     *
     * @param startRelationClass the class that is the start of the relation
     * @param classRelation      the type of relation between the classes
     * @param startClassName     the name of the starting class
     * @param endClassName       the name of the ending class
     * @return true if the relation is processed as an enum, false otherwise
     */
    private boolean processUMLetEnumRelation(EPackage ePackage, Class startRelationClass, UMLClassRelations classRelation, String startClassName
            , String endClassName) {
        // Check if the starting class is an enumeration
        if (ClassProcessor.isUMLetClassEnum(startRelationClass)) {
            // Retrieve the EEnum object for the starting class from the Ecore package
            EEnum eEnum = (EEnum) ePackage.getEClassifier(startClassName);

            // Ensure the relation type is a directed association; otherwise, log an error and exit
            if (classRelation != UMLClassRelations.DIRECTED_ASSOCIATION) {
                LOG.severe("Wrong type of relation between a Class " + endClassName + " and Enumeration" + startClassName);
                return false;
            }

            // Retrieve the EClass object for the end class
            EClass eClassEnd = (EClass) ePackage.getEClassifier(endClassName);

            // Create an attribute in the end class of the enum type
            EAttribute eAttribute = EcoreFactory.eINSTANCE.createEAttribute();
            eAttribute.setEType(eEnum);
            eAttribute.setName(startClassName);

            eClassEnd.getEStructuralFeatures().add(eAttribute);

            // Return true indicating that the enum relation was processed successfully
            return true;
        }
        // Return false if the start class is not an enum
        return false;
    }

    /**
     * Determines the type of UML class relation represented by a given relation object
     * based on its attributes.
     *
     * @param relation the relation whose type is to be determined
     * @return the UMLClassRelations type of the relation, or null if no match is found
     */
    private UMLClassRelations getClassRelationType(final Relation relation) {
        // Retrieve the list of attributes associated with the relation
        List<String> attributes = relation.getPanelAttributesAsList();

        // If there are no attributes, return null as the relation type cannot be determined
        if (attributes.isEmpty())
            return null;

        // Get the type string from the first attribute, which indicates the relation type
        String relationType = attributes.getFirst();
        final String typePrefix = "lt=";

        // A switch statement to match the relation type to predefined UMLClassRelations
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
            case typePrefix + "<<<<-" -> // aggregation - EMF equivalent - Reference
                    UMLClassRelations.AGGREGATION;
            case typePrefix + "<<<<<-" -> // composition, EMF equivalent - Composition
                    UMLClassRelations.COMPOSITION;
            default -> null; // return null if no relation type matches
        };
    }

    /**
     * Adds a relationship between two EClasses in the Ecore model based on the type of UML class relation.
     *
     * @param classRelation the type of relation to add (e.g., inheritance, association, etc.)
     * @param eClassStart   the starting EClass of the relation
     * @param eClassEnd     the ending EClass of the relation
     * @param attributes    additional attributes related to the relation (if any)
     */
    private void addEMFClassRelation(final UMLClassRelations classRelation, EClass eClassStart, EClass eClassEnd,
                                     final List<String> attributes) {
        // Switch based on the type of UML class relation
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

    /**
     * Establishes an inheritance relationship between two EClasses by setting the parent class as a supertype
     * of the child class.
     *
     * @param parentClass the EClass that serves as the parent (superclass)
     * @param childClass  the EClass that serves as the child (subclass)
     */
    private void addInheritanceRelation(EClass parentClass, EClass childClass) {
        // Add the parent class to the list of super types for the child class
        childClass.getESuperTypes().add(parentClass);
    }

    /**
     * Establishes a bidirectional association relationship between two EClasses by creating
     * EReferences for both directions and setting them as opposites.
     *
     * @param parentClass the EClass that represents one end of the association
     * @param childClass  the EClass that represents the other end of the association
     * @param attributes  additional attributes that may define the association's characteristics
     */
    private void addAssociationRelation(EClass parentClass, EClass childClass, final List<String> attributes) {
        // Create a directed association from the parent class to the child class
        EReference parentToChild = directedAssociationRelationHelper(childClass, attributes, "m1=");

        // Create a directed association from the child class to the parent class
        EReference childToParent = directedAssociationRelationHelper(parentClass, attributes, "m2=");

        // Set the opposite references for bidirectional association
        parentToChild.setEOpposite(childToParent);
        childToParent.setEOpposite(parentToChild);

        // Add the parent-to-child reference to the parent class's structural features
        parentClass.getEStructuralFeatures().add(parentToChild);

        // Add the child-to-parent reference to the child class's structural features
        childClass.getEStructuralFeatures().add(childToParent);
    }

    /**
     * Helper method to create an EReference representing a directed association
     * between two EClasses, with optional cardinality specifications from the attributes.
     *
     * @param childClass the EClass that represents the child end of the association
     * @param attributes a list of attributes that may define cardinality and other properties
     * @param delimiter  a string that helps identify cardinality attributes in the list
     * @return the created EReference with set properties
     */
    private EReference directedAssociationRelationHelper(EClass childClass, final List<String> attributes,
                                                         final String delimiter) {
        // Create a new EReference instance
        EReference eRef = EcoreFactory.eINSTANCE.createEReference();
        eRef.setEType(childClass);
        eRef.setName(childClass.getName());

        // Check if only one attribute is provided (indicating no cardinality specified)
        if (attributes.size() == 1) {
            eRef.setLowerBound(0); // Default lower bound is 0
            eRef.setUpperBound(1); // Default upper bound is 1 (optional reference)
            return eRef; // Return the reference with default cardinality
        }

        // Regular expression to match cardinality specifications
        final String cardinalityRegEx = "[0-9]+\\.\\.[*n0-9]$";
        final int attributeIndex = Integer.parseInt(String.valueOf(delimiter.charAt(1)));


        // Iterate through attributes to find cardinality specifications
        for (String attribute : attributes) {
            if (attribute.matches("^" + delimiter + cardinalityRegEx)) {
                processCardinality(eRef, attribute, delimiter);
            } else if (attribute.matches("^r" + attributeIndex + "=" + cardinalityRegEx)) {
                //processCardinality(eRef, attribute, "r" + attributeIndex + "=");
            }
        }
        return eRef; // Return the configured EReference
    }

    /**
     * Adds a directed association relationship from the parent class to the child class
     * by creating an EReference and adding it to the parent's structural features.
     *
     * @param parentClass the EClass that represents the parent end of the directed association
     * @param childClass  the EClass that represents the child end of the directed association
     * @param attributes  a list of attributes that may define properties of the association
     */
    private void addDirectedAssociationRelation(EClass parentClass, EClass childClass, List<String> attributes) {
        // Create the EReference for the directed association using the helper method
        EReference eRef = directedAssociationRelationHelper(childClass, attributes, "m1=");

        // Add the created EReference to the parent class's structural features
        parentClass.getEStructuralFeatures().add(eRef);
    }

    /**
     * Establishes a realization relationship between two EClasses by treating
     * it as an inheritance relationship, with the parent class being the interface
     * and the child class being the implementing class.
     *
     * @param parentClass the EClass representing the interface (parent)
     * @param childClass  the EClass representing the implementing class (child)
     */
    private void addRealizationRelation(EClass parentClass, EClass childClass) {
        // Add an inheritance relationship from the parent class (interface) to the child class (implementation)
        addInheritanceRelation(parentClass, childClass);
    }

    /**
     * Helper method to create an EReference representing an aggregation relationship
     * between a parent EClass and a child EClass. The relationship can specify
     * cardinalities and containment.
     *
     * @param parentClass the EClass representing the parent in the aggregation
     * @param childClass  the EClass representing the child in the aggregation
     * @param attributes  a list of attributes that may define cardinalities for the relationship
     * @param containment boolean indicating whether the relationship is a containment relationship
     */
    private void aggregationRelationHelper(EClass parentClass, EClass childClass, List<String> attributes,
                                           final boolean containment) {
        // Create a new EReference for the aggregation relationship
        EReference eRef = EcoreFactory.eINSTANCE.createEReference();
        eRef.setEType(childClass);
        eRef.setContainment(containment);
        eRef.setName(childClass.getName());

        // Check if only one attribute is provided, indicating a default implicit relation
        if (attributes.size() == 1) {
            eRef.setLowerBound(0);
            eRef.setUpperBound(ETypedElement.UNBOUNDED_MULTIPLICITY);
        } else {
            // Iterate through attributes to find cardinality specifications
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

    /**
     * Establishes an aggregation relationship between a parent EClass and a child EClass
     * by using the helper method for aggregation relations.
     *
     * @param parentClass the EClass that represents the parent in the aggregation relationship
     * @param childClass  the EClass that represents the child in the aggregation relationship
     * @param attributes  a list of attributes that may define properties of the aggregation
     */
    private void addAggregationRelation(EClass parentClass, EClass childClass, List<String> attributes) {
        aggregationRelationHelper(parentClass, childClass, attributes, false);
    }

    /**
     * Establishes a composite relationship between a parent EClass and a child EClass
     * by using the helper method for aggregation relations, with containment set to true.
     *
     * @param parentClass the EClass that represents the parent in the composite relationship
     * @param childClass  the EClass that represents the child in the composite relationship
     * @param attributes  a list of attributes that may define properties of the composite relation
     */
    private void addCompositeRelation(EClass parentClass, EClass childClass, final List<String> attributes) {
        aggregationRelationHelper(parentClass, childClass, attributes, true);
    }

    /**
     * Processes and sets the cardinality for an EReference based on the given attribute
     * that describes the cardinality in UMLet format.
     *
     * @param eRef                       the EReference to which the cardinality will be applied
     * @param cardinalityAttribute       the string attribute containing the cardinality information
     * @param UMLetCardinalityIdentifier the identifier used to parse the cardinality from the attribute
     */
    private void processCardinality(EReference eRef, final String cardinalityAttribute,
                                    final String UMLetCardinalityIdentifier) {
        // Split the cardinality attribute to extract the child cardinality
        final String cardinalityChild = cardinalityAttribute.split(UMLetCardinalityIdentifier)[1];

        // Parse the lower bound from the cardinality string
        final int cardinalityChild_0 = Integer.parseInt(cardinalityChild.split("\\.\\.")[0]);
        final String cardinalityChild_1 = cardinalityChild.split("\\.\\.")[1];

        // Set the lower bound for the EReference
        eRef.setLowerBound(cardinalityChild_0);

        // Check if the upper bound is specified as unbounded
        if (cardinalityChild_1.matches("[*nN]")) {
            // Set upper bound to unbounded
            eRef.setUpperBound(ETypedElement.UNBOUNDED_MULTIPLICITY);
        } else {
            try {
                // Parse the upper bound and set it
                final int cardChild1 = Integer.parseInt(cardinalityChild_1);
                eRef.setUpperBound(cardChild1);
            } catch (NumberFormatException e) {
                // If parsing fails, reset bounds to default
                eRef.setLowerBound(0);
                eRef.setUpperBound(ETypedElement.UNBOUNDED_MULTIPLICITY);
            }
        }
    }
}
