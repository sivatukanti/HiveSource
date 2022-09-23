// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

public class StatementClassMapping
{
    public static final int MEMBER_DATASTORE_ID = -1;
    public static final int MEMBER_VERSION = -2;
    public static final int MEMBER_DISCRIMINATOR = -3;
    String className;
    String memberName;
    String nucleusTypeColumn;
    int[] memberNumbers;
    Map<Integer, StatementMappingIndex> mappings;
    Map<Integer, StatementClassMapping> children;
    
    public StatementClassMapping() {
        this(null, null);
    }
    
    public StatementClassMapping(final String className, final String memberName) {
        this.mappings = new HashMap<Integer, StatementMappingIndex>();
        this.className = className;
        this.memberName = memberName;
    }
    
    public StatementClassMapping(final String memberName) {
        this(null, memberName);
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public String getMemberName() {
        return this.memberName;
    }
    
    public void setNucleusTypeColumnName(final String colName) {
        this.nucleusTypeColumn = colName;
    }
    
    public String getNucleusTypeColumnName() {
        return this.nucleusTypeColumn;
    }
    
    public StatementMappingIndex getMappingForMemberPosition(final int position) {
        return this.mappings.get(position);
    }
    
    public StatementClassMapping getMappingDefinitionForMemberPosition(final int position) {
        if (this.children != null) {
            return this.children.get(position);
        }
        return null;
    }
    
    public boolean hasChildMappingDefinitions() {
        return this.children != null && this.children.size() > 0;
    }
    
    public int[] getMemberNumbers() {
        if (this.memberNumbers != null) {
            return this.memberNumbers;
        }
        int length = this.mappings.size();
        if (this.mappings.containsKey(-1)) {
            --length;
        }
        if (this.mappings.containsKey(-2)) {
            --length;
        }
        if (this.mappings.containsKey(-3)) {
            --length;
        }
        final int[] positions = new int[length];
        final Iterator<Integer> iter = this.mappings.keySet().iterator();
        int i = 0;
        while (iter.hasNext()) {
            final Integer val = iter.next();
            if (val >= 0) {
                positions[i++] = val;
            }
        }
        return this.memberNumbers = positions;
    }
    
    public void addMappingForMember(final int position, final StatementMappingIndex mapping) {
        this.memberNumbers = null;
        this.mappings.put(position, mapping);
    }
    
    public void addMappingDefinitionForMember(final int position, final StatementClassMapping defn) {
        this.memberNumbers = null;
        if (this.children == null) {
            this.children = new HashMap<Integer, StatementClassMapping>();
        }
        this.children.put(position, defn);
    }
    
    public StatementClassMapping cloneStatementMappingWithoutChildren() {
        final StatementClassMapping mapping = new StatementClassMapping(this.className, this.memberName);
        mapping.nucleusTypeColumn = this.nucleusTypeColumn;
        for (final Map.Entry entry : this.mappings.entrySet()) {
            final Integer key = entry.getKey();
            final StatementMappingIndex value = entry.getValue();
            mapping.addMappingForMember(key, value);
        }
        return mapping;
    }
    
    @Override
    public String toString() {
        final StringBuffer str = new StringBuffer("StatementClassMapping:");
        str.append("class=" + this.className + ",member=" + this.memberName);
        str.append(",mappings=[");
        final Iterator<Map.Entry<Integer, StatementMappingIndex>> mapIter = this.mappings.entrySet().iterator();
        while (mapIter.hasNext()) {
            final Map.Entry<Integer, StatementMappingIndex> entry = mapIter.next();
            str.append("{field=").append(entry.getKey());
            str.append(",mapping=").append(entry.getValue());
            str.append("}");
            if (mapIter.hasNext() || this.children != null) {
                str.append(",");
            }
        }
        str.append("]");
        if (this.children != null) {
            str.append(",children=[");
            final Iterator<Map.Entry<Integer, StatementClassMapping>> childIter = this.children.entrySet().iterator();
            while (childIter.hasNext()) {
                final Map.Entry<Integer, StatementClassMapping> entry2 = childIter.next();
                str.append("{field=").append(entry2.getKey());
                str.append(",mapping=").append(entry2.getValue());
                str.append("}");
                if (childIter.hasNext()) {
                    str.append(",");
                }
            }
            str.append("]");
        }
        if (this.nucleusTypeColumn != null) {
            str.append(",nucleusTypeColumn=" + this.nucleusTypeColumn);
        }
        return str.toString();
    }
}
