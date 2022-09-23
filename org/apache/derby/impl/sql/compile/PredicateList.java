// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.iapi.sql.dictionary.TableDescriptor;
import org.apache.derby.iapi.sql.compile.RequiredRowOrdering;
import org.apache.derby.iapi.services.compiler.LocalField;
import org.apache.derby.iapi.services.compiler.MethodBuilder;
import org.apache.derby.iapi.sql.compile.ExpressionClassBuilderInterface;
import org.apache.derby.iapi.types.DataValueDescriptor;
import java.util.ArrayList;
import org.apache.derby.iapi.sql.compile.CompilerContext;
import java.util.Iterator;
import java.util.List;
import org.apache.derby.iapi.sql.compile.Visitor;
import org.apache.derby.iapi.util.JBitSet;
import java.util.Arrays;
import org.apache.derby.iapi.sql.compile.AccessPath;
import org.apache.derby.iapi.sql.dictionary.ConglomerateDescriptor;
import org.apache.derby.iapi.sql.compile.Optimizable;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.compile.OptimizablePredicate;
import org.apache.derby.iapi.sql.compile.OptimizablePredicateList;

public class PredicateList extends QueryTreeNodeVector implements OptimizablePredicateList
{
    private int numberOfStartPredicates;
    private int numberOfStopPredicates;
    private int numberOfQualifiers;
    private static final int QUALIFIER_ORDER_EQUALS = 0;
    private static final int QUALIFIER_ORDER_OTHER_RELOP = 1;
    private static final int QUALIFIER_ORDER_NOT_EQUALS = 2;
    private static final int QUALIFIER_ORDER_NON_QUAL = 3;
    private static final int QUALIFIER_ORDER_OR_CLAUSE = 4;
    private static final int QUALIFIER_NUM_CATEGORIES = 5;
    
    public OptimizablePredicate getOptPredicate(final int n) {
        return (OptimizablePredicate)this.elementAt(n);
    }
    
    public final void removeOptPredicate(final int n) throws StandardException {
        final Predicate predicate = (Predicate)this.remove(n);
        if (predicate.isStartKey()) {
            --this.numberOfStartPredicates;
        }
        if (predicate.isStopKey()) {
            --this.numberOfStopPredicates;
        }
        if (predicate.isQualifier()) {
            --this.numberOfQualifiers;
        }
    }
    
    public final void removeOptPredicate(final OptimizablePredicate optimizablePredicate) {
        this.removeElement((QueryTreeNode)optimizablePredicate);
        if (optimizablePredicate.isStartKey()) {
            --this.numberOfStartPredicates;
        }
        if (optimizablePredicate.isStopKey()) {
            --this.numberOfStopPredicates;
        }
        if (optimizablePredicate.isQualifier()) {
            --this.numberOfQualifiers;
        }
    }
    
    public void addOptPredicate(final OptimizablePredicate optimizablePredicate) {
        this.addElement((QueryTreeNode)optimizablePredicate);
        if (optimizablePredicate.isStartKey()) {
            ++this.numberOfStartPredicates;
        }
        if (optimizablePredicate.isStopKey()) {
            ++this.numberOfStopPredicates;
        }
        if (optimizablePredicate.isQualifier()) {
            ++this.numberOfQualifiers;
        }
    }
    
    public void addOptPredicate(final OptimizablePredicate optimizablePredicate, final int n) {
        this.insertElementAt((QueryTreeNode)optimizablePredicate, n);
        if (optimizablePredicate.isStartKey()) {
            ++this.numberOfStartPredicates;
        }
        if (optimizablePredicate.isStopKey()) {
            ++this.numberOfStopPredicates;
        }
        if (optimizablePredicate.isQualifier()) {
            ++this.numberOfQualifiers;
        }
    }
    
    public boolean useful(final Optimizable optimizable, final ConglomerateDescriptor conglomerateDescriptor) throws StandardException {
        boolean b = false;
        if (!conglomerateDescriptor.isIndex()) {
            return false;
        }
        for (int size = this.size(), i = 0; i < size; ++i) {
            final Predicate predicate = (Predicate)this.elementAt(i);
            final RelationalOperator relop = predicate.getRelop();
            final InListOperatorNode sourceInList = predicate.getSourceInList();
            final boolean b2 = sourceInList != null;
            if (b2 || relop != null) {
                if (b2 || relop.usefulStartKey(optimizable) || relop.usefulStopKey(optimizable)) {
                    ColumnReference columnOperand = null;
                    if (b2) {
                        if (sourceInList.getLeftOperand() instanceof ColumnReference) {
                            columnOperand = (ColumnReference)sourceInList.getLeftOperand();
                            if (columnOperand.getColumnNumber() != conglomerateDescriptor.getIndexDescriptor().baseColumnPositions()[0]) {
                                columnOperand = null;
                            }
                        }
                    }
                    else {
                        columnOperand = relop.getColumnOperand(optimizable, conglomerateDescriptor.getIndexDescriptor().baseColumnPositions()[0]);
                    }
                    if (columnOperand != null) {
                        if (!b2 || !sourceInList.selfReference(columnOperand)) {
                            if (b2 || !relop.selfComparison(columnOperand)) {
                                b = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return b;
    }
    
    public void pushUsefulPredicates(final Optimizable optimizable) throws StandardException {
        final AccessPath trulyTheBestAccessPath = optimizable.getTrulyTheBestAccessPath();
        this.orderUsefulPredicates(optimizable, trulyTheBestAccessPath.getConglomerateDescriptor(), true, trulyTheBestAccessPath.getNonMatchingIndexScan(), trulyTheBestAccessPath.getCoveringIndexScan());
    }
    
    public void classify(final Optimizable optimizable, final ConglomerateDescriptor conglomerateDescriptor) throws StandardException {
        this.orderUsefulPredicates(optimizable, conglomerateDescriptor, false, false, false);
    }
    
    public void markAllPredicatesQualifiers() {
        final int size = this.size();
        for (int i = 0; i < size; ++i) {
            ((Predicate)this.elementAt(i)).markQualifier();
        }
        this.numberOfQualifiers = size;
    }
    
    public int hasEqualityPredicateOnOrderedColumn(final Optimizable optimizable, final int n, final boolean b) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final Predicate predicate = (Predicate)this.elementAt(i);
            if (!predicate.getReferencedMap().hasSingleBitSet()) {
                if (predicate.getAndNode().getLeftOperand().optimizableEqualityNode(optimizable, n, b)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public boolean hasOptimizableEqualityPredicate(final Optimizable optimizable, final int n, final boolean b) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            if (((Predicate)this.elementAt(i)).getAndNode().getLeftOperand().optimizableEqualityNode(optimizable, n, b)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean hasOptimizableEquijoin(final Optimizable optimizable, final int n) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final Predicate predicate = (Predicate)this.elementAt(i);
            if (!predicate.isScopedForPush()) {
                final ValueNode leftOperand = predicate.getAndNode().getLeftOperand();
                if (leftOperand.optimizableEqualityNode(optimizable, n, false)) {
                    if (((RelationalOperator)leftOperand).isQualifier(optimizable, false)) {
                        if (!predicate.getReferencedMap().hasSingleBitSet()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public void putOptimizableEqualityPredicateFirst(final Optimizable optimizable, final int n) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final Predicate predicate = (Predicate)this.elementAt(i);
            if (predicate.getAndNode().getLeftOperand().optimizableEqualityNode(optimizable, n, false)) {
                if (i != 0) {
                    this.removeElementAt(i);
                    this.insertElementAt(predicate, 0);
                }
                return;
            }
        }
    }
    
    private void orderUsefulPredicates(final Optimizable optimizable, final ConglomerateDescriptor conglomerateDescriptor, final boolean b, final boolean b2, final boolean b3) throws StandardException {
        final int size = this.size();
        Predicate[] a = new Predicate[size];
        int n = 0;
        for (int i = 0; i < size; ++i) {
            ((Predicate)this.elementAt(i)).clearScanFlags();
        }
        if (conglomerateDescriptor == null || !conglomerateDescriptor.isIndex() || (b2 && b3)) {
            final Predicate[] array = new Predicate[size];
            for (int j = 0; j < size; ++j) {
                final Predicate predicate = (Predicate)this.elementAt(j);
                if (!predicate.isRelationalOpPredicate()) {
                    if (!predicate.isPushableOrClause(optimizable)) {
                        continue;
                    }
                }
                else if (!predicate.getRelop().isQualifier(optimizable, b)) {
                    continue;
                }
                predicate.markQualifier();
                if (b && optimizable.pushOptPredicate(predicate)) {
                    array[j] = predicate;
                }
            }
            for (int k = size - 1; k >= 0; --k) {
                if (array[k] != null) {
                    this.removeOptPredicate(array[k]);
                }
            }
            return;
        }
        final int[] baseColumnPositions = conglomerateDescriptor.getIndexDescriptor().baseColumnPositions();
        final boolean[] ascending = conglomerateDescriptor.getIndexDescriptor().isAscending();
        final boolean b4 = b && optimizable.getTrulyTheBestAccessPath().getJoinStrategy().isHashJoin();
        for (int l = 0; l < size; ++l) {
            final Predicate predicate2 = (Predicate)this.elementAt(l);
            ColumnReference columnOperand = null;
            final RelationalOperator relop = predicate2.getRelop();
            final InListOperatorNode sourceInList = predicate2.getSourceInList();
            final boolean b5 = sourceInList != null;
            if (!b5) {
                if (relop == null) {
                    continue;
                }
                if (!relop.isQualifier(optimizable, b)) {
                    continue;
                }
            }
            if (!b4 || !predicate2.isInListProbePredicate()) {
                int indexPosition;
                for (indexPosition = 0; indexPosition < baseColumnPositions.length; ++indexPosition) {
                    if (b5) {
                        if (sourceInList.getLeftOperand() instanceof ColumnReference) {
                            columnOperand = (ColumnReference)sourceInList.getLeftOperand();
                            if (optimizable.getTableNumber() != columnOperand.getTableNumber() || columnOperand.getColumnNumber() != baseColumnPositions[indexPosition] || sourceInList.selfReference(columnOperand)) {
                                columnOperand = null;
                            }
                            else if (predicate2.isInListProbePredicate() && indexPosition > 0) {
                                columnOperand = null;
                            }
                        }
                    }
                    else {
                        columnOperand = relop.getColumnOperand(optimizable, baseColumnPositions[indexPosition]);
                    }
                    if (columnOperand != null) {
                        break;
                    }
                }
                if (columnOperand != null) {
                    predicate2.setIndexPosition(indexPosition);
                    a[n++] = predicate2;
                }
            }
        }
        if (n == 0) {
            return;
        }
        if (a.length > n) {
            final Predicate[] array2 = new Predicate[n];
            System.arraycopy(a, 0, array2, 0, n);
            a = array2;
        }
        Arrays.sort(a);
        int n2 = -1;
        boolean b6 = false;
        int n3 = -1;
        boolean b7 = false;
        int n4 = 0;
        int n5 = -1;
        int n6 = -1;
        boolean b8 = false;
        boolean b9 = false;
        for (int n7 = 0; n7 < n; ++n7) {
            final Predicate predicate3 = a[n7];
            final int indexPosition2 = predicate3.getIndexPosition();
            boolean b10 = false;
            final RelationalOperator relop2 = predicate3.getRelop();
            int operator = -1;
            final boolean b11 = predicate3.getSourceInList() != null;
            if (relop2 != null) {
                operator = relop2.getOperator();
            }
            if (n2 != indexPosition2) {
                if (indexPosition2 - n2 > 1) {
                    b6 = true;
                }
                else if (operator == 1 || operator == 7) {
                    n6 = indexPosition2;
                }
                if (!b6 && !b9 && (b11 || (relop2.usefulStartKey(optimizable) && ascending[indexPosition2]) || (relop2.usefulStopKey(optimizable) && !ascending[indexPosition2]))) {
                    predicate3.markStartKey();
                    n2 = indexPosition2;
                    b10 = true;
                    b9 = (predicate3.getStartOperator(optimizable) == -1);
                }
            }
            if (n3 != indexPosition2) {
                if (indexPosition2 - n3 > 1) {
                    b7 = true;
                }
                if (!b7 && !b8 && (b11 || (relop2.usefulStopKey(optimizable) && ascending[indexPosition2]) || (relop2.usefulStartKey(optimizable) && !ascending[indexPosition2]))) {
                    predicate3.markStopKey();
                    n3 = indexPosition2;
                    b10 = true;
                    b8 = (predicate3.getStopOperator(optimizable) == 1);
                }
            }
            if (!b11 && (!b10 || (n4 != 0 && indexPosition2 != n5))) {
                predicate3.markQualifier();
            }
            if (n6 != indexPosition2 && n5 == -1 && operator != 1 && operator != 7) {
                n4 = 1;
                n5 = indexPosition2;
            }
            if (b) {
                if (!b11 || b10) {
                    Predicate predicate5;
                    if (b11 && !predicate3.isInListProbePredicate()) {
                        final AndNode andNode = (AndNode)this.getNodeFactory().getNode(39, predicate3.getAndNode().getLeftOperand(), predicate3.getAndNode().getRightOperand(), this.getContextManager());
                        andNode.copyFields(predicate3.getAndNode());
                        final Predicate predicate4 = (Predicate)this.getNodeFactory().getNode(78, andNode, predicate3.getReferencedSet(), this.getContextManager());
                        predicate4.copyFields(predicate3);
                        predicate5 = predicate4;
                    }
                    else {
                        predicate5 = predicate3;
                    }
                    if (optimizable.pushOptPredicate(predicate5) && (!b11 || predicate3.isInListProbePredicate())) {
                        this.removeOptPredicate(predicate3);
                    }
                }
            }
            else {
                this.removeOptPredicate(predicate3);
                this.addOptPredicate(predicate3, n7);
            }
        }
    }
    
    public void addPredicate(final Predicate predicate) throws StandardException {
        if (predicate.isStartKey()) {
            ++this.numberOfStartPredicates;
        }
        if (predicate.isStopKey()) {
            ++this.numberOfStopPredicates;
        }
        if (predicate.isQualifier()) {
            ++this.numberOfQualifiers;
        }
        this.addElement(predicate);
    }
    
    protected void transferNonQualifiers(final Optimizable optimizable, final PredicateList list) throws StandardException {
        for (int i = this.size() - 1; i >= 0; --i) {
            final Predicate predicate = (Predicate)this.elementAt(i);
            if (!predicate.isRelationalOpPredicate() || !predicate.getRelop().isQualifier(optimizable, false)) {
                predicate.clearScanFlags();
                this.removeElementAt(i);
                list.addElement(predicate);
            }
        }
        this.markAllPredicatesQualifiers();
    }
    
    public void categorize() throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((Predicate)this.elementAt(i)).categorize();
        }
    }
    
    public void eliminateBooleanTrueAndBooleanTrue() {
        for (int i = this.size() - 1; i >= 0; --i) {
            final AndNode andNode = ((Predicate)this.elementAt(i)).getAndNode();
            if (andNode.getLeftOperand().isBooleanTrue() && andNode.getRightOperand().isBooleanTrue()) {
                this.removeElementAt(i);
            }
        }
    }
    
    public ValueNode restoreConstantPredicates() throws StandardException {
        BinaryOperatorNode binaryOperatorNode = null;
        ValueNode rightOperand = null;
        for (int i = this.size() - 1; i >= 0; --i) {
            final AndNode andNode = ((Predicate)this.elementAt(i)).getAndNode();
            if (andNode.isConstantExpression()) {
                this.removeElementAt(i);
                if (!andNode.getLeftOperand().isBooleanTrue() || !andNode.getRightOperand().isBooleanTrue()) {
                    if (andNode.getLeftOperand().isBooleanFalse()) {
                        binaryOperatorNode = andNode;
                    }
                    if (rightOperand != null) {
                        andNode.setRightOperand(rightOperand);
                        if (rightOperand.getTypeServices().isNullable()) {
                            andNode.setNullability(true);
                        }
                    }
                    rightOperand = andNode;
                }
            }
        }
        if (rightOperand != null && ((AndNode)rightOperand).getRightOperand().isBooleanTrue()) {
            rightOperand = ((AndNode)rightOperand).getLeftOperand();
        }
        else if (binaryOperatorNode != null) {
            rightOperand = binaryOperatorNode.getLeftOperand();
        }
        return rightOperand;
    }
    
    public ValueNode restorePredicates() throws StandardException {
        BinaryOperatorNode binaryOperatorNode = null;
        ValueNode rightOperand = null;
        for (int size = this.size(), i = 0; i < size; ++i) {
            final AndNode andNode = ((Predicate)this.elementAt(i)).getAndNode();
            if (!andNode.getLeftOperand().isBooleanTrue() || !andNode.getRightOperand().isBooleanTrue()) {
                if (andNode.getLeftOperand().isBooleanFalse()) {
                    binaryOperatorNode = andNode;
                }
                if (rightOperand != null) {
                    andNode.setRightOperand(rightOperand);
                    if (rightOperand.getTypeServices().isNullable()) {
                        andNode.setNullability(true);
                    }
                }
                rightOperand = andNode;
            }
        }
        if (rightOperand != null && ((AndNode)rightOperand).getRightOperand().isBooleanTrue()) {
            rightOperand = ((AndNode)rightOperand).getLeftOperand();
        }
        else if (binaryOperatorNode != null) {
            rightOperand = binaryOperatorNode.getLeftOperand();
        }
        this.removeAllElements();
        return rightOperand;
    }
    
    public void remapColumnReferencesToExpressions() throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final Predicate predicate = (Predicate)this.elementAt(i);
            predicate.setAndNode((AndNode)predicate.getAndNode().remapColumnReferencesToExpressions());
        }
    }
    
    void pullExpressions(final int n, final ValueNode valueNode) throws StandardException {
        if (valueNode != null) {
            AndNode andNode = (AndNode)valueNode;
            final BooleanConstantNode rightOperand = (BooleanConstantNode)this.getNodeFactory().getNode(38, Boolean.TRUE, this.getContextManager());
            while (andNode.getRightOperand() instanceof AndNode) {
                final AndNode andNode2 = andNode;
                andNode = (AndNode)andNode.getRightOperand();
                andNode2.setRightOperand(null);
                andNode2.setRightOperand(rightOperand);
                this.addPredicate((Predicate)this.getNodeFactory().getNode(78, andNode2, new JBitSet(n), this.getContextManager()));
            }
            this.addPredicate((Predicate)this.getNodeFactory().getNode(78, andNode, new JBitSet(n), this.getContextManager()));
        }
    }
    
    public void xorReferencedSet(final JBitSet set) {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((Predicate)this.elementAt(i)).getReferencedSet().xor(set);
        }
    }
    
    private void countScanFlags() {
        for (int size = this.size(), i = 0; i < size; ++i) {
            final Predicate predicate = (Predicate)this.elementAt(i);
            if (predicate.isStartKey()) {
                ++this.numberOfStartPredicates;
            }
            if (predicate.isStopKey()) {
                ++this.numberOfStopPredicates;
            }
            if (predicate.isQualifier()) {
                ++this.numberOfQualifiers;
            }
        }
    }
    
    private static boolean isConstantOrParameterNode(final ValueNode valueNode) {
        return valueNode instanceof ConstantNode || valueNode instanceof ParameterNode;
    }
    
    void pushExpressionsIntoSelect(final SelectNode selectNode, final boolean b) throws StandardException {
        for (int i = this.size() - 1; i >= 0; --i) {
            Predicate predicate = (Predicate)this.elementAt(i);
            final CollectNodesVisitor collectNodesVisitor = new CollectNodesVisitor(ColumnReference.class);
            predicate.getAndNode().accept(collectNodesVisitor);
            final List list = collectNodesVisitor.getList();
            int n = (list.size() > 0) ? 1 : 0;
            if (n != 0) {
                final Iterator<ColumnReference> iterator = list.iterator();
                while (iterator.hasNext()) {
                    if (!iterator.next().pointsToColumnReference()) {
                        n = 0;
                        break;
                    }
                }
            }
            if (n != 0) {
                if (b) {
                    final AndNode andNode = predicate.getAndNode();
                    BinaryRelationalOperatorNode binaryRelationalOperatorNode = null;
                    BinaryListOperatorNode binaryListOperatorNode = null;
                    ColumnReference columnReference;
                    if (andNode.getLeftOperand() instanceof BinaryRelationalOperatorNode) {
                        binaryRelationalOperatorNode = (BinaryRelationalOperatorNode)andNode.getLeftOperand();
                        if (!(binaryRelationalOperatorNode.getLeftOperand() instanceof ColumnReference)) {
                            continue;
                        }
                        if (!isConstantOrParameterNode(binaryRelationalOperatorNode.getRightOperand())) {
                            continue;
                        }
                        columnReference = (ColumnReference)binaryRelationalOperatorNode.getLeftOperand();
                    }
                    else {
                        if (!(andNode.getLeftOperand() instanceof InListOperatorNode)) {
                            continue;
                        }
                        binaryListOperatorNode = (InListOperatorNode)andNode.getLeftOperand();
                        if (!(binaryListOperatorNode.getLeftOperand() instanceof ColumnReference)) {
                            continue;
                        }
                        if (!binaryListOperatorNode.getRightOperandList().isConstantExpression()) {
                            continue;
                        }
                        columnReference = (ColumnReference)binaryListOperatorNode.getLeftOperand();
                    }
                    final ColumnReference columnReferenceInResult = selectNode.findColumnReferenceInResult(columnReference.columnName);
                    if (columnReferenceInResult == null) {
                        continue;
                    }
                    BinaryRelationalOperatorNode binaryRelationalOperatorNode3;
                    if (andNode.getLeftOperand() instanceof BinaryRelationalOperatorNode) {
                        InListOperatorNode inListOperatorNode = binaryRelationalOperatorNode.getInListOp();
                        if (inListOperatorNode != null) {
                            inListOperatorNode = inListOperatorNode.shallowCopy();
                            inListOperatorNode.setLeftOperand(columnReferenceInResult);
                        }
                        final BinaryRelationalOperatorNode binaryRelationalOperatorNode2 = (BinaryRelationalOperatorNode)this.getNodeFactory().getNode(binaryRelationalOperatorNode.getNodeType(), columnReferenceInResult, binaryRelationalOperatorNode.getRightOperand(), inListOperatorNode, binaryRelationalOperatorNode.getForQueryRewrite(), this.getContextManager());
                        binaryRelationalOperatorNode2.bindComparisonOperator();
                        binaryRelationalOperatorNode3 = binaryRelationalOperatorNode2;
                    }
                    else {
                        final InListOperatorNode inListOperatorNode2 = (InListOperatorNode)this.getNodeFactory().getNode(55, columnReferenceInResult, binaryListOperatorNode.getRightOperandList(), this.getContextManager());
                        inListOperatorNode2.setType(binaryListOperatorNode.getTypeServices());
                        binaryRelationalOperatorNode3 = (BinaryRelationalOperatorNode)inListOperatorNode2;
                    }
                    final AndNode andNode2 = (AndNode)this.getNodeFactory().getNode(39, binaryRelationalOperatorNode3, this.getNodeFactory().getNode(38, Boolean.TRUE, this.getContextManager()), this.getContextManager());
                    andNode2.postBindFixup();
                    predicate = (Predicate)this.getNodeFactory().getNode(78, andNode2, new JBitSet(selectNode.referencedTableMap.size()), this.getContextManager());
                }
                else {
                    if (predicate.isStartKey()) {
                        --this.numberOfStartPredicates;
                    }
                    if (predicate.isStopKey()) {
                        --this.numberOfStopPredicates;
                    }
                    if (predicate.isQualifier()) {
                        --this.numberOfQualifiers;
                    }
                    predicate.clearScanFlags();
                    this.removeElementAt(i);
                }
                selectNode.pushExpressionsIntoSelect(predicate);
            }
        }
    }
    
    void markReferencedColumns() throws StandardException {
        final CollectNodesVisitor collectNodesVisitor = new CollectNodesVisitor(ColumnReference.class);
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((Predicate)this.elementAt(i)).getAndNode().accept(collectNodesVisitor);
        }
        final Iterator iterator = collectNodesVisitor.getList().iterator();
        while (iterator.hasNext()) {
            final ResultColumn source = iterator.next().getSource();
            if (source != null) {
                source.markAllRCsInChainReferenced();
            }
        }
    }
    
    void checkTopPredicatesForEqualsConditions(final int n, final boolean[] array, final int[] array2, final JBitSet[] array3, final boolean b) throws StandardException {
        for (int size = this.size(), i = 0; i < size; ++i) {
            ((Predicate)this.elementAt(i)).getAndNode().checkTopPredicatesForEqualsConditions(n, array, array2, array3, b);
        }
    }
    
    boolean allPushable() {
        for (int size = this.size(), i = 0; i < size; ++i) {
            if (!((Predicate)this.elementAt(i)).getPushable()) {
                return false;
            }
        }
        return true;
    }
    
    boolean allReference(final FromBaseTable fromBaseTable) {
        final int tableNumber = fromBaseTable.getTableNumber();
        for (int i = 0; i < this.size(); ++i) {
            if (!((Predicate)this.elementAt(i)).getReferencedSet().get(tableNumber)) {
                return false;
            }
        }
        return true;
    }
    
    PredicateList getPushablePredicates(final JBitSet set) throws StandardException {
        PredicateList list = null;
        for (int i = this.size() - 1; i >= 0; --i) {
            final Predicate predicate = (Predicate)this.elementAt(i);
            if (predicate.getPushable()) {
                if (set.contains(predicate.getReferencedSet())) {
                    if (list == null) {
                        list = (PredicateList)this.getNodeFactory().getNode(8, this.getContextManager());
                    }
                    list.addPredicate(predicate);
                    predicate.getAndNode().accept(new RemapCRsVisitor(true));
                    this.removeElementAt(i);
                }
            }
        }
        return list;
    }
    
    void decrementLevel(final FromList list, final int n) {
        final int[] tableNumbers = list.getTableNumbers();
        for (int size = this.size(), i = 0; i < size; ++i) {
            ColumnReference columnReference = null;
            ColumnReference columnReference2 = null;
            final ValueNode leftOperand = ((Predicate)this.elementAt(i)).getAndNode().getLeftOperand();
            if (leftOperand instanceof BinaryOperatorNode) {
                final BinaryOperatorNode binaryOperatorNode = (BinaryOperatorNode)leftOperand;
                if (binaryOperatorNode.getLeftOperand() instanceof ColumnReference) {
                    columnReference = (ColumnReference)binaryOperatorNode.getLeftOperand();
                }
                if (binaryOperatorNode.getRightOperand() instanceof ColumnReference) {
                    columnReference2 = (ColumnReference)binaryOperatorNode.getRightOperand();
                }
            }
            else if (leftOperand instanceof UnaryOperatorNode) {
                final UnaryOperatorNode unaryOperatorNode = (UnaryOperatorNode)leftOperand;
                if (unaryOperatorNode.getOperand() instanceof ColumnReference) {
                    columnReference = (ColumnReference)unaryOperatorNode.getOperand();
                }
            }
            if (columnReference != null) {
                final int tableNumber = columnReference.getTableNumber();
                for (int j = 0; j < tableNumbers.length; ++j) {
                    if (tableNumbers[j] == tableNumber) {
                        columnReference.setSourceLevel(columnReference.getSourceLevel() - n);
                        break;
                    }
                }
            }
            if (columnReference2 != null) {
                final int tableNumber2 = columnReference2.getTableNumber();
                for (int k = 0; k < tableNumbers.length; ++k) {
                    if (tableNumbers[k] == tableNumber2) {
                        columnReference2.setSourceLevel(columnReference2.getSourceLevel() - n);
                        break;
                    }
                }
            }
        }
    }
    
    void joinClauseTransitiveClosure(final int n, final FromList list, final CompilerContext compilerContext) throws StandardException {
        if (list.size() < 3) {
            return;
        }
        final PredicateList[] array = new PredicateList[n];
        for (int i = 0; i < n; ++i) {
            array[i] = new PredicateList();
        }
        for (int size = this.size(), j = 0; j < size; ++j) {
            final Predicate predicate = (Predicate)this.elementAt(j);
            final ValueNode leftOperand = predicate.getAndNode().getLeftOperand();
            if (leftOperand.isBinaryEqualsOperatorNode()) {
                final BinaryRelationalOperatorNode binaryRelationalOperatorNode = (BinaryRelationalOperatorNode)leftOperand;
                final ValueNode leftOperand2 = binaryRelationalOperatorNode.getLeftOperand();
                final ValueNode rightOperand = binaryRelationalOperatorNode.getRightOperand();
                if (leftOperand2 instanceof ColumnReference && rightOperand instanceof ColumnReference) {
                    final ColumnReference columnReference = (ColumnReference)leftOperand2;
                    final ColumnReference columnReference2 = (ColumnReference)rightOperand;
                    if (columnReference.getSourceLevel() == columnReference2.getSourceLevel() && columnReference.getTableNumber() != columnReference2.getTableNumber() && !list.tableNumberIsNotExists(columnReference.getTableNumber()) && !list.tableNumberIsNotExists(columnReference2.getTableNumber())) {
                        array[columnReference.getTableNumber()].addElement(predicate);
                        array[columnReference2.getTableNumber()].addElement(predicate);
                    }
                }
            }
        }
        for (int k = 0; k < n; ++k) {
            final PredicateList list2 = array[k];
            if (list2.size() != 0) {
                final ArrayList list3 = new ArrayList<Predicate>();
                for (int l = list2.size() - 1; l >= 0; --l) {
                    final Predicate e = (Predicate)list2.elementAt(l);
                    if (e.getEquivalenceClass() != -1) {
                        list2.removeElementAt(l);
                        list3.add(e);
                    }
                }
                for (int index = 0; index < list3.size(); ++index) {
                    list2.insertElementAt(list3.get(index), 0);
                }
                for (int n2 = 0; n2 < list2.size(); ++n2) {
                    ColumnReference columnReference3 = null;
                    final int n3 = k;
                    final Predicate predicate2 = (Predicate)list2.elementAt(n2);
                    if (predicate2.getEquivalenceClass() == -1) {
                        predicate2.setEquivalenceClass(compilerContext.getNextEquivalenceClass());
                    }
                    final int equivalenceClass = predicate2.getEquivalenceClass();
                    final BinaryRelationalOperatorNode binaryRelationalOperatorNode2 = (BinaryRelationalOperatorNode)predicate2.getAndNode().getLeftOperand();
                    final ColumnReference columnReference4 = (ColumnReference)binaryRelationalOperatorNode2.getLeftOperand();
                    final ColumnReference columnReference5 = (ColumnReference)binaryRelationalOperatorNode2.getRightOperand();
                    int n4;
                    int n5;
                    int n6;
                    ColumnReference columnReference6;
                    if (columnReference4.getTableNumber() == n3) {
                        n4 = columnReference4.getColumnNumber();
                        n5 = columnReference5.getTableNumber();
                        n6 = columnReference5.getColumnNumber();
                        columnReference6 = columnReference4;
                    }
                    else {
                        n4 = columnReference5.getColumnNumber();
                        n5 = columnReference4.getTableNumber();
                        n6 = columnReference4.getColumnNumber();
                        columnReference6 = columnReference5;
                    }
                    final PredicateList list4 = array[n5];
                    for (int n7 = 0; n7 < list4.size(); ++n7) {
                        final Predicate predicate3 = (Predicate)list4.elementAt(n7);
                        if (predicate3.getEquivalenceClass() == -1 || predicate3.getEquivalenceClass() == equivalenceClass) {
                            final BinaryRelationalOperatorNode binaryRelationalOperatorNode3 = (BinaryRelationalOperatorNode)predicate3.getAndNode().getLeftOperand();
                            final ColumnReference columnReference7 = (ColumnReference)binaryRelationalOperatorNode3.getLeftOperand();
                            final ColumnReference columnReference8 = (ColumnReference)binaryRelationalOperatorNode3.getRightOperand();
                            int n8;
                            int n9;
                            if (columnReference7.getTableNumber() == n5) {
                                if (columnReference7.getColumnNumber() != n6) {
                                    continue;
                                }
                                n8 = columnReference8.getTableNumber();
                                n9 = columnReference8.getColumnNumber();
                            }
                            else {
                                if (columnReference8.getColumnNumber() != n6) {
                                    continue;
                                }
                                n8 = columnReference7.getTableNumber();
                                n9 = columnReference7.getColumnNumber();
                            }
                            if (n3 != n8 || n4 != n9) {
                                predicate3.setEquivalenceClass(equivalenceClass);
                                Predicate predicate4 = null;
                                PredicateList list5;
                                int n10;
                                BinaryRelationalOperatorNode binaryRelationalOperatorNode4;
                                ColumnReference columnReference9;
                                ColumnReference columnReference10;
                                int n11;
                                int n12;
                                for (list5 = array[n8], n10 = 0; n10 < list5.size(); ++n10) {
                                    predicate4 = (Predicate)list5.elementAt(n10);
                                    if (predicate4.getEquivalenceClass() == -1 || predicate4.getEquivalenceClass() == equivalenceClass) {
                                        binaryRelationalOperatorNode4 = (BinaryRelationalOperatorNode)predicate4.getAndNode().getLeftOperand();
                                        columnReference9 = (ColumnReference)binaryRelationalOperatorNode4.getLeftOperand();
                                        columnReference10 = (ColumnReference)binaryRelationalOperatorNode4.getRightOperand();
                                        if (columnReference9.getTableNumber() == n8) {
                                            if (columnReference9.getColumnNumber() != n9) {
                                                continue;
                                            }
                                            n11 = columnReference10.getTableNumber();
                                            n12 = columnReference10.getColumnNumber();
                                            columnReference3 = columnReference9;
                                        }
                                        else {
                                            if (columnReference10.getColumnNumber() != n9) {
                                                continue;
                                            }
                                            n11 = columnReference9.getTableNumber();
                                            n12 = columnReference9.getColumnNumber();
                                            columnReference3 = columnReference10;
                                        }
                                        if (n11 == n3 && n12 == n4) {
                                            break;
                                        }
                                    }
                                }
                                if (n10 != list5.size()) {
                                    predicate4.setEquivalenceClass(equivalenceClass);
                                }
                                else {
                                    final BinaryRelationalOperatorNode binaryRelationalOperatorNode5 = (BinaryRelationalOperatorNode)this.getNodeFactory().getNode(41, columnReference6.getClone(), columnReference3.getClone(), Boolean.FALSE, this.getContextManager());
                                    binaryRelationalOperatorNode5.bindComparisonOperator();
                                    final AndNode andNode = (AndNode)this.getNodeFactory().getNode(39, binaryRelationalOperatorNode5, this.getNodeFactory().getNode(38, Boolean.TRUE, this.getContextManager()), this.getContextManager());
                                    andNode.postBindFixup();
                                    final JBitSet set = new JBitSet(n);
                                    andNode.categorize(set, false);
                                    final Predicate predicate5 = (Predicate)this.getNodeFactory().getNode(78, andNode, set, this.getContextManager());
                                    predicate5.setEquivalenceClass(equivalenceClass);
                                    this.addPredicate(predicate5);
                                    if (n2 != list2.size() - 1) {
                                        list2.insertElementAt(predicate5, n2 + 1);
                                    }
                                    else {
                                        list2.addElement(predicate5);
                                    }
                                    if (list2 != list5) {
                                        list5.addElement(predicate5);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    void searchClauseTransitiveClosure(final int n, final boolean b) throws StandardException {
        final PredicateList list = new PredicateList();
        final PredicateList list2 = new PredicateList();
        RelationalOperator relationalOperator = null;
        for (int size = this.size(), i = 0; i < size; ++i) {
            final Predicate predicate = (Predicate)this.elementAt(i);
            final AndNode andNode = predicate.getAndNode();
            if (predicate.isRelationalOpPredicate()) {
                final RelationalOperator relationalOperator2 = (RelationalOperator)andNode.getLeftOperand();
                if (((ValueNode)relationalOperator2).isBinaryEqualsOperatorNode()) {
                    final BinaryRelationalOperatorNode binaryRelationalOperatorNode = (BinaryRelationalOperatorNode)(relationalOperator = relationalOperator2);
                    final ValueNode leftOperand = binaryRelationalOperatorNode.getLeftOperand();
                    final ValueNode rightOperand = binaryRelationalOperatorNode.getRightOperand();
                    if (leftOperand instanceof ColumnReference && rightOperand instanceof ColumnReference) {
                        final ColumnReference columnReference = (ColumnReference)leftOperand;
                        final ColumnReference columnReference2 = (ColumnReference)rightOperand;
                        if (columnReference.getSourceLevel() == columnReference2.getSourceLevel() && columnReference.getTableNumber() != columnReference2.getTableNumber()) {
                            list.addElement(predicate);
                        }
                        continue;
                    }
                }
                if (relationalOperator2 instanceof UnaryComparisonOperatorNode) {
                    if (((UnaryComparisonOperatorNode)relationalOperator2).getOperand() instanceof ColumnReference) {
                        list2.addElement(predicate);
                    }
                }
                else if (relationalOperator2 instanceof BinaryComparisonOperatorNode) {
                    final BinaryRelationalOperatorNode binaryRelationalOperatorNode2 = (BinaryRelationalOperatorNode)relationalOperator2;
                    final ValueNode leftOperand2 = binaryRelationalOperatorNode2.getLeftOperand();
                    final ValueNode rightOperand2 = binaryRelationalOperatorNode2.getRightOperand();
                    if (leftOperand2 instanceof ColumnReference && isConstantOrParameterNode(rightOperand2)) {
                        list2.addElement(predicate);
                    }
                    else if (isConstantOrParameterNode(leftOperand2) && rightOperand2 instanceof ColumnReference) {
                        andNode.setLeftOperand(binaryRelationalOperatorNode2.getSwappedEquivalent());
                        list2.addElement(predicate);
                    }
                }
            }
        }
        if (list.size() == 0 || list2.size() == 0) {
            return;
        }
        for (int j = 0; j < list2.size(); ++j) {
            DataValueDescriptor value = null;
            final RelationalOperator transitiveSearchClauseAdded = (RelationalOperator)((Predicate)list2.elementAt(j)).getAndNode().getLeftOperand();
            ColumnReference columnReference3;
            if (transitiveSearchClauseAdded instanceof UnaryComparisonOperatorNode) {
                columnReference3 = (ColumnReference)((UnaryComparisonOperatorNode)transitiveSearchClauseAdded).getOperand();
            }
            else {
                columnReference3 = (ColumnReference)((BinaryComparisonOperatorNode)transitiveSearchClauseAdded).getLeftOperand();
                if (((BinaryComparisonOperatorNode)transitiveSearchClauseAdded).getRightOperand() instanceof ConstantNode) {
                    value = ((ConstantNode)((BinaryComparisonOperatorNode)transitiveSearchClauseAdded).getRightOperand()).getValue();
                }
                else {
                    value = null;
                }
            }
            final int tableNumber = columnReference3.getTableNumber();
            final int columnNumber = columnReference3.getColumnNumber();
            for (int size2 = list.size(), k = 0; k < size2; ++k) {
                final Predicate predicate2 = (Predicate)list.elementAt(k);
                if (!predicate2.transitiveSearchClauseAdded(transitiveSearchClauseAdded)) {
                    final BinaryRelationalOperatorNode binaryRelationalOperatorNode3 = (BinaryRelationalOperatorNode)predicate2.getAndNode().getLeftOperand();
                    final ColumnReference columnReference4 = (ColumnReference)binaryRelationalOperatorNode3.getLeftOperand();
                    final ColumnReference columnReference5 = (ColumnReference)binaryRelationalOperatorNode3.getRightOperand();
                    ColumnReference columnReference6;
                    if (columnReference4.getTableNumber() == tableNumber && columnReference4.getColumnNumber() == columnNumber) {
                        columnReference6 = columnReference5;
                    }
                    else {
                        if (columnReference5.getTableNumber() != tableNumber || columnReference5.getColumnNumber() != columnNumber) {
                            continue;
                        }
                        columnReference6 = columnReference4;
                    }
                    predicate2.setTransitiveSearchClauseAdded(transitiveSearchClauseAdded);
                    boolean b2 = false;
                    for (int size3 = list2.size(), l = 0; l < size3; ++l) {
                        DataValueDescriptor value2 = null;
                        final RelationalOperator relationalOperator3 = (RelationalOperator)((Predicate)list2.elementAt(l)).getAndNode().getLeftOperand();
                        ColumnReference columnReference7;
                        if (relationalOperator3 instanceof UnaryComparisonOperatorNode) {
                            columnReference7 = (ColumnReference)((UnaryComparisonOperatorNode)relationalOperator3).getOperand();
                        }
                        else {
                            columnReference7 = (ColumnReference)((BinaryComparisonOperatorNode)relationalOperator3).getLeftOperand();
                            if (((BinaryComparisonOperatorNode)relationalOperator3).getRightOperand() instanceof ConstantNode) {
                                value2 = ((ConstantNode)((BinaryComparisonOperatorNode)relationalOperator3).getRightOperand()).getValue();
                            }
                            else {
                                value2 = null;
                            }
                        }
                        if (columnReference7.getTableNumber() == columnReference6.getTableNumber() && columnReference7.getColumnNumber() == columnReference6.getColumnNumber() && ((value2 != null && value != null && value2.compare(value) == 0) || (value2 == null && value == null)) && relationalOperator3.getOperator() == transitiveSearchClauseAdded.getOperator() && relationalOperator3.getClass().getName().equals(transitiveSearchClauseAdded.getClass().getName())) {
                            b2 = true;
                            break;
                        }
                    }
                    if (!b2) {
                        final RelationalOperator transitiveSearchClause = transitiveSearchClauseAdded.getTransitiveSearchClause((ColumnReference)columnReference6.getClone());
                        if (transitiveSearchClause instanceof BinaryComparisonOperatorNode) {
                            ((BinaryComparisonOperatorNode)transitiveSearchClause).bindComparisonOperator();
                        }
                        else {
                            ((UnaryComparisonOperatorNode)transitiveSearchClause).bindComparisonOperator();
                        }
                        final AndNode andNode2 = (AndNode)this.getNodeFactory().getNode(39, transitiveSearchClause, this.getNodeFactory().getNode(38, Boolean.TRUE, this.getContextManager()), this.getContextManager());
                        andNode2.postBindFixup();
                        final JBitSet set = new JBitSet(n);
                        andNode2.categorize(set, false);
                        final Predicate predicate3 = (Predicate)this.getNodeFactory().getNode(78, andNode2, set, this.getContextManager());
                        this.addPredicate(predicate3);
                        list2.addElement(predicate3);
                    }
                }
            }
        }
        if (b) {
            return;
        }
        for (int n2 = this.size() - 1; n2 >= 0; --n2) {
            if (((Predicate)this.elementAt(n2)).transitiveSearchClauseAdded(relationalOperator)) {
                this.removeElementAt(n2);
            }
        }
    }
    
    void removeRedundantPredicates() {
        for (int i = this.size() - 1; i >= 0; --i) {
            final Predicate predicate = (Predicate)this.elementAt(i);
            final int equivalenceClass = predicate.getEquivalenceClass();
            if (equivalenceClass != -1) {
                for (int j = i - 1; j >= 0; --j) {
                    final Predicate predicate2 = (Predicate)this.elementAt(j);
                    if (predicate2.getEquivalenceClass() == equivalenceClass) {
                        if (predicate2.isStartKey()) {
                            predicate.markStartKey();
                        }
                        if (predicate2.isStopKey()) {
                            predicate.markStopKey();
                        }
                        if ((predicate2.isStartKey() || predicate2.isStopKey()) && predicate2.isQualifier() && !predicate.isQualifier()) {
                            predicate.markQualifier();
                            ++this.numberOfQualifiers;
                        }
                        if (predicate2.isQualifier()) {
                            --this.numberOfQualifiers;
                        }
                        this.removeElementAt(j);
                        --i;
                    }
                }
            }
        }
    }
    
    public void transferPredicates(final OptimizablePredicateList list, final JBitSet set, final Optimizable optimizable) throws StandardException {
        final PredicateList list2 = (PredicateList)list;
        for (int i = this.size() - 1; i >= 0; --i) {
            final Predicate predicate = (Predicate)this.elementAt(i);
            if (set.contains(predicate.getReferencedSet())) {
                if (predicate.isStartKey()) {
                    --this.numberOfStartPredicates;
                }
                if (predicate.isStopKey()) {
                    --this.numberOfStopPredicates;
                }
                if (predicate.isQualifier()) {
                    --this.numberOfQualifiers;
                }
                predicate.clearScanFlags();
                list2.addPredicate(predicate);
                this.removeElementAt(i);
            }
        }
        final AccessPath trulyTheBestAccessPath = optimizable.getTrulyTheBestAccessPath();
        list2.orderUsefulPredicates(optimizable, trulyTheBestAccessPath.getConglomerateDescriptor(), false, trulyTheBestAccessPath.getNonMatchingIndexScan(), trulyTheBestAccessPath.getCoveringIndexScan());
        list2.countScanFlags();
    }
    
    public void transferAllPredicates(final OptimizablePredicateList list) throws StandardException {
        final PredicateList list2 = (PredicateList)list;
        for (int size = this.size(), i = 0; i < size; ++i) {
            final Predicate predicate = (Predicate)this.elementAt(i);
            predicate.clearScanFlags();
            list2.addPredicate(predicate);
        }
        this.removeAllElements();
        this.numberOfStartPredicates = 0;
        this.numberOfStopPredicates = 0;
        this.numberOfQualifiers = 0;
    }
    
    public void copyPredicatesToOtherList(final OptimizablePredicateList list) throws StandardException {
        for (int i = 0; i < this.size(); ++i) {
            list.addOptPredicate(this.getOptPredicate(i));
        }
    }
    
    public boolean isRedundantPredicate(final int n) {
        final Predicate predicate = (Predicate)this.elementAt(n);
        if (predicate.getEquivalenceClass() == -1) {
            return false;
        }
        for (int i = 0; i < n; ++i) {
            if (((Predicate)this.elementAt(i)).getEquivalenceClass() == predicate.getEquivalenceClass()) {
                return true;
            }
        }
        return false;
    }
    
    public void setPredicatesAndProperties(final OptimizablePredicateList list) throws StandardException {
        final PredicateList list2 = (PredicateList)list;
        list2.removeAllElements();
        for (int i = 0; i < this.size(); ++i) {
            list2.addOptPredicate(this.getOptPredicate(i));
        }
        list2.numberOfStartPredicates = this.numberOfStartPredicates;
        list2.numberOfStopPredicates = this.numberOfStopPredicates;
        list2.numberOfQualifiers = this.numberOfQualifiers;
    }
    
    public int startOperator(final Optimizable optimizable) {
        int startOperator = -1;
        for (int i = this.size() - 1; i >= 0; --i) {
            final Predicate predicate = (Predicate)this.elementAt(i);
            if (predicate.isStartKey()) {
                startOperator = predicate.getStartOperator(optimizable);
                break;
            }
        }
        return startOperator;
    }
    
    public void generateStopKey(final ExpressionClassBuilderInterface expressionClassBuilderInterface, final MethodBuilder methodBuilder, final Optimizable optimizable) throws StandardException {
        final ExpressionClassBuilder expressionClassBuilder = (ExpressionClassBuilder)expressionClassBuilderInterface;
        if (this.numberOfStopPredicates != 0) {
            final MethodBuilder exprFun = expressionClassBuilder.newExprFun();
            final LocalField generateIndexableRow = this.generateIndexableRow(expressionClassBuilder, this.numberOfStopPredicates);
            int n = 0;
            for (int size = this.size(), i = 0; i < size; ++i) {
                final Predicate predicate = (Predicate)this.elementAt(i);
                if (predicate.isStopKey()) {
                    this.generateSetColumn(expressionClassBuilder, exprFun, n, predicate, optimizable, generateIndexableRow, false);
                    ++n;
                }
            }
            this.finishKey(expressionClassBuilder, methodBuilder, exprFun, generateIndexableRow);
            return;
        }
        methodBuilder.pushNull("org.apache.derby.iapi.services.loader.GeneratedMethod");
    }
    
    public int stopOperator(final Optimizable optimizable) {
        int stopOperator = -1;
        for (int i = this.size() - 1; i >= 0; --i) {
            final Predicate predicate = (Predicate)this.elementAt(i);
            if (predicate.isStopKey()) {
                stopOperator = predicate.getStopOperator(optimizable);
                break;
            }
        }
        return stopOperator;
    }
    
    private void generateSingleQualifierCode(final MethodBuilder methodBuilder, final Optimizable optimizable, final boolean b, final ExpressionClassBuilder expressionClassBuilder, final RelationalOperator relationalOperator, final LocalField localField, final int n, final int n2) throws StandardException {
        methodBuilder.getField(localField);
        methodBuilder.pushThis();
        methodBuilder.callMethod((short)182, expressionClassBuilder.getBaseClassName(), "getExecutionFactory", "org.apache.derby.iapi.sql.execute.ExecutionFactory", 0);
        if (b) {
            relationalOperator.generateAbsoluteColumnId(methodBuilder, optimizable);
        }
        else {
            relationalOperator.generateRelativeColumnId(methodBuilder, optimizable);
        }
        relationalOperator.generateOperator(methodBuilder, optimizable);
        relationalOperator.generateQualMethod(expressionClassBuilder, methodBuilder, optimizable);
        expressionClassBuilder.pushThisAsActivation(methodBuilder);
        relationalOperator.generateOrderedNulls(methodBuilder);
        relationalOperator.generateNegate(methodBuilder, optimizable);
        relationalOperator.generateNegate(methodBuilder, optimizable);
        methodBuilder.push(relationalOperator.getOrderableVariantType(optimizable));
        methodBuilder.callMethod((short)185, "org.apache.derby.iapi.sql.execute.ExecutionFactory", "getQualifier", "org.apache.derby.iapi.store.access.Qualifier", 8);
        methodBuilder.push(n);
        methodBuilder.push(n2);
        methodBuilder.callMethod((short)184, expressionClassBuilder.getBaseClassName(), "setQualifier", "void", 4);
    }
    
    protected void generateInListValues(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder) throws StandardException {
        for (int i = this.size() - 1; i >= 0; --i) {
            final Predicate predicate = (Predicate)this.elementAt(i);
            if (predicate.isInListProbePredicate()) {
                this.removeOptPredicate(predicate);
                final InListOperatorNode sourceInList = predicate.getSourceInList();
                methodBuilder.getField(sourceInList.generateListAsArray(expressionClassBuilder, methodBuilder));
                if (sourceInList.sortDescending()) {
                    methodBuilder.push(2);
                }
                else if (!sourceInList.isOrdered()) {
                    methodBuilder.push(1);
                }
                else {
                    methodBuilder.push(3);
                }
                return;
            }
        }
    }
    
    public void generateQualifiers(final ExpressionClassBuilderInterface expressionClassBuilderInterface, final MethodBuilder methodBuilder, final Optimizable optimizable, final boolean b) throws StandardException {
        final String s = "org.apache.derby.iapi.store.access.Qualifier[][]";
        if (this.numberOfQualifiers == 0) {
            methodBuilder.pushNull(s);
            return;
        }
        final ExpressionClassBuilder expressionClassBuilder = (ExpressionClassBuilder)expressionClassBuilderInterface;
        final MethodBuilder constructor = expressionClassBuilder.getConstructor();
        final MethodBuilder executeMethod = expressionClassBuilder.getExecuteMethod();
        final LocalField fieldDeclaration = expressionClassBuilder.newFieldDeclaration(2, s);
        executeMethod.getField(fieldDeclaration);
        executeMethod.callMethod((short)184, expressionClassBuilder.getBaseClassName(), "reinitializeQualifiers", "void", 1);
        int n = 0;
        for (int i = 0; i < this.numberOfQualifiers; ++i) {
            if (((Predicate)this.elementAt(i)).isOrList()) {
                ++n;
            }
        }
        constructor.pushNewArray("org.apache.derby.iapi.store.access.Qualifier[]", n + 1);
        constructor.setField(fieldDeclaration);
        constructor.getField(fieldDeclaration);
        constructor.push(0);
        constructor.push(this.numberOfQualifiers - n);
        constructor.callMethod((short)184, expressionClassBuilder.getBaseClassName(), "allocateQualArray", "void", 3);
        this.orderQualifiers();
        int n2 = 0;
        final int size = this.size();
        boolean b2 = false;
        for (int j = 0; j < size; ++j) {
            final Predicate predicate = (Predicate)this.elementAt(j);
            if (predicate.isQualifier()) {
                if (predicate.isOrList()) {
                    b2 = true;
                    break;
                }
                this.generateSingleQualifierCode(constructor, optimizable, b, expressionClassBuilder, predicate.getRelop(), fieldDeclaration, 0, n2);
                ++n2;
            }
        }
        if (b2) {
            for (int n3 = 1, k = n2; k < size; ++k, ++n3) {
                final Predicate predicate2 = (Predicate)this.elementAt(k);
                final ArrayList list = new ArrayList<ValueNode>();
                OrNode orNode;
                for (ValueNode valueNode = predicate2.getAndNode().getLeftOperand(); valueNode instanceof OrNode; valueNode = orNode.getRightOperand()) {
                    orNode = (OrNode)valueNode;
                    if (orNode.getLeftOperand() instanceof RelationalOperator) {
                        list.add(orNode.getLeftOperand());
                    }
                }
                constructor.getField(fieldDeclaration);
                constructor.push(n3);
                constructor.push(list.size());
                constructor.callMethod((short)184, expressionClassBuilder.getBaseClassName(), "allocateQualArray", "void", 3);
                for (int l = 0; l < list.size(); ++l) {
                    this.generateSingleQualifierCode(constructor, optimizable, b, expressionClassBuilder, (RelationalOperator)list.get(l), fieldDeclaration, n3, l);
                }
                ++n2;
            }
        }
        methodBuilder.getField(fieldDeclaration);
    }
    
    private void orderQualifiers() {
        final PredicateList[] array = new PredicateList[5];
        for (int i = array.length - 1; i >= 0; --i) {
            array[i] = new PredicateList();
        }
        for (int size = this.size(), j = 0; j < size; ++j) {
            final Predicate predicate = (Predicate)this.elementAt(j);
            if (!predicate.isQualifier()) {
                array[3].addElement(predicate);
            }
            else {
                final AndNode andNode = predicate.getAndNode();
                if (!(andNode.getLeftOperand() instanceof OrNode)) {
                    switch (((RelationalOperator)andNode.getLeftOperand()).getOperator()) {
                        case 1:
                        case 7: {
                            array[0].addElement(predicate);
                            break;
                        }
                        case 2:
                        case 8: {
                            array[2].addElement(predicate);
                            break;
                        }
                        default: {
                            array[1].addElement(predicate);
                            break;
                        }
                    }
                }
                else {
                    array[4].addElement(predicate);
                }
            }
        }
        int n = 0;
        for (int k = 0; k < 5; ++k) {
            for (int l = 0; l < array[k].size(); ++l) {
                this.setElementAt(array[k].elementAt(l), n++);
            }
        }
    }
    
    public void generateStartKey(final ExpressionClassBuilderInterface expressionClassBuilderInterface, final MethodBuilder methodBuilder, final Optimizable optimizable) throws StandardException {
        final ExpressionClassBuilder expressionClassBuilder = (ExpressionClassBuilder)expressionClassBuilderInterface;
        if (this.numberOfStartPredicates != 0) {
            final MethodBuilder exprFun = expressionClassBuilder.newExprFun();
            final LocalField generateIndexableRow = this.generateIndexableRow(expressionClassBuilder, this.numberOfStartPredicates);
            int n = 0;
            for (int size = this.size(), i = 0; i < size; ++i) {
                final Predicate predicate = (Predicate)this.elementAt(i);
                if (predicate.isStartKey()) {
                    this.generateSetColumn(expressionClassBuilder, exprFun, n, predicate, optimizable, generateIndexableRow, true);
                    ++n;
                }
            }
            this.finishKey(expressionClassBuilder, methodBuilder, exprFun, generateIndexableRow);
            return;
        }
        methodBuilder.pushNull("org.apache.derby.iapi.services.loader.GeneratedMethod");
    }
    
    public boolean sameStartStopPosition() throws StandardException {
        if (this.numberOfStartPredicates != this.numberOfStopPredicates) {
            return false;
        }
        for (int size = this.size(), i = 0; i < size; ++i) {
            final Predicate predicate = (Predicate)this.elementAt(i);
            if ((predicate.isStartKey() && !predicate.isStopKey()) || (predicate.isStopKey() && !predicate.isStartKey())) {
                return false;
            }
            if (predicate.getAndNode().getLeftOperand() instanceof InListOperatorNode) {
                return false;
            }
        }
        return true;
    }
    
    private LocalField generateIndexableRow(final ExpressionClassBuilder expressionClassBuilder, final int n) {
        final MethodBuilder constructor = expressionClassBuilder.getConstructor();
        expressionClassBuilder.pushGetExecutionFactoryExpression(constructor);
        constructor.push(n);
        constructor.callMethod((short)185, "org.apache.derby.iapi.sql.execute.ExecutionFactory", "getIndexableRow", "org.apache.derby.iapi.sql.execute.ExecIndexRow", 1);
        final LocalField fieldDeclaration = expressionClassBuilder.newFieldDeclaration(2, "org.apache.derby.iapi.sql.execute.ExecIndexRow");
        constructor.setField(fieldDeclaration);
        return fieldDeclaration;
    }
    
    private void generateSetColumn(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder, final int n, final Predicate predicate, final Optimizable optimizable, final LocalField localField, final boolean b) throws StandardException {
        boolean b2 = false;
        MethodBuilder constructor;
        if (predicate.compareWithKnownConstant(optimizable, false)) {
            b2 = true;
            constructor = expressionClassBuilder.getConstructor();
        }
        else {
            constructor = methodBuilder;
        }
        final int[] baseColumnPositions = optimizable.getTrulyTheBestAccessPath().getConglomerateDescriptor().getIndexDescriptor().baseColumnPositions();
        final boolean[] ascending = optimizable.getTrulyTheBestAccessPath().getConglomerateDescriptor().getIndexDescriptor().isAscending();
        final boolean b3 = predicate.getAndNode().getLeftOperand() instanceof InListOperatorNode;
        constructor.getField(localField);
        constructor.push(n + 1);
        if (b3) {
            predicate.getSourceInList().generateStartStopKey(ascending[n], b, expressionClassBuilder, constructor);
        }
        else {
            predicate.generateExpressionOperand(optimizable, baseColumnPositions[n], expressionClassBuilder, constructor);
        }
        constructor.upCast("org.apache.derby.iapi.types.DataValueDescriptor");
        constructor.callMethod((short)185, "org.apache.derby.iapi.sql.Row", "setColumn", "void", 2);
        if (!b3) {
            final RelationalOperator relop = predicate.getRelop();
            int orderedNulls = relop.orderedNulls() ? 1 : 0;
            if (orderedNulls == 0 && !relop.getColumnOperand(optimizable).getTypeServices().isNullable()) {
                if (b2) {
                    orderedNulls = 1;
                }
                else {
                    final ValueNode expressionOperand = relop.getExpressionOperand(optimizable.getTableNumber(), baseColumnPositions[n], (FromTable)optimizable);
                    if (expressionOperand instanceof ColumnReference) {
                        orderedNulls = (((ColumnReference)expressionOperand).getTypeServices().isNullable() ? 0 : 1);
                    }
                }
            }
            if (orderedNulls != 0) {
                constructor.getField(localField);
                constructor.push(n);
                constructor.callMethod((short)185, "org.apache.derby.iapi.sql.execute.ExecIndexRow", "orderedNulls", "void", 1);
            }
        }
    }
    
    private void finishKey(final ExpressionClassBuilder expressionClassBuilder, final MethodBuilder methodBuilder, final MethodBuilder methodBuilder2, final LocalField localField) {
        methodBuilder2.getField(localField);
        methodBuilder2.methodReturn();
        methodBuilder2.complete();
        expressionClassBuilder.pushMethodReference(methodBuilder, methodBuilder2);
    }
    
    boolean constantColumn(final ColumnReference columnReference) {
        boolean b = false;
        for (int size = this.size(), i = 0; i < size; ++i) {
            final Predicate predicate = (Predicate)this.elementAt(i);
            final RelationalOperator relop = predicate.getRelop();
            if (predicate.isRelationalOpPredicate()) {
                if (relop.getOperator() == 1) {
                    final ValueNode operand = relop.getOperand(columnReference, predicate.getReferencedSet().size(), true);
                    if (operand != null && operand.isConstantExpression()) {
                        b = true;
                        break;
                    }
                }
                else if (relop.getOperator() == 7 && relop.getOperand(columnReference, predicate.getReferencedSet().size(), false) != null) {
                    b = true;
                }
            }
        }
        return b;
    }
    
    public void adjustForSortElimination(final RequiredRowOrdering requiredRowOrdering) throws StandardException {
        if (requiredRowOrdering == null) {
            return;
        }
        final int size = this.size();
        final OrderByList list = (OrderByList)requiredRowOrdering;
        for (int i = 0; i < size; ++i) {
            final Predicate predicate = (Predicate)this.elementAt(i);
            if (predicate.isInListProbePredicate()) {
                if (list.requiresDescending((ColumnReference)((BinaryRelationalOperatorNode)predicate.getRelop()).getLeftOperand(), predicate.getReferencedSet().size())) {
                    predicate.getSourceInList(true).markSortDescending();
                }
            }
        }
    }
    
    public double selectivity(final Optimizable optimizable) throws StandardException {
        final TableDescriptor tableDescriptor = optimizable.getTableDescriptor();
        final ConglomerateDescriptor[] conglomerateDescriptors = tableDescriptor.getConglomerateDescriptors();
        final int size = this.size();
        final int length = conglomerateDescriptors.length;
        if (length == 1) {
            return -1.0;
        }
        if (size == 0) {
            return -1.0;
        }
        boolean b = true;
        final PredicateList list = new PredicateList();
        for (int i = 0; i < size; ++i) {
            if (!this.isRedundantPredicate(i)) {
                list.addOptPredicate((OptimizablePredicate)this.elementAt(i));
            }
        }
        final int size2 = list.size();
        final PredicateWrapperList[] array = new PredicateWrapperList[length];
        for (int j = 0; j < length; ++j) {
            final ConglomerateDescriptor conglomerateDescriptor = conglomerateDescriptors[j];
            if (conglomerateDescriptor.isIndex()) {
                if (tableDescriptor.statisticsExist(conglomerateDescriptor)) {
                    final int[] baseColumnPositions = conglomerateDescriptor.getIndexDescriptor().baseColumnPositions();
                    for (int k = 0; k < size2; ++k) {
                        final Predicate predicate = (Predicate)list.elementAt(k);
                        final int hasEqualOnColumnList = predicate.hasEqualOnColumnList(baseColumnPositions, optimizable);
                        if (hasEqualOnColumnList >= 0) {
                            b = false;
                            if (array[j] == null) {
                                array[j] = new PredicateWrapperList(size2);
                            }
                            array[j].insert(new PredicateWrapper(hasEqualOnColumnList, predicate, k));
                        }
                    }
                }
            }
        }
        if (b) {
            return -1.0;
        }
        for (int l = 0; l < length; ++l) {
            if (array[l] != null) {
                array[l].retainLeadingContiguous();
            }
        }
        this.calculateWeight(array, size2);
        double n = 1.0;
        final ArrayList<Predicate> list2 = new ArrayList<Predicate>();
        do {
            list2.clear();
            final int chooseLongestMatch = this.chooseLongestMatch(array, list2, size2);
            if (chooseLongestMatch == -1) {
                break;
            }
            n *= tableDescriptor.selectivityForConglomerate(conglomerateDescriptors[chooseLongestMatch], list2.size());
            for (int index = 0; index < list2.size(); ++index) {
                list.removeOptPredicate(list2.get(index));
            }
        } while (list.size() != 0);
        if (list.size() != 0) {
            n *= list.selectivityNoStatistics(optimizable);
        }
        return n;
    }
    
    private void calculateWeight(final PredicateWrapperList[] array, final int n) {
        final int[] array2 = new int[n];
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != null) {
                for (int j = 0; j < array[i].size(); ++j) {
                    final int[] array3 = array2;
                    final int predicateID = array[i].elementAt(j).getPredicateID();
                    array3[predicateID] += n - j;
                }
            }
        }
        for (int k = 0; k < array.length; ++k) {
            int weight = 0;
            if (array[k] != null) {
                for (int l = 0; l < array[k].size(); ++l) {
                    weight += array2[array[k].elementAt(l).getPredicateID()];
                }
                array[k].setWeight(weight);
            }
        }
    }
    
    private int chooseLongestMatch(final PredicateWrapperList[] array, final List list, final int n) {
        int n2 = 0;
        int n3 = 0;
        int n4 = -1;
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != null) {
                if (array[i].uniqueSize() != 0) {
                    if (array[i].uniqueSize() > n2) {
                        n2 = array[i].uniqueSize();
                        n4 = i;
                        n3 = array[i].getWeight();
                    }
                    if (array[i].uniqueSize() == n2) {
                        if (array[i].getWeight() <= n3) {
                            n4 = i;
                            n2 = array[i].uniqueSize();
                            n3 = array[i].getWeight();
                        }
                    }
                }
            }
        }
        if (n4 == -1) {
            return -1;
        }
        final List access$000 = array[n4].createLeadingUnique();
        for (int j = 0; j < access$000.size(); ++j) {
            final Predicate predicate = access$000.get(j).getPredicate();
            list.add(predicate);
            for (int k = 0; k < array.length; ++k) {
                if (array[k] != null) {
                    array[k].removeElement(predicate);
                }
            }
        }
        for (int l = 0; l < array.length; ++l) {
            if (array[l] != null) {
                array[l].retainLeadingContiguous();
            }
        }
        this.calculateWeight(array, n);
        return n4;
    }
    
    private double selectivityNoStatistics(final Optimizable optimizable) throws StandardException {
        double n = 1.0;
        for (int i = 0; i < this.size(); ++i) {
            n *= ((OptimizablePredicate)this.elementAt(i)).selectivity(optimizable);
        }
        return n;
    }
    
    private class PredicateWrapperList
    {
        private final ArrayList pwList;
        int numPreds;
        int numDuplicates;
        int weight;
        
        PredicateWrapperList(final int initialCapacity) {
            this.pwList = new ArrayList(initialCapacity);
        }
        
        void removeElement(final Predicate predicate) {
            for (int i = this.numPreds - 1; i >= 0; --i) {
                if (this.elementAt(i).getPredicate() == predicate) {
                    this.removeElementAt(i);
                }
            }
        }
        
        void removeElementAt(final int index) {
            if (index < this.numPreds - 1 && this.elementAt(index + 1).getIndexPosition() == index) {
                --this.numDuplicates;
            }
            this.pwList.remove(index);
            --this.numPreds;
        }
        
        PredicateWrapper elementAt(final int index) {
            return this.pwList.get(index);
        }
        
        void insert(final PredicateWrapper element) {
            int i;
            for (i = 0; i < this.pwList.size(); ++i) {
                if (element.getIndexPosition() == this.elementAt(i).getIndexPosition()) {
                    ++this.numDuplicates;
                }
                if (element.before(this.elementAt(i))) {
                    break;
                }
            }
            ++this.numPreds;
            this.pwList.add(i, element);
        }
        
        int size() {
            return this.numPreds;
        }
        
        int uniqueSize() {
            if (this.numPreds > 0) {
                return this.numPreds - this.numDuplicates;
            }
            return 0;
        }
        
        void retainLeadingContiguous() {
            if (this.pwList.isEmpty()) {
                return;
            }
            if (this.elementAt(0).getIndexPosition() != 0) {
                this.pwList.clear();
                final int n = 0;
                this.numDuplicates = n;
                this.numPreds = n;
                return;
            }
            int n2;
            for (n2 = 0; n2 < this.numPreds - 1 && this.elementAt(n2).contiguous(this.elementAt(n2 + 1)); ++n2) {}
            for (int i = this.numPreds - 1; i > n2; --i) {
                if (this.elementAt(i).getIndexPosition() == this.elementAt(i - 1).getIndexPosition()) {
                    --this.numDuplicates;
                }
                this.pwList.remove(i);
            }
            this.numPreds = n2 + 1;
        }
        
        private List createLeadingUnique() {
            if (this.numPreds == 0) {
                return null;
            }
            int n = this.elementAt(0).getIndexPosition();
            if (n != 0) {
                return null;
            }
            final ArrayList<PredicateWrapper> list = new ArrayList<PredicateWrapper>();
            list.add(this.elementAt(0));
            for (int i = 1; i < this.numPreds; ++i) {
                if (this.elementAt(i).getIndexPosition() != n) {
                    n = this.elementAt(i).getIndexPosition();
                    list.add(this.elementAt(i));
                }
            }
            return list;
        }
        
        void setWeight(final int weight) {
            this.weight = weight;
        }
        
        int getWeight() {
            return this.weight;
        }
    }
    
    private class PredicateWrapper
    {
        int indexPosition;
        Predicate pred;
        int predicateID;
        
        PredicateWrapper(final int indexPosition, final Predicate pred, final int predicateID) {
            this.indexPosition = indexPosition;
            this.pred = pred;
            this.predicateID = predicateID;
        }
        
        int getIndexPosition() {
            return this.indexPosition;
        }
        
        Predicate getPredicate() {
            return this.pred;
        }
        
        int getPredicateID() {
            return this.predicateID;
        }
        
        boolean before(final PredicateWrapper predicateWrapper) {
            return this.indexPosition < predicateWrapper.getIndexPosition();
        }
        
        boolean contiguous(final PredicateWrapper predicateWrapper) {
            final int indexPosition = predicateWrapper.getIndexPosition();
            return this.indexPosition == indexPosition || this.indexPosition - indexPosition == 1 || this.indexPosition - indexPosition == -1;
        }
    }
}
