// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Comparison;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.Task;

public class ResourceCount extends Task implements Condition
{
    private static final String ONE_NESTED_MESSAGE = "ResourceCount can count resources from exactly one nested ResourceCollection.";
    private static final String COUNT_REQUIRED = "Use of the ResourceCount condition requires that the count attribute be set.";
    private ResourceCollection rc;
    private Comparison when;
    private Integer count;
    private String property;
    
    public ResourceCount() {
        this.when = Comparison.EQUAL;
    }
    
    public void add(final ResourceCollection r) {
        if (this.rc != null) {
            throw new BuildException("ResourceCount can count resources from exactly one nested ResourceCollection.");
        }
        this.rc = r;
    }
    
    public void setRefid(final Reference r) {
        final Object o = r.getReferencedObject();
        if (!(o instanceof ResourceCollection)) {
            throw new BuildException(r.getRefId() + " doesn't denote a ResourceCollection");
        }
        this.add((ResourceCollection)o);
    }
    
    @Override
    public void execute() {
        if (this.rc == null) {
            throw new BuildException("ResourceCount can count resources from exactly one nested ResourceCollection.");
        }
        if (this.property == null) {
            this.log("resource count = " + this.rc.size());
        }
        else {
            this.getProject().setNewProperty(this.property, Integer.toString(this.rc.size()));
        }
    }
    
    public boolean eval() {
        if (this.rc == null) {
            throw new BuildException("ResourceCount can count resources from exactly one nested ResourceCollection.");
        }
        if (this.count == null) {
            throw new BuildException("Use of the ResourceCount condition requires that the count attribute be set.");
        }
        return this.when.evaluate(new Integer(this.rc.size()).compareTo(this.count));
    }
    
    public void setCount(final int c) {
        this.count = new Integer(c);
    }
    
    public void setWhen(final Comparison c) {
        this.when = c;
    }
    
    public void setProperty(final String p) {
        this.property = p;
    }
}
