// 
// Decompiled by Procyon v0.5.36
// 

package com.automationanywhere.botcommand;

import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import java.sql.DriverManager;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.core.security.SecureString;
import java.sql.Connection;
import com.automationanywhere.commandsdk.i18n.Messages;
import java.util.Map;

public class ConnectHive
{
    private Map<String, Object> sessions;
    private static final Messages MESSAGES;
    private Connection connection;
    
    public void action(final String sessionName, final String hiveVersion, final String hostname, final String portnumber, final String databasename, final String connectionParams, final SecureString username, final SecureString password) {
        this.validateSameSession(sessionName);
        this.connection = this.connectDB(hiveVersion, hostname, portnumber, databasename, connectionParams, username.getInsecureString(), password.getInsecureString());
        this.sessions.put(sessionName, this.connection);
    }
    
    private Connection connectDB(final String hiveVersion, final String hostname, final String portnumber, final String databasename, final String connectionParams, final String username, final String password) {
        if ("".equals(hiveVersion.trim())) {
            throw new BotCommandException(ConnectHive.MESSAGES.getString("emptyInputString", "hiveVersion"));
        }
        if ("".equals(hostname.trim())) {
            throw new BotCommandException(ConnectHive.MESSAGES.getString("emptyInputString", "hostname"));
        }
        if ("".equals(portnumber.trim())) {
            throw new BotCommandException(ConnectHive.MESSAGES.getString("emptyInputString", "portnumber"));
        }
        if ("".equals(databasename.trim())) {
            throw new BotCommandException(ConnectHive.MESSAGES.getString("emptyInputString", "databasename"));
        }
        if ("".equals(username.trim())) {
            throw new BotCommandException(ConnectHive.MESSAGES.getString("emptyInputString", "username"));
        }
        // if ("".equals(password.trim())) {
        //     throw new BotCommandException(ConnectHive.MESSAGES.getString("emptyInputString", "password"));
        // }
        String driverName = null;
        String connectionUrl = null;
        String connectionUrlHeader = null;
        String connectionUrlFooter = null;
        if (hiveVersion.equals("1")) {
            driverName = "org.apache.hadoop.hive.jdbc.HiveDriver";
            connectionUrlHeader = "jdbc:hive://";
            connectionUrlFooter = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, databasename, connectionParams);
        }
        else if (hiveVersion.equals("2")) {
            driverName = "org.apache.hive.jdbc.HiveDriver";
            connectionUrlHeader = "jdbc:hive2://";
            connectionUrlFooter = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, databasename, connectionParams);
        }
        else if (hiveVersion.equals("3")) {}
        connectionUrl = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, connectionUrlHeader, hostname, portnumber, connectionUrlFooter);
        System.out.println("***********************************************");
        System.out.println(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, connectionUrl));
        System.out.println("***********************************************");
        if (driverName == null) {
            throw new BotCommandException(ConnectHive.MESSAGES.getString("notImplemented", "3"));
        }
        Connection con = null;
        try {
            Class.forName(driverName);
            con = DriverManager.getConnection(connectionUrl, username, password);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new BotCommandException(ConnectHive.MESSAGES.getString("SQLError", e.getMessage()));
        }
        return con;
    }
    
    private void validateSameSession(final String sessionName) {
        if (this.sessions != null && this.sessions.containsKey(sessionName)) {
            throw new BotCommandException(ConnectHive.MESSAGES.getString("sameSession", sessionName));
        }
    }
    
    public void setSessions(final Map<String, Object> sessions) {
        this.sessions = sessions;
    }
    
    static {
        MESSAGES = MessagesFactory.getMessages("com.automationanywhere.botcommand.messages.messages");
    }
}
