package cz.cuni.mff.umlift.core.emf;

import cz.cuni.mff.umlift.core.config.GenerationConfig;
import cz.cuni.mff.umlift.core.transformation.UMLetToEcoreTransformer;
import cz.cuni.mff.umlift.core.util.HelperUtil;
import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.common.util.Diagnostic;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static cz.cuni.mff.umlift.core.TestConstants.*;
import static cz.cuni.mff.umlift.core.util.HelperUtil.saveGenModel;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests functionality of {@link GenModelGenerator}.
 */
public class GenModelGeneratorTest {
    private GenModelGenerator genModelGenerator;
    private static UMLetToEcoreTransformer transformer;
    private static Path tmpDir;
    private static final GenerationConfig config = GenerationConfig.getInstance();

    @BeforeAll
    static void setup(){
        transformer = new UMLetToEcoreTransformer();
        config.setGenJDKLevel(GenJDKLevel.JDK210_LITERAL)
                .setProjectName(projectName)
                .setProjectNsPrefix(projectNsPrefix)
                .setProjectNsURI(projectNsUri);
    }

    @BeforeEach
    void setUpBeforeEach() throws IOException{
        genModelGenerator = new GenModelGenerator();
        tmpDir = Files.createTempDirectory("genModelGeneratorTestDirectory");
        config.setGeneratedFilesDir(tmpDir)
                .setEcoreGenModelDir(Path.of(tmpDir.toString() + "/models"));
    }

    @AfterEach
    void tearDownAfterEach() throws IOException {
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

    @ParameterizedTest(name = "{index} - Generating GenModel for UXF: {0}")
    @MethodSource("provideUxfFiles")
    void givenUMLetFile_whenGenerateGenModel_thenGenModelValid(Path uxfFile) throws Exception {
        final File file = uxfFile.toFile();

        // Validate existence of UXF file
        assertTrue(file.exists(), "UXF: " + file.getAbsolutePath() + " does not exist");

        config.setProjectName(uxfFile.getFileName().toString().replace(".uxf", ""));

        transformer.transform(file.toPath());
        HelperUtil.saveRegisteredEcoreModels(config.getEcoreGenModelDir());

        GenModel genModel = genModelGenerator.generateGenModelFromEcore(config.getEcoreGenModelDir());

        assertNotNull(genModel, "GenModel should not be null");

        Diagnostic diagnostic = genModel.diagnose();

        // Validate GenModel
        assertNotNull(diagnostic, "Diagnostic should not be null");
        assertEquals(Diagnostic.OK, diagnostic.getSeverity(), "Diagnostic should be OK");

        // Validate saving of GenModel
        assertTrue(validateSaveGenModel(genModel), "Saving of GenModel failed: " + diagnostic);
    }

    /**
     * Tests that saving a valid {@link GenModel} does not throw and the file is present afterward.
     *
     * @param genModel {@link GenModel} to save
     * @return {@code true} if file was successfully saved on the disk, {@code false} if an {@link IOException} occurred
     */
    boolean validateSaveGenModel(GenModel genModel) {
        try {
            assertNotNull(config.getEcoreGenModelDir(), "GenModelPathToSaveTo should not be null");

            File genModelFile = saveGenModel(genModel, config.getEcoreGenModelDir());

            assertNotNull(genModelFile, "GenModelFile should not be null");
            assertTrue(genModelFile.exists(), "GenModel file does not exist: " + genModelFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }
}
