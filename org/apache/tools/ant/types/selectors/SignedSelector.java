// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.selectors;

import java.io.File;
import org.apache.tools.ant.taskdefs.condition.IsSigned;
import org.apache.tools.ant.types.DataType;

public class SignedSelector extends DataType implements FileSelector
{
    private IsSigned isSigned;
    
    public SignedSelector() {
        this.isSigned = new IsSigned();
    }
    
    public void setName(final String name) {
        this.isSigned.setName(name);
    }
    
    public boolean isSelected(final File basedir, final String filename, final File file) {
        if (file.isDirectory()) {
            return false;
        }
        this.isSigned.setProject(this.getProject());
        this.isSigned.setFile(file);
        return this.isSigned.eval();
    }
}
