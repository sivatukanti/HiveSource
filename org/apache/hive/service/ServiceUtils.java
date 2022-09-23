// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service;

public class ServiceUtils
{
    public static int indexOfDomainMatch(final String userName) {
        if (userName == null) {
            return -1;
        }
        final int idx = userName.indexOf(47);
        final int idx2 = userName.indexOf(64);
        int endIdx = Math.min(idx, idx2);
        if (endIdx == -1) {
            endIdx = Math.max(idx, idx2);
        }
        return endIdx;
    }
}
