// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.mappers;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileNameMapper;

public class CutDirsMapper implements FileNameMapper
{
    private int dirs;
    
    public CutDirsMapper() {
        this.dirs = 0;
    }
    
    public void setDirs(final int dirs) {
        this.dirs = dirs;
    }
    
    public void setFrom(final String ignore) {
    }
    
    public void setTo(final String ignore) {
    }
    
    public String[] mapFileName(final String sourceFileName) {
        if (this.dirs <= 0) {
            throw new BuildException("dirs must be set to a positive number");
        }
        final char fileSep = File.separatorChar;
        final String fileSepCorrected = sourceFileName.replace('/', fileSep).replace('\\', fileSep);
        int nthMatch = fileSepCorrected.indexOf(fileSep);
        for (int n = 1; nthMatch > -1 && n < this.dirs; nthMatch = fileSepCorrected.indexOf(fileSep, nthMatch + 1), ++n) {}
        if (nthMatch == -1) {
            return null;
        }
        return new String[] { sourceFileName.substring(nthMatch + 1) };
    }
}
