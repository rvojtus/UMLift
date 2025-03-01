package cz.cuni.mff.umlift.plugin.intellij;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.nio.charset.StandardCharsets;

public class UMLetFileType implements FileType {

    @Override
    public @NonNls @NotNull String getName() {
        return "UXF File";
    }

    @Override
    public @NlsContexts.Label @NotNull String getDescription() {
        return "UMLet XML File";
    }

    @Override
    public @NlsSafe @NotNull String getDefaultExtension() {
        return "";
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public boolean isBinary() {
        return false;
    }

    @Override
    public @NonNls @Nullable String getCharset(@NotNull VirtualFile file, byte @NotNull [] content) {
        return StandardCharsets.UTF_8.name();
    }
}
