// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby;

public class KOptionInfo
{
    private String name;
    private KOptionGroup group;
    private KOptionType type;
    private String description;
    private Object value;
    
    public KOptionInfo(final String name, final String description) {
        this(name, description, KOptionType.NOV);
    }
    
    public KOptionInfo(final String name, final String description, final KOptionType type) {
        this(name, description, null, type);
    }
    
    public KOptionInfo(final String name, final String description, final KOptionGroup group) {
        this(name, description, group, KOptionType.NOV);
    }
    
    public KOptionInfo(final String name, final String description, final KOptionGroup group, final KOptionType type) {
        this.name = name;
        this.description = description;
        this.group = group;
        this.type = type;
    }
    
    public void setType(final KOptionType type) {
        this.type = type;
    }
    
    public KOptionType getType() {
        return this.type;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setValue(final Object value) {
        this.value = value;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    public void setGroup(final KOptionGroup group) {
        this.group = group;
    }
    
    public KOptionGroup getGroup() {
        return this.group;
    }
}
