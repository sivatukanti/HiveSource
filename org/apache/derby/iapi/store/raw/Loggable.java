// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw;

import org.apache.derby.iapi.util.ByteArray;
import java.io.IOException;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.LimitObjectInput;
import org.apache.derby.iapi.store.raw.log.LogInstant;
import org.apache.derby.iapi.services.io.Formatable;

public interface Loggable extends Formatable
{
    public static final int FIRST = 1;
    public static final int LAST = 2;
    public static final int COMPENSATION = 4;
    public static final int BI_LOG = 8;
    public static final int COMMIT = 16;
    public static final int ABORT = 32;
    public static final int PREPARE = 64;
    public static final int XA_NEEDLOCK = 128;
    public static final int RAWSTORE = 256;
    public static final int FILE_RESOURCE = 1024;
    public static final int CHECKSUM = 2048;
    
    void doMe(final Transaction p0, final LogInstant p1, final LimitObjectInput p2) throws StandardException, IOException;
    
    ByteArray getPreparedLog() throws StandardException;
    
    boolean needsRedo(final Transaction p0) throws StandardException;
    
    void releaseResource(final Transaction p0);
    
    int group();
}
