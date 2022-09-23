// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json.reader;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import net.minidev.json.JSONUtil;
import net.minidev.json.JSONStyle;

public class BeansWriter implements JsonWriterI<Object>
{
    @Override
    public <E> void writeJSONString(final E value, final Appendable out, final JSONStyle compression) throws IOException {
        try {
            Class<?> nextClass = value.getClass();
            boolean needSep = false;
            compression.objectStart(out);
            while (nextClass != Object.class) {
                final Field[] fields = nextClass.getDeclaredFields();
                Field[] array;
                for (int length = (array = fields).length, i = 0; i < length; ++i) {
                    final Field field = array[i];
                    final int m = field.getModifiers();
                    if ((m & 0x98) <= 0) {
                        Object v = null;
                        if ((m & 0x1) > 0) {
                            v = field.get(value);
                        }
                        else {
                            String g = JSONUtil.getGetterName(field.getName());
                            Method mtd = null;
                            try {
                                mtd = nextClass.getDeclaredMethod(g, (Class<?>[])new Class[0]);
                            }
                            catch (Exception ex) {}
                            if (mtd == null) {
                                final Class<?> c2 = field.getType();
                                if (c2 == Boolean.TYPE || c2 == Boolean.class) {
                                    g = JSONUtil.getIsName(field.getName());
                                    mtd = nextClass.getDeclaredMethod(g, (Class<?>[])new Class[0]);
                                }
                            }
                            if (mtd == null) {
                                continue;
                            }
                            v = mtd.invoke(value, new Object[0]);
                        }
                        if (v != null || !compression.ignoreNull()) {
                            if (needSep) {
                                compression.objectNext(out);
                            }
                            else {
                                needSep = true;
                            }
                            final String key = field.getName();
                            JsonWriter.writeJSONKV(key, v, out, compression);
                        }
                    }
                }
                nextClass = nextClass.getSuperclass();
            }
            compression.objectStop(out);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
