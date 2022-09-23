// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.i18n;

import java.text.DateFormat;
import org.apache.derby.iapi.error.StandardException;
import java.util.Locale;

public interface LocaleFinder
{
    Locale getCurrentLocale() throws StandardException;
    
    DateFormat getDateFormat() throws StandardException;
    
    DateFormat getTimeFormat() throws StandardException;
    
    DateFormat getTimestampFormat() throws StandardException;
}
