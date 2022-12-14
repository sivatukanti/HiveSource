// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

public interface C_NodeNames
{
    public static final String AGGREGATE_NODE_NAME = "org.apache.derby.impl.sql.compile.AggregateNode";
    public static final String ALL_RESULT_COLUMN_NAME = "org.apache.derby.impl.sql.compile.AllResultColumn";
    public static final String ALTER_TABLE_NODE_NAME = "org.apache.derby.impl.sql.compile.AlterTableNode";
    public static final String AND_NODE_NAME = "org.apache.derby.impl.sql.compile.AndNode";
    public static final String BASE_COLUMN_NODE_NAME = "org.apache.derby.impl.sql.compile.BaseColumnNode";
    public static final String BETWEEN_OPERATOR_NODE_NAME = "org.apache.derby.impl.sql.compile.BetweenOperatorNode";
    public static final String BINARY_ARITHMETIC_OPERATOR_NODE_NAME = "org.apache.derby.impl.sql.compile.BinaryArithmeticOperatorNode";
    public static final String BINARY_OPERATOR_NODE_NAME = "org.apache.derby.impl.sql.compile.BinaryOperatorNode";
    public static final String BINARY_RELATIONAL_OPERATOR_NODE_NAME = "org.apache.derby.impl.sql.compile.BinaryRelationalOperatorNode";
    public static final String BIT_CONSTANT_NODE_NAME = "org.apache.derby.impl.sql.compile.BitConstantNode";
    public static final String BOOLEAN_CONSTANT_NODE_NAME = "org.apache.derby.impl.sql.compile.BooleanConstantNode";
    public static final String CALL_STATEMENT_NODE_NAME = "org.apache.derby.impl.sql.compile.CallStatementNode";
    public static final String CAST_NODE_NAME = "org.apache.derby.impl.sql.compile.CastNode";
    public static final String CHAR_CONSTANT_NODE_NAME = "org.apache.derby.impl.sql.compile.CharConstantNode";
    public static final String COALESCE_FUNCTION_NODE_NAME = "org.apache.derby.impl.sql.compile.CoalesceFunctionNode";
    public static final String COLUMN_DEFINITION_NODE_NAME = "org.apache.derby.impl.sql.compile.ColumnDefinitionNode";
    public static final String COLUMN_REFERENCE_NAME = "org.apache.derby.impl.sql.compile.ColumnReference";
    public static final String CONCATENATION_OPERATOR_NODE_NAME = "org.apache.derby.impl.sql.compile.ConcatenationOperatorNode";
    public static final String CONDITIONAL_NODE_NAME = "org.apache.derby.impl.sql.compile.ConditionalNode";
    public static final String CONSTRAINT_DEFINITION_NODE_NAME = "org.apache.derby.impl.sql.compile.ConstraintDefinitionNode";
    public static final String CREATE_ALIAS_NODE_NAME = "org.apache.derby.impl.sql.compile.CreateAliasNode";
    public static final String CREATE_ROLE_NODE_NAME = "org.apache.derby.impl.sql.compile.CreateRoleNode";
    public static final String CREATE_INDEX_NODE_NAME = "org.apache.derby.impl.sql.compile.CreateIndexNode";
    public static final String CREATE_SCHEMA_NODE_NAME = "org.apache.derby.impl.sql.compile.CreateSchemaNode";
    public static final String CREATE_SEQUENCE_NODE_NAME = "org.apache.derby.impl.sql.compile.CreateSequenceNode";
    public static final String CREATE_TABLE_NODE_NAME = "org.apache.derby.impl.sql.compile.CreateTableNode";
    public static final String CREATE_TRIGGER_NODE_NAME = "org.apache.derby.impl.sql.compile.CreateTriggerNode";
    public static final String CREATE_VIEW_NODE_NAME = "org.apache.derby.impl.sql.compile.CreateViewNode";
    public static final String CURRENT_DATETIME_OPERATOR_NODE_NAME = "org.apache.derby.impl.sql.compile.CurrentDatetimeOperatorNode";
    public static final String CURRENT_OF_NODE_NAME = "org.apache.derby.impl.sql.compile.CurrentOfNode";
    public static final String CURRENT_ROW_LOCATION_NODE_NAME = "org.apache.derby.impl.sql.compile.CurrentRowLocationNode";
    public static final String SPECIAL_FUNCTION_NODE_NAME = "org.apache.derby.impl.sql.compile.SpecialFunctionNode";
    public static final String CURSOR_NODE_NAME = "org.apache.derby.impl.sql.compile.CursorNode";
    public static final String DB2_LENGTH_OPERATOR_NODE_NAME = "org.apache.derby.impl.sql.compile.DB2LengthOperatorNode";
    public static final String DML_MOD_STATEMENT_NODE_NAME = "org.apache.derby.impl.sql.compile.DMLModStatementNode";
    public static final String DEFAULT_NODE_NAME = "org.apache.derby.impl.sql.compile.DefaultNode";
    public static final String DELETE_NODE_NAME = "org.apache.derby.impl.sql.compile.DeleteNode";
    public static final String DISTINCT_NODE_NAME = "org.apache.derby.impl.sql.compile.DistinctNode";
    public static final String DROP_ALIAS_NODE_NAME = "org.apache.derby.impl.sql.compile.DropAliasNode";
    public static final String DROP_INDEX_NODE_NAME = "org.apache.derby.impl.sql.compile.DropIndexNode";
    public static final String DROP_ROLE_NODE_NAME = "org.apache.derby.impl.sql.compile.DropRoleNode";
    public static final String DROP_SCHEMA_NODE_NAME = "org.apache.derby.impl.sql.compile.DropSchemaNode";
    public static final String DROP_SEQUENCE_NODE_NAME = "org.apache.derby.impl.sql.compile.DropSequenceNode";
    public static final String DROP_TABLE_NODE_NAME = "org.apache.derby.impl.sql.compile.DropTableNode";
    public static final String DROP_TRIGGER_NODE_NAME = "org.apache.derby.impl.sql.compile.DropTriggerNode";
    public static final String DROP_VIEW_NODE_NAME = "org.apache.derby.impl.sql.compile.DropViewNode";
    public static final String EXEC_SPS_NODE_NAME = "org.apache.derby.impl.sql.compile.ExecSPSNode";
    public static final String EXTRACT_OPERATOR_NODE_NAME = "org.apache.derby.impl.sql.compile.ExtractOperatorNode";
    public static final String FK_CONSTRAINT_DEFINITION_NODE_NAME = "org.apache.derby.impl.sql.compile.FKConstraintDefinitionNode";
    public static final String FROM_BASE_TABLE_NAME = "org.apache.derby.impl.sql.compile.FromBaseTable";
    public static final String FROM_LIST_NAME = "org.apache.derby.impl.sql.compile.FromList";
    public static final String FROM_SUBQUERY_NAME = "org.apache.derby.impl.sql.compile.FromSubquery";
    public static final String FROM_VTI_NAME = "org.apache.derby.impl.sql.compile.FromVTI";
    public static final String GENERATION_CLAUSE_NODE_NAME = "org.apache.derby.impl.sql.compile.GenerationClauseNode";
    public static final String GET_CURRENT_CONNECTION_NODE_NAME = "org.apache.derby.impl.sql.compile.GetCurrentConnectionNode";
    public static final String GRANT_NODE_NAME = "org.apache.derby.impl.sql.compile.GrantNode";
    public static final String GRANT_ROLE_NODE_NAME = "org.apache.derby.impl.sql.compile.GrantRoleNode";
    public static final String GROUP_BY_COLUMN_NAME = "org.apache.derby.impl.sql.compile.GroupByColumn";
    public static final String GROUP_BY_LIST_NAME = "org.apache.derby.impl.sql.compile.GroupByList";
    public static final String GROUP_BY_NODE_NAME = "org.apache.derby.impl.sql.compile.GroupByNode";
    public static final String HALF_OUTER_JOIN_NODE_NAME = "org.apache.derby.impl.sql.compile.HalfOuterJoinNode";
    public static final String HASH_TABLE_NODE_NAME = "org.apache.derby.impl.sql.compile.HashTableNode";
    public static final String IN_LIST_OPERATOR_NODE_NAME = "org.apache.derby.impl.sql.compile.InListOperatorNode";
    public static final String INDEX_TO_BASE_ROW_NODE_NAME = "org.apache.derby.impl.sql.compile.IndexToBaseRowNode";
    public static final String INSERT_NODE_NAME = "org.apache.derby.impl.sql.compile.InsertNode";
    public static final String IS_NODE_NAME = "org.apache.derby.impl.sql.compile.IsNode";
    public static final String IS_NULL_NODE_NAME = "org.apache.derby.impl.sql.compile.IsNullNode";
    public static final String JAVA_TO_SQL_VALUE_NODE_NAME = "org.apache.derby.impl.sql.compile.JavaToSQLValueNode";
    public static final String JOIN_NODE_NAME = "org.apache.derby.impl.sql.compile.JoinNode";
    public static final String LENGTH_OPERATOR_NODE_NAME = "org.apache.derby.impl.sql.compile.LengthOperatorNode";
    public static final String LIKE_OPERATOR_NODE_NAME = "org.apache.derby.impl.sql.compile.LikeEscapeOperatorNode";
    public static final String LOCK_TABLE_NODE_NAME = "org.apache.derby.impl.sql.compile.LockTableNode";
    public static final String MATERIALIZE_RESULT_SET_NODE_NAME = "org.apache.derby.impl.sql.compile.MaterializeResultSetNode";
    public static final String MODIFY_COLUMN_NODE_NAME = "org.apache.derby.impl.sql.compile.ModifyColumnNode";
    public static final String NOP_STATEMENT_NODE_NAME = "org.apache.derby.impl.sql.compile.NOPStatementNode";
    public static final String NEW_INVOCATION_NODE_NAME = "org.apache.derby.impl.sql.compile.NewInvocationNode";
    public static final String NEXT_SEQUENCE_NODE_NAME = "org.apache.derby.impl.sql.compile.NextSequenceNode";
    public static final String NON_STATIC_METHOD_CALL_NODE_NAME = "org.apache.derby.impl.sql.compile.NonStaticMethodCallNode";
    public static final String NORMALIZE_RESULT_SET_NODE_NAME = "org.apache.derby.impl.sql.compile.NormalizeResultSetNode";
    public static final String NOT_NODE_NAME = "org.apache.derby.impl.sql.compile.NotNode";
    public static final String NUMERIC_CONSTANT_NODE_NAME = "org.apache.derby.impl.sql.compile.NumericConstantNode";
    public static final String OR_NODE_NAME = "org.apache.derby.impl.sql.compile.OrNode";
    public static final String ORDER_BY_COLUMN_NAME = "org.apache.derby.impl.sql.compile.OrderByColumn";
    public static final String ORDER_BY_LIST_NAME = "org.apache.derby.impl.sql.compile.OrderByList";
    public static final String ORDER_BY_NODE_NAME = "org.apache.derby.impl.sql.compile.OrderByNode";
    public static final String PARAMETER_NODE_NAME = "org.apache.derby.impl.sql.compile.ParameterNode";
    public static final String PREDICATE_NAME = "org.apache.derby.impl.sql.compile.Predicate";
    public static final String PREDICATE_LIST_NAME = "org.apache.derby.impl.sql.compile.PredicateList";
    public static final String PRIVILEGE_NAME = "org.apache.derby.impl.sql.compile.PrivilegeNode";
    public static final String PROJECT_RESTRICT_NODE_NAME = "org.apache.derby.impl.sql.compile.ProjectRestrictNode";
    public static final String RENAME_NODE_NAME = "org.apache.derby.impl.sql.compile.RenameNode";
    public static final String RESULT_COLUMN_NAME = "org.apache.derby.impl.sql.compile.ResultColumn";
    public static final String RESULT_COLUMN_LIST_NAME = "org.apache.derby.impl.sql.compile.ResultColumnList";
    public static final String REVOKE_NODE_NAME = "org.apache.derby.impl.sql.compile.RevokeNode";
    public static final String REVOKE_ROLE_NODE_NAME = "org.apache.derby.impl.sql.compile.RevokeRoleNode";
    public static final String ROW_RESULT_SET_NODE_NAME = "org.apache.derby.impl.sql.compile.RowResultSetNode";
    public static final String SQL_BOOLEAN_CONSTANT_NODE_NAME = "org.apache.derby.impl.sql.compile.SQLBooleanConstantNode";
    public static final String SQL_TO_JAVA_VALUE_NODE_NAME = "org.apache.derby.impl.sql.compile.SQLToJavaValueNode";
    public static final String SCROLL_INSENSITIVE_RESULT_SET_NODE_NAME = "org.apache.derby.impl.sql.compile.ScrollInsensitiveResultSetNode";
    public static final String SELECT_NODE_NAME = "org.apache.derby.impl.sql.compile.SelectNode";
    public static final String SET_ROLE_NODE_NAME = "org.apache.derby.impl.sql.compile.SetRoleNode";
    public static final String SET_SCHEMA_NODE_NAME = "org.apache.derby.impl.sql.compile.SetSchemaNode";
    public static final String SET_TRANSACTION_ISOLATION_NODE_NAME = "org.apache.derby.impl.sql.compile.SetTransactionIsolationNode";
    public static final String SIMPLE_STRING_OPERATOR_NODE_NAME = "org.apache.derby.impl.sql.compile.SimpleStringOperatorNode";
    public static final String STATIC_CLASS_FIELD_REFERENCE_NODE_NAME = "org.apache.derby.impl.sql.compile.StaticClassFieldReferenceNode";
    public static final String STATIC_METHOD_CALL_NODE_NAME = "org.apache.derby.impl.sql.compile.StaticMethodCallNode";
    public static final String SUBQUERY_LIST_NAME = "org.apache.derby.impl.sql.compile.SubqueryList";
    public static final String SUBQUERY_NODE_NAME = "org.apache.derby.impl.sql.compile.SubqueryNode";
    public static final String TABLE_ELEMENT_LIST_NAME = "org.apache.derby.impl.sql.compile.TableElementList";
    public static final String TABLE_ELEMENT_NODE_NAME = "org.apache.derby.impl.sql.compile.TableElementNode";
    public static final String TABLE_NAME_NAME = "org.apache.derby.impl.sql.compile.TableName";
    public static final String TABLE_PRIVILEGES_NAME = "org.apache.derby.impl.sql.compile.TablePrivilegesNode";
    public static final String TERNARY_OPERATOR_NODE_NAME = "org.apache.derby.impl.sql.compile.TernaryOperatorNode";
    public static final String TEST_CONSTRAINT_NODE_NAME = "org.apache.derby.impl.sql.compile.TestConstraintNode";
    public static final String TIMESTAMP_OPERATOR_NODE_NAME = "org.apache.derby.impl.sql.compile.TimestampOperatorNode";
    public static final String UNARY_ARITHMETIC_OPERATOR_NODE_NAME = "org.apache.derby.impl.sql.compile.UnaryArithmeticOperatorNode";
    public static final String UNARY_DATE_TIMESTAMP_OPERATOR_NODE_NAME = "org.apache.derby.impl.sql.compile.UnaryDateTimestampOperatorNode";
    public static final String UNARY_OPERATOR_NODE_NAME = "org.apache.derby.impl.sql.compile.UnaryOperatorNode";
    public static final String UNION_NODE_NAME = "org.apache.derby.impl.sql.compile.UnionNode";
    public static final String INTERSECT_OR_EXCEPT_NODE_NAME = "org.apache.derby.impl.sql.compile.IntersectOrExceptNode";
    public static final String UNTYPED_NULL_CONSTANT_NODE_NAME = "org.apache.derby.impl.sql.compile.UntypedNullConstantNode";
    public static final String UPDATE_NODE_NAME = "org.apache.derby.impl.sql.compile.UpdateNode";
    public static final String USERTYPE_CONSTANT_NODE_NAME = "org.apache.derby.impl.sql.compile.UserTypeConstantNode";
    public static final String VALUE_NODE_LIST_NAME = "org.apache.derby.impl.sql.compile.ValueNodeList";
    public static final String VARBIT_CONSTANT_NODE_NAME = "org.apache.derby.impl.sql.compile.VarbitConstantNode";
    public static final String VIRTUAL_COLUMN_NODE_NAME = "org.apache.derby.impl.sql.compile.VirtualColumnNode";
    public static final String SAVEPOINT_NODE_NAME = "org.apache.derby.impl.sql.compile.SavepointNode";
    public static final String XML_CONSTANT_NODE_NAME = "org.apache.derby.impl.sql.compile.XMLConstantNode";
    public static final String AGGREGATE_WINDOW_FUNCTION_NAME = "org.apache.derby.impl.sql.compile.AggregateWindowFunctionNode";
    public static final String ROW_NUMBER_FUNCTION_NAME = "org.apache.derby.impl.sql.compile.RowNumberFunctionNode";
    public static final String WINDOW_DEFINITION_NAME = "org.apache.derby.impl.sql.compile.WindowDefinitionNode";
    public static final String WINDOW_REFERENCE_NAME = "org.apache.derby.impl.sql.compile.WindowReferenceNode";
    public static final String WINDOW_RESULTSET_NODE_NAME = "org.apache.derby.impl.sql.compile.WindowResultSetNode";
    public static final String ROW_COUNT_NODE_NAME = "org.apache.derby.impl.sql.compile.RowCountNode";
}
