// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.ClassConstants;
import java.util.HashSet;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.ClassLoaderResolver;
import java.util.Iterator;
import java.util.Collection;
import org.datanucleus.util.Localiser;
import java.io.Serializable;

public class MetaData implements Serializable
{
    protected static final Localiser LOCALISER;
    public static final int METADATA_CREATED_STATE = 0;
    public static final int METADATA_POPULATED_STATE = 1;
    public static final int METADATA_INITIALISED_STATE = 2;
    public static final int METADATA_USED_STATE = 3;
    protected int metaDataState;
    protected MetaData parent;
    public static final String VENDOR_NAME = "datanucleus";
    public static final String VENDOR_NAME_OLD = "jpox";
    protected Collection<ExtensionMetaData> extensions;
    
    public MetaData() {
        this.metaDataState = 0;
        this.extensions = null;
    }
    
    public MetaData(final MetaData parent) {
        this.metaDataState = 0;
        this.extensions = null;
        this.parent = parent;
    }
    
    public MetaData(final MetaData parent, final MetaData copy) {
        this.metaDataState = 0;
        this.extensions = null;
        this.parent = parent;
        if (copy != null && copy.extensions != null) {
            for (final ExtensionMetaData extmd : copy.extensions) {
                this.addExtension(extmd.getVendorName(), extmd.getKey(), extmd.getValue());
            }
        }
    }
    
    public void initialise(final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        this.setInitialised();
    }
    
    void setInitialised() {
        this.metaDataState = 2;
    }
    
    void setPopulated() {
        this.metaDataState = 1;
    }
    
    void setUsed() {
        this.metaDataState = 3;
    }
    
    public void setParent(final MetaData md) {
        if (this.isPopulated() || this.isInitialised()) {
            throw new NucleusException("Cannot set parent of " + this + " since it is already populated/initialised");
        }
        this.parent = md;
    }
    
    public MetaData addExtension(final String vendor, final String key, final String value) {
        if (vendor == null || (this.isSupportedVendor(vendor) && (key == null || value == null))) {
            throw new InvalidMetaDataException(MetaData.LOCALISER, "044160", vendor, key, value);
        }
        if (this.isSupportedVendor(vendor) && this.hasExtension(key)) {
            this.removeExtension(key);
        }
        if (this.extensions == null) {
            this.extensions = new HashSet<ExtensionMetaData>(2);
        }
        this.extensions.add(new ExtensionMetaData(vendor, key, value));
        return this;
    }
    
    public MetaData addExtension(final String key, final String value) {
        return this.addExtension("datanucleus", key, value);
    }
    
    public ExtensionMetaData newExtensionMetaData(final String vendor, final String key, final String value) {
        if (vendor == null || (this.isSupportedVendor(vendor) && (key == null || value == null))) {
            throw new InvalidMetaDataException(MetaData.LOCALISER, "044160", vendor, key, value);
        }
        final ExtensionMetaData extmd = new ExtensionMetaData(vendor, key, value);
        if (this.extensions == null) {
            this.extensions = new HashSet<ExtensionMetaData>(2);
        }
        this.extensions.add(extmd);
        return extmd;
    }
    
    public MetaData removeExtension(final String key) {
        if (this.extensions == null) {
            return this;
        }
        final Iterator iter = this.extensions.iterator();
        while (iter.hasNext()) {
            final ExtensionMetaData ex = iter.next();
            if (ex.getKey().equals(key) && this.isSupportedVendor(ex.getVendorName())) {
                iter.remove();
                break;
            }
        }
        return this;
    }
    
    public MetaData getParent() {
        return this.parent;
    }
    
    public boolean isPopulated() {
        return this.metaDataState >= 1;
    }
    
    public boolean isInitialised() {
        return this.metaDataState >= 2;
    }
    
    public boolean isUsed() {
        return this.metaDataState == 3;
    }
    
    public int getNoOfExtensions() {
        if (this.extensions == null) {
            return 0;
        }
        return this.extensions.size();
    }
    
    public void assertIfInitialised() {
        if (this.isInitialised()) {
            throw new NucleusException("MetaData is already initialised so attribute cannot be set.");
        }
    }
    
    public ExtensionMetaData[] getExtensions() {
        if (this.extensions == null || this.extensions.size() == 0) {
            return null;
        }
        return this.extensions.toArray(new ExtensionMetaData[this.extensions.size()]);
    }
    
    public boolean hasExtension(final String key) {
        if (this.extensions == null || key == null) {
            return false;
        }
        for (final ExtensionMetaData ex : this.extensions) {
            if (ex.getKey().equals(key) && this.isSupportedVendor(ex.getVendorName())) {
                return true;
            }
        }
        return false;
    }
    
    public String getValueForExtension(final String key) {
        if (this.extensions == null || key == null) {
            return null;
        }
        for (final ExtensionMetaData ex : this.extensions) {
            if (ex.getKey().equals(key) && this.isSupportedVendor(ex.getVendorName())) {
                return ex.getValue();
            }
        }
        return null;
    }
    
    public String[] getValuesForExtension(final String key) {
        if (this.extensions == null || key == null) {
            return null;
        }
        for (final ExtensionMetaData ex : this.extensions) {
            if (ex.getKey().equals(key) && this.isSupportedVendor(ex.getVendorName())) {
                return MetaDataUtils.getInstance().getValuesForCommaSeparatedAttribute(ex.getValue());
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return this.toString("", "");
    }
    
    public String toString(final String prefix, final String indent) {
        if (this.extensions == null || this.extensions.size() == 0) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        for (final ExtensionMetaData ex : this.extensions) {
            sb.append(prefix).append(ex.toString()).append("\n");
        }
        return sb.toString();
    }
    
    private boolean isSupportedVendor(final String vendorName) {
        return vendorName.equalsIgnoreCase("datanucleus") || vendorName.equalsIgnoreCase("jpox");
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
