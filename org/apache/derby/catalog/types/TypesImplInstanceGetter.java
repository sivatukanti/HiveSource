// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.catalog.types;

import org.apache.derby.iapi.services.io.FormatableInstanceGetter;

public class TypesImplInstanceGetter extends FormatableInstanceGetter
{
    public Object getNewInstance() {
        switch (this.fmtId) {
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 25:
            case 28:
            case 30:
            case 32:
            case 33:
            case 34:
            case 196:
            case 231:
            case 233:
            case 442:
            case 446:
            case 457: {
                return new BaseTypeIdImpl(this.fmtId);
            }
            case 198: {
                return new DecimalTypeIdImpl(false);
            }
            case 259: {
                return new OldRoutineType();
            }
            default: {
                return null;
            }
        }
    }
}
