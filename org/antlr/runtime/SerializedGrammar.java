// 
// Decompiled by Procyon v0.5.36
// 

package org.antlr.runtime;

import java.util.ArrayList;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.List;

public class SerializedGrammar
{
    public static final String COOKIE = "$ANTLR";
    public static final int FORMAT_VERSION = 1;
    public String name;
    public char type;
    public List rules;
    
    public SerializedGrammar(final String filename) throws IOException {
        System.out.println("loading " + filename);
        final FileInputStream fis = new FileInputStream(filename);
        final BufferedInputStream bos = new BufferedInputStream(fis);
        final DataInputStream in = new DataInputStream(bos);
        this.readFile(in);
        in.close();
    }
    
    protected void readFile(final DataInputStream in) throws IOException {
        final String cookie = this.readString(in);
        if (!cookie.equals("$ANTLR")) {
            throw new IOException("not a serialized grammar file");
        }
        final int version = in.readByte();
        final char grammarType = (char)in.readByte();
        this.type = grammarType;
        final String grammarName = this.readString(in);
        this.name = grammarName;
        System.out.println(grammarType + " grammar " + grammarName);
        final int numRules = in.readShort();
        System.out.println("num rules = " + numRules);
        this.rules = this.readRules(in, numRules);
    }
    
    protected List readRules(final DataInputStream in, final int numRules) throws IOException {
        final List rules = new ArrayList();
        for (int i = 0; i < numRules; ++i) {
            final Rule r = this.readRule(in);
            rules.add(r);
        }
        return rules;
    }
    
    protected Rule readRule(final DataInputStream in) throws IOException {
        final byte R = in.readByte();
        if (R != 82) {
            throw new IOException("missing R on start of rule");
        }
        final String name = this.readString(in);
        System.out.println("rule: " + name);
        final byte B = in.readByte();
        final Block b = this.readBlock(in);
        final byte period = in.readByte();
        if (period != 46) {
            throw new IOException("missing . on end of rule");
        }
        return new Rule(name, b);
    }
    
    protected Block readBlock(final DataInputStream in) throws IOException {
        final int nalts = in.readShort();
        final List[] alts = new List[nalts];
        for (int i = 0; i < nalts; ++i) {
            final List alt = this.readAlt(in);
            alts[i] = alt;
        }
        return new Block(alts);
    }
    
    protected List readAlt(final DataInputStream in) throws IOException {
        final List alt = new ArrayList();
        final byte A = in.readByte();
        if (A != 65) {
            throw new IOException("missing A on start of alt");
        }
        for (byte cmd = in.readByte(); cmd != 59; cmd = in.readByte()) {
            switch (cmd) {
                case 116: {
                    final int ttype = in.readShort();
                    alt.add(new TokenRef(ttype));
                    break;
                }
                case 114: {
                    final int ruleIndex = in.readShort();
                    alt.add(new RuleRef(ruleIndex));
                }
                case 45: {
                    final int from = in.readChar();
                    final int to = in.readChar();
                    break;
                }
                case 126: {
                    final int notThisTokenType = in.readShort();
                    break;
                }
                case 66: {
                    final Block b = this.readBlock(in);
                    alt.add(b);
                    break;
                }
            }
        }
        return alt;
    }
    
    protected String readString(final DataInputStream in) throws IOException {
        byte c = in.readByte();
        final StringBuffer buf = new StringBuffer();
        while (c != 59) {
            buf.append((char)c);
            c = in.readByte();
        }
        return buf.toString();
    }
    
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append(this.type + " grammar " + this.name);
        buf.append(this.rules);
        return buf.toString();
    }
    
    class Rule
    {
        String name;
        Block block;
        
        public Rule(final String name, final Block block) {
            this.name = name;
            this.block = block;
        }
        
        public String toString() {
            return this.name + ":" + this.block;
        }
    }
    
    class Block
    {
        List[] alts;
        
        public Block(final List[] alts) {
            this.alts = alts;
        }
        
        public String toString() {
            final StringBuffer buf = new StringBuffer();
            buf.append("(");
            for (int i = 0; i < this.alts.length; ++i) {
                final List alt = this.alts[i];
                if (i > 0) {
                    buf.append("|");
                }
                buf.append(alt.toString());
            }
            buf.append(")");
            return buf.toString();
        }
    }
    
    class TokenRef
    {
        int ttype;
        
        public TokenRef(final int ttype) {
            this.ttype = ttype;
        }
        
        public String toString() {
            return String.valueOf(this.ttype);
        }
    }
    
    class RuleRef
    {
        int ruleIndex;
        
        public RuleRef(final int ruleIndex) {
            this.ruleIndex = ruleIndex;
        }
        
        public String toString() {
            return String.valueOf(this.ruleIndex);
        }
    }
}
