package com.turbinekreuzberg.plugins.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ComposerPackageDescription {
    public String name;
    public String version;

    public ComposerPackageDescription() {
        super();
    }

    public ComposerPackageDescription(String name, String version) {
        this.name = name;
        this.version = version;
    }
}
