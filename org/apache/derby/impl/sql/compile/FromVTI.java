// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.vti.RestrictedVTI;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.apache.derby.catalog.DefaultInfo;
import org.apache.derby.iapi.types.DataValueDescriptor;
import org.apache.derby.iapi.types.DataTypeDescriptor;
import org.apache.derby.catalog.TypeDescriptor;
import java.io.Serializable;
import org.apache.derby.iapi.sql.execute.ExecutionContext;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptor;
import org.apache.derby.iapi.sql.dictionary.ColumnDescriptorList;
import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.sql.compile.ExpressionClassBuilderInterface;
import org.apache.derby.iapi.services.io.FormatableBitSet;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.sql.compile.Visitor;
import java.util.Iterator;
import java.lang.reflect.InvocationTargetException;
import org.apache.derby.vti.DeferModification;
import java.sql.ResultSetMetaData;
import org.apache.derby.catalog.UUID;
import org.apache.derby.catalog.types.RoutineAliasInfo;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import java.lang.reflect.Method;
import java.util.List;
import org.apache.derby.iapi.sql.dictionary.DataDictionary;
import org.apache.derby.iapi.sql.compile.OptimizablePredicate;
import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.vti.VTICosting;
import java.sql.SQLException;
import org.apache.derby.iapi.sql.compile.RowOrdering;
import org.apache.derby.iapi.sql.compile.Optimizer;
import org.apache.derby.iapi.sql.compile.CostEstimate;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.compile.OptimizablePredicateList;
import org.apache.derby.iapi.error.StandardException;
import java.util.HashMap;
import java.util.ArrayList;
import org.apache.derby.vti.Restriction;
import java.sql.PreparedStatement;
import org.apache.derby.iapi.services.io.FormatableHashtable;
import java.sql.ResultSet;
import org.apache.derby.iapi.util.JBitSet;
import org.apache.derby.vti.VTIEnvironment;

public class FromVTI extends FromTable implements VTIEnvironment
{
    JBitSet correlationMap;
    JBitSet dependencyMap;
    MethodCallNode methodCall;
    TableName exposedName;
    SubqueryList subqueryList;
    boolean implementsVTICosting;
    boolean optimized;
    boolean materializable;
    boolean isTarget;
    boolean isDerbyStyleTableFunction;
    boolean isRestrictedTableFunction;
    ResultSet rs;
    private FormatableHashtable compileTimeConstants;
    protected int numVTICols;
    private PredicateList restrictionList;
    double estimatedCost;
    double estimatedRowCount;
    boolean supportsMultipleInstantiations;
    boolean vtiCosted;
    protected boolean version2;
    private boolean implementsPushable;
    private PreparedStatement ps;
    private JavaValueNode[] methodParms;
    private boolean controlsDeferral;
    private int resultSetType;
    private String[] projectedColumnNames;
    private Restriction vtiRestriction;
    private ArrayList outerFromLists;
    private HashMap argSources;
    
    public FromVTI() {
        this.estimatedCost = 100000.0;
        this.estimatedRowCount = 10000.0;
        this.supportsMultipleInstantiations = true;
        this.resultSetType = 1003;
        this.outerFromLists = new ArrayList();
        this.argSources = new HashMap();
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4) throws StandardException {
        this.init(o, o2, o3, o4, this.makeTableName(null, (String)o2));
    }
    
    public void init(final Object o, final Object o2, final Object o3, final Object o4, final Object o5) throws StandardException {
        super.init(o2, o4);
        this.methodCall = (MethodCallNode)o;
        this.resultColumns = (ResultColumnList)o3;
        this.subqueryList = (SubqueryList)this.getNodeFactory().getNode(11, this.getContextManager());
        this.exposedName = (TableName)o5;
    }
    
    public CostEstimate estimateCost(final OptimizablePredicateList list, final ConglomerateDescriptor conglomerateDescriptor, final CostEstimate costEstimate, final Optimizer optimizer, final RowOrdering rowOrdering) throws StandardException {
        this.costEstimate = this.getCostEstimate(optimizer);
        if (this.implementsVTICosting && !this.vtiCosted) {
            try {
                final VTICosting vtiCosting = this.getVTICosting();
                this.estimatedCost = vtiCosting.getEstimatedCostPerInstantiation(this);
                this.estimatedRowCount = vtiCosting.getEstimatedRowCount(this);
                this.supportsMultipleInstantiations = vtiCosting.supportsMultipleInstantiations(this);
                if (this.ps != null) {
                    this.ps.close();
                    this.ps = null;
                }
                if (this.rs != null) {
                    this.rs.close();
                    this.rs = null;
                }
            }
            catch (SQLException ex) {
                throw StandardException.unexpectedUserException(ex);
            }
            this.vtiCosted = true;
        }
        this.costEstimate.setCost(this.estimatedCost, this.estimatedRowCount, this.estimatedRowCount);
        if (this.getCurrentAccessPath().getJoinStrategy().multiplyBaseCostByOuterRows()) {
            this.costEstimate.multiply(costEstimate.rowCount(), this.costEstimate);
        }
        if (!this.optimized) {
            this.subqueryList.optimize(optimizer.getDataDictionary(), this.costEstimate.rowCount());
            this.subqueryList.modifyAccessPaths();
        }
        this.optimized = true;
        return this.costEstimate;
    }
    
    public boolean legalJoinOrder(final JBitSet set) {
        set.or(this.correlationMap);
        return set.contains(this.dependencyMap);
    }
    
    public boolean isMaterializable() {
        return this.materializable;
    }
    
    public boolean supportsMultipleInstantiations() {
        return this.supportsMultipleInstantiations;
    }
    
    public boolean isDerbyStyleTableFunction() {
        return this.isDerbyStyleTableFunction;
    }
    
    public void adjustForSortElimination() {
    }
    
    public Optimizable modifyAccessPath(final JBitSet set) throws StandardException {
        if (this.rs != null) {
            try {
                this.rs.close();
                this.rs = null;
            }
            catch (Throwable t) {
                throw StandardException.unexpectedUserException(t);
            }
        }
        return super.modifyAccessPath(set);
    }
    
    public void addOuterFromList(final FromList e) {
        this.outerFromLists.add(e);
    }
    
    public boolean pushOptPredicate(final OptimizablePredicate optimizablePredicate) throws StandardException {
        if (!this.implementsPushable) {
            return false;
        }
        if (!optimizablePredicate.getReferencedMap().hasSingleBitSet()) {
            return false;
        }
        if (this.restrictionList == null) {
            this.restrictionList = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
        }
        this.restrictionList.addPredicate((Predicate)optimizablePredicate);
        return true;
    }
    
    public String toString() {
        return "";
    }
    
    public void printSubNodes(final int n) {
    }
    
    public boolean isConstructor() {
        return this.methodCall instanceof NewInvocationNode;
    }
    
    public MethodCallNode getMethodCall() {
        return this.methodCall;
    }
    
    public String getExposedName() {
        return this.correlationName;
    }
    
    public TableName getExposedTableName() {
        return this.exposedName;
    }
    
    void setTarget() {
        this.isTarget = true;
        this.version2 = true;
    }
    
    public ResultSetNode bindNonVTITables(final DataDictionary dataDictionary, final FromList list) throws StandardException {
        if (this.tableNumber == -1) {
            this.tableNumber = this.getCompilerContext().getNextTableNumber();
        }
        return this;
    }
    
    String getVTIName() {
        return this.methodCall.getJavaClassName();
    }
    
    public ResultSetNode bindVTITables(final FromList list) throws StandardException {
        final ResultColumnList resultColumns = this.resultColumns;
        final LanguageConnectionContext languageConnectionContext = this.getLanguageConnectionContext();
        this.methodCall.bindExpression(list, this.subqueryList, new ArrayList());
        this.methodParms = this.methodCall.getMethodParms();
        final RoutineAliasInfo routineInfo = this.methodCall.getRoutineInfo();
        if (routineInfo != null && routineInfo.getReturnType().isRowMultiSet() && routineInfo.getParameterStyle() == 1) {
            this.isDerbyStyleTableFunction = true;
        }
        if (this.isDerbyStyleTableFunction) {
            this.isRestrictedTableFunction = RestrictedVTI.class.isAssignableFrom(((Method)this.methodCall.getResolvedMethod()).getReturnType());
        }
        if (this.isConstructor()) {
            final NewInvocationNode newInvocationNode = (NewInvocationNode)this.methodCall;
            if (!newInvocationNode.assignableTo("java.sql.PreparedStatement")) {
                if (this.version2) {
                    throw StandardException.newException("42X08", this.getVTIName(), "java.sql.PreparedStatement");
                }
                if (!newInvocationNode.assignableTo("java.sql.ResultSet")) {
                    throw StandardException.newException("42X08", this.getVTIName(), "java.sql.ResultSet");
                }
            }
            else {
                this.version2 = true;
            }
            if (this.version2) {
                this.implementsPushable = newInvocationNode.assignableTo("org.apache.derby.vti.IQualifyable");
            }
            this.implementsVTICosting = newInvocationNode.assignableTo("org.apache.derby.vti.VTICosting");
        }
        if (this.isDerbyStyleTableFunction) {
            this.implementsVTICosting = this.implementsDerbyStyleVTICosting(this.methodCall.getJavaClassName());
        }
        final UUID specialTriggerVTITableName;
        if (this.isConstructor() && (specialTriggerVTITableName = this.getSpecialTriggerVTITableName(languageConnectionContext, this.methodCall.getJavaClassName())) != null) {
            this.resultColumns = this.genResultColList(this.getDataDictionary().getTableDescriptor(specialTriggerVTITableName));
            this.vtiCosted = true;
            this.estimatedCost = 50.0;
            this.estimatedRowCount = 5.0;
            this.supportsMultipleInstantiations = true;
        }
        else {
            this.resultColumns = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
            if (this.isDerbyStyleTableFunction) {
                this.createResultColumnsForTableFunction(routineInfo.getReturnType());
            }
            else {
                final ResultSetMetaData resultSetMetaData = this.getResultSetMetaData();
                if (resultSetMetaData == null) {
                    throw StandardException.newException("42X43", this.getVTIName());
                }
                try {
                    this.numVTICols = resultSetMetaData.getColumnCount();
                }
                catch (SQLException ex) {
                    this.numVTICols = 0;
                }
                this.resultColumns.createListFromResultSetMetaData(resultSetMetaData, this.exposedName, this.getVTIName());
            }
        }
        this.numVTICols = this.resultColumns.size();
        if (resultColumns != null) {
            this.resultColumns.propagateDCLInfo(resultColumns, this.correlationName);
        }
        return this;
    }
    
    public ResultSetMetaData getResultSetMetaData() throws StandardException {
        ResultSetMetaData resultSetMetaData;
        try {
            if (this.version2) {
                this.ps = (PreparedStatement)this.getNewInstance();
                if (this.ps.getResultSetConcurrency() != 1008) {
                    throw StandardException.newException("42Z90", this.getVTIName());
                }
                resultSetMetaData = this.ps.getMetaData();
                this.controlsDeferral = (this.ps instanceof DeferModification);
                try {
                    this.resultSetType = this.ps.getResultSetType();
                }
                catch (SQLException ex) {}
                catch (AbstractMethodError abstractMethodError) {}
                catch (NoSuchMethodError noSuchMethodError) {}
                if (!this.implementsVTICosting) {
                    this.ps.close();
                    this.ps = null;
                }
            }
            else {
                this.rs = (ResultSet)this.getNewInstance();
                resultSetMetaData = this.rs.getMetaData();
                if (!this.implementsVTICosting) {
                    this.rs.close();
                    this.rs = null;
                }
            }
        }
        catch (Throwable t) {
            throw StandardException.unexpectedUserException(t);
        }
        return resultSetMetaData;
    }
    
    private Object getNewInstance() throws StandardException {
        Class[] methodParameterClasses = ((NewInvocationNode)this.methodCall).getMethodParameterClasses();
        Object[] initargs;
        if (methodParameterClasses != null) {
            initargs = new Object[methodParameterClasses.length];
            for (int i = 0; i < methodParameterClasses.length; ++i) {
                final Class<?> clazz = (Class<?>)methodParameterClasses[i];
                initargs[i] = this.methodParms[i].getConstantValueAsObject();
                if (initargs[i] != null && clazz.isPrimitive()) {
                    if (clazz.equals(Short.TYPE)) {
                        initargs[i] = new Short(((Integer)initargs[i]).shortValue());
                    }
                    else if (clazz.equals(Byte.TYPE)) {
                        initargs[i] = new Byte(((Integer)initargs[i]).byteValue());
                    }
                }
                if (initargs[i] == null && clazz.isPrimitive()) {
                    if (clazz.equals(Integer.TYPE)) {
                        initargs[i] = new Integer(0);
                    }
                    else if (clazz.equals(Short.TYPE)) {
                        initargs[i] = new Short((short)0);
                    }
                    else if (clazz.equals(Byte.TYPE)) {
                        initargs[i] = new Byte((byte)0);
                    }
                    else if (clazz.equals(Long.TYPE)) {
                        initargs[i] = new Long(0L);
                    }
                    else if (clazz.equals(Float.TYPE)) {
                        initargs[i] = new Float(0.0f);
                    }
                    else if (clazz.equals(Double.TYPE)) {
                        initargs[i] = new Double(0.0);
                    }
                    else if (clazz.equals(Boolean.TYPE)) {
                        initargs[i] = Boolean.FALSE;
                    }
                    else if (clazz.equals(Character.TYPE)) {
                        initargs[i] = new Character('\0');
                    }
                }
            }
        }
        else {
            methodParameterClasses = new Class[0];
            initargs = new Object[0];
        }
        try {
            return this.getClassFactory().getClassInspector().getClass(this.methodCall.getJavaClassName()).getConstructor((Class<?>[])methodParameterClasses).newInstance(initargs);
        }
        catch (Throwable t) {
            if (t instanceof InvocationTargetException) {
                final Throwable targetException = ((InvocationTargetException)t).getTargetException();
                if (targetException instanceof StandardException) {
                    throw (StandardException)targetException;
                }
            }
            throw StandardException.unexpectedUserException(t);
        }
    }
    
    public DeferModification getDeferralControl() throws StandardException {
        if (!this.controlsDeferral) {
            return null;
        }
        try {
            return (DeferModification)this.getNewInstance();
        }
        catch (Throwable t) {
            throw StandardException.unexpectedUserException(t);
        }
    }
    
    public int getResultSetType() {
        return this.resultSetType;
    }
    
    public void bindExpressions(final FromList list) throws StandardException {
        this.materializable = this.methodCall.areParametersQueryInvariant();
        final List nodesFromParameters = this.getNodesFromParameters(ColumnReference.class);
        List list2 = null;
        for (final ColumnReference columnReference : nodesFromParameters) {
            boolean b = !columnReference.getCorrelated();
            if (columnReference.getCorrelated()) {
                for (int i = 0; i < this.outerFromLists.size(); ++i) {
                    if (this.columnInFromList((FromList)this.outerFromLists.get(i), columnReference) != null) {
                        b = true;
                        break;
                    }
                }
            }
            else {
                final FromTable columnInFromList = this.columnInFromList(list, columnReference);
                if (columnInFromList != null && !this.isDerbyStyleTableFunction && !(columnInFromList instanceof FromVTI)) {
                    break;
                }
            }
            if (b) {
                throw StandardException.newException("42ZB7", columnReference.getSQLColumnName());
            }
            if (columnReference.getTableNumber() != -1) {
                continue;
            }
            if (list2 == null) {
                list2 = new ArrayList();
            }
            columnReference.bindExpression(list, this.subqueryList, list2);
        }
    }
    
    private FromTable columnInFromList(final FromList list, final ColumnReference columnReference) throws StandardException {
        final int tableNumber = columnReference.getTableNumber();
        for (int i = 0; i < list.size(); ++i) {
            final FromTable value = (FromTable)list.elementAt(i);
            if (tableNumber == value.getTableNumber()) {
                this.argSources.put(new Integer(value.getTableNumber()), value);
                return value;
            }
        }
        return null;
    }
    
    List getNodesFromParameters(final Class clazz) throws StandardException {
        final CollectNodesVisitor collectNodesVisitor = new CollectNodesVisitor(clazz);
        this.methodCall.accept(collectNodesVisitor);
        return collectNodesVisitor.getList();
    }
    
    public ResultColumnList getAllResultColumns(final TableName tableName) throws StandardException {
        TableName tableName2;
        if (tableName != null) {
            tableName2 = this.makeTableName(tableName.getSchemaName(), this.correlationName);
        }
        else {
            tableName2 = this.makeTableName(null, this.correlationName);
        }
        if (tableName != null && !tableName.equals(tableName2)) {
            return null;
        }
        final ResultColumnList list = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
        for (int size = this.resultColumns.size(), i = 0; i < size; ++i) {
            final ResultColumn resultColumn = (ResultColumn)this.resultColumns.elementAt(i);
            if (!resultColumn.isGenerated()) {
                final String name = resultColumn.getName();
                list.addResultColumn((ResultColumn)this.getNodeFactory().getNode(80, name, this.getNodeFactory().getNode(62, name, this.exposedName, this.getContextManager()), this.getContextManager()));
            }
        }
        return list;
    }
    
    public ResultColumn getMatchingColumn(final ColumnReference columnReference) throws StandardException {
        if (this.resultColumns == null) {
            return null;
        }
        ResultColumn resultColumn = null;
        final TableName tableNameNode = columnReference.getTableNameNode();
        if (tableNameNode == null || tableNameNode.equals(this.exposedName)) {
            resultColumn = this.resultColumns.getResultColumn(columnReference.getColumnName());
            if (resultColumn != null) {
                columnReference.setTableNumber(this.tableNumber);
                columnReference.setColumnNumber(resultColumn.getColumnPosition());
            }
        }
        return resultColumn;
    }
    
    public ResultSetNode preprocess(final int n, final GroupByList list, final FromList list2) throws StandardException {
        this.methodCall.preprocess(n, (FromList)this.getNodeFactory().getNode(37, this.getNodeFactory().doJoinOrderOptimization(), this.getContextManager()), (SubqueryList)this.getNodeFactory().getNode(11, this.getContextManager()), (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager()));
        (this.referencedTableMap = new JBitSet(n)).set(this.tableNumber);
        this.dependencyMap = new JBitSet(n);
        this.methodCall.categorize(this.dependencyMap, false);
        this.dependencyMap.clear(this.tableNumber);
        this.correlationMap = new JBitSet(n);
        this.methodCall.getCorrelationTables(this.correlationMap);
        return this.genProjectRestrict(n);
    }
    
    protected ResultSetNode genProjectRestrict(final int n) throws StandardException {
        final ResultColumnList resultColumns = this.resultColumns;
        resultColumns.genVirtualColumnNodes(this, this.resultColumns = this.resultColumns.copyListAndObjects(), false);
        resultColumns.doProjection();
        return (ResultSetNode)this.getNodeFactory().getNode(151, this, resultColumns, null, null, null, null, this.tableProperties, this.getContextManager());
    }
    
    public boolean performMaterialization(final JBitSet set) throws StandardException {
        return set.getFirstSetBit() != -1 && !set.hasSingleBitSet() && !this.getTrulyTheBestAccessPath().getJoinStrategy().doesMaterialization() && this.isMaterializable() && !this.supportsMultipleInstantiations;
    }
    
    void computeProjectionAndRestriction(final PredicateList list) throws StandardException {
        if (!this.isRestrictedTableFunction) {
            return;
        }
        this.computeRestriction(list, this.computeProjection());
    }
    
    private HashMap computeProjection() throws StandardException {
        final HashMap<String, String> hashMap = new HashMap<String, String>();
        final ResultColumnList resultColumns = this.getResultColumns();
        final int size = resultColumns.size();
        this.projectedColumnNames = new String[size];
        for (int i = 0; i < size; ++i) {
            final ResultColumn resultColumn = resultColumns.getResultColumn(i + 1);
            final String name = resultColumn.getName();
            if (resultColumn.isReferenced()) {
                hashMap.put(name, this.projectedColumnNames[i] = resultColumn.getBaseColumnNode().getColumnName());
            }
        }
        return hashMap;
    }
    
    private void computeRestriction(final PredicateList list, final HashMap hashMap) throws StandardException {
        if (list == null) {
            return;
        }
        for (int size = list.size(), i = 0; i < size; ++i) {
            final Predicate predicate = (Predicate)list.elementAt(i);
            if (this.canBePushedDown(predicate)) {
                final Restriction restriction = this.makeRestriction(predicate.getAndNode(), hashMap);
                if (restriction == null) {
                    this.vtiRestriction = null;
                    return;
                }
                if (this.vtiRestriction == null) {
                    this.vtiRestriction = restriction;
                }
                else {
                    this.vtiRestriction = new Restriction.AND(this.vtiRestriction, restriction);
                }
            }
        }
    }
    
    private boolean canBePushedDown(final Predicate predicate) throws StandardException {
        final JBitSet referencedSet = predicate.getReferencedSet();
        return predicate.isQualifier() && referencedSet != null && referencedSet.hasSingleBitSet() && referencedSet.get(this.getTableNumber());
    }
    
    private Restriction makeRestriction(final ValueNode valueNode, final HashMap hashMap) throws StandardException {
        if (valueNode instanceof AndNode) {
            final AndNode andNode = (AndNode)valueNode;
            if (andNode.getRightOperand().isBooleanTrue()) {
                return this.makeRestriction(andNode.getLeftOperand(), hashMap);
            }
            final Restriction restriction = this.makeRestriction(andNode.getLeftOperand(), hashMap);
            final Restriction restriction2 = this.makeRestriction(andNode.getRightOperand(), hashMap);
            if (restriction == null || restriction2 == null) {
                return null;
            }
            return new Restriction.AND(restriction, restriction2);
        }
        else if (valueNode instanceof OrNode) {
            final OrNode orNode = (OrNode)valueNode;
            if (orNode.getRightOperand().isBooleanFalse()) {
                return this.makeRestriction(orNode.getLeftOperand(), hashMap);
            }
            final Restriction restriction3 = this.makeRestriction(orNode.getLeftOperand(), hashMap);
            final Restriction restriction4 = this.makeRestriction(orNode.getRightOperand(), hashMap);
            if (restriction3 == null || restriction4 == null) {
                return null;
            }
            return new Restriction.OR(restriction3, restriction4);
        }
        else {
            if (valueNode instanceof BinaryRelationalOperatorNode) {
                return this.makeLeafRestriction((BinaryRelationalOperatorNode)valueNode, hashMap);
            }
            if (valueNode instanceof IsNullNode) {
                return this.makeIsNullRestriction((IsNullNode)valueNode, hashMap);
            }
            return this.iAmConfused(valueNode);
        }
    }
    
    private Restriction makeLeafRestriction(final BinaryRelationalOperatorNode binaryRelationalOperatorNode, final HashMap hashMap) throws StandardException {
        int n = binaryRelationalOperatorNode.getOperator();
        ColumnReference columnReference;
        ValueNode valueNode;
        if (binaryRelationalOperatorNode.getLeftOperand() instanceof ColumnReference) {
            columnReference = (ColumnReference)binaryRelationalOperatorNode.getLeftOperand();
            valueNode = binaryRelationalOperatorNode.getRightOperand();
        }
        else {
            if (!(binaryRelationalOperatorNode.getRightOperand() instanceof ColumnReference)) {
                return this.iAmConfused(binaryRelationalOperatorNode);
            }
            columnReference = (ColumnReference)binaryRelationalOperatorNode.getRightOperand();
            valueNode = binaryRelationalOperatorNode.getLeftOperand();
            n = this.flipOperator(n);
        }
        final int mapOperator = this.mapOperator(n);
        if (mapOperator < 0) {
            return this.iAmConfused(binaryRelationalOperatorNode);
        }
        final String s = hashMap.get(columnReference.getColumnName());
        final Object squeezeConstantValue = this.squeezeConstantValue(valueNode);
        if (s == null || squeezeConstantValue == null) {
            return this.iAmConfused(binaryRelationalOperatorNode);
        }
        return new Restriction.ColumnQualifier(s, mapOperator, squeezeConstantValue);
    }
    
    private Restriction makeIsNullRestriction(final IsNullNode isNullNode, final HashMap hashMap) throws StandardException {
        final ColumnReference columnReference = (ColumnReference)isNullNode.getOperand();
        final int mapOperator = this.mapOperator(isNullNode.getOperator());
        if (mapOperator < 0) {
            return this.iAmConfused(isNullNode);
        }
        if (mapOperator != 5 && mapOperator != 6) {
            return this.iAmConfused(isNullNode);
        }
        final String s = hashMap.get(columnReference.getColumnName());
        if (s == null) {
            return this.iAmConfused(isNullNode);
        }
        return new Restriction.ColumnQualifier(s, mapOperator, null);
    }
    
    private Restriction iAmConfused(final ValueNode valueNode) throws StandardException {
        return null;
    }
    
    private int flipOperator(final int n) throws StandardException {
        switch (n) {
            case 1: {
                return 1;
            }
            case 4: {
                return 6;
            }
            case 3: {
                return 5;
            }
            case 6: {
                return 4;
            }
            case 5: {
                return 3;
            }
            case 2: {
                return 2;
            }
            default: {
                return -1;
            }
        }
    }
    
    private int mapOperator(final int n) throws StandardException {
        switch (n) {
            case 1: {
                return 1;
            }
            case 4: {
                return 4;
            }
            case 3: {
                return 3;
            }
            case 6: {
                return 2;
            }
            case 5: {
                return 0;
            }
            case 7: {
                return 5;
            }
            case 8: {
                return 6;
            }
            case 2: {
                return 7;
            }
            default: {
                return -1;
            }
        }
    }
    
    private Object squeezeConstantValue(final ValueNode valueNode) throws StandardException {
        if (valueNode instanceof ParameterNode) {
            return new int[] { ((ParameterNode)valueNode).getParameterNumber() };
        }
        if (valueNode instanceof ConstantNode) {
            return ((ConstantNode)valueNode).getValue().getObject();
        }
        return this.iAmConfused(valueNode);
    }
    
    public void generate(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        if (this.isRestrictedTableFunction && this.projectedColumnNames == null) {
            this.computeProjection();
        }
        this.methodCall.accept(new RemapCRsVisitor(true));
        this.remapBaseTableColumns();
        this.assignResultSetNumber();
        activationClassBuilder.pushGetResultSetFactoryExpression(methodBuilder);
        methodBuilder.callMethod((short)185, null, "getVTIResultSet", "org.apache.derby.iapi.sql.execute.NoPutResultSet", this.getScanArguments(activationClassBuilder, methodBuilder));
    }
    
    private void remapBaseTableColumns() throws StandardException {
        for (final ColumnReference columnReference : this.getNodesFromParameters(ColumnReference.class)) {
            final FromTable fromTable = this.argSources.get(new Integer(columnReference.getTableNumber()));
            if (fromTable != null) {
                final ResultColumnList resultColumns = fromTable.getResultColumns();
                if (resultColumns == null) {
                    continue;
                }
                final ResultColumn resultColumn = resultColumns.getResultColumn(columnReference.getColumnName());
                if (resultColumn == null) {
                    continue;
                }
                columnReference.setSource(resultColumn);
            }
        }
    }
    
    private int getScanArguments(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        final int size = this.resultColumns.size();
        final FormatableBitSet set = new FormatableBitSet(size);
        int addItem = -1;
        int n = 0;
        this.costEstimate = this.getFinalCostEstimate();
        for (int i = 0; i < size; ++i) {
            if (((ResultColumn)this.resultColumns.elementAt(i)).isReferenced()) {
                set.set(i);
                ++n;
            }
        }
        if (n != this.numVTICols) {
            addItem = activationClassBuilder.addItem(set);
        }
        final int addItem2 = activationClassBuilder.addItem(this.compileTimeConstants);
        activationClassBuilder.pushThisAsActivation(methodBuilder);
        methodBuilder.push(activationClassBuilder.addItem(this.resultColumns.buildRowTemplate()));
        final boolean b = this.version2 && this.getNodesFromParameters(ParameterNode.class).isEmpty() && this.getNodesFromParameters(ColumnReference.class).isEmpty();
        methodBuilder.push(this.resultSetNumber);
        this.generateConstructor(activationClassBuilder, methodBuilder, b);
        methodBuilder.push(this.methodCall.getJavaClassName());
        if (this.restrictionList != null) {
            this.restrictionList.generateQualifiers(activationClassBuilder, methodBuilder, this, true);
        }
        else {
            methodBuilder.pushNull("org.apache.derby.iapi.store.access.Qualifier[][]");
        }
        methodBuilder.push(addItem);
        methodBuilder.push(this.version2);
        methodBuilder.push(b);
        methodBuilder.push(addItem2);
        methodBuilder.push(this.isTarget);
        methodBuilder.push(this.getCompilerContext().getScanIsolationLevel());
        methodBuilder.push(this.costEstimate.rowCount());
        methodBuilder.push(this.costEstimate.getEstimatedCost());
        methodBuilder.push(this.isDerbyStyleTableFunction);
        int addItem3 = -1;
        if (this.isDerbyStyleTableFunction) {
            addItem3 = activationClassBuilder.addItem(this.methodCall.getRoutineInfo().getReturnType());
        }
        methodBuilder.push(addItem3);
        methodBuilder.push(this.storeObjectInPS(activationClassBuilder, this.projectedColumnNames));
        methodBuilder.push(this.storeObjectInPS(activationClassBuilder, this.vtiRestriction));
        return 18;
    }
    
    private int storeObjectInPS(final ActivationClassBuilder activationClassBuilder, final Object o) throws StandardException {
        if (o == null) {
            return -1;
        }
        return activationClassBuilder.addItem(o);
    }
    
    private void generateConstructor(final ActivationClassBuilder activationClassBuilder, final MethodBuilder methodBuilder, final boolean b) throws StandardException {
        final String s = this.version2 ? "java.sql.PreparedStatement" : "java.sql.ResultSet";
        final MethodBuilder generatedFun = activationClassBuilder.newGeneratedFun(s, 1);
        generatedFun.addThrownException("java.lang.Exception");
        final LocalField localField = b ? activationClassBuilder.newFieldDeclaration(2, "java.sql.PreparedStatement") : null;
        if (b) {
            generatedFun.getField(localField);
            generatedFun.conditionalIfNull();
        }
        this.methodCall.generateExpression(activationClassBuilder, generatedFun);
        generatedFun.upCast(s);
        if (b) {
            generatedFun.putField(localField);
            generatedFun.startElseCode();
            generatedFun.getField(localField);
            generatedFun.completeConditional();
        }
        generatedFun.methodReturn();
        generatedFun.complete();
        activationClassBuilder.pushMethodReference(methodBuilder, generatedFun);
        if (b) {
            final MethodBuilder closeActivationMethod = activationClassBuilder.getCloseActivationMethod();
            closeActivationMethod.getField(localField);
            closeActivationMethod.conditionalIfNull();
            closeActivationMethod.push(0);
            closeActivationMethod.startElseCode();
            closeActivationMethod.getField(localField);
            closeActivationMethod.callMethod((short)185, "java.sql.Statement", "close", "void", 0);
            closeActivationMethod.push(0);
            closeActivationMethod.completeConditional();
            closeActivationMethod.endStatement();
        }
    }
    
    public boolean referencesTarget(final String s, final boolean b) throws StandardException {
        return !b && s.equals(this.methodCall.getJavaClassName());
    }
    
    void acceptChildren(final Visitor visitor) throws StandardException {
        super.acceptChildren(visitor);
        if (this.methodCall != null) {
            this.methodCall = (MethodCallNode)this.methodCall.accept(visitor);
        }
    }
    
    private UUID getSpecialTriggerVTITableName(final LanguageConnectionContext languageConnectionContext, final String s) throws StandardException {
        if (!s.equals("org.apache.derby.catalog.TriggerNewTransitionRows") && !s.equals("org.apache.derby.catalog.TriggerOldTransitionRows")) {
            return null;
        }
        if (languageConnectionContext.getTriggerTable() != null) {
            return languageConnectionContext.getTriggerTable().getUUID();
        }
        if (languageConnectionContext.getTriggerExecutionContext() != null) {
            return languageConnectionContext.getTriggerExecutionContext().getTargetTableId();
        }
        throw StandardException.newException("42Y45", s);
    }
    
    private ResultColumnList genResultColList(final TableDescriptor tableDescriptor) throws StandardException {
        final ResultColumnList list = (ResultColumnList)this.getNodeFactory().getNode(9, this.getContextManager());
        final ColumnDescriptorList columnDescriptorList = tableDescriptor.getColumnDescriptorList();
        for (int size = columnDescriptorList.size(), i = 0; i < size; ++i) {
            final ColumnDescriptor element = columnDescriptorList.elementAt(i);
            list.addResultColumn((ResultColumn)this.getNodeFactory().getNode(80, element, this.getNodeFactory().getNode(94, element.getColumnName(), this.exposedName, element.getType(), this.getContextManager()), this.getContextManager()));
        }
        return list;
    }
    
    public boolean needsSpecialRCLBinding() {
        return true;
    }
    
    boolean isUpdatableCursor() throws StandardException {
        return true;
    }
    
    public final boolean isCompileTime() {
        return true;
    }
    
    public String getOriginalSQL() {
        return this.getCompilerContext().getParser().getSQLtext();
    }
    
    public final int getStatementIsolationLevel() {
        return ExecutionContext.CS_TO_JDBC_ISOLATION_LEVEL_MAP[this.getCompilerContext().getScanIsolationLevel()];
    }
    
    public void setSharedState(final String s, final Serializable s2) {
        if (s == null) {
            return;
        }
        if (this.compileTimeConstants == null) {
            this.compileTimeConstants = new FormatableHashtable();
        }
        this.compileTimeConstants.put(s, s2);
    }
    
    public Object getSharedState(final String key) {
        if (key == null || this.compileTimeConstants == null) {
            return null;
        }
        return this.compileTimeConstants.get(key);
    }
    
    private void createResultColumnsForTableFunction(final TypeDescriptor typeDescriptor) throws StandardException {
        final String[] rowColumnNames = typeDescriptor.getRowColumnNames();
        final TypeDescriptor[] rowTypes = typeDescriptor.getRowTypes();
        for (int i = 0; i < rowColumnNames.length; ++i) {
            final String s = rowColumnNames[i];
            final DataTypeDescriptor type = DataTypeDescriptor.getType(rowTypes[i]);
            this.resultColumns.addColumn(this.exposedName, s, type).setColumnDescriptor(null, new ColumnDescriptor(s, i + 1, type, null, null, (UUID)null, null, 0L, 0L, 0L));
        }
    }
    
    private boolean implementsDerbyStyleVTICosting(final String s) throws StandardException {
        final Class lookupClass = this.lookupClass(s);
        final Class lookupClass2 = this.lookupClass(VTICosting.class.getName());
        try {
            if (!lookupClass2.isAssignableFrom(lookupClass)) {
                return false;
            }
        }
        catch (Throwable t) {
            throw StandardException.unexpectedUserException(t);
        }
        Constructor constructor;
        try {
            constructor = lookupClass.getConstructor((Class[])new Class[0]);
        }
        catch (Throwable t2) {
            throw StandardException.newException("42ZB5", t2, s);
        }
        if (Modifier.isPublic(constructor.getModifiers())) {
            return true;
        }
        throw StandardException.newException("42ZB5", s);
    }
    
    private VTICosting getVTICosting() throws StandardException {
        if (!this.isDerbyStyleTableFunction) {
            return (VTICosting)(this.version2 ? this.ps : ((VTICosting)this.rs));
        }
        final Class lookupClass = this.lookupClass(this.methodCall.getJavaClassName());
        try {
            return lookupClass.getConstructor((Class<?>[])new Class[0]).newInstance((Object[])null);
        }
        catch (Throwable t) {
            throw StandardException.unexpectedUserException(t);
        }
    }
    
    private Class lookupClass(final String s) throws StandardException {
        try {
            return this.getClassFactory().getClassInspector().getClass(s);
        }
        catch (ClassNotFoundException ex) {
            throw StandardException.unexpectedUserException(ex);
        }
    }
}
