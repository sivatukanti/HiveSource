// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jna.platform.unix;

public interface LibCAPI extends Reboot, Resource
{
    public static final int HOST_NAME_MAX = 255;
    
    int getuid();
    
    int geteuid();
    
    int getgid();
    
    int getegid();
    
    int setuid(final int p0);
    
    int seteuid(final int p0);
    
    int setgid(final int p0);
    
    int setegid(final int p0);
    
    int gethostname(final byte[] p0, final int p1);
    
    int sethostname(final String p0, final int p1);
    
    int getdomainname(final byte[] p0, final int p1);
    
    int setdomainname(final String p0, final int p1);
    
    String getenv(final String p0);
    
    int setenv(final String p0, final String p1, final int p2);
    
    int unsetenv(final String p0);
    
    int getloadavg(final double[] p0, final int p1);
}
