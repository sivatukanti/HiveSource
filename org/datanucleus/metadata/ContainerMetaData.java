// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.HashMap;
import org.datanucleus.ClassLoaderResolver;

public class ContainerMetaData extends MetaData
{
    Boolean allowNulls;
    
    public ContainerMetaData() {
        this.allowNulls = null;
    }
    
    public ContainerMetaData(final ContainerMetaData contmd) {
        super(null, contmd);
        this.allowNulls = null;
    }
    
    public void populate(final ClassLoaderResolver clr, final ClassLoader primary, final MetaDataManager mmgr) {
        if (this.parent != null && this.parent.hasExtension("allow-nulls")) {
            if (this.parent.getValueForExtension("allow-nulls").equalsIgnoreCase("true")) {
                this.allowNulls = Boolean.TRUE;
            }
            else if (this.parent.getValueForExtension("allow-nulls").equalsIgnoreCase("false")) {
                this.allowNulls = Boolean.FALSE;
            }
        }
        if (this.allowNulls == null) {
            final Class type = ((AbstractMemberMetaData)this.parent).getType();
            if (type.isArray()) {
                if (type.getComponentType().isPrimitive()) {
                    this.allowNulls = Boolean.FALSE;
                }
                else {
                    this.allowNulls = Boolean.TRUE;
                }
            }
            else if (type == HashMap.class) {
                this.allowNulls = Boolean.TRUE;
            }
            else if (type == Hashtable.class) {
                this.allowNulls = Boolean.FALSE;
            }
            else if (type == HashSet.class) {
                this.allowNulls = Boolean.TRUE;
            }
            else if (type == LinkedHashSet.class) {
                this.allowNulls = Boolean.TRUE;
            }
            else if (type == LinkedHashMap.class) {
                this.allowNulls = Boolean.TRUE;
            }
            else if (List.class.isAssignableFrom(type)) {
                this.allowNulls = Boolean.TRUE;
            }
        }
    }
    
    public Boolean allowNulls() {
        return this.allowNulls;
    }
    
    public AbstractMemberMetaData getMemberMetaData() {
        if (this.parent != null) {
            return (AbstractMemberMetaData)this.parent;
        }
        return null;
    }
    
    public String getFieldName() {
        if (this.parent != null) {
            return ((AbstractMemberMetaData)this.parent).getName();
        }
        return null;
    }
}
