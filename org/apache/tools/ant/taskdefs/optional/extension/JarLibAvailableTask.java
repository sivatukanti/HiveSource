// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.extension;

import java.util.jar.Manifest;
import java.util.Iterator;
import org.apache.tools.ant.BuildException;
import java.util.Vector;
import java.io.File;
import org.apache.tools.ant.Task;

public class JarLibAvailableTask extends Task
{
    private File libraryFile;
    private final Vector extensionFileSets;
    private String propertyName;
    private ExtensionAdapter requiredExtension;
    
    public JarLibAvailableTask() {
        this.extensionFileSets = new Vector();
    }
    
    public void setProperty(final String property) {
        this.propertyName = property;
    }
    
    public void setFile(final File file) {
        this.libraryFile = file;
    }
    
    public void addConfiguredExtension(final ExtensionAdapter extension) {
        if (null != this.requiredExtension) {
            final String message = "Can not specify extension to search for multiple times.";
            throw new BuildException("Can not specify extension to search for multiple times.");
        }
        this.requiredExtension = extension;
    }
    
    public void addConfiguredExtensionSet(final ExtensionSet extensionSet) {
        this.extensionFileSets.addElement(extensionSet);
    }
    
    @Override
    public void execute() throws BuildException {
        this.validate();
        final Extension test = this.requiredExtension.toExtension();
        if (!this.extensionFileSets.isEmpty()) {
            for (final ExtensionSet extensionSet : this.extensionFileSets) {
                final Extension[] extensions = extensionSet.toExtensions(this.getProject());
                for (int i = 0; i < extensions.length; ++i) {
                    final Extension extension = extensions[i];
                    if (extension.isCompatibleWith(test)) {
                        this.getProject().setNewProperty(this.propertyName, "true");
                    }
                }
            }
        }
        else {
            final Manifest manifest = ExtensionUtil.getManifest(this.libraryFile);
            final Extension[] extensions2 = Extension.getAvailable(manifest);
            for (int j = 0; j < extensions2.length; ++j) {
                final Extension extension2 = extensions2[j];
                if (extension2.isCompatibleWith(test)) {
                    this.getProject().setNewProperty(this.propertyName, "true");
                }
            }
        }
    }
    
    private void validate() throws BuildException {
        if (null == this.requiredExtension) {
            final String message = "Extension element must be specified.";
            throw new BuildException("Extension element must be specified.");
        }
        if (null == this.libraryFile && this.extensionFileSets.isEmpty()) {
            final String message = "File attribute not specified.";
            throw new BuildException("File attribute not specified.");
        }
        if (null != this.libraryFile && !this.libraryFile.exists()) {
            final String message = "File '" + this.libraryFile + "' does not exist.";
            throw new BuildException(message);
        }
        if (null != this.libraryFile && !this.libraryFile.isFile()) {
            final String message = "'" + this.libraryFile + "' is not a file.";
            throw new BuildException(message);
        }
    }
}
