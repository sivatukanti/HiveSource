// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.util.ajax;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;

public class JSONObjectConvertor implements JSON.Convertor
{
    private boolean _fromJSON;
    private Set _excluded;
    
    public JSONObjectConvertor() {
        this._excluded = null;
        this._fromJSON = false;
    }
    
    public JSONObjectConvertor(final boolean fromJSON) {
        this._excluded = null;
        this._fromJSON = fromJSON;
    }
    
    public JSONObjectConvertor(final boolean fromJSON, final String[] excluded) {
        this._excluded = null;
        this._fromJSON = fromJSON;
        if (excluded != null) {
            this._excluded = new HashSet(Arrays.asList(excluded));
        }
    }
    
    public Object fromJSON(final Map map) {
        if (this._fromJSON) {
            throw new UnsupportedOperationException();
        }
        return map;
    }
    
    public void toJSON(final Object obj, final JSON.Output out) {
        try {
            final Class c = obj.getClass();
            if (this._fromJSON) {
                out.addClass(obj.getClass());
            }
            final Method[] methods = obj.getClass().getMethods();
            for (int i = 0; i < methods.length; ++i) {
                final Method m = methods[i];
                if (!Modifier.isStatic(m.getModifiers()) && m.getParameterTypes().length == 0 && m.getReturnType() != null && m.getDeclaringClass() != Object.class) {
                    String name = m.getName();
                    if (name.startsWith("is")) {
                        name = name.substring(2, 3).toLowerCase() + name.substring(3);
                    }
                    else {
                        if (!name.startsWith("get")) {
                            continue;
                        }
                        name = name.substring(3, 4).toLowerCase() + name.substring(4);
                    }
                    if (this.includeField(name, obj, m)) {
                        out.add(name, m.invoke(obj, (Object[])null));
                    }
                }
            }
        }
        catch (Throwable e) {
            throw new RuntimeException("Illegal argument", e);
        }
    }
    
    protected boolean includeField(final String name, final Object o, final Method m) {
        return this._excluded == null || !this._excluded.contains(name);
    }
}
