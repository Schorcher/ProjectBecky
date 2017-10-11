package com.becky.util;

/**
 * Simple name value pair
 * Created by Clayton on 10/10/2017.
 */
public class NameValuePair<N, V> {
    private final N name;
    private final V value;

    public NameValuePair(final N name, final V value) {
        this.name = name;
        this.value = value;
    }

    public N getName() {
        return this.name;
    }

    public V getValue() {
        return this.value;
    }
}
