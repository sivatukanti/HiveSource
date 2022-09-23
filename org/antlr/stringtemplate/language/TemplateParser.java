// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate.language;

import antlr.NoViableAltException;
import antlr.TokenStreamException;
import antlr.Token;
import antlr.ParserSharedInputState;
import antlr.TokenStream;
import antlr.TokenBuffer;
import org.antlr.stringtemplate.StringTemplateGroup;
import antlr.RecognitionException;
import antlr.collections.impl.BitSet;
import org.antlr.stringtemplate.StringTemplate;
import antlr.LLkParser;

public class TemplateParser extends LLkParser implements TemplateParserTokenTypes
{
    protected StringTemplate self;
    public static final String[] _tokenNames;
    public static final BitSet _tokenSet_0;
    public static final BitSet _tokenSet_1;
    
    public void reportError(final RecognitionException e) {
        final StringTemplateGroup group = this.self.getGroup();
        if (group == StringTemplate.defaultGroup) {
            this.self.error("template parse error; template context is " + this.self.getEnclosingInstanceStackString(), e);
        }
        else {
            this.self.error("template parse error in group " + this.self.getGroup().getName() + " line " + this.self.getGroupFileLine() + "; template context is " + this.self.getEnclosingInstanceStackString(), e);
        }
    }
    
    protected TemplateParser(final TokenBuffer tokenBuf, final int k) {
        super(tokenBuf, k);
        this.tokenNames = TemplateParser._tokenNames;
    }
    
    public TemplateParser(final TokenBuffer tokenBuf) {
        this(tokenBuf, 1);
    }
    
    protected TemplateParser(final TokenStream lexer, final int k) {
        super(lexer, k);
        this.tokenNames = TemplateParser._tokenNames;
    }
    
    public TemplateParser(final TokenStream lexer) {
        this(lexer, 1);
    }
    
    public TemplateParser(final ParserSharedInputState state) {
        super(state, 1);
        this.tokenNames = TemplateParser._tokenNames;
    }
    
    public final void template(final StringTemplate self) throws RecognitionException, TokenStreamException {
        Token s = null;
        Token nl = null;
        this.self = self;
        Label_0175: {
            try {
                while (true) {
                    switch (this.LA(1)) {
                        case 4: {
                            s = this.LT(1);
                            this.match(4);
                            self.addChunk(new StringRef(self, s.getText()));
                            continue;
                        }
                        case 5: {
                            nl = this.LT(1);
                            this.match(5);
                            if (this.LA(1) != 9 && this.LA(1) != 10) {
                                self.addChunk(new NewlineRef(self, nl.getText()));
                                continue;
                            }
                            continue;
                        }
                        case 6:
                        case 7:
                        case 11:
                        case 12: {
                            this.action(self);
                            continue;
                        }
                        default: {
                            break Label_0175;
                        }
                    }
                }
            }
            catch (RecognitionException ex) {
                this.reportError(ex);
                this.recover(ex, TemplateParser._tokenSet_0);
            }
        }
    }
    
    public final void action(final StringTemplate self) throws RecognitionException, TokenStreamException {
        Token a = null;
        Token i = null;
        Token ei = null;
        Token rr = null;
        Token rd = null;
        try {
            switch (this.LA(1)) {
                case 6: {
                    a = this.LT(1);
                    this.match(6);
                    final String indent = ((ChunkToken)a).getIndentation();
                    final ASTExpr c = self.parseAction(a.getText());
                    c.setIndentation(indent);
                    self.addChunk(c);
                    break;
                }
                case 7: {
                    i = this.LT(1);
                    this.match(7);
                    final ConditionalExpr c2 = (ConditionalExpr)self.parseAction(i.getText());
                    final StringTemplate subtemplate = new StringTemplate(self.getGroup(), null);
                    subtemplate.setEnclosingInstance(self);
                    subtemplate.setName(i.getText() + "_subtemplate");
                    self.addChunk(c2);
                    this.template(subtemplate);
                    if (c2 != null) {
                        c2.setSubtemplate(subtemplate);
                    }
                    while (this.LA(1) == 8) {
                        ei = this.LT(1);
                        this.match(8);
                        final ASTExpr ec = self.parseAction(ei.getText());
                        final StringTemplate elseIfSubtemplate = new StringTemplate(self.getGroup(), null);
                        elseIfSubtemplate.setEnclosingInstance(self);
                        elseIfSubtemplate.setName(ei.getText() + "_subtemplate");
                        this.template(elseIfSubtemplate);
                        if (c2 != null) {
                            c2.addElseIfSubtemplate(ec, elseIfSubtemplate);
                        }
                    }
                    switch (this.LA(1)) {
                        case 9: {
                            this.match(9);
                            final StringTemplate elseSubtemplate = new StringTemplate(self.getGroup(), null);
                            elseSubtemplate.setEnclosingInstance(self);
                            elseSubtemplate.setName("else_subtemplate");
                            this.template(elseSubtemplate);
                            if (c2 != null) {
                                c2.setElseSubtemplate(elseSubtemplate);
                                break;
                            }
                            break;
                        }
                        case 10: {
                            break;
                        }
                        default: {
                            throw new NoViableAltException(this.LT(1), this.getFilename());
                        }
                    }
                    this.match(10);
                    break;
                }
                case 11: {
                    rr = this.LT(1);
                    this.match(11);
                    final String regionName = rr.getText();
                    String mangledRef = null;
                    boolean err = false;
                    if (regionName.startsWith("super.")) {
                        final String regionRef = regionName.substring("super.".length(), regionName.length());
                        final String templateScope = self.getGroup().getUnMangledTemplateName(self.getName());
                        final StringTemplate scopeST = self.getGroup().lookupTemplate(templateScope);
                        if (scopeST == null) {
                            self.getGroup().error("reference to region within undefined template: " + templateScope);
                            err = true;
                        }
                        if (!scopeST.containsRegionName(regionRef)) {
                            self.getGroup().error("template " + templateScope + " has no region called " + regionRef);
                            err = true;
                        }
                        else {
                            mangledRef = self.getGroup().getMangledRegionName(templateScope, regionRef);
                            mangledRef = "super." + mangledRef;
                        }
                    }
                    else {
                        final StringTemplate regionST = self.getGroup().defineImplicitRegionTemplate(self, regionName);
                        mangledRef = regionST.getName();
                    }
                    if (!err) {
                        final String indent2 = ((ChunkToken)rr).getIndentation();
                        final ASTExpr c3 = self.parseAction(mangledRef + "()");
                        c3.setIndentation(indent2);
                        self.addChunk(c3);
                        break;
                    }
                    break;
                }
                case 12: {
                    rd = this.LT(1);
                    this.match(12);
                    final String combinedNameTemplateStr = rd.getText();
                    final int indexOfDefSymbol = combinedNameTemplateStr.indexOf("::=");
                    if (indexOfDefSymbol >= 1) {
                        final String regionName2 = combinedNameTemplateStr.substring(0, indexOfDefSymbol);
                        final String template = combinedNameTemplateStr.substring(indexOfDefSymbol + 3, combinedNameTemplateStr.length());
                        final StringTemplate regionST2 = self.getGroup().defineRegionTemplate(self, regionName2, template, 2);
                        final String indent3 = ((ChunkToken)rd).getIndentation();
                        final ASTExpr c4 = self.parseAction(regionST2.getName() + "()");
                        c4.setIndentation(indent3);
                        self.addChunk(c4);
                        break;
                    }
                    self.error("embedded region definition screwed up");
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            this.recover(ex, TemplateParser._tokenSet_1);
        }
    }
    
    private static final long[] mk_tokenSet_0() {
        final long[] data = { 1792L, 0L };
        return data;
    }
    
    private static final long[] mk_tokenSet_1() {
        final long[] data = { 8176L, 0L };
        return data;
    }
    
    static {
        _tokenNames = new String[] { "<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD", "LITERAL", "NEWLINE", "ACTION", "IF", "ELSEIF", "ELSE", "ENDIF", "REGION_REF", "REGION_DEF", "EXPR", "TEMPLATE", "IF_EXPR", "ESC_CHAR", "ESC", "HEX", "SUBTEMPLATE", "NESTED_PARENS", "INDENT", "COMMENT", "LINE_BREAK" };
        _tokenSet_0 = new BitSet(mk_tokenSet_0());
        _tokenSet_1 = new BitSet(mk_tokenSet_1());
    }
}
