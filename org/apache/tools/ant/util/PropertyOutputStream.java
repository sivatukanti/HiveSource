// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import org.apache.tools.ant.Project;
import java.io.ByteArrayOutputStream;

public class PropertyOutputStream extends ByteArrayOutputStream
{
    private Project project;
    private String property;
    private boolean trim;
    
    public PropertyOutputStream(final Project p, final String s) {
        this(p, s, true);
    }
    
    public PropertyOutputStream(final Project p, final String s, final boolean b) {
        this.project = p;
        this.property = s;
        this.trim = b;
    }
    
    @Override
    public void close() {
        if (this.project != null && this.property != null) {
            final String s = new String(this.toByteArray());
            this.project.setNewProperty(this.property, this.trim ? s.trim() : s);
        }
    }
}
