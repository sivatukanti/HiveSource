// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.reflect;

import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.loader.GeneratedByteCode;
import org.apache.derby.iapi.services.loader.GeneratedMethod;

class DirectCall implements GeneratedMethod
{
    private final int which;
    
    DirectCall(final int which) {
        this.which = which;
    }
    
    public Object invoke(final Object o) throws StandardException {
        try {
            final GeneratedByteCode generatedByteCode = (GeneratedByteCode)o;
            switch (this.which) {
                case 0: {
                    return generatedByteCode.e0();
                }
                case 1: {
                    return generatedByteCode.e1();
                }
                case 2: {
                    return generatedByteCode.e2();
                }
                case 3: {
                    return generatedByteCode.e3();
                }
                case 4: {
                    return generatedByteCode.e4();
                }
                case 5: {
                    return generatedByteCode.e5();
                }
                case 6: {
                    return generatedByteCode.e6();
                }
                case 7: {
                    return generatedByteCode.e7();
                }
                case 8: {
                    return generatedByteCode.e8();
                }
                case 9: {
                    return generatedByteCode.e9();
                }
                default: {
                    return null;
                }
            }
        }
        catch (StandardException ex) {
            throw ex;
        }
        catch (Throwable t) {
            throw StandardException.unexpectedUserException(t);
        }
    }
}
