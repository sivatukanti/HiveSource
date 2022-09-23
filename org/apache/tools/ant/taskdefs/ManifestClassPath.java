// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.io.UnsupportedEncodingException;
import org.apache.tools.ant.launch.Locator;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;
import java.io.File;
import org.apache.tools.ant.Task;

public class ManifestClassPath extends Task
{
    private String name;
    private File dir;
    private int maxParentLevels;
    private Path path;
    
    public ManifestClassPath() {
        this.maxParentLevels = 2;
    }
    
    @Override
    public void execute() {
        if (this.name == null) {
            throw new BuildException("Missing 'property' attribute!");
        }
        if (this.dir == null) {
            throw new BuildException("Missing 'jarfile' attribute!");
        }
        if (this.getProject().getProperty(this.name) != null) {
            throw new BuildException("Property '" + this.name + "' already set!");
        }
        if (this.path == null) {
            throw new BuildException("Missing nested <classpath>!");
        }
        final StringBuffer tooLongSb = new StringBuffer();
        for (int i = 0; i < this.maxParentLevels + 1; ++i) {
            tooLongSb.append("../");
        }
        final String tooLongPrefix = tooLongSb.toString();
        final FileUtils fileUtils = FileUtils.getFileUtils();
        this.dir = fileUtils.normalize(this.dir.getAbsolutePath());
        final String[] elements = this.path.list();
        final StringBuffer buffer = new StringBuffer();
        for (int j = 0; j < elements.length; ++j) {
            File pathEntry = new File(elements[j]);
            final String fullPath = pathEntry.getAbsolutePath();
            pathEntry = fileUtils.normalize(fullPath);
            String relPath = null;
            String canonicalPath = null;
            try {
                relPath = FileUtils.getRelativePath(this.dir, pathEntry);
                canonicalPath = pathEntry.getCanonicalPath();
                if (File.separatorChar != '/') {
                    canonicalPath = canonicalPath.replace(File.separatorChar, '/');
                }
            }
            catch (Exception e) {
                throw new BuildException("error trying to get the relative path from " + this.dir + " to " + fullPath, e);
            }
            if (relPath.equals(canonicalPath) || relPath.startsWith(tooLongPrefix)) {
                throw new BuildException("No suitable relative path from " + this.dir + " to " + fullPath);
            }
            if (pathEntry.isDirectory() && !relPath.endsWith("/")) {
                relPath += '/';
            }
            try {
                relPath = Locator.encodeURI(relPath);
            }
            catch (UnsupportedEncodingException exc) {
                throw new BuildException(exc);
            }
            buffer.append(relPath);
            buffer.append(' ');
        }
        this.getProject().setNewProperty(this.name, buffer.toString().trim());
    }
    
    public void setProperty(final String name) {
        this.name = name;
    }
    
    public void setJarFile(final File jarfile) {
        final File parent = jarfile.getParentFile();
        if (!parent.isDirectory()) {
            throw new BuildException("Jar's directory not found: " + parent);
        }
        this.dir = parent;
    }
    
    public void setMaxParentLevels(final int levels) {
        if (levels < 0) {
            throw new BuildException("maxParentLevels must not be a negative number");
        }
        this.maxParentLevels = levels;
    }
    
    public void addClassPath(final Path path) {
        this.path = path;
    }
}
