// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.io;

import java.io.IOException;
import org.apache.hadoop.mapred.RecordReader;

public interface HiveIOExceptionHandler
{
    RecordReader<?, ?> handleRecordReaderCreationException(final Exception p0) throws IOException;
    
    void handleRecorReaderNextException(final Exception p0, final HiveIOExceptionNextHandleResult p1) throws IOException;
}
