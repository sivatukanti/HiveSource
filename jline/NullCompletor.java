// 
// Decompiled by Procyon v0.5.36
// 

package jline;

import java.util.List;

public class NullCompletor implements Completor
{
    public int complete(final String buffer, final int cursor, final List candidates) {
        return -1;
    }
}
