// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.load;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.File;
import org.apache.derby.iapi.services.io.FileUtil;
import org.apache.derby.iapi.error.PublicAPI;
import org.apache.derby.iapi.error.StandardException;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.IOException;

public class Export extends ExportAbstract
{
    private String outputFileName;
    private String lobsFileName;
    
    private void doExport() throws SQLException {
        try {
            if (this.entityName == null && this.selectStatement == null) {
                throw LoadError.entityNameMissing();
            }
            if (this.outputFileName == null) {
                throw LoadError.dataFileNull();
            }
            if (this.dataFileExists(this.outputFileName)) {
                throw LoadError.dataFileExists(this.outputFileName);
            }
            if (this.lobsFileName != null && this.lobsFileExists(this.lobsFileName)) {
                throw LoadError.lobsFileExists(this.lobsFileName);
            }
            try {
                this.doAllTheWork();
            }
            catch (IOException ex) {
                throw LoadError.errorWritingData(ex);
            }
        }
        catch (Exception ex2) {
            throw LoadError.unexpectedError(ex2);
        }
    }
    
    private Export(final Connection con, final String schemaName, final String entityName, final String selectStatement, final String outputFileName, final String s, final String s2, final String s3) throws SQLException {
        this.con = con;
        this.schemaName = schemaName;
        this.entityName = entityName;
        this.selectStatement = selectStatement;
        this.outputFileName = outputFileName;
        try {
            (this.controlFileReader = new ControlInfo()).setControlProperties(s, s2, s3);
        }
        catch (Exception ex) {
            throw LoadError.unexpectedError(ex);
        }
    }
    
    private void setLobsExtFileName(final String lobsFileName) throws SQLException {
        if (lobsFileName == null) {
            throw PublicAPI.wrapStandardException(StandardException.newException("XIE0Q.S"));
        }
        this.lobsFileName = lobsFileName;
        this.lobsInExtFile = true;
    }
    
    private boolean lobsFileExists(String stripProtocolFromFileName) throws SQLException {
        if (stripProtocolFromFileName == null) {
            throw PublicAPI.wrapStandardException(StandardException.newException("XIE0Q.S"));
        }
        stripProtocolFromFileName = FileUtil.stripProtocolFromFileName(stripProtocolFromFileName);
        return this.fileExists(new File(stripProtocolFromFileName));
    }
    
    private boolean dataFileExists(String stripProtocolFromFileName) throws SQLException {
        if (stripProtocolFromFileName == null) {
            throw PublicAPI.wrapStandardException(StandardException.newException("XIE05.S"));
        }
        stripProtocolFromFileName = FileUtil.stripProtocolFromFileName(stripProtocolFromFileName);
        return this.fileExists(new File(stripProtocolFromFileName));
    }
    
    private final boolean fileExists(final File file) {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction() {
            public Object run() {
                return new Boolean(file.exists());
            }
        });
    }
    
    public static void exportTable(final Connection connection, final String s, final String s2, final String s3, final String s4, final String s5, final String s6) throws SQLException {
        new Export(connection, s, s2, null, s3, s5, s4, s6).doExport();
    }
    
    public static void exportTable(final Connection connection, final String s, final String s2, final String s3, final String s4, final String s5, final String s6, final String lobsExtFileName) throws SQLException {
        final Export export = new Export(connection, s, s2, null, s3, s5, s4, s6);
        export.setLobsExtFileName(lobsExtFileName);
        export.doExport();
    }
    
    public static void exportQuery(final Connection connection, final String s, final String s2, final String s3, final String s4, final String s5) throws SQLException {
        new Export(connection, null, null, s, s2, s4, s3, s5).doExport();
    }
    
    public static void exportQuery(final Connection connection, final String s, final String s2, final String s3, final String s4, final String s5, final String lobsExtFileName) throws SQLException {
        final Export export = new Export(connection, null, null, s, s2, s4, s3, s5);
        export.setLobsExtFileName(lobsExtFileName);
        export.doExport();
    }
    
    protected ExportWriteDataAbstract getExportWriteData() throws Exception {
        if (this.lobsInExtFile) {
            return new ExportWriteData(this.outputFileName, this.lobsFileName, this.controlFileReader);
        }
        return new ExportWriteData(this.outputFileName, this.controlFileReader);
    }
}
