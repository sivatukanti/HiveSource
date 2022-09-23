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
import java.util.HashMap;
import java.util.Map;

public class BeansWriterASMRemap implements JsonWriterI<Object>
{
    private Map<String, String> rename;
    
    public BeansWriterASMRemap() {
        this.rename = new HashMap<String, String>();
    }
    
    public void renameField(final String source, final String dest) {
        this.rename.put(source, dest);
    }
    
    private String rename(final String key) {
        final String k2 = this.rename.get(key);
        if (k2 != null) {
            return k2;
        }
        return key;
    }
    
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
                    String key = field.getName();
                    key = this.rename(key);
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
