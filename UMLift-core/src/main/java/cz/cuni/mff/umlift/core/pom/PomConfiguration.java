package cz.cuni.mff.umlift.core.pom;

import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;

/**
 * Provides configuration for {@link PomGenerator}.
 *
 * @since 1.0
 */
public record PomConfiguration(String modelVersion, String groupId, String artifactId, String version,
                               MavenPackaging packaging, GenJDKLevel genJDKLevel) {
}
