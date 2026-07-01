package cz.cuni.mff.umlift.core.transformation.processors;

import com.baselet.element.elementnew.uml.Class;
import org.eclipse.emf.ecore.*;

import java.util.List;

public class ClassProcessor {

    public static final String interfaceRegex = "^<<\\s*[Ii]+nterface\\s*>>$";
    public static final String enumRegex = "^<<\\s*[Ee]num(eration)?\\s*>>$";

    /**
     * Processes a UML class element by creating a corresponding Ecore model element and adding it to the package.
     *
     * @param UMLClass the UML class element to process
     */
    public void processUMLClassElement(EPackage ePackage, Class UMLClass) {
        // Create a new EClass in the Ecore model to represent the UML class
        EClass eClass = EcoreFactory.eINSTANCE.createEClass();

        // Get the name of the UML class and set it in the EClass
        String name = getUMLetClassName(UMLClass);
        eClass.setName(name);

        // Check if the class is an interface by matching its attributes against the interface regex
        if (UMLClass.getPanelAttributesAsList().getFirst().matches(interfaceRegex)) {
            // Mark the EClass as an interface and abstract if it matches the interface pattern
            eClass.setInterface(true);
            eClass.setAbstract(true);
            ePackage.getEClassifiers().add(eClass);
        }
        // Check if the class is an enumeration by matching its attributes against the enum regex
        else if (UMLClass.getPanelAttributesAsList().getFirst().matches(enumRegex)) {
            EEnum eEnum = EcoreFactory.eINSTANCE.createEEnum();
            eEnum.setName(name);
            populateEEnum(UMLClass, eEnum);
            ePackage.getEClassifiers().add(eEnum);
        } else { // Process as a standard (non-interface, non-enum) class
            // Trim the class name attribute and check if it is abstract (indicated by enclosing slashes)
            String classNameAttribute = UMLClass.getPanelAttributesAsList().getFirst().trim();
            if (classNameAttribute.matches("/.+/")) {
                eClass.setAbstract(true);
                eClass.setName(name.substring(1, name.length() - 1));
            }

            // Parse and add all specified attributes and fields
            parseAttributes(UMLClass, eClass);
            ePackage.getEClassifiers().add(eClass);
        }
    }

    /**
     * Retrieves the name of the given UMLet class based on its attributes.
     *
     * @param umletClazz the UMLet class from which to get the name
     * @return the name of the class or "DefaultName" if it is an enum or interface with one attribute,
     * or an empty string if there are no attributes
     */
    public static String getUMLetClassName(Class umletClazz) {
        // Retrieve the list of panel attributes for the UMLet class
        List<String> panelAttributes = umletClazz.getPanelAttributesAsList();

        if (panelAttributes.isEmpty())
            return "";

        // Check if the class is either an enum or an interface
        if (isUMLetClassEnum(umletClazz) || isUMLetClassInterface(umletClazz)) {
            // If there's only one attribute, return a default name
            if (panelAttributes.size() == 1)
                return "DefaultName";
            // Otherwise, return the second attribute (after trimming whitespace)
            return panelAttributes.get(1).trim();
        }

        // For standard classes, return the first attribute (after trimming whitespace)
        return panelAttributes.getFirst().trim();
    }

    /**
     * Determines if the given UMLet class represents an enumeration based on its attributes.
     *
     * @param clazz the UMLet class to check
     * @return true if the class is an enumeration, false otherwise
     */
    public static boolean isUMLetClassEnum(Class clazz) {
        // Check if the first attribute of the class matches the enum pattern
        return clazz.getPanelAttributesAsList().getFirst().matches(enumRegex);
    }

    /**
     * Determines if the given UMLet class represents an interface based on its attributes.
     *
     * @param clazz the UMLet class to check
     * @return true if the class is an interface, false otherwise
     */
    public static boolean isUMLetClassInterface(Class clazz) {
        // Check if the first attribute of the class matches the interface pattern
        return clazz.getPanelAttributesAsList().getFirst().matches(interfaceRegex);
    }

    /**
     * Used to parse, extract, and add attributes from a UMLet Class to Ecore EClass
     *
     * @param umletClazz UMLet Class to parse
     * @param eClass     EClass to populate
     */
    private void parseAttributes(Class umletClazz, EClass eClass) {
        // Retrieve the list of panel attributes for the UMLet class
        List<String> panelAttributes = umletClazz.getPanelAttributesAsList();

        if (panelAttributes.isEmpty())
            return;

        for (String attr : panelAttributes) {
            if (attr.equals("--")) // panel_attributes delimiter, we skip this
                continue;
            if (attr.startsWith("-")) {
                addAttributeToEClass(eClass, attr);
            }
        }
    }

    /**
     * Used to parse, extract, and add items from a UMLet Class Stereotype Enumeration to Ecore EEnum
     *
     * @param umletClazz UMLet Class to parse
     * @param eNum       EEnum to populate
     */
    private void populateEEnum(Class umletClazz, EEnum eNum) {
        // Retrieve the list of panel attributes for the UMLet class
        List<String> panelAttributes = umletClazz.getPanelAttributesAsList();

        final int umletEnumHeaderSize = 3;

        if (panelAttributes.isEmpty() || panelAttributes.size() <= umletEnumHeaderSize)
            return;

        for (int i = umletEnumHeaderSize; i < panelAttributes.size(); i++) {
            String attr = panelAttributes.get(i).trim();
            if (attr.startsWith("-"))
                attr = attr.substring(1).trim();
            EEnumLiteral eEnumLiteral = EcoreFactory.eINSTANCE.createEEnumLiteral();
            eEnumLiteral.setName(attr);
            eEnumLiteral.setValue(i - umletEnumHeaderSize);
            eEnumLiteral.setLiteral(attr);
            eNum.getELiterals().add(eEnumLiteral);
        }
    }

    /**
     * Parses an attribute, decides its data type, and adds it to the EClass attributes
     *
     * @param eClass EClass to receive the attribute
     * @param attr   Attribute taken from UMLet Class panel attributes
     */
    private void addAttributeToEClass(EClass eClass, String attr) {
        attr = attr.trim().substring(1); // remove the "-" prefix for the attribute name
        EDataType dataType = EcorePackage.eINSTANCE.getEString();
        EAttribute eAttribute = EcoreFactory.eINSTANCE.createEAttribute();
        String name;
        if (attr.contains(":")) { // default, not specified data type -> EString
            String[] split = attr.split(":");
            if (split.length != 2)
                return;

            dataType = switch (split[1].trim()) {
                case "int", "integer", "Integer":
                    yield EcorePackage.eINSTANCE.getEInt();
                case "boolean", "Boolean":
                    yield EcorePackage.eINSTANCE.getEBoolean();
                case "float", "Float":
                    yield EcorePackage.eINSTANCE.getEFloat();
                case "double", "Double":
                    yield EcorePackage.eINSTANCE.getEDouble();
                case "date", "Date":
                    yield EcorePackage.eINSTANCE.getEDate();
                default:
                    yield EcorePackage.eINSTANCE.getEString();
            };
            name = split[0].trim();
        } else
            name = attr.trim();


        eAttribute.setEType(dataType);
        eAttribute.setName(name);
        eClass.getEStructuralFeatures().add(eAttribute);
    }
}
