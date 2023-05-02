package de.dertkw.refreshrate;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class Display {
    private final int index;
    private final GraphicsDevice device;
    private final String name;
    private final List<Integer> refreshRates;
    private Integer refreshRate;

    /**
     * @see Utils#loadDisplayNames
     */
    public Display(int index, GraphicsDevice device, String name, Integer refreshRate, List<Integer> refreshRates) {
        this.index = index;
        this.device = device;
        this.name = "Display " + (index + 1);
        //this.name = name;
        this.refreshRate = refreshRate;
        this.refreshRates = refreshRates;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getNameAndResolution() {
        return name + " (" + getResolution() + ")";
    }

    public Integer getRefreshRate() {
        return refreshRate;
    }

    public void setRefreshRate(Integer refreshRate) {
        this.refreshRate = refreshRate;
    }

    public List<Integer> getRefreshRates() {
        return refreshRates;
    }

    private String getResolution() {
        DisplayMode dm = device.getDisplayMode();
        StringBuilder sb = new StringBuilder();
        sb.append(dm.getWidth());
        sb.append(" x ");
        sb.append(dm.getHeight());
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Display display = (Display) o;
        return index == display.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name);
        sb.append(" @ ");
        sb.append(getResolution());
        sb.append(", ");
        sb.append(refreshRate);
        sb.append(" Hz");
        return sb.toString();
    }
}
