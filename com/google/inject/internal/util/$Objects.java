// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import java.util.Arrays;

public final class $Objects
{
    private $Objects() {
    }
    
    public static boolean equal(@$Nullable final Object a, @$Nullable final Object b) {
        return a == b || (a != null && a.equals(b));
    }
    
    public static int hashCode(final Object... objects) {
        return Arrays.hashCode(objects);
    }
}
