// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.model;

public class MPrincipalDesc
{
    private String name;
    private String type;
    
    public MPrincipalDesc() {
    }
    
    public MPrincipalDesc(final String name, final String type) {
        this.name = name;
        this.type = type;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    @Override
    public int hashCode() {
        return this.type.hashCode() + this.name.hashCode();
    }
    
    @Override
    public boolean equals(final Object object) {
        final MPrincipalDesc another = (MPrincipalDesc)object;
        return this.type.equals(another.type) && this.name.equals(another.name);
    }
}
