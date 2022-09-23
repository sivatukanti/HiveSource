// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.objectvaluegenerator;

import org.datanucleus.metadata.ExtensionMetaData;
import org.datanucleus.ExecutionContext;

public interface ObjectValueGenerator
{
    Object generate(final ExecutionContext p0, final Object p1, final ExtensionMetaData[] p2);
}
