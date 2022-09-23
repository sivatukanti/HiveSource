// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import java.io.IOException;

public class ImmutableResourceException extends IOException
{
    private static final long serialVersionUID = 1L;
    
    public ImmutableResourceException() {
    }
    
    public ImmutableResourceException(final String s) {
        super(s);
    }
}
