// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

final class $Hashing
{
    private static final int MAX_TABLE_SIZE = 1073741824;
    private static final int CUTOFF = 536870912;
    
    private $Hashing() {
    }
    
    static int smear(int hashCode) {
        hashCode ^= (hashCode >>> 20 ^ hashCode >>> 12);
        return hashCode ^ hashCode >>> 7 ^ hashCode >>> 4;
    }
    
    static int chooseTableSize(final int setSize) {
        if (setSize < 536870912) {
            return Integer.highestOneBit(setSize) << 2;
        }
        $Preconditions.checkArgument(setSize < 1073741824, (Object)"collection too large");
        return 1073741824;
    }
}
