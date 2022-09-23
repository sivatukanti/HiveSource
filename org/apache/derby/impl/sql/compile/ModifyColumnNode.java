// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.dictionary.DefaultDescriptor;
import org.apache.derby.catalog.types.DefaultInfoImpl;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptor;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptorList;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.dictionary.KeyConstraintDescriptor;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.catalog.UUID;

public class ModifyColumnNode extends ColumnDefinitionNode
{
    int columnPosition;
    UUID oldDefaultUUID;
    
    public ModifyColumnNode() {
        this.columnPosition = -1;
    }
    
    UUID getOldDefaultUUID() {
        return this.oldDefaultUUID;
    }
    
    public int getColumnPosition() {
        return this.columnPosition;
    }
    
    public void checkUserType(final TableDescriptor tableDescriptor) throws StandardException {
        if (this.getNodeType() != 186) {
            return;
        }
        final ColumnDescriptor columnDescriptor = tableDescriptor.getColumnDescriptor(this.name);
        if (columnDescriptor == null) {
            throw StandardException.newException("42X14", this.name, tableDescriptor.getName());
        }
        final DataTypeDescriptor type = columnDescriptor.getType();
        this.setNullability(type.isNullable());
        if (!type.getTypeId().equals(this.getType().getTypeId())) {
            throw StandardException.newException("42Z15", this.name);
        }
        final String typeName = this.getType().getTypeName();
        if (!typeName.equals("VARCHAR") && !typeName.equals("VARCHAR () FOR BIT DATA") && !typeName.equals("BLOB") && !typeName.equals("CLOB")) {
            throw StandardException.newException("42Z16");
        }
        if (this.getType().getMaximumWidth() < type.getMaximumWidth()) {
            throw StandardException.newException("42Z17", this.name);
        }
    }
    
    public void checkExistingConstraints(final TableDescriptor tableDescriptor) throws StandardException {
        if (this.getNodeType() != 186 && this.getNodeType() != 187 && this.getNodeType() != 193) {
            return;
        }
        final DataDictionary dataDictionary = this.getDataDictionary();
        final ConstraintDescriptorList constraintDescriptors = dataDictionary.getConstraintDescriptors(tableDescriptor);
        final int[] array = { this.columnPosition };
        for (int i = 0; i < constraintDescriptors.size(); ++i) {
            final ConstraintDescriptor element = constraintDescriptors.elementAt(i);
            if (element instanceof KeyConstraintDescriptor) {
                if (element.columnIntersects(array)) {
                    if (element.getConstraintType() == 6 && this.getNodeType() == 186) {
                        throw StandardException.newException("42Z18", this.name, element.getConstraintName());
                    }
                    if (!dataDictionary.checkVersion(160, null) && this.getNodeType() == 187 && element.getConstraintType() == 3) {
                        throw StandardException.newException("42Z20", this.name);
                    }
                    if (this.getNodeType() == 187 && element.getConstraintType() == 2) {
                        throw StandardException.newException(this.getLanguageConnectionContext().getDataDictionary().checkVersion(160, null) ? "42Z20.S.1" : "42Z20", this.name);
                    }
                    final ConstraintDescriptorList foreignKeys = dataDictionary.getForeignKeys(element.getUUID());
                    if (foreignKeys.size() > 0) {
                        throw StandardException.newException("42Z19", this.name, foreignKeys.elementAt(0).getConstraintName());
                    }
                    this.getCompilerContext().createDependency(element);
                }
            }
        }
    }
    
    public void useExistingCollation(final TableDescriptor tableDescriptor) throws StandardException {
        final ColumnDescriptor columnDescriptor = tableDescriptor.getColumnDescriptor(this.name);
        if (columnDescriptor == null) {
            throw StandardException.newException("42X14", this.name, tableDescriptor.getName());
        }
        if (this.getType() != null && this.getType().getTypeId().isStringTypeId()) {
            this.setCollationType(columnDescriptor.getType().getCollationType());
        }
    }
    
    int getAction() {
        switch (this.getNodeType()) {
            case 97: {
                if (this.autoinc_create_or_modify_Start_Increment == 1L) {
                    return 5;
                }
                if (this.autoinc_create_or_modify_Start_Increment == 2L) {
                    return 6;
                }
                return 7;
            }
            case 186: {
                return 2;
            }
            case 187: {
                return 3;
            }
            case 193: {
                return 4;
            }
            case 113: {
                return 1;
            }
            default: {
                return 0;
            }
        }
    }
    
    void bindAndValidateDefault(final DataDictionary dataDictionary, final TableDescriptor tableDescriptor) throws StandardException {
        final ColumnDescriptor columnDescriptor = tableDescriptor.getColumnDescriptor(this.name);
        if (columnDescriptor == null) {
            throw StandardException.newException("42X14", this.name, tableDescriptor.getName());
        }
        final DefaultDescriptor defaultDescriptor = columnDescriptor.getDefaultDescriptor(dataDictionary);
        this.oldDefaultUUID = ((defaultDescriptor == null) ? null : defaultDescriptor.getUUID());
        this.columnPosition = columnDescriptor.getPosition();
        if (this.getNodeType() != 97) {
            return;
        }
        if (this.keepCurrentDefault) {
            this.defaultInfo = (DefaultInfoImpl)columnDescriptor.getDefaultInfo();
        }
        else if (columnDescriptor.hasGenerationClause()) {
            throw StandardException.newException("42XA7", columnDescriptor.getColumnName());
        }
        if (this.autoinc_create_or_modify_Start_Increment == 1L) {
            this.autoincrementIncrement = columnDescriptor.getAutoincInc();
        }
        if (this.autoinc_create_or_modify_Start_Increment == 2L) {
            this.autoincrementStart = columnDescriptor.getAutoincStart();
        }
        this.type = columnDescriptor.getType();
        this.validateDefault(dataDictionary, tableDescriptor);
    }
    
    private ColumnDescriptor getLocalColumnDescriptor(final String s, final TableDescriptor tableDescriptor) throws StandardException {
        final ColumnDescriptor columnDescriptor = tableDescriptor.getColumnDescriptor(s);
        if (columnDescriptor == null) {
            throw StandardException.newException("42X14", s, tableDescriptor.getName());
        }
        return columnDescriptor;
    }
    
    public void validateAutoincrement(final DataDictionary dataDictionary, final TableDescriptor tableDescriptor, final int n) throws StandardException {
        if (this.getNodeType() == 187 && this.getLocalColumnDescriptor(this.name, tableDescriptor).isAutoincrement()) {
            throw StandardException.newException("42Z26", this.getColumnName());
        }
        if (this.autoincrementVerify && !this.getLocalColumnDescriptor(this.name, tableDescriptor).isAutoincrement()) {
            throw StandardException.newException("42837", tableDescriptor.getQualifiedName(), this.name);
        }
        if (!this.isAutoincrement) {
            return;
        }
        super.validateAutoincrement(dataDictionary, tableDescriptor, n);
        if (this.getType().isNullable()) {
            throw StandardException.newException("42Z27", this.getColumnName());
        }
    }
}
