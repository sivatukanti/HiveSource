// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate.language;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.Writer;
import java.io.StringWriter;
import org.antlr.stringtemplate.AttributeRenderer;
import org.antlr.stringtemplate.StringTemplateGroup;
import java.util.Collection;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.io.IOException;
import antlr.RecognitionException;
import org.antlr.stringtemplate.StringTemplateWriter;
import antlr.collections.AST;
import java.util.Set;
import java.util.Map;
import org.antlr.stringtemplate.StringTemplate;

public class ASTExpr extends Expr
{
    public static final int MISSING = -1;
    public static final String DEFAULT_ATTRIBUTE_NAME = "it";
    public static final String DEFAULT_ATTRIBUTE_NAME_DEPRECATED = "attr";
    public static final String DEFAULT_INDEX_VARIABLE_NAME = "i";
    public static final String DEFAULT_INDEX0_VARIABLE_NAME = "i0";
    public static final String DEFAULT_MAP_VALUE_NAME = "_default_";
    public static final String DEFAULT_MAP_KEY_NAME = "key";
    public static final StringTemplate MAP_KEY_VALUE;
    public static final String EMPTY_OPTION = "empty expr option";
    public static final Map defaultOptionValues;
    public static final Set supportedOptions;
    AST exprTree;
    Map options;
    String wrapString;
    String nullValue;
    String separatorString;
    String formatString;
    
    public ASTExpr(final StringTemplate enclosingTemplate, final AST exprTree, final Map options) {
        super(enclosingTemplate);
        this.exprTree = null;
        this.options = null;
        this.wrapString = null;
        this.nullValue = null;
        this.separatorString = null;
        this.formatString = null;
        this.exprTree = exprTree;
        this.options = options;
    }
    
    public AST getAST() {
        return this.exprTree;
    }
    
    public int write(final StringTemplate self, final StringTemplateWriter out) throws IOException {
        if (this.exprTree == null || self == null || out == null) {
            return 0;
        }
        final StringTemplateAST anchorAST = (StringTemplateAST)this.getOption("anchor");
        if (anchorAST != null) {
            out.pushAnchorPoint();
        }
        out.pushIndentation(this.getIndentation());
        this.handleExprOptions(self);
        final ActionEvaluator eval = new ActionEvaluator(self, this, out);
        int n = 0;
        try {
            n = eval.action(this.exprTree);
        }
        catch (RecognitionException re) {
            self.error("can't evaluate tree: " + this.exprTree.toStringList(), re);
        }
        out.popIndentation();
        if (anchorAST != null) {
            out.popAnchorPoint();
        }
        return n;
    }
    
    protected void handleExprOptions(final StringTemplate self) {
        this.formatString = null;
        final StringTemplateAST wrapAST = (StringTemplateAST)this.getOption("wrap");
        if (wrapAST != null) {
            this.wrapString = this.evaluateExpression(self, wrapAST);
        }
        final StringTemplateAST nullValueAST = (StringTemplateAST)this.getOption("null");
        if (nullValueAST != null) {
            this.nullValue = this.evaluateExpression(self, nullValueAST);
        }
        final StringTemplateAST separatorAST = (StringTemplateAST)this.getOption("separator");
        if (separatorAST != null) {
            this.separatorString = this.evaluateExpression(self, separatorAST);
        }
        final StringTemplateAST formatAST = (StringTemplateAST)this.getOption("format");
        if (formatAST != null) {
            this.formatString = this.evaluateExpression(self, formatAST);
        }
        if (this.options != null) {
            for (final String option : this.options.keySet()) {
                if (!ASTExpr.supportedOptions.contains(option)) {
                    self.warning("ignoring unsupported option: " + option);
                }
            }
        }
    }
    
    public Object applyTemplateToListOfAttributes(final StringTemplate self, final List attributes, final StringTemplate templateToApply) {
        if (attributes == null || templateToApply == null || attributes.size() == 0) {
            return null;
        }
        Map argumentContext = null;
        final List results = new StringTemplate.STAttributeList();
        for (int a = 0; a < attributes.size(); ++a) {
            Object o = attributes.get(a);
            if (o != null) {
                o = convertAnythingToIterator(o);
                attributes.set(a, o);
            }
        }
        int numAttributes = attributes.size();
        final Map formalArguments = templateToApply.getFormalArguments();
        if (formalArguments == null || formalArguments.size() == 0) {
            self.error("missing arguments in anonymous template in context " + self.getEnclosingInstanceStackString());
            return null;
        }
        Object[] formalArgumentNames = formalArguments.keySet().toArray();
        if (formalArgumentNames.length != numAttributes) {
            self.error("number of arguments " + formalArguments.keySet() + " mismatch between attribute list and anonymous" + " template in context " + self.getEnclosingInstanceStackString());
            final int shorterSize = numAttributes = Math.min(formalArgumentNames.length, numAttributes);
            final Object[] newFormalArgumentNames = new Object[shorterSize];
            System.arraycopy(formalArgumentNames, 0, newFormalArgumentNames, 0, shorterSize);
            formalArgumentNames = newFormalArgumentNames;
        }
        int i = 0;
        while (true) {
            argumentContext = new HashMap();
            int numEmpty = 0;
            for (int a2 = 0; a2 < numAttributes; ++a2) {
                final Iterator it = attributes.get(a2);
                if (it != null && it.hasNext()) {
                    final String argName = (String)formalArgumentNames[a2];
                    final Object iteratedValue = it.next();
                    argumentContext.put(argName, iteratedValue);
                }
                else {
                    ++numEmpty;
                }
            }
            if (numEmpty == numAttributes) {
                break;
            }
            argumentContext.put("i", new Integer(i + 1));
            argumentContext.put("i0", new Integer(i));
            final StringTemplate embedded = templateToApply.getInstanceOf();
            embedded.setEnclosingInstance(self);
            embedded.setArgumentContext(argumentContext);
            results.add(embedded);
            ++i;
        }
        return results;
    }
    
    public Object applyListOfAlternatingTemplates(final StringTemplate self, Object attributeValue, final List templatesToApply) {
        if (attributeValue == null || templatesToApply == null || templatesToApply.size() == 0) {
            return null;
        }
        StringTemplate embedded = null;
        Map argumentContext = null;
        attributeValue = convertArrayToList(attributeValue);
        attributeValue = convertAnythingIteratableToIterator(attributeValue);
        if (attributeValue instanceof Iterator) {
            List resultVector = new StringTemplate.STAttributeList();
            final Iterator iter = (Iterator)attributeValue;
            int i = 0;
            while (iter.hasNext()) {
                Object ithValue = iter.next();
                if (ithValue == null) {
                    if (this.nullValue == null) {
                        continue;
                    }
                    ithValue = this.nullValue;
                }
                final int templateIndex = i % templatesToApply.size();
                embedded = templatesToApply.get(templateIndex);
                final StringTemplateAST args = embedded.getArgumentsAST();
                embedded = embedded.getInstanceOf();
                embedded.setEnclosingInstance(self);
                embedded.setArgumentsAST(args);
                argumentContext = new HashMap();
                final Map formalArgs = embedded.getFormalArguments();
                final boolean isAnonymous = embedded.getName() == "anonymous";
                this.setSoleFormalArgumentToIthValue(embedded, argumentContext, ithValue);
                if (!isAnonymous || formalArgs == null || formalArgs.size() <= 0) {
                    argumentContext.put("it", ithValue);
                    argumentContext.put("attr", ithValue);
                }
                argumentContext.put("i", new Integer(i + 1));
                argumentContext.put("i0", new Integer(i));
                embedded.setArgumentContext(argumentContext);
                this.evaluateArguments(embedded);
                resultVector.add(embedded);
                ++i;
            }
            if (resultVector.size() == 0) {
                resultVector = null;
            }
            return resultVector;
        }
        embedded = templatesToApply.get(0);
        argumentContext = new HashMap();
        final Map formalArgs2 = embedded.getFormalArguments();
        final StringTemplateAST args2 = embedded.getArgumentsAST();
        this.setSoleFormalArgumentToIthValue(embedded, argumentContext, attributeValue);
        final boolean isAnonymous2 = embedded.getName() == "anonymous";
        if (!isAnonymous2 || formalArgs2 == null || formalArgs2.size() <= 0) {
            argumentContext.put("it", attributeValue);
            argumentContext.put("attr", attributeValue);
        }
        argumentContext.put("i", new Integer(1));
        argumentContext.put("i0", new Integer(0));
        embedded.setArgumentContext(argumentContext);
        this.evaluateArguments(embedded);
        return embedded;
    }
    
    protected void setSoleFormalArgumentToIthValue(final StringTemplate embedded, final Map argumentContext, final Object ithValue) {
        final Map formalArgs = embedded.getFormalArguments();
        if (formalArgs != null) {
            String soleArgName = null;
            final boolean isAnonymous = embedded.getName() == "anonymous";
            if (formalArgs.size() == 1 || (isAnonymous && formalArgs.size() > 0)) {
                if (isAnonymous && formalArgs.size() > 1) {
                    embedded.error("too many arguments on {...} template: " + formalArgs);
                }
                final Set argNames = formalArgs.keySet();
                soleArgName = (String)argNames.toArray()[0];
                argumentContext.put(soleArgName, ithValue);
            }
        }
    }
    
    public Object getObjectProperty(final StringTemplate self, final Object o, final Object propertyName) {
        if (o == null || propertyName == null) {
            return null;
        }
        Object value = this.rawGetObjectProperty(self, o, propertyName);
        value = convertArrayToList(value);
        return value;
    }
    
    protected Object rawGetObjectProperty(final StringTemplate self, final Object o, final Object property) {
        final Class c = o.getClass();
        Object value = null;
        if (c == StringTemplate.Aggregate.class) {
            final String propertyName = (String)property;
            value = ((StringTemplate.Aggregate)o).get(propertyName);
            return value;
        }
        if (c == StringTemplate.class) {
            final Map attributes = ((StringTemplate)o).getAttributes();
            if (attributes != null) {
                final String propertyName2 = (String)property;
                value = attributes.get(propertyName2);
                return value;
            }
        }
        if (o instanceof Map) {
            final Map map = (Map)o;
            if (property.equals("keys")) {
                value = map.keySet();
            }
            else if (property.equals("values")) {
                value = map.values();
            }
            else if (map.containsKey(property)) {
                value = map.get(property);
            }
            else if (map.containsKey(property.toString())) {
                value = map.get(property.toString());
            }
            else if (map.containsKey("_default_")) {
                value = map.get("_default_");
            }
            if (value == ASTExpr.MAP_KEY_VALUE) {
                value = property;
            }
            return value;
        }
        Method m = null;
        final String propertyName2 = (String)property;
        final String methodSuffix = Character.toUpperCase(propertyName2.charAt(0)) + propertyName2.substring(1, propertyName2.length());
        m = this.getMethod(c, "get" + methodSuffix);
        if (m == null) {
            m = this.getMethod(c, "is" + methodSuffix);
        }
        if (m != null) {
            try {
                value = this.invokeMethod(m, o, value);
            }
            catch (Exception e) {
                self.error("Can't get property " + propertyName2 + " using method get/is" + methodSuffix + " from " + c.getName() + " instance", e);
            }
        }
        else {
            try {
                final Field f = c.getField(propertyName2);
                try {
                    value = this.accessField(f, o, value);
                }
                catch (IllegalAccessException iae) {
                    self.error("Can't access property " + propertyName2 + " using method get/is" + methodSuffix + " or direct field access from " + c.getName() + " instance", iae);
                }
            }
            catch (NoSuchFieldException nsfe) {
                self.error("Class " + c.getName() + " has no such attribute: " + propertyName2 + " in template context " + self.getEnclosingInstanceStackString(), nsfe);
            }
        }
        return value;
    }
    
    protected Object accessField(final Field f, final Object o, Object value) throws IllegalAccessException {
        try {
            f.setAccessible(true);
        }
        catch (SecurityException ex) {}
        value = f.get(o);
        return value;
    }
    
    protected Object invokeMethod(final Method m, final Object o, Object value) throws IllegalAccessException, InvocationTargetException {
        try {
            m.setAccessible(true);
        }
        catch (SecurityException ex) {}
        value = m.invoke(o, (Object[])null);
        return value;
    }
    
    protected Method getMethod(final Class c, final String methodName) {
        Method m;
        try {
            m = c.getMethod(methodName, (Class[])null);
        }
        catch (NoSuchMethodException nsme) {
            m = null;
        }
        return m;
    }
    
    public boolean testAttributeTrue(final Object a) {
        if (a == null) {
            return false;
        }
        if (a instanceof Boolean) {
            return (boolean)a;
        }
        if (a instanceof Collection) {
            return ((Collection)a).size() > 0;
        }
        if (a instanceof Map) {
            return ((Map)a).size() > 0;
        }
        return !(a instanceof Iterator) || ((Iterator)a).hasNext();
    }
    
    public Object add(final Object a, final Object b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        return a.toString() + b.toString();
    }
    
    public StringTemplate getTemplateInclude(final StringTemplate enclosing, final String templateName, final StringTemplateAST argumentsAST) {
        final StringTemplateGroup group = enclosing.getGroup();
        final StringTemplate embedded = group.getEmbeddedInstanceOf(enclosing, templateName);
        if (embedded == null) {
            enclosing.error("cannot make embedded instance of " + templateName + " in template " + enclosing.getName());
            return null;
        }
        embedded.setArgumentsAST(argumentsAST);
        this.evaluateArguments(embedded);
        return embedded;
    }
    
    public int writeAttribute(final StringTemplate self, final Object o, final StringTemplateWriter out) {
        return this.write(self, o, out);
    }
    
    protected int write(final StringTemplate self, Object o, final StringTemplateWriter out) {
        if (o == null) {
            if (this.nullValue == null) {
                return -1;
            }
            o = this.nullValue;
        }
        final int n = 0;
        try {
            if (o instanceof StringTemplate) {
                return this.writeTemplate(self, o, out);
            }
            o = convertAnythingIteratableToIterator(o);
            if (o instanceof Iterator) {
                return this.writeIterableValue(self, o, out);
            }
            return this.writePOJO(self, o, out);
        }
        catch (IOException io) {
            self.error("problem writing object: " + o, io);
            return n;
        }
    }
    
    protected int writePOJO(final StringTemplate self, final Object o, final StringTemplateWriter out) throws IOException {
        int n = 0;
        final AttributeRenderer renderer = self.getAttributeRenderer(o.getClass());
        String v = null;
        if (renderer != null) {
            if (this.formatString != null) {
                v = renderer.toString(o, this.formatString);
            }
            else {
                v = renderer.toString(o);
            }
        }
        else {
            v = o.toString();
        }
        if (this.wrapString != null) {
            n = out.write(v, this.wrapString);
        }
        else {
            n = out.write(v);
        }
        return n;
    }
    
    protected int writeTemplate(final StringTemplate self, final Object o, final StringTemplateWriter out) throws IOException {
        int n = 0;
        final StringTemplate stToWrite = (StringTemplate)o;
        stToWrite.setEnclosingInstance(self);
        if (StringTemplate.inLintMode() && StringTemplate.isRecursiveEnclosingInstance(stToWrite)) {
            throw new IllegalStateException("infinite recursion to " + stToWrite.getTemplateDeclaratorString() + " referenced in " + stToWrite.getEnclosingInstance().getTemplateDeclaratorString() + "; stack trace:\n" + stToWrite.getEnclosingInstanceStackTrace());
        }
        if (this.wrapString != null) {
            n = out.writeWrapSeparator(this.wrapString);
        }
        if (this.formatString != null) {
            final AttributeRenderer renderer = self.getAttributeRenderer(String.class);
            if (renderer != null) {
                final StringWriter buf = new StringWriter();
                final StringTemplateWriter sw = self.getGroup().getStringTemplateWriter(buf);
                stToWrite.write(sw);
                n = out.write(renderer.toString(buf.toString(), this.formatString));
                return n;
            }
        }
        n = stToWrite.write(out);
        return n;
    }
    
    protected int writeIterableValue(final StringTemplate self, final Object o, final StringTemplateWriter out) throws IOException {
        int n = 0;
        final Iterator iter = (Iterator)o;
        boolean seenAValue = false;
        while (iter.hasNext()) {
            Object iterValue = iter.next();
            if (iterValue == null) {
                iterValue = this.nullValue;
            }
            if (iterValue != null) {
                if (this.separatorString == null) {
                    final int nw = this.write(self, iterValue, out);
                    if (nw == -1) {
                        continue;
                    }
                    n += nw;
                }
                else {
                    if (iterValue instanceof StringTemplate) {
                        final StringTemplate st = (StringTemplate)iterValue;
                        final int nchunks = (st.getChunks() != null) ? st.getChunks().size() : 0;
                        boolean nullable = true;
                        for (int i = 0; i < nchunks; ++i) {
                            final Expr a = st.getChunks().get(i);
                            if (!(a instanceof ConditionalExpr)) {
                                nullable = false;
                            }
                        }
                        if (!nullable) {
                            if (seenAValue && this.separatorString != null) {
                                n += out.writeSeparator(this.separatorString);
                            }
                            final int nw2 = this.write(self, iterValue, out);
                            n += nw2;
                            seenAValue = true;
                            continue;
                        }
                    }
                    if (!(iterValue instanceof StringTemplate) && !(iterValue instanceof Iterator)) {
                        if (seenAValue && this.separatorString != null) {
                            n += out.writeSeparator(this.separatorString);
                        }
                        final int nw = this.write(self, iterValue, out);
                        seenAValue = true;
                        n += nw;
                    }
                    else {
                        final StringWriter buf = new StringWriter();
                        final StringTemplateWriter sw = self.getGroup().getStringTemplateWriter(buf);
                        final int tmpsize = this.write(self, iterValue, sw);
                        if (tmpsize == -1) {
                            continue;
                        }
                        if (seenAValue && this.separatorString != null) {
                            n += out.writeSeparator(this.separatorString);
                        }
                        final int nw2 = this.write(self, iterValue, out);
                        n += nw2;
                        seenAValue = true;
                    }
                }
            }
        }
        return n;
    }
    
    public String evaluateExpression(final StringTemplate self, final Object expr) {
        if (expr == null) {
            return null;
        }
        if (expr instanceof StringTemplateAST) {
            final StringTemplateAST exprAST = (StringTemplateAST)expr;
            final StringWriter buf = new StringWriter();
            final StringTemplateWriter sw = self.getGroup().getStringTemplateWriter(buf);
            final ActionEvaluator eval = new ActionEvaluator(self, this, sw);
            try {
                eval.action(exprAST);
            }
            catch (RecognitionException re) {
                self.error("can't evaluate tree: " + this.exprTree.toStringList(), re);
            }
            return buf.toString();
        }
        return expr.toString();
    }
    
    protected void evaluateArguments(final StringTemplate self) {
        final StringTemplateAST argumentsAST = self.getArgumentsAST();
        if (argumentsAST == null || argumentsAST.getFirstChild() == null) {
            return;
        }
        final StringTemplate enclosing = self.getEnclosingInstance();
        final StringTemplate argContextST = new StringTemplate(self.getGroup(), "");
        argContextST.setName("<invoke " + self.getName() + " arg context>");
        argContextST.setEnclosingInstance(enclosing);
        argContextST.setArgumentContext(self.getArgumentContext());
        final ActionEvaluator eval = new ActionEvaluator(argContextST, this, null);
        try {
            final Map ac = eval.argList(argumentsAST, self, self.getArgumentContext());
            self.setArgumentContext(ac);
        }
        catch (RecognitionException re) {
            self.error("can't evaluate tree: " + argumentsAST.toStringList(), re);
        }
    }
    
    public static Object convertArrayToList(final Object value) {
        if (value == null) {
            return null;
        }
        if (!value.getClass().isArray()) {
            return value;
        }
        if (value.getClass().getComponentType().isPrimitive()) {
            return new ArrayWrappedInList(value);
        }
        return Arrays.asList((Object[])value);
    }
    
    protected static Object convertAnythingIteratableToIterator(final Object o) {
        Iterator iter = null;
        if (o instanceof Collection) {
            iter = ((Collection)o).iterator();
        }
        else if (o instanceof Map) {
            iter = ((Map)o).values().iterator();
        }
        else if (o instanceof Iterator) {
            iter = (Iterator)o;
        }
        if (iter == null) {
            return o;
        }
        return iter;
    }
    
    protected static Iterator convertAnythingToIterator(final Object o) {
        Iterator iter = null;
        if (o instanceof Collection) {
            iter = ((Collection)o).iterator();
        }
        else if (o instanceof Map) {
            iter = ((Map)o).values().iterator();
        }
        else if (o instanceof Iterator) {
            iter = (Iterator)o;
        }
        if (iter == null) {
            final List singleton = new StringTemplate.STAttributeList(1);
            singleton.add(o);
            return singleton.iterator();
        }
        return iter;
    }
    
    public Object first(Object attribute) {
        if (attribute == null) {
            return null;
        }
        Object f = attribute;
        attribute = convertAnythingIteratableToIterator(attribute);
        if (attribute instanceof Iterator) {
            final Iterator it = (Iterator)attribute;
            if (it.hasNext()) {
                f = it.next();
            }
        }
        return f;
    }
    
    public Object rest(Object attribute) {
        if (attribute == null) {
            return null;
        }
        Object theRest = attribute;
        attribute = convertAnythingIteratableToIterator(attribute);
        if (!(attribute instanceof Iterator)) {
            theRest = null;
            return theRest;
        }
        final List a = new ArrayList();
        final Iterator it = (Iterator)attribute;
        if (!it.hasNext()) {
            return null;
        }
        it.next();
        while (it.hasNext()) {
            final Object o = it.next();
            if (o != null) {
                a.add(o);
            }
        }
        return a;
    }
    
    public Object last(Object attribute) {
        if (attribute == null) {
            return null;
        }
        Object last = attribute;
        attribute = convertAnythingIteratableToIterator(attribute);
        if (attribute instanceof Iterator) {
            final Iterator it = (Iterator)attribute;
            while (it.hasNext()) {
                last = it.next();
            }
        }
        return last;
    }
    
    public Object strip(Object attribute) {
        if (attribute == null) {
            return null;
        }
        attribute = convertAnythingIteratableToIterator(attribute);
        if (attribute instanceof Iterator) {
            final List a = new ArrayList();
            final Iterator it = (Iterator)attribute;
            while (it.hasNext()) {
                final Object o = it.next();
                if (o != null) {
                    a.add(o);
                }
            }
            return a;
        }
        return attribute;
    }
    
    public Object trunc(Object attribute) {
        if (attribute == null) {
            return null;
        }
        attribute = convertAnythingIteratableToIterator(attribute);
        if (attribute instanceof Iterator) {
            final List a = new ArrayList();
            final Iterator it = (Iterator)attribute;
            while (it.hasNext()) {
                final Object o = it.next();
                if (it.hasNext()) {
                    a.add(o);
                }
            }
            return a;
        }
        return null;
    }
    
    public Object length(final Object attribute) {
        if (attribute == null) {
            return new Integer(0);
        }
        int i = 1;
        if (attribute instanceof Map) {
            i = ((Map)attribute).size();
        }
        else if (attribute instanceof Collection) {
            i = ((Collection)attribute).size();
        }
        else if (attribute instanceof Object[]) {
            final Object[] list = (Object[])attribute;
            i = list.length;
        }
        else if (attribute instanceof int[]) {
            final int[] list2 = (int[])attribute;
            i = list2.length;
        }
        else if (attribute instanceof long[]) {
            final long[] list3 = (long[])attribute;
            i = list3.length;
        }
        else if (attribute instanceof float[]) {
            final float[] list4 = (float[])attribute;
            i = list4.length;
        }
        else if (attribute instanceof double[]) {
            final double[] list5 = (double[])attribute;
            i = list5.length;
        }
        else if (attribute instanceof Iterator) {
            final Iterator it = (Iterator)attribute;
            i = 0;
            while (it.hasNext()) {
                it.next();
                ++i;
            }
        }
        return new Integer(i);
    }
    
    public Object getOption(final String name) {
        Object value = null;
        if (this.options != null) {
            value = this.options.get(name);
            if (value == "empty expr option") {
                return ASTExpr.defaultOptionValues.get(name);
            }
        }
        return value;
    }
    
    public String toString() {
        return this.exprTree.toStringList();
    }
    
    static {
        MAP_KEY_VALUE = new StringTemplate();
        defaultOptionValues = new HashMap() {
            {
                this.put("anchor", new StringTemplateAST(34, "true"));
                this.put("wrap", new StringTemplateAST(34, "\n"));
            }
        };
        supportedOptions = new HashSet() {
            {
                this.add("anchor");
                this.add("format");
                this.add("null");
                this.add("separator");
                this.add("wrap");
            }
        };
    }
}
