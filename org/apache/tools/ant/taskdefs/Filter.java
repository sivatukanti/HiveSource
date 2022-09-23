// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import java.io.File;
import org.apache.tools.ant.Task;

public class Filter extends Task
{
    private String token;
    private String value;
    private File filtersFile;
    
    public void setToken(final String token) {
        this.token = token;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    public void setFiltersfile(final File filtersFile) {
        this.filtersFile = filtersFile;
    }
    
    @Override
    public void execute() throws BuildException {
        final boolean isFiltersFromFile = this.filtersFile != null && this.token == null && this.value == null;
        final boolean isSingleFilter = this.filtersFile == null && this.token != null && this.value != null;
        if (!isFiltersFromFile && !isSingleFilter) {
            throw new BuildException("both token and value parameters, or only a filtersFile parameter is required", this.getLocation());
        }
        if (isSingleFilter) {
            this.getProject().getGlobalFilterSet().addFilter(this.token, this.value);
        }
        if (isFiltersFromFile) {
            this.readFilters();
        }
    }
    
    protected void readFilters() throws BuildException {
        this.log("Reading filters from " + this.filtersFile, 3);
        this.getProject().getGlobalFilterSet().readFiltersFromFile(this.filtersFile);
    }
}
