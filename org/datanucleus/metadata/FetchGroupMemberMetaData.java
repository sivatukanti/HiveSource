// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.util.StringUtils;

public class FetchGroupMemberMetaData extends MetaData
{
    String name;
    int recursionDepth;
    boolean isProperty;
    
    public FetchGroupMemberMetaData(final MetaData parent, final String name) {
        super(parent);
        this.recursionDepth = 1;
        this.isProperty = false;
        this.name = name;
    }
    
    public void setProperty() {
        this.isProperty = true;
    }
    
    public boolean isProperty() {
        return this.isProperty;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getRecursionDepth() {
        return this.recursionDepth;
    }
    
    public void setRecursionDepth(final int depth) {
        this.recursionDepth = depth;
    }
    
    public void setRecursionDepth(final String depth) {
        if (!StringUtils.isWhitespace(depth)) {
            try {
                this.recursionDepth = Integer.parseInt(depth);
            }
            catch (NumberFormatException ex) {}
        }
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        if (this.recursionDepth != 1) {
            sb.append(prefix).append("<field name=\"" + this.name + "\" recursion-depth=\"" + this.recursionDepth + "\"/>\n");
        }
        else {
            sb.append(prefix).append("<field name=\"" + this.name + "\"/>\n");
        }
        return sb.toString();
    }
}
