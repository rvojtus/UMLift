package cz.cuni.mff.umlift.plugin.intellij.filetypes;

import com.intellij.icons.AllIcons;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Represents the file type for EMF Ecore files in IntelliJ IDEA.
 * Ecore files are used as part of the Eclipse Modeling Framework (EMF)
 * to define models and their components.
 * This class provides properties such as name, description, default file extension,
 * and icon to identify and describe Ecore files in the IDE.
 * <p>
 * Extends the {@link LanguageFileType} class and uses the XML language as its basis.
 */
public class EcoreFileType extends LanguageFileType {
    public static final EcoreFileType INSTANCE = new EcoreFileType();

    /**
     * Constructs an instance of the EcoreFileType class.
     * <p>
     * This private constructor ensures that the EcoreFileType class follows a singleton
     * design pattern, preventing external instantiation and restricting its instance
     * to the predefined {@code INSTANCE}.
     * <p>
     * The constructor initializes the Ecore file type as being based on the XML language
     * by passing {@link XMLLanguage#INSTANCE} to the superclass constructor.
     */
    private EcoreFileType() {
        super(XMLLanguage.INSTANCE);
    }

    @Override
    public @NonNls @NotNull String getName() {
        return "Ecore File";
    }

    @Override
    public @NlsContexts.Label @NotNull String getDescription() {
        return "EMF ecore file";
    }

    @Override
    public @Nls @NotNull String getDisplayName() {
        return "Ecore";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "ecore";
    }

    @Override
    public Icon getIcon() {
        return AllIcons.FileTypes.Xml;
    }
}
