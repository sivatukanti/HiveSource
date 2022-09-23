// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.columnar;

import java.io.IOException;

public interface LazyDecompressionCallback
{
    byte[] decompress() throws IOException;
}
