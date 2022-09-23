// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types;

import java.util.ArrayList;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.Task;
import java.util.Iterator;
import org.apache.tools.ant.Target;
import java.util.List;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.helper.ProjectHelperImpl;
import org.apache.tools.ant.ProjectHelper;

public class Description extends DataType
{
    public void addText(final String text) {
        final ProjectHelper ph = this.getProject().getReference("ant.projectHelper");
        if (!(ph instanceof ProjectHelperImpl)) {
            return;
        }
        final String currentDescription = this.getProject().getDescription();
        if (currentDescription == null) {
            this.getProject().setDescription(text);
        }
        else {
            this.getProject().setDescription(currentDescription + text);
        }
    }
    
    public static String getDescription(final Project project) {
        final List<Target> targets = project.getReference("ant.targets");
        if (targets == null) {
            return null;
        }
        final StringBuilder description = new StringBuilder();
        for (final Target t : targets) {
            concatDescriptions(project, t, description);
        }
        return description.toString();
    }
    
    private static void concatDescriptions(final Project project, final Target t, final StringBuilder description) {
        if (t == null) {
            return;
        }
        for (final Task task : findElementInTarget(project, t, "description")) {
            if (!(task instanceof UnknownElement)) {
                continue;
            }
            final UnknownElement ue = (UnknownElement)task;
            final String descComp = ue.getWrapper().getText().toString();
            if (descComp == null) {
                continue;
            }
            description.append(project.replaceProperties(descComp));
        }
    }
    
    private static List<Task> findElementInTarget(final Project project, final Target t, final String name) {
        final List<Task> elems = new ArrayList<Task>();
        for (final Task task : t.getTasks()) {
            if (name.equals(task.getTaskName())) {
                elems.add(task);
            }
        }
        return elems;
    }
}
