// 
// Decompiled by Procyon v0.5.36
// 

package com.automationanywhere.botcommand;

import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import org.apache.logging.log4j.LogManager;
import com.automationanywhere.core.security.SecureString;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.util.HashMap;
import org.apache.logging.log4j.util.Supplier;
import com.automationanywhere.bot.service.GlobalSessionContext;
import java.util.Optional;
import com.automationanywhere.botcommand.data.Value;
import java.util.Map;
import com.automationanywhere.commandsdk.i18n.Messages;
import org.apache.logging.log4j.Logger;

public final class ConnectHiveCommand implements BotCommand
{
    private static final Logger logger;
    private static final Messages MESSAGES_GENERIC;
    
    @Deprecated
    public Optional<Value> execute(final Map<String, Value> parameters, final Map<String, Object> sessionMap) {
        return this.execute(null, parameters, sessionMap);
    }
    
    public Optional<Value> execute(final GlobalSessionContext globalSessionContext, final Map<String, Value> parameters, final Map<String, Object> sessionMap) {
        ConnectHiveCommand.logger.traceEntry(new Supplier[] { () -> (parameters != null) ? parameters.toString() : null, () -> (sessionMap != null) ? sessionMap.toString() : null });
        final ConnectHive command = new ConnectHive();
        final HashMap<String, Object> convertedParameters = new HashMap<String, Object>();
        if (parameters.containsKey("sessionName") && parameters.get("sessionName") != null && parameters.get("sessionName").get() != null) {
            convertedParameters.put("sessionName", parameters.get("sessionName").get());
            if (!(convertedParameters.get("sessionName") instanceof String)) {
                throw new BotCommandException(ConnectHiveCommand.MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived", "sessionName", "String", parameters.get("sessionName").get().getClass().getSimpleName()));
            }
        }
        if (convertedParameters.get("sessionName") == null) {
            throw new BotCommandException(ConnectHiveCommand.MESSAGES_GENERIC.getString("generic.validation.notEmpty", "sessionName"));
        }
        if (parameters.containsKey("hiveVersion") && parameters.get("hiveVersion") != null && parameters.get("hiveVersion").get() != null) {
            convertedParameters.put("hiveVersion", parameters.get("hiveVersion").get());
            if (!(convertedParameters.get("hiveVersion") instanceof String)) {
                throw new BotCommandException(ConnectHiveCommand.MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived", "hiveVersion", "String", parameters.get("hiveVersion").get().getClass().getSimpleName()));
            }
        }
        if (convertedParameters.get("hiveVersion") == null) {
            throw new BotCommandException(ConnectHiveCommand.MESSAGES_GENERIC.getString("generic.validation.notEmpty", "hiveVersion"));
        }
        if (convertedParameters.get("hiveVersion") != null) {
            final String s = convertedParameters.get("hiveVersion");
            switch (s) {
                case "1": {
                    break;
                }
                case "2": {
                    break;
                }
                default: {
                    throw new BotCommandException(ConnectHiveCommand.MESSAGES_GENERIC.getString("generic.InvalidOption", "hiveVersion"));
                }
            }
        }
        if (parameters.containsKey("hostname") && parameters.get("hostname") != null && parameters.get("hostname").get() != null) {
            convertedParameters.put("hostname", parameters.get("hostname").get());
            if (!(convertedParameters.get("hostname") instanceof String)) {
                throw new BotCommandException(ConnectHiveCommand.MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived", "hostname", "String", parameters.get("hostname").get().getClass().getSimpleName()));
            }
        }
        if (convertedParameters.get("hostname") == null) {
            throw new BotCommandException(ConnectHiveCommand.MESSAGES_GENERIC.getString("generic.validation.notEmpty", "hostname"));
        }
        if (parameters.containsKey("portnumber") && parameters.get("portnumber") != null && parameters.get("portnumber").get() != null) {
            convertedParameters.put("portnumber", parameters.get("portnumber").get());
            if (!(convertedParameters.get("portnumber") instanceof String)) {
                throw new BotCommandException(ConnectHiveCommand.MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived", "portnumber", "String", parameters.get("portnumber").get().getClass().getSimpleName()));
            }
        }
        if (convertedParameters.get("portnumber") == null) {
            throw new BotCommandException(ConnectHiveCommand.MESSAGES_GENERIC.getString("generic.validation.notEmpty", "portnumber"));
        }
        if (parameters.containsKey("databasename") && parameters.get("databasename") != null && parameters.get("databasename").get() != null) {
            convertedParameters.put("databasename", parameters.get("databasename").get());
            if (!(convertedParameters.get("databasename") instanceof String)) {
                throw new BotCommandException(ConnectHiveCommand.MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived", "databasename", "String", parameters.get("databasename").get().getClass().getSimpleName()));
            }
        }
        if (convertedParameters.get("databasename") == null) {
            throw new BotCommandException(ConnectHiveCommand.MESSAGES_GENERIC.getString("generic.validation.notEmpty", "databasename"));
        }
        if (parameters.containsKey("connectionParams") && parameters.get("connectionParams") != null && parameters.get("connectionParams").get() != null) {
            convertedParameters.put("connectionParams", parameters.get("connectionParams").get());
            if (!(convertedParameters.get("connectionParams") instanceof String)) {
                throw new BotCommandException(ConnectHiveCommand.MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived", "connectionParams", "String", parameters.get("connectionParams").get().getClass().getSimpleName()));
            }
        }
        if (parameters.containsKey("username") && parameters.get("username") != null && parameters.get("username").get() != null) {
            convertedParameters.put("username", parameters.get("username").get());
            if (!(convertedParameters.get("username") instanceof SecureString)) {
                throw new BotCommandException(ConnectHiveCommand.MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived", "username", "SecureString", parameters.get("username").get().getClass().getSimpleName()));
            }
        }
        if (convertedParameters.get("username") == null) {
            throw new BotCommandException(ConnectHiveCommand.MESSAGES_GENERIC.getString("generic.validation.notEmpty", "username"));
        }
        if (parameters.containsKey("password") && parameters.get("password") != null && parameters.get("password").get() != null) {
            convertedParameters.put("password", parameters.get("password").get());
            if (!(convertedParameters.get("password") instanceof SecureString)) {
                throw new BotCommandException(ConnectHiveCommand.MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived", "password", "SecureString", parameters.get("password").get().getClass().getSimpleName()));
            }
        }
        if (convertedParameters.get("password") == null) {
            throw new BotCommandException(ConnectHiveCommand.MESSAGES_GENERIC.getString("generic.validation.notEmpty", "password"));
        }
        command.setSessions(sessionMap);
        try {
            command.action(convertedParameters.get("sessionName"), convertedParameters.get("hiveVersion"), convertedParameters.get("hostname"), convertedParameters.get("portnumber"), convertedParameters.get("databasename"), convertedParameters.get("connectionParams"), convertedParameters.get("username"), convertedParameters.get("password"));
            final Optional<Value> result = Optional.empty();
            return (Optional<Value>)ConnectHiveCommand.logger.traceExit((Object)result);
        }
        catch (ClassCastException e3) {
            throw new BotCommandException(ConnectHiveCommand.MESSAGES_GENERIC.getString("generic.IllegalParameters", "action"));
        }
        catch (BotCommandException e) {
            ConnectHiveCommand.logger.fatal(e.getMessage(), (Throwable)e);
            throw e;
        }
        catch (Throwable e2) {
            ConnectHiveCommand.logger.fatal(e2.getMessage(), e2);
            throw new BotCommandException(ConnectHiveCommand.MESSAGES_GENERIC.getString("generic.NotBotCommandException", e2.getMessage()), e2);
        }
    }
    
    static {
        logger = LogManager.getLogger((Class)ConnectHiveCommand.class);
        MESSAGES_GENERIC = MessagesFactory.getMessages("com.automationanywhere.commandsdk.generic.messages");
    }
}
