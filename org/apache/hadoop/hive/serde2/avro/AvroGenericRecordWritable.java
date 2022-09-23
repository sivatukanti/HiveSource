// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.avro;

import org.apache.avro.io.DatumReader;
import org.apache.avro.file.DataFileStream;
import java.io.ByteArrayInputStream;
import org.apache.avro.io.Decoder;
import org.apache.avro.generic.GenericDatumReader;
import java.io.InputStream;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.generic.GenericData;
import java.io.DataInput;
import java.io.IOException;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.BinaryEncoder;
import java.io.OutputStream;
import java.io.DataOutputStream;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.generic.GenericDatumWriter;
import java.io.DataOutput;
import java.rmi.server.UID;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.io.Writable;

public class AvroGenericRecordWritable implements Writable
{
    GenericRecord record;
    private BinaryDecoder binaryDecoder;
    private Schema fileSchema;
    private UID recordReaderID;
    
    public GenericRecord getRecord() {
        return this.record;
    }
    
    public void setRecord(final GenericRecord record) {
        this.record = record;
    }
    
    public AvroGenericRecordWritable() {
    }
    
    public AvroGenericRecordWritable(final GenericRecord record) {
        this.record = record;
    }
    
    @Override
    public void write(final DataOutput out) throws IOException {
        String schemaString = this.record.getSchema().toString(false);
        out.writeUTF(schemaString);
        schemaString = this.fileSchema.toString(false);
        out.writeUTF(schemaString);
        this.recordReaderID.write(out);
        final GenericDatumWriter<GenericRecord> gdw = new GenericDatumWriter<GenericRecord>();
        final BinaryEncoder be = EncoderFactory.get().directBinaryEncoder((OutputStream)out, null);
        gdw.setSchema(this.record.getSchema());
        gdw.write(this.record, be);
    }
    
    @Override
    public void readFields(final DataInput in) throws IOException {
        final Schema schema = AvroSerdeUtils.getSchemaFor(in.readUTF());
        this.fileSchema = AvroSerdeUtils.getSchemaFor(in.readUTF());
        this.recordReaderID = UID.read(in);
        this.record = new GenericData.Record(schema);
        this.binaryDecoder = DecoderFactory.defaultFactory().createBinaryDecoder((InputStream)in, this.binaryDecoder);
        final GenericDatumReader<GenericRecord> gdr = new GenericDatumReader<GenericRecord>(schema);
        this.record = gdr.read(this.record, this.binaryDecoder);
    }
    
    public void readFields(final byte[] bytes, final int offset, final int length, final Schema writerSchema, final Schema readerSchema) throws IOException {
        this.fileSchema = writerSchema;
        this.record = new GenericData.Record(writerSchema);
        this.binaryDecoder = DecoderFactory.get().binaryDecoder(bytes, offset, length - offset, this.binaryDecoder);
        final GenericDatumReader<GenericRecord> gdr = new GenericDatumReader<GenericRecord>(writerSchema, readerSchema);
        this.record = gdr.read(null, this.binaryDecoder);
    }
    
    public void readFields(final byte[] bytes, final Schema writerSchema, final Schema readerSchema) throws IOException {
        this.fileSchema = writerSchema;
        this.record = new GenericData.Record(writerSchema);
        final GenericDatumReader<GenericRecord> gdr = new GenericDatumReader<GenericRecord>();
        gdr.setExpected(readerSchema);
        final ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        final DataFileStream<GenericRecord> dfr = new DataFileStream<GenericRecord>(is, gdr);
        this.record = dfr.next(this.record);
        dfr.close();
    }
    
    public UID getRecordReaderID() {
        return this.recordReaderID;
    }
    
    public void setRecordReaderID(final UID recordReaderID) {
        this.recordReaderID = recordReaderID;
    }
    
    public Schema getFileSchema() {
        return this.fileSchema;
    }
    
    public void setFileSchema(final Schema originalSchema) {
        this.fileSchema = originalSchema;
    }
}
