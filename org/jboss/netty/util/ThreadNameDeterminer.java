// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.util;

public interface ThreadNameDeterminer
{
    public static final ThreadNameDeterminer PROPOSED = new ThreadNameDeterminer() {
        public String determineThreadName(final String currentThreadName, final String proposedThreadName) throws Exception {
            return proposedThreadName;
        }
    };
    public static final ThreadNameDeterminer CURRENT = new ThreadNameDeterminer() {
        public String determineThreadName(final String currentThreadName, final String proposedThreadName) throws Exception {
            return null;
        }
    };
    
    String determineThreadName(final String p0, final String p1) throws Exception;
}
