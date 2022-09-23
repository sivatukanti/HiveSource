// 
// Decompiled by Procyon v0.5.36
// 

package com.nimbusds.jose.jca;

public interface JCAAware<T extends JCAContext>
{
    T getJCAContext();
}
