// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.http;

import java.util.Map;

public interface FilterContainer
{
    void addFilter(final String p0, final String p1, final Map<String, String> p2);
    
    void addGlobalFilter(final String p0, final String p1, final Map<String, String> p2);
}
