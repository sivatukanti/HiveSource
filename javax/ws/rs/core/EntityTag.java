// 
// Decompiled by Procyon v0.5.36
// 

package javax.ws.rs.core;

import javax.ws.rs.ext.RuntimeDelegate;

public class EntityTag
{
    private String value;
    private boolean weak;
    private static final RuntimeDelegate.HeaderDelegate<EntityTag> delegate;
    
    public EntityTag(final String value) {
        this(value, false);
    }
    
    public EntityTag(final String value, final boolean weak) {
        if (value == null) {
            throw new IllegalArgumentException("value==null");
        }
        this.value = value;
        this.weak = weak;
    }
    
    public static EntityTag valueOf(final String value) throws IllegalArgumentException {
        return EntityTag.delegate.fromString(value);
    }
    
    public boolean isWeak() {
        return this.weak;
    }
    
    public String getValue() {
        return this.value;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof EntityTag)) {
            return super.equals(obj);
        }
        final EntityTag other = (EntityTag)obj;
        return this.value.equals(other.getValue()) && this.weak == other.isWeak();
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + ((this.value != null) ? this.value.hashCode() : 0);
        hash = 17 * hash + (this.weak ? 1 : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        return EntityTag.delegate.toString(this);
    }
    
    static {
        delegate = RuntimeDelegate.getInstance().createHeaderDelegate(EntityTag.class);
    }
}
