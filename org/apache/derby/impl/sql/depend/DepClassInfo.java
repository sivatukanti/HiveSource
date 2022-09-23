// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.depend;

import org.apache.derby.iapi.services.io.FormatableInstanceGetter;

public class DepClassInfo extends FormatableInstanceGetter
{
    public Object getNewInstance() {
        switch (this.fmtId) {
            case 359: {
                return new BasicProviderInfo();
            }
            default: {
                return null;
            }
        }
    }
}
