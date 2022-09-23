// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.resources;

import org.apache.tools.ant.util.PropertyOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.Project;
import java.io.InputStream;
import org.apache.tools.ant.types.Resource;

public class PropertyResource extends Resource
{
    private static final int PROPERTY_MAGIC;
    private static final InputStream UNSET;
    
    public PropertyResource() {
    }
    
    public PropertyResource(final Project p, final String n) {
        super(n);
        this.setProject(p);
    }
    
    public String getValue() {
        if (this.isReference()) {
            return ((PropertyResource)this.getCheckedRef()).getValue();
        }
        final Project p = this.getProject();
        return (p == null) ? null : p.getProperty(this.getName());
    }
    
    public Object getObjectValue() {
        if (this.isReference()) {
            return ((PropertyResource)this.getCheckedRef()).getObjectValue();
        }
        final Project p = this.getProject();
        return (p == null) ? null : PropertyHelper.getProperty(p, this.getName());
    }
    
    @Override
    public boolean isExists() {
        if (this.isReferenceOrProxy()) {
            return this.getReferencedOrProxied().isExists();
        }
        return this.getObjectValue() != null;
    }
    
    @Override
    public long getSize() {
        if (this.isReferenceOrProxy()) {
            return this.getReferencedOrProxied().getSize();
        }
        final Object o = this.getObjectValue();
        return (o == null) ? 0L : String.valueOf(o).length();
    }
    
    @Override
    public boolean equals(final Object o) {
        return super.equals(o) || (this.isReferenceOrProxy() && this.getReferencedOrProxied().equals(o));
    }
    
    @Override
    public int hashCode() {
        if (this.isReferenceOrProxy()) {
            return this.getReferencedOrProxied().hashCode();
        }
        return super.hashCode() * PropertyResource.PROPERTY_MAGIC;
    }
    
    @Override
    public String toString() {
        if (this.isReferenceOrProxy()) {
            return this.getReferencedOrProxied().toString();
        }
        return this.getValue();
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        if (this.isReferenceOrProxy()) {
            return this.getReferencedOrProxied().getInputStream();
        }
        final Object o = this.getObjectValue();
        return (o == null) ? PropertyResource.UNSET : new ByteArrayInputStream(String.valueOf(o).getBytes());
    }
    
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (this.isReferenceOrProxy()) {
            return this.getReferencedOrProxied().getOutputStream();
        }
        if (this.isExists()) {
            throw new ImmutableResourceException();
        }
        return new PropertyOutputStream(this.getProject(), this.getName());
    }
    
    protected boolean isReferenceOrProxy() {
        return this.isReference() || this.getObjectValue() instanceof Resource;
    }
    
    protected Resource getReferencedOrProxied() {
        if (this.isReference()) {
            return this.getCheckedRef(Resource.class, "resource");
        }
        final Object o = this.getObjectValue();
        if (o instanceof Resource) {
            return (Resource)o;
        }
        throw new IllegalStateException("This PropertyResource does not reference or proxy another Resource");
    }
    
    static {
        PROPERTY_MAGIC = Resource.getMagicNumber("PropertyResource".getBytes());
        UNSET = new InputStream() {
            @Override
            public int read() {
                return -1;
            }
        };
    }
}
