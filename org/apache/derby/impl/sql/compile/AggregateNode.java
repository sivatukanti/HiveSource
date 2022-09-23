// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.sql.dictionary.SchemaDescriptor;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import org.apache.derby.iapi.services.loader.ClassFactory;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.sql.dictionary.PrivilegedSQLObject;
import org.apache.derby.iapi.sql.depend.Provider;
import org.apache.derby.iapi.sql.dictionary.AliasDescriptor;
import java.util.List;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.services.loader.ClassInspector;

public class AggregateNode extends UnaryOperatorNode
{
    private boolean distinct;
    private AggregateDefinition uad;
    private TableName userAggregateName;
    private StringBuffer aggregatorClassName;
    private String aggregateDefinitionClassName;
    private Class aggregateDefinitionClass;
    private ClassInspector classInspector;
    private String aggregateName;
    private ResultColumn generatedRC;
    private ColumnReference generatedRef;
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4) throws StandardException {
        super.init(o);
        this.aggregateName = (String)o4;
        if (o2 instanceof UserAggregateDefinition) {
            this.setUserDefinedAggregate((UserAggregateDefinition)o2);
            this.distinct = (boolean)o3;
        }
        else if (o2 instanceof TableName) {
            this.userAggregateName = (TableName)o2;
            this.distinct = (boolean)o3;
        }
        else {
            this.aggregateDefinitionClass = (Class)o2;
            if (!this.aggregateDefinitionClass.equals(MaxMinAggregateDefinition.class)) {
                this.distinct = (boolean)o3;
            }
            this.aggregateDefinitionClassName = this.aggregateDefinitionClass.getName();
        }
    }
    
    private void setUserDefinedAggregate(final UserAggregateDefinition uad) {
        this.uad = uad;
        this.aggregateDefinitionClass = this.uad.getClass();
        this.aggregateDefinitionClassName = this.aggregateDefinitionClass.getName();
    }
    
    public ValueNode replaceAggregatesWithColumnReferences(final ResultColumnList list, final int tableNumber) throws StandardException {
        if (this.generatedRef == null) {
            (this.generatedRC = (ResultColumn)this.getNodeFactory().getNode(80, "SQLCol" + this.getCompilerContext().getNextColumnNumber(), this, this.getContextManager())).markGenerated();
            (this.generatedRef = (ColumnReference)this.getNodeFactory().getNode(62, this.generatedRC.getName(), null, this.getContextManager())).setSource(this.generatedRC);
            this.generatedRef.setNestingLevel(0);
            this.generatedRef.setSourceLevel(0);
            if (tableNumber != -1) {
                this.generatedRef.setTableNumber(tableNumber);
            }
            list.addResultColumn(this.generatedRC);
            this.generatedRef.markGeneratedToReplaceAggregate();
        }
        else {
            list.addResultColumn(this.generatedRC);
        }
        return this.generatedRef;
    }
    
    AggregateDefinition getAggregateDefinition() {
        return this.uad;
    }
    
    public ResultColumn getGeneratedRC() {
        return this.generatedRC;
    }
    
    public ColumnReference getGeneratedRef() {
        return this.generatedRef;
    }
    
    public ValueNode bindExpression(final FromList list, final SubqueryList list2, final List list3) throws StandardException {
        final DataDictionary dataDictionary = this.getDataDictionary();
        DataTypeDescriptor typeServices = null;
        final ClassFactory classFactory = this.getClassFactory();
        this.classInspector = classFactory.getClassInspector();
        if (this.userAggregateName != null) {
            this.userAggregateName.bind(dataDictionary);
            final AliasDescriptor resolveAggregate = resolveAggregate(dataDictionary, this.getSchemaDescriptor(this.userAggregateName.getSchemaName(), true), this.userAggregateName.getTableName());
            if (resolveAggregate == null) {
                throw StandardException.newException("42X94", AliasDescriptor.getAliasType('G'), this.userAggregateName.getTableName());
            }
            this.setUserDefinedAggregate(new UserAggregateDefinition(resolveAggregate));
            this.aggregateName = resolveAggregate.getJavaClassName();
        }
        this.instantiateAggDef();
        if (this.isUserDefinedAggregate()) {
            final AliasDescriptor aliasDescriptor = ((UserAggregateDefinition)this.uad).getAliasDescriptor();
            this.getCompilerContext().createDependency(aliasDescriptor);
            if (this.isPrivilegeCollectionRequired()) {
                this.getCompilerContext().addRequiredUsagePriv(aliasDescriptor);
            }
        }
        list3.add(this);
        final CompilerContext compilerContext = this.getCompilerContext();
        if (this.operand != null) {
            final int orReliability = this.orReliability(16384);
            this.bindOperand(list, list2, list3);
            compilerContext.setReliability(orReliability);
            final HasNodeVisitor hasNodeVisitor = new HasNodeVisitor(this.getClass(), ResultSetNode.class);
            this.operand.accept(hasNodeVisitor);
            if (hasNodeVisitor.hasNode()) {
                throw StandardException.newException("42Y33", this.getSQLName());
            }
            SelectNode.checkNoWindowFunctions(this.operand, this.aggregateName);
            typeServices = this.operand.getTypeServices();
            if (this.uad instanceof CountAggregateDefinition && !typeServices.isNullable()) {
                this.setOperator(this.aggregateName);
                this.setMethodName(this.aggregateName);
            }
            if (this.distinct && !this.operand.getTypeId().orderable(classFactory)) {
                throw StandardException.newException("X0X67.S", typeServices.getTypeId().getSQLTypeName());
            }
            if (this.operand instanceof UntypedNullConstantNode) {
                throw StandardException.newException("42Y83", this.getSQLName());
            }
        }
        this.aggregatorClassName = new StringBuffer();
        final DataTypeDescriptor aggregator = this.uad.getAggregator(typeServices, this.aggregatorClassName);
        if (aggregator == null) {
            throw StandardException.newException("42Y22", this.getSQLName(), this.operand.getTypeId().getSQLTypeName());
        }
        if (this.isUserDefinedAggregate()) {
            final ValueNode castInputValue = ((UserAggregateDefinition)this.uad).castInputValue(this.operand, this.getNodeFactory(), this.getContextManager());
            if (castInputValue != null) {
                this.operand = castInputValue.bindExpression(list, list2, list3);
            }
        }
        this.checkAggregatorClassName(this.aggregatorClassName.toString());
        this.setType(aggregator);
        return this;
    }
    
    public static AliasDescriptor resolveAggregate(final DataDictionary dataDictionary, final SchemaDescriptor schemaDescriptor, final String s) throws StandardException {
        if (schemaDescriptor.getUUID() == null) {
            return null;
        }
        final List routineList = dataDictionary.getRoutineList(schemaDescriptor.getUUID().toString(), s, 'G');
        if (routineList.size() > 0) {
            return routineList.get(0);
        }
        return null;
    }
    
    private void checkAggregatorClassName(final String s) throws StandardException {
        this.verifyClassExist(s);
        if (!this.classInspector.assignableTo(s, "org.apache.derby.iapi.sql.execute.ExecAggregator")) {
            throw StandardException.newException("42Y32", s, this.getSQLName(), this.operand.getTypeId().getSQLTypeName());
        }
    }
    
    private void instantiateAggDef() throws StandardException {
        if (this.uad == null) {
            Class clazz = this.aggregateDefinitionClass;
            if (clazz == null) {
                final String aggregateDefinitionClassName = this.aggregateDefinitionClassName;
                this.verifyClassExist(aggregateDefinitionClassName);
                try {
                    clazz = this.classInspector.getClass(aggregateDefinitionClassName);
                }
                catch (Throwable t) {
                    throw StandardException.unexpectedUserException(t);
                }
            }
            MaxMinAggregateDefinition instance;
            try {
                instance = clazz.newInstance();
            }
            catch (Throwable t2) {
                throw StandardException.unexpectedUserException(t2);
            }
            if (!(instance instanceof AggregateDefinition)) {
                throw StandardException.newException("42Y00", this.aggregateDefinitionClassName);
            }
            if (instance instanceof MaxMinAggregateDefinition) {
                final MaxMinAggregateDefinition maxMinAggregateDefinition = instance;
                if (this.aggregateName.equals("MAX")) {
                    maxMinAggregateDefinition.setMaxOrMin(true);
                }
                else {
                    maxMinAggregateDefinition.setMaxOrMin(false);
                }
            }
            if (instance instanceof SumAvgAggregateDefinition) {
                final SumAvgAggregateDefinition sumAvgAggregateDefinition = (SumAvgAggregateDefinition)instance;
                if (this.aggregateName.equals("SUM")) {
                    sumAvgAggregateDefinition.setSumOrAvg(true);
                }
                else {
                    sumAvgAggregateDefinition.setSumOrAvg(false);
                }
            }
            this.uad = instance;
        }
        this.setOperator(this.aggregateName);
        this.setMethodName(this.aggregateDefinitionClassName);
    }
    
    public boolean isDistinct() {
        return this.distinct;
    }
    
    public String getAggregatorClassName() {
        return this.aggregatorClassName.toString();
    }
    
    public String getAggregateName() {
        return this.aggregateName;
    }
    
    public ResultColumn getNewAggregatorResultColumn(final DataDictionary dataDictionary) throws StandardException {
        final ConstantNode nullNode = this.getNullNode(DataTypeDescriptor.getSQLDataTypeDescriptor(this.aggregatorClassName.toString()));
        nullNode.bindExpression(null, null, null);
        return (ResultColumn)this.getNodeFactory().getNode(80, this.aggregateName, nullNode, this.getContextManager());
    }
    
    public ResultColumn getNewExpressionResultColumn(final DataDictionary dataDictionary) throws StandardException {
        return (ResultColumn)this.getNodeFactory().getNode(80, "##aggregate expression", (this.operand == null) ? this.getNewNullResultExpression() : this.operand, this.getContextManager());
    }
    
    public ValueNode getNewNullResultExpression() throws StandardException {
        return this.getNullNode(this.getTypeServices());
    }
    
    public void generateExpression(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
    }
    
    public String toString() {
        return "";
    }
    
    public boolean isConstant() {
        return false;
    }
    
    public boolean constantExpression(final PredicateList list) {
        return false;
    }
    
    public String getSQLName() {
        if (this.isUserDefinedAggregate()) {
            return ((UserAggregateDefinition)this.uad).getAliasDescriptor().getQualifiedName();
        }
        return this.aggregateName;
    }
    
    private boolean isUserDefinedAggregate() {
        return this.uad instanceof UserAggregateDefinition;
    }
}
