// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import java.io.IOException;
import java.io.Writer;
import antlr.collections.impl.ASTEnumerator;
import antlr.collections.ASTEnumeration;
import antlr.collections.impl.Vector;
import java.io.Serializable;
import antlr.collections.AST;

public abstract class BaseAST implements AST, Serializable
{
    protected BaseAST down;
    protected BaseAST right;
    private static boolean verboseStringConversion;
    private static String[] tokenNames;
    
    public void addChild(final AST ast) {
        if (ast == null) {
            return;
        }
        BaseAST baseAST = this.down;
        if (baseAST != null) {
            while (baseAST.right != null) {
                baseAST = baseAST.right;
            }
            baseAST.right = (BaseAST)ast;
        }
        else {
            this.down = (BaseAST)ast;
        }
    }
    
    public int getNumberOfChildren() {
        BaseAST baseAST = this.down;
        final int n = 0;
        if (baseAST != null) {
            int n2;
            for (n2 = 1; baseAST.right != null; baseAST = baseAST.right, ++n2) {}
            return n2;
        }
        return n;
    }
    
    private static void doWorkForFindAll(final AST ast, final Vector vector, final AST ast2, final boolean b) {
        for (AST nextSibling = ast; nextSibling != null; nextSibling = nextSibling.getNextSibling()) {
            if ((b && nextSibling.equalsTreePartial(ast2)) || (!b && nextSibling.equalsTree(ast2))) {
                vector.appendElement(nextSibling);
            }
            if (nextSibling.getFirstChild() != null) {
                doWorkForFindAll(nextSibling.getFirstChild(), vector, ast2, b);
            }
        }
    }
    
    public boolean equals(final AST ast) {
        if (ast == null) {
            return false;
        }
        if ((this.getText() == null && ast.getText() != null) || (this.getText() != null && ast.getText() == null)) {
            return false;
        }
        if (this.getText() == null && ast.getText() == null) {
            return this.getType() == ast.getType();
        }
        return this.getText().equals(ast.getText()) && this.getType() == ast.getType();
    }
    
    public boolean equalsList(AST nextSibling) {
        if (nextSibling == null) {
            return false;
        }
        AST nextSibling2;
        for (nextSibling2 = this; nextSibling2 != null && nextSibling != null; nextSibling2 = nextSibling2.getNextSibling(), nextSibling = nextSibling.getNextSibling()) {
            if (!nextSibling2.equals(nextSibling)) {
                return false;
            }
            if (nextSibling2.getFirstChild() != null) {
                if (!nextSibling2.getFirstChild().equalsList(nextSibling.getFirstChild())) {
                    return false;
                }
            }
            else if (nextSibling.getFirstChild() != null) {
                return false;
            }
        }
        return nextSibling2 == null && nextSibling == null;
    }
    
    public boolean equalsListPartial(AST nextSibling) {
        if (nextSibling == null) {
            return true;
        }
        AST nextSibling2;
        for (nextSibling2 = this; nextSibling2 != null && nextSibling != null; nextSibling2 = nextSibling2.getNextSibling(), nextSibling = nextSibling.getNextSibling()) {
            if (!nextSibling2.equals(nextSibling)) {
                return false;
            }
            if (nextSibling2.getFirstChild() != null && !nextSibling2.getFirstChild().equalsListPartial(nextSibling.getFirstChild())) {
                return false;
            }
        }
        return nextSibling2 != null || nextSibling == null;
    }
    
    public boolean equalsTree(final AST ast) {
        if (!this.equals(ast)) {
            return false;
        }
        if (this.getFirstChild() != null) {
            if (!this.getFirstChild().equalsList(ast.getFirstChild())) {
                return false;
            }
        }
        else if (ast.getFirstChild() != null) {
            return false;
        }
        return true;
    }
    
    public boolean equalsTreePartial(final AST ast) {
        return ast == null || (this.equals(ast) && (this.getFirstChild() == null || this.getFirstChild().equalsListPartial(ast.getFirstChild())));
    }
    
    public ASTEnumeration findAll(final AST ast) {
        final Vector vector = new Vector(10);
        if (ast == null) {
            return null;
        }
        doWorkForFindAll(this, vector, ast, false);
        return new ASTEnumerator(vector);
    }
    
    public ASTEnumeration findAllPartial(final AST ast) {
        final Vector vector = new Vector(10);
        if (ast == null) {
            return null;
        }
        doWorkForFindAll(this, vector, ast, true);
        return new ASTEnumerator(vector);
    }
    
    public AST getFirstChild() {
        return this.down;
    }
    
    public AST getNextSibling() {
        return this.right;
    }
    
    public String getText() {
        return "";
    }
    
    public int getType() {
        return 0;
    }
    
    public int getLine() {
        return 0;
    }
    
    public int getColumn() {
        return 0;
    }
    
    public abstract void initialize(final int p0, final String p1);
    
    public abstract void initialize(final AST p0);
    
    public abstract void initialize(final Token p0);
    
    public void removeChildren() {
        this.down = null;
    }
    
    public void setFirstChild(final AST ast) {
        this.down = (BaseAST)ast;
    }
    
    public void setNextSibling(final AST ast) {
        this.right = (BaseAST)ast;
    }
    
    public void setText(final String s) {
    }
    
    public void setType(final int n) {
    }
    
    public static void setVerboseStringConversion(final boolean verboseStringConversion, final String[] tokenNames) {
        BaseAST.verboseStringConversion = verboseStringConversion;
        BaseAST.tokenNames = tokenNames;
    }
    
    public static String[] getTokenNames() {
        return BaseAST.tokenNames;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (BaseAST.verboseStringConversion && this.getText() != null && !this.getText().equalsIgnoreCase(BaseAST.tokenNames[this.getType()]) && !this.getText().equalsIgnoreCase(StringUtils.stripFrontBack(BaseAST.tokenNames[this.getType()], "\"", "\""))) {
            sb.append('[');
            sb.append(this.getText());
            sb.append(",<");
            sb.append(BaseAST.tokenNames[this.getType()]);
            sb.append(">]");
            return sb.toString();
        }
        return this.getText();
    }
    
    public String toStringList() {
        String string = "";
        if (this.getFirstChild() != null) {
            string += " (";
        }
        String str = string + " " + this.toString();
        if (this.getFirstChild() != null) {
            str += ((BaseAST)this.getFirstChild()).toStringList();
        }
        if (this.getFirstChild() != null) {
            str += " )";
        }
        if (this.getNextSibling() != null) {
            str += ((BaseAST)this.getNextSibling()).toStringList();
        }
        return str;
    }
    
    public String toStringTree() {
        String string = "";
        if (this.getFirstChild() != null) {
            string += " (";
        }
        String s = string + " " + this.toString();
        if (this.getFirstChild() != null) {
            s += ((BaseAST)this.getFirstChild()).toStringList();
        }
        if (this.getFirstChild() != null) {
            s += " )";
        }
        return s;
    }
    
    public static String decode(final String s) {
        final StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            if (char1 == '&') {
                final char char2 = s.charAt(i + 1);
                final char char3 = s.charAt(i + 2);
                final char char4 = s.charAt(i + 3);
                final char char5 = s.charAt(i + 4);
                final char char6 = s.charAt(i + 5);
                if (char2 == 'a' && char3 == 'm' && char4 == 'p' && char5 == ';') {
                    buffer.append("&");
                    i += 5;
                }
                else if (char2 == 'l' && char3 == 't' && char4 == ';') {
                    buffer.append("<");
                    i += 4;
                }
                else if (char2 == 'g' && char3 == 't' && char4 == ';') {
                    buffer.append(">");
                    i += 4;
                }
                else if (char2 == 'q' && char3 == 'u' && char4 == 'o' && char5 == 't' && char6 == ';') {
                    buffer.append("\"");
                    i += 6;
                }
                else if (char2 == 'a' && char3 == 'p' && char4 == 'o' && char5 == 's' && char6 == ';') {
                    buffer.append("'");
                    i += 6;
                }
                else {
                    buffer.append("&");
                }
            }
            else {
                buffer.append(char1);
            }
        }
        return new String(buffer);
    }
    
    public static String encode(final String s) {
        final StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            switch (char1) {
                case 38: {
                    buffer.append("&amp;");
                    break;
                }
                case 60: {
                    buffer.append("&lt;");
                    break;
                }
                case 62: {
                    buffer.append("&gt;");
                    break;
                }
                case 34: {
                    buffer.append("&quot;");
                    break;
                }
                case 39: {
                    buffer.append("&apos;");
                    break;
                }
                default: {
                    buffer.append(char1);
                    break;
                }
            }
        }
        return new String(buffer);
    }
    
    public void xmlSerializeNode(final Writer writer) throws IOException {
        final StringBuffer sb = new StringBuffer(100);
        sb.append("<");
        sb.append(this.getClass().getName() + " ");
        sb.append("text=\"" + encode(this.getText()) + "\" type=\"" + this.getType() + "\"/>");
        writer.write(sb.toString());
    }
    
    public void xmlSerializeRootOpen(final Writer writer) throws IOException {
        final StringBuffer sb = new StringBuffer(100);
        sb.append("<");
        sb.append(this.getClass().getName() + " ");
        sb.append("text=\"" + encode(this.getText()) + "\" type=\"" + this.getType() + "\">\n");
        writer.write(sb.toString());
    }
    
    public void xmlSerializeRootClose(final Writer writer) throws IOException {
        writer.write("</" + this.getClass().getName() + ">\n");
    }
    
    public void xmlSerialize(final Writer writer) throws IOException {
        for (AST nextSibling = this; nextSibling != null; nextSibling = nextSibling.getNextSibling()) {
            if (nextSibling.getFirstChild() == null) {
                ((BaseAST)nextSibling).xmlSerializeNode(writer);
            }
            else {
                ((BaseAST)nextSibling).xmlSerializeRootOpen(writer);
                ((BaseAST)nextSibling.getFirstChild()).xmlSerialize(writer);
                ((BaseAST)nextSibling).xmlSerializeRootClose(writer);
            }
        }
    }
    
    static {
        BaseAST.verboseStringConversion = false;
        BaseAST.tokenNames = null;
    }
}
