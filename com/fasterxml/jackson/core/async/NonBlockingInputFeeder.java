// 
// Decompiled by Procyon v0.5.36
// 

package com.fasterxml.jackson.core.async;

public interface NonBlockingInputFeeder
{
    boolean needMoreInput();
    
    void endOfInput();
}
