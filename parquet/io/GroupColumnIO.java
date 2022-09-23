// 
// Decompiled by Procyon v0.5.36
// 

package parquet.io;

import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import parquet.schema.Type;
import parquet.schema.GroupType;
import java.util.List;
import java.util.Map;
import parquet.Log;

public class GroupColumnIO extends ColumnIO
{
    private static final Log LOG;
    private final Map<String, ColumnIO> childrenByName;
    private final List<ColumnIO> children;
    private int childrenSize;
    
    GroupColumnIO(final GroupType groupType, final GroupColumnIO parent, final int index) {
        super(groupType, parent, index);
        this.childrenByName = new HashMap<String, ColumnIO>();
        this.children = new ArrayList<ColumnIO>();
        this.childrenSize = 0;
    }
    
    void add(final ColumnIO child) {
        this.children.add(child);
        this.childrenByName.put(child.getType().getName(), child);
        ++this.childrenSize;
    }
    
    @Override
    void setLevels(final int r, final int d, final String[] fieldPath, final int[] indexFieldPath, final List<ColumnIO> repetition, final List<ColumnIO> path) {
        super.setLevels(r, d, fieldPath, indexFieldPath, repetition, path);
        for (final ColumnIO child : this.children) {
            final String[] newFieldPath = Arrays.copyOf(fieldPath, fieldPath.length + 1);
            final int[] newIndexFieldPath = Arrays.copyOf(indexFieldPath, indexFieldPath.length + 1);
            newFieldPath[fieldPath.length] = child.getType().getName();
            newIndexFieldPath[indexFieldPath.length] = child.getIndex();
            List<ColumnIO> newRepetition;
            if (child.getType().isRepetition(Type.Repetition.REPEATED)) {
                newRepetition = new ArrayList<ColumnIO>(repetition);
                newRepetition.add(child);
            }
            else {
                newRepetition = repetition;
            }
            final List<ColumnIO> newPath = new ArrayList<ColumnIO>(path);
            newPath.add(child);
            child.setLevels(child.getType().isRepetition(Type.Repetition.REPEATED) ? (r + 1) : r, child.getType().isRepetition(Type.Repetition.REQUIRED) ? d : (d + 1), newFieldPath, newIndexFieldPath, newRepetition, newPath);
        }
    }
    
    @Override
    List<String[]> getColumnNames() {
        final ArrayList<String[]> result = new ArrayList<String[]>();
        for (final ColumnIO c : this.children) {
            result.addAll(c.getColumnNames());
        }
        return result;
    }
    
    @Override
    PrimitiveColumnIO getLast() {
        return this.children.get(this.children.size() - 1).getLast();
    }
    
    @Override
    PrimitiveColumnIO getFirst() {
        return this.children.get(0).getFirst();
    }
    
    public ColumnIO getChild(final String name) {
        return this.childrenByName.get(name);
    }
    
    public ColumnIO getChild(final int fieldIndex) {
        try {
            return this.children.get(fieldIndex);
        }
        catch (IndexOutOfBoundsException e) {
            throw new InvalidRecordException("could not get child " + fieldIndex + " from " + this.children, e);
        }
    }
    
    public int getChildrenCount() {
        return this.childrenSize;
    }
    
    static {
        LOG = Log.getLog(GroupColumnIO.class);
    }
}
