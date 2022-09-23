// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.apache.log4j.helpers.ThreadLocalMap;

public class MDCFriend
{
    public static void fixForJava9() {
        if (MDC.mdc.tlm == null) {
            MDC.mdc.tlm = new ThreadLocalMap();
            MDC.mdc.java1 = false;
            setRemoveMethod(MDC.mdc);
        }
    }
    
    private static void setRemoveMethod(final MDC mdc) {
        try {
            final Method removeMethod = ThreadLocal.class.getMethod("remove", (Class<?>[])new Class[0]);
            final Field removeMethodField = MDC.class.getDeclaredField("removeMethod");
            removeMethodField.setAccessible(true);
            removeMethodField.set(mdc, removeMethod);
        }
        catch (NoSuchMethodException e) {}
        catch (SecurityException e2) {}
        catch (NoSuchFieldException e3) {}
        catch (IllegalArgumentException e4) {}
        catch (IllegalAccessException ex) {}
    }
}
