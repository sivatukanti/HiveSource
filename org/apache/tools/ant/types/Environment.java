// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types;

import java.io.File;
import org.apache.tools.ant.BuildException;
import java.util.Vector;

public class Environment
{
    protected Vector<Variable> variables;
    
    public Environment() {
        this.variables = new Vector<Variable>();
    }
    
    public void addVariable(final Variable var) {
        this.variables.addElement(var);
    }
    
    public String[] getVariables() throws BuildException {
        if (this.variables.size() == 0) {
            return null;
        }
        final String[] result = new String[this.variables.size()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = this.variables.elementAt(i).getContent();
        }
        return result;
    }
    
    public Vector<Variable> getVariablesVector() {
        return this.variables;
    }
    
    public static class Variable
    {
        private String key;
        private String value;
        
        public void setKey(final String key) {
            this.key = key;
        }
        
        public void setValue(final String value) {
            this.value = value;
        }
        
        public String getKey() {
            return this.key;
        }
        
        public String getValue() {
            return this.value;
        }
        
        public void setPath(final Path path) {
            this.value = path.toString();
        }
        
        public void setFile(final File file) {
            this.value = file.getAbsolutePath();
        }
        
        public String getContent() throws BuildException {
            this.validate();
            final StringBuffer sb = new StringBuffer(this.key.trim());
            sb.append("=").append(this.value.trim());
            return sb.toString();
        }
        
        public void validate() {
            if (this.key == null || this.value == null) {
                throw new BuildException("key and value must be specified for environment variables.");
            }
        }
    }
}
