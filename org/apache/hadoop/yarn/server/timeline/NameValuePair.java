// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.timeline;

import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class NameValuePair
{
    String name;
    Object value;
    
    public NameValuePair(final String name, final Object value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Object getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return "{ name: " + this.name + ", value: " + this.value + " }";
    }
}
