// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.attribute.AttributeNamespace;
import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.ProjectHelper;

public final class AttributeNamespaceDef extends AntlibDefinition
{
    @Override
    public void execute() {
        final String componentName = ProjectHelper.nsToComponentName(this.getURI());
        final AntTypeDefinition def = new AntTypeDefinition();
        def.setName(componentName);
        def.setClassName(AttributeNamespace.class.getName());
        def.setClass(AttributeNamespace.class);
        def.setRestrict(true);
        def.setClassLoader(AttributeNamespace.class.getClassLoader());
        ComponentHelper.getComponentHelper(this.getProject()).addDataTypeDefinition(def);
    }
}
