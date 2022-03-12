package org.metadevs.holeinthewall.utils;

public class Option<T> {
    private final String name;
    private final T value;

    public Option(final String name, final T value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public T getValue() {
        return this.value;
    }
}
