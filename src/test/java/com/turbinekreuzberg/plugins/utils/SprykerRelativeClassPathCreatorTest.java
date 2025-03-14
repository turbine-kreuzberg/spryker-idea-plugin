package com.turbinekreuzberg.plugins.utils;

import com.intellij.mock.MockFileTypeManager;
import com.intellij.mock.MockVirtualFile;
import com.turbinekreuzberg.plugins.PyzPluginTestCase;

public class SprykerRelativeClassPathCreatorTest extends PyzPluginTestCase {

    public void testGetRelativePathForFileInSprykerVendor() {
        MockVirtualFile vFile = MockVirtualFile.file("PHP.UNKNOWN");
        MockVirtualFile vFileDir = MockVirtualFile.dir("sdf/vendor/spryker/acl/src/Spryker/Zed/blah.txt");
        MockFileTypeManager.getInstance().getFileTypeByFile(vFile);
        vFile.setText("<?php ");
        vFile.setParent(vFileDir);

        assertEquals("Zed/blah.txt", (new SprykerRelativeClassPathCreator()).getRelativeClassPath(vFile));
    }

    public void testGetEmptyStringForFileNotInSprykerVendor() {
        MockVirtualFile vFile = MockVirtualFile.file("PHP.UNKNOWN");
        MockVirtualFile vFileDir = MockVirtualFile.dir("sdf/vendor/symfony/acl/src/Spryker/Zed/blah.txt");
        MockFileTypeManager.getInstance().getFileTypeByFile(vFile);
        vFile.setText("<?php ");
        vFile.setParent(vFileDir);

        assertEquals("", (new SprykerRelativeClassPathCreator()).getRelativeClassPath(vFile));
    }

    public void testIsFileNotLocatedInSprykerVendor() {
        MockVirtualFile vFile = MockVirtualFile.file("PHP.UNKNOWN");
        MockVirtualFile vFileDir = MockVirtualFile.dir("sdf/vendor/symfony/acl/src/Spryker/Zed/blah.txt");
        MockFileTypeManager.getInstance().getFileTypeByFile(vFile);
        vFile.setText("<?php ");
        vFile.setParent(vFileDir);

        assertFalse((new SprykerRelativeClassPathCreator()).isLocatedInSprykerVendor(vFile));
    }

    public void testIsFileLocatedInSprykerVendor() {
        MockVirtualFile vFile = MockVirtualFile.file("PHP.UNKNOWN");
        MockVirtualFile vFileDir = MockVirtualFile.dir("sdf/vendor/spryker/acl/src/Spryker/Zed/blah.txt");
        MockFileTypeManager.getInstance().getFileTypeByFile(vFile);
        vFile.setText("<?php ");
        vFile.setParent(vFileDir);

        assertTrue((new SprykerRelativeClassPathCreator()).isLocatedInSprykerVendor(vFile));
    }
}