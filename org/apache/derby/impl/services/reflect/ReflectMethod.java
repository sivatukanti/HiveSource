// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.reflect;

import java.lang.reflect.InvocationTargetException;
import org.apache.derby.iapi.error.StandardException;
import java.lang.reflect.Method;
import org.apache.derby.iapi.services.loader.GeneratedMethod;

class ReflectMethod implements GeneratedMethod
{
    private final Method realMethod;
    
    ReflectMethod(final Method realMethod) {
        this.realMethod = realMethod;
    }
    
    public Object invoke(final Object obj) throws StandardException {
        Throwable targetException;
        try {
            return this.realMethod.invoke(obj, (Object[])null);
        }
        catch (IllegalAccessException ex) {
            targetException = ex;
        }
        catch (IllegalArgumentException ex2) {
            targetException = ex2;
        }
        catch (InvocationTargetException ex3) {
            targetException = ex3.getTargetException();
            if (targetException instanceof StandardException) {
                throw (StandardException)targetException;
            }
        }
        throw StandardException.unexpectedUserException(targetException);
    }
}
