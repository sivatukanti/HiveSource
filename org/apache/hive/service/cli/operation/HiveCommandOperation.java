// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hive.service.cli.operation;

import java.io.File;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.io.FileReader;
import java.util.Iterator;
import java.util.List;
import org.apache.hive.service.cli.RowSetFactory;
import org.apache.hive.service.cli.RowSet;
import org.apache.hive.service.cli.FetchOrientation;
import org.apache.hadoop.hive.metastore.api.Schema;
import org.apache.hadoop.hive.ql.processors.CommandProcessorResponse;
import org.apache.hive.service.cli.HiveSQLException;
import org.apache.hive.service.cli.OperationState;
import org.apache.hadoop.io.IOUtils;
import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.FileOutputStream;
import org.apache.hadoop.hive.ql.session.SessionState;
import java.util.Map;
import org.apache.hive.service.cli.session.HiveSession;
import java.io.BufferedReader;
import org.apache.hive.service.cli.TableSchema;
import org.apache.hadoop.hive.ql.processors.CommandProcessor;

public class HiveCommandOperation extends ExecuteStatementOperation
{
    private CommandProcessor commandProcessor;
    private TableSchema resultSchema;
    private BufferedReader resultReader;
    
    protected HiveCommandOperation(final HiveSession parentSession, final String statement, final CommandProcessor commandProcessor, final Map<String, String> confOverlay) {
        super(parentSession, statement, confOverlay, false);
        this.resultSchema = null;
        this.commandProcessor = commandProcessor;
        this.setupSessionIO(parentSession.getSessionState());
    }
    
    private void setupSessionIO(final SessionState sessionState) {
        try {
            HiveCommandOperation.LOG.info("Putting temp output to file " + sessionState.getTmpOutputFile().toString());
            sessionState.in = null;
            sessionState.out = new PrintStream(new FileOutputStream(sessionState.getTmpOutputFile()), true, "UTF-8");
            sessionState.err = new PrintStream(System.err, true, "UTF-8");
        }
        catch (IOException e) {
            HiveCommandOperation.LOG.error("Error in creating temp output file ", e);
            try {
                sessionState.in = null;
                sessionState.out = new PrintStream(System.out, true, "UTF-8");
                sessionState.err = new PrintStream(System.err, true, "UTF-8");
            }
            catch (UnsupportedEncodingException ee) {
                HiveCommandOperation.LOG.error("Error creating PrintStream", e);
                ee.printStackTrace();
                sessionState.out = null;
                sessionState.err = null;
            }
        }
    }
    
    private void tearDownSessionIO() {
        IOUtils.cleanup(HiveCommandOperation.LOG, this.parentSession.getSessionState().out);
        IOUtils.cleanup(HiveCommandOperation.LOG, this.parentSession.getSessionState().err);
    }
    
    public void runInternal() throws HiveSQLException {
        this.setState(OperationState.RUNNING);
        try {
            final String command = this.getStatement().trim();
            final String[] tokens = this.statement.split("\\s");
            final String commandArgs = command.substring(tokens[0].length()).trim();
            final CommandProcessorResponse response = this.commandProcessor.run(commandArgs);
            final int returnCode = response.getResponseCode();
            if (returnCode != 0) {
                throw this.toSQLException("Error while processing statement", response);
            }
            final Schema schema = response.getSchema();
            if (schema != null) {
                this.setHasResultSet(true);
                this.resultSchema = new TableSchema(schema);
            }
            else {
                this.setHasResultSet(false);
                this.resultSchema = new TableSchema();
            }
        }
        catch (HiveSQLException e) {
            this.setState(OperationState.ERROR);
            throw e;
        }
        catch (Exception e2) {
            this.setState(OperationState.ERROR);
            throw new HiveSQLException("Error running query: " + e2.toString(), e2);
        }
        this.setState(OperationState.FINISHED);
    }
    
    @Override
    public void close() throws HiveSQLException {
        this.setState(OperationState.CLOSED);
        this.tearDownSessionIO();
        this.cleanTmpFile();
        this.cleanupOperationLog();
    }
    
    @Override
    public TableSchema getResultSetSchema() throws HiveSQLException {
        return this.resultSchema;
    }
    
    @Override
    public RowSet getNextRowSet(final FetchOrientation orientation, final long maxRows) throws HiveSQLException {
        this.validateDefaultFetchOrientation(orientation);
        if (orientation.equals(FetchOrientation.FETCH_FIRST)) {
            this.resetResultReader();
        }
        final List<String> rows = this.readResults((int)maxRows);
        final RowSet rowSet = RowSetFactory.create(this.resultSchema, this.getProtocolVersion());
        for (final String row : rows) {
            rowSet.addRow(new String[] { row });
        }
        return rowSet;
    }
    
    private List<String> readResults(final int nLines) throws HiveSQLException {
        if (this.resultReader == null) {
            final SessionState sessionState = this.getParentSession().getSessionState();
            final File tmp = sessionState.getTmpOutputFile();
            try {
                this.resultReader = new BufferedReader(new FileReader(tmp));
            }
            catch (FileNotFoundException e) {
                HiveCommandOperation.LOG.error("File " + tmp + " not found. ", e);
                throw new HiveSQLException(e);
            }
        }
        final List<String> results = new ArrayList<String>();
        int i = 0;
        while (true) {
            if (i >= nLines) {
                if (nLines > 0) {
                    break;
                }
            }
            try {
                final String line = this.resultReader.readLine();
                if (line == null) {
                    break;
                }
                results.add(line);
            }
            catch (IOException e2) {
                HiveCommandOperation.LOG.error("Reading temp results encountered an exception: ", e2);
                throw new HiveSQLException(e2);
            }
            ++i;
        }
        return results;
    }
    
    private void cleanTmpFile() {
        this.resetResultReader();
        final SessionState sessionState = this.getParentSession().getSessionState();
        final File tmp = sessionState.getTmpOutputFile();
        tmp.delete();
    }
    
    private void resetResultReader() {
        if (this.resultReader != null) {
            IOUtils.cleanup(HiveCommandOperation.LOG, this.resultReader);
            this.resultReader = null;
        }
    }
}
