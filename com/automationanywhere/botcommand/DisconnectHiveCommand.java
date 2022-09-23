// 
// Decompiled by Procyon v0.5.36
// 

package com.automationanywhere.botcommand;

import com.automationanywhere.commandsdk.i18n.MessagesFactory;
import org.apache.logging.log4j.LogManager;
import com.automationanywhere.botcommand.exception.BotCommandException;
import java.util.HashMap;
import org.apache.logging.log4j.util.Supplier;
import com.automationanywhere.bot.service.GlobalSessionContext;
import java.util.Optional;
import com.automationanywhere.botcommand.data.Value;
import java.util.Map;
import com.automationanywhere.commandsdk.i18n.Messages;
import org.apache.logging.log4j.Logger;

public final class DisconnectHiveCommand implements BotCommand
{
    private static final Logger logger;
    private static final Messages MESSAGES_GENERIC;
    
    @Deprecated
    public Optional<Value> execute(final Map<String, Value> parameters, final Map<String, Object> sessionMap) {
        return this.execute(null, parameters, sessionMap);
    }
    
    public Optional<Value> execute(final GlobalSessionContext globalSessionContext, final Map<String, Value> parameters, final Map<String, Object> sessionMap) {
        DisconnectHiveCommand.logger.traceEntry(new Supplier[] { () -> (parameters != null) ? parameters.toString() : null, () -> (sessionMap != null) ? sessionMap.toString() : null });
        final DisconnectHive command = new DisconnectHive();
        final HashMap<String, Object> convertedParameters = new HashMap<String, Object>();
        if (parameters.containsKey("sessionName") && parameters.get("sessionName") != null && parameters.get("sessionName").get() != null) {
            convertedParameters.put("sessionName", parameters.get("sessionName").get());
            if (!(convertedParameters.get("sessionName") instanceof String)) {
                throw new BotCommandException(DisconnectHiveCommand.MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived", "sessionName", "String", parameters.get("sessionName").get().getClass().getSimpleName()));
            }
        }
        if (convertedParameters.get("sessionName") == null) {
            throw new BotCommandException(DisconnectHiveCommand.MESSAGES_GENERIC.getString("generic.validation.notEmpty", "sessionName"));
        }
        command.setSessions(sessionMap);
        try {
            command.action(convertedParameters.get("sessionName"));
            final Optional<Value> result = Optional.empty();
            return (Optional<Value>)DisconnectHiveCommand.logger.traceExit((Object)result);
        }
        catch (ClassCastException e3) {
            throw new BotCommandException(DisconnectHiveCommand.MESSAGES_GENERIC.getString("generic.IllegalParameters", "action"));
        }
        catch (BotCommandException e) {
            DisconnectHiveCommand.logger.fatal(e.getMessage(), (Throwable)e);
            throw e;
        }
        catch (Throwable e2) {
            DisconnectHiveCommand.logger.fatal(e2.getMessage(), e2);
            throw new BotCommandException(DisconnectHiveCommand.MESSAGES_GENERIC.getString("generic.NotBotCommandException", e2.getMessage()), e2);
        }
    }
    
    static {
        logger = LogManager.getLogger((Class)DisconnectHiveCommand.class);
        MESSAGES_GENERIC = MessagesFactory.getMessages("com.automationanywhere.commandsdk.generic.messages");
    }
}
