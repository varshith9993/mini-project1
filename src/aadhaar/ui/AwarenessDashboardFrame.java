package aadhaar.ui;

import aadhaar.backend.AwarenessService;
import java.nio.file.Path;
import javax.swing.JFrame;

public final class AwarenessDashboardFrame extends JFrame {
    public AwarenessDashboardFrame(AwarenessService service, Path assetsDir) {
        super("Legacy Desktop UI Disabled");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(960, 720);
    }
}
