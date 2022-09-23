// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.email;

public class Header
{
    private String name;
    private String value;
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    public String getValue() {
        return this.value;
    }
}
