// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.compile.Node;
import org.apache.derby.iapi.services.context.ContextManager;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.property.PropertyUtil;
import org.apache.derby.iapi.services.monitor.Monitor;
import java.util.Properties;
import org.apache.derby.iapi.services.loader.ClassInfo;
import org.apache.derby.iapi.services.monitor.ModuleSupportable;
import org.apache.derby.iapi.services.monitor.ModuleControl;
import org.apache.derby.iapi.sql.compile.NodeFactory;

public class NodeFactoryImpl extends NodeFactory implements ModuleControl, ModuleSupportable
{
    private Boolean joinOrderOptimization;
    private final ClassInfo[] nodeCi;
    
    public boolean canSupport(final Properties properties) {
        return Monitor.isDesiredType(properties, 130);
    }
    
    public void boot(final boolean b, final Properties properties) throws StandardException {
        final String systemProperty = PropertyUtil.getSystemProperty("derby.optimizer.optimizeJoinOrder");
        if (systemProperty != null) {
            this.joinOrderOptimization = Boolean.valueOf(systemProperty);
        }
    }
    
    public void stop() {
    }
    
    public NodeFactoryImpl() {
        this.joinOrderOptimization = Boolean.TRUE;
        this.nodeCi = new ClassInfo[232];
    }
    
    public Boolean doJoinOrderOptimization() {
        return this.joinOrderOptimization;
    }
    
    public Node getNode(final int nodeType, final ContextManager contextManager) throws StandardException {
        ClassInfo classInfo = this.nodeCi[nodeType];
        Class<?> forName = null;
        if (classInfo == null) {
            final String nodeName = this.nodeName(nodeType);
            try {
                forName = Class.forName(nodeName);
            }
            catch (ClassNotFoundException ex) {}
            classInfo = new ClassInfo(forName);
            this.nodeCi[nodeType] = classInfo;
        }
        QueryTreeNode queryTreeNode = null;
        try {
            queryTreeNode = (QueryTreeNode)classInfo.getNewInstance();
        }
        catch (Exception ex2) {}
        queryTreeNode.setContextManager(contextManager);
        queryTreeNode.setNodeType(nodeType);
        return queryTreeNode;
    }
    
    protected String nodeName(final int n) throws StandardException {
        switch (n) {
            case 2: {
                return "org.apache.derby.impl.sql.compile.CurrentRowLocationNode";
            }
            case 3: {
                return "org.apache.derby.impl.sql.compile.GroupByList";
            }
            case 7: {
                return "org.apache.derby.impl.sql.compile.OrderByList";
            }
            case 8: {
                return "org.apache.derby.impl.sql.compile.PredicateList";
            }
            case 9: {
                return "org.apache.derby.impl.sql.compile.ResultColumnList";
            }
            case 11: {
                return "org.apache.derby.impl.sql.compile.SubqueryList";
            }
            case 12: {
                return "org.apache.derby.impl.sql.compile.TableElementList";
            }
            case 13: {
                return "org.apache.derby.impl.sql.compile.UntypedNullConstantNode";
            }
            case 14: {
                return "org.apache.derby.impl.sql.compile.TableElementNode";
            }
            case 15: {
                return "org.apache.derby.impl.sql.compile.ValueNodeList";
            }
            case 16: {
                return "org.apache.derby.impl.sql.compile.AllResultColumn";
            }
            case 18: {
                return "org.apache.derby.impl.sql.compile.GetCurrentConnectionNode";
            }
            case 19: {
                return "org.apache.derby.impl.sql.compile.NOPStatementNode";
            }
            case 21: {
                return "org.apache.derby.impl.sql.compile.SetTransactionIsolationNode";
            }
            case 23: {
                return "org.apache.derby.impl.sql.compile.LengthOperatorNode";
            }
            case 24:
            case 25: {
                return "org.apache.derby.impl.sql.compile.IsNullNode";
            }
            case 26: {
                return "org.apache.derby.impl.sql.compile.NotNode";
            }
            case 28: {
                return "org.apache.derby.impl.sql.compile.SQLToJavaValueNode";
            }
            case 34: {
                return "org.apache.derby.impl.sql.compile.TableName";
            }
            case 35: {
                return "org.apache.derby.impl.sql.compile.GroupByColumn";
            }
            case 36: {
                return "org.apache.derby.impl.sql.compile.JavaToSQLValueNode";
            }
            case 37: {
                return "org.apache.derby.impl.sql.compile.FromList";
            }
            case 38: {
                return "org.apache.derby.impl.sql.compile.BooleanConstantNode";
            }
            case 39: {
                return "org.apache.derby.impl.sql.compile.AndNode";
            }
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 47: {
                return "org.apache.derby.impl.sql.compile.BinaryRelationalOperatorNode";
            }
            case 40:
            case 46:
            case 48:
            case 49:
            case 194: {
                return "org.apache.derby.impl.sql.compile.BinaryArithmeticOperatorNode";
            }
            case 192: {
                return "org.apache.derby.impl.sql.compile.CoalesceFunctionNode";
            }
            case 50: {
                return "org.apache.derby.impl.sql.compile.ConcatenationOperatorNode";
            }
            case 51: {
                return "org.apache.derby.impl.sql.compile.LikeEscapeOperatorNode";
            }
            case 52: {
                return "org.apache.derby.impl.sql.compile.OrNode";
            }
            case 53: {
                return "org.apache.derby.impl.sql.compile.BetweenOperatorNode";
            }
            case 54: {
                return "org.apache.derby.impl.sql.compile.ConditionalNode";
            }
            case 55: {
                return "org.apache.derby.impl.sql.compile.InListOperatorNode";
            }
            case 58: {
                return "org.apache.derby.impl.sql.compile.BitConstantNode";
            }
            case 59:
            case 72:
            case 195: {
                return "org.apache.derby.impl.sql.compile.VarbitConstantNode";
            }
            case 60: {
                return "org.apache.derby.impl.sql.compile.CastNode";
            }
            case 61:
            case 73:
            case 77:
            case 196: {
                return "org.apache.derby.impl.sql.compile.CharConstantNode";
            }
            case 199: {
                return "org.apache.derby.impl.sql.compile.XMLConstantNode";
            }
            case 62: {
                return "org.apache.derby.impl.sql.compile.ColumnReference";
            }
            case 63: {
                return "org.apache.derby.impl.sql.compile.DropIndexNode";
            }
            case 65: {
                return "org.apache.derby.impl.sql.compile.DropTriggerNode";
            }
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 74:
            case 75: {
                return "org.apache.derby.impl.sql.compile.NumericConstantNode";
            }
            case 76: {
                return "org.apache.derby.impl.sql.compile.UserTypeConstantNode";
            }
            case 78: {
                return "org.apache.derby.impl.sql.compile.Predicate";
            }
            case 80: {
                return "org.apache.derby.impl.sql.compile.ResultColumn";
            }
            case 212: {
                return "org.apache.derby.impl.sql.compile.SetRoleNode";
            }
            case 81: {
                return "org.apache.derby.impl.sql.compile.SetSchemaNode";
            }
            case 83: {
                return "org.apache.derby.impl.sql.compile.SimpleStringOperatorNode";
            }
            case 84: {
                return "org.apache.derby.impl.sql.compile.StaticClassFieldReferenceNode";
            }
            case 85: {
                return "org.apache.derby.impl.sql.compile.StaticMethodCallNode";
            }
            case 87: {
                return "org.apache.derby.impl.sql.compile.ExtractOperatorNode";
            }
            case 88: {
                return "org.apache.derby.impl.sql.compile.ParameterNode";
            }
            case 90: {
                return "org.apache.derby.impl.sql.compile.DropSchemaNode";
            }
            case 214: {
                return "org.apache.derby.impl.sql.compile.DropRoleNode";
            }
            case 91: {
                return "org.apache.derby.impl.sql.compile.DropTableNode";
            }
            case 92: {
                return "org.apache.derby.impl.sql.compile.DropViewNode";
            }
            case 93: {
                return "org.apache.derby.impl.sql.compile.SubqueryNode";
            }
            case 94: {
                return "org.apache.derby.impl.sql.compile.BaseColumnNode";
            }
            case 95: {
                return "org.apache.derby.impl.sql.compile.CallStatementNode";
            }
            case 97:
            case 113:
            case 186:
            case 187:
            case 193: {
                return "org.apache.derby.impl.sql.compile.ModifyColumnNode";
            }
            case 98: {
                return "org.apache.derby.impl.sql.compile.NonStaticMethodCallNode";
            }
            case 99: {
                return "org.apache.derby.impl.sql.compile.CurrentOfNode";
            }
            case 100: {
                return "org.apache.derby.impl.sql.compile.DefaultNode";
            }
            case 101: {
                return "org.apache.derby.impl.sql.compile.DeleteNode";
            }
            case 102: {
                return "org.apache.derby.impl.sql.compile.UpdateNode";
            }
            case 104: {
                return "org.apache.derby.impl.sql.compile.OrderByColumn";
            }
            case 105: {
                return "org.apache.derby.impl.sql.compile.RowResultSetNode";
            }
            case 107: {
                return "org.apache.derby.impl.sql.compile.VirtualColumnNode";
            }
            case 108: {
                return "org.apache.derby.impl.sql.compile.CurrentDatetimeOperatorNode";
            }
            case 4:
            case 5:
            case 6:
            case 109:
            case 110:
            case 125:
            case 126:
            case 210: {
                return "org.apache.derby.impl.sql.compile.SpecialFunctionNode";
            }
            case 111: {
                return "org.apache.derby.impl.sql.compile.IsNode";
            }
            case 112: {
                return "org.apache.derby.impl.sql.compile.LockTableNode";
            }
            case 114: {
                return "org.apache.derby.impl.sql.compile.AlterTableNode";
            }
            case 115: {
                return "org.apache.derby.impl.sql.compile.AggregateNode";
            }
            case 116: {
                return "org.apache.derby.impl.sql.compile.ColumnDefinitionNode";
            }
            case 118: {
                return "org.apache.derby.impl.sql.compile.ExecSPSNode";
            }
            case 119: {
                return "org.apache.derby.impl.sql.compile.FKConstraintDefinitionNode";
            }
            case 120: {
                return "org.apache.derby.impl.sql.compile.FromVTI";
            }
            case 121: {
                return "org.apache.derby.impl.sql.compile.MaterializeResultSetNode";
            }
            case 122: {
                return "org.apache.derby.impl.sql.compile.NormalizeResultSetNode";
            }
            case 123: {
                return "org.apache.derby.impl.sql.compile.ScrollInsensitiveResultSetNode";
            }
            case 140: {
                return "org.apache.derby.impl.sql.compile.OrderByNode";
            }
            case 124: {
                return "org.apache.derby.impl.sql.compile.DistinctNode";
            }
            case 127:
            case 154:
            case 184:
            case 185:
            case 190: {
                return "org.apache.derby.impl.sql.compile.TernaryOperatorNode";
            }
            case 129: {
                return "org.apache.derby.impl.sql.compile.SelectNode";
            }
            case 130: {
                return "org.apache.derby.impl.sql.compile.CreateViewNode";
            }
            case 131: {
                return "org.apache.derby.impl.sql.compile.ConstraintDefinitionNode";
            }
            case 133: {
                return "org.apache.derby.impl.sql.compile.NewInvocationNode";
            }
            case 211: {
                return "org.apache.derby.impl.sql.compile.CreateRoleNode";
            }
            case 134: {
                return "org.apache.derby.impl.sql.compile.CreateSchemaNode";
            }
            case 135: {
                return "org.apache.derby.impl.sql.compile.FromBaseTable";
            }
            case 136: {
                return "org.apache.derby.impl.sql.compile.FromSubquery";
            }
            case 137: {
                return "org.apache.derby.impl.sql.compile.GroupByNode";
            }
            case 138: {
                return "org.apache.derby.impl.sql.compile.InsertNode";
            }
            case 139: {
                return "org.apache.derby.impl.sql.compile.JoinNode";
            }
            case 141: {
                return "org.apache.derby.impl.sql.compile.CreateTableNode";
            }
            case 191: {
                return "org.apache.derby.impl.sql.compile.RenameNode";
            }
            case 142: {
                return "org.apache.derby.impl.sql.compile.UnionNode";
            }
            case 157: {
                return "org.apache.derby.impl.sql.compile.IntersectOrExceptNode";
            }
            case 143: {
                return "org.apache.derby.impl.sql.compile.CreateTriggerNode";
            }
            case 144: {
                return "org.apache.derby.impl.sql.compile.HalfOuterJoinNode";
            }
            case 146: {
                return "org.apache.derby.impl.sql.compile.CreateIndexNode";
            }
            case 147: {
                return "org.apache.derby.impl.sql.compile.CursorNode";
            }
            case 148: {
                return "org.apache.derby.impl.sql.compile.HashTableNode";
            }
            case 149: {
                return "org.apache.derby.impl.sql.compile.IndexToBaseRowNode";
            }
            case 150: {
                return "org.apache.derby.impl.sql.compile.CreateAliasNode";
            }
            case 151: {
                return "org.apache.derby.impl.sql.compile.ProjectRestrictNode";
            }
            case 31: {
                return "org.apache.derby.impl.sql.compile.SQLBooleanConstantNode";
            }
            case 156: {
                return "org.apache.derby.impl.sql.compile.DropAliasNode";
            }
            case 1: {
                return "org.apache.derby.impl.sql.compile.TestConstraintNode";
            }
            case 29:
            case 30:
            case 188:
            case 189: {
                return "org.apache.derby.impl.sql.compile.UnaryArithmeticOperatorNode";
            }
            case 198: {
                return "org.apache.derby.impl.sql.compile.SavepointNode";
            }
            case 32: {
                return "org.apache.derby.impl.sql.compile.UnaryDateTimestampOperatorNode";
            }
            case 33: {
                return "org.apache.derby.impl.sql.compile.TimestampOperatorNode";
            }
            case 20: {
                return "org.apache.derby.impl.sql.compile.DB2LengthOperatorNode";
            }
            case 200:
            case 201: {
                return "org.apache.derby.impl.sql.compile.UnaryOperatorNode";
            }
            case 202:
            case 203: {
                return "org.apache.derby.impl.sql.compile.BinaryOperatorNode";
            }
            case 89: {
                return "org.apache.derby.impl.sql.compile.GrantNode";
            }
            case 86: {
                return "org.apache.derby.impl.sql.compile.RevokeNode";
            }
            case 215: {
                return "org.apache.derby.impl.sql.compile.GrantRoleNode";
            }
            case 216: {
                return "org.apache.derby.impl.sql.compile.RevokeRoleNode";
            }
            case 103: {
                return "org.apache.derby.impl.sql.compile.PrivilegeNode";
            }
            case 106: {
                return "org.apache.derby.impl.sql.compile.TablePrivilegesNode";
            }
            case 226: {
                return "org.apache.derby.impl.sql.compile.AggregateWindowFunctionNode";
            }
            case 227: {
                return "org.apache.derby.impl.sql.compile.RowNumberFunctionNode";
            }
            case 228: {
                return "org.apache.derby.impl.sql.compile.WindowDefinitionNode";
            }
            case 229: {
                return "org.apache.derby.impl.sql.compile.WindowReferenceNode";
            }
            case 230: {
                return "org.apache.derby.impl.sql.compile.WindowResultSetNode";
            }
            case 222: {
                return "org.apache.derby.impl.sql.compile.GenerationClauseNode";
            }
            case 223: {
                return "org.apache.derby.impl.sql.compile.RowCountNode";
            }
            case 224: {
                return "org.apache.derby.impl.sql.compile.CreateSequenceNode";
            }
            case 225: {
                return "org.apache.derby.impl.sql.compile.DropSequenceNode";
            }
            case 231: {
                return "org.apache.derby.impl.sql.compile.NextSequenceNode";
            }
            default: {
                throw StandardException.newException("0A000.S");
            }
        }
    }
}
