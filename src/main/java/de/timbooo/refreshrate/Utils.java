package de.timbooo.refreshrate;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

public class Utils {
    public static final String DEFAULT_PREF_KEY = "default";

    private Utils() {
    }

    public static BufferedImage createImage(int rr) {
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = img.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Arial", Font.PLAIN, rr >= 100 ? 9 : 10));
        graphics.drawString("" + rr, rr >= 100 ? 0 : 2, 12);
        return img;
    }

    public static int loadDefault() {
        Preferences preferences = Preferences.userNodeForPackage(RefreshRate.class);
        return preferences.getInt(DEFAULT_PREF_KEY, 0);
    }

    public static void saveDefault(Integer defaultDisplayId) {
        Preferences.userNodeForPackage(RefreshRate.class).putInt(DEFAULT_PREF_KEY, defaultDisplayId);
    }

    public static Map<Integer, Integer> getDisplays() {
        Map<Integer, Integer> result = new HashMap<>();

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = env.getScreenDevices();

        if (devices.length == 0) {
            throw new RuntimeException("No devices found in your environment.");
        }

        for (int i = 0; i < devices.length; i++) {
            DisplayMode dm = devices[i].getDisplayMode();
            if (dm.getRefreshRate() == DisplayMode.REFRESH_RATE_UNKNOWN) {
                continue;
            }
            result.put(i, dm.getRefreshRate());
        }

        return Collections.unmodifiableMap(result);
    }
}