// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.ProjectComponent;

public class TypeFound extends ProjectComponent implements Condition
{
    private String name;
    private String uri;
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setURI(final String uri) {
        this.uri = uri;
    }
    
    protected boolean doesTypeExist(final String typename) {
        final ComponentHelper helper = ComponentHelper.getComponentHelper(this.getProject());
        final String componentName = ProjectHelper.genComponentName(this.uri, typename);
        final AntTypeDefinition def = helper.getDefinition(componentName);
        if (def == null) {
            return false;
        }
        final boolean found = def.getExposedClass(this.getProject()) != null;
        if (!found) {
            final String text = helper.diagnoseCreationFailure(componentName, "type");
            this.log(text, 3);
        }
        return found;
    }
    
    public boolean eval() throws BuildException {
        if (this.name == null) {
            throw new BuildException("No type specified");
        }
        return this.doesTypeExist(this.name);
    }
}
