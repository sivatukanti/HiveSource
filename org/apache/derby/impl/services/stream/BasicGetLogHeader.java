// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.stream;

import java.util.Date;
import org.apache.derby.iapi.services.stream.PrintWriterGetHeader;

class BasicGetLogHeader implements PrintWriterGetHeader
{
    private boolean doThreadId;
    private boolean doTimeStamp;
    private String tag;
    
    BasicGetLogHeader(final boolean doThreadId, final boolean doTimeStamp, final String tag) {
        this.doThreadId = doThreadId;
        this.doTimeStamp = doTimeStamp;
        this.tag = tag;
    }
    
    public String getHeader() {
        final StringBuffer sb = new StringBuffer(48);
        if (this.tag != null) {
            sb.append(this.tag);
            sb.append(' ');
        }
        if (this.doTimeStamp) {
            sb.append(new Date());
            sb.append(' ');
        }
        if (this.doThreadId) {
            sb.append(Thread.currentThread().toString());
            sb.append(' ');
        }
        return sb.toString();
    }
}
