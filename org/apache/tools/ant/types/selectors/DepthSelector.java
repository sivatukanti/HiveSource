// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.types.selectors;

import org.apache.tools.ant.BuildException;
import java.util.StringTokenizer;
import java.io.File;
import org.apache.tools.ant.types.Parameter;

public class DepthSelector extends BaseExtendSelector
{
    public int min;
    public int max;
    public static final String MIN_KEY = "min";
    public static final String MAX_KEY = "max";
    
    public DepthSelector() {
        this.min = -1;
        this.max = -1;
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder("{depthselector min: ");
        buf.append(this.min);
        buf.append(" max: ");
        buf.append(this.max);
        buf.append("}");
        return buf.toString();
    }
    
    public void setMin(final int min) {
        this.min = min;
    }
    
    public void setMax(final int max) {
        this.max = max;
    }
    
    @Override
    public void setParameters(final Parameter[] parameters) {
        super.setParameters(parameters);
        if (parameters != null) {
            for (int i = 0; i < parameters.length; ++i) {
                final String paramname = parameters[i].getName();
                if ("min".equalsIgnoreCase(paramname)) {
                    try {
                        this.setMin(Integer.parseInt(parameters[i].getValue()));
                    }
                    catch (NumberFormatException nfe1) {
                        this.setError("Invalid minimum value " + parameters[i].getValue());
                    }
                }
                else if ("max".equalsIgnoreCase(paramname)) {
                    try {
                        this.setMax(Integer.parseInt(parameters[i].getValue()));
                    }
                    catch (NumberFormatException nfe1) {
                        this.setError("Invalid maximum value " + parameters[i].getValue());
                    }
                }
                else {
                    this.setError("Invalid parameter " + paramname);
                }
            }
        }
    }
    
    @Override
    public void verifySettings() {
        if (this.min < 0 && this.max < 0) {
            this.setError("You must set at least one of the min or the max levels.");
        }
        if (this.max < this.min && this.max > -1) {
            this.setError("The maximum depth is lower than the minimum.");
        }
    }
    
    @Override
    public boolean isSelected(final File basedir, final String filename, final File file) {
        this.validate();
        int depth = -1;
        final String absBase = basedir.getAbsolutePath();
        final String absFile = file.getAbsolutePath();
        final StringTokenizer tokBase = new StringTokenizer(absBase, File.separator);
        final StringTokenizer tokFile = new StringTokenizer(absFile, File.separator);
        while (tokFile.hasMoreTokens()) {
            final String filetoken = tokFile.nextToken();
            if (tokBase.hasMoreTokens()) {
                final String basetoken = tokBase.nextToken();
                if (!basetoken.equals(filetoken)) {
                    throw new BuildException("File " + filename + " does not appear within " + absBase + "directory");
                }
                continue;
            }
            else {
                ++depth;
                if (this.max > -1 && depth > this.max) {
                    return false;
                }
                continue;
            }
        }
        if (tokBase.hasMoreTokens()) {
            throw new BuildException("File " + filename + " is outside of " + absBase + "directory tree");
        }
        return this.min <= -1 || depth >= this.min;
    }
}
