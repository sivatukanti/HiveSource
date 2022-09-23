// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

import java.util.Collection;
import java.util.Set;

public interface FetchPlan
{
    public static final String DEFAULT = "default";
    public static final String ALL = "all";
    public static final int DETACH_UNLOAD_FIELDS = 2;
    public static final int DETACH_LOAD_FIELDS = 1;
    public static final int FETCH_SIZE_GREEDY = -1;
    public static final int FETCH_SIZE_OPTIMAL = 0;
    
    FetchPlan addGroup(final String p0);
    
    FetchPlan removeGroup(final String p0);
    
    FetchPlan clearGroups();
    
    Set getGroups();
    
    FetchPlan setGroups(final Collection p0);
    
    FetchPlan setGroups(final String... p0);
    
    FetchPlan setGroup(final String p0);
    
    FetchPlan setMaxFetchDepth(final int p0);
    
    int getMaxFetchDepth();
    
    FetchPlan setDetachmentRoots(final Collection p0);
    
    Collection getDetachmentRoots();
    
    FetchPlan setDetachmentRootClasses(final Class... p0);
    
    Class[] getDetachmentRootClasses();
    
    FetchPlan setFetchSize(final int p0);
    
    int getFetchSize();
    
    FetchPlan setDetachmentOptions(final int p0);
    
    int getDetachmentOptions();
}
