// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.compiler;

import org.datanucleus.query.node.ParameterNode;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Collection;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.List;
import org.datanucleus.query.node.NodeType;
import java.util.ArrayList;
import org.datanucleus.store.query.QueryCompilerSyntaxException;
import java.util.Map;
import org.datanucleus.query.node.Node;
import java.util.Stack;

public class JPQLParser implements Parser
{
    private Lexer p;
    private Stack<Node> stack;
    private static String paramPrefixes;
    private Map parameterValues;
    int parameterPosition;
    
    public JPQLParser(final Map options, final Map params) {
        this.stack = new Stack<Node>();
        this.parameterPosition = 0;
        this.parameterValues = params;
    }
    
    @Override
    public Node parse(final String expression) {
        this.p = new Lexer(expression, JPQLParser.paramPrefixes, false);
        this.stack = new Stack<Node>();
        final Node result = this.processExpression();
        if (this.p.ci.getIndex() != this.p.ci.getEndIndex()) {
            final String unparsed = this.p.getInput().substring(this.p.ci.getIndex());
            throw new QueryCompilerSyntaxException("Portion of expression could not be parsed: " + unparsed);
        }
        return result;
    }
    
    @Override
    public Node parseVariable(final String expression) {
        this.p = new Lexer(expression, JPQLParser.paramPrefixes, false);
        this.stack = new Stack<Node>();
        if (!this.processIdentifier()) {
            throw new QueryCompilerSyntaxException("expected identifier", this.p.getIndex(), this.p.getInput());
        }
        if (!this.processIdentifier()) {
            throw new QueryCompilerSyntaxException("expected identifier", this.p.getIndex(), this.p.getInput());
        }
        final Node nodeVariable = this.stack.pop();
        final Node nodeType = this.stack.pop();
        nodeType.appendChildNode(nodeVariable);
        return nodeType;
    }
    
    @Override
    public Node[] parseFrom(final String expression) {
        this.p = new Lexer(expression, JPQLParser.paramPrefixes, false);
        this.stack = new Stack<Node>();
        return this.processFromExpression();
    }
    
    @Override
    public Node[] parseUpdate(final String expression) {
        this.p = new Lexer(expression, JPQLParser.paramPrefixes, false);
        this.stack = new Stack<Node>();
        return this.parseTupple(expression);
    }
    
    @Override
    public Node[] parseOrder(final String expression) {
        this.p = new Lexer(expression, JPQLParser.paramPrefixes, false);
        this.stack = new Stack<Node>();
        return this.processOrderExpression();
    }
    
    @Override
    public Node[] parseResult(final String expression) {
        this.p = new Lexer(expression, JPQLParser.paramPrefixes, false);
        this.stack = new Stack<Node>();
        final List nodes = new ArrayList();
        do {
            this.processExpression();
            final Node node = this.stack.pop();
            String alias = this.p.parseIdentifier();
            if (alias != null && alias.equalsIgnoreCase("AS")) {
                alias = this.p.parseIdentifier();
            }
            if (alias != null) {
                final Node aliasNode = new Node(NodeType.NAME, alias.toLowerCase());
                node.appendChildNode(aliasNode);
            }
            nodes.add(node);
        } while (this.p.parseString(","));
        return nodes.toArray(new Node[nodes.size()]);
    }
    
    @Override
    public Node[] parseTupple(final String expression) {
        this.p = new Lexer(expression, JPQLParser.paramPrefixes, false);
        this.stack = new Stack<Node>();
        final List nodes = new ArrayList();
        do {
            this.processExpression();
            final Node node = this.stack.pop();
            nodes.add(node);
        } while (this.p.parseString(","));
        return nodes.toArray(new Node[nodes.size()]);
    }
    
    @Override
    public Node[][] parseVariables(final String expression) {
        this.p = new Lexer(expression, JPQLParser.paramPrefixes, false);
        final List nodes = new ArrayList();
        do {
            this.processPrimary();
            if (this.stack.isEmpty()) {
                throw new QueryCompilerSyntaxException("expected identifier", this.p.getIndex(), this.p.getInput());
            }
            if (!this.processIdentifier()) {
                throw new QueryCompilerSyntaxException("expected identifier", this.p.getIndex(), this.p.getInput());
            }
            final Node nodeVariable = this.stack.pop();
            final Node nodeType = this.stack.pop();
            nodes.add(new Node[] { nodeType, nodeVariable });
        } while (this.p.parseString(";"));
        return nodes.toArray(new Node[nodes.size()][2]);
    }
    
    @Override
    public Node[][] parseParameters(final String expression) {
        this.p = new Lexer(expression, JPQLParser.paramPrefixes, false);
        final List nodes = new ArrayList();
        do {
            this.processPrimary();
            if (this.stack.isEmpty()) {
                throw new QueryCompilerSyntaxException("expected identifier", this.p.getIndex(), this.p.getInput());
            }
            if (!this.processIdentifier()) {
                throw new QueryCompilerSyntaxException("expected identifier", this.p.getIndex(), this.p.getInput());
            }
            final Node nodeVariable = this.stack.pop();
            final Node nodeType = this.stack.pop();
            nodes.add(new Node[] { nodeType, nodeVariable });
        } while (this.p.parseString(","));
        return nodes.toArray(new Node[nodes.size()][2]);
    }
    
    private Node[] processFromExpression() {
        String candidateClassName = null;
        String candidateAlias = null;
        final List nodes = new ArrayList();
        do {
            if (this.p.peekStringIgnoreCase("IN(") || this.p.peekStringIgnoreCase("IN ")) {
                this.p.parseStringIgnoreCase("IN");
                if (!this.p.parseChar('(')) {
                    throw new QueryCompilerSyntaxException("Expected: '(' but got " + this.p.remaining(), this.p.getIndex(), this.p.getInput());
                }
                final String name = this.p.parseIdentifier();
                Node parentNode;
                final Node joinedNode = parentNode = new Node(NodeType.IDENTIFIER, name);
                while (this.p.nextIsDot()) {
                    this.p.parseChar('.');
                    final String subName = this.p.parseIdentifier();
                    final Node subNode = new Node(NodeType.IDENTIFIER, subName);
                    parentNode.appendChildNode(subNode);
                    parentNode = subNode;
                }
                if (!this.p.parseChar(')')) {
                    throw new QueryCompilerSyntaxException("Expected: ')' but got " + this.p.remaining(), this.p.getIndex(), this.p.getInput());
                }
                this.p.parseStringIgnoreCase("AS");
                final String alias = this.p.parseIdentifier();
                final Node classNode = new Node(NodeType.CLASS, candidateClassName);
                final Node classAliasNode = new Node(NodeType.NAME, candidateAlias);
                classNode.insertChildNode(classAliasNode);
                this.stack.push(classNode);
                final Node joinNode = new Node(NodeType.OPERATOR, "JOIN_INNER");
                joinNode.appendChildNode(joinedNode);
                final Node joinAliasNode = new Node(NodeType.NAME, alias);
                joinNode.appendChildNode(joinAliasNode);
                classNode.appendChildNode(joinNode);
                this.processFromJoinExpression();
                nodes.add(classNode);
            }
            else {
                this.processExpression();
                Node id = this.stack.pop();
                final StringBuilder className = new StringBuilder(id.getNodeValue().toString());
                while (id.getChildNodes().size() > 0) {
                    id = id.getFirstChild();
                    className.append(".").append(id.getNodeValue().toString());
                }
                String alias2 = this.p.parseIdentifier();
                if (alias2 != null && alias2.equalsIgnoreCase("AS")) {
                    alias2 = this.p.parseIdentifier();
                }
                if (candidateClassName == null) {
                    candidateClassName = className.toString();
                    candidateAlias = alias2;
                }
                final Node classNode2 = new Node(NodeType.CLASS, className.toString());
                final Node aliasNode = new Node(NodeType.NAME, alias2);
                classNode2.insertChildNode(aliasNode);
                this.stack.push(classNode2);
                this.processFromJoinExpression();
                nodes.add(classNode2);
            }
        } while (this.p.parseString(","));
        return nodes.toArray(new Node[nodes.size()]);
    }
    
    private void processFromJoinExpression() {
        final Node candidateNode = this.stack.pop();
        boolean moreJoins = true;
        while (moreJoins) {
            boolean leftJoin = false;
            boolean innerJoin = false;
            if (this.p.parseStringIgnoreCase("INNER ")) {
                innerJoin = true;
            }
            else if (this.p.parseStringIgnoreCase("LEFT ")) {
                this.p.parseStringIgnoreCase("OUTER");
                leftJoin = true;
            }
            if (this.p.parseStringIgnoreCase("JOIN ")) {
                if (!innerJoin && !leftJoin) {
                    innerJoin = true;
                }
                boolean fetch = false;
                if (this.p.parseStringIgnoreCase("FETCH")) {
                    fetch = true;
                }
                final String id = this.p.parseIdentifier();
                Node parentNode;
                final Node joinedNode = parentNode = new Node(NodeType.IDENTIFIER, id);
                while (this.p.nextIsDot()) {
                    this.p.parseChar('.');
                    final Node subNode = new Node(NodeType.IDENTIFIER, this.p.parseName());
                    parentNode.appendChildNode(subNode);
                    parentNode = subNode;
                }
                this.p.parseStringIgnoreCase("AS ");
                final String alias = this.p.parseName();
                Node onNode = null;
                if (this.p.parseStringIgnoreCase("ON ")) {
                    this.processExpression();
                    onNode = this.stack.pop();
                }
                String joinType = "JOIN_INNER";
                if (innerJoin) {
                    joinType = (fetch ? "JOIN_INNER_FETCH" : "JOIN_INNER");
                }
                else if (leftJoin) {
                    joinType = (fetch ? "JOIN_OUTER_FETCH" : "JOIN_OUTER");
                }
                final Node joinNode = new Node(NodeType.OPERATOR, joinType);
                joinNode.appendChildNode(joinedNode);
                final Node joinedAliasNode = new Node(NodeType.NAME, alias);
                joinNode.appendChildNode(joinedAliasNode);
                candidateNode.appendChildNode(joinNode);
                if (onNode == null) {
                    continue;
                }
                joinNode.appendChildNode(onNode);
            }
            else {
                if (innerJoin || leftJoin) {
                    throw new NucleusUserException("Expected JOIN after INNER/LEFT keyword at" + this.p.remaining());
                }
                moreJoins = false;
            }
        }
        this.stack.push(candidateNode);
    }
    
    private Node[] processOrderExpression() {
        final List nodes = new ArrayList();
        do {
            this.processExpression();
            Node directionNode = null;
            if (this.p.parseStringIgnoreCase("asc")) {
                directionNode = new Node(NodeType.OPERATOR, "ascending");
            }
            else if (this.p.parseStringIgnoreCase("desc")) {
                directionNode = new Node(NodeType.OPERATOR, "descending");
            }
            else {
                directionNode = new Node(NodeType.OPERATOR, "ascending");
            }
            Node nullsNode = null;
            if (this.p.parseString("NULLS FIRST") || this.p.parseString("nulls first")) {
                nullsNode = new Node(NodeType.OPERATOR, "nulls first");
            }
            else if (this.p.parseString("NULLS LAST") || this.p.parseString("nulls last")) {
                nullsNode = new Node(NodeType.OPERATOR, "nulls first");
            }
            final Node expr = new Node(NodeType.OPERATOR, "order");
            expr.insertChildNode(directionNode);
            if (nullsNode != null) {
                expr.appendChildNode(nullsNode);
            }
            if (!this.stack.empty()) {
                expr.insertChildNode(this.stack.pop());
            }
            nodes.add(expr);
        } while (this.p.parseString(","));
        return nodes.toArray(new Node[nodes.size()]);
    }
    
    private Node processExpression() {
        this.processOrExpression();
        return this.stack.peek();
    }
    
    private void processOrExpression() {
        this.processAndExpression();
        while (this.p.parseStringIgnoreCase("OR ")) {
            this.processAndExpression();
            final Node expr = new Node(NodeType.OPERATOR, "||");
            expr.insertChildNode(this.stack.pop());
            expr.insertChildNode(this.stack.pop());
            this.stack.push(expr);
        }
    }
    
    private void processAndExpression() {
        this.processRelationalExpression();
        while (this.p.parseStringIgnoreCase("AND ")) {
            this.processRelationalExpression();
            final Node expr = new Node(NodeType.OPERATOR, "&&");
            expr.insertChildNode(this.stack.pop());
            expr.insertChildNode(this.stack.pop());
            this.stack.push(expr);
        }
    }
    
    private void processRelationalExpression() {
        this.processAdditiveExpression();
        while (true) {
            if (this.p.parseString("=")) {
                this.processAdditiveExpression();
                final Node right = this.stack.pop();
                final Node left = this.stack.pop();
                if (right.getNodeType() == NodeType.TYPE) {
                    final Node primNode = right.getFirstChild();
                    final Node expr = new Node(NodeType.OPERATOR, "instanceof");
                    expr.appendChildNode(primNode);
                    expr.appendChildNode(left);
                    this.stack.push(expr);
                }
                else if (left.getNodeType() == NodeType.TYPE) {
                    final Node primNode = left.getFirstChild();
                    final Node expr = new Node(NodeType.OPERATOR, "instanceof");
                    expr.appendChildNode(primNode);
                    expr.appendChildNode(right);
                    this.stack.push(expr);
                }
                else {
                    final Node expr2 = new Node(NodeType.OPERATOR, "==");
                    expr2.insertChildNode(right);
                    expr2.insertChildNode(left);
                    this.stack.push(expr2);
                }
            }
            else if (this.p.parseString("<>")) {
                this.processAdditiveExpression();
                final Node right = this.stack.pop();
                final Node left = this.stack.pop();
                if (right.getNodeType() == NodeType.TYPE) {
                    final Node primNode = right.getFirstChild();
                    final Node expr = new Node(NodeType.OPERATOR, "instanceof");
                    expr.appendChildNode(primNode);
                    expr.appendChildNode(left);
                    final Node notNode = new Node(NodeType.OPERATOR, "!");
                    notNode.appendChildNode(expr);
                    this.stack.push(notNode);
                }
                else if (left.getNodeType() == NodeType.TYPE) {
                    final Node primNode = left.getFirstChild();
                    final Node expr = new Node(NodeType.OPERATOR, "instanceof");
                    expr.appendChildNode(primNode);
                    expr.appendChildNode(right);
                    final Node notNode = new Node(NodeType.OPERATOR, "!");
                    notNode.appendChildNode(expr);
                    this.stack.push(notNode);
                }
                else {
                    final Node expr2 = new Node(NodeType.OPERATOR, "!=");
                    expr2.insertChildNode(right);
                    expr2.insertChildNode(left);
                    this.stack.push(expr2);
                }
            }
            else if (this.p.parseStringIgnoreCase("NOT ")) {
                if (this.p.parseStringIgnoreCase("BETWEEN ")) {
                    final Node inputNode = this.stack.pop();
                    this.processAdditiveExpression();
                    final Node lowerNode = this.stack.pop();
                    if (!this.p.parseStringIgnoreCase("AND ")) {
                        throw new NucleusUserException("Query has BETWEEN keyword with no AND clause");
                    }
                    this.processAdditiveExpression();
                    final Node upperNode = this.stack.pop();
                    final Node leftNode = new Node(NodeType.OPERATOR, "<");
                    leftNode.appendChildNode(inputNode);
                    leftNode.appendChildNode(lowerNode);
                    final Node rightNode = new Node(NodeType.OPERATOR, ">");
                    rightNode.appendChildNode(inputNode);
                    rightNode.appendChildNode(upperNode);
                    final Node betweenNode = new Node(NodeType.OPERATOR, "||");
                    betweenNode.appendChildNode(leftNode);
                    betweenNode.appendChildNode(rightNode);
                    this.stack.push(betweenNode);
                }
                else if (this.p.parseStringIgnoreCase("LIKE ")) {
                    this.processLikeExpression();
                    final Node notNode2 = new Node(NodeType.OPERATOR, "!");
                    notNode2.insertChildNode(this.stack.pop());
                    this.stack.push(notNode2);
                }
                else if (this.p.parseStringIgnoreCase("IN")) {
                    this.processInExpression(true);
                }
                else {
                    if (!this.p.parseStringIgnoreCase("MEMBER ")) {
                        throw new NucleusException("Unsupported query syntax NOT followed by unsupported keyword");
                    }
                    this.processMemberExpression(true);
                }
            }
            else if (this.p.parseStringIgnoreCase("BETWEEN ")) {
                final Node inputNode = this.stack.pop();
                this.processAdditiveExpression();
                final Node lowerNode = this.stack.pop();
                if (!this.p.parseStringIgnoreCase("AND ")) {
                    throw new NucleusUserException("Query has BETWEEN keyword with no AND clause");
                }
                this.processAdditiveExpression();
                final Node upperNode = this.stack.pop();
                final Node leftNode = new Node(NodeType.OPERATOR, ">=");
                leftNode.appendChildNode(inputNode);
                leftNode.appendChildNode(lowerNode);
                final Node rightNode = new Node(NodeType.OPERATOR, "<=");
                rightNode.appendChildNode(inputNode);
                rightNode.appendChildNode(upperNode);
                final Node betweenNode = new Node(NodeType.OPERATOR, "&&");
                betweenNode.appendChildNode(rightNode);
                betweenNode.appendChildNode(leftNode);
                this.stack.push(betweenNode);
            }
            else if (this.p.parseStringIgnoreCase("LIKE ")) {
                this.processLikeExpression();
            }
            else if (this.p.parseStringIgnoreCase("IN")) {
                this.processInExpression(false);
            }
            else if (this.p.parseStringIgnoreCase("MEMBER ")) {
                this.processMemberExpression(false);
            }
            else if (this.p.parseStringIgnoreCase("IS ")) {
                final Node inputRootNode;
                Node inputNode = inputRootNode = this.stack.pop();
                if (inputNode.getNodeType() == NodeType.IDENTIFIER) {
                    while (inputNode.getFirstChild() != null) {
                        inputNode = inputNode.getFirstChild();
                    }
                }
                boolean not = false;
                if (this.p.parseStringIgnoreCase("NOT ")) {
                    not = true;
                }
                if (this.p.parseStringIgnoreCase("NULL")) {
                    final Node isNode = new Node(NodeType.OPERATOR, not ? "!=" : "==");
                    final Node compareNode = new Node(NodeType.LITERAL, null);
                    isNode.insertChildNode(compareNode);
                    isNode.insertChildNode(inputRootNode);
                    this.stack.push(isNode);
                }
                else {
                    if (!this.p.parseStringIgnoreCase("EMPTY")) {
                        throw new NucleusException("Encountered IS " + (not ? "NOT " : " ") + " that should be followed by NULL | EMPTY but isnt");
                    }
                    final Node sizeNode = new Node(NodeType.INVOKE, "size");
                    inputNode.insertChildNode(sizeNode);
                    final Node isEmptyNode = new Node(NodeType.OPERATOR, not ? "!=" : "==");
                    isEmptyNode.appendChildNode(inputNode);
                    final Node zeroNode = new Node(NodeType.LITERAL, 0);
                    isEmptyNode.appendChildNode(zeroNode);
                    this.stack.push(isEmptyNode);
                }
            }
            else if (this.p.parseString("<=")) {
                this.processAdditiveExpression();
                final Node expr3 = new Node(NodeType.OPERATOR, "<=");
                expr3.insertChildNode(this.stack.pop());
                expr3.insertChildNode(this.stack.pop());
                this.stack.push(expr3);
            }
            else if (this.p.parseString(">=")) {
                this.processAdditiveExpression();
                final Node expr3 = new Node(NodeType.OPERATOR, ">=");
                expr3.insertChildNode(this.stack.pop());
                expr3.insertChildNode(this.stack.pop());
                this.stack.push(expr3);
            }
            else if (this.p.parseChar('<')) {
                this.processAdditiveExpression();
                final Node expr3 = new Node(NodeType.OPERATOR, "<");
                expr3.insertChildNode(this.stack.pop());
                expr3.insertChildNode(this.stack.pop());
                this.stack.push(expr3);
            }
            else {
                if (!this.p.parseChar('>')) {
                    return;
                }
                this.processAdditiveExpression();
                final Node expr3 = new Node(NodeType.OPERATOR, ">");
                expr3.insertChildNode(this.stack.pop());
                expr3.insertChildNode(this.stack.pop());
                this.stack.push(expr3);
            }
        }
    }
    
    private void processLikeExpression() {
        final Node primaryRootNode;
        Node primaryNode = primaryRootNode = this.stack.pop();
        if (primaryNode.getNodeType() == NodeType.IDENTIFIER) {
            while (primaryNode.getFirstChild() != null) {
                primaryNode = primaryNode.getFirstChild();
            }
        }
        this.processAdditiveExpression();
        final Node likeExprNode = this.stack.pop();
        if (this.p.parseStringIgnoreCase("ESCAPE")) {
            this.processAdditiveExpression();
            final Node escapeNode = this.stack.pop();
            final Node matchesNode = new Node(NodeType.INVOKE, "matches");
            matchesNode.addProperty(likeExprNode);
            matchesNode.addProperty(escapeNode);
            primaryNode.appendChildNode(matchesNode);
            this.stack.push(primaryRootNode);
        }
        else {
            final Node matchesNode2 = new Node(NodeType.INVOKE, "matches");
            matchesNode2.addProperty(likeExprNode);
            primaryNode.appendChildNode(matchesNode2);
            this.stack.push(primaryRootNode);
        }
    }
    
    private void processInExpression(final boolean not) {
        final Node inputNode = this.stack.pop();
        if (!this.p.parseChar('(')) {
            final Node inNode = new Node(NodeType.OPERATOR, not ? "NOT IN" : "IN");
            inNode.appendChildNode(inputNode);
            this.processExpression();
            final Node subqueryNode = this.stack.pop();
            inNode.appendChildNode(subqueryNode);
            this.stack.push(inNode);
            return;
        }
        Node inNode = null;
        int numArgs = 0;
        do {
            this.processPrimary();
            if (this.stack.peek() == null) {
                throw new QueryCompilerSyntaxException("Expected literal|parameter but got " + this.p.remaining(), this.p.getIndex(), this.p.getInput());
            }
            ++numArgs;
            final Node valueNode = this.stack.pop();
            this.p.skipWS();
            if (numArgs == 1 && !this.p.peekStringIgnoreCase(",") && valueNode.getNodeType() == NodeType.PARAMETER && this.parameterValues != null && this.parameterValues.containsKey(valueNode.getNodeValue())) {
                final Object paramValue = this.parameterValues.get(valueNode.getNodeValue());
                if (paramValue instanceof Collection) {
                    final Node containsNode = new Node(NodeType.INVOKE, "contains");
                    containsNode.addProperty(inputNode);
                    valueNode.appendChildNode(containsNode);
                    inNode = valueNode;
                    break;
                }
            }
            final Node compareNode = new Node(NodeType.OPERATOR, not ? "!=" : "==");
            compareNode.appendChildNode(inputNode);
            compareNode.appendChildNode(valueNode);
            if (inNode == null) {
                inNode = compareNode;
            }
            else {
                final Node newInNode = new Node(NodeType.OPERATOR, not ? "&&" : "||");
                newInNode.appendChildNode(inNode);
                newInNode.appendChildNode(compareNode);
                inNode = newInNode;
            }
        } while (this.p.parseChar(','));
        if (!this.p.parseChar(')')) {
            throw new QueryCompilerSyntaxException("Expected: ')' but got " + this.p.remaining(), this.p.getIndex(), this.p.getInput());
        }
        this.stack.push(inNode);
    }
    
    private void processMemberExpression(final boolean not) {
        final Node inputNode = this.stack.pop();
        this.p.parseStringIgnoreCase("OF");
        this.processPrimary();
        Node containerNode;
        for (containerNode = this.stack.peek(); containerNode.getFirstChild() != null; containerNode = containerNode.getFirstChild()) {}
        if (not) {
            final Node notNode = new Node(NodeType.OPERATOR, "!");
            this.stack.pop();
            notNode.insertChildNode(containerNode);
            this.stack.push(notNode);
        }
        final Node containsNode = new Node(NodeType.INVOKE, "contains");
        containsNode.addProperty(inputNode);
        containerNode.appendChildNode(containsNode);
    }
    
    private void processCaseExpression() {
        final Node caseNode = new Node(NodeType.CASE);
        while (this.p.parseStringIgnoreCase("WHEN ")) {
            this.processExpression();
            final Node whenNode = this.stack.pop();
            caseNode.appendChildNode(whenNode);
            final boolean hasThen = this.p.parseStringIgnoreCase("THEN ");
            if (!hasThen) {
                throw new QueryCompilerSyntaxException("expected 'THEN' as part of CASE", this.p.getIndex(), this.p.getInput());
            }
            this.processExpression();
            final Node actionNode = this.stack.pop();
            caseNode.appendChildNode(actionNode);
        }
        if (this.p.parseStringIgnoreCase("ELSE ")) {
            this.processExpression();
            final Node elseNode = this.stack.pop();
            caseNode.appendChildNode(elseNode);
        }
        if (!this.p.parseStringIgnoreCase("END")) {
            throw new QueryCompilerSyntaxException("expected 'END' as part of CASE", this.p.getIndex(), this.p.getInput());
        }
        this.stack.push(caseNode);
    }
    
    protected void processAdditiveExpression() {
        this.processMultiplicativeExpression();
        while (true) {
            if (this.p.parseChar('+')) {
                this.processMultiplicativeExpression();
                final Node expr = new Node(NodeType.OPERATOR, "+");
                expr.insertChildNode(this.stack.pop());
                expr.insertChildNode(this.stack.pop());
                this.stack.push(expr);
            }
            else {
                if (!this.p.parseChar('-')) {
                    break;
                }
                this.processMultiplicativeExpression();
                final Node expr = new Node(NodeType.OPERATOR, "-");
                expr.insertChildNode(this.stack.pop());
                expr.insertChildNode(this.stack.pop());
                this.stack.push(expr);
            }
        }
    }
    
    protected void processMultiplicativeExpression() {
        this.processUnaryExpression();
        while (true) {
            if (this.p.parseChar('*')) {
                this.processUnaryExpression();
                final Node expr = new Node(NodeType.OPERATOR, "*");
                expr.insertChildNode(this.stack.pop());
                expr.insertChildNode(this.stack.pop());
                this.stack.push(expr);
            }
            else if (this.p.parseChar('/')) {
                this.processUnaryExpression();
                final Node expr = new Node(NodeType.OPERATOR, "/");
                expr.insertChildNode(this.stack.pop());
                expr.insertChildNode(this.stack.pop());
                this.stack.push(expr);
            }
            else {
                if (!this.p.parseChar('%')) {
                    break;
                }
                this.processUnaryExpression();
                final Node expr = new Node(NodeType.OPERATOR, "%");
                expr.insertChildNode(this.stack.pop());
                expr.insertChildNode(this.stack.pop());
                this.stack.push(expr);
            }
        }
    }
    
    protected void processUnaryExpression() {
        if (this.p.parseString("++")) {
            throw new NucleusUserException("Unsupported operator '++'");
        }
        if (this.p.parseString("--")) {
            throw new NucleusUserException("Unsupported operator '--'");
        }
        if (this.p.parseChar('+')) {
            this.processUnaryExpression();
        }
        else if (this.p.parseChar('-')) {
            this.processUnaryExpression();
            final Node expr = new Node(NodeType.OPERATOR, "-");
            expr.insertChildNode(this.stack.pop());
            this.stack.push(expr);
        }
        else if (this.p.parseStringIgnoreCase("NOT ")) {
            this.processRelationalExpression();
            final Node expr = new Node(NodeType.OPERATOR, "!");
            expr.insertChildNode(this.stack.pop());
            this.stack.push(expr);
        }
        else {
            this.processPrimary();
        }
    }
    
    protected void processPrimary() {
        String subqueryKeyword = null;
        Node subqueryNode = null;
        if (this.p.parseStringIgnoreCase("SOME ")) {
            subqueryKeyword = "SOME";
            this.processExpression();
            subqueryNode = this.stack.pop();
        }
        else if (this.p.parseStringIgnoreCase("ALL ")) {
            subqueryKeyword = "ALL";
            this.processExpression();
            subqueryNode = this.stack.pop();
        }
        else if (this.p.parseStringIgnoreCase("ANY ")) {
            subqueryKeyword = "ANY";
            this.processExpression();
            subqueryNode = this.stack.pop();
        }
        else if (this.p.parseStringIgnoreCase("EXISTS ")) {
            subqueryKeyword = "EXISTS";
            this.processExpression();
            subqueryNode = this.stack.pop();
        }
        if (subqueryKeyword != null && subqueryNode != null) {
            final Node subNode = new Node(NodeType.SUBQUERY, subqueryKeyword);
            subNode.appendChildNode(subqueryNode);
            this.stack.push(subNode);
            return;
        }
        if (this.p.parseStringIgnoreCase("CURRENT_DATE")) {
            final Node node = new Node(NodeType.INVOKE, "CURRENT_DATE");
            this.stack.push(node);
            return;
        }
        if (this.p.parseStringIgnoreCase("CURRENT_TIMESTAMP")) {
            final Node node = new Node(NodeType.INVOKE, "CURRENT_TIMESTAMP");
            this.stack.push(node);
            return;
        }
        if (this.p.parseStringIgnoreCase("CURRENT_TIME")) {
            final Node node = new Node(NodeType.INVOKE, "CURRENT_TIME");
            this.stack.push(node);
            return;
        }
        if (this.p.parseStringIgnoreCase("CASE ")) {
            this.processCaseExpression();
            return;
        }
        if (this.p.parseStringIgnoreCase("DISTINCT ")) {
            final Node distinctNode = new Node(NodeType.OPERATOR, "DISTINCT");
            this.processExpression();
            final Node identifierNode = this.stack.pop();
            distinctNode.appendChildNode(identifierNode);
            this.stack.push(distinctNode);
            return;
        }
        if (this.p.parseString("TREAT(")) {
            this.processExpression();
            final Node identifierNode2 = this.stack.pop();
            String typeName = this.p.parseIdentifier();
            if (typeName == null || !typeName.equalsIgnoreCase("AS")) {
                throw new QueryCompilerSyntaxException("TREAT should always be structured as 'TREAT(id AS typeName)'");
            }
            this.processExpression();
            final Node typeNode = this.stack.pop();
            typeName = typeNode.getNodeChildId();
            final Node castNode = new Node(NodeType.CAST, typeName);
            castNode.setParent(identifierNode2);
            identifierNode2.appendChildNode(castNode);
            if (!this.p.parseChar(')')) {
                throw new QueryCompilerSyntaxException("')' expected", this.p.getIndex(), this.p.getInput());
            }
            this.stack.push(castNode);
        }
        else {
            if (this.processCreator() || this.processLiteral() || this.processMethod()) {
                return;
            }
            Node castNode2 = null;
            if (this.p.parseChar('(')) {
                this.processExpression();
                if (!this.p.parseChar(')')) {
                    throw new QueryCompilerSyntaxException("expected ')'", this.p.getIndex(), this.p.getInput());
                }
                final Node peekNode = this.stack.peek();
                if (peekNode.getNodeType() != NodeType.CAST) {
                    return;
                }
                castNode2 = peekNode;
            }
            if (castNode2 == null && !this.processIdentifier()) {
                throw new QueryCompilerSyntaxException("Identifier expected", this.p.getIndex(), this.p.getInput());
            }
            final int size = this.stack.size();
            while (this.p.parseChar('.')) {
                if (this.processMethod()) {
                    continue;
                }
                if (this.processIdentifier()) {
                    continue;
                }
                throw new QueryCompilerSyntaxException("Identifier expected", this.p.getIndex(), this.p.getInput());
            }
            while (this.stack.size() > size) {
                final Node top = this.stack.pop();
                final Node peek = this.stack.peek();
                peek.insertChildNode(top);
            }
            if (castNode2 != null) {
                this.stack.pop();
                final Node castParentNode = castNode2.getParent();
                this.stack.push(castParentNode);
            }
        }
    }
    
    private boolean processCreator() {
        if (this.p.parseStringIgnoreCase("NEW ")) {
            final int size = this.stack.size();
            if (!this.processMethod()) {
                if (!this.processIdentifier()) {
                    throw new QueryCompilerSyntaxException("Identifier expected", this.p.getIndex(), this.p.getInput());
                }
                while (this.p.parseChar('.')) {
                    if (this.processMethod()) {
                        continue;
                    }
                    if (this.processIdentifier()) {
                        continue;
                    }
                    throw new QueryCompilerSyntaxException("Identifier expected", this.p.getIndex(), this.p.getInput());
                }
            }
            while (this.stack.size() - 1 > size) {
                final Node top = this.stack.pop();
                final Node peek = this.stack.peek();
                peek.insertChildNode(top);
            }
            final Node node = this.stack.pop();
            final Node newNode = new Node(NodeType.CREATOR);
            newNode.insertChildNode(node);
            this.stack.push(newNode);
            return true;
        }
        return false;
    }
    
    private boolean processMethod() {
        String method = this.p.parseMethod();
        if (method == null) {
            return false;
        }
        this.p.skipWS();
        this.p.parseChar('(');
        if (method.equalsIgnoreCase("COUNT")) {
            method = "COUNT";
        }
        else if (method.equalsIgnoreCase("AVG")) {
            method = "AVG";
        }
        else if (method.equalsIgnoreCase("MIN")) {
            method = "MIN";
        }
        else if (method.equalsIgnoreCase("MAX")) {
            method = "MAX";
        }
        else if (method.equalsIgnoreCase("SUM")) {
            method = "SUM";
        }
        else if (method.equalsIgnoreCase("ABS")) {
            method = "ABS";
        }
        else if (method.equalsIgnoreCase("INDEX")) {
            method = "INDEX";
        }
        else if (method.equalsIgnoreCase("FUNCTION")) {
            method = "FUNCTION";
        }
        if (method.equalsIgnoreCase("Object")) {
            this.processExpression();
            if (!this.p.parseChar(')')) {
                throw new QueryCompilerSyntaxException("')' expected", this.p.getIndex(), this.p.getInput());
            }
            return true;
        }
        else if (method.equalsIgnoreCase("MOD")) {
            final Node modNode = new Node(NodeType.OPERATOR, "%");
            this.processExpression();
            final Node firstNode = this.stack.pop();
            if (!this.p.parseChar(',')) {
                throw new QueryCompilerSyntaxException("',' expected", this.p.getIndex(), this.p.getInput());
            }
            this.processExpression();
            final Node secondNode = this.stack.pop();
            modNode.appendChildNode(firstNode);
            modNode.appendChildNode(secondNode);
            this.stack.push(modNode);
            if (!this.p.parseChar(')')) {
                throw new QueryCompilerSyntaxException("')' expected", this.p.getIndex(), this.p.getInput());
            }
            return true;
        }
        else if (method.equalsIgnoreCase("TYPE")) {
            final Node typeNode = new Node(NodeType.TYPE);
            this.processExpression();
            final Node typePrimaryNode = this.stack.pop();
            typeNode.appendChildNode(typePrimaryNode);
            this.stack.push(typeNode);
            if (!this.p.parseChar(')')) {
                throw new QueryCompilerSyntaxException("')' expected", this.p.getIndex(), this.p.getInput());
            }
            return true;
        }
        else if (method.equalsIgnoreCase("SUBSTRING")) {
            final Node invokeNode = new Node(NodeType.INVOKE, "substring");
            this.processExpression();
            final Node primaryNode = this.stack.pop();
            if (!this.p.parseChar(',')) {
                throw new QueryCompilerSyntaxException("',' expected", this.p.getIndex(), this.p.getInput());
            }
            this.processExpression();
            final Node arg1 = this.stack.pop();
            final Node oneNode = new Node(NodeType.LITERAL, 1);
            final Node arg1Node = new Node(NodeType.OPERATOR, "-");
            arg1Node.insertChildNode(arg1);
            arg1Node.appendChildNode(oneNode);
            if (this.p.parseChar(',')) {
                this.processExpression();
                final Node arg2 = this.stack.pop();
                final Node arg2Node = new Node(NodeType.OPERATOR, "+");
                arg2Node.appendChildNode(arg2);
                arg2Node.appendChildNode(arg1Node);
                if (!this.p.parseChar(')')) {
                    throw new QueryCompilerSyntaxException("')' expected", this.p.getIndex(), this.p.getInput());
                }
                primaryNode.appendChildNode(invokeNode);
                invokeNode.addProperty(arg1Node);
                invokeNode.addProperty(arg2Node);
                this.stack.push(primaryNode);
                return true;
            }
            else {
                if (this.p.parseChar(')')) {
                    primaryNode.appendChildNode(invokeNode);
                    invokeNode.addProperty(arg1Node);
                    this.stack.push(primaryNode);
                    return true;
                }
                throw new QueryCompilerSyntaxException("')' expected", this.p.getIndex(), this.p.getInput());
            }
        }
        else if (method.equalsIgnoreCase("UPPER")) {
            final Node invokeNode = new Node(NodeType.INVOKE, "toUpperCase");
            this.processExpression();
            if (!this.p.parseChar(')')) {
                throw new QueryCompilerSyntaxException("',' expected", this.p.getIndex(), this.p.getInput());
            }
            Node primaryNode;
            Node primaryRootNode;
            for (primaryNode = (primaryRootNode = this.stack.pop()); primaryNode.getFirstChild() != null; primaryNode = primaryNode.getFirstChild()) {}
            primaryNode.appendChildNode(invokeNode);
            this.stack.push(primaryRootNode);
            return true;
        }
        else if (method.equalsIgnoreCase("LOWER")) {
            final Node invokeNode = new Node(NodeType.INVOKE, "toLowerCase");
            this.processExpression();
            if (!this.p.parseChar(')')) {
                throw new QueryCompilerSyntaxException("',' expected", this.p.getIndex(), this.p.getInput());
            }
            Node primaryNode;
            Node primaryRootNode;
            for (primaryNode = (primaryRootNode = this.stack.pop()); primaryNode.getFirstChild() != null; primaryNode = primaryNode.getFirstChild()) {}
            primaryNode.appendChildNode(invokeNode);
            this.stack.push(primaryRootNode);
            return true;
        }
        else if (method.equalsIgnoreCase("LENGTH")) {
            final Node invokeNode = new Node(NodeType.INVOKE, "length");
            this.processExpression();
            if (!this.p.parseChar(')')) {
                throw new QueryCompilerSyntaxException("',' expected", this.p.getIndex(), this.p.getInput());
            }
            Node primaryNode;
            Node primaryRootNode;
            for (primaryNode = (primaryRootNode = this.stack.pop()); primaryNode.getFirstChild() != null; primaryNode = primaryNode.getFirstChild()) {}
            primaryNode.appendChildNode(invokeNode);
            this.stack.push(primaryRootNode);
            return true;
        }
        else {
            if (method.equalsIgnoreCase("CONCAT")) {
                this.processExpression();
                Node prevNode = this.stack.pop();
                while (this.p.parseChar(',')) {
                    this.processExpression();
                    final Node thisNode = this.stack.pop();
                    Node currentNode = new Node(NodeType.OPERATOR, "+");
                    currentNode.appendChildNode(prevNode);
                    currentNode.appendChildNode(thisNode);
                    if (this.p.parseChar(')')) {
                        this.stack.push(currentNode);
                        return true;
                    }
                    prevNode = currentNode;
                    currentNode = null;
                }
                throw new QueryCompilerSyntaxException("',' expected", this.p.getIndex(), this.p.getInput());
            }
            if (method.equalsIgnoreCase("LOCATE")) {
                this.processExpression();
                final Node searchNode = this.stack.pop();
                final Node invokeNode2 = new Node(NodeType.INVOKE, "indexOf");
                invokeNode2.addProperty(searchNode);
                if (!this.p.parseChar(',')) {
                    throw new QueryCompilerSyntaxException("',' expected", this.p.getIndex(), this.p.getInput());
                }
                this.processExpression();
                Node primaryRootNode2;
                Node primaryNode2;
                for (primaryNode2 = (primaryRootNode2 = this.stack.pop()); primaryNode2.getFirstChild() != null; primaryNode2 = primaryNode2.getFirstChild()) {}
                primaryNode2.appendChildNode(invokeNode2);
                final Node oneNode2 = new Node(NodeType.LITERAL, 1);
                if (this.p.parseChar(',')) {
                    this.processExpression();
                    final Node fromPosNode = this.stack.pop();
                    final Node positionNode = new Node(NodeType.OPERATOR, "-");
                    positionNode.appendChildNode(fromPosNode);
                    positionNode.appendChildNode(oneNode2);
                    invokeNode2.addProperty(positionNode);
                }
                if (!this.p.parseChar(')')) {
                    throw new QueryCompilerSyntaxException("')' expected", this.p.getIndex(), this.p.getInput());
                }
                final Node locateNode = new Node(NodeType.OPERATOR, "+");
                locateNode.appendChildNode(primaryRootNode2);
                locateNode.appendChildNode(oneNode2);
                this.stack.push(locateNode);
                return true;
            }
            else if (method.equalsIgnoreCase("TRIM")) {
                String methodName = "trim";
                if (this.p.parseStringIgnoreCase("LEADING")) {
                    methodName = "trimLeft";
                }
                else if (this.p.parseStringIgnoreCase("TRAILING")) {
                    methodName = "trimRight";
                }
                else if (this.p.parseStringIgnoreCase("BOTH")) {}
                final Node invokeNode2 = new Node(NodeType.INVOKE, methodName);
                Node trimCharNode = null;
                this.processExpression();
                Node next = this.stack.pop();
                if (this.p.parseChar(')')) {
                    next.appendChildNode(invokeNode2);
                    this.stack.push(next);
                    return true;
                }
                if (next.getNodeType() == NodeType.LITERAL) {
                    trimCharNode = next;
                    if (this.p.parseStringIgnoreCase("FROM ")) {}
                    this.processExpression();
                    next = this.stack.pop();
                }
                else if (next.getNodeType() == NodeType.IDENTIFIER) {
                    final Object litValue = next.getNodeValue();
                    if (!(litValue instanceof String) || !((String)litValue).equals("FROM")) {
                        throw new QueryCompilerSyntaxException("Unexpected expression", this.p.getIndex(), this.p.getInput());
                    }
                    this.processExpression();
                    next = this.stack.pop();
                }
                if (!this.p.parseChar(')')) {
                    throw new QueryCompilerSyntaxException("')' expected", this.p.getIndex(), this.p.getInput());
                }
                next.appendChildNode(invokeNode2);
                if (trimCharNode != null) {
                    invokeNode2.addProperty(trimCharNode);
                }
                this.stack.push(next);
                return true;
            }
            else if (method.equalsIgnoreCase("SIZE")) {
                final Node invokeNode = new Node(NodeType.INVOKE, "size");
                this.processExpression();
                if (!this.p.parseChar(')')) {
                    throw new QueryCompilerSyntaxException("',' expected", this.p.getIndex(), this.p.getInput());
                }
                Node primaryNode;
                Node primaryRootNode;
                for (primaryNode = (primaryRootNode = this.stack.pop()); primaryNode.getFirstChild() != null; primaryNode = primaryNode.getFirstChild()) {}
                primaryNode.appendChildNode(invokeNode);
                this.stack.push(primaryRootNode);
                return true;
            }
            else if (method.equalsIgnoreCase("KEY")) {
                final Node invokeNode = new Node(NodeType.INVOKE, "mapKey");
                this.processExpression();
                if (!this.p.parseChar(')')) {
                    throw new QueryCompilerSyntaxException("',' expected", this.p.getIndex(), this.p.getInput());
                }
                Node primaryNode;
                Node primaryRootNode;
                for (primaryNode = (primaryRootNode = this.stack.pop()); primaryNode.getFirstChild() != null; primaryNode = primaryNode.getFirstChild()) {}
                primaryNode.appendChildNode(invokeNode);
                this.stack.push(primaryRootNode);
                return true;
            }
            else if (method.equalsIgnoreCase("VALUE")) {
                final Node invokeNode = new Node(NodeType.INVOKE, "mapValue");
                this.processExpression();
                if (!this.p.parseChar(')')) {
                    throw new QueryCompilerSyntaxException("',' expected", this.p.getIndex(), this.p.getInput());
                }
                Node primaryNode;
                Node primaryRootNode;
                for (primaryNode = (primaryRootNode = this.stack.pop()); primaryNode.getFirstChild() != null; primaryNode = primaryNode.getFirstChild()) {}
                primaryNode.appendChildNode(invokeNode);
                this.stack.push(primaryRootNode);
                return true;
            }
            else if (method.equalsIgnoreCase("ENTRY")) {
                final Node invokeNode = new Node(NodeType.INVOKE, "mapEntry");
                this.processExpression();
                if (!this.p.parseChar(')')) {
                    throw new QueryCompilerSyntaxException("',' expected", this.p.getIndex(), this.p.getInput());
                }
                Node primaryNode;
                Node primaryRootNode;
                for (primaryNode = (primaryRootNode = this.stack.pop()); primaryNode.getFirstChild() != null; primaryNode = primaryNode.getFirstChild()) {}
                primaryNode.appendChildNode(invokeNode);
                this.stack.push(primaryRootNode);
                return true;
            }
            else if (method.equalsIgnoreCase("YEAR")) {
                final Node invokeNode = new Node(NodeType.INVOKE, "getYear");
                this.processExpression();
                if (!this.p.parseChar(')')) {
                    throw new QueryCompilerSyntaxException("',' expected", this.p.getIndex(), this.p.getInput());
                }
                Node primaryNode;
                Node primaryRootNode;
                for (primaryNode = (primaryRootNode = this.stack.pop()); primaryNode.getFirstChild() != null; primaryNode = primaryNode.getFirstChild()) {}
                primaryNode.appendChildNode(invokeNode);
                this.stack.push(primaryRootNode);
                return true;
            }
            else if (method.equalsIgnoreCase("MONTH")) {
                final Node invokeNode = new Node(NodeType.INVOKE, "getMonth");
                this.processExpression();
                if (!this.p.parseChar(')')) {
                    throw new QueryCompilerSyntaxException("',' expected", this.p.getIndex(), this.p.getInput());
                }
                Node primaryNode;
                Node primaryRootNode;
                for (primaryNode = (primaryRootNode = this.stack.pop()); primaryNode.getFirstChild() != null; primaryNode = primaryNode.getFirstChild()) {}
                primaryNode.appendChildNode(invokeNode);
                this.stack.push(primaryRootNode);
                return true;
            }
            else if (method.equalsIgnoreCase("DAY")) {
                final Node invokeNode = new Node(NodeType.INVOKE, "getDay");
                this.processExpression();
                if (!this.p.parseChar(')')) {
                    throw new QueryCompilerSyntaxException("',' expected", this.p.getIndex(), this.p.getInput());
                }
                Node primaryNode;
                Node primaryRootNode;
                for (primaryNode = (primaryRootNode = this.stack.pop()); primaryNode.getFirstChild() != null; primaryNode = primaryNode.getFirstChild()) {}
                primaryNode.appendChildNode(invokeNode);
                this.stack.push(primaryRootNode);
                return true;
            }
            else if (method.equalsIgnoreCase("HOUR")) {
                final Node invokeNode = new Node(NodeType.INVOKE, "getHour");
                this.processExpression();
                if (!this.p.parseChar(')')) {
                    throw new QueryCompilerSyntaxException("',' expected", this.p.getIndex(), this.p.getInput());
                }
                Node primaryNode;
                Node primaryRootNode;
                for (primaryNode = (primaryRootNode = this.stack.pop()); primaryNode.getFirstChild() != null; primaryNode = primaryNode.getFirstChild()) {}
                primaryNode.appendChildNode(invokeNode);
                this.stack.push(primaryRootNode);
                return true;
            }
            else if (method.equalsIgnoreCase("MINUTE")) {
                final Node invokeNode = new Node(NodeType.INVOKE, "getMinute");
                this.processExpression();
                if (!this.p.parseChar(')')) {
                    throw new QueryCompilerSyntaxException("',' expected", this.p.getIndex(), this.p.getInput());
                }
                Node primaryNode;
                Node primaryRootNode;
                for (primaryNode = (primaryRootNode = this.stack.pop()); primaryNode.getFirstChild() != null; primaryNode = primaryNode.getFirstChild()) {}
                primaryNode.appendChildNode(invokeNode);
                this.stack.push(primaryRootNode);
                return true;
            }
            else if (method.equalsIgnoreCase("SECOND")) {
                final Node invokeNode = new Node(NodeType.INVOKE, "getSecond");
                this.processExpression();
                if (!this.p.parseChar(')')) {
                    throw new QueryCompilerSyntaxException("',' expected", this.p.getIndex(), this.p.getInput());
                }
                Node primaryNode;
                Node primaryRootNode;
                for (primaryNode = (primaryRootNode = this.stack.pop()); primaryNode.getFirstChild() != null; primaryNode = primaryNode.getFirstChild()) {}
                primaryNode.appendChildNode(invokeNode);
                this.stack.push(primaryRootNode);
                return true;
            }
            else {
                if (!method.equalsIgnoreCase("FUNCTION")) {
                    final Node node = new Node(NodeType.INVOKE, method);
                    if (!this.p.parseChar(')')) {
                        do {
                            this.processExpression();
                            node.addProperty(this.stack.pop());
                        } while (this.p.parseChar(','));
                        if (!this.p.parseChar(')')) {
                            throw new QueryCompilerSyntaxException("')' expected", this.p.getIndex(), this.p.getInput());
                        }
                    }
                    this.stack.push(node);
                    return true;
                }
                this.processExpression();
                final Node sqlFunctionNode = this.stack.pop();
                final Node invokeNode2 = new Node(NodeType.INVOKE, "SQL_function");
                invokeNode2.addProperty(sqlFunctionNode);
                if (this.p.parseChar(',')) {
                    do {
                        this.processExpression();
                        invokeNode2.addProperty(this.stack.pop());
                    } while (this.p.parseChar(','));
                }
                if (!this.p.parseChar(')')) {
                    throw new QueryCompilerSyntaxException("')' expected", this.p.getIndex(), this.p.getInput());
                }
                this.stack.push(invokeNode2);
                return true;
            }
        }
    }
    
    protected boolean processLiteral() {
        Object litValue = null;
        final boolean single_quote_next = this.p.nextIsSingleQuote();
        final String sLiteral;
        if ((sLiteral = this.p.parseStringLiteral()) != null) {
            if (sLiteral.length() == 1 && single_quote_next) {
                litValue = sLiteral.charAt(0);
            }
            else {
                litValue = sLiteral;
            }
        }
        else {
            final BigDecimal fLiteral;
            if ((fLiteral = this.p.parseFloatingPointLiteral()) != null) {
                litValue = fLiteral;
            }
            else {
                final BigInteger iLiteral;
                if ((iLiteral = this.p.parseIntegerLiteral()) != null) {
                    litValue = iLiteral.longValue();
                }
                else {
                    final Boolean bLiteral;
                    if ((bLiteral = this.p.parseBooleanLiteralIgnoreCase()) != null) {
                        litValue = bLiteral;
                    }
                    else if (!this.p.parseNullLiteralIgnoreCase()) {
                        return false;
                    }
                }
            }
        }
        this.stack.push(new Node(NodeType.LITERAL, litValue));
        return true;
    }
    
    private boolean processIdentifier() {
        final String id = this.p.parseIdentifier();
        if (id == null || id.length() == 0) {
            return false;
        }
        final char first = id.charAt(0);
        if (first == '?') {
            final String paramName = id.substring(1);
            final Node node = new ParameterNode(NodeType.PARAMETER, paramName, this.parameterPosition);
            ++this.parameterPosition;
            this.stack.push(node);
            return true;
        }
        if (first == ':') {
            final Node node2 = new ParameterNode(NodeType.PARAMETER, id.substring(1), this.parameterPosition);
            ++this.parameterPosition;
            this.stack.push(node2);
            return true;
        }
        final Node node2 = new Node(NodeType.IDENTIFIER, id);
        this.stack.push(node2);
        return true;
    }
    
    static {
        JPQLParser.paramPrefixes = ":?";
    }
}
