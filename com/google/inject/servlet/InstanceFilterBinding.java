// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import javax.servlet.Filter;

public interface InstanceFilterBinding extends ServletModuleBinding
{
    Filter getFilterInstance();
}
