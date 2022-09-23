// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.http.pathmap;

public abstract class PathSpec implements Comparable<PathSpec>
{
    protected String pathSpec;
    protected PathSpecGroup group;
    protected int pathDepth;
    protected int specLength;
    
    @Override
    public int compareTo(final PathSpec other) {
        int diff = this.group.ordinal() - other.group.ordinal();
        if (diff != 0) {
            return diff;
        }
        diff = other.specLength - this.specLength;
        if (diff != 0) {
            return diff;
        }
        return this.pathSpec.compareTo(other.pathSpec);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final PathSpec other = (PathSpec)obj;
        if (this.pathSpec == null) {
            if (other.pathSpec != null) {
                return false;
            }
        }
        else if (!this.pathSpec.equals(other.pathSpec)) {
            return false;
        }
        return true;
    }
    
    public PathSpecGroup getGroup() {
        return this.group;
    }
    
    public int getPathDepth() {
        return this.pathDepth;
    }
    
    public abstract String getPathInfo(final String p0);
    
    public abstract String getPathMatch(final String p0);
    
    public String getDeclaration() {
        return this.pathSpec;
    }
    
    public abstract String getRelativePath(final String p0, final String p1);
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.pathSpec == null) ? 0 : this.pathSpec.hashCode());
        return result;
    }
    
    public abstract boolean matches(final String p0);
    
    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();
        str.append(this.getClass().getSimpleName()).append("[\"");
        str.append(this.pathSpec);
        str.append("\",pathDepth=").append(this.pathDepth);
        str.append(",group=").append(this.group);
        str.append("]");
        return str.toString();
    }
}
