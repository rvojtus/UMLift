package cz.cuni.mff.umlift.plugin.intellij.filetypes;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class UMLetFileType extends LanguageFileType {
    public static final UMLetFileType INSTANCE = new UMLetFileType();

    private UMLetFileType() {
        super(PlainTextLanguage.INSTANCE);
    }

    @Override
    public @NonNls @NotNull String getName() {
        return "UMLet File";
    }

    @Override
    public @NlsContexts.Label @NotNull String getDescription() {
        return "UMLet UXF file";
    }

    @Override
    public @Nls @NotNull String getDisplayName() {
        return "UXF";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "uxf";
    }

    @Override
    public Icon getIcon() {
        return AllIcons.FileTypes.Any_type;
    }
}
