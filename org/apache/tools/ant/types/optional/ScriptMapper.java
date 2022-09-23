// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.optional;

import java.util.ArrayList;
import org.apache.tools.ant.util.FileNameMapper;

public class ScriptMapper extends AbstractScriptComponent implements FileNameMapper
{
    private ArrayList<String> files;
    
    public void setFrom(final String from) {
    }
    
    public void setTo(final String to) {
    }
    
    public void clear() {
        this.files = new ArrayList<String>(1);
    }
    
    public void addMappedName(final String mapping) {
        this.files.add(mapping);
    }
    
    public String[] mapFileName(final String sourceFileName) {
        this.initScriptRunner();
        this.getRunner().addBean("source", sourceFileName);
        this.clear();
        this.executeScript("ant_mapper");
        if (this.files.size() == 0) {
            return null;
        }
        return this.files.toArray(new String[this.files.size()]);
    }
}
