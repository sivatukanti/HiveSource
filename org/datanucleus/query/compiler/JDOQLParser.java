// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.compiler;

import org.datanucleus.ClassConstants;
import org.datanucleus.query.node.ParameterNode;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.util.Arrays;
import org.datanucleus.exceptions.NucleusException;
import java.util.StringTokenizer;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.query.JDOQLQueryHelper;
import org.datanucleus.util.StringUtils;
import java.util.List;
import org.datanucleus.query.node.NodeType;
import org.datanucleus.store.query.QueryCompilerSyntaxException;
import java.util.Map;
import java.util.ArrayList;
import org.datanucleus.query.node.Node;
import java.util.Stack;
import org.datanucleus.util.Localiser;

public class JDOQLParser implements Parser
{
    protected static final Localiser LOCALISER;
    private static String[] jdoqlMethodNames;
    private ParameterType paramType;
    private String jdoqlMode;
    private Lexer p;
    private Stack<Node> stack;
    private static String paramPrefixes;
    private boolean allowSingleEquals;
    private ArrayList<Object> parameterNameList;
    
    public JDOQLParser(final Map options) {
        this.paramType = ParameterType.IMPLICIT;
        this.jdoqlMode = "DataNucleus";
        this.stack = new Stack<Node>();
        this.allowSingleEquals = false;
        this.parameterNameList = null;
        if (options != null && options.containsKey("jdoql.level")) {
            this.jdoqlMode = options.get("jdoql.level");
        }
        if (options != null && options.containsKey("explicitParameters")) {
            this.paramType = ParameterType.EXPLICIT;
        }
    }
    
    public void allowSingleEquals(final boolean flag) {
        this.allowSingleEquals = flag;
    }
    
    @Override
    public Node parse(final String expression) {
        this.p = new Lexer(expression, JDOQLParser.paramPrefixes, true);
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
        this.p = new Lexer(expression, JDOQLParser.paramPrefixes, true);
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
        this.p = new Lexer(expression, JDOQLParser.paramPrefixes, true);
        this.stack = new Stack<Node>();
        return this.processFromExpression();
    }
    
    private Node[] processFromExpression() {
        this.processExpression();
        Node id = this.stack.pop();
        final StringBuilder className = new StringBuilder(id.getNodeValue().toString());
        while (id.getChildNodes().size() > 0) {
            id = id.getFirstChild();
            className.append(".").append(id.getNodeValue().toString());
        }
        String alias = this.p.parseIdentifier();
        if (alias != null && alias.equalsIgnoreCase("AS")) {
            alias = this.p.parseIdentifier();
        }
        if (alias == null) {
            alias = "this";
        }
        final Node classNode = new Node(NodeType.CLASS, className.toString());
        final Node aliasNode = new Node(NodeType.NAME, alias);
        classNode.insertChildNode(aliasNode);
        this.stack.push(classNode);
        return new Node[] { classNode };
    }
    
    @Override
    public Node[] parseUpdate(final String expression) {
        return null;
    }
    
    @Override
    public Node[] parseOrder(final String expression) {
        this.p = new Lexer(expression, JDOQLParser.paramPrefixes, true);
        this.stack = new Stack<Node>();
        return this.processOrderExpression();
    }
    
    @Override
    public Node[] parseResult(final String expression) {
        this.p = new Lexer(expression, JDOQLParser.paramPrefixes, true);
        this.stack = new Stack<Node>();
        final List nodes = new ArrayList();
        do {
            this.processExpression();
            final Node expr = this.stack.pop();
            String alias = this.p.parseIdentifier();
            if (alias != null && alias.equalsIgnoreCase("AS")) {
                alias = this.p.parseIdentifier();
            }
            if (alias != null) {
                final Node aliasNode = new Node(NodeType.NAME, alias);
                expr.appendChildNode(aliasNode);
            }
            nodes.add(expr);
        } while (this.p.parseString(","));
        return nodes.toArray(new Node[nodes.size()]);
    }
    
    @Override
    public Node[] parseTupple(final String expression) {
        this.p = new Lexer(expression, JDOQLParser.paramPrefixes, true);
        this.stack = new Stack<Node>();
        final List nodes = new ArrayList();
        do {
            this.processExpression();
            final Node expr = this.stack.pop();
            nodes.add(expr);
        } while (this.p.parseString(","));
        return nodes.toArray(new Node[nodes.size()]);
    }
    
    @Override
    public Node[][] parseVariables(final String expression) {
        this.p = new Lexer(expression, JDOQLParser.paramPrefixes, true);
        final List nodes = new ArrayList();
        while (!StringUtils.isWhitespace(this.p.remaining())) {
            this.processPrimary();
            if (this.stack.isEmpty()) {
                throw new QueryCompilerSyntaxException("Parsing variable list and expected variable type", this.p.getIndex(), this.p.getInput());
            }
            if (!this.processIdentifier()) {
                throw new QueryCompilerSyntaxException("Parsing variable list and expected variable name", this.p.getIndex(), this.p.getInput());
            }
            final Node nodeVariable = this.stack.pop();
            final String varName = (String)nodeVariable.getNodeValue();
            if (!JDOQLQueryHelper.isValidJavaIdentifierForJDOQL(varName)) {
                throw new NucleusUserException(JDOQLParser.LOCALISER.msg("021105", varName));
            }
            final Node nodeType = this.stack.pop();
            nodes.add(new Node[] { nodeType, nodeVariable });
            if (!this.p.parseString(";")) {
                return nodes.toArray(new Node[nodes.size()][2]);
            }
        }
        return nodes.toArray(new Node[nodes.size()][2]);
    }
    
    @Override
    public Node[][] parseParameters(final String expression) {
        final List nodes = new ArrayList();
        final StringTokenizer tokeniser = new StringTokenizer(expression, ",");
        while (tokeniser.hasMoreTokens()) {
            final String token = tokeniser.nextToken();
            final StringTokenizer subTokeniser = new StringTokenizer(token, " ");
            if (subTokeniser.countTokens() != 2) {
                throw new QueryCompilerSyntaxException(JDOQLParser.LOCALISER.msg("021101", expression));
            }
            final String classDecl = subTokeniser.nextToken();
            final String parameterName = subTokeniser.nextToken();
            final Node declNode = new Node(NodeType.IDENTIFIER, classDecl);
            final Node nameNode = new Node(NodeType.IDENTIFIER, parameterName);
            nodes.add(new Node[] { declNode, nameNode });
        }
        return nodes.toArray(new Node[nodes.size()][2]);
    }
    
    private Node[] processOrderExpression() {
        final List nodes = new ArrayList();
        do {
            this.processExpression();
            Node directionNode = null;
            if (this.p.parseString("ascending") || this.p.parseString("asc") || this.p.parseString("ASCENDING") || this.p.parseString("ASC")) {
                directionNode = new Node(NodeType.OPERATOR, "ascending");
            }
            else if (this.p.parseString("descending") || this.p.parseString("desc") || this.p.parseString("DESCENDING") || this.p.parseString("DESC")) {
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
        } while (this.p.parseChar(','));
        return nodes.toArray(new Node[nodes.size()]);
    }
    
    private Node processExpression() {
        this.processConditionalOrExpression();
        return this.stack.peek();
    }
    
    private void processConditionalOrExpression() {
        this.processConditionalAndExpression();
        while (this.p.parseString("||")) {
            this.processConditionalAndExpression();
            final Node expr = new Node(NodeType.OPERATOR, "||");
            expr.insertChildNode(this.stack.pop());
            expr.insertChildNode(this.stack.pop());
            this.stack.push(expr);
        }
    }
    
    private void processConditionalAndExpression() {
        this.processInclusiveOrExpression();
        while (this.p.parseString("&&")) {
            this.processInclusiveOrExpression();
            final Node expr = new Node(NodeType.OPERATOR, "&&");
            expr.insertChildNode(this.stack.pop());
            expr.insertChildNode(this.stack.pop());
            this.stack.push(expr);
        }
    }
    
    private void processInclusiveOrExpression() {
        this.processExclusiveOrExpression();
        while (this.p.parseChar('|', '|')) {
            this.processExclusiveOrExpression();
            final Node expr = new Node(NodeType.OPERATOR, "|");
            expr.insertChildNode(this.stack.pop());
            expr.insertChildNode(this.stack.pop());
            this.stack.push(expr);
        }
    }
    
    private void processExclusiveOrExpression() {
        this.processAndExpression();
        while (this.p.parseChar('^')) {
            this.processAndExpression();
            final Node expr = new Node(NodeType.OPERATOR, "^");
            expr.insertChildNode(this.stack.pop());
            expr.insertChildNode(this.stack.pop());
            this.stack.push(expr);
        }
    }
    
    private void processAndExpression() {
        this.processRelationalExpression();
        while (this.p.parseChar('&', '&')) {
            this.processRelationalExpression();
            final Node expr = new Node(NodeType.OPERATOR, "&");
            expr.insertChildNode(this.stack.pop());
            expr.insertChildNode(this.stack.pop());
            this.stack.push(expr);
        }
    }
    
    private void processRelationalExpression() {
        this.processAdditiveExpression();
        while (true) {
            if (this.p.parseString("==")) {
                this.processAdditiveExpression();
                final Node expr = new Node(NodeType.OPERATOR, "==");
                expr.insertChildNode(this.stack.pop());
                expr.insertChildNode(this.stack.pop());
                this.stack.push(expr);
            }
            else if (this.p.parseString("!=")) {
                this.processAdditiveExpression();
                final Node expr = new Node(NodeType.OPERATOR, "!=");
                expr.insertChildNode(this.stack.pop());
                expr.insertChildNode(this.stack.pop());
                this.stack.push(expr);
            }
            else if (this.p.parseString("=")) {
                if (!this.allowSingleEquals) {
                    throw new QueryCompilerSyntaxException("Invalid operator \"=\". Did you mean to use \"==\"?");
                }
                this.processAdditiveExpression();
                final Node expr = new Node(NodeType.OPERATOR, "==");
                expr.insertChildNode(this.stack.pop());
                expr.insertChildNode(this.stack.pop());
                this.stack.push(expr);
            }
            else if (this.p.parseString("<=")) {
                this.processAdditiveExpression();
                final Node expr = new Node(NodeType.OPERATOR, "<=");
                expr.insertChildNode(this.stack.pop());
                expr.insertChildNode(this.stack.pop());
                this.stack.push(expr);
            }
            else if (this.p.parseString(">=")) {
                this.processAdditiveExpression();
                final Node expr = new Node(NodeType.OPERATOR, ">=");
                expr.insertChildNode(this.stack.pop());
                expr.insertChildNode(this.stack.pop());
                this.stack.push(expr);
            }
            else if (this.p.parseChar('<')) {
                this.processAdditiveExpression();
                final Node expr = new Node(NodeType.OPERATOR, "<");
                expr.insertChildNode(this.stack.pop());
                expr.insertChildNode(this.stack.pop());
                this.stack.push(expr);
            }
            else if (this.p.parseChar('>')) {
                this.processAdditiveExpression();
                final Node expr = new Node(NodeType.OPERATOR, ">");
                expr.insertChildNode(this.stack.pop());
                expr.insertChildNode(this.stack.pop());
                this.stack.push(expr);
            }
            else {
                if (!this.p.parseString("instanceof")) {
                    return;
                }
                this.processAdditiveExpression();
                final Node expr = new Node(NodeType.OPERATOR, "instanceof");
                expr.insertChildNode(this.stack.pop());
                expr.insertChildNode(this.stack.pop());
                this.stack.push(expr);
            }
        }
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
            throw new QueryCompilerSyntaxException("Unsupported operator '++'");
        }
        if (this.p.parseString("--")) {
            throw new QueryCompilerSyntaxException("Unsupported operator '--'");
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
        else if (this.p.parseChar('~')) {
            this.processUnaryExpression();
            final Node expr = new Node(NodeType.OPERATOR, "~");
            expr.insertChildNode(this.stack.pop());
            this.stack.push(expr);
        }
        else if (this.p.parseChar('!')) {
            this.processUnaryExpression();
            final Node expr = new Node(NodeType.OPERATOR, "!");
            expr.insertChildNode(this.stack.pop());
            this.stack.push(expr);
        }
        else {
            this.processPrimary();
        }
    }
    
    protected void processPrimary() {
        if (this.p.parseString("DISTINCT ") || this.p.parseString("distinct")) {
            final Node distinctNode = new Node(NodeType.OPERATOR, "DISTINCT");
            this.processExpression();
            final Node identifierNode = this.stack.pop();
            distinctNode.appendChildNode(identifierNode);
            this.stack.push(distinctNode);
            return;
        }
        Node castNode = null;
        if (this.processCast()) {
            castNode = this.stack.pop();
        }
        if (this.processCreator()) {
            boolean endOfChain = false;
            while (!endOfChain) {
                if (this.p.parseChar('.')) {
                    if (!this.processMethod()) {
                        continue;
                    }
                    final Node invokeNode = this.stack.pop();
                    final Node invokedNode = this.stack.peek();
                    invokedNode.appendChildNode(invokeNode);
                }
                else {
                    endOfChain = true;
                }
            }
            if (castNode != null) {
                throw new NucleusException("Dont currently support compile of cast of creator expression");
            }
        }
        else if (this.processLiteral()) {
            boolean endOfChain = false;
            while (!endOfChain) {
                if (this.p.parseChar('.')) {
                    if (!this.processMethod()) {
                        continue;
                    }
                    final Node invokeNode = this.stack.pop();
                    final Node invokedNode = this.stack.peek();
                    invokedNode.appendChildNode(invokeNode);
                }
                else {
                    endOfChain = true;
                }
            }
            if (castNode != null) {
                throw new NucleusException("Dont currently support compile of cast of literal expression");
            }
        }
        else if (this.processMethod()) {
            if (castNode != null) {
                throw new NucleusException("Dont currently support compile of cast of static method call");
            }
        }
        else {
            if (!this.processArray()) {
                final int sizeBeforeBraceProcessing = this.stack.size();
                boolean braceProcessing = false;
                if (this.p.parseChar('(')) {
                    this.processExpression();
                    if (!this.p.parseChar(')')) {
                        throw new QueryCompilerSyntaxException("expected ')'", this.p.getIndex(), this.p.getInput());
                    }
                    if (!this.p.parseChar('.')) {
                        return;
                    }
                    braceProcessing = true;
                }
                if (!this.processMethod()) {
                    if (!this.processIdentifier()) {
                        throw new QueryCompilerSyntaxException("Method/Identifier expected", this.p.getIndex(), this.p.getInput());
                    }
                }
                int size = this.stack.size();
                if (braceProcessing) {
                    size = sizeBeforeBraceProcessing + 1;
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
                if (castNode != null) {
                    this.stack.peek().appendChildNode(castNode);
                }
                while (this.stack.size() > size) {
                    final Node top = this.stack.pop();
                    final Node peek = this.stack.peek();
                    final Node lastDescendant = this.getLastDescendantNodeForNode(peek);
                    if (lastDescendant != null) {
                        lastDescendant.appendChildNode(top);
                    }
                    else {
                        final Node primNode = new Node(NodeType.PRIMARY);
                        primNode.appendChildNode(peek);
                        primNode.appendChildNode(top);
                        this.stack.pop();
                        this.stack.push(primNode);
                    }
                }
                return;
            }
            boolean endOfChain = false;
            while (!endOfChain) {
                if (this.p.parseChar('.')) {
                    if (!this.processMethod()) {
                        continue;
                    }
                    final Node invokeNode = this.stack.pop();
                    final Node invokedNode = this.stack.peek();
                    invokedNode.appendChildNode(invokeNode);
                }
                else {
                    endOfChain = true;
                }
            }
            if (castNode != null) {
                throw new NucleusException("Dont currently support compile of cast of array expression");
            }
        }
    }
    
    private Node getLastDescendantNodeForNode(final Node node) {
        if (node == null) {
            return null;
        }
        if (node.getChildNodes() == null) {
            return node;
        }
        if (node.getChildNodes().size() > 1) {
            return null;
        }
        if (!node.hasNextChild()) {
            return node;
        }
        return this.getLastDescendantNodeForNode(node.getChildNode(0));
    }
    
    private boolean processCast() {
        final String typeName = this.p.parseCast();
        if (typeName == null) {
            return false;
        }
        final Node castNode = new Node(NodeType.CAST, typeName);
        this.stack.push(castNode);
        return true;
    }
    
    private boolean processCreator() {
        if (this.p.parseString("new ")) {
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
            final Node expr = this.stack.pop();
            final Node newExpr = new Node(NodeType.CREATOR);
            newExpr.insertChildNode(expr);
            this.stack.push(newExpr);
            return true;
        }
        return false;
    }
    
    private boolean processMethod() {
        final String method = this.p.parseMethod();
        if (method == null) {
            return false;
        }
        this.p.skipWS();
        this.p.parseChar('(');
        if (this.jdoqlMode.equals("JDO2") && Arrays.binarySearch(JDOQLParser.jdoqlMethodNames, method) < 0) {
            throw new QueryCompilerSyntaxException("Query uses method \"" + method + "\" but this is not a standard JDOQL method name");
        }
        final Node expr = new Node(NodeType.INVOKE, method);
        if (!this.p.parseChar(')')) {
            do {
                this.processExpression();
                expr.addProperty(this.stack.pop());
            } while (this.p.parseChar(','));
            if (!this.p.parseChar(')')) {
                throw new QueryCompilerSyntaxException("')' expected", this.p.getIndex(), this.p.getInput());
            }
        }
        this.stack.push(expr);
        return true;
    }
    
    private boolean processArray() {
        if (this.p.parseChar('{')) {
            final List<Node> elements = new ArrayList<Node>();
            while (!this.p.parseChar('}')) {
                this.processPrimary();
                final Node elementNode = this.stack.pop();
                elements.add(elementNode);
                if (this.p.parseChar('}')) {
                    break;
                }
                if (!this.p.parseChar(',')) {
                    throw new QueryCompilerSyntaxException("',' or '}' expected", this.p.getIndex(), this.p.getInput());
                }
            }
            final Node arrayNode = new Node(NodeType.ARRAY, elements);
            this.stack.push(arrayNode);
            if (this.p.parseString(".length")) {
                final Node lengthMethod = new Node(NodeType.INVOKE, "length");
                arrayNode.appendChildNode(lengthMethod);
            }
            return true;
        }
        return false;
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
                    if ((bLiteral = this.p.parseBooleanLiteral()) != null) {
                        litValue = bLiteral;
                    }
                    else if (!this.p.parseNullLiteral()) {
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
        if (id == null) {
            return false;
        }
        final char first = id.charAt(0);
        if (first != ':') {
            final Node expr = new Node(NodeType.IDENTIFIER, id);
            this.stack.push(expr);
            return true;
        }
        if (this.paramType == ParameterType.EXPLICIT) {
            throw new QueryCompilerSyntaxException("Explicit parameters defined for query, yet implicit parameter syntax (\"" + id + "\") found");
        }
        final String name = id.substring(1);
        final Node expr2 = new ParameterNode(NodeType.PARAMETER, name, this.getPositionFromParameterName(name));
        this.stack.push(expr2);
        return true;
    }
    
    private int getPositionFromParameterName(final Object name) {
        if (this.parameterNameList == null) {
            this.parameterNameList = new ArrayList<Object>(1);
        }
        int pos = this.parameterNameList.indexOf(name);
        if (pos == -1) {
            pos = this.parameterNameList.size();
            this.parameterNameList.add(name);
        }
        return pos;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
        JDOQLParser.jdoqlMethodNames = new String[] { "contains", "get", "containsKey", "containsValue", "isEmpty", "size", "toLowerCase", "toUpperCase", "indexOf", "matches", "substring", "startsWith", "endsWith", "getObjectId", "abs", "sqrt" };
        JDOQLParser.paramPrefixes = ":";
    }
    
    private enum ParameterType
    {
        IMPLICIT, 
        EXPLICIT;
    }
}
