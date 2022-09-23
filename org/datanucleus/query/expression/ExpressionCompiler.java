// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.expression;

import java.util.Collections;
import org.datanucleus.query.symbol.Symbol;
import org.datanucleus.util.NucleusLogger;
import java.lang.reflect.Array;
import org.datanucleus.query.node.ParameterNode;
import java.util.List;
import java.util.ArrayList;
import org.datanucleus.store.query.QueryCompilerSyntaxException;
import java.util.Iterator;
import org.datanucleus.query.node.NodeType;
import org.datanucleus.query.node.Node;
import java.util.Map;
import org.datanucleus.query.symbol.SymbolTable;

public class ExpressionCompiler
{
    SymbolTable symtbl;
    Map<String, String> aliasByPrefix;
    
    public ExpressionCompiler() {
        this.aliasByPrefix = null;
    }
    
    public void setMethodAliases(final Map<String, String> aliasByPrefix) {
        this.aliasByPrefix = aliasByPrefix;
    }
    
    public void setSymbolTable(final SymbolTable symtbl) {
        this.symtbl = symtbl;
    }
    
    public Expression compileOrderExpression(final Node node) {
        if (this.isOperator(node, "order")) {
            final Node nameNode = node.getFirstChild();
            if (node.getChildNodes().size() > 1) {
                final String node1Value = (String)node.getNextChild().getNodeValue();
                final String node2Value = node.hasNextChild() ? ((String)node.getNextChild().getNodeValue()) : null;
                String ordering = null;
                String nullOrdering = null;
                if (node1Value.equalsIgnoreCase("ascending") || node1Value.equalsIgnoreCase("descending")) {
                    ordering = node1Value;
                    if (node2Value != null) {
                        nullOrdering = node2Value;
                    }
                }
                else {
                    nullOrdering = node1Value;
                }
                return new OrderExpression(this.compileExpression(nameNode), ordering, nullOrdering);
            }
            if (node.getChildNodes().size() == 1) {
                return new OrderExpression(this.compileExpression(nameNode));
            }
        }
        return this.compileExpression(node.getFirstChild());
    }
    
    public Expression compileFromExpression(final Node node, final boolean classIsExpression) {
        if (node.getNodeType() == NodeType.CLASS) {
            final Node aliasNode = node.getFirstChild();
            final ClassExpression clsExpr = new ClassExpression((String)aliasNode.getNodeValue());
            if (classIsExpression) {
                clsExpr.setCandidateExpression((String)node.getNodeValue());
            }
            JoinExpression currentJoinExpr = null;
            for (final Node childNode : node.getChildNodes()) {
                if (childNode.getNodeType() == NodeType.OPERATOR) {
                    final String joinType = (String)childNode.getNodeValue();
                    JoinExpression.JoinType joinTypeId = JoinExpression.JoinType.JOIN_INNER;
                    if (joinType.equals("JOIN_INNER_FETCH")) {
                        joinTypeId = JoinExpression.JoinType.JOIN_INNER_FETCH;
                    }
                    else if (joinType.equals("JOIN_OUTER_FETCH")) {
                        joinTypeId = JoinExpression.JoinType.JOIN_LEFT_OUTER_FETCH;
                    }
                    else if (joinType.equals("JOIN_OUTER")) {
                        joinTypeId = JoinExpression.JoinType.JOIN_LEFT_OUTER;
                    }
                    final Node joinedNode = childNode.getFirstChild();
                    final Node joinedAliasNode = childNode.getNextChild();
                    final PrimaryExpression primExpr = (PrimaryExpression)this.compilePrimaryExpression(joinedNode);
                    DyadicExpression onExpr = null;
                    if (childNode.hasNextChild()) {
                        final Node onNode = childNode.getNextChild();
                        onExpr = (DyadicExpression)this.compileExpression(onNode);
                    }
                    final JoinExpression joinExpr = new JoinExpression(primExpr, (String)joinedAliasNode.getNodeValue(), joinTypeId);
                    if (currentJoinExpr != null) {
                        currentJoinExpr.setJoinExpression(joinExpr);
                    }
                    else {
                        clsExpr.setJoinExpression(joinExpr);
                    }
                    if (onExpr != null) {
                        joinExpr.setOnExpression(onExpr);
                    }
                    currentJoinExpr = joinExpr;
                }
            }
            return clsExpr;
        }
        return null;
    }
    
    public Expression compileExpression(final Node node) {
        return this.compileOrAndExpression(node);
    }
    
    private Expression compileOrAndExpression(final Node node) {
        if (this.isOperator(node, "||")) {
            final Expression left = this.compileExpression(node.getFirstChild());
            final Expression right = this.compileExpression(node.getNextChild());
            return new DyadicExpression(left, Expression.OP_OR, right);
        }
        if (this.isOperator(node, "&&")) {
            final Expression left = this.compileExpression(node.getFirstChild());
            final Expression right = this.compileExpression(node.getNextChild());
            return new DyadicExpression(left, Expression.OP_AND, right);
        }
        if (this.isOperator(node, "|")) {
            final Expression left = this.compileExpression(node.getFirstChild());
            final Expression right = this.compileExpression(node.getNextChild());
            return new DyadicExpression(left, Expression.OP_OR, right);
        }
        if (this.isOperator(node, "^")) {
            final Expression left = this.compileExpression(node.getFirstChild());
            final Expression right = this.compileExpression(node.getNextChild());
            return new DyadicExpression(left, Expression.OP_OR, right);
        }
        if (this.isOperator(node, "&")) {
            final Expression left = this.compileExpression(node.getFirstChild());
            final Expression right = this.compileExpression(node.getNextChild());
            return new DyadicExpression(left, Expression.OP_AND, right);
        }
        return this.compileRelationalExpression(node);
    }
    
    private Expression compileRelationalExpression(final Node node) {
        if (this.isOperator(node, "==")) {
            final Expression left = this.compileExpression(node.getFirstChild());
            final Expression right = this.compileExpression(node.getNextChild());
            return new DyadicExpression(left, Expression.OP_EQ, right);
        }
        if (this.isOperator(node, "!=")) {
            final Expression left = this.compileExpression(node.getFirstChild());
            final Expression right = this.compileExpression(node.getNextChild());
            return new DyadicExpression(left, Expression.OP_NOTEQ, right);
        }
        if (this.isOperator(node, "LIKE")) {
            final Expression left = this.compileExpression(node.getFirstChild());
            final Expression right = this.compileExpression(node.getNextChild());
            return new DyadicExpression(left, Expression.OP_LIKE, right);
        }
        if (this.isOperator(node, "<=")) {
            final Expression left = this.compileExpression(node.getFirstChild());
            final Expression right = this.compileExpression(node.getNextChild());
            return new DyadicExpression(left, Expression.OP_LTEQ, right);
        }
        if (this.isOperator(node, ">=")) {
            final Expression left = this.compileExpression(node.getFirstChild());
            final Expression right = this.compileExpression(node.getNextChild());
            return new DyadicExpression(left, Expression.OP_GTEQ, right);
        }
        if (this.isOperator(node, "<")) {
            final Expression left = this.compileExpression(node.getFirstChild());
            final Expression right = this.compileExpression(node.getNextChild());
            return new DyadicExpression(left, Expression.OP_LT, right);
        }
        if (this.isOperator(node, ">")) {
            final Expression left = this.compileExpression(node.getFirstChild());
            final Expression right = this.compileExpression(node.getNextChild());
            return new DyadicExpression(left, Expression.OP_GT, right);
        }
        if (this.isOperator(node, "instanceof")) {
            final Expression left = this.compileExpression(node.getFirstChild());
            final Expression right = this.compileExpression(node.getNextChild());
            return new DyadicExpression(left, Expression.OP_IS, right);
        }
        if (this.isOperator(node, "IN")) {
            final Expression left = this.compileExpression(node.getFirstChild());
            final Expression right = this.compileExpression(node.getNextChild());
            return new DyadicExpression(left, Expression.OP_IN, right);
        }
        if (this.isOperator(node, "NOT IN")) {
            final Expression left = this.compileExpression(node.getFirstChild());
            final Expression right = this.compileExpression(node.getNextChild());
            return new DyadicExpression(left, Expression.OP_NOTIN, right);
        }
        return this.compileAdditiveMultiplicativeExpression(node);
    }
    
    private Expression compileAdditiveMultiplicativeExpression(final Node node) {
        if (this.isOperator(node, "+")) {
            final Expression left = this.compileExpression(node.getFirstChild());
            final Expression right = this.compileExpression(node.getNextChild());
            return new DyadicExpression(left, Expression.OP_ADD, right);
        }
        if (this.isOperator(node, "-") && node.getChildNodes().size() > 1) {
            final Expression left = this.compileExpression(node.getFirstChild());
            final Expression right = this.compileExpression(node.getNextChild());
            return new DyadicExpression(left, Expression.OP_SUB, right);
        }
        if (this.isOperator(node, "*")) {
            final Expression left = this.compileExpression(node.getFirstChild());
            final Expression right = this.compileExpression(node.getNextChild());
            return new DyadicExpression(left, Expression.OP_MUL, right);
        }
        if (this.isOperator(node, "/")) {
            final Expression left = this.compileExpression(node.getFirstChild());
            final Expression right = this.compileExpression(node.getNextChild());
            return new DyadicExpression(left, Expression.OP_DIV, right);
        }
        if (this.isOperator(node, "%")) {
            final Expression left = this.compileExpression(node.getFirstChild());
            final Expression right = this.compileExpression(node.getNextChild());
            return new DyadicExpression(left, Expression.OP_MOD, right);
        }
        return this.compileUnaryExpression(node);
    }
    
    private Expression compileUnaryExpression(final Node node) {
        if (this.isOperator(node, "-") && node.getChildNodes().size() == 1) {
            final Expression left = this.compileExpression(node.getFirstChild());
            if (left instanceof Literal) {
                ((Literal)left).negate();
                return left;
            }
            return new DyadicExpression(Expression.OP_NEG, left);
        }
        else {
            if (this.isOperator(node, "~")) {
                final Expression left = this.compileExpression(node.getFirstChild());
                return new DyadicExpression(Expression.OP_COM, left);
            }
            if (this.isOperator(node, "!")) {
                final Expression left = this.compileExpression(node.getFirstChild());
                if (left instanceof DyadicExpression && left.getOperator() == Expression.OP_IS) {
                    final DyadicExpression leftExpr = (DyadicExpression)left;
                    return new DyadicExpression(leftExpr.getLeft(), Expression.OP_ISNOT, leftExpr.getRight());
                }
                return new DyadicExpression(Expression.OP_NOT, left);
            }
            else {
                if (this.isOperator(node, "DISTINCT")) {
                    final Expression left = this.compileExpression(node.getFirstChild());
                    return new DyadicExpression(Expression.OP_DISTINCT, left);
                }
                return this.compilePrimaryExpression(node);
            }
        }
    }
    
    private Expression compilePrimaryExpression(final Node node) {
        if (node.getNodeType() == NodeType.PRIMARY) {
            final Node currentNode = node.getFirstChild();
            final Node invokeNode = node.getNextChild();
            if (invokeNode.getNodeType() != NodeType.INVOKE) {
                throw new QueryCompilerSyntaxException("Dont support compilation of " + node);
            }
            final Expression currentExpr = this.compileExpression(currentNode);
            final String methodName = (String)invokeNode.getNodeValue();
            final List parameterExprs = this.getExpressionsForPropertiesOfNode(invokeNode);
            final Expression invokeExpr = new InvokeExpression(currentExpr, methodName, parameterExprs);
            return invokeExpr;
        }
        else {
            if (node.getNodeType() == NodeType.IDENTIFIER) {
                Node currentNode = node;
                List tupple = new ArrayList();
                Expression currentExpr = null;
                while (currentNode != null) {
                    tupple.add(currentNode.getNodeValue());
                    if (currentNode.getNodeType() == NodeType.INVOKE) {
                        if (currentExpr == null && tupple.size() > 1) {
                            final String first = tupple.get(0);
                            final Symbol firstSym = this.symtbl.getSymbol(first);
                            if (firstSym != null) {
                                if (firstSym.getType() == 1) {
                                    currentExpr = new ParameterExpression(first, -1);
                                    if (tupple.size() > 2) {
                                        currentExpr = new PrimaryExpression(currentExpr, tupple.subList(1, tupple.size() - 1));
                                    }
                                }
                                else if (firstSym.getType() == 2) {
                                    currentExpr = new VariableExpression(first);
                                    if (tupple.size() > 2) {
                                        currentExpr = new PrimaryExpression(currentExpr, tupple.subList(1, tupple.size() - 1));
                                    }
                                }
                            }
                            if (currentExpr == null) {
                                currentExpr = new PrimaryExpression(tupple.subList(0, tupple.size() - 1));
                            }
                        }
                        String methodName = tupple.get(tupple.size() - 1);
                        if (currentExpr instanceof PrimaryExpression) {
                            final String id = ((PrimaryExpression)currentExpr).getId();
                            if (this.aliasByPrefix != null && this.aliasByPrefix.containsKey(id)) {
                                final String alias = this.aliasByPrefix.get(id);
                                methodName = alias + "." + methodName;
                                currentExpr = null;
                            }
                        }
                        final List parameterExprs = this.getExpressionsForPropertiesOfNode(currentNode);
                        currentExpr = new InvokeExpression(currentExpr, methodName, parameterExprs);
                        currentNode = currentNode.getFirstChild();
                        tupple = new ArrayList();
                    }
                    else if (currentNode.getNodeType() == NodeType.CAST) {
                        if (currentExpr == null && tupple.size() > 1) {
                            currentExpr = new PrimaryExpression(tupple.subList(0, tupple.size() - 1));
                            final PrimaryExpression primExpr = (PrimaryExpression)currentExpr;
                            if (primExpr.tuples.size() == 1) {
                                final Symbol sym = this.symtbl.getSymbol(primExpr.getId());
                                if (sym != null) {
                                    if (sym.getType() == 1) {
                                        currentExpr = new ParameterExpression(primExpr.getId(), -1);
                                    }
                                    else if (sym.getType() == 2) {
                                        currentExpr = new VariableExpression(primExpr.getId());
                                    }
                                }
                            }
                        }
                        final String className = tupple.get(tupple.size() - 1);
                        currentExpr = new DyadicExpression(currentExpr, Expression.OP_CAST, new Literal(className));
                        currentNode = currentNode.getFirstChild();
                        tupple = new ArrayList();
                    }
                    else {
                        currentNode = currentNode.getFirstChild();
                    }
                }
                if (currentExpr != null && tupple.size() > 0) {
                    currentExpr = new PrimaryExpression(currentExpr, tupple);
                }
                if (currentExpr == null) {
                    final String first = tupple.get(0);
                    final Symbol firstSym = this.symtbl.getSymbol(first);
                    if (firstSym != null) {
                        if (firstSym.getType() == 1) {
                            final ParameterExpression paramExpr = new ParameterExpression(first, -1);
                            if (tupple.size() > 1) {
                                currentExpr = new PrimaryExpression(paramExpr, tupple.subList(1, tupple.size()));
                            }
                            else {
                                currentExpr = paramExpr;
                            }
                        }
                        else if (firstSym.getType() == 2) {
                            final VariableExpression varExpr = new VariableExpression(first);
                            if (tupple.size() > 1) {
                                currentExpr = new PrimaryExpression(varExpr, tupple.subList(1, tupple.size()));
                            }
                            else {
                                currentExpr = varExpr;
                            }
                        }
                        else {
                            currentExpr = new PrimaryExpression(tupple);
                        }
                    }
                    else {
                        currentExpr = new PrimaryExpression(tupple);
                    }
                }
                return currentExpr;
            }
            if (node.getNodeType() == NodeType.PARAMETER) {
                final Object val = node.getNodeValue();
                Expression currentExpr2 = null;
                if (val instanceof Integer) {
                    currentExpr2 = new ParameterExpression("" + node.getNodeValue(), ((ParameterNode)node).getPosition());
                }
                else {
                    currentExpr2 = new ParameterExpression((String)node.getNodeValue(), ((ParameterNode)node).getPosition());
                }
                for (Node childNode = node.getFirstChild(); childNode != null; childNode = childNode.getFirstChild()) {
                    if (childNode.getNodeType() == NodeType.INVOKE) {
                        final String methodName = (String)childNode.getNodeValue();
                        final List parameterExprs = this.getExpressionsForPropertiesOfNode(childNode);
                        currentExpr2 = new InvokeExpression(currentExpr2, methodName, parameterExprs);
                    }
                    else {
                        if (childNode.getNodeType() != NodeType.IDENTIFIER) {
                            throw new QueryCompilerSyntaxException("Dont support compilation of " + node);
                        }
                        final String identifier = childNode.getNodeId();
                        final List tuples = new ArrayList();
                        tuples.add(identifier);
                        boolean moreIdentifierNodes = true;
                        while (moreIdentifierNodes) {
                            final Node currentNode2 = childNode;
                            childNode = childNode.getFirstChild();
                            if (childNode == null || childNode.getNodeType() != NodeType.IDENTIFIER) {
                                moreIdentifierNodes = false;
                                childNode = currentNode2;
                            }
                            else {
                                tuples.add(childNode.getNodeId());
                            }
                        }
                        currentExpr2 = new PrimaryExpression(currentExpr2, tuples);
                    }
                }
                return currentExpr2;
            }
            if (node.getNodeType() == NodeType.INVOKE) {
                Node currentNode = node;
                List tupple = new ArrayList();
                Expression currentExpr = null;
                while (currentNode != null) {
                    tupple.add(currentNode.getNodeValue());
                    if (currentNode.getNodeType() == NodeType.INVOKE) {
                        final String methodName = tupple.get(tupple.size() - 1);
                        final List parameterExprs = this.getExpressionsForPropertiesOfNode(currentNode);
                        currentExpr = new InvokeExpression(currentExpr, methodName, parameterExprs);
                        currentNode = currentNode.getFirstChild();
                        if (currentNode == null) {
                            continue;
                        }
                        tupple = new ArrayList();
                        tupple.add(currentExpr);
                    }
                    else {
                        currentNode = currentNode.getFirstChild();
                    }
                }
                return currentExpr;
            }
            if (node.getNodeType() == NodeType.CREATOR) {
                Node currentNode = node.getFirstChild();
                final List tupple = new ArrayList();
                boolean method = false;
                while (currentNode != null) {
                    tupple.add(currentNode.getNodeValue());
                    if (currentNode.getNodeType() == NodeType.INVOKE) {
                        method = true;
                        break;
                    }
                    currentNode = currentNode.getFirstChild();
                }
                List parameterExprs2 = null;
                if (method) {
                    parameterExprs2 = this.getExpressionsForPropertiesOfNode(currentNode);
                }
                else {
                    parameterExprs2 = new ArrayList();
                }
                return new CreatorExpression(tupple, parameterExprs2);
            }
            if (node.getNodeType() == NodeType.LITERAL) {
                Node currentNode = node;
                List tupple = new ArrayList();
                Expression currentExpr = null;
                while (currentNode != null) {
                    tupple.add(currentNode.getNodeValue());
                    if (currentNode.getNodeType() == NodeType.INVOKE) {
                        if (currentExpr == null && tupple.size() > 1) {
                            currentExpr = new Literal(node.getNodeValue());
                        }
                        final String methodName = tupple.get(tupple.size() - 1);
                        final List parameterExprs = this.getExpressionsForPropertiesOfNode(currentNode);
                        currentExpr = new InvokeExpression(currentExpr, methodName, parameterExprs);
                        currentNode = currentNode.getFirstChild();
                        tupple = new ArrayList();
                    }
                    else {
                        currentNode = currentNode.getFirstChild();
                    }
                }
                if (currentExpr == null) {
                    currentExpr = new Literal(node.getNodeValue());
                }
                return currentExpr;
            }
            if (node.getNodeType() == NodeType.ARRAY) {
                Node currentNode = node;
                final List<Node> arrayElements = (List<Node>)node.getNodeValue();
                boolean literal = true;
                Class type = null;
                for (final Node element : arrayElements) {
                    if (type == null) {
                        type = element.getNodeValue().getClass();
                    }
                    if (element.getNodeType() == NodeType.IDENTIFIER) {
                        literal = false;
                        break;
                    }
                }
                Expression currentExpr3 = null;
                if (literal) {
                    final Object array = Array.newInstance(type, arrayElements.size());
                    final Iterator<Node> iter = arrayElements.iterator();
                    int index = 0;
                    while (iter.hasNext()) {
                        final Node element2 = iter.next();
                        Array.set(array, index++, element2.getNodeValue());
                    }
                    currentExpr3 = new Literal(array);
                }
                else {
                    final Expression[] arrayElementExprs = new Expression[arrayElements.size()];
                    for (int i = 0; i < arrayElementExprs.length; ++i) {
                        arrayElementExprs[i] = this.compilePrimaryExpression(arrayElements.get(i));
                    }
                    currentExpr3 = new ArrayExpression(arrayElementExprs);
                }
                currentNode = currentNode.getFirstChild();
                List tupple2 = new ArrayList();
                while (currentNode != null) {
                    tupple2.add(currentNode.getNodeValue());
                    if (currentNode.getNodeType() == NodeType.INVOKE) {
                        if (tupple2.size() > 1) {
                            currentExpr3 = new Literal(node.getNodeValue());
                        }
                        final String methodName2 = tupple2.get(tupple2.size() - 1);
                        final List parameterExprs3 = this.getExpressionsForPropertiesOfNode(currentNode);
                        currentExpr3 = new InvokeExpression(currentExpr3, methodName2, parameterExprs3);
                        currentNode = currentNode.getFirstChild();
                        tupple2 = new ArrayList();
                    }
                    else {
                        currentNode = currentNode.getFirstChild();
                    }
                }
                return currentExpr3;
            }
            if (node.getNodeType() == NodeType.SUBQUERY) {
                final List children = node.getChildNodes();
                if (children.size() != 1) {
                    throw new QueryCompilerSyntaxException("Invalid number of children for SUBQUERY node : " + node);
                }
                final Node varNode = children.get(0);
                final VariableExpression subqueryExpr = new VariableExpression(varNode.getNodeId());
                final Expression currentExpr4 = new SubqueryExpression((String)node.getNodeValue(), subqueryExpr);
                return currentExpr4;
            }
            else {
                if (node.getNodeType() != NodeType.CASE) {
                    NucleusLogger.QUERY.warn("ExpressionCompiler.compilePrimary " + node + " ignored by ExpressionCompiler");
                    return null;
                }
                final List<Node> children2 = (List<Node>)node.getChildNodes();
                if (children2.size() % 2 == 0) {
                    throw new QueryCompilerSyntaxException("Invalid number of children for CASE node (should be odd) : " + node);
                }
                final Node elseNode = children2.get(children2.size() - 1);
                final CaseExpression caseExpr = new CaseExpression(this.compileExpression(elseNode));
                final Iterator<Node> childIter = children2.iterator();
                while (childIter.hasNext()) {
                    final Node whenNode = childIter.next();
                    if (childIter.hasNext()) {
                        final Node actionNode = childIter.next();
                        final Expression whenExpr = this.compileExpression(whenNode);
                        final Expression actionExpr = this.compileExpression(actionNode);
                        caseExpr.addCondition(whenExpr, actionExpr);
                    }
                }
                return caseExpr;
            }
        }
    }
    
    private List<Expression> getExpressionsForPropertiesOfNode(final Node node) {
        if (node.hasProperties()) {
            final List<Expression> parameterExprs = new ArrayList<Expression>();
            final List propNodes = node.getProperties();
            for (int i = 0; i < propNodes.size(); ++i) {
                parameterExprs.add(this.compileExpression(propNodes.get(i)));
            }
            return parameterExprs;
        }
        return (List<Expression>)Collections.EMPTY_LIST;
    }
    
    private boolean isOperator(final Node node, final String operator) {
        return node.getNodeType() == NodeType.OPERATOR && node.getNodeValue().equals(operator);
    }
}
