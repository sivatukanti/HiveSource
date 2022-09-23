// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.selectors.modifiedselector;

import java.io.File;

public interface Algorithm
{
    boolean isValid();
    
    String getValue(final File p0);
}
