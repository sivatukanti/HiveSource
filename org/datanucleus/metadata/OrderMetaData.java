// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.io.Serializable;
import java.util.StringTokenizer;
import org.datanucleus.util.StringUtils;
import org.datanucleus.ClassLoaderResolver;
import java.util.ArrayList;
import java.util.List;

public class OrderMetaData extends MetaData implements ColumnMetaDataContainer
{
    protected String columnName;
    final List<ColumnMetaData> columns;
    protected IndexMetaData indexMetaData;
    protected IndexedValue indexed;
    protected String mappedBy;
    protected String ordering;
    protected FieldOrder[] fieldOrders;
    protected ColumnMetaData[] columnMetaData;
    
    public OrderMetaData(final OrderMetaData omd) {
        super(null, omd);
        this.columnName = null;
        this.columns = new ArrayList<ColumnMetaData>();
        this.indexed = null;
        this.mappedBy = null;
        this.ordering = null;
        this.fieldOrders = null;
        this.indexed = omd.indexed;
        this.columnName = omd.columnName;
        if (omd.indexMetaData != null) {
            this.indexMetaData = omd.indexMetaData;
            this.indexMetaData.parent = this;
        }
        for (int i = 0; i < omd.columns.size(); ++i) {
            this.addColumn(omd.columns.get(i));
        }
        this.mappedBy = omd.mappedBy;
        this.ordering = omd.ordering;
    }
    
    public OrderMetaData() {
        this.columnName = null;
        this.columns = new ArrayList<ColumnMetaData>();
        this.indexed = null;
        this.mappedBy = null;
        this.ordering = null;
        this.fieldOrders = null;
    }
    
    @Override
    public void initialise(final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        if (this.hasExtension("list-ordering")) {
            final String val = this.getValueForExtension("list-ordering");
            if (!StringUtils.isWhitespace(val)) {
                this.ordering = val;
            }
        }
        this.columnMetaData = new ColumnMetaData[this.columns.size()];
        if (this.columns.size() == 0 && this.columnName != null) {
            this.columnMetaData = new ColumnMetaData[1];
            (this.columnMetaData[0] = new ColumnMetaData()).setName(this.columnName);
            this.columnMetaData[0].parent = this;
            this.columnMetaData[0].initialise(clr, mmgr);
        }
        else {
            this.columnMetaData = new ColumnMetaData[this.columns.size()];
            for (int i = 0; i < this.columnMetaData.length; ++i) {
                (this.columnMetaData[i] = this.columns.get(i)).initialise(clr, mmgr);
            }
        }
        if (this.indexMetaData == null && this.columnMetaData != null && this.indexed != null && this.indexed != IndexedValue.FALSE) {
            (this.indexMetaData = new IndexMetaData()).setUnique(this.indexed == IndexedValue.UNIQUE);
            this.indexMetaData.parent = this;
            for (int i = 0; i < this.columnMetaData.length; ++i) {
                this.indexMetaData.addColumn(this.columnMetaData[i]);
            }
        }
        if (this.indexMetaData != null) {
            this.indexMetaData.initialise(clr, mmgr);
        }
        if (this.mappedBy != null) {
            final AbstractMemberMetaData fmd = (AbstractMemberMetaData)this.parent;
            final AbstractClassMetaData elementCmd = fmd.getCollection().element.classMetaData;
            if (elementCmd != null && !elementCmd.hasMember(this.mappedBy)) {
                throw new InvalidMemberMetaDataException(OrderMetaData.LOCALISER, "044137", fmd.getClassName(), fmd.getName(), elementCmd.getFullClassName(), this.mappedBy);
            }
        }
        this.setInitialised();
    }
    
    @Override
    public void addColumn(final ColumnMetaData colmd) {
        this.columns.add(colmd);
        colmd.parent = this;
    }
    
    public ColumnMetaData newColumnMetaData() {
        final ColumnMetaData colmd = new ColumnMetaData();
        this.addColumn(colmd);
        return colmd;
    }
    
    public final OrderMetaData setIndexed(final IndexedValue val) {
        this.indexed = val;
        return this;
    }
    
    public final OrderMetaData setIndexMetaData(final IndexMetaData indexMetaData) {
        this.indexMetaData = indexMetaData;
        return (OrderMetaData)(indexMetaData.parent = this);
    }
    
    public IndexMetaData newIndexMetaData() {
        final IndexMetaData idxmd = new IndexMetaData();
        this.setIndexMetaData(idxmd);
        return idxmd;
    }
    
    public boolean isIndexedList() {
        return this.ordering == null;
    }
    
    public String getMappedBy() {
        return this.mappedBy;
    }
    
    public OrderMetaData setMappedBy(final String mappedby) {
        this.mappedBy = (StringUtils.isWhitespace(mappedby) ? null : mappedby);
        return this;
    }
    
    public FieldOrder[] getFieldOrders() {
        if (this.ordering != null && this.fieldOrders == null) {
            FieldOrder[] theOrders = null;
            final AbstractMemberMetaData fmd = (AbstractMemberMetaData)this.parent;
            final AbstractClassMetaData elementCmd = fmd.hasCollection() ? fmd.getCollection().element.classMetaData : fmd.getArray().element.classMetaData;
            if (elementCmd != null && this.ordering.equals("#PK")) {
                theOrders = new FieldOrder[elementCmd.getNoOfPrimaryKeyMembers()];
                final String[] pkFieldNames = elementCmd.getPrimaryKeyMemberNames();
                int i = 0;
                for (int pkFieldNum = 0; pkFieldNum < theOrders.length; ++pkFieldNum) {
                    theOrders[i++] = new FieldOrder(pkFieldNames[pkFieldNum]);
                }
            }
            else if (elementCmd != null) {
                final StringTokenizer tokeniser = new StringTokenizer(this.ordering, ",");
                final int num = tokeniser.countTokens();
                theOrders = new FieldOrder[num];
                int j = 0;
                while (tokeniser.hasMoreTokens()) {
                    final String nextToken = tokeniser.nextToken().trim();
                    String fieldName = null;
                    boolean forward = true;
                    final int spacePos = nextToken.indexOf(32);
                    if (spacePos > 0) {
                        fieldName = nextToken.substring(0, spacePos);
                        final String direction = nextToken.substring(spacePos + 1).trim();
                        if (direction.equalsIgnoreCase("DESC")) {
                            forward = false;
                        }
                        else if (!direction.equalsIgnoreCase("ASC")) {
                            throw new InvalidMemberMetaDataException(OrderMetaData.LOCALISER, "044139", fmd.getClassName(), fmd.getName(), direction);
                        }
                    }
                    else {
                        fieldName = nextToken;
                    }
                    if (!elementCmd.hasMember(fieldName)) {
                        throw new InvalidMemberMetaDataException(OrderMetaData.LOCALISER, "044138", fmd.getClassName(), fmd.getName(), elementCmd.getFullClassName(), fieldName);
                    }
                    theOrders[j] = new FieldOrder(fieldName);
                    if (!forward) {
                        theOrders[j].setBackward();
                    }
                    ++j;
                }
            }
            else {
                theOrders = new FieldOrder[0];
            }
            this.fieldOrders = theOrders;
        }
        return this.fieldOrders;
    }
    
    @Override
    public final ColumnMetaData[] getColumnMetaData() {
        return this.columnMetaData;
    }
    
    public final String getColumnName() {
        return this.columnName;
    }
    
    public OrderMetaData setColumnName(final String column) {
        this.columnName = (StringUtils.isWhitespace(column) ? null : column);
        return this;
    }
    
    public final IndexMetaData getIndexMetaData() {
        return this.indexMetaData;
    }
    
    public String getOrdering() {
        return this.ordering;
    }
    
    public OrderMetaData setOrdering(final String ordering) {
        this.ordering = ordering;
        return this;
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<order");
        if (this.columnName != null) {
            sb.append(" column=\"" + this.columnName + "\"");
        }
        if (this.indexed != null) {
            sb.append(" indexed=\"" + this.indexed.toString() + "\"");
        }
        if (this.mappedBy != null) {
            sb.append(" mapped-by=\"" + this.mappedBy + "\"");
        }
        sb.append(">\n");
        for (int i = 0; i < this.columns.size(); ++i) {
            final ColumnMetaData c = this.columns.get(i);
            sb.append(c.toString(prefix + indent, indent));
        }
        if (this.indexMetaData != null) {
            sb.append(this.indexMetaData.toString(prefix + indent, indent));
        }
        sb.append(super.toString(prefix + indent, indent));
        sb.append(prefix).append("</order>\n");
        return sb.toString();
    }
    
    public static class FieldOrder implements Serializable
    {
        String fieldName;
        boolean forward;
        
        public FieldOrder(final String name) {
            this.forward = true;
            this.fieldName = name;
        }
        
        public void setBackward() {
            this.forward = false;
        }
        
        public String getFieldName() {
            return this.fieldName;
        }
        
        public boolean isForward() {
            return this.forward;
        }
    }
}
