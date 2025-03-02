package cz.cuni.mff.umlift.plugin.intellij.contextmenu;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;

public class ContextUtils {
    static void notifySuccess(@NotNull Project project, final String message) {
        NotificationGroupManager.getInstance().getNotificationGroup("cz.cuni.mff.umlift.plugin.intellij.notificationgroup")
                .createNotification(message, NotificationType.INFORMATION)
                .notify(project);
    }

    static void notifyFailure(@NotNull Project project, final String message) {
        NotificationGroupManager.getInstance().getNotificationGroup("cz.cuni.mff.umlift.plugin.intellij.notificationgroup")
                .createNotification(message, NotificationType.ERROR)
                .notify(project);
    }

    static void refreshFiles() {
        VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
    }

    static class ProgressOutputStream extends OutputStream {
        private final ProgressIndicator progressIndicator;

        public ProgressOutputStream(ProgressIndicator progressIndicator) {
            this.progressIndicator = progressIndicator;
        }

        @Override
        public void write(int b) {
            String output = String.valueOf((char) b);
            if (progressIndicator != null) {
                progressIndicator.setText(progressIndicator.getText() + output);
            }
        }
    }
}
