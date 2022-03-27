package org.metadevs.holeinthewall.utils.interfaces;

public interface CommandProvider {

    boolean validateArgs();

    void execute();
}
