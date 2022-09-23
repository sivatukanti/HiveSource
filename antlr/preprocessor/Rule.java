// 
// Decompiled by Procyon v0.5.36
// 

package antlr.preprocessor;

import java.util.Enumeration;
import antlr.collections.impl.IndexedVector;

class Rule
{
    protected String name;
    protected String block;
    protected String args;
    protected String returnValue;
    protected String throwsSpec;
    protected String initAction;
    protected IndexedVector options;
    protected String visibility;
    protected Grammar enclosingGrammar;
    protected boolean bang;
    
    public Rule(final String name, final String block, final IndexedVector options, final Grammar enclosingGrammar) {
        this.bang = false;
        this.name = name;
        this.block = block;
        this.options = options;
        this.setEnclosingGrammar(enclosingGrammar);
    }
    
    public String getArgs() {
        return this.args;
    }
    
    public boolean getBang() {
        return this.bang;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getReturnValue() {
        return this.returnValue;
    }
    
    public String getVisibility() {
        return this.visibility;
    }
    
    public boolean narrowerVisibility(final Rule rule) {
        if (this.visibility.equals("public")) {
            return !rule.equals("public");
        }
        if (this.visibility.equals("protected")) {
            return rule.equals("private");
        }
        return this.visibility.equals("private") && false;
    }
    
    public boolean sameSignature(final Rule rule) {
        boolean equals = true;
        boolean equals2 = true;
        final boolean equals3 = this.name.equals(rule.getName());
        if (this.args != null) {
            equals = this.args.equals(rule.getArgs());
        }
        if (this.returnValue != null) {
            equals2 = this.returnValue.equals(rule.getReturnValue());
        }
        return equals3 && equals && equals2;
    }
    
    public void setArgs(final String args) {
        this.args = args;
    }
    
    public void setBang() {
        this.bang = true;
    }
    
    public void setEnclosingGrammar(final Grammar enclosingGrammar) {
        this.enclosingGrammar = enclosingGrammar;
    }
    
    public void setInitAction(final String initAction) {
        this.initAction = initAction;
    }
    
    public void setOptions(final IndexedVector options) {
        this.options = options;
    }
    
    public void setReturnValue(final String returnValue) {
        this.returnValue = returnValue;
    }
    
    public void setThrowsSpec(final String throwsSpec) {
        this.throwsSpec = throwsSpec;
    }
    
    public void setVisibility(final String visibility) {
        this.visibility = visibility;
    }
    
    public String toString() {
        String str = "" + ((this.visibility == null) ? "" : (this.visibility + " ")) + this.name + (this.getBang() ? "!" : "") + ((this.args == null) ? "" : this.args) + " " + ((this.returnValue == null) ? "" : ("returns " + this.returnValue)) + this.throwsSpec;
        if (this.options != null) {
            String s = str + System.getProperty("line.separator") + "options {" + System.getProperty("line.separator");
            final Enumeration elements = this.options.elements();
            while (elements.hasMoreElements()) {
                s = s + elements.nextElement() + System.getProperty("line.separator");
            }
            str = s + "}" + System.getProperty("line.separator");
        }
        if (this.initAction != null) {
            str = str + this.initAction + System.getProperty("line.separator");
        }
        return str + this.block;
    }
}
