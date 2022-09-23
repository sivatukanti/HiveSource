// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import javax.servlet.http.HttpServlet;
import com.google.inject.Key;

public interface LinkedServletBinding extends ServletModuleBinding
{
    Key<? extends HttpServlet> getLinkedKey();
}
