// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import javax.servlet.Filter;
import com.google.inject.Key;

public interface LinkedFilterBinding extends ServletModuleBinding
{
    Key<? extends Filter> getLinkedKey();
}
