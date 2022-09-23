// 
// Decompiled by Procyon v0.5.36
// 

package com.automationanywhere.botcommand;

import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.FileOutputStream;
import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Connection;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.i18n.Messages;
import java.util.Map;

public class ExecuteQuery
{
    private Map<String, Object> sessions;
    private static final Messages MESSAGES;
    
    public void action(final String sessionName, final String query, final Double size, final String fileName) {
        this.validateSession(sessionName);
        if ("".equals(query.trim())) {
            throw new BotCommandException(ExecuteQuery.MESSAGES.getString("emptyInputString", "query"));
        }
        if (!query.toLowerCase().contains("select") && !query.toLowerCase().contains("show")) {
            throw new BotCommandException(ExecuteQuery.MESSAGES.getString("wrongInputString", query));
        }
        int sizeValue = 999999;
        if (size != null) {
            if (size < 0.0) {
                throw new BotCommandException(ExecuteQuery.MESSAGES.getString("wrongSize", size));
            }
            sizeValue = (int)Math.round(size);
        }
        final Connection connection = this.sessions.get(sessionName);
        try {
            final Statement stmt = connection.createStatement();
            final ResultSet rs = stmt.executeQuery(query);
            this.toWrite(rs, fileName, sizeValue);
            rs.close();
            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new BotCommandException(ExecuteQuery.MESSAGES.getString("SQLError", e.getMessage()));
        }
    }
    
    public void setSessions(final Map<String, Object> sessions) {
        this.sessions = sessions;
    }
    
    private void validateSession(final String sessionName) {
        if (this.sessions == null || !this.sessions.containsKey(sessionName)) {
            throw new BotCommandException(ExecuteQuery.MESSAGES.getString("emptyConnection", sessionName));
        }
    }
    
    private void toWrite(final ResultSet resultSet, final String csvFileName, final int selectSize) {
        try {
            final File objFile = new File(csvFileName);
            objFile.getParentFile().mkdirs();
            objFile.createNewFile();
            if (!objFile.exists()) {
                throw new BotCommandException(ExecuteQuery.MESSAGES.getString("fileNotFound", csvFileName));
            }
            int counter = 0;
            boolean isheader = true;
            final int ncols = resultSet.getMetaData().getColumnCount();
            final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFileName, false), StandardCharsets.UTF_8));
            while (isheader || (resultSet.next() && (selectSize == 0 || counter < selectSize))) {
                for (int i = 1; i < ncols + 1; ++i) {
                    if (isheader) {
                        out.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, resultSet.getMetaData().getColumnName(i)));
                    }
                    else if (resultSet.getString(i) != null) {
                        out.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, resultSet.getString(i)));
                    }
                    else {
                        out.append((CharSequence)"");
                    }
                    if (i < ncols) {
                        out.append((CharSequence)",");
                    }
                    else {
                        out.append((CharSequence)"\r\n");
                    }
                }
                if (selectSize > 0 && !isheader) {
                    ++counter;
                }
                isheader = false;
            }
            out.flush();
            out.close();
        }
        catch (IOException e) {
            throw new BotCommandException(ExecuteQuery.MESSAGES.getString("SQLError", e.getMessage()));
        }
        catch (SQLException e2) {
            throw new BotCommandException(ExecuteQuery.MESSAGES.getString("SQLError", e2.getMessage()));
        }
    }
    
    static {
        MESSAGES = MessagesFactory.getMessages("com.automationanywhere.botcommand.messages.messages");
    }
}
