// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.stringtemplate.language;

import java.util.LinkedHashMap;
import antlr.TokenStreamException;
import antlr.Token;
import antlr.NoViableAltException;
import antlr.ParserSharedInputState;
import antlr.TokenStream;
import antlr.TokenBuffer;
import antlr.RecognitionException;
import antlr.collections.impl.BitSet;
import org.antlr.stringtemplate.StringTemplateGroupInterface;
import antlr.LLkParser;

public class InterfaceParser extends LLkParser implements InterfaceParserTokenTypes
{
    protected StringTemplateGroupInterface groupI;
    public static final String[] _tokenNames;
    public static final BitSet _tokenSet_0;
    public static final BitSet _tokenSet_1;
    public static final BitSet _tokenSet_2;
    
    public void reportError(final RecognitionException e) {
        if (this.groupI != null) {
            this.groupI.error("template group interface parse error", e);
        }
        else {
            System.err.println("template group interface parse error: " + e);
            e.printStackTrace(System.err);
        }
    }
    
    protected InterfaceParser(final TokenBuffer tokenBuf, final int k) {
        super(tokenBuf, k);
        this.tokenNames = InterfaceParser._tokenNames;
    }
    
    public InterfaceParser(final TokenBuffer tokenBuf) {
        this(tokenBuf, 3);
    }
    
    protected InterfaceParser(final TokenStream lexer, final int k) {
        super(lexer, k);
        this.tokenNames = InterfaceParser._tokenNames;
    }
    
    public InterfaceParser(final TokenStream lexer) {
        this(lexer, 3);
    }
    
    public InterfaceParser(final ParserSharedInputState state) {
        super(state, 3);
        this.tokenNames = InterfaceParser._tokenNames;
    }
    
    public final void groupInterface(final StringTemplateGroupInterface groupI) throws RecognitionException, TokenStreamException {
        Token name = null;
        this.groupI = groupI;
        try {
            this.match(4);
            name = this.LT(1);
            this.match(5);
            groupI.setName(name.getText());
            this.match(6);
            int _cnt3 = 0;
            while (this.LA(1) == 5 || this.LA(1) == 7) {
                this.template(groupI);
                ++_cnt3;
            }
            if (_cnt3 < 1) {
                throw new NoViableAltException(this.LT(1), this.getFilename());
            }
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            this.recover(ex, InterfaceParser._tokenSet_0);
        }
    }
    
    public final void template(final StringTemplateGroupInterface groupI) throws RecognitionException, TokenStreamException {
        Token opt = null;
        Token name = null;
        LinkedHashMap formalArgs = new LinkedHashMap();
        String templateName = null;
        try {
            switch (this.LA(1)) {
                case 7: {
                    opt = this.LT(1);
                    this.match(7);
                    break;
                }
                case 5: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            name = this.LT(1);
            this.match(5);
            this.match(8);
            switch (this.LA(1)) {
                case 5: {
                    formalArgs = this.args();
                    break;
                }
                case 9: {
                    break;
                }
                default: {
                    throw new NoViableAltException(this.LT(1), this.getFilename());
                }
            }
            this.match(9);
            this.match(6);
            templateName = name.getText();
            groupI.defineTemplate(templateName, formalArgs, opt != null);
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            this.recover(ex, InterfaceParser._tokenSet_1);
        }
    }
    
    public final LinkedHashMap args() throws RecognitionException, TokenStreamException {
        final LinkedHashMap args = new LinkedHashMap();
        Token a = null;
        Token b = null;
        try {
            a = this.LT(1);
            this.match(5);
            args.put(a.getText(), new FormalArgument(a.getText()));
            while (this.LA(1) == 10) {
                this.match(10);
                b = this.LT(1);
                this.match(5);
                args.put(b.getText(), new FormalArgument(b.getText()));
            }
        }
        catch (RecognitionException ex) {
            this.reportError(ex);
            this.recover(ex, InterfaceParser._tokenSet_2);
        }
        return args;
    }
    
    private static final long[] mk_tokenSet_0() {
        final long[] data = { 2L, 0L };
        return data;
    }
    
    private static final long[] mk_tokenSet_1() {
        final long[] data = { 162L, 0L };
        return data;
    }
    
    private static final long[] mk_tokenSet_2() {
        final long[] data = { 512L, 0L };
        return data;
    }
    
    static {
        _tokenNames = new String[] { "<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD", "\"interface\"", "ID", "SEMI", "\"optional\"", "LPAREN", "RPAREN", "COMMA", "COLON", "SL_COMMENT", "ML_COMMENT", "WS" };
        _tokenSet_0 = new BitSet(mk_tokenSet_0());
        _tokenSet_1 = new BitSet(mk_tokenSet_1());
        _tokenSet_2 = new BitSet(mk_tokenSet_2());
    }
}
