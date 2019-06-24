package org.cirdles.squid.gui.dataReductionReports.reportsManager;

import java.io.Serializable;

public class Fruit implements Serializable, Comparable<Fruit> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String name = "";

    public Fruit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Fruit fruit) {
        return name.compareTo(fruit.getName());
    }
}
