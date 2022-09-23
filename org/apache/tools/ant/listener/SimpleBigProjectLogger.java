// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.listener;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.NoBannerLogger;

public class SimpleBigProjectLogger extends NoBannerLogger
{
    @Override
    protected String extractTargetName(final BuildEvent event) {
        final String targetName = super.extractTargetName(event);
        final String projectName = this.extractProjectName(event);
        if (projectName != null && targetName != null) {
            return projectName + '.' + targetName;
        }
        return targetName;
    }
}
