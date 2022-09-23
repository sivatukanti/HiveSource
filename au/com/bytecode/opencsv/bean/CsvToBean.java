// 
// Decompiled by Procyon v0.5.36
// 

package au.com.bytecode.opencsv.bean;

import java.beans.PropertyEditorManager;
import java.util.HashMap;
import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import au.com.bytecode.opencsv.CSVReader;
import java.util.List;
import java.io.Reader;
import java.beans.PropertyEditor;
import java.util.Map;

public class CsvToBean<T>
{
    private Map<Class<?>, PropertyEditor> editorMap;
    
    public CsvToBean() {
        this.editorMap = null;
    }
    
    public List<T> parse(final MappingStrategy<T> mapper, final Reader reader) {
        return this.parse(mapper, new CSVReader(reader));
    }
    
    public List<T> parse(final MappingStrategy<T> mapper, final CSVReader csv) {
        try {
            mapper.captureHeader(csv);
            final List<T> list = new ArrayList<T>();
            String[] line;
            while (null != (line = csv.readNext())) {
                final T obj = this.processLine(mapper, line);
                list.add(obj);
            }
            return list;
        }
        catch (Exception e) {
            throw new RuntimeException("Error parsing CSV!", e);
        }
    }
    
    protected T processLine(final MappingStrategy<T> mapper, final String[] line) throws IllegalAccessException, InvocationTargetException, InstantiationException, IntrospectionException {
        final T bean = mapper.createBean();
        for (int col = 0; col < line.length; ++col) {
            final PropertyDescriptor prop = mapper.findDescriptor(col);
            if (null != prop) {
                final String value = this.checkForTrim(line[col], prop);
                final Object obj = this.convertValue(value, prop);
                prop.getWriteMethod().invoke(bean, obj);
            }
        }
        return bean;
    }
    
    private String checkForTrim(final String s, final PropertyDescriptor prop) {
        return this.trimmableProperty(prop) ? s.trim() : s;
    }
    
    private boolean trimmableProperty(final PropertyDescriptor prop) {
        return !prop.getPropertyType().getName().contains("String");
    }
    
    protected Object convertValue(final String value, final PropertyDescriptor prop) throws InstantiationException, IllegalAccessException {
        final PropertyEditor editor = this.getPropertyEditor(prop);
        Object obj = value;
        if (null != editor) {
            editor.setAsText(value);
            obj = editor.getValue();
        }
        return obj;
    }
    
    private PropertyEditor getPropertyEditorValue(final Class<?> cls) {
        if (this.editorMap == null) {
            this.editorMap = new HashMap<Class<?>, PropertyEditor>();
        }
        PropertyEditor editor = this.editorMap.get(cls);
        if (editor == null) {
            editor = PropertyEditorManager.findEditor(cls);
            this.addEditorToMap(cls, editor);
        }
        return editor;
    }
    
    private void addEditorToMap(final Class<?> cls, final PropertyEditor editor) {
        if (editor != null) {
            this.editorMap.put(cls, editor);
        }
    }
    
    protected PropertyEditor getPropertyEditor(final PropertyDescriptor desc) throws InstantiationException, IllegalAccessException {
        final Class<?> cls = desc.getPropertyEditorClass();
        if (null != cls) {
            return (PropertyEditor)cls.newInstance();
        }
        return this.getPropertyEditorValue(desc.getPropertyType());
    }
}
