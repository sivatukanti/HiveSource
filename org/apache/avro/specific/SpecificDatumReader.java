// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.specific;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.util.ClassUtils;
import org.apache.avro.generic.GenericData;
import java.lang.reflect.Type;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;

public class SpecificDatumReader<T> extends GenericDatumReader<T>
{
    public SpecificDatumReader() {
        this(null, null, SpecificData.get());
    }
    
    public SpecificDatumReader(final Class<T> c) {
        this(new SpecificData(c.getClassLoader()));
        this.setSchema(this.getSpecificData().getSchema(c));
    }
    
    public SpecificDatumReader(final Schema schema) {
        this(schema, schema, SpecificData.get());
    }
    
    public SpecificDatumReader(final Schema writer, final Schema reader) {
        this(writer, reader, SpecificData.get());
    }
    
    public SpecificDatumReader(final Schema writer, final Schema reader, final SpecificData data) {
        super(writer, reader, data);
    }
    
    public SpecificDatumReader(final SpecificData data) {
        super(data);
    }
    
    public SpecificData getSpecificData() {
        return (SpecificData)this.getData();
    }
    
    @Override
    public void setSchema(final Schema actual) {
        if (this.getExpected() == null && actual != null && actual.getType() == Schema.Type.RECORD) {
            final SpecificData data = this.getSpecificData();
            final Class c = data.getClass(actual);
            if (c != null && SpecificRecord.class.isAssignableFrom(c)) {
                this.setExpected(data.getSchema(c));
            }
        }
        super.setSchema(actual);
    }
    
    @Override
    protected Class findStringClass(final Schema schema) {
        Class stringClass = null;
        switch (schema.getType()) {
            case STRING: {
                stringClass = this.getPropAsClass(schema, "java-class");
                break;
            }
            case MAP: {
                stringClass = this.getPropAsClass(schema, "java-key-class");
                break;
            }
        }
        if (stringClass != null) {
            return stringClass;
        }
        return super.findStringClass(schema);
    }
    
    private Class getPropAsClass(final Schema schema, final String prop) {
        final String name = schema.getProp(prop);
        if (name == null) {
            return null;
        }
        try {
            return ClassUtils.forName(this.getData().getClassLoader(), name);
        }
        catch (ClassNotFoundException e) {
            throw new AvroRuntimeException(e);
        }
    }
}
