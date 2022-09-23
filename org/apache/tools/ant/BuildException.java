// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant;

public class BuildException extends RuntimeException
{
    private static final long serialVersionUID = -5419014565354664240L;
    private Location location;
    
    public BuildException() {
        this.location = Location.UNKNOWN_LOCATION;
    }
    
    public BuildException(final String message) {
        super(message);
        this.location = Location.UNKNOWN_LOCATION;
    }
    
    public BuildException(final String message, final Throwable cause) {
        super(message, cause);
        this.location = Location.UNKNOWN_LOCATION;
    }
    
    public BuildException(final String msg, final Throwable cause, final Location location) {
        this(msg, cause);
        this.location = location;
    }
    
    public BuildException(final Throwable cause) {
        super(cause);
        this.location = Location.UNKNOWN_LOCATION;
    }
    
    public BuildException(final String message, final Location location) {
        super(message);
        this.location = Location.UNKNOWN_LOCATION;
        this.location = location;
    }
    
    public BuildException(final Throwable cause, final Location location) {
        this(cause);
        this.location = location;
    }
    
    @Deprecated
    public Throwable getException() {
        return this.getCause();
    }
    
    @Override
    public String toString() {
        return this.location.toString() + this.getMessage();
    }
    
    public void setLocation(final Location location) {
        this.location = location;
    }
    
    public Location getLocation() {
        return this.location;
    }
}
