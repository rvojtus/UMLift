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
 * Represents the file type for EMF GenModel files in IntelliJ IDEA.
 * GenModel files are used as part of the Eclipse Modeling Framework (EMF)
 * to define generator models, which describe the generation structure of code
 * based on Ecore models.
 * This class provides properties such as name, description, default file extension,
 * and icon to identify and describe GenModel files in the IDE.
 * <p>
 * Extends the {@link LanguageFileType} class and uses the XML language as its basis.
 */
public class GenModelFileType extends LanguageFileType {
    public static final GenModelFileType INSTANCE = new GenModelFileType();

    /**
     * Constructs an instance of the GenModelFileType class.
     * <p>
     * This private constructor ensures that the GenModelFileType class adheres to the singleton
     * design pattern, restricting instantiation to the static {@code INSTANCE}.
     * It initializes the GenModel file type as being associated with the XML language
     * by passing {@link XMLLanguage#INSTANCE} to the superclass constructor.
     */
    private GenModelFileType() {
        super(XMLLanguage.INSTANCE);
    }

    @Override
    public @NonNls @NotNull String getName() {
        return "GenModel File";
    }

    @Override
    public @NlsContexts.Label @NotNull String getDescription() {
        return "EMF GenModel file";
    }

    @Override
    public @Nls @NotNull String getDisplayName() {
        return "GenModel";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "genmodel";
    }

    @Override
    public Icon getIcon() {
        return AllIcons.FileTypes.Xml;
    }
}
