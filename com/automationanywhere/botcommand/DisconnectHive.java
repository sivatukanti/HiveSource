// 
// Decompiled by Procyon v0.5.36
// 

package com.automationanywhere.botcommand;

import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import java.sql.SQLException;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.sql.Connection;
import com.automationanywhere.commandsdk.i18n.Messages;
import java.util.Map;

public class DisconnectHive
{
    private Map<String, Object> sessions;
    private static final Messages MESSAGES;
    
    public void action(final String sessionName) {
        final Connection connection = this.sessions.get(sessionName);
        try {
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new BotCommandException(DisconnectHive.MESSAGES.getString("emptyConnection", sessionName));
        }
        this.sessions.remove(sessionName);
    }
    
    public void setSessions(final Map<String, Object> sessions) {
        this.sessions = sessions;
    }
    
    static {
        MESSAGES = MessagesFactory.getMessages("com.automationanywhere.botcommand.messages.messages");
    }
}
