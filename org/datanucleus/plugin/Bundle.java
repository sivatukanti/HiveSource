// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.net.URL;

public class Bundle
{
    private final String symbolicName;
    private final String vendorName;
    private final String name;
    private final String version;
    private final URL manifestLocation;
    private List requireBundle;
    
    public Bundle(final String symbolicName, final String name, final String vendorName, final String version, final URL manifestLocation) {
        this.symbolicName = symbolicName;
        this.name = name;
        this.vendorName = vendorName;
        this.version = version;
        this.manifestLocation = manifestLocation;
    }
    
    public String getSymbolicName() {
        return this.symbolicName;
    }
    
    public String getVendorName() {
        return this.vendorName;
    }
    
    public String getVersion() {
        return this.version;
    }
    
    public URL getManifestLocation() {
        return this.manifestLocation;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setRequireBundle(final List requireBundle) {
        this.requireBundle = requireBundle;
    }
    
    public List getRequireBundle() {
        return this.requireBundle;
    }
    
    @Override
    public String toString() {
        return "Bundle [Symbolic Name]" + this.symbolicName + " [Version] " + this.version;
    }
    
    public static class BundleDescription
    {
        private String bundleSymbolicName;
        private Map parameters;
        
        public BundleDescription() {
            this.parameters = new HashMap();
        }
        
        public String getBundleSymbolicName() {
            return this.bundleSymbolicName;
        }
        
        public void setBundleSymbolicName(final String bundleSymbolicName) {
            this.bundleSymbolicName = bundleSymbolicName;
        }
        
        public String getParameter(final String name) {
            return this.parameters.get(name);
        }
        
        public void setParameter(final String name, final String value) {
            this.parameters.put(name, value);
        }
        
        public void setParameters(final Map parameters) {
            this.parameters.putAll(parameters);
        }
        
        @Override
        public String toString() {
            return "BundleDescription [Symbolic Name] " + this.bundleSymbolicName + " [Parameters] " + this.parameters;
        }
    }
    
    public static class BundleVersion
    {
        public int major;
        public int minor;
        public int micro;
        public String qualifier;
        
        public BundleVersion() {
            this.qualifier = "";
        }
        
        @Override
        public int hashCode() {
            return this.major ^ this.minor ^ this.micro ^ this.qualifier.hashCode();
        }
        
        @Override
        public boolean equals(final Object object) {
            return object != null && this.compareTo(object) == 0;
        }
        
        public int compareTo(final Object object) {
            if (object == this) {
                return 0;
            }
            final BundleVersion other = (BundleVersion)object;
            int result = this.major - other.major;
            if (result != 0) {
                return result;
            }
            result = this.minor - other.minor;
            if (result != 0) {
                return result;
            }
            result = this.micro - other.micro;
            if (result != 0) {
                return result;
            }
            return this.qualifier.compareTo(other.qualifier);
        }
        
        @Override
        public String toString() {
            return "" + this.major + "." + this.minor + "." + this.micro + ((this.qualifier.length() > 0) ? ("." + this.qualifier) : "");
        }
    }
    
    public static class BundleVersionRange
    {
        public BundleVersion floor;
        public BundleVersion ceiling;
        public boolean floor_inclusive;
        public boolean ceiling_inclusive;
        
        public BundleVersionRange() {
            this.floor_inclusive = true;
            this.ceiling_inclusive = false;
        }
        
        @Override
        public String toString() {
            return "Bundle VersionRange [Floor] " + this.floor + " inclusive:" + this.floor_inclusive + " [Ceiling] " + this.ceiling + " inclusive:" + this.ceiling_inclusive;
        }
    }
}
