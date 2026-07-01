package cz.cuni.mff.umlift.core.emf;

import cz.cuni.mff.umlift.core.config.GenerationConfig;
import cz.cuni.mff.umlift.core.util.HelperUtil;
import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;
import org.eclipse.emf.common.util.Diagnostic;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static cz.cuni.mff.umlift.core.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests functionality of {@link ModelToCodeGenerator}.
 */
public class ModelToCodeGeneratorTest {
    private static Path tmpDir;
    private static ModelToCodeGenerator generator;

    @BeforeAll
    static void setup() {
        generator = new ModelToCodeGenerator();
        GenerationConfig.getInstance()
                .setProjectName(projectName)
                .setProjectNsPrefix(projectNsPrefix)
                .setProjectNsURI(projectNsUri)
                .setGenJDKLevel(GenJDKLevel.JDK210_LITERAL);
    }

    @BeforeEach
    void setUpBeforeEach() throws IOException {
        tmpDir = Files.createTempDirectory("modelToCodeGeneratorTestDirectory");
        GenerationConfig.getInstance()
                .setGeneratedFilesDir(tmpDir)
                .setEcoreGenModelDir(Path.of(tmpDir.toString() + "/models"));
    }

    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(tmpDir)) {
            Files.walk(tmpDir)
                    .sorted((p1, p2) -> -p1.compareTo(p2))
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (IOException ignored) {
                        }
                    });
            Files.deleteIfExists(tmpDir);
        }
        HelperUtil.removeRegisteredProjectPackages();
    }

    static Stream<Path> provideUxfFiles() throws Exception {
        Path testDir = Paths.get(UMLetFilesDir);
        return Files.walk(testDir)
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().endsWith(".uxf"))
                .filter(path -> !path.getFileName().toString().equals("empty.uxf")); // skip empty Diagram
    }

    @ParameterizedTest(name = "{index} - Generating code for UXF: {0}")
    @MethodSource("provideUxfFiles")
    void givenUMLetFile_whenGenerateCodeFromUMLetFile_thenCorrectlyGeneratedArtefacts(Path inputFile) throws Exception {
        final File uxfFile = inputFile.toFile();

        // Validate UMLet file exists
        assertTrue(uxfFile.exists(), "Tested file should exist: " + uxfFile.getAbsolutePath());

        GenerationConfig.getInstance().setProjectName(uxfFile.getName().replace(".uxf", ""));

        PrintStream originalStream = System.out;
        // Silence STDOUT, to not see output of Code Generation's EMF Generator
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));

        // Run code generation
        Diagnostic diagnostic = generator.generateCodeFromUMLetFile(inputFile);

        // Restore default PrintStream
        System.setOut(originalStream);

        assertNotNull(diagnostic, "Diagnostic should not be null");
        assertEquals(Diagnostic.OK, diagnostic.getSeverity(), "Diagnostic should be OK");

        // Assert Java Files have been generated
        assertTrue(generatedJavaFilesPresent(),
                "Generated Java files should be present in directory: " + tmpDir.toString());
    }

    /**
     * Tests that the code generation - {@link ModelToCodeGenerator#generateCodeFromUMLetFile(Path)}
     * has successfully generated expected Java files in the expected directory {@link #tmpDir}
     *
     * @return {@code true} if at least 1 Java file has been found, {@code false} if no Java files are present in the
     * expected directory
     * @throws IOException if any IO error occurs, i.e. {@link #tmpDir} is invalid
     */
    boolean generatedJavaFilesPresent() throws IOException {
        return Files.walk(tmpDir)
                .filter(Files::isRegularFile)
                .anyMatch(path -> path.toString().endsWith(".java"));
    }

}
