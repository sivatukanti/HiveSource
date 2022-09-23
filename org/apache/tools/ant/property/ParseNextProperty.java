// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.property;

import java.text.ParsePosition;
import org.apache.tools.ant.Project;

public interface ParseNextProperty
{
    Project getProject();
    
    Object parseNextProperty(final String p0, final ParsePosition p1);
}
