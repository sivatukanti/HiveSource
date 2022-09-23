// 
// Decompiled by Procyon v0.5.36
// 

package parquet.org.apache.thrift;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

public class TEnumHelper
{
    public static TEnum getByValue(final Class<? extends TEnum> enumClass, final int value) {
        try {
            final Method method = enumClass.getMethod("findByValue", Integer.TYPE);
            return (TEnum)method.invoke(null, value);
        }
        catch (NoSuchMethodException nsme) {
            return null;
        }
        catch (IllegalAccessException iae) {
            return null;
        }
        catch (InvocationTargetException ite) {
            return null;
        }
    }
}
