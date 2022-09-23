// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.ajax;

import org.eclipse.jetty.util.log.Log;
import java.util.Map;
import org.eclipse.jetty.util.Loader;
import java.lang.reflect.Method;
import org.eclipse.jetty.util.log.Logger;

public class JSONEnumConvertor implements JSON.Convertor
{
    private static final Logger LOG;
    private boolean _fromJSON;
    private Method _valueOf;
    
    public JSONEnumConvertor() {
        this(false);
    }
    
    public JSONEnumConvertor(final boolean fromJSON) {
        try {
            final Class e = Loader.loadClass(this.getClass(), "java.lang.Enum");
            this._valueOf = e.getMethod("valueOf", Class.class, String.class);
        }
        catch (Exception e2) {
            throw new RuntimeException("!Enums", e2);
        }
        this._fromJSON = fromJSON;
    }
    
    public Object fromJSON(final Map map) {
        if (!this._fromJSON) {
            throw new UnsupportedOperationException();
        }
        try {
            final Class c = Loader.loadClass(this.getClass(), map.get("class"));
            return this._valueOf.invoke(null, c, map.get("value"));
        }
        catch (Exception e) {
            JSONEnumConvertor.LOG.warn(e);
            return null;
        }
    }
    
    public void toJSON(final Object obj, final JSON.Output out) {
        if (this._fromJSON) {
            out.addClass(obj.getClass());
            out.add("value", obj.toString());
        }
        else {
            out.add(obj.toString());
        }
    }
    
    static {
        LOG = Log.getLogger(JSONEnumConvertor.class);
    }
}
