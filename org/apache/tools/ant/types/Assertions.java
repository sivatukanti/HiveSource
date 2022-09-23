// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types;

import java.util.Collection;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.List;
import org.apache.tools.ant.BuildException;
import java.util.ArrayList;

public class Assertions extends DataType implements Cloneable
{
    private Boolean enableSystemAssertions;
    private ArrayList<BaseAssertion> assertionList;
    
    public Assertions() {
        this.assertionList = new ArrayList<BaseAssertion>();
    }
    
    public void addEnable(final EnabledAssertion assertion) {
        this.checkChildrenAllowed();
        this.assertionList.add(assertion);
    }
    
    public void addDisable(final DisabledAssertion assertion) {
        this.checkChildrenAllowed();
        this.assertionList.add(assertion);
    }
    
    public void setEnableSystemAssertions(final Boolean enableSystemAssertions) {
        this.checkAttributesAllowed();
        this.enableSystemAssertions = enableSystemAssertions;
    }
    
    @Override
    public void setRefid(final Reference ref) {
        if (this.assertionList.size() > 0 || this.enableSystemAssertions != null) {
            throw this.tooManyAttributes();
        }
        super.setRefid(ref);
    }
    
    private Assertions getFinalReference() {
        if (this.getRefid() == null) {
            return this;
        }
        final Object o = this.getRefid().getReferencedObject(this.getProject());
        if (!(o instanceof Assertions)) {
            throw new BuildException("reference is of wrong type");
        }
        return (Assertions)o;
    }
    
    public int size() {
        final Assertions clause = this.getFinalReference();
        return clause.getFinalSize();
    }
    
    private int getFinalSize() {
        return this.assertionList.size() + ((this.enableSystemAssertions != null) ? 1 : 0);
    }
    
    public void applyAssertions(final List<String> commandList) {
        this.getProject().log("Applying assertions", 4);
        final Assertions clause = this.getFinalReference();
        if (Boolean.TRUE.equals(clause.enableSystemAssertions)) {
            this.getProject().log("Enabling system assertions", 4);
            commandList.add("-enablesystemassertions");
        }
        else if (Boolean.FALSE.equals(clause.enableSystemAssertions)) {
            this.getProject().log("disabling system assertions", 4);
            commandList.add("-disablesystemassertions");
        }
        for (final BaseAssertion assertion : clause.assertionList) {
            final String arg = assertion.toCommand();
            this.getProject().log("adding assertion " + arg, 4);
            commandList.add(arg);
        }
    }
    
    public void applyAssertions(final CommandlineJava command) {
        final Assertions clause = this.getFinalReference();
        if (Boolean.TRUE.equals(clause.enableSystemAssertions)) {
            addVmArgument(command, "-enablesystemassertions");
        }
        else if (Boolean.FALSE.equals(clause.enableSystemAssertions)) {
            addVmArgument(command, "-disablesystemassertions");
        }
        for (final BaseAssertion assertion : clause.assertionList) {
            final String arg = assertion.toCommand();
            addVmArgument(command, arg);
        }
    }
    
    public void applyAssertions(final ListIterator<String> commandIterator) {
        this.getProject().log("Applying assertions", 4);
        final Assertions clause = this.getFinalReference();
        if (Boolean.TRUE.equals(clause.enableSystemAssertions)) {
            this.getProject().log("Enabling system assertions", 4);
            commandIterator.add("-enablesystemassertions");
        }
        else if (Boolean.FALSE.equals(clause.enableSystemAssertions)) {
            this.getProject().log("disabling system assertions", 4);
            commandIterator.add("-disablesystemassertions");
        }
        for (final BaseAssertion assertion : clause.assertionList) {
            final String arg = assertion.toCommand();
            this.getProject().log("adding assertion " + arg, 4);
            commandIterator.add(arg);
        }
    }
    
    private static void addVmArgument(final CommandlineJava command, final String arg) {
        final Commandline.Argument argument = command.createVmArgument();
        argument.setValue(arg);
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        final Assertions that = (Assertions)super.clone();
        that.assertionList = new ArrayList<BaseAssertion>(this.assertionList);
        return that;
    }
    
    public abstract static class BaseAssertion
    {
        private String packageName;
        private String className;
        
        public void setClass(final String className) {
            this.className = className;
        }
        
        public void setPackage(final String packageName) {
            this.packageName = packageName;
        }
        
        protected String getClassName() {
            return this.className;
        }
        
        protected String getPackageName() {
            return this.packageName;
        }
        
        public abstract String getCommandPrefix();
        
        public String toCommand() {
            if (this.getPackageName() != null && this.getClassName() != null) {
                throw new BuildException("Both package and class have been set");
            }
            final StringBuffer command = new StringBuffer(this.getCommandPrefix());
            if (this.getPackageName() != null) {
                command.append(':');
                command.append(this.getPackageName());
                if (!command.toString().endsWith("...")) {
                    command.append("...");
                }
            }
            else if (this.getClassName() != null) {
                command.append(':');
                command.append(this.getClassName());
            }
            return command.toString();
        }
    }
    
    public static class EnabledAssertion extends BaseAssertion
    {
        @Override
        public String getCommandPrefix() {
            return "-ea";
        }
    }
    
    public static class DisabledAssertion extends BaseAssertion
    {
        @Override
        public String getCommandPrefix() {
            return "-da";
        }
    }
}
