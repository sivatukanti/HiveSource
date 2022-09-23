// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.validation;

import javax.xml.stream.Location;

public class XMLValidationProblem
{
    public static final int SEVERITY_WARNING = 1;
    public static final int SEVERITY_ERROR = 2;
    public static final int SEVERITY_FATAL = 3;
    protected Location mLocation;
    protected final String mMessage;
    protected final int mSeverity;
    protected String mType;
    protected XMLValidator mReporter;
    
    public XMLValidationProblem(final Location location, final String s) {
        this(location, s, 2);
    }
    
    public XMLValidationProblem(final Location location, final String s, final int n) {
        this(location, s, n, null);
    }
    
    public XMLValidationProblem(final Location mLocation, final String mMessage, final int mSeverity, final String mType) {
        this.mLocation = mLocation;
        this.mMessage = mMessage;
        this.mSeverity = mSeverity;
        this.mType = mType;
    }
    
    public XMLValidationException toException() {
        return XMLValidationException.createException(this);
    }
    
    public void setType(final String mType) {
        this.mType = mType;
    }
    
    public void setLocation(final Location mLocation) {
        this.mLocation = mLocation;
    }
    
    public void setReporter(final XMLValidator mReporter) {
        this.mReporter = mReporter;
    }
    
    public Location getLocation() {
        return this.mLocation;
    }
    
    public String getMessage() {
        return this.mMessage;
    }
    
    public int getSeverity() {
        return this.mSeverity;
    }
    
    public String getType() {
        return this.mType;
    }
    
    public XMLValidator getReporter() {
        return this.mReporter;
    }
}
