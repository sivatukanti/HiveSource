// 
// Decompiled by Procyon v0.5.36
// 

package com.automationanywhere.botcommand;

import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Connection;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.i18n.Messages;
import java.util.Map;

public class ExecuteInsertUpdate
{
    private Map<String, Object> sessions;
    private static final Messages MESSAGES;
    
    public void action(final String sessionName, final String query) {
        this.validateSession(sessionName);
        if ("".equals(query.trim())) {
            throw new BotCommandException(ExecuteInsertUpdate.MESSAGES.getString("emptyInputString", "query"));
        }
        if (!query.toLowerCase().contains("insert") && !query.toLowerCase().contains("update") && !query.toLowerCase().contains("alter") && !query.toLowerCase().contains("drop") && !query.toLowerCase().contains("create")) {
            throw new BotCommandException(ExecuteInsertUpdate.MESSAGES.getString("wrongInputString", query));
        }
        final Connection connection = this.sessions.get(sessionName);
        try {
            final Statement stmt = connection.createStatement();
            stmt.execute(query);
            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new BotCommandException(ExecuteInsertUpdate.MESSAGES.getString("SQLError", e.getMessage()));
        }
    }
    
    public void setSessions(final Map<String, Object> sessions) {
        this.sessions = sessions;
    }
    
    private void validateSession(final String sessionName) {
        if (this.sessions == null || !this.sessions.containsKey(sessionName)) {
            throw new BotCommandException(ExecuteInsertUpdate.MESSAGES.getString("emptyConnection", sessionName));
        }
    }
    
    static {
        MESSAGES = MessagesFactory.getMessages("com.automationanywhere.botcommand.messages.messages");
    }
}
