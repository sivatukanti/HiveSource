// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.avro;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.serde2.SerDeStats;
import org.apache.hadoop.io.Writable;
import java.util.ArrayList;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoUtils;
import java.util.Arrays;
import org.apache.hadoop.hive.serde2.SerDeException;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.avro.Schema;
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfo;
import java.util.List;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.SerDeSpec;
import org.apache.hadoop.hive.serde2.AbstractSerDe;

@SerDeSpec(schemaProps = { "columns", "columns.types", "columns.comments", "name", "comment", "avro.schema.literal", "avro.schema.url", "avro.schema.namespace", "avro.schema.name", "avro.schema.doc" })
public class AvroSerDe extends AbstractSerDe
{
    private static final Log LOG;
    public static final String TABLE_NAME = "name";
    public static final String TABLE_COMMENT = "comment";
    public static final String LIST_COLUMN_COMMENTS = "columns.comments";
    public static final String DECIMAL_TYPE_NAME = "decimal";
    public static final String CHAR_TYPE_NAME = "char";
    public static final String VARCHAR_TYPE_NAME = "varchar";
    public static final String DATE_TYPE_NAME = "date";
    public static final String TIMESTAMP_TYPE_NAME = "timestamp-millis";
    public static final String AVRO_PROP_LOGICAL_TYPE = "logicalType";
    public static final String AVRO_PROP_PRECISION = "precision";
    public static final String AVRO_PROP_SCALE = "scale";
    public static final String AVRO_PROP_MAX_LENGTH = "maxLength";
    public static final String AVRO_STRING_TYPE_NAME = "string";
    public static final String AVRO_INT_TYPE_NAME = "int";
    public static final String AVRO_LONG_TYPE_NAME = "long";
    private ObjectInspector oi;
    private List<String> columnNames;
    private List<TypeInfo> columnTypes;
    private Schema schema;
    private AvroDeserializer avroDeserializer;
    private AvroSerializer avroSerializer;
    private boolean badSchema;
    
    public AvroSerDe() {
        this.avroDeserializer = null;
        this.avroSerializer = null;
        this.badSchema = false;
    }
    
    @Override
    public void initialize(final Configuration configuration, final Properties tableProperties, final Properties partitionProperties) throws SerDeException {
        this.initialize(configuration, tableProperties);
    }
    
    @Override
    public void initialize(final Configuration configuration, final Properties properties) throws SerDeException {
        if (this.schema != null) {
            AvroSerDe.LOG.info("Resetting already initialized AvroSerDe");
        }
        this.schema = null;
        this.oi = null;
        this.columnNames = null;
        this.columnTypes = null;
        final String columnNameProperty = properties.getProperty("columns");
        final String columnTypeProperty = properties.getProperty("columns.types");
        final String columnCommentProperty = properties.getProperty("columns.comments", "");
        if (properties.getProperty(AvroSerdeUtils.AvroTableProperties.SCHEMA_LITERAL.getPropName()) != null || properties.getProperty(AvroSerdeUtils.AvroTableProperties.SCHEMA_URL.getPropName()) != null || columnNameProperty == null || columnNameProperty.isEmpty() || columnTypeProperty == null || columnTypeProperty.isEmpty()) {
            this.schema = this.determineSchemaOrReturnErrorSchema(configuration, properties);
        }
        else {
            this.columnNames = Arrays.asList(columnNameProperty.split(","));
            this.columnTypes = TypeInfoUtils.getTypeInfosFromTypeString(columnTypeProperty);
            this.schema = getSchemaFromCols(properties, this.columnNames, this.columnTypes, columnCommentProperty);
            properties.setProperty(AvroSerdeUtils.AvroTableProperties.SCHEMA_LITERAL.getPropName(), this.schema.toString());
        }
        AvroSerDe.LOG.info("Avro schema is " + this.schema);
        if (configuration == null) {
            AvroSerDe.LOG.info("Configuration null, not inserting schema");
        }
        else {
            configuration.set(AvroSerdeUtils.AvroTableProperties.AVRO_SERDE_SCHEMA.getPropName(), this.schema.toString(false));
        }
        this.badSchema = this.schema.equals(SchemaResolutionProblem.SIGNAL_BAD_SCHEMA);
        final AvroObjectInspectorGenerator aoig = new AvroObjectInspectorGenerator(this.schema);
        this.columnNames = aoig.getColumnNames();
        this.columnTypes = aoig.getColumnTypes();
        this.oi = aoig.getObjectInspector();
    }
    
    public static Schema getSchemaFromCols(final Properties properties, final List<String> columnNames, final List<TypeInfo> columnTypes, final String columnCommentProperty) {
        List<String> columnComments;
        if (columnCommentProperty == null || columnCommentProperty.isEmpty()) {
            columnComments = new ArrayList<String>();
        }
        else {
            columnComments = Arrays.asList(columnCommentProperty.split("\u0000"));
            AvroSerDe.LOG.info("columnComments is " + columnCommentProperty);
        }
        if (columnNames.size() != columnTypes.size()) {
            throw new IllegalArgumentException("AvroSerde initialization failed. Number of column name and column type differs. columnNames = " + columnNames + ", columnTypes = " + columnTypes);
        }
        final String tableName = properties.getProperty("name");
        final String tableComment = properties.getProperty("comment");
        final TypeInfoToSchema typeInfoToSchema = new TypeInfoToSchema();
        return typeInfoToSchema.convert(columnNames, columnTypes, columnComments, properties.getProperty(AvroSerdeUtils.AvroTableProperties.SCHEMA_NAMESPACE.getPropName()), properties.getProperty(AvroSerdeUtils.AvroTableProperties.SCHEMA_NAME.getPropName(), tableName), properties.getProperty(AvroSerdeUtils.AvroTableProperties.SCHEMA_DOC.getPropName(), tableComment));
    }
    
    public Schema determineSchemaOrReturnErrorSchema(final Configuration conf, final Properties props) {
        try {
            this.configErrors = "";
            return AvroSerdeUtils.determineSchemaOrThrowException(conf, props);
        }
        catch (AvroSerdeException he) {
            AvroSerDe.LOG.warn("Encountered AvroSerdeException determining schema. Returning signal schema to indicate problem", he);
            this.configErrors = new String("Encountered AvroSerdeException determining schema. Returning signal schema to indicate problem: " + he.getMessage());
            return this.schema = SchemaResolutionProblem.SIGNAL_BAD_SCHEMA;
        }
        catch (Exception e) {
            AvroSerDe.LOG.warn("Encountered exception determining schema. Returning signal schema to indicate problem", e);
            this.configErrors = new String("Encountered exception determining schema. Returning signal schema to indicate problem: " + e.getMessage());
            return SchemaResolutionProblem.SIGNAL_BAD_SCHEMA;
        }
    }
    
    @Override
    public Class<? extends Writable> getSerializedClass() {
        return AvroGenericRecordWritable.class;
    }
    
    @Override
    public Writable serialize(final Object o, final ObjectInspector objectInspector) throws SerDeException {
        if (this.badSchema) {
            throw new BadSchemaException();
        }
        return this.getSerializer().serialize(o, objectInspector, this.columnNames, this.columnTypes, this.schema);
    }
    
    @Override
    public Object deserialize(final Writable writable) throws SerDeException {
        if (this.badSchema) {
            throw new BadSchemaException();
        }
        return this.getDeserializer().deserialize(this.columnNames, this.columnTypes, writable, this.schema);
    }
    
    @Override
    public ObjectInspector getObjectInspector() throws SerDeException {
        return this.oi;
    }
    
    @Override
    public SerDeStats getSerDeStats() {
        return null;
    }
    
    private AvroDeserializer getDeserializer() {
        if (this.avroDeserializer == null) {
            this.avroDeserializer = new AvroDeserializer();
        }
        return this.avroDeserializer;
    }
    
    private AvroSerializer getSerializer() {
        if (this.avroSerializer == null) {
            this.avroSerializer = new AvroSerializer();
        }
        return this.avroSerializer;
    }
    
    static {
        LOG = LogFactory.getLog(AvroSerDe.class);
    }
}
