package com.turbinekreuzberg.plugins.utils;

import com.turbinekreuzberg.plugins.PyzPluginTestCase;

public class ComposerPackageDescriptionTest extends PyzPluginTestCase {
    private ComposerPackageDescription packageDescription;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        packageDescription = new ComposerPackageDescription("spryker/catalog", "1.0.0");
    }

    public void testConstructorAndGetters() {
        assertEquals("spryker/catalog", packageDescription.name);
        assertEquals("1.0.0", packageDescription.version);
    }

    public void testDefaultConstructor() {
        packageDescription = new ComposerPackageDescription();
        assertNull("Name should be null by default", packageDescription.name);
        assertNull("Version should be null by default", packageDescription.version);
    }
}
