// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.ajax;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.jetty.util.log.Log;
import java.util.Iterator;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;
import java.lang.reflect.Method;
import java.util.Map;
import org.eclipse.jetty.util.log.Logger;

public class JSONPojoConvertor implements JSON.Convertor
{
    private static final Logger LOG;
    public static final Object[] GETTER_ARG;
    public static final Object[] NULL_ARG;
    private static final Map<Class<?>, NumberType> __numberTypes;
    protected boolean _fromJSON;
    protected Class<?> _pojoClass;
    protected Map<String, Method> _getters;
    protected Map<String, Setter> _setters;
    protected Set<String> _excluded;
    public static final NumberType SHORT;
    public static final NumberType INTEGER;
    public static final NumberType FLOAT;
    public static final NumberType LONG;
    public static final NumberType DOUBLE;
    
    public static NumberType getNumberType(final Class<?> clazz) {
        return JSONPojoConvertor.__numberTypes.get(clazz);
    }
    
    public JSONPojoConvertor(final Class<?> pojoClass) {
        this(pojoClass, null, true);
    }
    
    public JSONPojoConvertor(final Class<?> pojoClass, final String[] excluded) {
        this(pojoClass, new HashSet<String>(Arrays.asList(excluded)), true);
    }
    
    public JSONPojoConvertor(final Class<?> pojoClass, final Set<String> excluded) {
        this(pojoClass, excluded, true);
    }
    
    public JSONPojoConvertor(final Class<?> pojoClass, final Set<String> excluded, final boolean fromJSON) {
        this._getters = new HashMap<String, Method>();
        this._setters = new HashMap<String, Setter>();
        this._pojoClass = pojoClass;
        this._excluded = excluded;
        this._fromJSON = fromJSON;
        this.init();
    }
    
    public JSONPojoConvertor(final Class<?> pojoClass, final boolean fromJSON) {
        this(pojoClass, null, fromJSON);
    }
    
    protected void init() {
        final Method[] methods = this._pojoClass.getMethods();
        for (int i = 0; i < methods.length; ++i) {
            final Method m = methods[i];
            if (!Modifier.isStatic(m.getModifiers()) && m.getDeclaringClass() != Object.class) {
                String name = m.getName();
                switch (m.getParameterTypes().length) {
                    case 0: {
                        if (m.getReturnType() == null) {
                            break;
                        }
                        if (name.startsWith("is") && name.length() > 2) {
                            name = name.substring(2, 3).toLowerCase() + name.substring(3);
                        }
                        else {
                            if (!name.startsWith("get") || name.length() <= 3) {
                                break;
                            }
                            name = name.substring(3, 4).toLowerCase() + name.substring(4);
                        }
                        if (this.includeField(name, m)) {
                            this.addGetter(name, m);
                            break;
                        }
                        break;
                    }
                    case 1: {
                        if (!name.startsWith("set") || name.length() <= 3) {
                            break;
                        }
                        name = name.substring(3, 4).toLowerCase() + name.substring(4);
                        if (this.includeField(name, m)) {
                            this.addSetter(name, m);
                            break;
                        }
                        break;
                    }
                }
            }
        }
    }
    
    protected void addGetter(final String name, final Method method) {
        this._getters.put(name, method);
    }
    
    protected void addSetter(final String name, final Method method) {
        this._setters.put(name, new Setter(name, method));
    }
    
    protected Setter getSetter(final String name) {
        return this._setters.get(name);
    }
    
    protected boolean includeField(final String name, final Method m) {
        return this._excluded == null || !this._excluded.contains(name);
    }
    
    protected int getExcludedCount() {
        return (this._excluded == null) ? 0 : this._excluded.size();
    }
    
    public Object fromJSON(final Map object) {
        Object obj = null;
        try {
            obj = this._pojoClass.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.setProps(obj, object);
        return obj;
    }
    
    public int setProps(final Object obj, final Map<?, ?> props) {
        int count = 0;
        for (final Map.Entry<?, ?> entry : props.entrySet()) {
            final Setter setter = this.getSetter((String)entry.getKey());
            if (setter != null) {
                try {
                    setter.invoke(obj, entry.getValue());
                    ++count;
                }
                catch (Exception e) {
                    JSONPojoConvertor.LOG.warn(this._pojoClass.getName() + "#" + setter.getPropertyName() + " not set from " + entry.getValue().getClass().getName() + "=" + entry.getValue().toString(), new Object[0]);
                    this.log(e);
                }
            }
        }
        return count;
    }
    
    public void toJSON(final Object obj, final JSON.Output out) {
        if (this._fromJSON) {
            out.addClass(this._pojoClass);
        }
        for (final Map.Entry<String, Method> entry : this._getters.entrySet()) {
            try {
                out.add(entry.getKey(), entry.getValue().invoke(obj, JSONPojoConvertor.GETTER_ARG));
            }
            catch (Exception e) {
                JSONPojoConvertor.LOG.warn("{} property '{}' excluded. (errors)", this._pojoClass.getName(), entry.getKey());
                this.log(e);
            }
        }
    }
    
    protected void log(final Throwable t) {
        JSONPojoConvertor.LOG.ignore(t);
    }
    
    static {
        LOG = Log.getLogger(JSONPojoConvertor.class);
        GETTER_ARG = new Object[0];
        NULL_ARG = new Object[] { null };
        __numberTypes = new HashMap<Class<?>, NumberType>();
        SHORT = new NumberType() {
            public Object getActualValue(final Number number) {
                return new Short(number.shortValue());
            }
        };
        INTEGER = new NumberType() {
            public Object getActualValue(final Number number) {
                return new Integer(number.intValue());
            }
        };
        FLOAT = new NumberType() {
            public Object getActualValue(final Number number) {
                return new Float(number.floatValue());
            }
        };
        LONG = new NumberType() {
            public Object getActualValue(final Number number) {
                return (number instanceof Long) ? number : new Long(number.longValue());
            }
        };
        DOUBLE = new NumberType() {
            public Object getActualValue(final Number number) {
                return (number instanceof Double) ? number : new Double(number.doubleValue());
            }
        };
        JSONPojoConvertor.__numberTypes.put(Short.class, JSONPojoConvertor.SHORT);
        JSONPojoConvertor.__numberTypes.put(Short.TYPE, JSONPojoConvertor.SHORT);
        JSONPojoConvertor.__numberTypes.put(Integer.class, JSONPojoConvertor.INTEGER);
        JSONPojoConvertor.__numberTypes.put(Integer.TYPE, JSONPojoConvertor.INTEGER);
        JSONPojoConvertor.__numberTypes.put(Long.class, JSONPojoConvertor.LONG);
        JSONPojoConvertor.__numberTypes.put(Long.TYPE, JSONPojoConvertor.LONG);
        JSONPojoConvertor.__numberTypes.put(Float.class, JSONPojoConvertor.FLOAT);
        JSONPojoConvertor.__numberTypes.put(Float.TYPE, JSONPojoConvertor.FLOAT);
        JSONPojoConvertor.__numberTypes.put(Double.class, JSONPojoConvertor.DOUBLE);
        JSONPojoConvertor.__numberTypes.put(Double.TYPE, JSONPojoConvertor.DOUBLE);
    }
    
    public static class Setter
    {
        protected String _propertyName;
        protected Method _setter;
        protected NumberType _numberType;
        protected Class<?> _type;
        protected Class<?> _componentType;
        
        public Setter(final String propertyName, final Method method) {
            this._propertyName = propertyName;
            this._setter = method;
            this._type = method.getParameterTypes()[0];
            this._numberType = JSONPojoConvertor.__numberTypes.get(this._type);
            if (this._numberType == null && this._type.isArray()) {
                this._componentType = this._type.getComponentType();
                this._numberType = JSONPojoConvertor.__numberTypes.get(this._componentType);
            }
        }
        
        public String getPropertyName() {
            return this._propertyName;
        }
        
        public Method getMethod() {
            return this._setter;
        }
        
        public NumberType getNumberType() {
            return this._numberType;
        }
        
        public Class<?> getType() {
            return this._type;
        }
        
        public Class<?> getComponentType() {
            return this._componentType;
        }
        
        public boolean isPropertyNumber() {
            return this._numberType != null;
        }
        
        public void invoke(final Object obj, final Object value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
            if (value == null) {
                this._setter.invoke(obj, JSONPojoConvertor.NULL_ARG);
            }
            else {
                this.invokeObject(obj, value);
            }
        }
        
        protected void invokeObject(final Object obj, final Object value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
            if (this._type.isEnum()) {
                if (value instanceof Enum) {
                    this._setter.invoke(obj, value);
                }
                else {
                    this._setter.invoke(obj, Enum.valueOf(this._type, value.toString()));
                }
            }
            else if (this._numberType != null && value instanceof Number) {
                this._setter.invoke(obj, this._numberType.getActualValue((Number)value));
            }
            else if (Character.TYPE.equals(this._type) || Character.class.equals(this._type)) {
                this._setter.invoke(obj, String.valueOf(value).charAt(0));
            }
            else if (this._componentType != null && value.getClass().isArray()) {
                if (this._numberType == null) {
                    final int len = Array.getLength(value);
                    final Object array = Array.newInstance(this._componentType, len);
                    try {
                        System.arraycopy(value, 0, array, 0, len);
                    }
                    catch (Exception e) {
                        JSONPojoConvertor.LOG.ignore(e);
                        this._setter.invoke(obj, value);
                        return;
                    }
                    this._setter.invoke(obj, array);
                }
                else {
                    final Object[] old = (Object[])value;
                    final Object array = Array.newInstance(this._componentType, old.length);
                    try {
                        for (int i = 0; i < old.length; ++i) {
                            Array.set(array, i, this._numberType.getActualValue((Number)old[i]));
                        }
                    }
                    catch (Exception e) {
                        JSONPojoConvertor.LOG.ignore(e);
                        this._setter.invoke(obj, value);
                        return;
                    }
                    this._setter.invoke(obj, array);
                }
            }
            else {
                this._setter.invoke(obj, value);
            }
        }
    }
    
    public interface NumberType
    {
        Object getActualValue(final Number p0);
    }
}
