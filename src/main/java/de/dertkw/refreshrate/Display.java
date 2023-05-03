package de.dertkw.refreshrate;

import java.util.List;
import java.util.Objects;

public class Display {
    private final int index;
    private final String name;
    private final String card;
    private String resolution;
    private Integer refreshRate;
    private List<Integer> refreshRates;

    public Display(int index, String card) {
        this.index = index;
        this.card = card;
        this.name = "Display " + (index + 1);
    }

    public void initSettings(String resolution, Integer refreshRate) {
        this.resolution = resolution;
        this.refreshRate = refreshRate;
    }

    public void initRefreshRates(List<Integer> refreshRates) {
        this.refreshRates = refreshRates;
    }

    public int getIndex() {
        return index;
    }

    public String getNameAndResolution() {
        return name + " (" + resolution + ")";
    }

    public String getResolution() {
        return resolution;
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
        sb.append(" (");
        sb.append(resolution);
        sb.append(" @");
        sb.append(refreshRate);
        sb.append("Hz on ");
        sb.append(card);
        sb.append(")");
        return sb.toString();
    }
}
