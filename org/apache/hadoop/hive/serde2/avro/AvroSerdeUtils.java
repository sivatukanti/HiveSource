// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.avro;

import org.apache.commons.logging.LogFactory;
import java.io.File;
import java.math.BigInteger;
import org.apache.hadoop.hive.common.type.HiveDecimal;
import java.nio.ByteBuffer;
import java.nio.Buffer;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.mapred.JobConf;
import java.util.List;
import org.apache.hadoop.fs.FSDataInputStream;
import java.io.InputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import java.net.URL;
import org.apache.avro.Schema;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.commons.logging.Log;

public class AvroSerdeUtils
{
    private static final Log LOG;
    @Deprecated
    public static final String SCHEMA_LITERAL = "avro.schema.literal";
    @Deprecated
    public static final String SCHEMA_URL = "avro.schema.url";
    @Deprecated
    public static final String SCHEMA_NAMESPACE = "avro.schema.namespace";
    @Deprecated
    public static final String SCHEMA_NAME = "avro.schema.name";
    @Deprecated
    public static final String SCHEMA_DOC = "avro.schema.doc";
    @Deprecated
    public static final String AVRO_SERDE_SCHEMA;
    @Deprecated
    public static final String SCHEMA_RETRIEVER;
    public static final String SCHEMA_NONE = "none";
    public static final String EXCEPTION_MESSAGE;
    
    public static Schema determineSchemaOrThrowException(final Configuration conf, final Properties properties) throws IOException, AvroSerdeException {
        String schemaString = properties.getProperty(AvroTableProperties.SCHEMA_LITERAL.getPropName());
        if (schemaString != null && !schemaString.equals("none")) {
            return getSchemaFor(schemaString);
        }
        schemaString = properties.getProperty(AvroTableProperties.SCHEMA_URL.getPropName());
        if (schemaString == null || schemaString.equals("none")) {
            throw new AvroSerdeException(AvroSerdeUtils.EXCEPTION_MESSAGE);
        }
        try {
            final Schema s = getSchemaFromFS(schemaString, conf);
            if (s == null) {
                return getSchemaFor(new URL(schemaString).openStream());
            }
            return s;
        }
        catch (IOException ioe) {
            throw new AvroSerdeException("Unable to read schema from given path: " + schemaString, ioe);
        }
        catch (URISyntaxException urie) {
            throw new AvroSerdeException("Unable to read schema from given path: " + schemaString, urie);
        }
    }
    
    protected static Schema getSchemaFromFS(final String schemaFSUrl, final Configuration conf) throws IOException, URISyntaxException {
        FSDataInputStream in = null;
        FileSystem fs = null;
        try {
            fs = FileSystem.get(new URI(schemaFSUrl), conf);
        }
        catch (IOException ioe) {
            final String msg = "Failed to open file system for uri " + schemaFSUrl + " assuming it is not a FileSystem url";
            AvroSerdeUtils.LOG.debug(msg, ioe);
            return null;
        }
        try {
            in = fs.open(new Path(schemaFSUrl));
            final Schema s = getSchemaFor(in);
            return s;
        }
        finally {
            if (in != null) {
                in.close();
            }
        }
    }
    
    public static boolean isNullableType(final Schema schema) {
        return schema.getType().equals(Schema.Type.UNION) && schema.getTypes().size() == 2 && (schema.getTypes().get(0).getType().equals(Schema.Type.NULL) || schema.getTypes().get(1).getType().equals(Schema.Type.NULL));
    }
    
    public static Schema getOtherTypeFromNullableType(final Schema schema) {
        final List<Schema> types = schema.getTypes();
        return types.get(0).getType().equals(Schema.Type.NULL) ? types.get(1) : types.get(0);
    }
    
    public static boolean insideMRJob(final JobConf job) {
        return job != null && HiveConf.getVar((Configuration)job, HiveConf.ConfVars.PLAN) != null && !HiveConf.getVar((Configuration)job, HiveConf.ConfVars.PLAN).isEmpty();
    }
    
    public static Buffer getBufferFromBytes(final byte[] input) {
        final ByteBuffer bb = ByteBuffer.wrap(input);
        return bb.rewind();
    }
    
    public static Buffer getBufferFromDecimal(HiveDecimal dec, final int scale) {
        if (dec == null) {
            return null;
        }
        dec = dec.setScale(scale);
        return getBufferFromBytes(dec.unscaledValue().toByteArray());
    }
    
    public static byte[] getBytesFromByteBuffer(final ByteBuffer byteBuffer) {
        byteBuffer.rewind();
        final byte[] result = new byte[byteBuffer.limit()];
        byteBuffer.get(result);
        return result;
    }
    
    public static HiveDecimal getHiveDecimalFromByteBuffer(final ByteBuffer byteBuffer, final int scale) {
        final byte[] result = getBytesFromByteBuffer(byteBuffer);
        final HiveDecimal dec = HiveDecimal.create(new BigInteger(result), scale);
        return dec;
    }
    
    public static Schema getSchemaFor(final String str) {
        final Schema.Parser parser = new Schema.Parser();
        final Schema schema = parser.parse(str);
        return schema;
    }
    
    public static Schema getSchemaFor(final File file) {
        final Schema.Parser parser = new Schema.Parser();
        Schema schema;
        try {
            schema = parser.parse(file);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to parse Avro schema from " + file.getName(), e);
        }
        return schema;
    }
    
    public static Schema getSchemaFor(final InputStream stream) {
        final Schema.Parser parser = new Schema.Parser();
        Schema schema;
        try {
            schema = parser.parse(stream);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to parse Avro schema", e);
        }
        return schema;
    }
    
    static {
        LOG = LogFactory.getLog(AvroSerdeUtils.class);
        AVRO_SERDE_SCHEMA = AvroTableProperties.AVRO_SERDE_SCHEMA.getPropName();
        SCHEMA_RETRIEVER = AvroTableProperties.SCHEMA_RETRIEVER.getPropName();
        EXCEPTION_MESSAGE = "Neither " + AvroTableProperties.SCHEMA_LITERAL.getPropName() + " nor " + AvroTableProperties.SCHEMA_URL.getPropName() + " specified, can't determine table schema";
    }
    
    public enum AvroTableProperties
    {
        SCHEMA_LITERAL("avro.schema.literal"), 
        SCHEMA_URL("avro.schema.url"), 
        SCHEMA_NAMESPACE("avro.schema.namespace"), 
        SCHEMA_NAME("avro.schema.name"), 
        SCHEMA_DOC("avro.schema.doc"), 
        AVRO_SERDE_SCHEMA("avro.serde.schema"), 
        SCHEMA_RETRIEVER("avro.schema.retriever");
        
        private final String propName;
        
        private AvroTableProperties(final String propName) {
            this.propName = propName;
        }
        
        public String getPropName() {
            return this.propName;
        }
    }
}
