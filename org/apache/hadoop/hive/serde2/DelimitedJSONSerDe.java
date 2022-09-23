// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2;

import org.apache.commons.logging.LogFactory;
import java.io.IOException;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.lazy.LazySerDeParameters;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.io.Writable;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe;

public class DelimitedJSONSerDe extends LazySimpleSerDe
{
    public static final Log LOG;
    
    public DelimitedJSONSerDe() throws SerDeException {
    }
    
    @Override
    public Object doDeserialize(final Writable field) throws SerDeException {
        DelimitedJSONSerDe.LOG.error("DelimitedJSONSerDe cannot deserialize.");
        throw new SerDeException("DelimitedJSONSerDe cannot deserialize.");
    }
    
    @Override
    protected void serializeField(final ByteStream.Output out, final Object obj, final ObjectInspector objInspector, final LazySerDeParameters serdeParams) throws SerDeException {
        Label_0090: {
            if (objInspector.getCategory().equals(ObjectInspector.Category.PRIMITIVE)) {
                if (!objInspector.getTypeName().equalsIgnoreCase("binary")) {
                    break Label_0090;
                }
            }
            try {
                LazySimpleSerDe.serialize(out, SerDeUtils.getJSONString(obj, objInspector, serdeParams.getNullSequence().toString()), PrimitiveObjectInspectorFactory.javaStringObjectInspector, serdeParams.getSeparators(), 1, serdeParams.getNullSequence(), serdeParams.isEscaped(), serdeParams.getEscapeChar(), serdeParams.getNeedsEscape());
                return;
            }
            catch (IOException e) {
                throw new SerDeException(e);
            }
        }
        super.serializeField(out, obj, objInspector, serdeParams);
    }
    
    static {
        LOG = LogFactory.getLog(DelimitedJSONSerDe.class.getName());
    }
}
