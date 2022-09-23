// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

import org.apache.derby.iapi.services.loader.InstanceGetter;

public abstract class FormatableInstanceGetter implements InstanceGetter
{
    protected int fmtId;
    
    public final void setFormatId(final int fmtId) {
        this.fmtId = fmtId;
    }
}
