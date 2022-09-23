// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types;

import java.net.URL;

public class ResourceLocation
{
    private String publicId;
    private String location;
    private URL base;
    
    public ResourceLocation() {
        this.publicId = null;
        this.location = null;
        this.base = null;
    }
    
    public void setPublicId(final String publicId) {
        this.publicId = publicId;
    }
    
    public void setLocation(final String location) {
        this.location = location;
    }
    
    public void setBase(final URL base) {
        this.base = base;
    }
    
    public String getPublicId() {
        return this.publicId;
    }
    
    public String getLocation() {
        return this.location;
    }
    
    public URL getBase() {
        return this.base;
    }
}
