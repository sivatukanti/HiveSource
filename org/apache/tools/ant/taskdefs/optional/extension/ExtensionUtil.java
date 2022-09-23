// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.extension;

import java.io.IOException;
import java.util.jar.Manifest;
import java.util.jar.JarFile;
import org.apache.tools.ant.DirectoryScanner;
import java.util.Iterator;
import java.io.File;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildException;
import java.util.ArrayList;
import java.util.List;

public final class ExtensionUtil
{
    private ExtensionUtil() {
    }
    
    static ArrayList toExtensions(final List adapters) throws BuildException {
        final ArrayList results = new ArrayList();
        for (int size = adapters.size(), i = 0; i < size; ++i) {
            final ExtensionAdapter adapter = adapters.get(i);
            final Extension extension = adapter.toExtension();
            results.add(extension);
        }
        return results;
    }
    
    static void extractExtensions(final Project project, final List libraries, final List fileset) throws BuildException {
        if (!fileset.isEmpty()) {
            final Extension[] extensions = getExtensions(project, fileset);
            for (int i = 0; i < extensions.length; ++i) {
                libraries.add(extensions[i]);
            }
        }
    }
    
    private static Extension[] getExtensions(final Project project, final List libraries) throws BuildException {
        final ArrayList extensions = new ArrayList();
        for (final FileSet fileSet : libraries) {
            boolean includeImpl = true;
            boolean includeURL = true;
            if (fileSet instanceof LibFileSet) {
                final LibFileSet libFileSet = (LibFileSet)fileSet;
                includeImpl = libFileSet.isIncludeImpl();
                includeURL = libFileSet.isIncludeURL();
            }
            final DirectoryScanner scanner = fileSet.getDirectoryScanner(project);
            final File basedir = scanner.getBasedir();
            final String[] files = scanner.getIncludedFiles();
            for (int i = 0; i < files.length; ++i) {
                final File file = new File(basedir, files[i]);
                loadExtensions(file, extensions, includeImpl, includeURL);
            }
        }
        return extensions.toArray(new Extension[extensions.size()]);
    }
    
    private static void loadExtensions(final File file, final List extensionList, final boolean includeImpl, final boolean includeURL) throws BuildException {
        try {
            final JarFile jarFile = new JarFile(file);
            final Extension[] extensions = Extension.getAvailable(jarFile.getManifest());
            for (int i = 0; i < extensions.length; ++i) {
                final Extension extension = extensions[i];
                addExtension(extensionList, extension, includeImpl, includeURL);
            }
        }
        catch (Exception e) {
            throw new BuildException(e.getMessage(), e);
        }
    }
    
    private static void addExtension(final List extensionList, final Extension originalExtension, final boolean includeImpl, final boolean includeURL) {
        Extension extension = originalExtension;
        if (!includeURL && null != extension.getImplementationURL()) {
            extension = new Extension(extension.getExtensionName(), extension.getSpecificationVersion().toString(), extension.getSpecificationVendor(), extension.getImplementationVersion().toString(), extension.getImplementationVendor(), extension.getImplementationVendorID(), null);
        }
        final boolean hasImplAttributes = null != extension.getImplementationURL() || null != extension.getImplementationVersion() || null != extension.getImplementationVendorID() || null != extension.getImplementationVendor();
        if (!includeImpl && hasImplAttributes) {
            extension = new Extension(extension.getExtensionName(), extension.getSpecificationVersion().toString(), extension.getSpecificationVendor(), null, null, null, extension.getImplementationURL());
        }
        extensionList.add(extension);
    }
    
    static Manifest getManifest(final File file) throws BuildException {
        try {
            final JarFile jarFile = new JarFile(file);
            final Manifest m = jarFile.getManifest();
            if (m == null) {
                throw new BuildException(file + " doesn't have a MANIFEST");
            }
            return m;
        }
        catch (IOException ioe) {
            throw new BuildException(ioe.getMessage(), ioe);
        }
    }
}
