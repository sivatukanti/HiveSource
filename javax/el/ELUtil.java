// 
// Decompiled by Procyon v0.5.36
// 

package javax.el;

import java.util.MissingResourceException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

class ELUtil
{
    private static ThreadLocal instance;
    
    private ELUtil() {
    }
    
    private static Map getCurrentInstance() {
        Map result = ELUtil.instance.get();
        if (null == result) {
            result = new HashMap();
            setCurrentInstance(result);
        }
        return result;
    }
    
    private static void setCurrentInstance(final Map context) {
        ELUtil.instance.set(context);
    }
    
    public static String getExceptionMessageString(final ELContext context, final String messageId) {
        return getExceptionMessageString(context, messageId, null);
    }
    
    public static String getExceptionMessageString(final ELContext context, final String messageId, final Object[] params) {
        String result = "";
        Locale locale = null;
        if (null == context || null == messageId) {
            return result;
        }
        if (null == (locale = context.getLocale())) {
            locale = Locale.getDefault();
        }
        if (null != locale) {
            final Map threadMap = getCurrentInstance();
            ResourceBundle rb = null;
            if (null == (rb = threadMap.get(locale.toString()))) {
                rb = ResourceBundle.getBundle("javax.el.PrivateMessages", locale);
                threadMap.put(locale.toString(), rb);
            }
            if (null != rb) {
                try {
                    result = rb.getString(messageId);
                    if (null != params) {
                        result = MessageFormat.format(result, params);
                    }
                }
                catch (IllegalArgumentException iae) {
                    result = "Can't get localized message: parameters to message appear to be incorrect.  Message to format: " + messageId;
                }
                catch (MissingResourceException mre) {
                    result = "Missing Resource in EL implementation: ???" + messageId + "???";
                }
                catch (Exception e) {
                    result = "Exception resolving message in EL implementation: ???" + messageId + "???";
                }
            }
        }
        return result;
    }
    
    static {
        ELUtil.instance = new ThreadLocal() {
            @Override
            protected Object initialValue() {
                return null;
            }
        };
    }
}
