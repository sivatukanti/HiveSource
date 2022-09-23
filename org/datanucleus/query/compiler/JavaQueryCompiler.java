// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.compiler;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.exceptions.NucleusException;
import java.lang.reflect.Field;
import org.datanucleus.query.expression.VariableExpression;
import org.datanucleus.query.expression.ParameterExpression;
import org.datanucleus.query.expression.PrimaryExpression;
import org.datanucleus.query.expression.PrimaryExpressionIsVariableException;
import org.datanucleus.query.expression.PrimaryExpressionIsClassStaticFieldException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.query.expression.Literal;
import org.datanucleus.query.expression.PrimaryExpressionIsClassLiteralException;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.AbstractClassMetaData;
import java.util.Iterator;
import java.util.List;
import org.datanucleus.query.expression.ExpressionCompiler;
import org.datanucleus.metadata.RelationType;
import org.datanucleus.util.StringUtils;
import org.datanucleus.query.symbol.Symbol;
import org.datanucleus.query.symbol.PropertySymbol;
import org.datanucleus.store.query.QueryCompilerSyntaxException;
import org.datanucleus.query.node.NodeType;
import org.datanucleus.query.node.Node;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.plugin.ConfigurationElement;
import java.util.HashMap;
import org.datanucleus.query.symbol.SymbolTable;
import org.datanucleus.util.Imports;
import java.util.Collection;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.metadata.MetaDataManager;
import java.util.Map;
import org.datanucleus.util.Localiser;
import org.datanucleus.query.symbol.SymbolResolver;

public abstract class JavaQueryCompiler implements SymbolResolver
{
    protected static final Localiser LOCALISER;
    protected JavaQueryCompiler parentCompiler;
    protected Map<Object, String> parameterSubtitutionMap;
    protected int parameterSubstitutionNumber;
    protected final MetaDataManager metaDataManager;
    protected final ClassLoaderResolver clr;
    protected boolean caseSensitiveAliases;
    protected Class candidateClass;
    protected String candidateAlias;
    protected String candidateAliasOrig;
    protected String from;
    protected Collection candidates;
    protected String update;
    protected String filter;
    protected String ordering;
    protected String parameters;
    protected String variables;
    protected String grouping;
    protected String having;
    protected String result;
    protected Imports imports;
    protected SymbolTable symtbl;
    protected Parser parser;
    protected Map<String, String> queryMethodAliasByPrefix;
    
    public JavaQueryCompiler(final MetaDataManager metaDataManager, final ClassLoaderResolver clr, final String from, final Class candidateClass, final Collection candidates, final String filter, final Imports imports, final String ordering, final String result, final String grouping, final String having, final String params, final String variables, final String update) {
        this.parameterSubstitutionNumber = 0;
        this.caseSensitiveAliases = true;
        this.candidateAlias = "this";
        this.candidateAliasOrig = null;
        this.queryMethodAliasByPrefix = null;
        this.metaDataManager = metaDataManager;
        this.clr = clr;
        final ConfigurationElement[] queryMethodAliases = metaDataManager.getNucleusContext().getPluginManager().getConfigurationElementsForExtension("org.datanucleus.query_method_prefix", null, (String)null);
        if (queryMethodAliases != null && queryMethodAliases.length > 0) {
            this.queryMethodAliasByPrefix = new HashMap<String, String>();
            for (int i = 0; i < queryMethodAliases.length; ++i) {
                this.queryMethodAliasByPrefix.put(queryMethodAliases[i].getAttribute("prefix"), queryMethodAliases[i].getAttribute("alias"));
            }
        }
        this.from = from;
        this.candidateClass = candidateClass;
        this.candidates = candidates;
        this.filter = filter;
        this.result = result;
        this.grouping = grouping;
        this.having = having;
        this.ordering = ordering;
        this.parameters = params;
        this.variables = variables;
        this.update = update;
        if ((this.imports = imports) == null) {
            this.imports = new Imports();
            if (candidateClass != null) {
                this.imports.importClass(candidateClass.getName());
                this.imports.importPackage(candidateClass.getName());
            }
        }
    }
    
    public abstract String getLanguage();
    
    public void setLinkToParentQuery(final JavaQueryCompiler parentCompiler, final Map paramSubstitutionMap) {
        this.parentCompiler = parentCompiler;
        this.parameterSubtitutionMap = (Map<Object, String>)paramSubstitutionMap;
    }
    
    public abstract QueryCompilation compile(final Map p0, final Map p1);
    
    public void compileCandidatesParametersVariables(final Map parameters) {
        this.compileCandidates();
        this.compileVariables();
        this.compileParameters();
    }
    
    protected Expression[] compileFrom() {
        if (this.from == null) {
            return null;
        }
        final Node[] node = this.parser.parseFrom(this.from);
        final Expression[] expr = new Expression[node.length];
        for (int i = 0; i < node.length; ++i) {
            final String className = (String)node[i].getNodeValue();
            String classAlias = null;
            Class cls = null;
            if (this.parentCompiler != null) {
                cls = this.getClassForSubqueryClassExpression(className);
            }
            else {
                cls = this.resolveClass(className);
            }
            final List children = node[i].getChildNodes();
            for (int j = 0; j < children.size(); ++j) {
                final Node child = children.get(j);
                if (child.getNodeType() == NodeType.NAME) {
                    classAlias = (String)child.getNodeValue();
                }
            }
            if (i == 0 && classAlias == null) {
                throw new QueryCompilerSyntaxException("FROM clause of query has class " + cls.getName() + " but no alias");
            }
            if (classAlias != null) {
                if (i == 0) {
                    this.candidateClass = cls;
                    if (this.parentCompiler != null && this.parentCompiler.candidateAlias.equals(classAlias)) {
                        this.candidateAliasOrig = classAlias;
                        this.candidateAlias = "sub_" + this.candidateAlias;
                        classAlias = this.candidateAlias;
                        this.swapCandidateAliasNodeName(node[i].getChildNode(0));
                    }
                    else {
                        this.candidateAlias = classAlias;
                    }
                }
                if (this.symtbl.getSymbol(classAlias) == null) {
                    this.symtbl.addSymbol(new PropertySymbol(classAlias, cls));
                }
            }
            for (final Node childNode : node[i].getChildNodes()) {
                if (childNode.getNodeType() == NodeType.OPERATOR) {
                    Node joinedNode = childNode.getFirstChild();
                    final String joinedAlias = (String)joinedNode.getNodeValue();
                    final Symbol joinedSym = this.caseSensitiveAliases ? this.symtbl.getSymbol(joinedAlias) : this.symtbl.getSymbolIgnoreCase(joinedAlias);
                    if (joinedSym == null) {
                        throw new QueryCompilerSyntaxException("FROM clause has identifier " + joinedNode.getNodeValue() + " but this is unknown");
                    }
                    AbstractClassMetaData joinedCmd = this.metaDataManager.getMetaDataForClass(joinedSym.getValueType(), this.clr);
                    Class joinedCls = joinedSym.getValueType();
                    while (joinedNode.getFirstChild() != null) {
                        joinedNode = joinedNode.getFirstChild();
                        final String joinedMember = (String)joinedNode.getNodeValue();
                        final String[] joinedMembers = joinedMember.contains(".") ? StringUtils.split(joinedMember, ".") : new String[] { joinedMember };
                        for (int k = 0; k < joinedMembers.length; ++k) {
                            final AbstractMemberMetaData mmd = joinedCmd.getMetaDataForMember(joinedMembers[k]);
                            if (mmd == null) {
                                throw new QueryCompilerSyntaxException("FROM clause has reference to " + joinedCmd.getFullClassName() + "." + joinedMembers[k] + " but it doesn't exist!");
                            }
                            final RelationType relationType = mmd.getRelationType(this.clr);
                            if (relationType == RelationType.ONE_TO_ONE_UNI || relationType == RelationType.ONE_TO_ONE_BI || relationType == RelationType.MANY_TO_ONE_BI) {
                                joinedCls = mmd.getType();
                                joinedCmd = this.metaDataManager.getMetaDataForClass(joinedCls, this.clr);
                            }
                            else if (relationType == RelationType.ONE_TO_MANY_UNI || relationType == RelationType.ONE_TO_MANY_BI || relationType == RelationType.MANY_TO_MANY_BI) {
                                if (mmd.hasCollection()) {
                                    joinedCmd = mmd.getCollection().getElementClassMetaData(this.clr, this.metaDataManager);
                                    joinedCls = this.clr.classForName(joinedCmd.getFullClassName());
                                }
                                else if (mmd.hasArray()) {
                                    joinedCmd = mmd.getArray().getElementClassMetaData(this.clr, this.metaDataManager);
                                    joinedCls = this.clr.classForName(joinedCmd.getFullClassName());
                                }
                            }
                        }
                    }
                    final Node aliasNode = childNode.getNextChild();
                    if (aliasNode.getNodeType() != NodeType.NAME) {
                        continue;
                    }
                    this.symtbl.addSymbol(new PropertySymbol((String)aliasNode.getNodeValue(), joinedCls));
                }
            }
            boolean classIsExpression = false;
            final String[] tokens = StringUtils.split(className, ".");
            if (this.symtbl.getParentSymbolTable() != null && this.symtbl.getParentSymbolTable().hasSymbol(tokens[0])) {
                classIsExpression = true;
            }
            final ExpressionCompiler comp = new ExpressionCompiler();
            comp.setSymbolTable(this.symtbl);
            comp.setMethodAliases(this.queryMethodAliasByPrefix);
            expr[i] = comp.compileFromExpression(node[i], classIsExpression);
            if (expr[i] != null) {
                expr[i].bind(this.symtbl);
            }
        }
        return expr;
    }
    
    private Class getClassForSubqueryClassExpression(final String classExpr) {
        if (classExpr == null) {
            return null;
        }
        final String[] tokens = StringUtils.split(classExpr, ".");
        Class cls = null;
        if (tokens[0].equalsIgnoreCase(this.parentCompiler.candidateAlias)) {
            cls = this.parentCompiler.candidateClass;
        }
        else {
            final Symbol sym = this.parentCompiler.symtbl.getSymbolIgnoreCase(tokens[0]);
            if (sym == null) {
                return this.resolveClass(classExpr);
            }
            cls = sym.getValueType();
        }
        AbstractClassMetaData cmd = this.metaDataManager.getMetaDataForClass(cls, this.clr);
        for (int i = 1; i < tokens.length; ++i) {
            final AbstractMemberMetaData mmd = cmd.getMetaDataForMember(tokens[i]);
            final RelationType relationType = mmd.getRelationType(this.clr);
            if (relationType == RelationType.ONE_TO_ONE_BI || relationType == RelationType.ONE_TO_ONE_UNI || relationType == RelationType.MANY_TO_ONE_BI) {
                cls = mmd.getType();
            }
            else if (relationType == RelationType.ONE_TO_MANY_UNI || relationType == RelationType.ONE_TO_MANY_BI || relationType == RelationType.MANY_TO_MANY_BI) {
                if (mmd.hasCollection()) {
                    cls = this.clr.classForName(mmd.getCollection().getElementType());
                }
                else if (mmd.hasMap()) {
                    cls = this.clr.classForName(mmd.getMap().getValueType());
                }
                else if (mmd.hasArray()) {
                    cls = this.clr.classForName(mmd.getArray().getElementType());
                }
            }
            if (i < tokens.length - 1) {
                cmd = this.metaDataManager.getMetaDataForClass(cls, this.clr);
            }
        }
        return cls;
    }
    
    private void compileCandidates() {
        if (this.symtbl.getSymbol(this.candidateAlias) == null) {
            if (this.parentCompiler != null && this.parentCompiler.candidateAlias.equals(this.candidateAlias)) {
                this.candidateAliasOrig = this.candidateAlias;
                this.candidateAlias = "sub_" + this.candidateAlias;
            }
            final PropertySymbol symbol = new PropertySymbol(this.candidateAlias, this.candidateClass);
            this.symtbl.addSymbol(symbol);
        }
    }
    
    public Expression[] compileUpdate() {
        if (this.update == null) {
            return null;
        }
        final Node[] node = this.parser.parseTupple(this.update);
        final Expression[] expr = new Expression[node.length];
        for (int i = 0; i < node.length; ++i) {
            final ExpressionCompiler comp = new ExpressionCompiler();
            comp.setSymbolTable(this.symtbl);
            comp.setMethodAliases(this.queryMethodAliasByPrefix);
            (expr[i] = comp.compileExpression(node[i])).bind(this.symtbl);
        }
        return expr;
    }
    
    public Expression compileFilter() {
        if (this.filter != null) {
            Node node = this.parser.parse(this.filter);
            if (this.candidateAliasOrig != null) {
                this.swapCandidateAliasNodeName(node);
            }
            if (this.parameterSubtitutionMap != null) {
                node = this.swapSubqueryParameters(node);
            }
            final ExpressionCompiler comp = new ExpressionCompiler();
            comp.setSymbolTable(this.symtbl);
            comp.setMethodAliases(this.queryMethodAliasByPrefix);
            final Expression expr = comp.compileExpression(node);
            expr.bind(this.symtbl);
            return expr;
        }
        return null;
    }
    
    protected void swapCandidateAliasNodeName(final Node node) {
        if (node == null) {
            return;
        }
        switch (node.getNodeType()) {
            case IDENTIFIER: {
                if (node.getNodeValue().equals(this.candidateAliasOrig)) {
                    node.setNodeValue(this.candidateAlias);
                    break;
                }
                break;
            }
            case OPERATOR: {
                while (node.hasNextChild()) {
                    final Node childNode = node.getNextChild();
                    this.swapCandidateAliasNodeName(childNode);
                }
                break;
            }
            case INVOKE: {
                if (node.hasProperties()) {
                    for (final Node propNode : node.getProperties()) {
                        this.swapCandidateAliasNodeName(propNode);
                    }
                    break;
                }
                break;
            }
            case CAST: {
                final Node childNode = node.getChildNode(0);
                this.swapCandidateAliasNodeName(childNode);
                break;
            }
            case NAME: {
                if (node.getNodeValue().equals(this.candidateAliasOrig)) {
                    node.setNodeValue(this.candidateAlias);
                    break;
                }
                break;
            }
        }
    }
    
    protected Node swapSubqueryParameters(final Node node) {
        if (node == null || this.parameterSubtitutionMap == null) {
            return null;
        }
        Node swapNode = null;
        switch (node.getNodeType()) {
            case PARAMETER: {
                final Object paramName = node.getNodeValue();
                if (this.parameterSubtitutionMap.containsKey(paramName)) {
                    final String paramValue = this.parameterSubtitutionMap.get(paramName);
                    swapNode = this.parser.parse(paramValue);
                }
                else {
                    final String paramValue = this.parameterSubtitutionMap.get(this.parameterSubstitutionNumber++);
                    swapNode = this.parser.parse(paramValue);
                }
                return swapNode;
            }
            case OPERATOR: {
                final List childNodes = node.getChildNodes();
                for (int i = 0; i < childNodes.size(); ++i) {
                    final Node swappedNode = this.swapSubqueryParameters(childNodes.get(i));
                    node.removeChildNode(childNodes.get(i));
                    node.insertChildNode(swappedNode, i);
                }
                break;
            }
            case INVOKE: {
                if (node.hasProperties()) {
                    final List<Node> propNodes = node.getProperties();
                    for (int j = 0; j < propNodes.size(); ++j) {
                        final Node propNode = propNodes.get(j);
                        swapNode = this.swapSubqueryParameters(propNode);
                        if (swapNode != propNode) {
                            node.setPropertyAtPosition(j, swapNode);
                        }
                    }
                    break;
                }
                break;
            }
        }
        return node;
    }
    
    public Expression[] compileResult() {
        if (this.result == null) {
            return null;
        }
        final Node[] node = this.parser.parseResult(this.result);
        final Expression[] expr = new Expression[node.length];
        for (int i = 0; i < node.length; ++i) {
            final ExpressionCompiler comp = new ExpressionCompiler();
            comp.setSymbolTable(this.symtbl);
            comp.setMethodAliases(this.queryMethodAliasByPrefix);
            String alias = null;
            Node aliasNode = null;
            while (node[i].hasNextChild()) {
                final Node childNode = node[i].getNextChild();
                if (childNode.getNodeType() == NodeType.NAME) {
                    aliasNode = childNode;
                }
            }
            if (aliasNode != null) {
                alias = (String)aliasNode.getNodeValue();
                node[i].removeChildNode(aliasNode);
            }
            if (this.candidateAliasOrig != null) {
                this.swapCandidateAliasNodeName(node[i]);
            }
            if (this.parameterSubtitutionMap != null) {
                node[i] = this.swapSubqueryParameters(node[i]);
            }
            expr[i] = comp.compileExpression(node[i]);
            if (alias != null) {
                expr[i].setAlias(alias);
            }
            try {
                expr[i].bind(this.symtbl);
            }
            catch (PrimaryExpressionIsClassLiteralException peil) {
                (expr[i] = peil.getLiteral()).bind(this.symtbl);
            }
            catch (PrimaryExpressionIsClassStaticFieldException peil2) {
                final Field fld = peil2.getLiteralField();
                try {
                    final Object value = fld.get(null);
                    (expr[i] = new Literal(value)).bind(this.symtbl);
                }
                catch (Exception e) {
                    throw new NucleusUserException("Error processing static field " + fld.getName(), e);
                }
            }
            catch (PrimaryExpressionIsVariableException pive) {
                (expr[i] = pive.getVariableExpression()).bind(this.symtbl);
            }
            if (expr[i] instanceof PrimaryExpression) {
                final String id = ((PrimaryExpression)expr[i]).getId();
                if (this.isKeyword(id)) {
                    throw new NucleusUserException(JavaQueryCompiler.LOCALISER.msg("021052", this.getLanguage(), id));
                }
            }
            else if (expr[i] instanceof ParameterExpression) {
                final String id = ((ParameterExpression)expr[i]).getId();
                if (this.isKeyword(id)) {
                    throw new NucleusUserException(JavaQueryCompiler.LOCALISER.msg("021052", this.getLanguage(), id));
                }
            }
            else if (expr[i] instanceof VariableExpression) {
                final String id = ((VariableExpression)expr[i]).getId();
                if (this.isKeyword(id)) {
                    throw new NucleusUserException(JavaQueryCompiler.LOCALISER.msg("021052", this.getLanguage(), id));
                }
            }
        }
        return expr;
    }
    
    public Expression[] compileGrouping() {
        if (this.grouping == null) {
            return null;
        }
        final Node[] node = this.parser.parseTupple(this.grouping);
        final Expression[] expr = new Expression[node.length];
        for (int i = 0; i < node.length; ++i) {
            if (this.candidateAliasOrig != null) {
                this.swapCandidateAliasNodeName(node[i]);
            }
            if (this.parameterSubtitutionMap != null) {
                node[i] = this.swapSubqueryParameters(node[i]);
            }
            final ExpressionCompiler comp = new ExpressionCompiler();
            comp.setSymbolTable(this.symtbl);
            comp.setMethodAliases(this.queryMethodAliasByPrefix);
            (expr[i] = comp.compileExpression(node[i])).bind(this.symtbl);
        }
        return expr;
    }
    
    public Expression compileHaving() {
        if (this.having == null) {
            return null;
        }
        Node node = this.parser.parse(this.having);
        if (this.candidateAliasOrig != null) {
            this.swapCandidateAliasNodeName(node);
        }
        if (this.parameterSubtitutionMap != null) {
            node = this.swapSubqueryParameters(node);
        }
        final ExpressionCompiler comp = new ExpressionCompiler();
        comp.setSymbolTable(this.symtbl);
        comp.setMethodAliases(this.queryMethodAliasByPrefix);
        final Expression expr = comp.compileExpression(node);
        expr.bind(this.symtbl);
        return expr;
    }
    
    private void compileVariables() {
        if (this.variables == null) {
            return;
        }
        final Node[][] node = this.parser.parseVariables(this.variables);
        for (int i = 0; i < node.length; ++i) {
            final String varName = (String)node[i][1].getNodeValue();
            if (this.isKeyword(varName) || varName.equals(this.candidateAlias)) {
                throw new NucleusUserException(JavaQueryCompiler.LOCALISER.msg("021052", this.getLanguage(), varName));
            }
            final Symbol varSym = this.symtbl.getSymbol(varName);
            final Class nodeCls = this.resolveClass(node[i][0].getNodeChildId());
            if (varSym != null) {
                if (nodeCls != null) {
                    varSym.setValueType(nodeCls);
                }
            }
            else {
                final PropertySymbol sym = new PropertySymbol(varName, nodeCls);
                sym.setType(2);
                this.symtbl.addSymbol(sym);
            }
        }
    }
    
    private void compileParameters() {
        if (this.parameters == null) {
            return;
        }
        final Node[][] node = this.parser.parseParameters(this.parameters);
        for (int i = 0; i < node.length; ++i) {
            final String paramName = (String)node[i][1].getNodeValue();
            if (this.isKeyword(paramName) || paramName.equals(this.candidateAlias)) {
                throw new NucleusUserException(JavaQueryCompiler.LOCALISER.msg("021052", this.getLanguage(), paramName));
            }
            final Symbol paramSym = this.symtbl.getSymbol(paramName);
            final Class nodeCls = this.resolveClass(node[i][0].getNodeChildId());
            if (paramSym == null) {
                final PropertySymbol sym = new PropertySymbol(paramName, nodeCls);
                sym.setType(1);
                this.symtbl.addSymbol(sym);
            }
        }
    }
    
    public Expression[] compileOrdering() {
        if (this.ordering == null) {
            return null;
        }
        final Node[] node = this.parser.parseOrder(this.ordering);
        final Expression[] expr = new Expression[node.length];
        for (int i = 0; i < node.length; ++i) {
            if (this.candidateAliasOrig != null) {
                this.swapCandidateAliasNodeName(node[i]);
            }
            if (this.parameterSubtitutionMap != null) {
                node[i] = this.swapSubqueryParameters(node[i]);
            }
            final ExpressionCompiler comp = new ExpressionCompiler();
            comp.setSymbolTable(this.symtbl);
            comp.setMethodAliases(this.queryMethodAliasByPrefix);
            (expr[i] = comp.compileOrderExpression(node[i])).bind(this.symtbl);
        }
        return expr;
    }
    
    @Override
    public Class getPrimaryClass() {
        return this.candidateClass;
    }
    
    @Override
    public Class resolveClass(final String className) {
        if (this.imports != null) {
            try {
                final Class cls = this.imports.resolveClassDeclaration(className, this.clr, null);
                if (cls != null) {
                    return cls;
                }
            }
            catch (NucleusException ex) {}
        }
        final AbstractClassMetaData acmd = this.metaDataManager.getMetaDataForEntityName(className);
        if (acmd != null) {
            final String fullClassName = acmd.getFullClassName();
            if (fullClassName != null) {
                return this.clr.classForName(fullClassName);
            }
        }
        throw new ClassNotResolvedException("Class " + className + " for query has not been resolved. Check the query and any imports/aliases specification");
    }
    
    @Override
    public Class getType(final List tuples) {
        Class type = null;
        Symbol symbol = null;
        final String firstTuple = tuples.get(0);
        if (this.caseSensitiveSymbolNames()) {
            symbol = this.symtbl.getSymbol(firstTuple);
        }
        else {
            symbol = this.symtbl.getSymbol(firstTuple);
            if (symbol == null) {
                symbol = this.symtbl.getSymbol(firstTuple.toUpperCase());
            }
            if (symbol == null) {
                symbol = this.symtbl.getSymbol(firstTuple.toLowerCase());
            }
        }
        if (symbol != null) {
            type = symbol.getValueType();
            if (type == null) {
                throw new NucleusUserException("Cannot find type of " + (Object)tuples.get(0) + " since symbol has no type; implicit variable?");
            }
            for (int i = 1; i < tuples.size(); ++i) {
                type = this.getType(type, tuples.get(i));
            }
        }
        else {
            symbol = this.symtbl.getSymbol(this.candidateAlias);
            type = symbol.getValueType();
            for (int i = 0; i < tuples.size(); ++i) {
                type = this.getType(type, tuples.get(i));
            }
        }
        return type;
    }
    
    Class getType(final Class cls, final String fieldName) {
        final AbstractClassMetaData acmd = this.metaDataManager.getMetaDataForClass(cls, this.clr);
        if (acmd != null) {
            final AbstractMemberMetaData fmd = acmd.getMetaDataForMember(fieldName);
            if (fmd == null) {
                throw new NucleusUserException("Cannot access field " + fieldName + " on type " + cls.getName());
            }
            return fmd.getType();
        }
        else {
            final Field field = ClassUtils.getFieldForClass(cls, fieldName);
            if (field == null) {
                throw new NucleusUserException("Cannot access field " + fieldName + " on type " + cls.getName());
            }
            return field.getType();
        }
    }
    
    protected abstract boolean isKeyword(final String p0);
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
