// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.jdbc;

import java.util.Properties;
import org.apache.derby.jdbc.InternalDriver;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import org.apache.derby.iapi.jdbc.EngineLOB;

public class LOBStoredProcedure
{
    public static int CLOBCREATELOCATOR() throws SQLException {
        return ((EngineLOB)getEmbedConnection().createClob()).getLocator();
    }
    
    public static void CLOBRELEASELOCATOR(final int n) throws SQLException {
        final Clob clob = (Clob)getEmbedConnection().getLOBMapping(n);
        if (clob == null) {
            throw newSQLException("XJ217.S");
        }
        ((EmbedClob)clob).free();
        getEmbedConnection().removeLOBMapping(n);
    }
    
    public static long CLOBGETPOSITIONFROMSTRING(final int n, final String s, final long n2) throws SQLException {
        return getClobObjectCorrespondingtoLOCATOR(n).position(s, n2);
    }
    
    public static long CLOBGETPOSITIONFROMLOCATOR(final int n, final int n2, final long n3) throws SQLException {
        return getClobObjectCorrespondingtoLOCATOR(n).position(getClobObjectCorrespondingtoLOCATOR(n2), n3);
    }
    
    public static long CLOBGETLENGTH(final int n) throws SQLException {
        return getClobObjectCorrespondingtoLOCATOR(n).length();
    }
    
    public static String CLOBGETSUBSTRING(final int n, final long n2, int min) throws SQLException {
        min = Math.min(min, 10890);
        return getClobObjectCorrespondingtoLOCATOR(n).getSubString(n2, min);
    }
    
    public static void CLOBSETSTRING(final int n, final long n2, final int n3, final String s) throws SQLException {
        getClobObjectCorrespondingtoLOCATOR(n).setString(n2, s, 0, n3);
    }
    
    public static void CLOBTRUNCATE(final int n, final long n2) throws SQLException {
        getClobObjectCorrespondingtoLOCATOR(n).truncate(n2);
    }
    
    private static Clob getClobObjectCorrespondingtoLOCATOR(final int n) throws SQLException {
        final Clob clob = (Clob)getEmbedConnection().getLOBMapping(n);
        if (clob == null) {
            throw newSQLException("XJ217.S");
        }
        return clob;
    }
    
    public static int BLOBCREATELOCATOR() throws SQLException {
        return ((EngineLOB)getEmbedConnection().createBlob()).getLocator();
    }
    
    public static void BLOBRELEASELOCATOR(final int n) throws SQLException {
        final Blob blob = (Blob)getEmbedConnection().getLOBMapping(n);
        if (blob == null) {
            throw newSQLException("XJ217.S");
        }
        ((EmbedBlob)blob).free();
        getEmbedConnection().removeLOBMapping(n);
    }
    
    public static long BLOBGETPOSITIONFROMLOCATOR(final int n, final int n2, final long n3) throws SQLException {
        return getBlobObjectCorrespondingtoLOCATOR(n).position(getBlobObjectCorrespondingtoLOCATOR(n2), n3);
    }
    
    public static long BLOBGETPOSITIONFROMBYTES(final int n, final byte[] array, final long n2) throws SQLException {
        return getBlobObjectCorrespondingtoLOCATOR(n).position(array, n2);
    }
    
    public static long BLOBGETLENGTH(final int n) throws SQLException {
        return getBlobObjectCorrespondingtoLOCATOR(n).length();
    }
    
    public static byte[] BLOBGETBYTES(final int n, final long n2, int min) throws SQLException {
        min = Math.min(min, 32672);
        return getBlobObjectCorrespondingtoLOCATOR(n).getBytes(n2, min);
    }
    
    public static void BLOBSETBYTES(final int n, final long n2, final int n3, final byte[] array) throws SQLException {
        getBlobObjectCorrespondingtoLOCATOR(n).setBytes(n2, array, 0, n3);
    }
    
    public static void BLOBTRUNCATE(final int n, final long n2) throws SQLException {
        getBlobObjectCorrespondingtoLOCATOR(n).truncate(n2);
    }
    
    private static Blob getBlobObjectCorrespondingtoLOCATOR(final int n) throws SQLException {
        final Blob blob = (Blob)getEmbedConnection().getLOBMapping(n);
        if (blob == null) {
            throw newSQLException("XJ217.S");
        }
        return blob;
    }
    
    private static EmbedConnection getEmbedConnection() throws SQLException {
        final InternalDriver activeDriver = InternalDriver.activeDriver();
        if (activeDriver != null) {
            final EmbedConnection embedConnection = (EmbedConnection)activeDriver.connect("jdbc:default:connection", null, 0);
            if (embedConnection != null) {
                return embedConnection;
            }
        }
        throw Util.noCurrentConnection();
    }
    
    private static SQLException newSQLException(final String s) {
        return Util.generateCsSQLException(s);
    }
}
