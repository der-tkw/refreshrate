package de.dertkw.refreshrate;

import org.tinylog.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class Utils {
    private static final String PREF_KEY_LAST_SELECTION = "lastSelection";
    private static final String PREF_KEY_CSR_PATH = "csrPath";

    private Utils() {
    }

    public static int getLastSelection() {
        Preferences preferences = Preferences.userNodeForPackage(RefreshRate.class);
        return preferences.getInt(PREF_KEY_LAST_SELECTION, 0);
    }

    public static void storeLastSelection(int index) {
        Preferences.userNodeForPackage(RefreshRate.class).putInt(PREF_KEY_LAST_SELECTION, index);
        Logger.debug("Stored last selected device: " + index);
    }

    public static String getCSRPath() {
        Preferences preferences = Preferences.userNodeForPackage(RefreshRate.class);
        String path = preferences.get(PREF_KEY_CSR_PATH, null);
        if (path == null) {
            return null;
        } else {
            // validate path
            File tmp = new File(path);
            if (!tmp.exists() || tmp.isDirectory()) {
                return null;
            }
        }
        return path;
    }

    public static void storeCSRPath(String path) {
        Preferences.userNodeForPackage(RefreshRate.class).put(PREF_KEY_CSR_PATH, path);
        Logger.debug("Stored CSR path: " + path);
    }

    public static void triggerCSRPath() {
        Logger.debug("Opening file chooser");
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Choose ChangeScreenResolution.exe ...");
        String path = Utils.getCSRPath();
        if (path != null) {
            File file = new File(path);
            fc.setCurrentDirectory(file.getParentFile());
        }
        int result = fc.showOpenDialog(new JDialog());
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fc.getSelectedFile();
            if (selectedFile.getName().equals("ChangeScreenResolution.exe")) {
                Utils.storeCSRPath(selectedFile.getAbsolutePath());
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect exe. Please choose ChangeScreenResolution.exe");
            }
        } else if (result == JFileChooser.CANCEL_OPTION) {
            if (getCSRPath() == null) {
                String msg = "RefreshRate cannot work without ChangeScreenResolution. Please restart the app.";
                JOptionPane.showMessageDialog(null, msg);
                throw new RuntimeException(msg);
            }
        }
    }

    public static void resetPreferences() {
        try {
            Preferences.userNodeForPackage(RefreshRate.class).clear();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
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
        List<Display> displays = new ArrayList<>();

        List<String> output = executeCSRCommand("/l");
        Display tmpDisplay = null;
        int index = 0;
        String settings = "Settings: ";
        for (String line : output) {
            if (line.contains("[")) {
                int tmpIndex = index++;
                String prefix = "DISPLAY" + (tmpIndex + 1);
                String card = line.substring(line.indexOf(prefix) + prefix.length()).trim();
                tmpDisplay = new Display(tmpIndex, card);
            } else if (line.contains(settings)) {
                String resolution = line.substring(line.indexOf(settings) + settings.length(), line.lastIndexOf("@")).trim();
                Integer refreshRate = Integer.valueOf(line.substring(line.indexOf("@") + 1, line.indexOf("Hz")));
                if (tmpDisplay != null) {
                    tmpDisplay.initSettings(resolution, refreshRate);
                    displays.add(tmpDisplay);
                }
            }
        }

        for (Display display : displays) {
            Set<Integer> refreshRates = new HashSet<>();
            output = executeCSRCommand("/m /d=" + display.getIndex());
            for (String line : output) {
                if (line.contains("@")) {
                    int refreshRate = Integer.parseInt(line.substring(line.indexOf("@") + 1, line.indexOf("Hz")));
                    if (refreshRate % 10 != 9) {
                        refreshRates.add(refreshRate);
                    }
                }
            }
            display.initRefreshRates(refreshRates.stream().sorted(Collections.reverseOrder()).collect(Collectors.toList()));
        }

        if (displays.isEmpty()) {
            throw new RuntimeException("No devices found in your environment.");
        }
        return displays;
    }

    public static void changeRefreshRate(Display display, int refreshRate) {
        executeCSRCommand("/d=" + display.getIndex() + " /f=" + refreshRate);
    }

    private static List<String> executeCSRCommand(String options) {
        return executeCommand("\"" + getCSRPath() + "\" " + options);
    }

    private static List<String> executeCommand(String cmd) {
        Logger.info("Executing command: " + cmd);
        try {
            Process process = Runtime.getRuntime().exec(cmd);

            StreamGobbler outputGobbler = new StreamGobbler(process.getInputStream(), "OUTPUT");
            StreamGobbler errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR");
            errorGobbler.start();
            outputGobbler.start();

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                Logger.error("Command exited abnormally: " + exitCode);
            }
            return new ArrayList<>(outputGobbler.getOutput());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}