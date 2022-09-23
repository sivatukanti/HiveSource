// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.EnumeratedAttribute;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import org.apache.tools.ant.util.FileUtils;
import java.io.IOException;
import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import org.apache.tools.ant.BuildException;
import java.util.Enumeration;
import java.io.File;
import org.apache.tools.ant.Task;

public class ManifestTask extends Task
{
    public static final String VALID_ATTRIBUTE_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_";
    private Manifest nestedManifest;
    private File manifestFile;
    private Mode mode;
    private String encoding;
    private boolean mergeClassPaths;
    private boolean flattenClassPaths;
    
    public ManifestTask() {
        this.nestedManifest = new Manifest();
        this.mergeClassPaths = false;
        this.flattenClassPaths = false;
        (this.mode = new Mode()).setValue("replace");
    }
    
    public void addConfiguredSection(final Manifest.Section section) throws ManifestException {
        final Enumeration<String> attributeKeys = section.getAttributeKeys();
        while (attributeKeys.hasMoreElements()) {
            final Manifest.Attribute attribute = section.getAttribute(attributeKeys.nextElement());
            this.checkAttribute(attribute);
        }
        this.nestedManifest.addConfiguredSection(section);
    }
    
    public void addConfiguredAttribute(final Manifest.Attribute attribute) throws ManifestException {
        this.checkAttribute(attribute);
        this.nestedManifest.addConfiguredAttribute(attribute);
    }
    
    private void checkAttribute(final Manifest.Attribute attribute) throws BuildException {
        final String name = attribute.getName();
        char ch = name.charAt(0);
        if (ch == '-' || ch == '_') {
            throw new BuildException("Manifest attribute names must not start with '" + ch + "'.");
        }
        for (int i = 0; i < name.length(); ++i) {
            ch = name.charAt(i);
            if ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_".indexOf(ch) < 0) {
                throw new BuildException("Manifest attribute names must not contain '" + ch + "'");
            }
        }
    }
    
    public void setFile(final File f) {
        this.manifestFile = f;
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    public void setMode(final Mode m) {
        this.mode = m;
    }
    
    public void setMergeClassPathAttributes(final boolean b) {
        this.mergeClassPaths = b;
    }
    
    public void setFlattenAttributes(final boolean b) {
        this.flattenClassPaths = b;
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.manifestFile == null) {
            throw new BuildException("the file attribute is required");
        }
        final Manifest toWrite = Manifest.getDefaultManifest();
        Manifest current = null;
        BuildException error = null;
        if (this.manifestFile.exists()) {
            FileInputStream fis = null;
            InputStreamReader isr = null;
            try {
                fis = new FileInputStream(this.manifestFile);
                if (this.encoding == null) {
                    isr = new InputStreamReader(fis, "UTF-8");
                }
                else {
                    isr = new InputStreamReader(fis, this.encoding);
                }
                current = new Manifest(isr);
            }
            catch (ManifestException m) {
                error = new BuildException("Existing manifest " + this.manifestFile + " is invalid", m, this.getLocation());
            }
            catch (IOException e) {
                error = new BuildException("Failed to read " + this.manifestFile, e, this.getLocation());
            }
            finally {
                FileUtils.close(isr);
            }
        }
        final Enumeration<String> e2 = this.nestedManifest.getWarnings();
        while (e2.hasMoreElements()) {
            this.log("Manifest warning: " + e2.nextElement(), 1);
        }
        try {
            if (this.mode.getValue().equals("update") && this.manifestFile.exists()) {
                if (current != null) {
                    toWrite.merge(current, false, this.mergeClassPaths);
                }
                else if (error != null) {
                    throw error;
                }
            }
            toWrite.merge(this.nestedManifest, false, this.mergeClassPaths);
        }
        catch (ManifestException i) {
            throw new BuildException("Manifest is invalid", i, this.getLocation());
        }
        if (toWrite.equals(current)) {
            this.log("Manifest has not changed, do not recreate", 3);
            return;
        }
        PrintWriter w = null;
        try {
            final FileOutputStream fos = new FileOutputStream(this.manifestFile);
            final OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            w = new PrintWriter(osw);
            toWrite.write(w, this.flattenClassPaths);
            if (w.checkError()) {
                throw new IOException("Encountered an error writing manifest");
            }
        }
        catch (IOException e3) {
            throw new BuildException("Failed to write " + this.manifestFile, e3, this.getLocation());
        }
        finally {
            FileUtils.close(w);
        }
    }
    
    public static class Mode extends EnumeratedAttribute
    {
        @Override
        public String[] getValues() {
            return new String[] { "update", "replace" };
        }
    }
}
