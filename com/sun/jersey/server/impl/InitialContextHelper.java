// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jersey.server.impl;

import javax.naming.NamingException;
import javax.naming.InitialContext;

public class InitialContextHelper
{
    public static InitialContext getInitialContext() {
        try {
            return new InitialContext();
        }
        catch (NamingException ex) {
            return null;
        }
        catch (LinkageError ex2) {
            return null;
        }
    }
}
