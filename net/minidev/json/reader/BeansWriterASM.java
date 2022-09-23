// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.json.reader;

import net.minidev.asm.Accessor;
import java.io.IOException;
import net.minidev.json.JSONObject;
import net.minidev.asm.FieldFilter;
import net.minidev.asm.BeansAccess;
import net.minidev.json.JSONUtil;
import net.minidev.json.JSONStyle;

public class BeansWriterASM implements JsonWriterI<Object>
{
    @Override
    public <E> void writeJSONString(final E value, final Appendable out, final JSONStyle compression) throws IOException {
        try {
            final Class<?> cls = value.getClass();
            boolean needSep = false;
            final BeansAccess fields = BeansAccess.get(cls, JSONUtil.JSON_SMART_FIELD_FILTER);
            out.append('{');
            Accessor[] accessors;
            for (int length = (accessors = fields.getAccessors()).length, i = 0; i < length; ++i) {
                final Accessor field = accessors[i];
                final Object v = fields.get(value, field.getIndex());
                if (v != null || !compression.ignoreNull()) {
                    if (needSep) {
                        out.append(',');
                    }
                    else {
                        needSep = true;
                    }
                    final String key = field.getName();
                    JSONObject.writeJSONKV(key, v, out, compression);
                }
            }
            out.append('}');
        }
        catch (IOException e) {
            throw e;
        }
    }
}
