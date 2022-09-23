// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.htrace.shaded.fasterxml.jackson.databind;

import java.io.Serializable;

public class PropertyMetadata implements Serializable
{
    private static final long serialVersionUID = -1L;
    public static final PropertyMetadata STD_REQUIRED;
    public static final PropertyMetadata STD_OPTIONAL;
    public static final PropertyMetadata STD_REQUIRED_OR_OPTIONAL;
    protected final Boolean _required;
    protected final String _description;
    protected final Integer _index;
    
    @Deprecated
    protected PropertyMetadata(final Boolean req, final String desc) {
        this(req, desc, null);
    }
    
    protected PropertyMetadata(final Boolean req, final String desc, final Integer index) {
        this._required = req;
        this._description = desc;
        this._index = index;
    }
    
    @Deprecated
    public static PropertyMetadata construct(final boolean req, final String desc) {
        return construct(req, desc, null);
    }
    
    public static PropertyMetadata construct(final boolean req, final String desc, final Integer index) {
        PropertyMetadata md = req ? PropertyMetadata.STD_REQUIRED : PropertyMetadata.STD_OPTIONAL;
        if (desc != null) {
            md = md.withDescription(desc);
        }
        if (index != null) {
            md = md.withIndex(index);
        }
        return md;
    }
    
    protected Object readResolve() {
        if (this._description != null || this._index != null) {
            return this;
        }
        if (this._required == null) {
            return PropertyMetadata.STD_REQUIRED_OR_OPTIONAL;
        }
        return this._required ? PropertyMetadata.STD_REQUIRED : PropertyMetadata.STD_OPTIONAL;
    }
    
    public PropertyMetadata withDescription(final String desc) {
        return new PropertyMetadata(this._required, desc, this._index);
    }
    
    public PropertyMetadata withIndex(final Integer index) {
        return new PropertyMetadata(this._required, this._description, index);
    }
    
    public PropertyMetadata withRequired(final Boolean b) {
        if (b == null) {
            if (this._required == null) {
                return this;
            }
        }
        else if (this._required != null && this._required == (boolean)b) {
            return this;
        }
        return new PropertyMetadata(b, this._description, this._index);
    }
    
    public String getDescription() {
        return this._description;
    }
    
    public boolean isRequired() {
        return this._required != null && this._required;
    }
    
    public Boolean getRequired() {
        return this._required;
    }
    
    public Integer getIndex() {
        return this._index;
    }
    
    public boolean hasIndex() {
        return this._index != null;
    }
    
    static {
        STD_REQUIRED = new PropertyMetadata(Boolean.TRUE, null, null);
        STD_OPTIONAL = new PropertyMetadata(Boolean.FALSE, null, null);
        STD_REQUIRED_OR_OPTIONAL = new PropertyMetadata(null, null, null);
    }
}
