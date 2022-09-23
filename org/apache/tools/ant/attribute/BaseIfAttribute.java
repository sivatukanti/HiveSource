// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.attribute;

import java.util.Iterator;
import org.apache.tools.ant.RuntimeConfigurable;
import java.util.HashMap;
import java.util.Map;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.ProjectComponent;

public abstract class BaseIfAttribute extends ProjectComponent implements EnableAttribute
{
    private boolean positive;
    
    public BaseIfAttribute() {
        this.positive = true;
    }
    
    protected void setPositive(final boolean positive) {
        this.positive = positive;
    }
    
    protected boolean isPositive() {
        return this.positive;
    }
    
    protected boolean convertResult(final boolean val) {
        return this.positive ? val : (!val);
    }
    
    protected Map getParams(final UnknownElement el) {
        final Map ret = new HashMap();
        final RuntimeConfigurable rc = el.getWrapper();
        final Map attributes = rc.getAttributeMap();
        for (final Map.Entry entry : attributes.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            if (key.startsWith("ant-attribute:param")) {
                final int pos = key.lastIndexOf(58);
                ret.put(key.substring(pos + 1), el.getProject().replaceProperties(value));
            }
        }
        return ret;
    }
}
