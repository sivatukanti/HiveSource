// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.types.TypeId;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import java.util.List;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.services.loader.ClassInspector;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.catalog.DefaultInfo;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.catalog.types.DefaultInfoImpl;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.DataTypeDescriptor;

public class ColumnDefinitionNode extends TableElementNode
{
    boolean isAutoincrement;
    DataTypeDescriptor type;
    DataValueDescriptor defaultValue;
    DefaultInfoImpl defaultInfo;
    DefaultNode defaultNode;
    boolean keepCurrentDefault;
    GenerationClauseNode generationClauseNode;
    long autoincrementIncrement;
    long autoincrementStart;
    long autoinc_create_or_modify_Start_Increment;
    boolean autoincrementVerify;
    public static final int CREATE_AUTOINCREMENT = 0;
    public static final int MODIFY_AUTOINCREMENT_RESTART_VALUE = 1;
    public static final int MODIFY_AUTOINCREMENT_INC_VALUE = 2;
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4) throws StandardException {
        super.init(o);
        this.type = (DataTypeDescriptor)o3;
        if (o2 instanceof UntypedNullConstantNode) {
            if (o3 != null) {
                this.defaultValue = ((UntypedNullConstantNode)o2).convertDefaultNode(this.type);
            }
        }
        else if (o2 instanceof GenerationClauseNode) {
            this.generationClauseNode = (GenerationClauseNode)o2;
        }
        else {
            this.defaultNode = (DefaultNode)o2;
            if (o4 != null) {
                final long[] array = (long[])o4;
                this.autoincrementStart = array[0];
                this.autoincrementIncrement = array[1];
                this.autoinc_create_or_modify_Start_Increment = array[3];
                this.autoincrementVerify = (array[2] <= 0L);
                this.isAutoincrement = true;
                if (o3 != null) {
                    this.setNullability(false);
                }
            }
        }
        this.keepCurrentDefault = (o2 == null);
    }
    
    public String toString() {
        return "";
    }
    
    public String getColumnName() {
        return this.name;
    }
    
    public final DataTypeDescriptor getType() {
        return this.type;
    }
    
    public void setType(final DataTypeDescriptor type) {
        this.type = type;
    }
    
    void setNullability(final boolean b) {
        this.type = this.getType().getNullabilityType(b);
    }
    
    void setCollationType(final int n) {
        this.type = this.getType().getCollatedType(n, 1);
    }
    
    public DataValueDescriptor getDefaultValue() {
        return this.defaultValue;
    }
    
    public DefaultInfo getDefaultInfo() {
        return this.defaultInfo;
    }
    
    public void setDefaultInfo(final DefaultInfoImpl defaultInfo) {
        this.defaultInfo = defaultInfo;
    }
    
    public DefaultNode getDefaultNode() {
        return this.defaultNode;
    }
    
    public boolean hasGenerationClause() {
        return this.generationClauseNode != null;
    }
    
    public GenerationClauseNode getGenerationClauseNode() {
        return this.generationClauseNode;
    }
    
    public boolean isAutoincrementColumn() {
        return this.isAutoincrement;
    }
    
    long getAutoincrementStart() {
        return this.autoincrementStart;
    }
    
    long getAutoincrementIncrement() {
        return this.autoincrementIncrement;
    }
    
    long getAutoinc_create_or_modify_Start_Increment() {
        return this.autoinc_create_or_modify_Start_Increment;
    }
    
    public void checkUserType(final TableDescriptor tableDescriptor) throws StandardException {
        if (this.hasGenerationClause() && this.getType() == null) {
            return;
        }
        if (!this.getType().getTypeId().userType()) {
            return;
        }
        this.setType(this.bindUserType(this.getType()));
        final ClassInspector classInspector = this.getClassFactory().getClassInspector();
        final String correspondingJavaTypeName = this.getType().getTypeId().getCorrespondingJavaTypeName();
        boolean accessible = false;
        Throwable t = null;
        try {
            accessible = classInspector.accessible(correspondingJavaTypeName);
        }
        catch (ClassNotFoundException ex) {
            t = ex;
        }
        if (!accessible) {
            throw StandardException.newException("42X26", t, correspondingJavaTypeName, this.name);
        }
        if (!classInspector.assignableTo(correspondingJavaTypeName, "java.io.Serializable") && !classInspector.assignableTo(correspondingJavaTypeName, "java.sql.SQLData")) {
            this.getCompilerContext().addWarning(StandardException.newWarning("01J04", correspondingJavaTypeName, this.name));
        }
    }
    
    UUID getOldDefaultUUID() {
        return null;
    }
    
    int getAction() {
        return 0;
    }
    
    void bindAndValidateDefault(final DataDictionary dataDictionary, final TableDescriptor tableDescriptor) throws StandardException {
        if (tableDescriptor != null && !this.hasGenerationClause() && !this.getType().isNullable() && this.defaultNode == null && !this.isAutoincrement) {
            throw StandardException.newException("42601", this.getColumnName());
        }
        if (this.defaultNode == null) {
            return;
        }
        if (this.defaultValue != null) {
            return;
        }
        this.validateDefault(dataDictionary, tableDescriptor);
    }
    
    public void validateAutoincrement(final DataDictionary dataDictionary, final TableDescriptor tableDescriptor, final int n) throws StandardException {
        if (!this.isAutoincrement) {
            return;
        }
        if (n == 3) {
            throw StandardException.newException("42995");
        }
        if (this.autoincrementIncrement == 0L && (this.autoinc_create_or_modify_Start_Increment == 0L || this.autoinc_create_or_modify_Start_Increment == 2L)) {
            throw StandardException.newException("42Z21", this.getColumnName());
        }
        switch (this.getType().getTypeId().getJDBCTypeId()) {
            case -6: {
                this.autoincrementCheckRange(-128L, 127L, "TINYINT");
                break;
            }
            case 5: {
                this.autoincrementCheckRange(-32768L, 32767L, "SMALLINT");
                break;
            }
            case 4: {
                this.autoincrementCheckRange(-2147483648L, 2147483647L, "INTEGER");
                break;
            }
            case -5: {
                this.autoincrementCheckRange(Long.MIN_VALUE, Long.MAX_VALUE, "BIGINT");
                break;
            }
            default: {
                throw StandardException.newException("42Z22", this.getColumnName());
            }
        }
    }
    
    private void autoincrementCheckRange(final long n, final long n2, final String s) throws StandardException {
        if (n > this.autoincrementIncrement || n2 < this.autoincrementIncrement) {
            throw StandardException.newException("22003", s);
        }
        if (n > this.autoincrementStart || n2 < this.autoincrementStart) {
            throw StandardException.newException("22003", s);
        }
    }
    
    void validateDefault(final DataDictionary dataDictionary, final TableDescriptor tableDescriptor) throws StandardException {
        if (this.defaultNode == null) {
            return;
        }
        if (this.isAutoincrement) {
            this.defaultInfo = createDefaultInfoOfAutoInc();
            return;
        }
        final CompilerContext compilerContext = this.getCompilerContext();
        final ValueNode defaultTree = this.defaultNode.getDefaultTree();
        final int reliability = compilerContext.getReliability();
        try {
            compilerContext.setReliability(1192);
            final ValueNode bindExpression = defaultTree.bindExpression((FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager()), null, null);
            final TypeId typeId = this.getType().getTypeId();
            final TypeId typeId2 = bindExpression.getTypeId();
            if (!this.defaultTypeIsValid(typeId, this.getType(), typeId2, bindExpression, this.defaultNode.getDefaultText())) {
                throw StandardException.newException("42894", this.name);
            }
            if (!this.getTypeCompiler(typeId).storable(typeId2, this.getClassFactory())) {
                throw StandardException.newException("42821", typeId.getSQLTypeName(), typeId2.getSQLTypeName());
            }
            this.defaultInfo = new DefaultInfoImpl(false, this.defaultNode.getDefaultText(), this.defaultValue);
        }
        finally {
            compilerContext.setReliability(reliability);
        }
    }
    
    private static DefaultInfoImpl createDefaultInfoOfAutoInc() {
        return new DefaultInfoImpl(true, null, null);
    }
    
    public boolean defaultTypeIsValid(final TypeId typeId, final DataTypeDescriptor dataTypeDescriptor, final TypeId typeId2, final ValueNode valueNode, final String s) throws StandardException {
        final int typeFormatId = typeId.getTypeFormatId();
        final int n = (typeId2 == null) ? -1 : typeId2.getTypeFormatId();
        if (!valueNode.isConstantExpression()) {
            final boolean b = typeFormatId == 5 || typeFormatId == 13 || typeFormatId == 230;
            if (valueNode instanceof SpecialFunctionNode) {
                switch (valueNode.getNodeType()) {
                    case 109:
                    case 110:
                    case 125:
                    case 126:
                    case 210: {
                        return b && dataTypeDescriptor.getMaximumWidth() >= 8;
                    }
                    case 6: {
                        return b && dataTypeDescriptor.getMaximumWidth() >= 128;
                    }
                    default: {
                        return false;
                    }
                }
            }
        }
        switch (typeFormatId) {
            case 4: {
                return valueNode instanceof BooleanConstantNode;
            }
            case 7: {
                return n == 7;
            }
            case 11: {
                return n == 7 || n == 11;
            }
            case 197: {
                if (n == 197) {
                    final DataTypeDescriptor typeServices = valueNode.getTypeServices();
                    final int length = s.length();
                    int precision = typeServices.getPrecision();
                    int scale = typeServices.getScale();
                    for (int n2 = 1; n2 <= scale && s.charAt(length - n2) == '0'; --scale, --precision) {}
                    return scale <= dataTypeDescriptor.getScale() && precision - scale <= dataTypeDescriptor.getPrecision() - dataTypeDescriptor.getScale();
                }
                return n == 11 || n == 7;
            }
            case 5:
            case 13:
            case 230: {
                return n == 5;
            }
            case 27:
            case 29:
            case 232: {
                return n == 27;
            }
            case 267: {
                return n == typeFormatId;
            }
            case 6:
            case 8:
            case 10:
            case 35:
            case 36:
            case 40:
            case 440:
            case 444: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public void printSubNodes(final int n) {
    }
}
