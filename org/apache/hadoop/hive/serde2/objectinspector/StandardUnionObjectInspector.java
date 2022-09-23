// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

import java.util.ArrayList;
import java.util.List;

public class StandardUnionObjectInspector extends SettableUnionObjectInspector
{
    private List<ObjectInspector> ois;
    
    protected StandardUnionObjectInspector() {
    }
    
    public StandardUnionObjectInspector(final List<ObjectInspector> ois) {
        this.ois = ois;
    }
    
    @Override
    public List<ObjectInspector> getObjectInspectors() {
        return this.ois;
    }
    
    @Override
    public byte getTag(final Object o) {
        if (o == null) {
            return -1;
        }
        return ((UnionObject)o).getTag();
    }
    
    @Override
    public Object getField(final Object o) {
        if (o == null) {
            return null;
        }
        return ((UnionObject)o).getObject();
    }
    
    @Override
    public ObjectInspector.Category getCategory() {
        return ObjectInspector.Category.UNION;
    }
    
    @Override
    public String getTypeName() {
        return ObjectInspectorUtils.getStandardUnionTypeName(this);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName());
        sb.append(this.getTypeName());
        return sb.toString();
    }
    
    @Override
    public Object create() {
        final ArrayList<Object> a = new ArrayList<Object>();
        return a;
    }
    
    @Override
    public Object addField(final Object union, final ObjectInspector oi) {
        final ArrayList<Object> a = (ArrayList<Object>)union;
        a.add(oi);
        return a;
    }
    
    public static class StandardUnion implements UnionObject
    {
        protected byte tag;
        protected Object object;
        
        public StandardUnion() {
        }
        
        public StandardUnion(final byte tag, final Object object) {
            this.tag = tag;
            this.object = object;
        }
        
        public void setObject(final Object o) {
            this.object = o;
        }
        
        public void setTag(final byte tag) {
            this.tag = tag;
        }
        
        @Override
        public Object getObject() {
            return this.object;
        }
        
        @Override
        public byte getTag() {
            return this.tag;
        }
        
        @Override
        public String toString() {
            return this.tag + ":" + this.object;
        }
    }
}
