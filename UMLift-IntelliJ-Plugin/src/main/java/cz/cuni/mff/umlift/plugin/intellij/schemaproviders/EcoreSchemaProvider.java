package cz.cuni.mff.umlift.plugin.intellij.schemaproviders;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.xml.XmlSchemaProvider;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.Objects;

/**
 * Provides schema support for Ecore-related files in the context of XML handling.
 * The class extends {@link XmlSchemaProvider} to offer custom schema functionality
 * for files recognized as Ecore within an IntelliJ Platform module.
 * <p>
 * The implementation primarily determines whether the schema provider is applicable
 * to a given XML file and retrieves the corresponding schema if applicable.
 * <p>
 * Responsibilities:
 * - Checks if a given XML file is associated with the Ecore file type using
 * descriptors from {@link EcoreFileTypeUsageSchemaDescriptor}.
 * - Supplies the appropriate schema referencing the provided module and context.
 * <p>
 * Usage Notes:
 * - The provider relies on the static utility methods from
 * {@link EcoreFileTypeUsageSchemaDescriptor} to validate Ecore file type usage.
 * - Schema retrieval makes use of the resource file "Ecore.xsd" within the same
 * package, resolving it based on the environment's context.
 */
public class EcoreSchemaProvider extends XmlSchemaProvider {
    private static final Logger LOG = Logger.getInstance(EcoreSchemaProvider.class);

    @Override
    public boolean isAvailable(final @NotNull XmlFile file) {
        return EcoreFileTypeUsageSchemaDescriptor.isEcore(file);
    }

    @Override
    public @Nullable XmlFile getSchema(@NotNull @NonNls String url, @Nullable Module module, @NotNull PsiFile psiFile) {
        return module != null && EcoreFileTypeUsageSchemaDescriptor.isEcore(psiFile) ? getReference(module) : null;
    }

    private static XmlFile getReference(@NotNull Module module) {
        final URL resource = EcoreSchemaProvider.class.getResource("Ecore.xsd");
        final VirtualFile fileByUrl = VfsUtil.findFileByURL(Objects.requireNonNull(resource));
        if (fileByUrl == null) {
            LOG.error("XSD not found: " + resource);
            return null;
        }

        PsiFile psiFile = PsiManager.getInstance(module.getProject()).findFile(fileByUrl);
        LOG.assertTrue(psiFile != null);
        return (XmlFile) psiFile.copy();
    }
}
