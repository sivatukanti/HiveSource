// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.data;

import org.apache.derby.iapi.services.io.DynamicByteArrayOutputStream;
import org.apache.derby.iapi.error.StandardException;

public class LongColumnException extends StandardException
{
    protected DynamicByteArrayOutputStream logBuffer;
    protected int nextColumn;
    protected int realSpaceOnPage;
    protected Object column;
    
    public LongColumnException() {
        super("lngcl.U");
    }
    
    public void setColumn(final Object column) {
        this.column = column;
    }
    
    public void setExceptionInfo(final DynamicByteArrayOutputStream logBuffer, final int nextColumn, final int realSpaceOnPage) {
        this.logBuffer = logBuffer;
        this.nextColumn = nextColumn;
        this.realSpaceOnPage = realSpaceOnPage;
    }
    
    public Object getColumn() {
        return this.column;
    }
    
    public DynamicByteArrayOutputStream getLogBuffer() {
        return this.logBuffer;
    }
    
    public int getNextColumn() {
        return this.nextColumn;
    }
    
    public int getRealSpaceOnPage() {
        return this.realSpaceOnPage;
    }
}
