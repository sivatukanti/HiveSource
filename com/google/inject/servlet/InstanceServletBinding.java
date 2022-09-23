// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.servlet;

import javax.servlet.http.HttpServlet;

public interface InstanceServletBinding extends ServletModuleBinding
{
    HttpServlet getServletInstance();
}
