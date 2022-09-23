// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.load;

import java.io.IOException;
import org.apache.derby.iapi.error.PublicAPI;
import org.apache.derby.iapi.error.StandardException;
import java.sql.SQLException;

class LoadError
{
    static SQLException connectionNull() {
        return PublicAPI.wrapStandardException(StandardException.newException("XIE01.S"));
    }
    
    static SQLException dataAfterStopDelimiter(final int value, final int value2) {
        return PublicAPI.wrapStandardException(StandardException.newException("XIE03.S", new Integer(value), new Integer(value2)));
    }
    
    static SQLException dataFileNotFound(final String s, final Exception ex) {
        return PublicAPI.wrapStandardException(StandardException.newException("XIE04.S", ex, s));
    }
    
    static SQLException dataFileNull() {
        return PublicAPI.wrapStandardException(StandardException.newException("XIE05.S"));
    }
    
    static SQLException dataFileExists(final String s) {
        return PublicAPI.wrapStandardException(StandardException.newException("XIE0S.S", s));
    }
    
    static SQLException lobsFileExists(final String s) {
        return PublicAPI.wrapStandardException(StandardException.newException("XIE0T.S", s));
    }
    
    static SQLException entityNameMissing() {
        return PublicAPI.wrapStandardException(StandardException.newException("XIE06.S"));
    }
    
    static SQLException fieldAndRecordSeparatorsSubset() {
        return PublicAPI.wrapStandardException(StandardException.newException("XIE07.S"));
    }
    
    static SQLException invalidColumnName(final String s) {
        return PublicAPI.wrapStandardException(StandardException.newException("XIE08.S", s));
    }
    
    static SQLException invalidColumnNumber(final int value) {
        return PublicAPI.wrapStandardException(StandardException.newException("XIE09.S", new Integer(value)));
    }
    
    static SQLException nonSupportedTypeColumn(final String s, final String s2) {
        return PublicAPI.wrapStandardException(StandardException.newException("XIE0B.S", s, s2));
    }
    
    static SQLException recordSeparatorMissing(final int value) {
        return PublicAPI.wrapStandardException(StandardException.newException("XIE0D.S", new Integer(value)));
    }
    
    static SQLException unexpectedEndOfFile(final int value) {
        return PublicAPI.wrapStandardException(StandardException.newException("XIE0E.S", new Integer(value)));
    }
    
    static SQLException errorWritingData(final IOException ex) {
        return PublicAPI.wrapStandardException(StandardException.newException("XIE0I.S", ex));
    }
    
    static SQLException periodAsCharDelimiterNotAllowed() {
        return PublicAPI.wrapStandardException(StandardException.newException("XIE0K.S"));
    }
    
    static SQLException delimitersAreNotMutuallyExclusive() {
        return PublicAPI.wrapStandardException(StandardException.newException("XIE0J.S"));
    }
    
    static SQLException tableNotFound(final String s) {
        return PublicAPI.wrapStandardException(StandardException.newException("XIE0M.S", s));
    }
    
    static SQLException unexpectedError(final Throwable t) {
        if (!(t instanceof SQLException)) {
            return PublicAPI.wrapStandardException(StandardException.plainWrapException(t));
        }
        return (SQLException)t;
    }
}
