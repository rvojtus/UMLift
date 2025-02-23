package cz.cuni.mff.vojtusr.informalmddintellijplugin.contextmenu;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;

public class ContextUtils {
    static void notifySuccess(@NotNull Project project, final String message) {
        NotificationGroupManager.getInstance().getNotificationGroup("cz.cuni.mff.vojtusr.notificationgroup")
                .createNotification(message, NotificationType.INFORMATION)
                .notify(project);
    }

    static void notifyFailure(@NotNull Project project, final String message) {
        NotificationGroupManager.getInstance().getNotificationGroup("cz.cuni.mff.vojtusr.notificationgroup")
                .createNotification(message, NotificationType.ERROR)
                .notify(project);
    }

    static void refreshFiles() {
        VirtualFileManager.getInstance().refreshWithoutFileWatcher(true);
    }
}
