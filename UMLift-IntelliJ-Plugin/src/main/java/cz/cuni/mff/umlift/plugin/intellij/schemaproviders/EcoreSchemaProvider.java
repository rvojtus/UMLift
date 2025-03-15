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
