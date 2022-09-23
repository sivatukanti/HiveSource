// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.ajax;

import java.util.Map;
import org.eclipse.jetty.util.Loader;

public class JSONPojoConvertorFactory implements JSON.Convertor
{
    private final JSON _json;
    private final boolean _fromJson;
    
    public JSONPojoConvertorFactory(final JSON json) {
        if (json == null) {
            throw new IllegalArgumentException();
        }
        this._json = json;
        this._fromJson = true;
    }
    
    public JSONPojoConvertorFactory(final JSON json, final boolean fromJSON) {
        if (json == null) {
            throw new IllegalArgumentException();
        }
        this._json = json;
        this._fromJson = fromJSON;
    }
    
    public void toJSON(final Object obj, final JSON.Output out) {
        final String clsName = obj.getClass().getName();
        JSON.Convertor convertor = this._json.getConvertorFor(clsName);
        if (convertor == null) {
            try {
                final Class cls = Loader.loadClass(JSON.class, clsName);
                convertor = new JSONPojoConvertor(cls, this._fromJson);
                this._json.addConvertorFor(clsName, convertor);
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (convertor != null) {
            convertor.toJSON(obj, out);
        }
    }
    
    public Object fromJSON(final Map object) {
        final Map map = object;
        final String clsName = map.get("class");
        if (clsName != null) {
            JSON.Convertor convertor = this._json.getConvertorFor(clsName);
            if (convertor == null) {
                try {
                    final Class cls = Loader.loadClass(JSON.class, clsName);
                    convertor = new JSONPojoConvertor(cls, this._fromJson);
                    this._json.addConvertorFor(clsName, convertor);
                }
                catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (convertor != null) {
                return convertor.fromJSON(object);
            }
        }
        return map;
    }
}
