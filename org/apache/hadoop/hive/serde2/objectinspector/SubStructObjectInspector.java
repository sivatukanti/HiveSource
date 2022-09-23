// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.serde2.objectinspector;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class SubStructObjectInspector extends StructObjectInspector
{
    protected StructObjectInspector baseOI;
    protected int startCol;
    protected int numCols;
    protected List<StructField> fields;
    
    public SubStructObjectInspector(final StructObjectInspector baseOI, final int startCol, final int numCols) {
        this.baseOI = baseOI;
        this.startCol = startCol;
        this.numCols = numCols;
        final List<? extends StructField> baseFields = baseOI.getAllStructFieldRefs();
        assert startCol < baseFields.size() && startCol + numCols < baseFields.size();
        (this.fields = new ArrayList<StructField>(numCols)).addAll(baseOI.getAllStructFieldRefs().subList(startCol, startCol + numCols));
    }
    
    @Override
    public String getTypeName() {
        return ObjectInspectorUtils.getStandardStructTypeName(this);
    }
    
    @Override
    public ObjectInspector.Category getCategory() {
        return ObjectInspector.Category.STRUCT;
    }
    
    @Override
    public List<? extends StructField> getAllStructFieldRefs() {
        return this.fields;
    }
    
    @Override
    public StructField getStructFieldRef(final String fieldName) {
        return ObjectInspectorUtils.getStandardStructFieldRef(fieldName, this.fields);
    }
    
    @Override
    public Object getStructFieldData(final Object data, final StructField fieldRef) {
        return this.baseOI.getStructFieldData(data, fieldRef);
    }
    
    @Override
    public List<Object> getStructFieldsDataAsList(final Object data) {
        return this.baseOI.getStructFieldsDataAsList(data).subList(this.startCol, this.startCol + this.numCols);
    }
}
