// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util.regexp;

import java.util.Vector;
import org.apache.tools.ant.BuildException;

public interface RegexpMatcher
{
    public static final int MATCH_DEFAULT = 0;
    public static final int MATCH_CASE_INSENSITIVE = 256;
    public static final int MATCH_MULTILINE = 4096;
    public static final int MATCH_SINGLELINE = 65536;
    
    void setPattern(final String p0) throws BuildException;
    
    String getPattern() throws BuildException;
    
    boolean matches(final String p0) throws BuildException;
    
    Vector getGroups(final String p0) throws BuildException;
    
    boolean matches(final String p0, final int p1) throws BuildException;
    
    Vector getGroups(final String p0, final int p1) throws BuildException;
}
