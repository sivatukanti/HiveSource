// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import antlr.TokenStream;
import org.antlr.stringtemplate.language.InterfaceParser;
import org.antlr.stringtemplate.language.InterfaceLexer;
import java.util.LinkedHashMap;
import java.io.Reader;
import java.util.Map;

public class StringTemplateGroupInterface
{
    protected String name;
    protected Map templates;
    protected StringTemplateGroupInterface superInterface;
    protected StringTemplateErrorListener listener;
    public static StringTemplateErrorListener DEFAULT_ERROR_LISTENER;
    
    public StringTemplateGroupInterface(final Reader r) {
        this(r, StringTemplateGroupInterface.DEFAULT_ERROR_LISTENER, null);
    }
    
    public StringTemplateGroupInterface(final Reader r, final StringTemplateErrorListener errors) {
        this(r, errors, null);
    }
    
    public StringTemplateGroupInterface(final Reader r, final StringTemplateErrorListener errors, final StringTemplateGroupInterface superInterface) {
        this.templates = new LinkedHashMap();
        this.superInterface = null;
        this.listener = StringTemplateGroupInterface.DEFAULT_ERROR_LISTENER;
        this.listener = errors;
        this.setSuperInterface(superInterface);
        this.parseInterface(r);
    }
    
    public StringTemplateGroupInterface getSuperInterface() {
        return this.superInterface;
    }
    
    public void setSuperInterface(final StringTemplateGroupInterface superInterface) {
        this.superInterface = superInterface;
    }
    
    protected void parseInterface(final Reader r) {
        try {
            final InterfaceLexer lexer = new InterfaceLexer(r);
            final InterfaceParser parser = new InterfaceParser(lexer);
            parser.groupInterface(this);
        }
        catch (Exception e) {
            String name = "<unknown>";
            if (this.getName() != null) {
                name = this.getName();
            }
            this.error("problem parsing group " + name + ": " + e, e);
        }
    }
    
    public void defineTemplate(final String name, final LinkedHashMap formalArgs, final boolean optional) {
        final TemplateDefinition d = new TemplateDefinition(name, formalArgs, optional);
        this.templates.put(d.name, d);
    }
    
    public List getMissingTemplates(final StringTemplateGroup group) {
        List missing = new ArrayList();
        for (final String name : this.templates.keySet()) {
            final TemplateDefinition d = this.templates.get(name);
            if (!d.optional && !group.isDefined(d.name)) {
                missing.add(d.name);
            }
        }
        if (missing.size() == 0) {
            missing = null;
        }
        return missing;
    }
    
    public List getMismatchedTemplates(final StringTemplateGroup group) {
        List mismatched = new ArrayList();
        for (final String name : this.templates.keySet()) {
            final TemplateDefinition d = this.templates.get(name);
            if (group.isDefined(d.name)) {
                final StringTemplate defST = group.getTemplateDefinition(d.name);
                final Map formalArgs = defST.getFormalArguments();
                boolean ack = false;
                if ((d.formalArgs != null && formalArgs == null) || (d.formalArgs == null && formalArgs != null) || d.formalArgs.size() != formalArgs.size()) {
                    ack = true;
                }
                if (!ack) {
                    for (final String argName : formalArgs.keySet()) {
                        if (d.formalArgs.get(argName) == null) {
                            ack = true;
                            break;
                        }
                    }
                }
                if (!ack) {
                    continue;
                }
                mismatched.add(this.getTemplateSignature(d));
            }
        }
        if (mismatched.size() == 0) {
            mismatched = null;
        }
        return mismatched;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
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
    
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("interface ");
        buf.append(this.getName());
        buf.append(";\n");
        for (final String name : this.templates.keySet()) {
            final TemplateDefinition d = this.templates.get(name);
            buf.append(this.getTemplateSignature(d));
            buf.append(";\n");
        }
        return buf.toString();
    }
    
    protected String getTemplateSignature(final TemplateDefinition d) {
        final StringBuffer buf = new StringBuffer();
        if (d.optional) {
            buf.append("optional ");
        }
        buf.append(d.name);
        if (d.formalArgs != null) {
            final StringBuffer args = new StringBuffer();
            args.append('(');
            int i = 1;
            for (final String name : d.formalArgs.keySet()) {
                if (i > 1) {
                    args.append(", ");
                }
                args.append(name);
                ++i;
            }
            args.append(')');
            buf.append(args);
        }
        else {
            buf.append("()");
        }
        return buf.toString();
    }
    
    static {
        StringTemplateGroupInterface.DEFAULT_ERROR_LISTENER = new StringTemplateErrorListener() {
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
    }
    
    static class TemplateDefinition
    {
        public String name;
        public LinkedHashMap formalArgs;
        public boolean optional;
        
        public TemplateDefinition(final String name, final LinkedHashMap formalArgs, final boolean optional) {
            this.optional = false;
            this.name = name;
            this.formalArgs = formalArgs;
            this.optional = optional;
        }
    }
}
