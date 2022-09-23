// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.util.StringUtils;
import java.io.Serializable;

class ContainerComponent implements Serializable
{
    protected Boolean embedded;
    protected Boolean serialized;
    protected Boolean dependent;
    protected String type;
    protected AbstractClassMetaData classMetaData;
    
    public ContainerComponent() {
        this.type = "java.lang.Object";
    }
    
    public Boolean getEmbedded() {
        return this.embedded;
    }
    
    public void setEmbedded(final Boolean embedded) {
        this.embedded = embedded;
    }
    
    public Boolean getSerialized() {
        return this.serialized;
    }
    
    public void setSerialized(final Boolean serialized) {
        this.serialized = serialized;
    }
    
    public Boolean getDependent() {
        return this.dependent;
    }
    
    public void setDependent(final Boolean dependent) {
        this.dependent = dependent;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = (StringUtils.isWhitespace(type) ? null : type);
    }
    
    void populate(final String packageName, final ClassLoaderResolver clr, final ClassLoader primary, final MetaDataManager mmgr) {
        if (this.type != null) {
            if (!ClassUtils.isPrimitiveArrayType(this.type)) {
                if (!ClassUtils.isPrimitiveType(this.type)) {
                    try {
                        clr.classForName(this.type, primary, false);
                    }
                    catch (ClassNotResolvedException cnre) {
                        String name = ClassUtils.createFullClassName(packageName, this.type);
                        try {
                            clr.classForName(name, primary, false);
                            this.type = name;
                        }
                        catch (ClassNotResolvedException cnre2) {
                            name = ClassUtils.getJavaLangClassForType(this.type);
                            clr.classForName(name, primary, false);
                            this.type = name;
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public String toString() {
        return "Type=" + this.type + " embedded=" + this.embedded + " serialized=" + this.serialized + " dependent=" + this.dependent + " cmd=" + this.classMetaData;
    }
}
