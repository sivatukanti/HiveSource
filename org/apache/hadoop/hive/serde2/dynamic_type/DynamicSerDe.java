// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.dynamic_type;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.serde2.SerDeStats;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import java.util.List;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.hive.serde2.SerDeException;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import org.apache.hadoop.hive.serde2.thrift.ConfigurableTProtocol;
import org.apache.thrift.transport.TTransport;
import java.io.OutputStream;
import java.io.InputStream;
import org.apache.hadoop.hive.serde2.thrift.TReflectionUtils;
import java.util.Properties;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.BytesWritable;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.protocol.TProtocol;
import org.apache.hadoop.hive.serde2.ByteStream;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.SerDeSpec;
import org.apache.hadoop.hive.serde2.AbstractSerDe;

@SerDeSpec(schemaProps = { "serialization.ddl", "serialization.format", "name" })
public class DynamicSerDe extends AbstractSerDe
{
    public static final Log LOG;
    private String type_name;
    private DynamicSerDeStructBase bt;
    public static final String META_TABLE_NAME = "name";
    private transient thrift_grammar parse_tree;
    protected transient ByteStream.Input bis_;
    protected transient ByteStream.Output bos_;
    protected transient TProtocol oprot_;
    protected transient TProtocol iprot_;
    TIOStreamTransport tios;
    Object deserializeReuse;
    BytesWritable ret;
    
    public DynamicSerDe() {
        this.deserializeReuse = null;
        this.ret = new BytesWritable();
    }
    
    @Override
    public void initialize(final Configuration job, final Properties tbl) throws SerDeException {
        try {
            final String ddl = tbl.getProperty("serialization.ddl");
            final String tableName = tbl.getProperty("name");
            final int index = tableName.indexOf(46);
            if (index != -1) {
                this.type_name = tableName.substring(index + 1, tableName.length());
            }
            else {
                this.type_name = tableName;
            }
            String protoName = tbl.getProperty("serialization.format");
            if (protoName == null) {
                protoName = "org.apache.thrift.protocol.TBinaryProtocol";
            }
            protoName = protoName.replace("com.facebook.thrift.protocol", "org.apache.thrift.protocol");
            final TProtocolFactory protFactory = TReflectionUtils.getProtocolFactoryByName(protoName);
            this.bos_ = new ByteStream.Output();
            this.bis_ = new ByteStream.Input();
            this.tios = new TIOStreamTransport(this.bis_, this.bos_);
            this.oprot_ = protFactory.getProtocol(this.tios);
            this.iprot_ = protFactory.getProtocol(this.tios);
            if (this.oprot_ instanceof ConfigurableTProtocol) {
                ((ConfigurableTProtocol)this.oprot_).initialize(job, tbl);
            }
            if (this.iprot_ instanceof ConfigurableTProtocol) {
                ((ConfigurableTProtocol)this.iprot_).initialize(job, tbl);
            }
            final List<String> include_path = new ArrayList<String>();
            include_path.add(".");
            DynamicSerDe.LOG.debug("ddl=" + ddl);
            (this.parse_tree = new thrift_grammar(new ByteArrayInputStream(ddl.getBytes()), include_path, false)).Start();
            this.bt = this.parse_tree.types.get(this.type_name);
            if (this.bt == null) {
                this.bt = this.parse_tree.tables.get(this.type_name);
            }
            if (this.bt == null) {
                throw new SerDeException("Could not lookup table type " + this.type_name + " in this ddl: " + ddl);
            }
            this.bt.initialize();
        }
        catch (Exception e) {
            System.err.println(StringUtils.stringifyException(e));
            throw new SerDeException(e);
        }
    }
    
    @Override
    public Object deserialize(final Writable field) throws SerDeException {
        try {
            if (field instanceof Text) {
                final Text b = (Text)field;
                this.bis_.reset(b.getBytes(), b.getLength());
            }
            else {
                final BytesWritable b2 = (BytesWritable)field;
                this.bis_.reset(b2.getBytes(), b2.getLength());
            }
            return this.deserializeReuse = this.bt.deserialize(this.deserializeReuse, this.iprot_);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new SerDeException(e);
        }
    }
    
    public static ObjectInspector dynamicSerDeStructBaseToObjectInspector(final DynamicSerDeTypeBase bt) throws SerDeException {
        if (bt.isList()) {
            return ObjectInspectorFactory.getStandardListObjectInspector(dynamicSerDeStructBaseToObjectInspector(((DynamicSerDeTypeList)bt).getElementType()));
        }
        if (bt.isMap()) {
            final DynamicSerDeTypeMap btMap = (DynamicSerDeTypeMap)bt;
            return ObjectInspectorFactory.getStandardMapObjectInspector(dynamicSerDeStructBaseToObjectInspector(btMap.getKeyType()), dynamicSerDeStructBaseToObjectInspector(btMap.getValueType()));
        }
        if (bt.isPrimitive()) {
            final PrimitiveObjectInspectorUtils.PrimitiveTypeEntry pte = PrimitiveObjectInspectorUtils.getTypeEntryFromPrimitiveJavaClass(bt.getRealType());
            return PrimitiveObjectInspectorFactory.getPrimitiveJavaObjectInspector(pte.primitiveCategory);
        }
        final DynamicSerDeStructBase btStruct = (DynamicSerDeStructBase)bt;
        final DynamicSerDeFieldList fieldList = btStruct.getFieldList();
        final DynamicSerDeField[] fields = fieldList.getChildren();
        final ArrayList<String> fieldNames = new ArrayList<String>(fields.length);
        final ArrayList<ObjectInspector> fieldObjectInspectors = new ArrayList<ObjectInspector>(fields.length);
        for (final DynamicSerDeField field : fields) {
            fieldNames.add(field.name);
            fieldObjectInspectors.add(dynamicSerDeStructBaseToObjectInspector(field.getFieldType().getMyType()));
        }
        return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldObjectInspectors);
    }
    
    @Override
    public ObjectInspector getObjectInspector() throws SerDeException {
        return dynamicSerDeStructBaseToObjectInspector(this.bt);
    }
    
    @Override
    public Class<? extends Writable> getSerializedClass() {
        return BytesWritable.class;
    }
    
    @Override
    public Writable serialize(final Object obj, final ObjectInspector objInspector) throws SerDeException {
        try {
            this.bos_.reset();
            this.bt.serialize(obj, objInspector, this.oprot_);
            this.oprot_.getTransport().flush();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new SerDeException(e);
        }
        this.ret.set(this.bos_.getData(), 0, this.bos_.getLength());
        return this.ret;
    }
    
    @Override
    public SerDeStats getSerDeStats() {
        return null;
    }
    
    static {
        LOG = LogFactory.getLog(DynamicSerDe.class.getName());
    }
}
