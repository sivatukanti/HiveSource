// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate;

import java.io.Writer;
import java.io.StringWriter;
import antlr.collections.ASTEnumeration;
import antlr.Token;
import antlr.CommonAST;
import antlr.CommonToken;
import java.util.LinkedList;
import java.util.HashSet;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import antlr.collections.AST;
import antlr.TokenStreamException;
import antlr.RecognitionException;
import org.antlr.stringtemplate.language.ConditionalExpr;
import org.antlr.stringtemplate.language.ActionParser;
import java.io.Reader;
import org.antlr.stringtemplate.language.ActionLexer;
import java.lang.reflect.Constructor;
import antlr.TokenStream;
import org.antlr.stringtemplate.language.TemplateParser;
import java.io.StringReader;
import antlr.CharScanner;
import java.io.IOException;
import org.antlr.stringtemplate.language.NewlineRef;
import org.antlr.stringtemplate.language.Expr;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Collection;
import org.antlr.stringtemplate.language.ASTExpr;
import java.util.HashMap;
import org.antlr.stringtemplate.language.FormalArgument;
import java.util.Set;
import java.util.LinkedHashMap;
import org.antlr.stringtemplate.language.StringTemplateAST;
import java.util.Map;
import java.util.List;

public class StringTemplate
{
    public static final String VERSION = "3.2.1";
    public static final int REGION_IMPLICIT = 1;
    public static final int REGION_EMBEDDED = 2;
    public static final int REGION_EXPLICIT = 3;
    public static final String ANONYMOUS_ST_NAME = "anonymous";
    static boolean lintMode;
    protected List referencedAttributes;
    protected String name;
    private static int templateCounter;
    protected int templateID;
    protected StringTemplate enclosingInstance;
    protected Map argumentContext;
    protected StringTemplateAST argumentsAST;
    protected LinkedHashMap formalArguments;
    protected int numberOfDefaultArgumentValues;
    protected boolean passThroughAttributes;
    protected StringTemplateGroup nativeGroup;
    protected StringTemplateGroup group;
    protected int groupFileLine;
    StringTemplateErrorListener listener;
    protected String pattern;
    protected Map attributes;
    protected Map attributeRenderers;
    protected List chunks;
    protected int regionDefType;
    protected boolean isRegion;
    protected Set regions;
    public static StringTemplateGroup defaultGroup;
    
    private static synchronized int getNextTemplateCounter() {
        return ++StringTemplate.templateCounter;
    }
    
    public static void resetTemplateCounter() {
        StringTemplate.templateCounter = 0;
    }
    
    public StringTemplate() {
        this.referencedAttributes = null;
        this.name = "anonymous";
        this.templateID = getNextTemplateCounter();
        this.enclosingInstance = null;
        this.argumentContext = null;
        this.argumentsAST = null;
        this.formalArguments = FormalArgument.UNKNOWN;
        this.numberOfDefaultArgumentValues = 0;
        this.passThroughAttributes = false;
        this.listener = null;
        this.group = StringTemplate.defaultGroup;
    }
    
    public StringTemplate(final String template) {
        this(null, template);
    }
    
    public StringTemplate(final String template, final Class lexer) {
        this();
        this.setGroup(new StringTemplateGroup("defaultGroup", lexer));
        this.setTemplate(template);
    }
    
    public StringTemplate(final StringTemplateGroup group, final String template) {
        this();
        if (group != null) {
            this.setGroup(group);
        }
        this.setTemplate(template);
    }
    
    public StringTemplate(final StringTemplateGroup group, final String template, final HashMap attributes) {
        this(group, template);
        this.attributes = attributes;
    }
    
    protected void dup(final StringTemplate from, final StringTemplate to) {
        to.attributeRenderers = from.attributeRenderers;
        to.pattern = from.pattern;
        to.chunks = from.chunks;
        to.formalArguments = from.formalArguments;
        to.numberOfDefaultArgumentValues = from.numberOfDefaultArgumentValues;
        to.name = from.name;
        to.group = from.group;
        to.nativeGroup = from.nativeGroup;
        to.listener = from.listener;
        to.regions = from.regions;
        to.isRegion = from.isRegion;
        to.regionDefType = from.regionDefType;
    }
    
    public StringTemplate getInstanceOf() {
        StringTemplate t = null;
        if (this.nativeGroup != null) {
            t = this.nativeGroup.createStringTemplate();
        }
        else {
            t = this.group.createStringTemplate();
        }
        this.dup(this, t);
        return t;
    }
    
    public StringTemplate getEnclosingInstance() {
        return this.enclosingInstance;
    }
    
    public StringTemplate getOutermostEnclosingInstance() {
        if (this.enclosingInstance != null) {
            return this.enclosingInstance.getOutermostEnclosingInstance();
        }
        return this;
    }
    
    public void setEnclosingInstance(final StringTemplate enclosingInstance) {
        if (this == enclosingInstance) {
            throw new IllegalArgumentException("cannot embed template " + this.getName() + " in itself");
        }
        this.enclosingInstance = enclosingInstance;
    }
    
    public Map getArgumentContext() {
        return this.argumentContext;
    }
    
    public void setArgumentContext(final Map ac) {
        this.argumentContext = ac;
    }
    
    public StringTemplateAST getArgumentsAST() {
        return this.argumentsAST;
    }
    
    public void setArgumentsAST(final StringTemplateAST argumentsAST) {
        this.argumentsAST = argumentsAST;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getOutermostName() {
        if (this.enclosingInstance != null) {
            return this.enclosingInstance.getOutermostName();
        }
        return this.getName();
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public StringTemplateGroup getGroup() {
        return this.group;
    }
    
    public void setGroup(final StringTemplateGroup group) {
        this.group = group;
    }
    
    public StringTemplateGroup getNativeGroup() {
        return this.nativeGroup;
    }
    
    public void setNativeGroup(final StringTemplateGroup nativeGroup) {
        this.nativeGroup = nativeGroup;
    }
    
    public int getGroupFileLine() {
        if (this.enclosingInstance != null) {
            return this.enclosingInstance.getGroupFileLine();
        }
        return this.groupFileLine;
    }
    
    public void setGroupFileLine(final int groupFileLine) {
        this.groupFileLine = groupFileLine;
    }
    
    public void setTemplate(final String template) {
        this.pattern = template;
        this.breakTemplateIntoChunks();
    }
    
    public String getTemplate() {
        return this.pattern;
    }
    
    public void setErrorListener(final StringTemplateErrorListener listener) {
        this.listener = listener;
    }
    
    public StringTemplateErrorListener getErrorListener() {
        if (this.listener == null) {
            return this.group.getErrorListener();
        }
        return this.listener;
    }
    
    public void reset() {
        this.attributes = new HashMap();
    }
    
    public void setPredefinedAttributes() {
        if (!inLintMode()) {
            return;
        }
    }
    
    public void removeAttribute(final String name) {
        if (this.attributes != null) {
            this.attributes.remove(name);
        }
    }
    
    public void setAttribute(final String name, Object value) {
        if (value == null || name == null) {
            return;
        }
        if (name.indexOf(46) >= 0) {
            throw new IllegalArgumentException("cannot have '.' in attribute names");
        }
        if (this.attributes == null) {
            this.attributes = new HashMap();
        }
        if (value instanceof StringTemplate) {
            ((StringTemplate)value).setEnclosingInstance(this);
        }
        else {
            value = ASTExpr.convertArrayToList(value);
        }
        final Object o = this.attributes.get(name);
        if (o == null) {
            this.rawSetAttribute(this.attributes, name, value);
            return;
        }
        STAttributeList v = null;
        if (o.getClass() == STAttributeList.class) {
            v = (STAttributeList)o;
        }
        else if (o instanceof List) {
            final List listAttr = (List)o;
            v = new STAttributeList(listAttr.size());
            v.addAll(listAttr);
            this.rawSetAttribute(this.attributes, name, v);
        }
        else {
            v = new STAttributeList();
            this.rawSetAttribute(this.attributes, name, v);
            v.add(o);
        }
        if (value instanceof List) {
            if (v != value) {
                v.addAll((Collection<?>)value);
            }
        }
        else {
            v.add(value);
        }
    }
    
    public void setAttribute(final String name, final int value) {
        this.setAttribute(name, new Integer(value));
    }
    
    public void setAttribute(final String aggrSpec, final Object v1, final Object v2) {
        this.setAttribute(aggrSpec, new Object[] { v1, v2 });
    }
    
    public void setAttribute(final String aggrSpec, final Object v1, final Object v2, final Object v3) {
        this.setAttribute(aggrSpec, new Object[] { v1, v2, v3 });
    }
    
    public void setAttribute(final String aggrSpec, final Object v1, final Object v2, final Object v3, final Object v4) {
        this.setAttribute(aggrSpec, new Object[] { v1, v2, v3, v4 });
    }
    
    public void setAttribute(final String aggrSpec, final Object v1, final Object v2, final Object v3, final Object v4, final Object v5) {
        this.setAttribute(aggrSpec, new Object[] { v1, v2, v3, v4, v5 });
    }
    
    protected void setAttribute(final String aggrSpec, final Object[] values) {
        final List properties = new ArrayList();
        final String aggrName = this.parseAggregateAttributeSpec(aggrSpec, properties);
        if (values == null || properties.size() == 0) {
            throw new IllegalArgumentException("missing properties or values for '" + aggrSpec + "'");
        }
        if (values.length != properties.size()) {
            throw new IllegalArgumentException("number of properties in '" + aggrSpec + "' != number of values");
        }
        final Aggregate aggr = new Aggregate();
        for (int i = 0; i < values.length; ++i) {
            Object value = values[i];
            if (value instanceof StringTemplate) {
                ((StringTemplate)value).setEnclosingInstance(this);
            }
            else {
                value = ASTExpr.convertArrayToList(value);
            }
            aggr.put(properties.get(i), value);
        }
        this.setAttribute(aggrName, aggr);
    }
    
    protected String parseAggregateAttributeSpec(final String aggrSpec, final List properties) {
        final int dot = aggrSpec.indexOf(46);
        if (dot <= 0) {
            throw new IllegalArgumentException("invalid aggregate attribute format: " + aggrSpec);
        }
        final String aggrName = aggrSpec.substring(0, dot);
        final String propString = aggrSpec.substring(dot + 1, aggrSpec.length());
        boolean error = true;
        final StringTokenizer tokenizer = new StringTokenizer(propString, "{,}", true);
        if (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            token = token.trim();
            if (token.equals("{")) {
                token = tokenizer.nextToken();
                token = token.trim();
                properties.add(token);
                for (token = tokenizer.nextToken(), token = token.trim(); token.equals(","); token = tokenizer.nextToken(), token = token.trim()) {
                    token = tokenizer.nextToken();
                    token = token.trim();
                    properties.add(token);
                }
                if (token.equals("}")) {
                    error = false;
                }
            }
        }
        if (error) {
            throw new IllegalArgumentException("invalid aggregate attribute format: " + aggrSpec);
        }
        return aggrName;
    }
    
    protected void rawSetAttribute(final Map attributes, final String name, final Object value) {
        if (this.formalArguments != FormalArgument.UNKNOWN && this.getFormalArgument(name) == null) {
            throw new NoSuchElementException("no such attribute: " + name + " in template context " + this.getEnclosingInstanceStackString());
        }
        if (value == null) {
            return;
        }
        attributes.put(name, value);
    }
    
    public void rawSetArgumentAttribute(final StringTemplate embedded, final Map attributes, final String name, final Object value) {
        if (embedded.formalArguments != FormalArgument.UNKNOWN && embedded.getFormalArgument(name) == null) {
            throw new NoSuchElementException("template " + embedded.getName() + " has no such attribute: " + name + " in template context " + this.getEnclosingInstanceStackString());
        }
        if (value == null) {
            return;
        }
        attributes.put(name, value);
    }
    
    public Object getAttribute(final String name) {
        final Object v = this.get(this, name);
        if (v == null) {
            this.checkNullAttributeAgainstFormalArguments(this, name);
        }
        return v;
    }
    
    public int write(final StringTemplateWriter out) throws IOException {
        if (this.group.debugTemplateOutput) {
            this.group.emitTemplateStartDebugString(this, out);
        }
        int n = 0;
        boolean missing = true;
        this.setPredefinedAttributes();
        this.setDefaultArgumentValues();
        for (int i = 0; this.chunks != null && i < this.chunks.size(); ++i) {
            final Expr a = this.chunks.get(i);
            final int chunkN = a.write(this, out);
            if (chunkN <= 0 && i == 0 && i + 1 < this.chunks.size() && this.chunks.get(i + 1) instanceof NewlineRef) {
                ++i;
            }
            else {
                if (chunkN <= 0 && i - 1 >= 0 && this.chunks.get(i - 1) instanceof NewlineRef && i + 1 < this.chunks.size() && this.chunks.get(i + 1) instanceof NewlineRef) {
                    ++i;
                }
                if (chunkN != -1) {
                    n += chunkN;
                    missing = false;
                }
            }
        }
        if (this.group.debugTemplateOutput) {
            this.group.emitTemplateStopDebugString(this, out);
        }
        if (StringTemplate.lintMode) {
            this.checkForTrouble();
        }
        if (missing && this.chunks != null && this.chunks.size() > 0) {
            return -1;
        }
        return n;
    }
    
    public Object get(final StringTemplate self, final String attribute) {
        if (self == null) {
            return null;
        }
        if (StringTemplate.lintMode) {
            self.trackAttributeReference(attribute);
        }
        Object o = null;
        if (self.attributes != null) {
            o = self.attributes.get(attribute);
        }
        if (o == null) {
            final Map argContext = self.getArgumentContext();
            if (argContext != null) {
                o = argContext.get(attribute);
            }
        }
        if (o == null && !self.passThroughAttributes && self.getFormalArgument(attribute) != null) {
            return null;
        }
        if (o == null && self.enclosingInstance != null) {
            final Object valueFromEnclosing = o = this.get(self.enclosingInstance, attribute);
        }
        else if (o == null && self.enclosingInstance == null) {
            o = self.group.getMap(attribute);
        }
        return o;
    }
    
    protected void breakTemplateIntoChunks() {
        if (this.pattern == null) {
            return;
        }
        try {
            final Class lexerClass = this.group.getTemplateLexerClass();
            final Constructor ctor = lexerClass.getConstructor(StringTemplate.class, Reader.class);
            final CharScanner chunkStream = ctor.newInstance(this, new StringReader(this.pattern));
            chunkStream.setTokenObjectClass("org.antlr.stringtemplate.language.ChunkToken");
            final TemplateParser chunkifier = new TemplateParser(chunkStream);
            chunkifier.template(this);
        }
        catch (Exception e) {
            String name = "<unknown>";
            final String outerName = this.getOutermostName();
            if (this.getName() != null) {
                name = this.getName();
            }
            if (outerName != null && !name.equals(outerName)) {
                name = name + " nested in " + outerName;
            }
            this.error("problem parsing template '" + name + "'", e);
        }
    }
    
    public ASTExpr parseAction(final String action) {
        final ActionLexer lexer = new ActionLexer(new StringReader(action.toString()));
        final ActionParser parser = new ActionParser(lexer, this);
        parser.setASTNodeClass("org.antlr.stringtemplate.language.StringTemplateAST");
        lexer.setTokenObjectClass("org.antlr.stringtemplate.language.StringTemplateToken");
        ASTExpr a = null;
        try {
            final Map options = parser.action();
            final AST tree = parser.getAST();
            if (tree != null) {
                if (tree.getType() == 8) {
                    a = new ConditionalExpr(this, tree);
                }
                else {
                    a = new ASTExpr(this, tree, options);
                }
            }
        }
        catch (RecognitionException re) {
            this.error("Can't parse chunk: " + action.toString(), re);
        }
        catch (TokenStreamException tse) {
            this.error("Can't parse chunk: " + action.toString(), tse);
        }
        return a;
    }
    
    public int getTemplateID() {
        return this.templateID;
    }
    
    public Map getAttributes() {
        return this.attributes;
    }
    
    public List getChunks() {
        return this.chunks;
    }
    
    public void addChunk(final Expr e) {
        if (this.chunks == null) {
            this.chunks = new ArrayList();
        }
        this.chunks.add(e);
    }
    
    public void setAttributes(final Map attributes) {
        this.attributes = attributes;
    }
    
    public Map getFormalArguments() {
        return this.formalArguments;
    }
    
    public void setFormalArguments(final LinkedHashMap args) {
        this.formalArguments = args;
    }
    
    public void setDefaultArgumentValues() {
        if (this.numberOfDefaultArgumentValues == 0) {
            return;
        }
        if (this.argumentContext == null) {
            this.argumentContext = new HashMap();
        }
        if (this.formalArguments != FormalArgument.UNKNOWN) {
            final Set argNames = this.formalArguments.keySet();
            for (final String argName : argNames) {
                final FormalArgument arg = this.formalArguments.get(argName);
                if (arg.defaultValueST != null) {
                    final Object existingValue = this.getAttribute(argName);
                    if (existingValue != null) {
                        continue;
                    }
                    Object defaultValue = arg.defaultValueST;
                    final int nchunks = arg.defaultValueST.chunks.size();
                    if (nchunks == 1) {
                        final Object a = arg.defaultValueST.chunks.get(0);
                        if (a instanceof ASTExpr) {
                            final ASTExpr e = (ASTExpr)a;
                            if (e.getAST().getType() == 9) {
                                defaultValue = e.evaluateExpression(this, e.getAST());
                            }
                        }
                    }
                    this.argumentContext.put(argName, defaultValue);
                }
            }
        }
    }
    
    public FormalArgument lookupFormalArgument(final String name) {
        FormalArgument arg = this.getFormalArgument(name);
        if (arg == null && this.enclosingInstance != null) {
            arg = this.enclosingInstance.lookupFormalArgument(name);
        }
        return arg;
    }
    
    public FormalArgument getFormalArgument(final String name) {
        return this.formalArguments.get(name);
    }
    
    public void defineEmptyFormalArgumentList() {
        this.setFormalArguments(new LinkedHashMap());
    }
    
    public void defineFormalArgument(final String name) {
        this.defineFormalArgument(name, null);
    }
    
    public void defineFormalArguments(final List names) {
        if (names == null) {
            return;
        }
        for (int i = 0; i < names.size(); ++i) {
            final String name = names.get(i);
            this.defineFormalArgument(name);
        }
    }
    
    public void defineFormalArgument(final String name, final StringTemplate defaultValue) {
        if (defaultValue != null) {
            ++this.numberOfDefaultArgumentValues;
        }
        final FormalArgument a = new FormalArgument(name, defaultValue);
        if (this.formalArguments == FormalArgument.UNKNOWN) {
            this.formalArguments = new LinkedHashMap();
        }
        this.formalArguments.put(name, a);
    }
    
    public void setPassThroughAttributes(final boolean passThroughAttributes) {
        this.passThroughAttributes = passThroughAttributes;
    }
    
    public void setAttributeRenderers(final Map renderers) {
        this.attributeRenderers = renderers;
    }
    
    public void registerRenderer(final Class attributeClassType, final AttributeRenderer renderer) {
        if (this.attributeRenderers == null) {
            this.attributeRenderers = new HashMap();
        }
        this.attributeRenderers.put(attributeClassType, renderer);
    }
    
    public AttributeRenderer getAttributeRenderer(final Class attributeClassType) {
        AttributeRenderer renderer = null;
        if (this.attributeRenderers != null) {
            renderer = this.attributeRenderers.get(attributeClassType);
        }
        if (renderer != null) {
            return renderer;
        }
        if (this.enclosingInstance != null) {
            return this.enclosingInstance.getAttributeRenderer(attributeClassType);
        }
        return this.group.getAttributeRenderer(attributeClassType);
    }
    
    public void error(final String msg) {
        this.error(msg, null);
    }
    
    public void warning(final String msg) {
        if (this.getErrorListener() != null) {
            this.getErrorListener().warning(msg);
        }
        else {
            System.err.println("StringTemplate: warning: " + msg);
        }
    }
    
    public void error(final String msg, Throwable e) {
        if (this.getErrorListener() != null) {
            this.getErrorListener().error(msg, e);
        }
        else if (e != null) {
            System.err.println("StringTemplate: error: " + msg + ": " + e.toString());
            if (e instanceof InvocationTargetException) {
                e = ((InvocationTargetException)e).getTargetException();
            }
            e.printStackTrace(System.err);
        }
        else {
            System.err.println("StringTemplate: error: " + msg);
        }
    }
    
    public static void setLintMode(final boolean lint) {
        StringTemplate.lintMode = lint;
    }
    
    public static boolean inLintMode() {
        return StringTemplate.lintMode;
    }
    
    protected void trackAttributeReference(final String name) {
        if (this.referencedAttributes == null) {
            this.referencedAttributes = new ArrayList();
        }
        this.referencedAttributes.add(name);
    }
    
    public static boolean isRecursiveEnclosingInstance(final StringTemplate st) {
        if (st == null) {
            return false;
        }
        StringTemplate p = st.enclosingInstance;
        if (p == st) {
            return true;
        }
        while (p != null) {
            if (p == st) {
                return true;
            }
            p = p.enclosingInstance;
        }
        return false;
    }
    
    public String getEnclosingInstanceStackTrace() {
        final StringBuffer buf = new StringBuffer();
        final Set seen = new HashSet();
        for (StringTemplate p = this; p != null; p = p.enclosingInstance) {
            if (seen.contains(p)) {
                buf.append(p.getTemplateDeclaratorString());
                buf.append(" (start of recursive cycle)");
                buf.append("\n");
                buf.append("...");
                break;
            }
            seen.add(p);
            buf.append(p.getTemplateDeclaratorString());
            if (p.attributes != null) {
                buf.append(", attributes=[");
                int i = 0;
                for (final String attrName : p.attributes.keySet()) {
                    if (i > 0) {
                        buf.append(", ");
                    }
                    ++i;
                    buf.append(attrName);
                    final Object o = p.attributes.get(attrName);
                    if (o instanceof StringTemplate) {
                        final StringTemplate st = (StringTemplate)o;
                        buf.append("=");
                        buf.append("<");
                        buf.append(st.getName());
                        buf.append("()@");
                        buf.append(String.valueOf(st.getTemplateID()));
                        buf.append(">");
                    }
                    else {
                        if (!(o instanceof List)) {
                            continue;
                        }
                        buf.append("=List[..");
                        final List list = (List)o;
                        int n = 0;
                        for (int j = 0; j < list.size(); ++j) {
                            final Object listValue = list.get(j);
                            if (listValue instanceof StringTemplate) {
                                if (n > 0) {
                                    buf.append(", ");
                                }
                                ++n;
                                final StringTemplate st2 = (StringTemplate)listValue;
                                buf.append("<");
                                buf.append(st2.getName());
                                buf.append("()@");
                                buf.append(String.valueOf(st2.getTemplateID()));
                                buf.append(">");
                            }
                        }
                        buf.append("..]");
                    }
                }
                buf.append("]");
            }
            if (p.referencedAttributes != null) {
                buf.append(", references=");
                buf.append(p.referencedAttributes);
            }
            buf.append(">\n");
        }
        return buf.toString();
    }
    
    public String getTemplateDeclaratorString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("<");
        buf.append(this.getName());
        buf.append("(");
        buf.append(this.formalArguments.keySet());
        buf.append(")@");
        buf.append(String.valueOf(this.getTemplateID()));
        buf.append(">");
        return buf.toString();
    }
    
    protected String getTemplateHeaderString(final boolean showAttributes) {
        if (showAttributes) {
            final StringBuffer buf = new StringBuffer();
            buf.append(this.getName());
            if (this.attributes != null) {
                buf.append(this.attributes.keySet());
            }
            return buf.toString();
        }
        return this.getName();
    }
    
    protected void checkNullAttributeAgainstFormalArguments(final StringTemplate self, final String attribute) {
        if (self.getFormalArguments() == FormalArgument.UNKNOWN) {
            if (self.enclosingInstance != null) {
                this.checkNullAttributeAgainstFormalArguments(self.enclosingInstance, attribute);
            }
            return;
        }
        final FormalArgument formalArg = self.lookupFormalArgument(attribute);
        if (formalArg == null) {
            throw new NoSuchElementException("no such attribute: " + attribute + " in template context " + this.getEnclosingInstanceStackString());
        }
    }
    
    protected void checkForTrouble() {
        if (this.attributes == null) {
            return;
        }
        final Set names = this.attributes.keySet();
        for (final String name : names) {
            if (this.referencedAttributes != null && !this.referencedAttributes.contains(name)) {
                this.warning(this.getName() + ": set but not used: " + name);
            }
        }
    }
    
    public String getEnclosingInstanceStackString() {
        final List names = new LinkedList();
        for (StringTemplate p = this; p != null; p = p.enclosingInstance) {
            final String name = p.getName();
            names.add(0, name + (p.passThroughAttributes ? "(...)" : ""));
        }
        return names.toString().replaceAll(",", "");
    }
    
    public boolean isRegion() {
        return this.isRegion;
    }
    
    public void setIsRegion(final boolean isRegion) {
        this.isRegion = isRegion;
    }
    
    public void addRegionName(final String name) {
        if (this.regions == null) {
            this.regions = new HashSet();
        }
        this.regions.add(name);
    }
    
    public boolean containsRegionName(final String name) {
        return this.regions != null && this.regions.contains(name);
    }
    
    public int getRegionDefType() {
        return this.regionDefType;
    }
    
    public void setRegionDefType(final int regionDefType) {
        this.regionDefType = regionDefType;
    }
    
    public String toDebugString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("template-" + this.getTemplateDeclaratorString() + ":");
        buf.append("chunks=");
        if (this.chunks != null) {
            buf.append(this.chunks.toString());
        }
        buf.append("attributes=[");
        if (this.attributes != null) {
            final Set attrNames = this.attributes.keySet();
            int n = 0;
            final Iterator iter = attrNames.iterator();
            while (iter.hasNext()) {
                if (n > 0) {
                    buf.append(',');
                }
                final String name = iter.next();
                buf.append(name + "=");
                final Object value = this.attributes.get(name);
                if (value instanceof StringTemplate) {
                    buf.append(((StringTemplate)value).toDebugString());
                }
                else {
                    buf.append(value);
                }
                ++n;
            }
            buf.append("]");
        }
        return buf.toString();
    }
    
    public String toStructureString() {
        return this.toStructureString(0);
    }
    
    public String toStructureString(final int indent) {
        final StringBuffer buf = new StringBuffer();
        for (int i = 1; i <= indent; ++i) {
            buf.append("  ");
        }
        buf.append(this.getName());
        buf.append(this.attributes.keySet());
        buf.append(":\n");
        if (this.attributes != null) {
            final Set attrNames = this.attributes.keySet();
            for (final String name : attrNames) {
                final Object value = this.attributes.get(name);
                if (value instanceof StringTemplate) {
                    buf.append(((StringTemplate)value).toStructureString(indent + 1));
                }
                else if (value instanceof List) {
                    final List alist = (List)value;
                    for (int j = 0; j < alist.size(); ++j) {
                        final Object o = alist.get(j);
                        if (o instanceof StringTemplate) {
                            buf.append(((StringTemplate)o).toStructureString(indent + 1));
                        }
                    }
                }
                else {
                    if (!(value instanceof Map)) {
                        continue;
                    }
                    final Map m = (Map)value;
                    final Collection mvalues = m.values();
                    for (final Object o2 : mvalues) {
                        if (o2 instanceof StringTemplate) {
                            buf.append(((StringTemplate)o2).toStructureString(indent + 1));
                        }
                    }
                }
            }
        }
        return buf.toString();
    }
    
    public StringTemplate getDOTForDependencyGraph(final boolean showAttributes) {
        final String structure = "digraph StringTemplateDependencyGraph {\nnode [shape=$shape$, $if(width)$width=$width$,$endif$      $if(height)$height=$height$,$endif$ fontsize=$fontsize$];\n$edges:{e|\"$e.src$\" -> \"$e.trg$\"\n}$}\n";
        final StringTemplate graphST = new StringTemplate(structure);
        final HashMap edges = new HashMap();
        this.getDependencyGraph(edges, showAttributes);
        final Set sourceNodes = edges.keySet();
        for (final String src : sourceNodes) {
            final Set targetNodes = edges.get(src);
            for (final String trg : targetNodes) {
                graphST.setAttribute("edges.{src,trg}", src, trg);
            }
        }
        graphST.setAttribute("shape", "none");
        graphST.setAttribute("fontsize", "11");
        graphST.setAttribute("height", "0");
        return graphST;
    }
    
    public void getDependencyGraph(final Map edges, final boolean showAttributes) {
        final String srcNode = this.getTemplateHeaderString(showAttributes);
        if (this.attributes != null) {
            final Set attrNames = this.attributes.keySet();
            for (final String name : attrNames) {
                final Object value = this.attributes.get(name);
                if (value instanceof StringTemplate) {
                    final String targetNode = ((StringTemplate)value).getTemplateHeaderString(showAttributes);
                    this.putToMultiValuedMap(edges, srcNode, targetNode);
                    ((StringTemplate)value).getDependencyGraph(edges, showAttributes);
                }
                else if (value instanceof List) {
                    final List alist = (List)value;
                    for (int i = 0; i < alist.size(); ++i) {
                        final Object o = alist.get(i);
                        if (o instanceof StringTemplate) {
                            final String targetNode2 = ((StringTemplate)o).getTemplateHeaderString(showAttributes);
                            this.putToMultiValuedMap(edges, srcNode, targetNode2);
                            ((StringTemplate)o).getDependencyGraph(edges, showAttributes);
                        }
                    }
                }
                else {
                    if (!(value instanceof Map)) {
                        continue;
                    }
                    final Map m = (Map)value;
                    final Collection mvalues = m.values();
                    for (final Object o2 : mvalues) {
                        if (o2 instanceof StringTemplate) {
                            final String targetNode3 = ((StringTemplate)o2).getTemplateHeaderString(showAttributes);
                            this.putToMultiValuedMap(edges, srcNode, targetNode3);
                            ((StringTemplate)o2).getDependencyGraph(edges, showAttributes);
                        }
                    }
                }
            }
        }
        for (int j = 0; this.chunks != null && j < this.chunks.size(); ++j) {
            final Expr expr = this.chunks.get(j);
            if (expr instanceof ASTExpr) {
                final ASTExpr e = (ASTExpr)expr;
                final AST tree = e.getAST();
                final AST includeAST = new CommonAST(new CommonToken(7, "include"));
                final ASTEnumeration it = tree.findAllPartial(includeAST);
                while (it.hasMoreNodes()) {
                    final AST t = it.nextNode();
                    final String templateInclude = t.getFirstChild().getText();
                    System.out.println("found include " + templateInclude);
                    this.putToMultiValuedMap(edges, srcNode, templateInclude);
                    final StringTemplateGroup group = this.getGroup();
                    if (group != null) {
                        final StringTemplate st = group.getInstanceOf(templateInclude);
                        st.getDependencyGraph(edges, showAttributes);
                    }
                }
            }
        }
    }
    
    protected void putToMultiValuedMap(final Map map, final Object key, final Object value) {
        HashSet bag = map.get(key);
        if (bag == null) {
            bag = new HashSet();
            map.put(key, bag);
        }
        bag.add(value);
    }
    
    public void printDebugString() {
        System.out.println("template-" + this.getName() + ":");
        System.out.print("chunks=");
        System.out.println(this.chunks.toString());
        if (this.attributes == null) {
            return;
        }
        System.out.print("attributes=[");
        final Set attrNames = this.attributes.keySet();
        int n = 0;
        final Iterator iter = attrNames.iterator();
        while (iter.hasNext()) {
            if (n > 0) {
                System.out.print(',');
            }
            final String name = iter.next();
            final Object value = this.attributes.get(name);
            if (value instanceof StringTemplate) {
                System.out.print(name + "=");
                ((StringTemplate)value).printDebugString();
            }
            else if (value instanceof List) {
                final ArrayList alist = (ArrayList)value;
                for (int i = 0; i < alist.size(); ++i) {
                    final Object o = alist.get(i);
                    System.out.print(name + "[" + i + "] is " + o.getClass().getName() + "=");
                    if (o instanceof StringTemplate) {
                        ((StringTemplate)o).printDebugString();
                    }
                    else {
                        System.out.println(o);
                    }
                }
            }
            else {
                System.out.print(name + "=");
                System.out.println(value);
            }
            ++n;
        }
        System.out.print("]\n");
    }
    
    public String toString() {
        return this.toString(-1);
    }
    
    public String toString(final int lineWidth) {
        final StringWriter out = new StringWriter();
        final StringTemplateWriter wr = this.group.getStringTemplateWriter(out);
        wr.setLineWidth(lineWidth);
        try {
            this.write(wr);
        }
        catch (IOException io) {
            this.error("Got IOException writing to writer " + wr.getClass().getName());
        }
        wr.setLineWidth(-1);
        return out.toString();
    }
    
    static {
        StringTemplate.lintMode = false;
        StringTemplate.templateCounter = 0;
        StringTemplate.defaultGroup = new StringTemplateGroup("defaultGroup", ".");
    }
    
    public static final class Aggregate
    {
        protected HashMap properties;
        
        public Aggregate() {
            this.properties = new HashMap();
        }
        
        protected void put(final String propName, final Object propValue) {
            this.properties.put(propName, propValue);
        }
        
        public Object get(final String propName) {
            return this.properties.get(propName);
        }
        
        public String toString() {
            return this.properties.toString();
        }
    }
    
    public static final class STAttributeList extends ArrayList
    {
        public STAttributeList(final int size) {
            super(size);
        }
        
        public STAttributeList() {
        }
    }
}
