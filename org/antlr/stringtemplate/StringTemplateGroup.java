// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate;

import org.antlr.stringtemplate.language.AngleBracketTemplateLexer;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Collections;
import java.lang.reflect.Constructor;
import java.io.Writer;
import antlr.TokenStream;
import org.antlr.stringtemplate.language.GroupParser;
import org.antlr.stringtemplate.language.GroupLexer;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.LinkedList;
import java.util.ArrayList;
import java.io.Reader;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.Map;

public class StringTemplateGroup
{
    protected String name;
    protected Map templates;
    protected Map maps;
    protected Class templateLexerClass;
    protected static Class defaultTemplateLexerClass;
    protected String rootDir;
    protected static Map nameToGroupMap;
    protected static Map nameToInterfaceMap;
    protected StringTemplateGroup superGroup;
    protected List interfaces;
    protected boolean templatesDefinedInGroupFile;
    protected Class userSpecifiedWriter;
    protected boolean debugTemplateOutput;
    protected Set noDebugStartStopStrings;
    protected Map attributeRenderers;
    private static StringTemplateGroupLoader groupLoader;
    protected StringTemplateErrorListener listener;
    public static StringTemplateErrorListener DEFAULT_ERROR_LISTENER;
    protected static final StringTemplate NOT_FOUND_ST;
    protected int refreshIntervalInSeconds;
    protected long lastCheckedDisk;
    String fileCharEncoding;
    
    public StringTemplateGroup(final String name, final String rootDir) {
        this(name, rootDir, DefaultTemplateLexer.class);
    }
    
    public StringTemplateGroup(final String name, final String rootDir, final Class lexer) {
        this.templates = new HashMap();
        this.maps = new HashMap();
        this.templateLexerClass = null;
        this.rootDir = null;
        this.superGroup = null;
        this.interfaces = null;
        this.templatesDefinedInGroupFile = false;
        this.debugTemplateOutput = false;
        this.listener = StringTemplateGroup.DEFAULT_ERROR_LISTENER;
        this.refreshIntervalInSeconds = 2147483;
        this.lastCheckedDisk = 0L;
        this.fileCharEncoding = System.getProperty("file.encoding");
        this.name = name;
        this.rootDir = rootDir;
        this.lastCheckedDisk = System.currentTimeMillis();
        StringTemplateGroup.nameToGroupMap.put(name, this);
        this.templateLexerClass = lexer;
    }
    
    public StringTemplateGroup(final String name) {
        this(name, null, null);
    }
    
    public StringTemplateGroup(final String name, final Class lexer) {
        this(name, null, lexer);
    }
    
    public StringTemplateGroup(final Reader r) {
        this(r, AngleBracketTemplateLexer.class, StringTemplateGroup.DEFAULT_ERROR_LISTENER, null);
    }
    
    public StringTemplateGroup(final Reader r, final StringTemplateErrorListener errors) {
        this(r, AngleBracketTemplateLexer.class, errors, null);
    }
    
    public StringTemplateGroup(final Reader r, final Class lexer) {
        this(r, lexer, null, null);
    }
    
    public StringTemplateGroup(final Reader r, final Class lexer, final StringTemplateErrorListener errors) {
        this(r, lexer, errors, null);
    }
    
    public StringTemplateGroup(final Reader r, Class lexer, final StringTemplateErrorListener errors, final StringTemplateGroup superGroup) {
        this.templates = new HashMap();
        this.maps = new HashMap();
        this.templateLexerClass = null;
        this.rootDir = null;
        this.superGroup = null;
        this.interfaces = null;
        this.templatesDefinedInGroupFile = false;
        this.debugTemplateOutput = false;
        this.listener = StringTemplateGroup.DEFAULT_ERROR_LISTENER;
        this.refreshIntervalInSeconds = 2147483;
        this.lastCheckedDisk = 0L;
        this.fileCharEncoding = System.getProperty("file.encoding");
        this.templatesDefinedInGroupFile = true;
        if (lexer == null) {
            lexer = AngleBracketTemplateLexer.class;
        }
        this.templateLexerClass = lexer;
        if (errors != null) {
            this.listener = errors;
        }
        this.setSuperGroup(superGroup);
        this.parseGroup(r);
        StringTemplateGroup.nameToGroupMap.put(this.name, this);
        this.verifyInterfaceImplementations();
    }
    
    public Class getTemplateLexerClass() {
        if (this.templateLexerClass != null) {
            return this.templateLexerClass;
        }
        return StringTemplateGroup.defaultTemplateLexerClass;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setSuperGroup(final StringTemplateGroup superGroup) {
        this.superGroup = superGroup;
    }
    
    public void setSuperGroup(final String superGroupName) {
        StringTemplateGroup superGroup = StringTemplateGroup.nameToGroupMap.get(superGroupName);
        if (superGroup != null) {
            this.setSuperGroup(superGroup);
            return;
        }
        superGroup = loadGroup(superGroupName, this.templateLexerClass, null);
        if (superGroup != null) {
            StringTemplateGroup.nameToGroupMap.put(superGroupName, superGroup);
            this.setSuperGroup(superGroup);
        }
        else if (StringTemplateGroup.groupLoader == null) {
            this.listener.error("no group loader registered", null);
        }
    }
    
    public void implementInterface(final StringTemplateGroupInterface I) {
        if (this.interfaces == null) {
            this.interfaces = new ArrayList();
        }
        this.interfaces.add(I);
    }
    
    public void implementInterface(final String interfaceName) {
        StringTemplateGroupInterface I = StringTemplateGroup.nameToInterfaceMap.get(interfaceName);
        if (I != null) {
            this.implementInterface(I);
            return;
        }
        I = loadInterface(interfaceName);
        if (I != null) {
            StringTemplateGroup.nameToInterfaceMap.put(interfaceName, I);
            this.implementInterface(I);
        }
        else if (StringTemplateGroup.groupLoader == null) {
            this.listener.error("no group loader registered", null);
        }
    }
    
    public StringTemplateGroup getSuperGroup() {
        return this.superGroup;
    }
    
    public String getGroupHierarchyStackString() {
        final List groupNames = new LinkedList();
        for (StringTemplateGroup p = this; p != null; p = p.superGroup) {
            groupNames.add(0, p.name);
        }
        return groupNames.toString().replaceAll(",", "");
    }
    
    public String getRootDir() {
        return this.rootDir;
    }
    
    public void setRootDir(final String rootDir) {
        this.rootDir = rootDir;
    }
    
    public StringTemplate createStringTemplate() {
        final StringTemplate st = new StringTemplate();
        return st;
    }
    
    protected StringTemplate getInstanceOf(final StringTemplate enclosingInstance, final String name) throws IllegalArgumentException {
        final StringTemplate st = this.lookupTemplate(enclosingInstance, name);
        if (st != null) {
            final StringTemplate instanceST = st.getInstanceOf();
            return instanceST;
        }
        return null;
    }
    
    public StringTemplate getInstanceOf(final String name) {
        return this.getInstanceOf(null, name);
    }
    
    public StringTemplate getInstanceOf(final String name, final Map attributes) {
        final StringTemplate st = this.getInstanceOf(name);
        st.attributes = attributes;
        return st;
    }
    
    public StringTemplate getEmbeddedInstanceOf(final StringTemplate enclosingInstance, final String name) throws IllegalArgumentException {
        StringTemplate st = null;
        if (name.startsWith("super.")) {
            st = enclosingInstance.getNativeGroup().getInstanceOf(enclosingInstance, name);
        }
        else {
            st = this.getInstanceOf(enclosingInstance, name);
        }
        st.setGroup(this);
        st.setEnclosingInstance(enclosingInstance);
        return st;
    }
    
    public synchronized StringTemplate lookupTemplate(final StringTemplate enclosingInstance, String name) throws IllegalArgumentException {
        if (!name.startsWith("super.")) {
            this.checkRefreshInterval();
            StringTemplate st = this.templates.get(name);
            if (st == null) {
                if (!this.templatesDefinedInGroupFile) {
                    st = this.loadTemplateFromBeneathRootDirOrCLASSPATH(this.getFileNameFromTemplateName(name));
                }
                if (st == null && this.superGroup != null) {
                    st = this.superGroup.getInstanceOf(name);
                    if (st != null) {
                        st.setGroup(this);
                    }
                }
                if (st == null) {
                    this.templates.put(name, StringTemplateGroup.NOT_FOUND_ST);
                    String context = "";
                    if (enclosingInstance != null) {
                        context = "; context is " + enclosingInstance.getEnclosingInstanceStackString();
                    }
                    final String hier = this.getGroupHierarchyStackString();
                    context = context + "; group hierarchy is " + hier;
                    throw new IllegalArgumentException("Can't find template " + this.getFileNameFromTemplateName(name) + context);
                }
                this.templates.put(name, st);
            }
            else if (st == StringTemplateGroup.NOT_FOUND_ST) {
                return null;
            }
            return st;
        }
        if (this.superGroup != null) {
            final int dot = name.indexOf(46);
            name = name.substring(dot + 1, name.length());
            final StringTemplate superScopeST = this.superGroup.lookupTemplate(enclosingInstance, name);
            return superScopeST;
        }
        throw new IllegalArgumentException(this.getName() + " has no super group; invalid template: " + name);
    }
    
    public StringTemplate lookupTemplate(final String name) {
        return this.lookupTemplate(null, name);
    }
    
    protected void checkRefreshInterval() {
        if (this.templatesDefinedInGroupFile) {
            return;
        }
        final boolean timeToFlush = this.refreshIntervalInSeconds == 0 || System.currentTimeMillis() - this.lastCheckedDisk >= this.refreshIntervalInSeconds * 1000;
        if (timeToFlush) {
            this.templates.clear();
            this.lastCheckedDisk = System.currentTimeMillis();
        }
    }
    
    protected StringTemplate loadTemplate(final String name, final BufferedReader r) throws IOException {
        final String nl = System.getProperty("line.separator");
        final StringBuffer buf = new StringBuffer(300);
        String line;
        while ((line = r.readLine()) != null) {
            buf.append(line);
            buf.append(nl);
        }
        final String pattern = buf.toString().trim();
        if (pattern.length() == 0) {
            this.error("no text in template '" + name + "'");
            return null;
        }
        return this.defineTemplate(name, pattern);
    }
    
    protected StringTemplate loadTemplateFromBeneathRootDirOrCLASSPATH(final String fileName) {
        StringTemplate template = null;
        final String name = this.getTemplateNameFromFileName(fileName);
        if (this.rootDir != null) {
            template = this.loadTemplate(name, this.rootDir + "/" + fileName);
            return template;
        }
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream is = cl.getResourceAsStream(fileName);
        if (is == null) {
            cl = this.getClass().getClassLoader();
            is = cl.getResourceAsStream(fileName);
        }
        if (is == null) {
            return null;
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(this.getInputStreamReader(is));
            template = this.loadTemplate(name, br);
        }
        catch (IOException ioe) {
            this.error("Problem reading template file: " + fileName, ioe);
            if (br != null) {
                try {
                    br.close();
                }
                catch (IOException ioe2) {
                    this.error("Cannot close template file: " + fileName, ioe2);
                }
            }
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                }
                catch (IOException ioe3) {
                    this.error("Cannot close template file: " + fileName, ioe3);
                }
            }
        }
        return template;
    }
    
    public String getFileNameFromTemplateName(final String templateName) {
        return templateName + ".st";
    }
    
    public String getTemplateNameFromFileName(final String fileName) {
        String name = fileName;
        final int suffix = name.lastIndexOf(".st");
        if (suffix >= 0) {
            name = name.substring(0, suffix);
        }
        return name;
    }
    
    protected StringTemplate loadTemplate(final String name, final String fileName) {
        BufferedReader br = null;
        StringTemplate template = null;
        try {
            final InputStream fin = new FileInputStream(fileName);
            final InputStreamReader isr = this.getInputStreamReader(fin);
            br = new BufferedReader(isr);
            template = this.loadTemplate(name, br);
            br.close();
            br = null;
        }
        catch (IOException ioe) {
            if (br != null) {
                try {
                    br.close();
                }
                catch (IOException ioe2) {
                    this.error("Cannot close template file: " + fileName);
                }
            }
        }
        return template;
    }
    
    protected InputStreamReader getInputStreamReader(final InputStream in) {
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(in, this.fileCharEncoding);
        }
        catch (UnsupportedEncodingException uee) {
            this.error("Invalid file character encoding: " + this.fileCharEncoding);
        }
        return isr;
    }
    
    public String getFileCharEncoding() {
        return this.fileCharEncoding;
    }
    
    public void setFileCharEncoding(final String fileCharEncoding) {
        this.fileCharEncoding = fileCharEncoding;
    }
    
    public synchronized StringTemplate defineTemplate(final String name, final String template) {
        if (name != null && name.indexOf(46) >= 0) {
            throw new IllegalArgumentException("cannot have '.' in template names");
        }
        final StringTemplate st = this.createStringTemplate();
        st.setName(name);
        st.setGroup(this);
        st.setNativeGroup(this);
        st.setTemplate(template);
        st.setErrorListener(this.listener);
        this.templates.put(name, st);
        return st;
    }
    
    public StringTemplate defineRegionTemplate(final String enclosingTemplateName, final String regionName, final String template, final int type) {
        final String mangledName = this.getMangledRegionName(enclosingTemplateName, regionName);
        final StringTemplate regionST = this.defineTemplate(mangledName, template);
        regionST.setIsRegion(true);
        regionST.setRegionDefType(type);
        return regionST;
    }
    
    public StringTemplate defineRegionTemplate(final StringTemplate enclosingTemplate, final String regionName, final String template, final int type) {
        final StringTemplate regionST = this.defineRegionTemplate(enclosingTemplate.getOutermostName(), regionName, template, type);
        enclosingTemplate.getOutermostEnclosingInstance().addRegionName(regionName);
        return regionST;
    }
    
    public StringTemplate defineImplicitRegionTemplate(final StringTemplate enclosingTemplate, final String name) {
        return this.defineRegionTemplate(enclosingTemplate, name, "", 1);
    }
    
    public String getMangledRegionName(final String enclosingTemplateName, final String name) {
        return "region__" + enclosingTemplateName + "__" + name;
    }
    
    public String getUnMangledTemplateName(final String mangledName) {
        return mangledName.substring("region__".length(), mangledName.lastIndexOf("__"));
    }
    
    public synchronized StringTemplate defineTemplateAlias(final String name, final String target) {
        final StringTemplate targetST = this.getTemplateDefinition(target);
        if (targetST == null) {
            this.error("cannot alias " + name + " to undefined template: " + target);
            return null;
        }
        this.templates.put(name, targetST);
        return targetST;
    }
    
    public synchronized boolean isDefinedInThisGroup(final String name) {
        final StringTemplate st = this.templates.get(name);
        return st != null && (!st.isRegion() || st.getRegionDefType() != 1);
    }
    
    public synchronized StringTemplate getTemplateDefinition(final String name) {
        return this.templates.get(name);
    }
    
    public boolean isDefined(final String name) {
        try {
            return this.lookupTemplate(name) != null;
        }
        catch (IllegalArgumentException iae) {
            return false;
        }
    }
    
    protected void parseGroup(final Reader r) {
        try {
            final GroupLexer lexer = new GroupLexer(r);
            final GroupParser parser = new GroupParser(lexer);
            parser.group(this);
        }
        catch (Exception e) {
            String name = "<unknown>";
            if (this.getName() != null) {
                name = this.getName();
            }
            this.error("problem parsing group " + name + ": " + e, e);
        }
    }
    
    protected void verifyInterfaceImplementations() {
        for (int i = 0; this.interfaces != null && i < this.interfaces.size(); ++i) {
            final StringTemplateGroupInterface I = this.interfaces.get(i);
            final List missing = I.getMissingTemplates(this);
            final List mismatched = I.getMismatchedTemplates(this);
            if (missing != null) {
                this.error("group " + this.getName() + " does not satisfy interface " + I.getName() + ": missing templates " + missing);
            }
            if (mismatched != null) {
                this.error("group " + this.getName() + " does not satisfy interface " + I.getName() + ": mismatched arguments on these templates " + mismatched);
            }
        }
    }
    
    public int getRefreshInterval() {
        return this.refreshIntervalInSeconds;
    }
    
    public void setRefreshInterval(final int refreshInterval) {
        this.refreshIntervalInSeconds = refreshInterval;
    }
    
    public void setErrorListener(final StringTemplateErrorListener listener) {
        this.listener = listener;
    }
    
    public StringTemplateErrorListener getErrorListener() {
        return this.listener;
    }
    
    public void setStringTemplateWriter(final Class c) {
        this.userSpecifiedWriter = c;
    }
    
    public StringTemplateWriter getStringTemplateWriter(final Writer w) {
        StringTemplateWriter stw = null;
        if (this.userSpecifiedWriter != null) {
            try {
                final Constructor ctor = this.userSpecifiedWriter.getConstructor(Writer.class);
                stw = ctor.newInstance(w);
            }
            catch (Exception e) {
                this.error("problems getting StringTemplateWriter", e);
            }
        }
        if (stw == null) {
            stw = new AutoIndentWriter(w);
        }
        return stw;
    }
    
    public void setAttributeRenderers(final Map renderers) {
        this.attributeRenderers = renderers;
    }
    
    public void registerRenderer(final Class attributeClassType, final Object renderer) {
        if (this.attributeRenderers == null) {
            this.attributeRenderers = Collections.synchronizedMap(new HashMap<Object, Object>());
        }
        this.attributeRenderers.put(attributeClassType, renderer);
    }
    
    public AttributeRenderer getAttributeRenderer(final Class attributeClassType) {
        if (this.attributeRenderers != null) {
            AttributeRenderer renderer = this.attributeRenderers.get(attributeClassType);
            if (renderer == null && this.superGroup != null) {
                renderer = this.superGroup.getAttributeRenderer(attributeClassType);
            }
            return renderer;
        }
        if (this.superGroup == null) {
            return null;
        }
        return this.superGroup.getAttributeRenderer(attributeClassType);
    }
    
    public Map getMap(final String name) {
        if (this.maps != null) {
            Map m = this.maps.get(name);
            if (m == null && this.superGroup != null) {
                m = this.superGroup.getMap(name);
            }
            return m;
        }
        if (this.superGroup == null) {
            return null;
        }
        return this.superGroup.getMap(name);
    }
    
    public void defineMap(final String name, final Map mapping) {
        this.maps.put(name, mapping);
    }
    
    public static void registerDefaultLexer(final Class lexerClass) {
        StringTemplateGroup.defaultTemplateLexerClass = lexerClass;
    }
    
    public static void registerGroupLoader(final StringTemplateGroupLoader loader) {
        StringTemplateGroup.groupLoader = loader;
    }
    
    public static StringTemplateGroup loadGroup(final String name) {
        return loadGroup(name, null, null);
    }
    
    public static StringTemplateGroup loadGroup(final String name, final StringTemplateGroup superGroup) {
        return loadGroup(name, null, superGroup);
    }
    
    public static StringTemplateGroup loadGroup(final String name, final Class lexer, final StringTemplateGroup superGroup) {
        if (StringTemplateGroup.groupLoader != null) {
            return StringTemplateGroup.groupLoader.loadGroup(name, lexer, superGroup);
        }
        return null;
    }
    
    public static StringTemplateGroupInterface loadInterface(final String name) {
        if (StringTemplateGroup.groupLoader != null) {
            return StringTemplateGroup.groupLoader.loadInterface(name);
        }
        return null;
    }
    
    public void error(final String msg) {
        this.error(msg, null);
    }
    
    public void error(final String msg, final Exception e) {
        if (this.listener != null) {
            this.listener.error(msg, e);
        }
        else {
            System.err.println("StringTemplate: " + msg);
            if (e != null) {
                e.printStackTrace();
            }
        }
    }
    
    public synchronized Set getTemplateNames() {
        return this.templates.keySet();
    }
    
    public void emitDebugStartStopStrings(final boolean emit) {
        this.debugTemplateOutput = emit;
    }
    
    public void doNotEmitDebugStringsForTemplate(final String templateName) {
        if (this.noDebugStartStopStrings == null) {
            this.noDebugStartStopStrings = new HashSet();
        }
        this.noDebugStartStopStrings.add(templateName);
    }
    
    public void emitTemplateStartDebugString(final StringTemplate st, final StringTemplateWriter out) throws IOException {
        if (this.noDebugStartStopStrings == null || !this.noDebugStartStopStrings.contains(st.getName())) {
            String groupPrefix = "";
            if (!st.getName().startsWith("if") && !st.getName().startsWith("else")) {
                if (st.getNativeGroup() != null) {
                    groupPrefix = st.getNativeGroup().getName() + ".";
                }
                else {
                    groupPrefix = st.getGroup().getName() + ".";
                }
            }
            out.write("<" + groupPrefix + st.getName() + ">");
        }
    }
    
    public void emitTemplateStopDebugString(final StringTemplate st, final StringTemplateWriter out) throws IOException {
        if (this.noDebugStartStopStrings == null || !this.noDebugStartStopStrings.contains(st.getName())) {
            String groupPrefix = "";
            if (!st.getName().startsWith("if") && !st.getName().startsWith("else")) {
                if (st.getNativeGroup() != null) {
                    groupPrefix = st.getNativeGroup().getName() + ".";
                }
                else {
                    groupPrefix = st.getGroup().getName() + ".";
                }
            }
            out.write("</" + groupPrefix + st.getName() + ">");
        }
    }
    
    public String toString() {
        return this.toString(true);
    }
    
    public String toString(final boolean showTemplatePatterns) {
        final StringBuffer buf = new StringBuffer();
        final Set templateNameSet = this.templates.keySet();
        final List sortedNames = new ArrayList(templateNameSet);
        Collections.sort((List<Comparable>)sortedNames);
        final Iterator iter = sortedNames.iterator();
        buf.append("group " + this.getName() + ";\n");
        StringTemplate formalArgs = new StringTemplate("$args;separator=\",\"$");
        while (iter.hasNext()) {
            final String tname = iter.next();
            final StringTemplate st = this.templates.get(tname);
            if (st != StringTemplateGroup.NOT_FOUND_ST) {
                formalArgs = formalArgs.getInstanceOf();
                formalArgs.setAttribute("args", st.getFormalArguments());
                buf.append(tname + "(" + formalArgs + ")");
                if (showTemplatePatterns) {
                    buf.append(" ::= <<");
                    buf.append(st.getTemplate());
                    buf.append(">>\n");
                }
                else {
                    buf.append('\n');
                }
            }
        }
        return buf.toString();
    }
    
    static {
        StringTemplateGroup.defaultTemplateLexerClass = DefaultTemplateLexer.class;
        StringTemplateGroup.nameToGroupMap = Collections.synchronizedMap(new HashMap<Object, Object>());
        StringTemplateGroup.nameToInterfaceMap = Collections.synchronizedMap(new HashMap<Object, Object>());
        StringTemplateGroup.groupLoader = null;
        StringTemplateGroup.DEFAULT_ERROR_LISTENER = new StringTemplateErrorListener() {
            public void error(final String s, final Throwable e) {
                System.err.println(s);
                if (e != null) {
                    e.printStackTrace(System.err);
                }
            }
            
            public void warning(final String s) {
                System.out.println(s);
            }
        };
        NOT_FOUND_ST = new StringTemplate();
    }
}
