package cz.cuni.mff.umlift.plugin.intellij.filetypes;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Represents the file type for UMLet UXF files in IntelliJ IDEA.
 * UMLet UXF files are used by the UMLet tool for creating and editing UML diagrams.
 * This class provides properties such as name, description, default file extension,
 * and icon to identify and describe UMLet UXF files in the IDE.
 * <p>
 * Extends the {@link LanguageFileType} class and uses the plain text language
 * as its basis.
 */
public class UMLetFileType extends LanguageFileType {
    public static final UMLetFileType INSTANCE = new UMLetFileType();

    /**
     * Constructs an instance of the UMLetFileType class.
     * <p>
     * This private constructor ensures that the UMLetFileType class adheres to the singleton
     * design pattern, restricting instantiation to the static {@code INSTANCE}.
     * It initializes the UMLet file type as being associated with the plain text language
     * by passing {@link PlainTextLanguage#INSTANCE} to the superclass constructor.
     */
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
