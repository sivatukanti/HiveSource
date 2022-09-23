// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.execute;

import java.util.List;
import org.apache.derby.iapi.services.context.ContextService;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.iapi.sql.conn.Authorizer;
import org.apache.derby.iapi.types.RowLocation;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.sql.ResultDescription;
import org.apache.derby.iapi.sql.dictionary.IndexRowGenerator;
import org.apache.derby.iapi.store.access.StaticCompiledOpenConglomInfo;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.impl.sql.compile.TableName;
import org.apache.derby.catalog.AliasInfo;
import java.util.Properties;
import org.apache.derby.iapi.sql.depend.ProviderInfo;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.execute.ConstantAction;
import org.apache.derby.iapi.sql.dictionary.ConstraintDescriptorList;

public class GenericConstantActionFactory
{
    public ConstantAction getSetConstraintsConstantAction(final ConstraintDescriptorList list, final boolean b, final boolean b2, final Object[] array) {
        return new SetConstraintsConstantAction(list, b, b2);
    }
    
    public ConstantAction getAlterTableConstantAction(final SchemaDescriptor schemaDescriptor, final String s, final UUID uuid, final long n, final int n2, final ColumnInfo[] array, final ConstraintConstantAction[] array2, final char c, final boolean b, final int n3, final boolean b2, final boolean b3, final boolean b4, final boolean b5, final boolean b6, final boolean b7, final boolean b8, final boolean b9, final boolean b10, final String s2) {
        return new AlterTableConstantAction(schemaDescriptor, s, uuid, n, n2, array, array2, c, b, n3, b2, b3, b4, b5, b6, b7, b8, b9, b10, s2);
    }
    
    public CreateConstraintConstantAction getCreateConstraintConstantAction(final String s, final int n, final boolean b, final String s2, final UUID uuid, final String s3, final String[] array, final IndexConstantAction indexConstantAction, final String s4, final boolean b2, final ConstraintInfo constraintInfo, final ProviderInfo[] array2) {
        return new CreateConstraintConstantAction(s, n, b, s2, uuid, s3, array, indexConstantAction, s4, b2, constraintInfo, array2);
    }
    
    public CreateIndexConstantAction getCreateIndexConstantAction(final boolean b, final boolean b2, final boolean b3, final String s, final String s2, final String s3, final String s4, final UUID uuid, final String[] array, final boolean[] array2, final boolean b4, final UUID uuid2, final Properties properties) {
        return new CreateIndexConstantAction(b, b2, b3, s, s2, s3, s4, uuid, array, array2, b4, uuid2, properties);
    }
    
    public ConstantAction getCreateAliasConstantAction(final String s, final String s2, final String s3, final AliasInfo aliasInfo, final char c) {
        return new CreateAliasConstantAction(s, s2, s3, aliasInfo, c);
    }
    
    public ConstantAction getCreateSchemaConstantAction(final String s, final String s2) {
        return new CreateSchemaConstantAction(s, s2);
    }
    
    public ConstantAction getCreateRoleConstantAction(final String s) {
        return new CreateRoleConstantAction(s);
    }
    
    public ConstantAction getSetRoleConstantAction(final String s, final int n) {
        return new SetRoleConstantAction(s, n);
    }
    
    public ConstantAction getCreateSequenceConstantAction(final TableName tableName, final DataTypeDescriptor dataTypeDescriptor, final long n, final long n2, final long n3, final long n4, final boolean b) {
        return new CreateSequenceConstantAction(tableName.getSchemaName(), tableName.getTableName(), dataTypeDescriptor, n, n2, n3, n4, b);
    }
    
    public ConstantAction getCreateTableConstantAction(final String s, final String s2, final int n, final ColumnInfo[] array, final CreateConstraintConstantAction[] array2, final Properties properties, final char c, final boolean b, final boolean b2) {
        return new CreateTableConstantAction(s, s2, n, array, array2, properties, c, b, b2);
    }
    
    public ConstantAction getSavepointConstantAction(final String s, final int n) {
        return new SavepointConstantAction(s, n);
    }
    
    public ConstantAction getCreateViewConstantAction(final String s, final String s2, final int n, final String s3, final int n2, final ColumnInfo[] array, final ProviderInfo[] array2, final UUID uuid) {
        return new CreateViewConstantAction(s, s2, n, s3, n2, array, array2, uuid);
    }
    
    public ConstantAction getDeleteConstantAction(final long n, final int n2, final StaticCompiledOpenConglomInfo staticCompiledOpenConglomInfo, final IndexRowGenerator[] array, final long[] array2, final StaticCompiledOpenConglomInfo[] array3, final boolean b, final boolean b2, final UUID uuid, final int n3, final Object o, final Object o2, final int[] array4, final long n4, final String s, final String s2, final ResultDescription resultDescription, final FKInfo[] array5, final TriggerInfo triggerInfo, final FormatableBitSet set, final int[] array6, final int[] array7, final int n5, final UUID uuid2, final boolean b3, final ConstantAction[] array8) throws StandardException {
        return new DeleteConstantAction(n, staticCompiledOpenConglomInfo, array, array2, array3, b, uuid, n3, array5, triggerInfo, set, array6, array7, n5, b3, resultDescription, array8);
    }
    
    public ConstraintConstantAction getDropConstraintConstantAction(final String s, final String s2, final String s3, final UUID uuid, final String s4, final IndexConstantAction indexConstantAction, final int n, final int n2) {
        return new DropConstraintConstantAction(s, s2, s3, uuid, s4, indexConstantAction, n, n2);
    }
    
    public DropIndexConstantAction getDropIndexConstantAction(final String s, final String s2, final String s3, final String s4, final UUID uuid, final long n) {
        return new DropIndexConstantAction(s, s2, s3, s4, uuid, n);
    }
    
    public ConstantAction getDropAliasConstantAction(final SchemaDescriptor schemaDescriptor, final String s, final char c) {
        return new DropAliasConstantAction(schemaDescriptor, s, c);
    }
    
    public ConstantAction getDropRoleConstantAction(final String s) {
        return new DropRoleConstantAction(s);
    }
    
    public ConstantAction getDropSequenceConstantAction(final SchemaDescriptor schemaDescriptor, final String s) {
        return new DropSequenceConstantAction(schemaDescriptor, s);
    }
    
    public ConstantAction getDropSchemaConstantAction(final String s) {
        return new DropSchemaConstantAction(s);
    }
    
    public ConstantAction getDropTableConstantAction(final String s, final String s2, final SchemaDescriptor schemaDescriptor, final long n, final UUID uuid, final int n2) {
        return new DropTableConstantAction(s, s2, schemaDescriptor, n, uuid, n2);
    }
    
    public ConstantAction getDropViewConstantAction(final String s, final String s2, final SchemaDescriptor schemaDescriptor) {
        return new DropViewConstantAction(s, s2, schemaDescriptor);
    }
    
    public ConstantAction getRenameConstantAction(final String s, final String s2, final String s3, final String s4, final SchemaDescriptor schemaDescriptor, final UUID uuid, final boolean b, final int n) {
        return new RenameConstantAction(s, s2, s3, s4, schemaDescriptor, uuid, b, n);
    }
    
    public ConstantAction getInsertConstantAction(final TableDescriptor tableDescriptor, final long n, final StaticCompiledOpenConglomInfo staticCompiledOpenConglomInfo, final IndexRowGenerator[] array, final long[] array2, final StaticCompiledOpenConglomInfo[] array3, final String[] array4, final boolean b, final boolean b2, final UUID uuid, final int n2, final Object o, final Object o2, final Properties properties, final FKInfo[] array5, final TriggerInfo triggerInfo, final int[] array6, final boolean[] array7, final UUID uuid2, final Object[] array8, final Object[] array9, final boolean b3, final RowLocation[] array10) throws StandardException {
        return new InsertConstantAction(tableDescriptor, n, staticCompiledOpenConglomInfo, array, array2, array3, array4, b, properties, uuid, n2, array5, triggerInfo, array6, array7, b3, array10);
    }
    
    public ConstantAction getUpdatableVTIConstantAction(final int n, final boolean b) throws StandardException {
        return new UpdatableVTIConstantAction(n, b, null);
    }
    
    public ConstantAction getUpdatableVTIConstantAction(final int n, final boolean b, final int[] array) throws StandardException {
        return new UpdatableVTIConstantAction(n, b, array);
    }
    
    public ConstantAction getLockTableConstantAction(final String s, final long n, final boolean b) {
        return new LockTableConstantAction(s, n, b);
    }
    
    public ConstantAction getSetSchemaConstantAction(final String s, final int n) {
        return new SetSchemaConstantAction(s, n);
    }
    
    public ConstantAction getSetTransactionIsolationConstantAction(final int n) {
        return new SetTransactionIsolationConstantAction(n);
    }
    
    public UpdateConstantAction getUpdateConstantAction(final long n, final int n2, final StaticCompiledOpenConglomInfo staticCompiledOpenConglomInfo, final IndexRowGenerator[] array, final long[] array2, final StaticCompiledOpenConglomInfo[] array3, final String[] array4, final boolean b, final UUID uuid, final int n3, final boolean b2, final int[] array5, final int[] array6, final Object o, final FKInfo[] array7, final TriggerInfo triggerInfo, final FormatableBitSet set, final int[] array8, final int[] array9, final int n4, final boolean b3, final boolean b4) throws StandardException {
        return new UpdateConstantAction(n, staticCompiledOpenConglomInfo, array, array2, array3, array4, b, uuid, n3, array5, array7, triggerInfo, set, array8, array9, n4, b3, b4);
    }
    
    protected static Authorizer getAuthorizer() {
        return ((LanguageConnectionContext)ContextService.getContext("LanguageConnectionContext")).getAuthorizer();
    }
    
    public ConstantAction getCreateTriggerConstantAction(final String s, final String s2, final int n, final boolean b, final boolean b2, final boolean b3, final TableDescriptor tableDescriptor, final UUID uuid, final String s3, final UUID uuid2, final String s4, final UUID uuid3, final int[] array, final int[] array2, final String s5, final boolean b4, final boolean b5, final String s6, final String s7) {
        return new CreateTriggerConstantAction(s, s2, n, b, b2, b3, tableDescriptor, uuid, s3, uuid2, s4, uuid3, array, array2, s5, b4, b5, s6, s7);
    }
    
    public ConstantAction getDropTriggerConstantAction(final SchemaDescriptor schemaDescriptor, final String s, final UUID uuid) {
        return new DropTriggerConstantAction(schemaDescriptor, s, uuid);
    }
    
    public ConstantAction getDropStatisticsConstantAction(final SchemaDescriptor schemaDescriptor, final String s, final String s2, final boolean b) {
        return new DropStatisticsConstantAction(schemaDescriptor, s, s2, b);
    }
    
    public ConstantAction getGrantConstantAction(final PrivilegeInfo privilegeInfo, final List list) {
        return new GrantRevokeConstantAction(true, privilegeInfo, list);
    }
    
    public ConstantAction getGrantRoleConstantAction(final List list, final List list2) {
        return new GrantRoleConstantAction(list, list2);
    }
    
    public ConstantAction getRevokeConstantAction(final PrivilegeInfo privilegeInfo, final List list) {
        return new GrantRevokeConstantAction(false, privilegeInfo, list);
    }
    
    public ConstantAction getRevokeRoleConstantAction(final List list, final List list2) {
        return new RevokeRoleConstantAction(list, list2);
    }
}
