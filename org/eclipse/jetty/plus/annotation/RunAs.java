// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.annotation;

import javax.servlet.ServletException;
import org.eclipse.jetty.servlet.ServletHolder;

public class RunAs
{
    private String _className;
    private String _roleName;
    
    public void setTargetClassName(final String className) {
        this._className = className;
    }
    
    public String getTargetClassName() {
        return this._className;
    }
    
    public void setRoleName(final String roleName) {
        this._roleName = roleName;
    }
    
    public String getRoleName() {
        return this._roleName;
    }
    
    public void setRunAs(final ServletHolder holder) throws ServletException {
        if (holder == null) {
            return;
        }
        final String className = holder.getClassName();
        if (className.equals(this._className) && holder.getRegistration().getRunAsRole() == null) {
            holder.getRegistration().setRunAsRole(this._roleName);
        }
    }
}
