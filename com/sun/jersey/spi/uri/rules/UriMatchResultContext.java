// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.spi.uri.rules;

import java.util.regex.MatchResult;
import com.sun.jersey.api.core.Traceable;

public interface UriMatchResultContext extends Traceable
{
    MatchResult getMatchResult();
    
    void setMatchResult(final MatchResult p0);
}
