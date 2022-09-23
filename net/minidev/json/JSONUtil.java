// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json;

import java.lang.reflect.Method;
import net.minidev.json.annotate.JsonIgnore;
import java.lang.reflect.Field;
import net.minidev.asm.FieldFilter;

public class JSONUtil
{
    public static final JsonSmartFieldFilter JSON_SMART_FIELD_FILTER;
    
    static {
        JSON_SMART_FIELD_FILTER = new JsonSmartFieldFilter();
    }
    
    public static Object convertToStrict(final Object obj, final Class<?> dest) {
        if (obj == null) {
            return null;
        }
        if (dest.isAssignableFrom(obj.getClass())) {
            return obj;
        }
        if (dest.isPrimitive()) {
            if (dest == Integer.TYPE) {
                if (obj instanceof Number) {
                    return ((Number)obj).intValue();
                }
                return Integer.valueOf(obj.toString());
            }
            else if (dest == Short.TYPE) {
                if (obj instanceof Number) {
                    return ((Number)obj).shortValue();
                }
                return Short.valueOf(obj.toString());
            }
            else if (dest == Long.TYPE) {
                if (obj instanceof Number) {
                    return ((Number)obj).longValue();
                }
                return Long.valueOf(obj.toString());
            }
            else if (dest == Byte.TYPE) {
                if (obj instanceof Number) {
                    return ((Number)obj).byteValue();
                }
                return Byte.valueOf(obj.toString());
            }
            else if (dest == Float.TYPE) {
                if (obj instanceof Number) {
                    return ((Number)obj).floatValue();
                }
                return Float.valueOf(obj.toString());
            }
            else {
                if (dest != Double.TYPE) {
                    if (dest == Character.TYPE) {
                        final String asString = dest.toString();
                        if (asString.length() > 0) {
                            return asString.charAt(0);
                        }
                    }
                    else if (dest == Boolean.TYPE) {
                        return obj;
                    }
                    throw new RuntimeException("Primitive: Can not convert " + obj.getClass().getName() + " to " + dest.getName());
                }
                if (obj instanceof Number) {
                    return ((Number)obj).doubleValue();
                }
                return Double.valueOf(obj.toString());
            }
        }
        else {
            if (dest.isEnum()) {
                return Enum.valueOf(dest, obj.toString());
            }
            if (dest == Integer.class) {
                if (obj instanceof Number) {
                    return ((Number)obj).intValue();
                }
                return Integer.valueOf(obj.toString());
            }
            else if (dest == Long.class) {
                if (obj instanceof Number) {
                    return ((Number)obj).longValue();
                }
                return Long.valueOf(obj.toString());
            }
            else if (dest == Short.class) {
                if (obj instanceof Number) {
                    return ((Number)obj).shortValue();
                }
                return Short.valueOf(obj.toString());
            }
            else if (dest == Byte.class) {
                if (obj instanceof Number) {
                    return ((Number)obj).byteValue();
                }
                return Byte.valueOf(obj.toString());
            }
            else if (dest == Float.class) {
                if (obj instanceof Number) {
                    return ((Number)obj).floatValue();
                }
                return Float.valueOf(obj.toString());
            }
            else {
                if (dest != Double.class) {
                    if (dest == Character.class) {
                        final String asString = dest.toString();
                        if (asString.length() > 0) {
                            return asString.charAt(0);
                        }
                    }
                    throw new RuntimeException("Object: Can not Convert " + obj.getClass().getName() + " to " + dest.getName());
                }
                if (obj instanceof Number) {
                    return ((Number)obj).doubleValue();
                }
                return Double.valueOf(obj.toString());
            }
        }
    }
    
    public static Object convertToX(final Object obj, final Class<?> dest) {
        if (obj == null) {
            return null;
        }
        if (dest.isAssignableFrom(obj.getClass())) {
            return obj;
        }
        if (dest.isPrimitive()) {
            if (obj instanceof Number) {
                return obj;
            }
            if (dest == Integer.TYPE) {
                return Integer.valueOf(obj.toString());
            }
            if (dest == Short.TYPE) {
                return Short.valueOf(obj.toString());
            }
            if (dest == Long.TYPE) {
                return Long.valueOf(obj.toString());
            }
            if (dest == Byte.TYPE) {
                return Byte.valueOf(obj.toString());
            }
            if (dest == Float.TYPE) {
                return Float.valueOf(obj.toString());
            }
            if (dest == Double.TYPE) {
                return Double.valueOf(obj.toString());
            }
            if (dest == Character.TYPE) {
                final String asString = dest.toString();
                if (asString.length() > 0) {
                    return asString.charAt(0);
                }
            }
            else if (dest == Boolean.TYPE) {
                return obj;
            }
            throw new RuntimeException("Primitive: Can not convert " + obj.getClass().getName() + " to " + dest.getName());
        }
        else {
            if (dest.isEnum()) {
                return Enum.valueOf(dest, obj.toString());
            }
            if (dest == Integer.class) {
                if (obj instanceof Number) {
                    return ((Number)obj).intValue();
                }
                return Integer.valueOf(obj.toString());
            }
            else if (dest == Long.class) {
                if (obj instanceof Number) {
                    return ((Number)obj).longValue();
                }
                return Long.valueOf(obj.toString());
            }
            else if (dest == Short.class) {
                if (obj instanceof Number) {
                    return ((Number)obj).shortValue();
                }
                return Short.valueOf(obj.toString());
            }
            else if (dest == Byte.class) {
                if (obj instanceof Number) {
                    return ((Number)obj).byteValue();
                }
                return Byte.valueOf(obj.toString());
            }
            else if (dest == Float.class) {
                if (obj instanceof Number) {
                    return ((Number)obj).floatValue();
                }
                return Float.valueOf(obj.toString());
            }
            else {
                if (dest != Double.class) {
                    if (dest == Character.class) {
                        final String asString = dest.toString();
                        if (asString.length() > 0) {
                            return asString.charAt(0);
                        }
                    }
                    throw new RuntimeException("Object: Can not Convert " + obj.getClass().getName() + " to " + dest.getName());
                }
                if (obj instanceof Number) {
                    return ((Number)obj).doubleValue();
                }
                return Double.valueOf(obj.toString());
            }
        }
    }
    
    public static String getSetterName(final String key) {
        final int len = key.length();
        final char[] b = new char[len + 3];
        b[0] = 's';
        b[1] = 'e';
        b[2] = 't';
        char c = key.charAt(0);
        if (c >= 'a' && c <= 'z') {
            c -= 32;
        }
        b[3] = c;
        for (int i = 1; i < len; ++i) {
            b[i + 3] = key.charAt(i);
        }
        return new String(b);
    }
    
    public static String getGetterName(final String key) {
        final int len = key.length();
        final char[] b = new char[len + 3];
        b[0] = 'g';
        b[1] = 'e';
        b[2] = 't';
        char c = key.charAt(0);
        if (c >= 'a' && c <= 'z') {
            c -= 32;
        }
        b[3] = c;
        for (int i = 1; i < len; ++i) {
            b[i + 3] = key.charAt(i);
        }
        return new String(b);
    }
    
    public static String getIsName(final String key) {
        final int len = key.length();
        final char[] b = new char[len + 2];
        b[0] = 'i';
        b[1] = 's';
        char c = key.charAt(0);
        if (c >= 'a' && c <= 'z') {
            c -= 32;
        }
        b[2] = c;
        for (int i = 1; i < len; ++i) {
            b[i + 2] = key.charAt(i);
        }
        return new String(b);
    }
    
    public static class JsonSmartFieldFilter implements FieldFilter
    {
        @Override
        public boolean canUse(final Field field) {
            final JsonIgnore ignore = field.getAnnotation(JsonIgnore.class);
            return ignore == null || !ignore.value();
        }
        
        @Override
        public boolean canUse(final Field field, final Method method) {
            final JsonIgnore ignore = method.getAnnotation(JsonIgnore.class);
            return ignore == null || !ignore.value();
        }
        
        @Override
        public boolean canRead(final Field field) {
            return true;
        }
        
        @Override
        public boolean canWrite(final Field field) {
            return true;
        }
    }
}
