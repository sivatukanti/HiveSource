// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.Text;
import java.util.List;
import org.apache.hadoop.hive.serde2.lazy.LazyArray;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyObjectInspectorParameters;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.objectinspector.ListObjectInspector;

public class LazyListObjectInspector implements ListObjectInspector
{
    public static final Log LOG;
    private ObjectInspector listElementObjectInspector;
    private byte separator;
    private LazyObjectInspectorParameters lazyParams;
    
    protected LazyListObjectInspector() {
    }
    
    protected LazyListObjectInspector(final ObjectInspector listElementObjectInspector, final byte separator, final LazyObjectInspectorParameters lazyParams) {
        this.listElementObjectInspector = listElementObjectInspector;
        this.separator = separator;
        this.lazyParams = lazyParams;
    }
    
    @Override
    public final ObjectInspector.Category getCategory() {
        return ObjectInspector.Category.LIST;
    }
    
    @Override
    public ObjectInspector getListElementObjectInspector() {
        return this.listElementObjectInspector;
    }
    
    @Override
    public Object getListElement(final Object data, final int index) {
        if (data == null) {
            return null;
        }
        final LazyArray array = (LazyArray)data;
        return array.getListElementObject(index);
    }
    
    @Override
    public int getListLength(final Object data) {
        if (data == null) {
            return -1;
        }
        final LazyArray array = (LazyArray)data;
        return array.getListLength();
    }
    
    @Override
    public List<?> getList(final Object data) {
        if (data == null) {
            return null;
        }
        final LazyArray array = (LazyArray)data;
        return array.getList();
    }
    
    @Override
    public String getTypeName() {
        return "array<" + this.listElementObjectInspector.getTypeName() + ">";
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
    
    static {
        LOG = LogFactory.getLog(LazyListObjectInspector.class.getName());
    }
}
