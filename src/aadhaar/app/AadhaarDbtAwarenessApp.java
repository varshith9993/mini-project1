package aadhaar.app;

import aadhaar.backend.AwarenessService;
import aadhaar.ui.AwarenessDashboardFrame;
import java.nio.file.Path;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class AadhaarDbtAwarenessApp {
    private AadhaarDbtAwarenessApp() {
    }

    public static void main(String[] args) {
        setLookAndFeel();
        SwingUtilities.invokeLater(() -> {
            AwarenessService service = new AwarenessService();
            AwarenessDashboardFrame frame = new AwarenessDashboardFrame(service, Path.of("assets"));
            frame.setVisible(true);
        });
    }

    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fallback to the default Swing look and feel if the system LAF is unavailable.
        }
    }
}
