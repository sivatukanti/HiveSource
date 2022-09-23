// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.types;

import org.apache.derby.iapi.services.io.FormatableInstanceGetter;

public class DTSClassInfo extends FormatableInstanceGetter
{
    public Object getNewInstance() {
        return DataValueFactoryImpl.getNullDVDWithUCS_BASICcollation(this.fmtId);
    }
}
