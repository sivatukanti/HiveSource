// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.lazy.objectinspector;

import org.apache.commons.logging.LogFactory;
import java.util.Map;
import org.apache.hadoop.hive.serde2.lazy.LazyMap;
import java.util.List;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyObjectInspectorParametersImpl;
import org.apache.hadoop.hive.serde2.lazy.objectinspector.primitive.LazyObjectInspectorParameters;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.serde2.objectinspector.MapObjectInspector;

public class LazyMapObjectInspector implements MapObjectInspector
{
    public static final Log LOG;
    private ObjectInspector mapKeyObjectInspector;
    private ObjectInspector mapValueObjectInspector;
    private byte itemSeparator;
    private byte keyValueSeparator;
    private LazyObjectInspectorParameters lazyParams;
    
    protected LazyMapObjectInspector() {
        this.lazyParams = new LazyObjectInspectorParametersImpl();
    }
    
    protected LazyMapObjectInspector(final ObjectInspector mapKeyObjectInspector, final ObjectInspector mapValueObjectInspector, final byte itemSeparator, final byte keyValueSeparator, final Text nullSequence, final boolean escaped, final byte escapeChar) {
        this.mapKeyObjectInspector = mapKeyObjectInspector;
        this.mapValueObjectInspector = mapValueObjectInspector;
        this.itemSeparator = itemSeparator;
        this.keyValueSeparator = keyValueSeparator;
        this.lazyParams = new LazyObjectInspectorParametersImpl(escaped, escapeChar, false, null, null, nullSequence);
    }
    
    protected LazyMapObjectInspector(final ObjectInspector mapKeyObjectInspector, final ObjectInspector mapValueObjectInspector, final byte itemSeparator, final byte keyValueSeparator, final LazyObjectInspectorParameters lazyParams) {
        this.mapKeyObjectInspector = mapKeyObjectInspector;
        this.mapValueObjectInspector = mapValueObjectInspector;
        this.itemSeparator = itemSeparator;
        this.keyValueSeparator = keyValueSeparator;
        this.lazyParams = lazyParams;
    }
    
    @Override
    public final ObjectInspector.Category getCategory() {
        return ObjectInspector.Category.MAP;
    }
    
    @Override
    public String getTypeName() {
        return "map<" + this.mapKeyObjectInspector.getTypeName() + "," + this.mapValueObjectInspector.getTypeName() + ">";
    }
    
    @Override
    public ObjectInspector getMapKeyObjectInspector() {
        return this.mapKeyObjectInspector;
    }
    
    @Override
    public ObjectInspector getMapValueObjectInspector() {
        return this.mapValueObjectInspector;
    }
    
    @Override
    public Object getMapValueElement(final Object data, final Object key) {
        return (data == null || key == null) ? null : ((LazyMap)data).getMapValueElement(key);
    }
    
    @Override
    public Map<?, ?> getMap(final Object data) {
        if (data == null) {
            return null;
        }
        return ((LazyMap)data).getMap();
    }
    
    @Override
    public int getMapSize(final Object data) {
        if (data == null) {
            return -1;
        }
        return ((LazyMap)data).getMapSize();
    }
    
    public byte getItemSeparator() {
        return this.itemSeparator;
    }
    
    public byte getKeyValueSeparator() {
        return this.keyValueSeparator;
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
        LOG = LogFactory.getLog(LazyMapObjectInspector.class.getName());
    }
}
