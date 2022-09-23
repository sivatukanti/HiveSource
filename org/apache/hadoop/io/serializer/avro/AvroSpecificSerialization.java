// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.serializer.avro;

import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.io.DatumReader;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.avro.specific.SpecificRecord;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class AvroSpecificSerialization extends AvroSerialization<SpecificRecord>
{
    @InterfaceAudience.Private
    @Override
    public boolean accept(final Class<?> c) {
        return SpecificRecord.class.isAssignableFrom(c);
    }
    
    @InterfaceAudience.Private
    @Override
    public DatumReader getReader(final Class<SpecificRecord> clazz) {
        try {
            return new SpecificDatumReader(clazz.newInstance().getSchema());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @InterfaceAudience.Private
    @Override
    public Schema getSchema(final SpecificRecord t) {
        return t.getSchema();
    }
    
    @InterfaceAudience.Private
    @Override
    public DatumWriter getWriter(final Class<SpecificRecord> clazz) {
        return new SpecificDatumWriter();
    }
}
