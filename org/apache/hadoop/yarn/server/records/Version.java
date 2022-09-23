// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
@InterfaceStability.Unstable
public abstract class Version
{
    public static Version newInstance(final int majorVersion, final int minorVersion) {
        final Version version = Records.newRecord(Version.class);
        version.setMajorVersion(majorVersion);
        version.setMinorVersion(minorVersion);
        return version;
    }
    
    public abstract int getMajorVersion();
    
    public abstract void setMajorVersion(final int p0);
    
    public abstract int getMinorVersion();
    
    public abstract void setMinorVersion(final int p0);
    
    @Override
    public String toString() {
        return this.getMajorVersion() + "." + this.getMinorVersion();
    }
    
    public boolean isCompatibleTo(final Version version) {
        return this.getMajorVersion() == version.getMajorVersion();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + this.getMajorVersion();
        result = 31 * result + this.getMinorVersion();
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final Version other = (Version)obj;
        return this.getMajorVersion() == other.getMajorVersion() && this.getMinorVersion() == other.getMinorVersion();
    }
}
