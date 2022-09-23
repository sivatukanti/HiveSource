// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework;

import org.apache.curator.framework.api.TempGetDataBuilder;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import java.io.Closeable;

public interface CuratorTempFramework extends Closeable
{
    void close();
    
    CuratorTransaction inTransaction() throws Exception;
    
    TempGetDataBuilder getData() throws Exception;
}
