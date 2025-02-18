package cz.cuni.mff.vojtusr.emf;

import cz.cuni.mff.vojtusr.transformation.UMLetToEcoreTransformer;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EPackage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static cz.cuni.mff.vojtusr.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

public class GenModelGeneratorTest {
    private GenModelGenerator genModelGenerator;
    private static Path tmpDir;

    @BeforeAll
    static void setUpBeforeAll() throws IOException {
        tmpDir = Files.createTempDirectory("testing");
    }

    @BeforeEach
    void setUpBeforeEach() {
        genModelGenerator = new GenModelGenerator();
    }


    @AfterAll
    static void tearDownAfterAll() throws IOException {
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

    static Stream<Path> provideEcoreFiles() throws Exception {
        Stream<Path> uxfFiles = provideUxfFiles();

        UMLetToEcoreTransformer transformer = new UMLetToEcoreTransformer.EcoreConfigBuilder().build();

        uxfFiles.forEach(path -> {
            EPackage ePackage = transformer.transform(path.toAbsolutePath().toString());
            if (ePackage != null) {
                try {
                    Path tmpEcore = Files.createTempFile(tmpDir, path.getFileName().toString(), ".ecore");
                    transformer.saveEcore(tmpEcore.toAbsolutePath().toString());
                } catch (IOException ignored) {
                }
            }
        });

        return Files.walk(tmpDir)
                .filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().endsWith(".ecore"));
    }

    @ParameterizedTest(name = "{index} - Generating GenModel for Ecore: {0}")
    @MethodSource("provideEcoreFiles")
    void givenEcoreFile_whenGenerateGenModel_thenGenModelValid(Path ecoreFile) throws Exception {
        final File file = ecoreFile.toFile();

        // Validate existence of Ecore file
        assertTrue(file.exists(), "Ecore: " + file.getAbsolutePath() + " does not exist");

        GenModel genModel = genModelGenerator.generateGenModelFromEcore(file);

        assertNotNull(genModel, "GenModel should not be null");

        Diagnostic diagnostic = genModel.diagnose();

        // Validate GenModel
        assertNotNull(diagnostic, "Diagnostic should not be null");
        assertEquals(Diagnostic.OK, diagnostic.getSeverity(), "Diagnostic should be OK");

        // Validate saving of GenModel
        assertTrue(validateSaveGenModel(genModel), "Saving of GenModel failed: " + diagnostic);
    }

    boolean validateSaveGenModel(GenModel genModel) {
        try {
            Path genModelPathToSaveTo = Files.createTempFile(tmpDir, "testGenModel", ".genmodel");
            assertNotNull(genModelPathToSaveTo, "GenModelPathToSaveTo should not be null");

            File genModelFile = genModelGenerator.saveGenModel(genModel, genModelPathToSaveTo);

            assertNotNull(genModelFile, "GenModelFile should not be null");
            assertTrue(genModelFile.exists(), "GenModel file does not exist: " + genModelFile.getAbsolutePath());
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
