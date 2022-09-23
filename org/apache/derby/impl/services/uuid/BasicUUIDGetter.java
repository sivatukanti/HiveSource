// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.uuid;

import org.apache.derby.iapi.services.io.FormatableInstanceGetter;

public class BasicUUIDGetter extends FormatableInstanceGetter
{
    public Object getNewInstance() {
        return new BasicUUID();
    }
}
