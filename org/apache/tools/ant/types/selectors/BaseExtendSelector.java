// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.selectors;

import org.apache.tools.ant.BuildException;
import java.io.File;
import org.apache.tools.ant.types.Parameter;

public abstract class BaseExtendSelector extends BaseSelector implements ExtendFileSelector
{
    protected Parameter[] parameters;
    
    public BaseExtendSelector() {
        this.parameters = null;
    }
    
    public void setParameters(final Parameter[] parameters) {
        this.parameters = parameters;
    }
    
    protected Parameter[] getParameters() {
        return this.parameters;
    }
    
    @Override
    public abstract boolean isSelected(final File p0, final String p1, final File p2) throws BuildException;
}
