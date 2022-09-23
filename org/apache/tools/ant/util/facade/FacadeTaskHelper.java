// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util.facade;

import org.apache.tools.ant.Project;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.tools.ant.types.Path;
import java.util.List;

public class FacadeTaskHelper
{
    private List args;
    private String userChoice;
    private String magicValue;
    private String defaultValue;
    private Path implementationClasspath;
    
    public FacadeTaskHelper(final String defaultValue) {
        this(defaultValue, null);
    }
    
    public FacadeTaskHelper(final String defaultValue, final String magicValue) {
        this.args = new ArrayList();
        this.defaultValue = defaultValue;
        this.magicValue = magicValue;
    }
    
    public void setMagicValue(final String magicValue) {
        this.magicValue = magicValue;
    }
    
    public void setImplementation(final String userChoice) {
        this.userChoice = userChoice;
    }
    
    public String getImplementation() {
        return (this.userChoice != null) ? this.userChoice : ((this.magicValue != null) ? this.magicValue : this.defaultValue);
    }
    
    public String getExplicitChoice() {
        return this.userChoice;
    }
    
    public void addImplementationArgument(final ImplementationSpecificArgument arg) {
        this.args.add(arg);
    }
    
    public String[] getArgs() {
        final List tmp = new ArrayList(this.args.size());
        for (final ImplementationSpecificArgument arg : this.args) {
            final String[] curr = arg.getParts(this.getImplementation());
            for (int i = 0; i < curr.length; ++i) {
                tmp.add(curr[i]);
            }
        }
        final String[] res = new String[tmp.size()];
        return tmp.toArray(res);
    }
    
    public boolean hasBeenSet() {
        return this.userChoice != null || this.magicValue != null;
    }
    
    public Path getImplementationClasspath(final Project project) {
        if (this.implementationClasspath == null) {
            this.implementationClasspath = new Path(project);
        }
        return this.implementationClasspath;
    }
}
