// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.cli;

import java.io.IOException;

public interface IHiveFileProcessor
{
    int processFile(final String p0) throws IOException;
}
