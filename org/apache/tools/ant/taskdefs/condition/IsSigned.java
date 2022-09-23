// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import java.io.IOException;
import java.util.Enumeration;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import java.io.File;
import org.apache.tools.ant.types.DataType;

public class IsSigned extends DataType implements Condition
{
    private static final String SIG_START = "META-INF/";
    private static final String SIG_END = ".SF";
    private static final int SHORT_SIG_LIMIT = 8;
    private String name;
    private File file;
    
    public void setFile(final File file) {
        this.file = file;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public static boolean isSigned(final File zipFile, String name) throws IOException {
        ZipFile jarFile = null;
        try {
            jarFile = new ZipFile(zipFile);
            if (null == name) {
                final Enumeration entries = jarFile.getEntries();
                while (entries.hasMoreElements()) {
                    final String eName = entries.nextElement().getName();
                    if (eName.startsWith("META-INF/") && eName.endsWith(".SF")) {
                        return true;
                    }
                }
                return false;
            }
            name = replaceInvalidChars(name);
            final boolean shortSig = jarFile.getEntry("META-INF/" + name.toUpperCase() + ".SF") != null;
            boolean longSig = false;
            if (name.length() > 8) {
                longSig = (jarFile.getEntry("META-INF/" + name.substring(0, 8).toUpperCase() + ".SF") != null);
            }
            return shortSig || longSig;
        }
        finally {
            ZipFile.closeQuietly(jarFile);
        }
    }
    
    public boolean eval() {
        if (this.file == null) {
            throw new BuildException("The file attribute must be set.");
        }
        if (!this.file.exists()) {
            this.log("The file \"" + this.file.getAbsolutePath() + "\" does not exist.", 3);
            return false;
        }
        boolean r = false;
        try {
            r = isSigned(this.file, this.name);
        }
        catch (IOException e) {
            this.log("Got IOException reading file \"" + this.file.getAbsolutePath() + "\"" + e, 1);
        }
        if (r) {
            this.log("File \"" + this.file.getAbsolutePath() + "\" is signed.", 3);
        }
        return r;
    }
    
    private static String replaceInvalidChars(final String name) {
        final StringBuffer sb = new StringBuffer();
        final int len = name.length();
        boolean changes = false;
        for (int i = 0; i < len; ++i) {
            final char ch = name.charAt(i);
            if ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_".indexOf(ch) < 0) {
                sb.append("_");
                changes = true;
            }
            else {
                sb.append(ch);
            }
        }
        return changes ? sb.toString() : name;
    }
}
