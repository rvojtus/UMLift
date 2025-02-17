package cz.cuni.mff.vojtusr.emf;

import org.eclipse.emf.common.util.Diagnostic;
import org.junit.jupiter.api.AfterEach;
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

import static cz.cuni.mff.vojtusr.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;


public class JavaGeneratorTest {
    private JavaGenerator generator;

    private Path tmpDir;

    @BeforeEach
    void setUp() throws IOException {
        generator = new JavaGenerator();
        tmpDir = Files.createTempDirectory("testing");
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
    }

    static Stream<Path> provideUxfFiles() throws Exception {
        Path testDir = Paths.get(UMLetFilesDir);
        return Files.walk(testDir)
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().endsWith(".uxf"))
                .filter(path -> !path.getFileName().toString().equals("empty.uxf")); // skip empty Diagram
    }

    boolean generatedJavaFilesPresent() throws IOException {
        return Files.walk(tmpDir)
                .filter(Files::isRegularFile)
                .anyMatch(path -> path.toString().endsWith(".java"));
    }

    @ParameterizedTest(name = "{index} - Generating code for UXF: {0}")
    @MethodSource("provideUxfFiles")
    void givenUMLetFile_whenGenerateCodeFromUMLetFile_thenCorrectlyGeneratedArtefacts(Path inputFile) throws Exception {
        final File uxfFile = inputFile.toFile();

        // Validate UMLet file exists
        assertTrue(uxfFile.exists(), "Tested file should exist: " + uxfFile.getAbsolutePath());

        PrintStream originalStream = System.out;
        // Silence STDOUT, to not see output of Code Generation's EMF Generator
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));

        // Run code generation
        Diagnostic diagnostic = generator.generateCodeFromUMLetFile(uxfFile.getAbsolutePath(), tmpDir.toString(), projectName);

        // Restore default PrintStream
        System.setOut(originalStream);

        assertNotNull(diagnostic, "Diagnostic should not be null");
        assertEquals(Diagnostic.OK, diagnostic.getSeverity(), "Diagnostic should be OK");

        // Assert Java Files have been generated
        assertTrue(generatedJavaFilesPresent(), "Generated Java files should be present in directory: " + tmpDir.toString());
    }

}
