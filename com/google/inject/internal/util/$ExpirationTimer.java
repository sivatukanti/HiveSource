// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import java.util.Timer;

class $ExpirationTimer
{
    static Timer instance;
    
    static {
        $ExpirationTimer.instance = new Timer(true);
    }
}
