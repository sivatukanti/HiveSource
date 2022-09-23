// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl.modelapi.validation;

import com.sun.jersey.api.model.AbstractModelComponent;
import java.util.Iterator;
import java.util.LinkedList;
import com.sun.jersey.api.model.ResourceModelIssue;
import java.util.List;
import com.sun.jersey.api.model.AbstractModelVisitor;

public abstract class AbstractModelValidator implements AbstractModelVisitor
{
    final List<ResourceModelIssue> issueList;
    
    public AbstractModelValidator() {
        this.issueList = new LinkedList<ResourceModelIssue>();
    }
    
    public List<ResourceModelIssue> getIssueList() {
        return this.issueList;
    }
    
    public boolean fatalIssuesFound() {
        for (final ResourceModelIssue issue : this.getIssueList()) {
            if (issue.isFatal()) {
                return true;
            }
        }
        return false;
    }
    
    public void cleanIssueList() {
        this.issueList.clear();
    }
    
    public void validate(final AbstractModelComponent component) {
        component.accept(this);
        final List<AbstractModelComponent> componentList = component.getComponents();
        if (null != componentList) {
            for (final AbstractModelComponent subcomponent : componentList) {
                this.validate(subcomponent);
            }
        }
    }
}
