// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.testing;

import org.apache.tools.ant.taskdefs.WaitFor;

public class BlockFor extends WaitFor
{
    private String text;
    
    public BlockFor() {
        super("blockfor");
        this.text = this.getTaskName() + " timed out";
    }
    
    public BlockFor(final String taskName) {
        super(taskName);
    }
    
    @Override
    protected void processTimeout() throws BuildTimeoutException {
        super.processTimeout();
        throw new BuildTimeoutException(this.text, this.getLocation());
    }
    
    public void addText(final String message) {
        this.text = this.getProject().replaceProperties(message);
    }
}
