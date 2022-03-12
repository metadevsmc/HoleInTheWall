package org.metadevs.holeinthewall.metalib.messages;

import org.jetbrains.annotations.NotNull;

public class Placeholder {
    private final String key;
    private final String value;

    public Placeholder(@NotNull String toReplace,@NotNull String replacement) {
        this.key = toReplace;
        this.value = replacement;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
