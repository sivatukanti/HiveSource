// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util.regexp;

import org.apache.tools.ant.BuildException;

public interface Regexp extends RegexpMatcher
{
    public static final int REPLACE_FIRST = 1;
    public static final int REPLACE_ALL = 16;
    
    String substitute(final String p0, final String p1, final int p2) throws BuildException;
}
