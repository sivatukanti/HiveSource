// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util.facade;

import org.apache.tools.ant.types.Commandline;

public class ImplementationSpecificArgument extends Commandline.Argument
{
    private String impl;
    
    public void setImplementation(final String impl) {
        this.impl = impl;
    }
    
    public final String[] getParts(final String chosenImpl) {
        if (this.impl == null || this.impl.equals(chosenImpl)) {
            return super.getParts();
        }
        return new String[0];
    }
}
