// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public abstract class VersionUtil
{
    public static int compareVersions(final String version1, final String version2) {
        final ComparableVersion v1 = new ComparableVersion(version1);
        final ComparableVersion v2 = new ComparableVersion(version2);
        return v1.compareTo(v2);
    }
}
