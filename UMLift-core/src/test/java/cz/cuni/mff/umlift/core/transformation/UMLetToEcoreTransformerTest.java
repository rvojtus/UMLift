package cz.cuni.mff.umlift.core.transformation;

import cz.cuni.mff.umlift.core.config.GenerationConfig;
import cz.cuni.mff.umlift.core.util.HelperUtil;
import org.eclipse.emf.ecore.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static cz.cuni.mff.umlift.core.TestConstants.*;

/**
 * Tests functionality of {@link UMLetToEcoreTransformer}.
 */
public class UMLetToEcoreTransformerTest {
    private UMLetToEcoreTransformer transformer;

    @BeforeEach
    public void setUp() {
        GenerationConfig.getInstance()
                .setProjectName(projectName)
                .setProjectNsPrefix(projectNsPrefix)
                .setProjectNsURI(projectNsUri);
        transformer = new UMLetToEcoreTransformer();
    }

    static Stream<Path> provideUxfFiles() throws Exception {
        Path testDir = Paths.get(UMLetFilesDir);
        return Files.walk(testDir)
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().endsWith(".uxf"));
    }

    @ParameterizedTest(name = "UMLet file {0} should be transformed and resulting Ecore saved on disk")
    @MethodSource("provideUxfFiles")
    void givenUxfFile_whenTransformAndSave_thenEcoreSaved(Path inputFile) throws Exception {
        final File uxfFile = inputFile.toFile();

        // Validate UMLet file exists
        assertTrue(uxfFile.exists(), "Tested file should exist: " + uxfFile.getAbsolutePath());

        // Run the transformation
        transformer.transform(uxfFile.toPath());

        EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(GenerationConfig.getInstance().getBaseEPackageURI());

        // Validate Ecore package is not null
        validateEPackageExists(ePackage);

        // Validate the name of the package
        assertFalse(ePackage.getName().isEmpty(), "Generated EPackage should have a non-empty name");

        // Resulting Ecore file
        Path tmpOutputEcoreFile = Files.createTempFile("test-uxf-", ".ecore");
        HelperUtil.saveEcoreModel(ePackage, tmpOutputEcoreFile);

        // Validate Ecore package is successfully saved on disk
        assertTrue(tmpOutputEcoreFile.toFile().exists(),
                "Result Ecore file should exist: " + tmpOutputEcoreFile.toFile().getAbsolutePath());

        Files.deleteIfExists(tmpOutputEcoreFile);
    }

    /**
     * Tests UMLet Diagram Class Elements: Class, Abstract Class, Interface etc.
     */
    @Nested
    class UMLetDiagramElementsValidation {
        @Test
        void givenSingleClass_whenTransform_thenEcoreValid() throws Exception {
            final File uxfFile = new File(diagramElementsResources + "singleClass.uxf");
            final String expectedClassName = "SimpleClass";

            // Run the transformation and validate
            EPackage ePackage = validateAndTransform(uxfFile);

            // Validate Ecore package contains EClassifier of type EClass with name 'SimpleClass'
            validateClassExists(ePackage, expectedClassName);
        }

        @Test
        void givenFewClassesNoRelations_whenTransform_thenEcoreValid() throws Exception {
            final File uxfFile = new File(diagramElementsResources + "fewClassNoRelations.uxf");
            final String expectedClassName = "SimpleClass";
            final int expectedNumberOfClasses = 5;

            // Run the transformation and validate
            EPackage ePackage = validateAndTransform(uxfFile);

            // Validate Ecore package contains all expected EClasses
            for (int i = 0; i < expectedNumberOfClasses; i++) {
                // Validate Ecore package contains EClassifier of type EClass with name 'SimpleClass_#'
                validateClassExists(ePackage, expectedClassName + "_" + i);
            }
        }

        @Test
        void givenManyClassesNoRelations_whenTransform_thenEcoreValid() throws Exception {
            final File uxfFile = new File(diagramElementsResources + "manyClassNoRelations.uxf");
            final String expectedClassName = "SimpleClass";
            final int expectedNumberOfClasses = 50;

            // Run the transformation and validate
            EPackage ePackage = validateAndTransform(uxfFile);

            // Validate Ecore package contains all expected EClasses
            for (int i = 0; i < expectedNumberOfClasses; i++) {
                // Validate Ecore package contains EClassifier of type EClass with name 'SimpleClass_#'
                validateClassExists(ePackage, expectedClassName + "_" + i);
            }
        }

        @Test
        void givenThousandClassesNoRelations_whenTransform_thenEcoreValid() throws Exception {
            final File uxfFile = new File(diagramElementsResources + "thousandClassNoRelations.uxf");
            final String expectedClassName = "SimpleClass";
            final int expectedNumberOfClasses = 1000;

            // Run the transformation and validate
            EPackage ePackage = validateAndTransform(uxfFile);

            // Validate Ecore package contains all expected EClasses
            for (int i = 0; i < expectedNumberOfClasses; i++) {
                // Validate Ecore package contains EClassifier of type EClass with name 'SimpleClass_#'
                validateClassExists(ePackage, expectedClassName + "_" + i);
            }
        }

        @Test
        void givenNoClassesNoRelations_whenTransform_thenEcoreValid() throws Exception {
            final File uxfFile = new File(diagramElementsResources + "empty.uxf");

            // Run the transformation and validate
            EPackage ePackage = validateAndTransform(uxfFile);

            // Empty Diagram should equal to empty, but valid EPackage
            assertTrue(ePackage.getEClassifiers().isEmpty(), "EPackage should be empty");
        }

        @Test
        void givenAbstractClassNoRelations_whenTransform_thenEcoreValid() throws Exception {
            final File uxfFile = new File(diagramElementsResources + "abstractClass.uxf");

            // Run the transformation and validate
            EPackage ePackage = validateAndTransform(uxfFile);

            validateAbstractClassExists(ePackage, "AbstractClass");
        }

        @Test
        void givenInterfaceNoRelations_whenTransform_thenEcoreValid() throws Exception {
            final File uxfFile = new File(diagramElementsResources + "interface.uxf");

            // Run the transformation and validate
            EPackage ePackage = validateAndTransform(uxfFile);

            validateInterfaceClassExists(ePackage, "InterfaceName");
        }

        @Test
        void givenEnumerationNoRelations_whenTransform_thenEcoreValid() throws Exception {
            final File uxfFile = new File(diagramElementsResources + "enumeration.uxf");

            // Run the transformation and validate
            EPackage ePackage = validateAndTransform(uxfFile);

            validateEnumerationClassExists(ePackage, "EnumName");
        }
    }

    @Nested
    class UMLetDiagramAttributesValidation {
        @Test
        void givenMultipleClassWithAttributes_whenTransform_thenEcoreValid() throws Exception {
            final File uxfFile = new File("src/test/resources/UMLetFiles/SimpleClinicSystemExample.uxf");

            // Run the transformation and validate
            EPackage ePackage = validateAndTransform(uxfFile);

            validateClassExists(ePackage, "User");
            EClass userClass = (EClass) ePackage.getEClassifier("User");

            validateAttributeExists(userClass, "id");
            validateAttributeExists(userClass, "name");
            validateAttributeExists(userClass, "email");
        }


        void validateAttributeExists(EClass eClass, String attributeName) throws Exception {
            assertTrue(hasEAttribute(eClass, attributeName));
        }

        boolean hasEAttribute(EClass eClass, String attributeName) throws Exception {
            for (EStructuralFeature feature : eClass.getEStructuralFeatures()) {
                if (feature instanceof EAttribute && feature.getName().equals(attributeName)) {
                    return true;
                }
            }
            return false;
        }

    }

    /**
     * Tests UMLet Diagram Relationship Elements: inheritance, realisation, relation etc.
     *
     * @see UMLClassRelations for types of supported Relations
     */
    @Nested
    class UMLetDiagramRelationshipsValidation {
        @Test
        void givenTwoClassesSingleReference_whenTransform_thenEcoreValid() throws Exception {
            final File uxfFile = new File(relationshipElementsResources + "simpleReference.uxf");

            // Run the transformation and validate
            EPackage ePackage = validateAndTransform(uxfFile);

            // Validate existence of the 2 Classes
            validateClassExists(ePackage, "SimpleClass_0");
            validateClassExists(ePackage, "SimpleClass_1");

            EClass SimpleClass_0 = (EClass) ePackage.getEClassifier("SimpleClass_0");
            EClass SimpleClass_1 = (EClass) ePackage.getEClassifier("SimpleClass_1");

            // Validate Reference between the 2 Classes
            // Relation visualization: SimpleClass_1 -[0..n]-> SimpleClass_0
            assertFalse(SimpleClass_1.getEReferences().isEmpty(), "SimpleClass_1 references should not be empty");
            assertTrue(SimpleClass_0.getEReferences().isEmpty(), "SimpleClass_0 should not have references");

            // Validate Reference Type
            assertEquals(SimpleClass_1.getEReferences().getFirst().getEReferenceType(), SimpleClass_0, "SimpleClass_1" +
                    " should have reference to SimpleClass_0");

            // Validate Cardinality
            assertEquals(0, SimpleClass_1.getEReferences().getFirst().getLowerBound());
            assertEquals(ETypedElement.UNBOUNDED_MULTIPLICITY,
                    SimpleClass_1.getEReferences().getFirst().getUpperBound());
        }

        @Test
        void givenTwoClassesSingleBiDirectionalReference_whenTransform_thenEcoreValid() throws Exception {
            final File uxfFile = new File(relationshipElementsResources + "simpleBiDirectionalReference.uxf");

            // Run the transformation and validate
            EPackage ePackage = validateAndTransform(uxfFile);

            // Validate existence of the 2 Classes
            validateClassExists(ePackage, "SimpleClass_0");
            validateClassExists(ePackage, "SimpleClass_1");

            EClass SimpleClass_0 = (EClass) ePackage.getEClassifier("SimpleClass_0");
            EClass SimpleClass_1 = (EClass) ePackage.getEClassifier("SimpleClass_1");

            /*
             Validate Reference between the 2 Classes
             Relation visualization: SimpleClass_1 -[0..n]-> SimpleClass_0
                                     SimpleClass_0 -[0..1]-> SimpleClass_1
             */
            assertFalse(SimpleClass_1.getEReferences().isEmpty(), "SimpleClass_1 references should not be empty");
            assertFalse(SimpleClass_0.getEReferences().isEmpty(), "SimpleClass_0 references should not be empty");

            // Validate Reference Type
            assertEquals(SimpleClass_1.getEReferences().getFirst().getEReferenceType(), SimpleClass_0, "SimpleClass_1" +
                    " should have reference to SimpleClass_0");
            assertEquals(SimpleClass_0.getEReferences().getFirst().getEReferenceType(), SimpleClass_1, "SimpleClass_0" +
                    " should have reference to SimpleClass_1");

            /* WIP
            // Validate eOpposite Type
            assertTrue(SimpleClass_1.getEReferences().getFirst().getEOpposite().getEReferenceType().equals
            (SimpleClass_0),
                    "SimpleClass_1 should have eOpposite to SimpleClass_0");
            assertTrue(SimpleClass_0.getEReferences().getFirst().getEOpposite().getEReferenceType().equals
            (SimpleClass_1),
                    "SimpleClass_0 should have eOpposite to SimpleClass_1");
             */

            // Validate Cardinality SimpleClass_0
            assertEquals(0, SimpleClass_0.getEReferences().getFirst().getLowerBound(), "SimpleClass_0 should have " +
                    "lower bound == 0");
            assertEquals(1, SimpleClass_0.getEReferences().getFirst().getUpperBound(), "SimpleClass_0 should have " +
                    "upper bound == 1");

            // Validate Cardinality SimpleClass_1
            assertEquals(0, SimpleClass_1.getEReferences().getFirst().getLowerBound(), "SimpleClass_1 should have " +
                    "lower bound == 0");
            assertEquals(ETypedElement.UNBOUNDED_MULTIPLICITY,
                    SimpleClass_1.getEReferences().getFirst().getUpperBound(), "SimpleClass_1 should have upper bound" +
                            " == -1");
        }

        @Test
        void givenTwoClassesSingleSuperType_whenTransform_thenEcoreValid() throws Exception {
            final File uxfFile = new File(relationshipElementsResources + "simpleSuperType.uxf");

            // Run the transformation and validate
            EPackage ePackage = validateAndTransform(uxfFile);

            // Validate existence of the 2 Classes
            validateClassExists(ePackage, "SimpleClass_0");
            validateClassExists(ePackage, "SimpleClass_1");

            EClass SimpleClass_0 = (EClass) ePackage.getEClassifier("SimpleClass_0");
            EClass SimpleClass_1 = (EClass) ePackage.getEClassifier("SimpleClass_1");

            // Validate SuperType (Inheritance) between the 2 Classes
            // Relation visualization: SimpleClass_0 <-[SuperType]- SimpleClass_1
            assertTrue(SimpleClass_0.getESuperTypes().isEmpty(), "SimpleClass_0 SuperTypes should be empty");
            assertFalse(SimpleClass_1.getESuperTypes().isEmpty(), "SimpleClass_1 SuperTypes should not be empty");

            assertEquals(SimpleClass_1.getESuperTypes().getFirst(), SimpleClass_0);
        }

        @Test
        void givenTwoClassesSingleRealisation_whenTransform_thenEcoreValid() throws Exception {
            final File uxfFile = new File(relationshipElementsResources + "simpleRealisation.uxf");

            // Run the transformation and validate
            EPackage ePackage = validateAndTransform(uxfFile);

            // Validate existence of the 2 Classes
            validateClassExists(ePackage, "SimpleClass_0");
            validateClassExists(ePackage, "SimpleClass_1");

            EClass SimpleClass_0 = (EClass) ePackage.getEClassifier("SimpleClass_0");
            EClass SimpleClass_1 = (EClass) ePackage.getEClassifier("SimpleClass_1");

            // Validate Realisation between the 2 Classes
            // Relation visualization: SimpleClass_0 <-[Realisation]- SimpleClass_1
            assertTrue(SimpleClass_0.getESuperTypes().isEmpty(), "SimpleClass_0 SuperTypes should be empty");
            assertFalse(SimpleClass_1.getESuperTypes().isEmpty(), "SimpleClass_1 SuperTypes should not be empty");

            assertEquals(SimpleClass_1.getESuperTypes().getFirst(), SimpleClass_0);
        }

        @Test
        void givenTwoClassesSingleComposition_whenTransform_thenEcoreValid() throws Exception {
            final File uxfFile = new File(relationshipElementsResources + "simpleComposition.uxf");

            // Run the transformation and validate
            EPackage ePackage = validateAndTransform(uxfFile);

            // Validate existence of the 2 Classes
            validateClassExists(ePackage, "SimpleClass_0");
            validateClassExists(ePackage, "SimpleClass_1");

            EClass SimpleClass_0 = (EClass) ePackage.getEClassifier("SimpleClass_0");
            EClass SimpleClass_1 = (EClass) ePackage.getEClassifier("SimpleClass_1");

            // Validate Composition between the 2 Classes
            // Relation visualization: SimpleClass_0 <>-[1..1]- SimpleClass_1
            assertFalse(SimpleClass_0.getEReferences().isEmpty(), "SimpleClass_0 references should not be empty");
            assertTrue(SimpleClass_1.getEReferences().isEmpty(), "SimpleClass_1 should not have references");

            assertTrue(SimpleClass_0.getEReferences().getFirst().isContainment(), "SimpleClass_0 should contain " +
                    "containment");
        }

        @Test
        void givenFewClassesFewRelations_whenTransform_thenEcoreValid() throws Exception {
            final File uxfFile = new File(relationshipElementsResources + "fewRelations.uxf");
            final String expectedClassName = "SimpleClass";
            final int expectedNumberOfClasses = 5;

            // Run the transformation and validate
            EPackage ePackage = validateAndTransform(uxfFile);

            // Validate Ecore package contains all expected EClasses
            for (int i = 0; i < expectedNumberOfClasses; i++) {
                // Validate Ecore package contains EClassifier of type EClass with name 'SimpleClass_#'
                validateClassExists(ePackage, expectedClassName + "_" + i);
            }

            // Validate Relations between Classes
            // Visualization of Relations: SimpleClass_i --> SimpleClass_i++
            for (int i = 0; i < expectedNumberOfClasses - 1; i++) {
                validateRelationExists(ePackage, expectedClassName + "_" + i, expectedClassName + "_" + (i + 1));
            }
        }

        @Test
        void givenManyClassesManyRelations_whenTransform_thenEcoreValid() throws Exception {
            final File uxfFile = new File(relationshipElementsResources + "manyRelations.uxf");
        }

        @Test
        void givenAbstractClassSuperTypeClass_whenTransform_thenEcoreValid() throws Exception {
            final File uxfFile = new File(relationshipElementsResources + "abstractClassSuperType.uxf");

            // Run the transformation and validate
            EPackage ePackage = validateAndTransform(uxfFile);

            // Validate existence of Classes
            validateClassExists(ePackage, "SimpleClass");
            validateAbstractClassExists(ePackage, "AbstractClass");

            EClass simpleClass = (EClass) ePackage.getEClassifier("SimpleClass");
            EClass abstractClass = (EClass) ePackage.getEClassifier("AbstractClass");

            // Validate SuperType (Inheritance) relation
            assertEquals(simpleClass.getESuperTypes().getFirst(), abstractClass, "AbstractClass should be a SuperType" +
                    " of SimpleClass");
        }

        @Test
        void givenInterfaceClassRealisation_whenTransform_thenEcoreValid() throws Exception {
            final File uxfFile = new File(relationshipElementsResources + "interfaceClassRealisation.uxf");

            // Run the transformation and validate
            EPackage ePackage = validateAndTransform(uxfFile);

            // Validate existence of Classes
            validateClassExists(ePackage, "SimpleClass");
            validateInterfaceClassExists(ePackage, "InterfaceName");

            EClass simpleClass = (EClass) ePackage.getEClassifier("SimpleClass");
            EClass interfaceClass = (EClass) ePackage.getEClassifier("InterfaceName");

            // Validate SuperType (Realisation) relation
            assertEquals(simpleClass.getESuperTypes().getFirst(), interfaceClass, "InterfaceClass should be a " +
                    "SuperType of SimpleClass");
        }
    }


    private void validateEPackageExists(EPackage ePackage) {
        assertNotNull(ePackage, "Generated EPackage should not be null");
    }

    /**
     * Tests that the provided {@link EPackage} contains an {@link EClassifier} of name {@code className}.
     *
     * @param ePackage  Ecore model to check
     * @param className name of the expected {@link EClassifier}
     */
    private void validateClassExists(EPackage ePackage, final String className) {
        assertNotNull(ePackage.getEClassifier(className), "EPackage should contain class: " + className);
    }

    /**
     * Tests that the provided {@link EPackage} contains an {@link EClassifier} of name {@code className},
     * and checks if the abstract modifier is {@code true}.
     *
     * @param ePackage  Ecore model to check
     * @param className name of the expected {@link EClassifier}
     */
    private void validateAbstractClassExists(EPackage ePackage, final String className) {
        EClass eClass = (EClass) ePackage.getEClassifier(className);
        assertNotNull(eClass, "EPackage should contain class: " + className);
        assertTrue(eClass.isAbstract(), "EClass should be abstract: " + eClass);
    }

    private void validateInterfaceClassExists(EPackage ePackage, final String className) {
        EClass eClass = (EClass) ePackage.getEClassifier(className);
        assertNotNull(eClass, "EPackage should contain class: " + className);
        assertTrue(eClass.isInterface(), "EClass should be interface: " + eClass);
    }

    private void validateEnumerationClassExists(EPackage ePackage, final String enumName) {
        EEnum eEnum = (EEnum) ePackage.getEClassifier(enumName);
        assertNotNull(eEnum, "EPackage should contain enumeration: " + enumName);
    }

    /**
     * Tests that there exists an {@link EReference} between two classes
     *
     * @param ePackage    Ecore model to check
     * @param className_0 first class name
     * @param className_1 second class name
     */
    private void validateRelationExists(EPackage ePackage, final String className_0, final String className_1) {
        final EClass eClass_0 = (EClass) ePackage.getEClassifier(className_0);
        final EClass eClass_1 = (EClass) ePackage.getEClassifier(className_1);

        assertNotNull(eClass_0, "EClass_0 should not be null. ClassName: " + className_0);
        assertNotNull(eClass_1, "EClass_1 should not be null. ClassName: " + className_1);

        assertFalse(eClass_0.getEReferences().isEmpty(), "EClass_0 references should not be empty");

        assertEquals(eClass_0.getEReferences().getFirst().getEReferenceType(), eClass_1, "EClass_0 should contain " +
                "reference");
    }

    /**
     * Tests existence of provided UMLet File, runs {@link UMLetToEcoreTransformer#transform(Path)},
     * and validates the resulting {@link EPackage}.
     *
     * @param uxfFile UMLet File to transform
     * @return an Ecore model: {@link EPackage}
     */
    private EPackage validateAndTransform(File uxfFile) {
        // Validate UMLet file exists
        assertTrue(uxfFile.exists(), "Tested file should exist: " + uxfFile.getAbsolutePath());

        // Run the transformation
        transformer.transform(uxfFile.toPath());

        EPackage ePackage = EPackage.Registry.INSTANCE.getEPackage(GenerationConfig.getInstance().getBaseEPackageURI());

        // Validate Ecore package is not null
        validateEPackageExists(ePackage);

        return ePackage;
    }
}
