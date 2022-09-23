// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.types.VariableSizeDataValue;
import java.io.Serializable;
import org.apache.derby.iapi.sql.execute.ExecutionContext;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.RowLocation;
import java.sql.SQLWarning;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import org.apache.derby.vti.RestrictedVTI;
import org.apache.derby.vti.DeferModification;
import org.apache.derby.vti.IQualifyable;
import org.apache.derby.vti.Pushable;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.execute.ExecRowBuilder;
import org.apache.derby.iapi.sql.Activation;
import org.apache.derby.vti.Restriction;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.catalog.TypeDescriptor;
import org.apache.derby.iapi.store.access.Qualifier;
import org.apache.derby.vti.IFastPath;
import org.apache.derby.iapi.services.io.FormatableHashtable;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.execute.ExecRow;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import org.apache.derby.iapi.services.loader.GeneratedMethod;
import org.apache.derby.vti.VTIEnvironment;
import org.apache.derby.iapi.sql.execute.CursorResultSet;

class VTIResultSet extends NoPutResultSetImpl implements CursorResultSet, VTIEnvironment
{
    public int rowsReturned;
    public String javaClassName;
    private GeneratedMethod constructor;
    private PreparedStatement userPS;
    private java.sql.ResultSet userVTI;
    private final ExecRow allocatedRow;
    private FormatableBitSet referencedColumns;
    private boolean version2;
    private boolean reuseablePs;
    private boolean isTarget;
    private FormatableHashtable compileTimeConstants;
    private int ctcNumber;
    private boolean pushedProjection;
    private IFastPath fastPath;
    private Qualifier[][] pushedQualifiers;
    private boolean[] runtimeNullableColumn;
    private boolean isDerbyStyleTableFunction;
    private final TypeDescriptor returnType;
    private DataTypeDescriptor[] returnColumnTypes;
    private String[] vtiProjection;
    private Restriction vtiRestriction;
    private int scanIsolationLevel;
    
    VTIResultSet(final Activation activation, final int n, final int n2, final GeneratedMethod constructor, final String javaClassName, final Qualifier[][] pushedQualifiers, final int n3, final boolean version2, final boolean reuseablePs, final int ctcNumber, final boolean isTarget, final int scanIsolationLevel, final double n4, final double n5, final boolean isDerbyStyleTableFunction, final int n6, final int n7, final int n8) throws StandardException {
        super(activation, n2, n4, n5);
        this.scanIsolationLevel = 0;
        this.constructor = constructor;
        this.javaClassName = javaClassName;
        this.version2 = version2;
        this.reuseablePs = reuseablePs;
        this.isTarget = isTarget;
        this.pushedQualifiers = pushedQualifiers;
        this.scanIsolationLevel = scanIsolationLevel;
        this.isDerbyStyleTableFunction = isDerbyStyleTableFunction;
        this.allocatedRow = ((ExecRowBuilder)activation.getPreparedStatement().getSavedObject(n)).build(activation.getExecutionFactory());
        this.returnType = ((n6 == -1) ? null : ((TypeDescriptor)activation.getPreparedStatement().getSavedObject(n6)));
        this.vtiProjection = (String[])((n7 == -1) ? null : activation.getPreparedStatement().getSavedObject(n7));
        this.vtiRestriction = ((n8 == -1) ? null : ((Restriction)activation.getPreparedStatement().getSavedObject(n8)));
        if (n3 != -1) {
            this.referencedColumns = (FormatableBitSet)activation.getPreparedStatement().getSavedObject(n3);
        }
        this.ctcNumber = ctcNumber;
        this.compileTimeConstants = (FormatableHashtable)activation.getPreparedStatement().getSavedObject(ctcNumber);
        this.recordConstructorTime();
    }
    
    public void openCore() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        this.isOpen = true;
        ++this.numOpens;
        try {
            if (this.version2) {
                this.userPS = (PreparedStatement)this.constructor.invoke(this.activation);
                if (this.userPS instanceof Pushable) {
                    final Pushable pushable = (Pushable)this.userPS;
                    if (this.referencedColumns != null) {
                        this.pushedProjection = pushable.pushProjection(this, this.getProjectedColList());
                    }
                }
                if (this.userPS instanceof IQualifyable) {
                    ((IQualifyable)this.userPS).setQualifiers(this, this.pushedQualifiers);
                }
                this.fastPath = ((this.userPS instanceof IFastPath) ? this.userPS : null);
                if (this.isTarget && this.userPS instanceof DeferModification && this.activation.getConstantAction() instanceof UpdatableVTIConstantAction) {
                    final UpdatableVTIConstantAction updatableVTIConstantAction = (UpdatableVTIConstantAction)this.activation.getConstantAction();
                    ((DeferModification)this.userPS).modificationNotify(updatableVTIConstantAction.statementType, updatableVTIConstantAction.deferred);
                }
                if (this.fastPath == null || !this.fastPath.executeAsFastPath()) {
                    this.userVTI = this.userPS.executeQuery();
                }
                if (this.isTarget) {
                    this.activation.setTargetVTI(this.userVTI);
                }
            }
            else {
                this.userVTI = (java.sql.ResultSet)this.constructor.invoke(this.activation);
                if (this.userVTI instanceof RestrictedVTI) {
                    ((RestrictedVTI)this.userVTI).initScan(this.vtiProjection, this.cloneRestriction(this.activation));
                }
            }
            this.setNullableColumnList();
        }
        catch (Throwable t) {
            throw StandardException.unexpectedUserException(t);
        }
        this.openTime += this.getElapsedMillis(this.beginTime);
    }
    
    private Restriction cloneRestriction(final Activation activation) throws StandardException {
        if (this.vtiRestriction == null) {
            return null;
        }
        return this.cloneRestriction(activation, this.vtiRestriction);
    }
    
    private Restriction cloneRestriction(final Activation activation, final Restriction restriction) throws StandardException {
        if (restriction instanceof Restriction.AND) {
            final Restriction.AND and = (Restriction.AND)restriction;
            return new Restriction.AND(this.cloneRestriction(activation, and.getLeftChild()), this.cloneRestriction(activation, and.getRightChild()));
        }
        if (restriction instanceof Restriction.OR) {
            final Restriction.OR or = (Restriction.OR)restriction;
            return new Restriction.OR(this.cloneRestriction(activation, or.getLeftChild()), this.cloneRestriction(activation, or.getRightChild()));
        }
        if (restriction instanceof Restriction.ColumnQualifier) {
            final Restriction.ColumnQualifier columnQualifier = (Restriction.ColumnQualifier)restriction;
            final Object constantOperand = columnQualifier.getConstantOperand();
            Object object;
            if (constantOperand == null) {
                object = null;
            }
            else if (constantOperand instanceof int[]) {
                object = activation.getParameterValueSet().getParameter(((int[])constantOperand)[0]).getObject();
            }
            else {
                object = constantOperand;
            }
            return new Restriction.ColumnQualifier(columnQualifier.getColumnName(), columnQualifier.getComparisonOperator(), object);
        }
        throw StandardException.newException("0A000.S", restriction.getClass().getName());
    }
    
    private boolean[] setNullableColumnList() throws SQLException, StandardException {
        if (this.runtimeNullableColumn != null) {
            return this.runtimeNullableColumn;
        }
        if (this.isDerbyStyleTableFunction) {
            final int n = this.getAllocatedRow().nColumns() + 1;
            this.runtimeNullableColumn = new boolean[n];
            for (int i = 0; i < n; ++i) {
                this.runtimeNullableColumn[i] = true;
            }
            return this.runtimeNullableColumn;
        }
        if (this.userVTI == null) {
            return null;
        }
        final ResultSetMetaData metaData = this.userVTI.getMetaData();
        final boolean[] runtimeNullableColumn = new boolean[metaData.getColumnCount() + 1];
        for (int j = 1; j < runtimeNullableColumn.length; ++j) {
            runtimeNullableColumn[j] = (metaData.isNullable(j) != 0);
        }
        return this.runtimeNullableColumn = runtimeNullableColumn;
    }
    
    public void reopenCore() throws StandardException {
        if (this.reuseablePs) {
            if (this.userVTI == null) {
                return;
            }
            try {
                this.userVTI.close();
                this.userVTI = this.userPS.executeQuery();
                if (this.isTarget) {
                    this.activation.setTargetVTI(this.userVTI);
                }
                return;
            }
            catch (SQLException ex) {
                throw StandardException.unexpectedUserException(ex);
            }
        }
        this.close();
        this.openCore();
    }
    
    public ExecRow getNextRowCore() throws StandardException {
        if (this.isXplainOnlyMode()) {
            return null;
        }
        ExecRow currentRow = null;
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            try {
                if (this.userVTI == null && this.fastPath != null) {
                    currentRow = this.getAllocatedRow();
                    final int nextRow = this.fastPath.nextRow(currentRow.getRowArray());
                    if (nextRow != 0) {
                        if (nextRow == -1) {
                            currentRow = null;
                        }
                        else if (nextRow == 1) {
                            this.userVTI = this.userPS.executeQuery();
                        }
                    }
                }
                if (this.userVTI != null) {
                    if (!this.userVTI.next()) {
                        if (null != this.fastPath) {
                            this.fastPath.rowsDone();
                        }
                        currentRow = null;
                    }
                    else {
                        currentRow = this.getAllocatedRow();
                        this.populateFromResultSet(currentRow);
                        if (this.fastPath != null) {
                            this.fastPath.currentRow(this.userVTI, currentRow.getRowArray());
                        }
                        final SQLWarning warnings = this.userVTI.getWarnings();
                        if (warnings != null) {
                            this.addWarning(warnings);
                        }
                    }
                }
            }
            catch (Throwable t) {
                throw StandardException.unexpectedUserException(t);
            }
        }
        this.setCurrentRow(currentRow);
        if (currentRow != null) {
            ++this.rowsReturned;
            ++this.rowsSeen;
        }
        this.nextTime += this.getElapsedMillis(this.beginTime);
        return currentRow;
    }
    
    public void close() throws StandardException {
        this.beginTime = this.getCurrentTimeMillis();
        if (this.isOpen) {
            this.clearCurrentRow();
            if (this.userVTI != null) {
                try {
                    this.userVTI.close();
                }
                catch (SQLException ex) {
                    throw StandardException.unexpectedUserException(ex);
                }
                finally {
                    this.userVTI = null;
                }
            }
            if (this.userPS != null && !this.reuseablePs) {
                try {
                    this.userPS.close();
                }
                catch (SQLException ex2) {
                    throw StandardException.unexpectedUserException(ex2);
                }
                finally {
                    this.userPS = null;
                }
            }
            super.close();
        }
        this.closeTime += this.getElapsedMillis(this.beginTime);
    }
    
    public void finish() throws StandardException {
        if (this.userPS != null && !this.reuseablePs) {
            try {
                this.userPS.close();
                this.userPS = null;
            }
            catch (SQLException ex) {
                throw StandardException.unexpectedUserException(ex);
            }
        }
        this.finishAndRTS();
    }
    
    public long getTimeSpent(final int n) {
        return this.constructorTime + this.openTime + this.nextTime + this.closeTime;
    }
    
    public RowLocation getRowLocation() {
        return null;
    }
    
    public ExecRow getCurrentRow() {
        return null;
    }
    
    GeneratedMethod getVTIConstructor() {
        return this.constructor;
    }
    
    boolean isReuseablePs() {
        return this.reuseablePs;
    }
    
    private ExecRow getAllocatedRow() throws StandardException {
        return this.allocatedRow;
    }
    
    private int[] getProjectedColList() {
        final FormatableBitSet referencedColumns = this.referencedColumns;
        final int size = referencedColumns.size();
        int n = 0;
        for (int i = 0; i < size; ++i) {
            if (referencedColumns.isSet(i)) {
                ++n;
            }
        }
        final int[] array = new int[n];
        int n2 = 0;
        for (int j = 0; j < size; ++j) {
            if (referencedColumns.isSet(j)) {
                array[n2++] = j + 1;
            }
        }
        return array;
    }
    
    public void populateFromResultSet(final ExecRow execRow) throws StandardException {
        try {
            DataTypeDescriptor[] returnColumnTypes = null;
            if (this.isDerbyStyleTableFunction) {
                returnColumnTypes = this.getReturnColumnTypes();
            }
            final boolean[] setNullableColumnList = this.setNullableColumnList();
            final DataValueDescriptor[] rowArray = execRow.getRowArray();
            int n = 1;
            for (int i = 0; i < rowArray.length; ++i) {
                if (this.referencedColumns != null && !this.referencedColumns.get(i)) {
                    if (!this.pushedProjection) {
                        ++n;
                    }
                }
                else {
                    rowArray[i].setValueFromResultSet(this.userVTI, n, setNullableColumnList[n]);
                    ++n;
                    if (this.isDerbyStyleTableFunction) {
                        this.cast(returnColumnTypes[i], rowArray[i]);
                    }
                }
            }
        }
        catch (StandardException ex) {
            throw ex;
        }
        catch (Throwable t) {
            throw StandardException.unexpectedUserException(t);
        }
    }
    
    public final int getScanIsolationLevel() {
        return this.scanIsolationLevel;
    }
    
    public final boolean isCompileTime() {
        return false;
    }
    
    public final String getOriginalSQL() {
        return this.activation.getPreparedStatement().getSource();
    }
    
    public final int getStatementIsolationLevel() {
        return ExecutionContext.CS_TO_JDBC_ISOLATION_LEVEL_MAP[this.getScanIsolationLevel()];
    }
    
    public final void setSharedState(final String key, final Serializable s) {
        if (key == null) {
            return;
        }
        if (this.compileTimeConstants == null) {
            final Object[] savedObjects = this.activation.getPreparedStatement().getSavedObjects();
            synchronized (savedObjects) {
                this.compileTimeConstants = (FormatableHashtable)savedObjects[this.ctcNumber];
                if (this.compileTimeConstants == null) {
                    this.compileTimeConstants = new FormatableHashtable();
                    savedObjects[this.ctcNumber] = this.compileTimeConstants;
                }
            }
        }
        if (s == null) {
            this.compileTimeConstants.remove(key);
        }
        else {
            this.compileTimeConstants.put(key, s);
        }
    }
    
    public Object getSharedState(final String key) {
        if (key == null || this.compileTimeConstants == null) {
            return null;
        }
        return this.compileTimeConstants.get(key);
    }
    
    private DataTypeDescriptor[] getReturnColumnTypes() throws StandardException {
        if (this.returnColumnTypes == null) {
            final TypeDescriptor[] rowTypes = this.returnType.getRowTypes();
            final int length = rowTypes.length;
            this.returnColumnTypes = new DataTypeDescriptor[length];
            for (int i = 0; i < length; ++i) {
                this.returnColumnTypes[i] = DataTypeDescriptor.getType(rowTypes[i]);
            }
        }
        return this.returnColumnTypes;
    }
    
    private void cast(final DataTypeDescriptor dataTypeDescriptor, final DataValueDescriptor dataValueDescriptor) throws StandardException {
        final TypeId typeId = dataTypeDescriptor.getTypeId();
        if (!typeId.isBlobTypeId() && !typeId.isClobTypeId()) {
            if (typeId.isLongVarcharTypeId()) {
                this.castLongvarchar(dataTypeDescriptor, dataValueDescriptor);
            }
            else if (typeId.isLongVarbinaryTypeId()) {
                this.castLongvarbinary(dataTypeDescriptor, dataValueDescriptor);
            }
            else if (typeId.isDecimalTypeId()) {
                this.castDecimal(dataTypeDescriptor, dataValueDescriptor);
            }
            else {
                dataValueDescriptor.setObjectForCast(dataValueDescriptor.getObject(), true, typeId.getCorrespondingJavaTypeName());
                if (typeId.variableLength()) {
                    final VariableSizeDataValue variableSizeDataValue = (VariableSizeDataValue)dataValueDescriptor;
                    int n;
                    if (typeId.isNumericTypeId()) {
                        n = dataTypeDescriptor.getPrecision();
                    }
                    else {
                        n = dataTypeDescriptor.getMaximumWidth();
                    }
                    variableSizeDataValue.setWidth(n, dataTypeDescriptor.getScale(), false);
                }
            }
        }
    }
    
    private void castLongvarchar(final DataTypeDescriptor dataTypeDescriptor, final DataValueDescriptor dataValueDescriptor) throws StandardException {
        if (dataValueDescriptor.getLength() > 32700) {
            dataValueDescriptor.setValue(dataValueDescriptor.getString().substring(0, 32700));
        }
    }
    
    private void castLongvarbinary(final DataTypeDescriptor dataTypeDescriptor, final DataValueDescriptor dataValueDescriptor) throws StandardException {
        if (dataValueDescriptor.getLength() > 32700) {
            final byte[] bytes = dataValueDescriptor.getBytes();
            final byte[] value = new byte[32700];
            System.arraycopy(bytes, 0, value, 0, 32700);
            dataValueDescriptor.setValue(value);
        }
    }
    
    private void castDecimal(final DataTypeDescriptor dataTypeDescriptor, final DataValueDescriptor dataValueDescriptor) throws StandardException {
        ((VariableSizeDataValue)dataValueDescriptor).setWidth(dataTypeDescriptor.getPrecision(), dataTypeDescriptor.getScale(), false);
    }
}
