// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

import java.util.Set;

public interface FetchGroup
{
    public static final String DEFAULT = "default";
    public static final String RELATIONSHIP = "relationship";
    public static final String MULTIVALUED = "multivalued";
    public static final String BASIC = "basic";
    public static final String ALL = "all";
    
    int hashCode();
    
    boolean equals(final Object p0);
    
    String getName();
    
    Class getType();
    
    boolean getPostLoad();
    
    FetchGroup setPostLoad(final boolean p0);
    
    FetchGroup addMember(final String p0);
    
    FetchGroup addMembers(final String... p0);
    
    FetchGroup removeMember(final String p0);
    
    FetchGroup removeMembers(final String... p0);
    
    FetchGroup addCategory(final String p0);
    
    FetchGroup removeCategory(final String p0);
    
    FetchGroup setRecursionDepth(final String p0, final int p1);
    
    int getRecursionDepth(final String p0);
    
    Set getMembers();
    
    FetchGroup setUnmodifiable();
    
    boolean isUnmodifiable();
}
