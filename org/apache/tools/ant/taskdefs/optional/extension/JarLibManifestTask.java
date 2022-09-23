// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.extension;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.apache.tools.ant.BuildException;
import java.util.ArrayList;
import java.io.File;
import org.apache.tools.ant.Task;

public final class JarLibManifestTask extends Task
{
    private static final String MANIFEST_VERSION = "1.0";
    private static final String CREATED_BY = "Created-By";
    private File destFile;
    private Extension extension;
    private final ArrayList dependencies;
    private final ArrayList optionals;
    private final ArrayList extraAttributes;
    
    public JarLibManifestTask() {
        this.dependencies = new ArrayList();
        this.optionals = new ArrayList();
        this.extraAttributes = new ArrayList();
    }
    
    public void setDestfile(final File destFile) {
        this.destFile = destFile;
    }
    
    public void addConfiguredExtension(final ExtensionAdapter extensionAdapter) throws BuildException {
        if (null != this.extension) {
            throw new BuildException("Can not have multiple extensions defined in one library.");
        }
        this.extension = extensionAdapter.toExtension();
    }
    
    public void addConfiguredDepends(final ExtensionSet extensionSet) {
        this.dependencies.add(extensionSet);
    }
    
    public void addConfiguredOptions(final ExtensionSet extensionSet) {
        this.optionals.add(extensionSet);
    }
    
    public void addConfiguredAttribute(final ExtraAttribute attribute) {
        this.extraAttributes.add(attribute);
    }
    
    @Override
    public void execute() throws BuildException {
        this.validate();
        final Manifest manifest = new Manifest();
        final Attributes attributes = manifest.getMainAttributes();
        attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        attributes.putValue("Created-By", "Apache Ant " + this.getProject().getProperty("ant.version"));
        this.appendExtraAttributes(attributes);
        if (null != this.extension) {
            Extension.addExtension(this.extension, attributes);
        }
        final ArrayList depends = this.toExtensions(this.dependencies);
        this.appendExtensionList(attributes, Extension.EXTENSION_LIST, "lib", depends.size());
        this.appendLibraryList(attributes, "lib", depends);
        final ArrayList option = this.toExtensions(this.optionals);
        this.appendExtensionList(attributes, Extension.OPTIONAL_EXTENSION_LIST, "opt", option.size());
        this.appendLibraryList(attributes, "opt", option);
        try {
            this.log("Generating manifest " + this.destFile.getAbsoluteFile(), 2);
            this.writeManifest(manifest);
        }
        catch (IOException ioe) {
            throw new BuildException(ioe.getMessage(), ioe);
        }
    }
    
    private void validate() throws BuildException {
        if (null == this.destFile) {
            throw new BuildException("Destfile attribute not specified.");
        }
        if (this.destFile.exists() && !this.destFile.isFile()) {
            throw new BuildException(this.destFile + " is not a file.");
        }
    }
    
    private void appendExtraAttributes(final Attributes attributes) {
        for (final ExtraAttribute attribute : this.extraAttributes) {
            attributes.putValue(attribute.getName(), attribute.getValue());
        }
    }
    
    private void writeManifest(final Manifest manifest) throws IOException {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(this.destFile);
            manifest.write(output);
            output.flush();
        }
        finally {
            if (null != output) {
                try {
                    output.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    private void appendLibraryList(final Attributes attributes, final String listPrefix, final ArrayList extensions) throws BuildException {
        for (int size = extensions.size(), i = 0; i < size; ++i) {
            final Extension ext = extensions.get(i);
            final String prefix = listPrefix + i + "-";
            Extension.addExtension(ext, prefix, attributes);
        }
    }
    
    private void appendExtensionList(final Attributes attributes, final Attributes.Name extensionKey, final String listPrefix, final int size) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size; ++i) {
            sb.append(listPrefix);
            sb.append(i);
            sb.append(' ');
        }
        attributes.put(extensionKey, sb.toString());
    }
    
    private ArrayList toExtensions(final ArrayList extensionSets) throws BuildException {
        final ArrayList results = new ArrayList();
        for (int size = extensionSets.size(), i = 0; i < size; ++i) {
            final ExtensionSet set = extensionSets.get(i);
            final Extension[] extensions = set.toExtensions(this.getProject());
            for (int j = 0; j < extensions.length; ++j) {
                results.add(extensions[j]);
            }
        }
        return results;
    }
}
