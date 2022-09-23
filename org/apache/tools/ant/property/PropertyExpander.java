// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.property;

import java.text.ParsePosition;
import org.apache.tools.ant.PropertyHelper;

public interface PropertyExpander extends PropertyHelper.Delegate
{
    String parsePropertyName(final String p0, final ParsePosition p1, final ParseNextProperty p2);
}
