// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import java.util.Iterator;
import org.apache.tools.ant.ProjectHelperRepository;
import org.apache.tools.ant.ProjectHelper;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.Task;

public class ProjectHelperTask extends Task
{
    private List projectHelpers;
    
    public ProjectHelperTask() {
        this.projectHelpers = new ArrayList();
    }
    
    public synchronized void addConfigured(final ProjectHelper projectHelper) {
        this.projectHelpers.add(projectHelper);
    }
    
    @Override
    public void execute() throws BuildException {
        final ProjectHelperRepository repo = ProjectHelperRepository.getInstance();
        for (final ProjectHelper helper : this.projectHelpers) {
            repo.registerProjectHelper(helper.getClass());
        }
    }
}
