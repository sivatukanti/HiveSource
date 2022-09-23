// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.RuntimeConfigurable;
import org.apache.tools.ant.TypeAdapter;
import org.apache.tools.ant.Task;

public class AugmentReference extends Task implements TypeAdapter
{
    private String id;
    
    public void checkProxyClass(final Class<?> proxyClass) {
    }
    
    public synchronized Object getProxy() {
        if (this.getProject() == null) {
            throw new IllegalStateException(this.getTaskName() + "Project owner unset");
        }
        this.hijackId();
        if (this.getProject().hasReference(this.id)) {
            final Object result = this.getProject().getReference(this.id);
            this.log("project reference " + this.id + "=" + String.valueOf(result), 4);
            return result;
        }
        throw new IllegalStateException("Unknown reference \"" + this.id + "\"");
    }
    
    public void setProxy(final Object o) {
        throw new UnsupportedOperationException();
    }
    
    private synchronized void hijackId() {
        if (this.id == null) {
            final RuntimeConfigurable wrapper = this.getWrapper();
            this.id = wrapper.getId();
            if (this.id == null) {
                throw new IllegalStateException(this.getTaskName() + " attribute 'id' unset");
            }
            wrapper.setAttribute("id", null);
            wrapper.removeAttribute("id");
            wrapper.setElementTag("augmented reference \"" + this.id + "\"");
        }
    }
    
    @Override
    public void execute() {
        this.restoreWrapperId();
    }
    
    private synchronized void restoreWrapperId() {
        if (this.id != null) {
            this.log("restoring augment wrapper " + this.id, 4);
            final RuntimeConfigurable wrapper = this.getWrapper();
            wrapper.setAttribute("id", this.id);
            wrapper.setElementTag(this.getTaskName());
            this.id = null;
        }
    }
}
