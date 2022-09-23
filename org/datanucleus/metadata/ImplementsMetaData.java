// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.ClassLoaderResolver;
import java.util.ArrayList;
import java.util.List;

public class ImplementsMetaData extends MetaData
{
    protected String name;
    protected final List<PropertyMetaData> properties;
    
    public ImplementsMetaData(final String name) {
        this.properties = new ArrayList<PropertyMetaData>();
        this.name = name;
    }
    
    public synchronized void populate(final ClassLoaderResolver clr, final ClassLoader primary, final MetaDataManager mmgr) {
        try {
            clr.classForName(this.name);
        }
        catch (ClassNotResolvedException cnre) {
            try {
                final String clsName = ClassUtils.createFullClassName(((ClassMetaData)this.parent).getPackageName(), this.name);
                clr.classForName(clsName);
                this.name = clsName;
            }
            catch (ClassNotResolvedException cnre2) {
                NucleusLogger.METADATA.error(ImplementsMetaData.LOCALISER.msg("044097", ((ClassMetaData)this.parent).getFullClassName(), this.name));
                throw new InvalidClassMetaDataException(ImplementsMetaData.LOCALISER, "044097", ((ClassMetaData)this.parent).getFullClassName(), this.name);
            }
        }
        this.setPopulated();
    }
    
    public String getName() {
        return this.name;
    }
    
    public void addProperty(final PropertyMetaData pmd) {
        if (pmd == null) {
            return;
        }
        this.properties.add(pmd);
        pmd.parent = this;
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<implements name=\"" + this.name + "\">\n");
        for (int i = 0; i < this.properties.size(); ++i) {
            final PropertyMetaData pmd = this.properties.get(i);
            sb.append(pmd.toString(prefix + indent, indent));
        }
        sb.append(super.toString(prefix + indent, indent));
        sb.append(prefix + "</implements>\n");
        return sb.toString();
    }
}
