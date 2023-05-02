package de.dertkw.refreshrate;

import org.tinylog.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RefreshRate {
    private TrayIcon icon;
    private List<Display> displays;
    private Display selectedDisplay;
    private final List<CheckboxMenuItem> displayCheckboxes = new ArrayList<>();
    private final PopupMenu menu = new PopupMenu();

    public void update() {
        Logger.info("Updating everything");
        displays = Utils.getDisplays();
        int index = Utils.getLastSelection();
        if (displays.size() >= (index + 1)) {
            selectedDisplay = displays.get(index);
        } else {
            selectedDisplay = displays.get(0);
        }
        updateMenu();
        updateIcon();
    }

    private void updateIcon() {
        Logger.debug("Updating icon");
        BufferedImage img = Utils.createImage(selectedDisplay.getRefreshRate());
        if (icon == null) {
            icon = new TrayIcon(img);
            icon.setPopupMenu(menu);
            try {
                SystemTray.getSystemTray().add(icon);
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
        } else {
            icon.setImage(img);
        }
        icon.setToolTip("Refresh Rate: " + selectedDisplay);
    }

    private void updateMenu() {
        Logger.debug("Updating menu");
        menu.removeAll();
        displayCheckboxes.clear();

        for (final Display display : displays) {
            final CheckboxMenuItem cmi = new CheckboxMenuItem(display.getNameAndResolution(), display.equals(selectedDisplay));
            cmi.setName(String.valueOf(display.getIndex()));
            cmi.addItemListener(e -> {
                selectedDisplay = display;
                Utils.storeLastSelection(display.getIndex());
                Logger.debug("Display has been selected: " + display);
                // update checkbox states
                displayCheckboxes.forEach(checkbox -> checkbox.setState(checkbox.equals(cmi)));
                // update the rest
                update();
            });
            displayCheckboxes.add(cmi);
            menu.add(cmi);
        }
        menu.addSeparator();

        Menu changeRefreshRate = new Menu("Change Refresh Rate");
        for (Integer refreshRate : selectedDisplay.getRefreshRates()) {
            boolean isCurrent = selectedDisplay.getRefreshRate().equals(refreshRate);
            MenuItem mi = new MenuItem(refreshRate + " Hz");
            mi.setName(String.valueOf(refreshRate));
            mi.setEnabled(!isCurrent);
            if (!isCurrent) {
                mi.addActionListener(e -> {
                    int rate = Integer.parseInt(((MenuItem) e.getSource()).getName());
                    Utils.changeRefreshRate(selectedDisplay, rate);
                    selectedDisplay.setRefreshRate(rate);
                    update();
                });
            }
            changeRefreshRate.add(mi);
        }
        menu.add(changeRefreshRate);

        MenuItem setPath = new MenuItem("Configure ChangeScreenResolution");
        setPath.addActionListener(e -> {
            Logger.debug("Opening file chooser");
            JFileChooser fc = new JFileChooser();
            String path = Utils.getCSRPath();
            if (path != null && !path.isEmpty()) {
                File file = new File(path);
                if (file.exists() && file.isFile()) {
                    fc.setCurrentDirectory(file.getParentFile());
                }
            }
            int result = fc.showOpenDialog(new JDialog());
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fc.getSelectedFile();
                Utils.storeCSRPath(selectedFile.getAbsolutePath());
            }
        });
        menu.add(setPath);

        MenuItem updateItem = new MenuItem("Force Update");
        updateItem.addActionListener(e -> update());
        menu.add(updateItem);

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        menu.addSeparator();
        menu.add(exitItem);
    }
}