// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.extension;

import java.util.jar.Manifest;
import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.extension.resolvers.AntResolver;
import org.apache.tools.ant.taskdefs.optional.extension.resolvers.URLResolver;
import org.apache.tools.ant.taskdefs.optional.extension.resolvers.LocationResolver;
import java.util.ArrayList;
import org.apache.tools.ant.Task;

public class JarLibResolveTask extends Task
{
    private String propertyName;
    private Extension requiredExtension;
    private final ArrayList resolvers;
    private boolean checkExtension;
    private boolean failOnError;
    
    public JarLibResolveTask() {
        this.resolvers = new ArrayList();
        this.checkExtension = true;
        this.failOnError = true;
    }
    
    public void setProperty(final String property) {
        this.propertyName = property;
    }
    
    public void setCheckExtension(final boolean checkExtension) {
        this.checkExtension = checkExtension;
    }
    
    public void setFailOnError(final boolean failOnError) {
        this.failOnError = failOnError;
    }
    
    public void addConfiguredLocation(final LocationResolver loc) {
        this.resolvers.add(loc);
    }
    
    public void addConfiguredUrl(final URLResolver url) {
        this.resolvers.add(url);
    }
    
    public void addConfiguredAnt(final AntResolver ant) {
        this.resolvers.add(ant);
    }
    
    public void addConfiguredExtension(final ExtensionAdapter extension) {
        if (null != this.requiredExtension) {
            final String message = "Can not specify extension to resolve multiple times.";
            throw new BuildException("Can not specify extension to resolve multiple times.");
        }
        this.requiredExtension = extension.toExtension();
    }
    
    @Override
    public void execute() throws BuildException {
        this.validate();
        this.getProject().log("Resolving extension: " + this.requiredExtension, 3);
        final String candidate = this.getProject().getProperty(this.propertyName);
        if (null == candidate) {
            for (int size = this.resolvers.size(), i = 0; i < size; ++i) {
                final ExtensionResolver resolver = this.resolvers.get(i);
                this.getProject().log("Searching for extension using Resolver:" + resolver, 3);
                try {
                    final File file = resolver.resolve(this.requiredExtension, this.getProject());
                    try {
                        this.checkExtension(file);
                        return;
                    }
                    catch (BuildException be) {
                        final String message = "File " + file + " returned by " + "resolver failed to satisfy extension due to: " + be.getMessage();
                        this.getProject().log(message, 1);
                    }
                }
                catch (BuildException be2) {
                    final String message2 = "Failed to resolve extension to file using resolver " + resolver + " due to: " + be2;
                    this.getProject().log(message2, 1);
                }
            }
            this.missingExtension();
            return;
        }
        final String message3 = "Property Already set to: " + candidate;
        if (this.failOnError) {
            throw new BuildException(message3);
        }
        this.getProject().log(message3, 0);
    }
    
    private void missingExtension() {
        final String message = "Unable to resolve extension to a file";
        if (this.failOnError) {
            throw new BuildException("Unable to resolve extension to a file");
        }
        this.getProject().log("Unable to resolve extension to a file", 0);
    }
    
    private void checkExtension(final File file) {
        if (!file.exists()) {
            throw new BuildException("File " + file + " does not exist");
        }
        if (!file.isFile()) {
            throw new BuildException("File " + file + " is not a file");
        }
        if (!this.checkExtension) {
            this.getProject().log("Setting property to " + file + " without verifying library satisfies extension", 3);
            this.setLibraryProperty(file);
            return;
        }
        this.getProject().log("Checking file " + file + " to see if it satisfies extension", 3);
        final Manifest manifest = ExtensionUtil.getManifest(file);
        final Extension[] extensions = Extension.getAvailable(manifest);
        for (int i = 0; i < extensions.length; ++i) {
            final Extension extension = extensions[i];
            if (extension.isCompatibleWith(this.requiredExtension)) {
                this.setLibraryProperty(file);
                return;
            }
        }
        final String message = "File " + file + " skipped as it " + "does not satisfy extension";
        this.getProject().log(message, 3);
        throw new BuildException(message);
    }
    
    private void setLibraryProperty(final File file) {
        this.getProject().setNewProperty(this.propertyName, file.getAbsolutePath());
    }
    
    private void validate() throws BuildException {
        if (null == this.propertyName) {
            final String message = "Property attribute must be specified.";
            throw new BuildException("Property attribute must be specified.");
        }
        if (null == this.requiredExtension) {
            final String message = "Extension element must be specified.";
            throw new BuildException("Extension element must be specified.");
        }
    }
}
