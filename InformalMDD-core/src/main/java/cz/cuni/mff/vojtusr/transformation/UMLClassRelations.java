package cz.cuni.mff.vojtusr.transformation;

/**
 * Enum representing different types of UML class relationships.
 */
public enum UMLClassRelations {
    /**
     * A directed association indicates a one-way relationship between two classes.
     */
    DIRECTED_ASSOCIATION,

    /**
     * An association represents a bidirectional relationship between two classes.
     */
    ASSOCIATION,

    /**
     * Inheritance indicates a relationship where one class derives from another.
     */
    INHERITANCE,

    /**
     * Realization represents the relationship between an interface and the class implementing it.
     */
    REALIZATION,

    /**
     * Dependency indicates that one class depends on another, often for a short period.
     */
    DEPENDENCY,

    /**
     * Aggregation represents a whole-part relationship where the part can exist independently of the whole.
     */
    AGGREGATION,

    /**
     * Composition represents a whole-part relationship where the part cannot exist independently of the whole.
     */
    COMPOSITION
}
