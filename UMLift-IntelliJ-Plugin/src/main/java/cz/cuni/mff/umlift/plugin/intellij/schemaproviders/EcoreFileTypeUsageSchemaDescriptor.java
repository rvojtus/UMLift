package cz.cuni.mff.umlift.plugin.intellij.schemaproviders;

import com.intellij.internal.statistic.collectors.fus.fileTypes.FileTypeUsageSchemaDescriptor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class EcoreFileTypeUsageSchemaDescriptor implements FileTypeUsageSchemaDescriptor {
    public static final @NonNls String ECORE_EXTENSION = "ecore";

    @Override
    public boolean describes(@NotNull Project project, @NotNull VirtualFile file) {
        return isEcore(file);
    }

    public static boolean isEcore(@NotNull PsiFile file) {
        final VirtualFile virtualFile = file.getViewProvider().getVirtualFile();
        return isEcore(virtualFile);
    }

    public static boolean isEcore(@NotNull VirtualFile virtualFile) {
        if (virtualFile.getName().endsWith(ECORE_EXTENSION)) {
            FileType fileType = virtualFile.getFileType();
            return fileType == getFileType() && !fileType.isBinary();
        }
        return false;
    }

    public static @NotNull FileType getFileType() {
        return FileTypeManager.getInstance().getFileTypeByExtension(ECORE_EXTENSION);
    }
}
