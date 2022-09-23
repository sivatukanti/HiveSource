// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.selectors.modifiedselector;

import java.io.Reader;
import org.apache.tools.ant.util.FileUtils;
import java.io.FileReader;
import java.io.File;

public class HashvalueAlgorithm implements Algorithm
{
    public boolean isValid() {
        return true;
    }
    
    public String getValue(final File file) {
        Reader r = null;
        try {
            if (!file.canRead()) {
                return null;
            }
            r = new FileReader(file);
            final int hash = FileUtils.readFully(r).hashCode();
            return Integer.toString(hash);
        }
        catch (Exception e) {
            return null;
        }
        finally {
            FileUtils.close(r);
        }
    }
    
    @Override
    public String toString() {
        return "HashvalueAlgorithm";
    }
}
