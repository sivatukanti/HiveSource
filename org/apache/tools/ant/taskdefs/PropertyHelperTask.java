// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.tools.ant.BuildException;
import java.util.List;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.Task;

public class PropertyHelperTask extends Task
{
    private PropertyHelper propertyHelper;
    private List delegates;
    
    public synchronized void addConfigured(final PropertyHelper propertyHelper) {
        if (this.propertyHelper != null) {
            throw new BuildException("Only one PropertyHelper can be installed");
        }
        this.propertyHelper = propertyHelper;
    }
    
    public synchronized void addConfigured(final PropertyHelper.Delegate delegate) {
        this.getAddDelegateList().add(delegate);
    }
    
    public DelegateElement createDelegate() {
        final DelegateElement result = new DelegateElement();
        this.getAddDelegateList().add(result);
        return result;
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.getProject() == null) {
            throw new BuildException("Project instance not set");
        }
        if (this.propertyHelper == null && this.delegates == null) {
            throw new BuildException("Either a new PropertyHelper or one or more PropertyHelper delegates are required");
        }
        PropertyHelper ph = this.propertyHelper;
        if (ph == null) {
            ph = PropertyHelper.getPropertyHelper(this.getProject());
        }
        else {
            ph = this.propertyHelper;
        }
        synchronized (ph) {
            if (this.delegates != null) {
                for (final Object o : this.delegates) {
                    final PropertyHelper.Delegate delegate = (o instanceof DelegateElement) ? ((DelegateElement)o).resolve() : o;
                    this.log("Adding PropertyHelper delegate " + delegate, 4);
                    ph.add(delegate);
                }
            }
        }
        if (this.propertyHelper != null) {
            this.log("Installing PropertyHelper " + this.propertyHelper, 4);
            this.getProject().addReference("ant.PropertyHelper", this.propertyHelper);
        }
    }
    
    private synchronized List getAddDelegateList() {
        if (this.delegates == null) {
            this.delegates = new ArrayList();
        }
        return this.delegates;
    }
    
    public final class DelegateElement
    {
        private String refid;
        
        private DelegateElement() {
        }
        
        public String getRefid() {
            return this.refid;
        }
        
        public void setRefid(final String refid) {
            this.refid = refid;
        }
        
        private PropertyHelper.Delegate resolve() {
            if (this.refid == null) {
                throw new BuildException("refid required for generic delegate");
            }
            return PropertyHelperTask.this.getProject().getReference(this.refid);
        }
    }
}
