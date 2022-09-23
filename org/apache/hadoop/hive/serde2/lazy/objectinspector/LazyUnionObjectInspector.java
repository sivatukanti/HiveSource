// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hive.serde2.lazy.LazyUnion;
import org.apache.hadoop.io.Text;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyObjectInspectorParameters;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.objectinspector.UnionObjectInspector;

public class LazyUnionObjectInspector implements UnionObjectInspector
{
    public static final Log LOG;
    private List<ObjectInspector> ois;
    private byte separator;
    private LazyObjectInspectorParameters lazyParams;
    
    protected LazyUnionObjectInspector() {
    }
    
    protected LazyUnionObjectInspector(final List<ObjectInspector> ois, final byte separator, final LazyObjectInspectorParameters lazyParams) {
        this.init(ois, separator, lazyParams);
    }
    
    @Override
    public String getTypeName() {
        return ObjectInspectorUtils.getStandardUnionTypeName(this);
    }
    
    protected void init(final List<ObjectInspector> ois, final byte separator, final LazyObjectInspectorParameters lazyParams) {
        this.separator = separator;
        this.lazyParams = lazyParams;
        (this.ois = new ArrayList<ObjectInspector>()).addAll(ois);
    }
    
    @Override
    public final ObjectInspector.Category getCategory() {
        return ObjectInspector.Category.UNION;
    }
    
    public byte getSeparator() {
        return this.separator;
    }
    
    public Text getNullSequence() {
        return this.lazyParams.getNullSequence();
    }
    
    public boolean isEscaped() {
        return this.lazyParams.isEscaped();
    }
    
    public byte getEscapeChar() {
        return this.lazyParams.getEscapeChar();
    }
    
    public LazyObjectInspectorParameters getLazyParams() {
        return this.lazyParams;
    }
    
    @Override
    public Object getField(final Object data) {
        if (data == null) {
            return null;
        }
        return ((LazyUnion)data).getField();
    }
    
    @Override
    public List<ObjectInspector> getObjectInspectors() {
        return this.ois;
    }
    
    @Override
    public byte getTag(final Object data) {
        if (data == null) {
            return -1;
        }
        return ((LazyUnion)data).getTag();
    }
    
    static {
        LOG = LogFactory.getLog(LazyUnionObjectInspector.class.getName());
    }
}
