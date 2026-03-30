package aadhaar.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

public final class Theme {
    public static final Color APP_BACKGROUND = new Color(246, 248, 255);
    public static final Color SURFACE = Color.WHITE;
    public static final Color SURFACE_ALT = new Color(248, 250, 255);
    public static final Color PRIMARY = new Color(70, 132, 229);
    public static final Color PRIMARY_DARK = new Color(41, 95, 200);
    public static final Color PRIMARY_SOFT = new Color(227, 237, 255);
    public static final Color ACCENT = new Color(247, 198, 67);
    public static final Color ACCENT_SOFT = new Color(255, 247, 229);
    public static final Color BORDER = new Color(223, 229, 243);
    public static final Color TEXT_PRIMARY = new Color(35, 47, 77);
    public static final Color TEXT_SECONDARY = new Color(95, 106, 134);
    public static final Color SUCCESS = new Color(36, 145, 94);
    public static final Color WARNING = new Color(203, 113, 29);
    public static final Color DANGER = new Color(188, 68, 68);

    private Theme() {
    }

    public static Font titleFont(float size) {
        return new Font("Segoe UI", Font.BOLD, Math.round(size));
    }

    public static Font bodyFont(float size) {
        return new Font("Segoe UI", Font.PLAIN, Math.round(size));
    }

    public static Border roundedBorder(Color color, int thickness, int padding) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, thickness, true),
                BorderFactory.createEmptyBorder(padding, padding, padding, padding)
        );
    }

    public static void applyQuality(Graphics2D graphics) {
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setStroke(new BasicStroke(1.2f));
    }
}
