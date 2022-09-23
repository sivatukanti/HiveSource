// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.avro.specific;

import java.io.IOException;
import org.apache.avro.io.Encoder;
import org.apache.avro.Schema;
import java.lang.reflect.Type;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;

public class SpecificDatumWriter<T> extends GenericDatumWriter<T>
{
    public SpecificDatumWriter() {
        super(SpecificData.get());
    }
    
    public SpecificDatumWriter(final Class<T> c) {
        super(SpecificData.get().getSchema(c), SpecificData.get());
    }
    
    public SpecificDatumWriter(final Schema schema) {
        super(schema, SpecificData.get());
    }
    
    public SpecificDatumWriter(final Schema root, final SpecificData specificData) {
        super(root, specificData);
    }
    
    protected SpecificDatumWriter(final SpecificData specificData) {
        super(specificData);
    }
    
    public SpecificData getSpecificData() {
        return (SpecificData)this.getData();
    }
    
    @Override
    protected void writeEnum(final Schema schema, final Object datum, final Encoder out) throws IOException {
        if (!(datum instanceof Enum)) {
            super.writeEnum(schema, datum, out);
        }
        else {
            out.writeEnum(((Enum)datum).ordinal());
        }
    }
    
    @Override
    protected void writeString(final Schema schema, Object datum, final Encoder out) throws IOException {
        if (!(datum instanceof CharSequence) && this.getSpecificData().isStringable(datum.getClass())) {
            datum = datum.toString();
        }
        this.writeString(datum, out);
    }
}
