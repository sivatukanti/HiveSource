// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate;

import org.antlr.stringtemplate.language.AngleBracketTemplateLexer;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.List;

public class PathGroupLoader implements StringTemplateGroupLoader
{
    protected List dirs;
    protected StringTemplateErrorListener errors;
    String fileCharEncoding;
    
    public PathGroupLoader(final StringTemplateErrorListener errors) {
        this.dirs = null;
        this.errors = null;
        this.fileCharEncoding = System.getProperty("file.encoding");
        this.errors = errors;
    }
    
    public PathGroupLoader(final String dirStr, final StringTemplateErrorListener errors) {
        this.dirs = null;
        this.errors = null;
        this.fileCharEncoding = System.getProperty("file.encoding");
        this.errors = errors;
        final StringTokenizer tokenizer = new StringTokenizer(dirStr, ":", false);
        while (tokenizer.hasMoreElements()) {
            final String dir = (String)tokenizer.nextElement();
            if (this.dirs == null) {
                this.dirs = new ArrayList();
            }
            this.dirs.add(dir);
        }
    }
    
    public StringTemplateGroup loadGroup(final String groupName, final Class templateLexer, final StringTemplateGroup superGroup) {
        StringTemplateGroup group = null;
        BufferedReader br = null;
        Class lexer = AngleBracketTemplateLexer.class;
        if (templateLexer != null) {
            lexer = templateLexer;
        }
        try {
            br = this.locate(groupName + ".stg");
            if (br == null) {
                this.error("no such group file " + groupName + ".stg");
                return null;
            }
            group = new StringTemplateGroup(br, lexer, this.errors, superGroup);
            br.close();
            br = null;
        }
        catch (IOException ioe) {
            this.error("can't load group " + groupName, ioe);
            if (br != null) {
                try {
                    br.close();
                }
                catch (IOException ioe2) {
                    this.error("Cannot close template group file: " + groupName + ".stg", ioe2);
                }
            }
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                }
                catch (IOException ioe3) {
                    this.error("Cannot close template group file: " + groupName + ".stg", ioe3);
                }
            }
        }
        return group;
    }
    
    public StringTemplateGroup loadGroup(final String groupName, final StringTemplateGroup superGroup) {
        return this.loadGroup(groupName, null, superGroup);
    }
    
    public StringTemplateGroup loadGroup(final String groupName) {
        return this.loadGroup(groupName, null);
    }
    
    public StringTemplateGroupInterface loadInterface(final String interfaceName) {
        StringTemplateGroupInterface I = null;
        try {
            final BufferedReader br = this.locate(interfaceName + ".sti");
            if (br == null) {
                this.error("no such interface file " + interfaceName + ".sti");
                return null;
            }
            I = new StringTemplateGroupInterface(br, this.errors);
        }
        catch (IOException ioe) {
            this.error("can't load interface " + interfaceName, ioe);
        }
        return I;
    }
    
    protected BufferedReader locate(final String name) throws IOException {
        for (int i = 0; i < this.dirs.size(); ++i) {
            final String dir = this.dirs.get(i);
            final String fileName = dir + "/" + name;
            if (new File(fileName).exists()) {
                final FileInputStream fis = new FileInputStream(fileName);
                final InputStreamReader isr = this.getInputStreamReader(fis);
                return new BufferedReader(isr);
            }
        }
        return null;
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
    
    public void error(final String msg) {
        this.error(msg, null);
    }
    
    public void error(final String msg, final Exception e) {
        if (this.errors != null) {
            this.errors.error(msg, e);
        }
        else {
            System.err.println("StringTemplate: " + msg);
            if (e != null) {
                e.printStackTrace();
            }
        }
    }
}
