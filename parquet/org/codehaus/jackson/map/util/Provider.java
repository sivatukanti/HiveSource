// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.codehaus.jackson.map.util;

import java.util.Collection;

public interface Provider<T>
{
    Collection<T> provide();
}
