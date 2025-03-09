package cz.cuni.mff.umlift.plugin.intellij.filetypes;

import com.intellij.icons.AllIcons;
import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GenModelFileType extends LanguageFileType {
    public static final GenModelFileType INSTANCE = new GenModelFileType();

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
