// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.server.replay;

public interface ReplayCheckService
{
    boolean checkReplay(final String p0, final String p1, final long p2, final int p3);
}
