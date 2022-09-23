// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

import antlr.collections.impl.ASTArray;
import antlr.collections.AST;
import java.util.Hashtable;

public class ASTFactory
{
    protected String theASTNodeType;
    protected Class theASTNodeTypeClass;
    protected Hashtable tokenTypeToASTClassMap;
    
    public ASTFactory() {
        this.theASTNodeType = null;
        this.theASTNodeTypeClass = null;
        this.tokenTypeToASTClassMap = null;
    }
    
    public ASTFactory(final Hashtable tokenTypeToASTClassMap) {
        this.theASTNodeType = null;
        this.theASTNodeTypeClass = null;
        this.tokenTypeToASTClassMap = null;
        this.setTokenTypeToASTClassMap(tokenTypeToASTClassMap);
    }
    
    public void setTokenTypeASTNodeType(final int n, final String str) throws IllegalArgumentException {
        if (this.tokenTypeToASTClassMap == null) {
            this.tokenTypeToASTClassMap = new Hashtable();
        }
        if (str == null) {
            this.tokenTypeToASTClassMap.remove(new Integer(n));
            return;
        }
        try {
            this.tokenTypeToASTClassMap.put(new Integer(n), Utils.loadClass(str));
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Invalid class, " + str);
        }
    }
    
    public Class getASTNodeType(final int value) {
        if (this.tokenTypeToASTClassMap != null) {
            final Class clazz = this.tokenTypeToASTClassMap.get(new Integer(value));
            if (clazz != null) {
                return clazz;
            }
        }
        if (this.theASTNodeTypeClass != null) {
            return this.theASTNodeTypeClass;
        }
        return CommonAST.class;
    }
    
    public void addASTChild(final ASTPair astPair, final AST ast) {
        if (ast != null) {
            if (astPair.root == null) {
                astPair.root = ast;
            }
            else if (astPair.child == null) {
                astPair.root.setFirstChild(ast);
            }
            else {
                astPair.child.setNextSibling(ast);
            }
            astPair.child = ast;
            astPair.advanceChildToEnd();
        }
    }
    
    public AST create() {
        return this.create(0);
    }
    
    public AST create(final int n) {
        final AST create = this.create(this.getASTNodeType(n));
        if (create != null) {
            create.initialize(n, "");
        }
        return create;
    }
    
    public AST create(final int n, final String s) {
        final AST create = this.create(n);
        if (create != null) {
            create.initialize(n, s);
        }
        return create;
    }
    
    public AST create(final int n, final String s, final String s2) {
        final AST create = this.create(s2);
        if (create != null) {
            create.initialize(n, s);
        }
        return create;
    }
    
    public AST create(final AST ast) {
        if (ast == null) {
            return null;
        }
        final AST create = this.create(ast.getType());
        if (create != null) {
            create.initialize(ast);
        }
        return create;
    }
    
    public AST create(final Token token) {
        final AST create = this.create(token.getType());
        if (create != null) {
            create.initialize(token);
        }
        return create;
    }
    
    public AST create(final Token token, final String s) {
        return this.createUsingCtor(token, s);
    }
    
    public AST create(final String str) {
        Class loadClass;
        try {
            loadClass = Utils.loadClass(str);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Invalid class, " + str);
        }
        return this.create(loadClass);
    }
    
    protected AST createUsingCtor(final Token token, final String str) {
        AST create;
        try {
            final Class loadClass = Utils.loadClass(str);
            final Class[] parameterTypes = { Token.class };
            try {
                create = loadClass.getConstructor((Class<?>[])parameterTypes).newInstance(token);
            }
            catch (NoSuchMethodException ex) {
                create = this.create(loadClass);
                if (create != null) {
                    create.initialize(token);
                }
            }
        }
        catch (Exception ex2) {
            throw new IllegalArgumentException("Invalid class or can't make instance, " + str);
        }
        return create;
    }
    
    protected AST create(final Class clazz) {
        AST ast;
        try {
            ast = clazz.newInstance();
        }
        catch (Exception ex) {
            this.error("Can't create AST Node " + clazz.getName());
            return null;
        }
        return ast;
    }
    
    public AST dup(final AST ast) {
        if (ast == null) {
            return null;
        }
        final AST create = this.create(ast.getClass());
        create.initialize(ast);
        return create;
    }
    
    public AST dupList(AST nextSibling) {
        AST ast2;
        final AST ast = ast2 = this.dupTree(nextSibling);
        while (nextSibling != null) {
            nextSibling = nextSibling.getNextSibling();
            ast2.setNextSibling(this.dupTree(nextSibling));
            ast2 = ast2.getNextSibling();
        }
        return ast;
    }
    
    public AST dupTree(final AST ast) {
        final AST dup = this.dup(ast);
        if (ast != null) {
            dup.setFirstChild(this.dupList(ast.getFirstChild()));
        }
        return dup;
    }
    
    public AST make(final AST[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        AST ast = array[0];
        AST ast2 = null;
        if (ast != null) {
            ast.setFirstChild(null);
        }
        for (int i = 1; i < array.length; ++i) {
            if (array[i] != null) {
                if (ast == null) {
                    ast2 = (ast = array[i]);
                }
                else if (ast2 == null) {
                    ast.setFirstChild(array[i]);
                    ast2 = ast.getFirstChild();
                }
                else {
                    ast2.setNextSibling(array[i]);
                    ast2 = ast2.getNextSibling();
                }
                while (ast2.getNextSibling() != null) {
                    ast2 = ast2.getNextSibling();
                }
            }
        }
        return ast;
    }
    
    public AST make(final ASTArray astArray) {
        return this.make(astArray.array);
    }
    
    public void makeASTRoot(final ASTPair astPair, final AST root) {
        if (root != null) {
            root.addChild(astPair.root);
            astPair.child = astPair.root;
            astPair.advanceChildToEnd();
            astPair.root = root;
        }
    }
    
    public void setASTNodeClass(final Class theASTNodeTypeClass) {
        if (theASTNodeTypeClass != null) {
            this.theASTNodeTypeClass = theASTNodeTypeClass;
            this.theASTNodeType = theASTNodeTypeClass.getName();
        }
    }
    
    public void setASTNodeClass(final String s) {
        this.theASTNodeType = s;
        try {
            this.theASTNodeTypeClass = Utils.loadClass(s);
        }
        catch (Exception ex) {
            this.error("Can't find/access AST Node type" + s);
        }
    }
    
    public void setASTNodeType(final String astNodeClass) {
        this.setASTNodeClass(astNodeClass);
    }
    
    public Hashtable getTokenTypeToASTClassMap() {
        return this.tokenTypeToASTClassMap;
    }
    
    public void setTokenTypeToASTClassMap(final Hashtable tokenTypeToASTClassMap) {
        this.tokenTypeToASTClassMap = tokenTypeToASTClassMap;
    }
    
    public void error(final String x) {
        System.err.println(x);
    }
}
