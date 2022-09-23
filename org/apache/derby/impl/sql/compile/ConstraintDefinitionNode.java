// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.monitor.Monitor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.util.ReuseFactory;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.services.uuid.UUIDFactory;
import org.apache.derby.iapi.sql.depend.ProviderList;
import java.util.Properties;

public class ConstraintDefinitionNode extends TableElementNode
{
    private TableName constraintName;
    protected int constraintType;
    protected Properties properties;
    ProviderList apl;
    UUIDFactory uuidFactory;
    String backingIndexName;
    UUID backingIndexUUID;
    int[] checkColumnReferences;
    ResultColumnList columnList;
    String constraintText;
    ValueNode checkCondition;
    private int behavior;
    private int verifyType;
    
    public ConstraintDefinitionNode() {
        this.verifyType = 5;
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7) {
        this.constraintName = (TableName)o;
        super.init(null);
        if (this.constraintName != null) {
            this.name = this.constraintName.getTableName();
        }
        this.constraintType = (int)o2;
        this.properties = (Properties)o4;
        this.columnList = (ResultColumnList)o3;
        this.checkCondition = (ValueNode)o5;
        this.constraintText = (String)o6;
        this.behavior = (int)o7;
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6) {
        this.init(o, o2, o3, o4, o5, o6, ReuseFactory.getInteger(2));
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5, final Object o6, final Object o7, final Object o8) {
        this.init(o, o2, o3, o4, o5, o6, o7);
        this.verifyType = (int)o8;
    }
    
    public String toString() {
        return "";
    }
    
    protected void bind(final DDLStatementNode ddlStatementNode, final DataDictionary dataDictionary) throws StandardException {
        if (this.constraintType == 5) {
            return;
        }
        if (this.constraintName != null) {
            final String schemaName = this.constraintName.getSchemaName();
            if (schemaName != null) {
                final TableName objectName = ddlStatementNode.getObjectName();
                String s = objectName.getSchemaName();
                if (s == null) {
                    s = this.getSchemaDescriptor(null).getSchemaName();
                    objectName.setSchemaName(s);
                }
                if (!schemaName.equals(s)) {
                    throw StandardException.newException("42X85", this.constraintName, objectName);
                }
            }
        }
        else {
            this.name = this.getBackingIndexName(dataDictionary);
        }
    }
    
    String getConstraintMoniker() {
        return this.name;
    }
    
    String getDropSchemaName() {
        if (this.constraintName != null) {
            return this.constraintName.getSchemaName();
        }
        return null;
    }
    
    UUID getBackingIndexUUID() {
        if (this.backingIndexUUID == null) {
            this.backingIndexUUID = this.getUUIDFactory().createUUID();
        }
        return this.backingIndexUUID;
    }
    
    String getBackingIndexName(final DataDictionary dataDictionary) {
        if (this.backingIndexName == null) {
            this.backingIndexName = dataDictionary.getSystemSQLName();
        }
        return this.backingIndexName;
    }
    
    void setAuxiliaryProviderList(final ProviderList apl) {
        this.apl = apl;
    }
    
    public ProviderList getAuxiliaryProviderList() {
        return this.apl;
    }
    
    boolean hasPrimaryKeyConstraint() {
        return this.constraintType == 2;
    }
    
    boolean hasUniqueKeyConstraint() {
        return this.constraintType == 3;
    }
    
    boolean hasForeignKeyConstraint() {
        return this.constraintType == 6;
    }
    
    boolean hasCheckConstraint() {
        return this.constraintType == 4;
    }
    
    boolean hasConstraint() {
        return true;
    }
    
    boolean requiresBackingIndex() {
        switch (this.constraintType) {
            case 2:
            case 3:
            case 6: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    boolean requiresUniqueIndex() {
        switch (this.constraintType) {
            case 2:
            case 3: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    int getConstraintType() {
        return this.constraintType;
    }
    
    public void setProperties(final Properties properties) {
        this.properties = properties;
    }
    
    public Properties getProperties() {
        return this.properties;
    }
    
    public boolean isReferenced() {
        return false;
    }
    
    public int getReferenceCount() {
        return 0;
    }
    
    public boolean isEnabled() {
        return true;
    }
    
    public ResultColumnList getColumnList() {
        return this.columnList;
    }
    
    public void setColumnList(final ResultColumnList columnList) {
        this.columnList = columnList;
    }
    
    public ValueNode getCheckCondition() {
        return this.checkCondition;
    }
    
    public void setCheckCondition(final ValueNode checkCondition) {
        this.checkCondition = checkCondition;
    }
    
    public String getConstraintText() {
        return this.constraintText;
    }
    
    public int[] getCheckColumnReferences() {
        return this.checkColumnReferences;
    }
    
    public void setCheckColumnReferences(final int[] checkColumnReferences) {
        this.checkColumnReferences = checkColumnReferences;
    }
    
    int getDropBehavior() {
        return this.behavior;
    }
    
    int getVerifyType() {
        return this.verifyType;
    }
    
    private UUIDFactory getUUIDFactory() {
        if (this.uuidFactory == null) {
            this.uuidFactory = Monitor.getMonitor().getUUIDFactory();
        }
        return this.uuidFactory;
    }
}
