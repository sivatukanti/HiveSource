// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.util.ajax;

import java.util.Map;
import org.mortbay.util.Loader;

public class JSONPojoConvertorFactory implements JSON.Convertor
{
    private final JSON _json;
    private final boolean _fromJSON;
    
    public JSONPojoConvertorFactory(final JSON json) {
        this._json = json;
        this._fromJSON = true;
        if (json == null) {
            throw new IllegalArgumentException();
        }
    }
    
    public JSONPojoConvertorFactory(final JSON json, final boolean fromJSON) {
        this._json = json;
        this._fromJSON = fromJSON;
        if (json == null) {
            throw new IllegalArgumentException();
        }
    }
    
    public void toJSON(final Object obj, final JSON.Output out) {
        final String clsName = obj.getClass().getName();
        JSON.Convertor convertor = this._json.getConvertorFor(clsName);
        if (convertor == null) {
            try {
                final Class cls = Loader.loadClass(JSON.class, clsName);
                convertor = new JSONPojoConvertor(cls, this._fromJSON);
                this._json.addConvertorFor(clsName, convertor);
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (convertor != null && obj.getClass() != Object.class) {
            convertor.toJSON(obj, out);
        }
        else {
            out.add(obj.toString());
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
                    convertor = new JSONPojoConvertor(cls);
                    this._json.addConvertorFor(clsName, convertor);
                }
                catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            if (convertor != null && !clsName.equals(Object.class.getName())) {
                return convertor.fromJSON(object);
            }
        }
        return map;
    }
}
