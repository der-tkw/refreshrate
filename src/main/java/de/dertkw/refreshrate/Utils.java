package de.dertkw.refreshrate;

import org.tinylog.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class Utils {
    private static final String LAST_SELECTION_PREF_KEY = "lastSelection";
    private static final String CSR_PATH_PREF_KEY = "csrPath";

    private static List<String> displayNames;
    private static final String PS_GET_DISPLAY_INFO = "powershell.exe -command \"get-wmiobject -Query 'select * from Win32_PnPEntity where service=\\\"monitor\\\"' | select Name | ft -HideTableHeaders\"";

    private Utils() {
    }

    public static int getLastSelection() {
        Preferences preferences = Preferences.userNodeForPackage(RefreshRate.class);
        return preferences.getInt(LAST_SELECTION_PREF_KEY, 0);
    }

    public static void storeLastSelection(int index) {
        Preferences.userNodeForPackage(RefreshRate.class).putInt(LAST_SELECTION_PREF_KEY, index);
        Logger.debug("Stored last selected device: " + index);
    }

    public static String getCSRPath() {
        Preferences preferences = Preferences.userNodeForPackage(RefreshRate.class);
        return preferences.get(CSR_PATH_PREF_KEY, null);
    }

    public static void storeCSRPath(String path) {
        Preferences.userNodeForPackage(RefreshRate.class).put(CSR_PATH_PREF_KEY, path);
        Logger.debug("Stored CSR path: " + path);
    }

    public static BufferedImage createImage(int rr) {
        Logger.debug("Created image for refresh rate: " + rr);
        BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = img.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.setFont(new Font("Arial", Font.PLAIN, rr >= 100 ? 9 : 10));
        graphics.drawString(String.valueOf(rr), rr >= 100 ? 0 : 2, 12);
        return img;
    }

    public static List<Display> getDisplays() {
        Logger.debug("Loading displays");
        GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

        List<Display> displays = new ArrayList<>();
        for (int i = 0; i < devices.length; i++) {
            GraphicsDevice device = devices[i];
            displays.add(new Display(i, device, getDisplayName(i), device.getDisplayMode().getRefreshRate(), loadRefreshRates(device)));
        }

        if (devices.length == 0 || displays.isEmpty()) {
            throw new RuntimeException("No devices found in your environment.");
        }
        return displays;
    }

    public static String getDisplayName(int index) {
        if (displayNames == null) {
            loadDisplayNames();
        }
        if (displayNames.size() >= (index + 1)) {
            return displayNames.get(index);
        }
        return null;
    }

    public static void changeRefreshRate(Display display, int refreshRate) {
        String path = getCSRPath();
        if (path == null) {
            prohibitChange();
            return;
        }
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            prohibitChange();
            return;
        }

        executeCommand("\"" + path + "\" /d=" + display.getIndex() + " /f=" + refreshRate);
    }

    private static void prohibitChange() {
        JOptionPane.showMessageDialog(null, "Refresh rate cannot be changed. Configure ChangeScreenResolution first.");
    }

    private static List<Integer> loadRefreshRates(GraphicsDevice device) {
        Logger.debug("Loading refresh rates for device " + device.getIDstring());
        Set<Integer> result = new HashSet<>();
        for (DisplayMode mode : device.getDisplayModes()) {
            int refreshRate = mode.getRefreshRate();
            if (refreshRate % 10 != 9) {
                result.add(refreshRate);
            }
        }
        return result.stream().sorted(Collections.reverseOrder()).collect(Collectors.toList());
    }

    /**
     * It is not possible to map the display names or any other output from wmi to the information from AWT, so this is currently not used.
     */
    private static void loadDisplayNames() {
        List<String> result = executeCommand(PS_GET_DISPLAY_INFO);
        displayNames = result.stream().filter(s -> s != null && !s.isEmpty()).map(Utils::fixName).collect(Collectors.toList());
    }

    private static String fixName(String name) {
        String prefix = "Generic Monitor (";
        if (name.startsWith(prefix)) {
            return name.substring(prefix.length(), name.lastIndexOf(")"));
        }
        return name;
    }

    private static List<String> executeCommand(String cmd) {
        Logger.info("Executing command: " + cmd);
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            int exitCode = process.waitFor();
            List<String> result = new ArrayList<>();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String line;
            while ((line = stdInput.readLine()) != null) {
                if (!line.isEmpty()) {
                    result.add(line);
                    Logger.debug(line);
                }
            }

            while ((line = stdError.readLine()) != null) {
                if (!line.isEmpty()) {
                    Logger.error(line);
                }
            }

            if (exitCode != 0) {
                Logger.error("Command exited abnormally: " +  exitCode);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}