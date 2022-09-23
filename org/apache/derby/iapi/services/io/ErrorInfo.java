// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.io;

interface ErrorInfo
{
    String getErrorInfo();
    
    Exception getNestedException();
}
