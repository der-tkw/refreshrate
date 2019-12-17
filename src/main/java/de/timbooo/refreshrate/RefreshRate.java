package de.timbooo.refreshrate;

import javax.swing.JOptionPane;
import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RefreshRate {
    private int selectedDisplay = 0;
    private TrayIcon icon;
    private Map<Integer, Integer> displays;
    private List<CheckboxMenuItem> displayCheckboxes = new ArrayList<>();
    private PopupMenu menu = new PopupMenu();

    public void start() {
        selectedDisplay = Utils.loadDefault();
        displays = Utils.getDisplays();

        if (selectedDisplay > (displays.size() - 1)) {
            // fallback in case the default display does not longer exist
            selectedDisplay = 0;
        }

        createOrUpdateMenu();
        createOrUpdateIcon();

        SystemTray tray = SystemTray.getSystemTray();
        try {
            tray.add(icon);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public void refresh() {
        // copy displays for later comparison
        Map<Integer, Integer> compare = new HashMap<>();
        compare.putAll(displays);

        // rebuild menu etc.
        displays = Utils.getDisplays();

        // check for changes
        boolean changed = false;
        for (Integer index : displays.keySet()) {
            Integer oldRR = compare.get(index);
            Integer newRR = displays.get(index);

            if (oldRR != null && newRR != null && !oldRR.equals(newRR)) {
                JOptionPane.showMessageDialog(null, "Refresh rate of display " + index + " changed from " + oldRR + "Hz" +
                        " to " + newRR + "Hz.");
                changed = true;
            }
        }

        if (displays.size() != compare.size()) {
            changed = true;
        }

        if (changed) {
            createOrUpdateMenu();
            updateStates(findActiveDisplay());
            createOrUpdateIcon();
        }
    }

    private void createOrUpdateIcon() {
        BufferedImage img = Utils.createImage(displays.get(selectedDisplay));
        if (icon == null) {
            icon = new TrayIcon(img);
            icon.setPopupMenu(menu);
        } else {
            icon.setImage(img);
        }
        icon.setToolTip("Refresh Rate (Display " + selectedDisplay + ")");
    }

    private void createOrUpdateMenu() {
        menu.removeAll();
        displayCheckboxes.clear();

        for (final Integer index : displays.keySet()) {
            final CheckboxMenuItem mi = new CheckboxMenuItem("Display " + index, index.equals(selectedDisplay) ? true
                    : false);
            mi.setName("" + index);
            mi.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    selectedDisplay = index;
                    updateStates(mi);
                    createOrUpdateIcon();
                }
            });
            displayCheckboxes.add(mi);
            menu.add(mi);
        }

        menu.addSeparator();
        MenuItem defaultDisplay = new MenuItem("Set Default Display");
        defaultDisplay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] choices = new String[displays.size()];
                String prefix = "Display ";
                for (final Integer index : displays.keySet()) {
                    choices[index] = prefix + index;
                }
                String result = (String) JOptionPane.showInputDialog(null, "",
                        "Set Default Display", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
                if (result != null) {
                    Integer defaultDisplayId = Integer.valueOf(result.substring(prefix.length()));
                    Utils.saveDefault(defaultDisplayId);
                }
            }
        });
        menu.add(defaultDisplay);

        MenuItem refreshItem = new MenuItem("Refresh Manually");
        refreshItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        });
        menu.add(refreshItem);

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menu.addSeparator();
        menu.add(exitItem);
    }

    private CheckboxMenuItem findActiveDisplay() {
        for (CheckboxMenuItem display : displayCheckboxes) {
            if (display.getName().equals("" + selectedDisplay)) {
                return display;
            }
        }
        throw new RuntimeException("Could not find an active display.");
    }

    private void updateStates(CheckboxMenuItem active) {
        for (CheckboxMenuItem checkbox : displayCheckboxes) {
            if (checkbox.equals(active)) {
                checkbox.setState(true);
            } else {
                checkbox.setState(false);
            }
        }
    }
}