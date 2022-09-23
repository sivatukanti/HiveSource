// 
// Decompiled by Procyon v0.5.36
// 

package antlr.preprocessor;

import java.util.Enumeration;
import java.io.IOException;
import antlr.CodeGenerator;
import antlr.Tool;
import antlr.collections.impl.IndexedVector;

class Grammar
{
    protected String name;
    protected String fileName;
    protected String superGrammar;
    protected String type;
    protected IndexedVector rules;
    protected IndexedVector options;
    protected String tokenSection;
    protected String preambleAction;
    protected String memberAction;
    protected Hierarchy hier;
    protected boolean predefined;
    protected boolean alreadyExpanded;
    protected boolean specifiedVocabulary;
    protected String superClass;
    protected String importVocab;
    protected String exportVocab;
    protected Tool antlrTool;
    
    public Grammar(final Tool antlrTool, final String name, final String superGrammar, final IndexedVector rules) {
        this.predefined = false;
        this.alreadyExpanded = false;
        this.specifiedVocabulary = false;
        this.superClass = null;
        this.importVocab = null;
        this.exportVocab = null;
        this.name = name;
        this.superGrammar = superGrammar;
        this.rules = rules;
        this.antlrTool = antlrTool;
    }
    
    public void addOption(final Option option) {
        if (this.options == null) {
            this.options = new IndexedVector();
        }
        this.options.appendElement(option.getName(), option);
    }
    
    public void addRule(final Rule rule) {
        this.rules.appendElement(rule.getName(), rule);
    }
    
    public void expandInPlace() {
        if (this.alreadyExpanded) {
            return;
        }
        final Grammar superGrammar = this.getSuperGrammar();
        if (superGrammar == null) {
            return;
        }
        if (this.exportVocab == null) {
            this.exportVocab = this.getName();
        }
        if (superGrammar.isPredefined()) {
            return;
        }
        superGrammar.expandInPlace();
        this.alreadyExpanded = true;
        this.hier.getFile(this.getFileName()).setExpanded(true);
        final Enumeration elements = superGrammar.getRules().elements();
        while (elements.hasMoreElements()) {
            this.inherit(elements.nextElement(), superGrammar);
        }
        final IndexedVector options = superGrammar.getOptions();
        if (options != null) {
            final Enumeration elements2 = options.elements();
            while (elements2.hasMoreElements()) {
                this.inherit(elements2.nextElement(), superGrammar);
            }
        }
        if ((this.options != null && this.options.getElement("importVocab") == null) || this.options == null) {
            this.addOption(new Option("importVocab", superGrammar.exportVocab + ";", this));
            final String pathToFile = this.antlrTool.pathToFile(superGrammar.getFileName());
            final String string = pathToFile + superGrammar.exportVocab + CodeGenerator.TokenTypesFileSuffix + CodeGenerator.TokenTypesFileExt;
            final String fileMinusPath = this.antlrTool.fileMinusPath(string);
            if (!pathToFile.equals("." + System.getProperty("file.separator"))) {
                try {
                    this.antlrTool.copyFile(string, fileMinusPath);
                }
                catch (IOException ex) {
                    this.antlrTool.toolError("cannot find/copy importVocab file " + string);
                    return;
                }
            }
        }
        this.inherit(superGrammar.memberAction, superGrammar);
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public String getName() {
        return this.name;
    }
    
    public IndexedVector getOptions() {
        return this.options;
    }
    
    public IndexedVector getRules() {
        return this.rules;
    }
    
    public Grammar getSuperGrammar() {
        if (this.superGrammar == null) {
            return null;
        }
        return this.hier.getGrammar(this.superGrammar);
    }
    
    public String getSuperGrammarName() {
        return this.superGrammar;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void inherit(final Option option, final Grammar grammar) {
        if (option.getName().equals("importVocab") || option.getName().equals("exportVocab")) {
            return;
        }
        Option option2 = null;
        if (this.options != null) {
            option2 = (Option)this.options.getElement(option.getName());
        }
        if (option2 == null) {
            this.addOption(option);
        }
    }
    
    public void inherit(final Rule rule, final Grammar grammar) {
        final Rule rule2 = (Rule)this.rules.getElement(rule.getName());
        if (rule2 != null) {
            if (!rule2.sameSignature(rule)) {
                this.antlrTool.warning("rule " + this.getName() + "." + rule2.getName() + " has different signature than " + grammar.getName() + "." + rule2.getName());
            }
        }
        else {
            this.addRule(rule);
        }
    }
    
    public void inherit(final String memberAction, final Grammar grammar) {
        if (this.memberAction != null) {
            return;
        }
        if (memberAction != null) {
            this.memberAction = memberAction;
        }
    }
    
    public boolean isPredefined() {
        return this.predefined;
    }
    
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }
    
    public void setHierarchy(final Hierarchy hier) {
        this.hier = hier;
    }
    
    public void setMemberAction(final String memberAction) {
        this.memberAction = memberAction;
    }
    
    public void setOptions(final IndexedVector options) {
        this.options = options;
    }
    
    public void setPreambleAction(final String preambleAction) {
        this.preambleAction = preambleAction;
    }
    
    public void setPredefined(final boolean predefined) {
        this.predefined = predefined;
    }
    
    public void setTokenSection(final String tokenSection) {
        this.tokenSection = tokenSection;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer(10000);
        if (this.preambleAction != null) {
            sb.append(this.preambleAction);
        }
        if (this.superGrammar == null) {
            return "class " + this.name + ";";
        }
        if (this.superClass != null) {
            sb.append("class " + this.name + " extends " + this.superClass + ";");
        }
        else {
            sb.append("class " + this.name + " extends " + this.type + ";");
        }
        sb.append(System.getProperty("line.separator") + System.getProperty("line.separator"));
        if (this.options != null) {
            sb.append(Hierarchy.optionsToString(this.options));
        }
        if (this.tokenSection != null) {
            sb.append(this.tokenSection + "\n");
        }
        if (this.memberAction != null) {
            sb.append(this.memberAction + System.getProperty("line.separator"));
        }
        for (int i = 0; i < this.rules.size(); ++i) {
            final Rule obj = (Rule)this.rules.elementAt(i);
            if (!this.getName().equals(obj.enclosingGrammar.getName())) {
                sb.append("// inherited from grammar " + obj.enclosingGrammar.getName() + System.getProperty("line.separator"));
            }
            sb.append(obj + System.getProperty("line.separator") + System.getProperty("line.separator"));
        }
        return sb.toString();
    }
}
