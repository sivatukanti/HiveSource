// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.catalog;

import org.apache.derby.iapi.services.io.FormatableInstanceGetter;

public class CoreDDFinderClassInfo extends FormatableInstanceGetter
{
    public Object getNewInstance() {
        switch (this.fmtId) {
            case 135:
            case 136:
            case 137:
            case 145:
            case 208:
            case 226:
            case 273:
            case 320:
            case 325:
            case 371:
            case 461:
            case 462:
            case 463:
            case 471:
            case 472:
            case 473: {
                return new DDdependableFinder(this.fmtId);
            }
            case 393: {
                return new DDColumnDependableFinder(this.fmtId);
            }
            default: {
                return null;
            }
        }
    }
}
