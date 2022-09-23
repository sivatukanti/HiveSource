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

public final class ExecuteQueryCommand implements BotCommand
{
    private static final Logger logger;
    private static final Messages MESSAGES_GENERIC;
    
    @Deprecated
    public Optional<Value> execute(final Map<String, Value> parameters, final Map<String, Object> sessionMap) {
        return this.execute(null, parameters, sessionMap);
    }
    
    public Optional<Value> execute(final GlobalSessionContext globalSessionContext, final Map<String, Value> parameters, final Map<String, Object> sessionMap) {
        ExecuteQueryCommand.logger.traceEntry(new Supplier[] { () -> (parameters != null) ? parameters.toString() : null, () -> (sessionMap != null) ? sessionMap.toString() : null });
        final ExecuteQuery command = new ExecuteQuery();
        final HashMap<String, Object> convertedParameters = new HashMap<String, Object>();
        if (parameters.containsKey("sessionName") && parameters.get("sessionName") != null && parameters.get("sessionName").get() != null) {
            convertedParameters.put("sessionName", parameters.get("sessionName").get());
            if (!(convertedParameters.get("sessionName") instanceof String)) {
                throw new BotCommandException(ExecuteQueryCommand.MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived", "sessionName", "String", parameters.get("sessionName").get().getClass().getSimpleName()));
            }
        }
        if (convertedParameters.get("sessionName") == null) {
            throw new BotCommandException(ExecuteQueryCommand.MESSAGES_GENERIC.getString("generic.validation.notEmpty", "sessionName"));
        }
        if (parameters.containsKey("query") && parameters.get("query") != null && parameters.get("query").get() != null) {
            convertedParameters.put("query", parameters.get("query").get());
            if (!(convertedParameters.get("query") instanceof String)) {
                throw new BotCommandException(ExecuteQueryCommand.MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived", "query", "String", parameters.get("query").get().getClass().getSimpleName()));
            }
        }
        if (convertedParameters.get("query") == null) {
            throw new BotCommandException(ExecuteQueryCommand.MESSAGES_GENERIC.getString("generic.validation.notEmpty", "query"));
        }
        if (parameters.containsKey("size") && parameters.get("size") != null && parameters.get("size").get() != null) {
            convertedParameters.put("size", parameters.get("size").get());
            if (!(convertedParameters.get("size") instanceof Double)) {
                throw new BotCommandException(ExecuteQueryCommand.MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived", "size", "Double", parameters.get("size").get().getClass().getSimpleName()));
            }
        }
        if (parameters.containsKey("fileName") && parameters.get("fileName") != null && parameters.get("fileName").get() != null) {
            convertedParameters.put("fileName", parameters.get("fileName").get());
            if (!(convertedParameters.get("fileName") instanceof String)) {
                throw new BotCommandException(ExecuteQueryCommand.MESSAGES_GENERIC.getString("generic.UnexpectedTypeReceived", "fileName", "String", parameters.get("fileName").get().getClass().getSimpleName()));
            }
        }
        if (convertedParameters.get("fileName") == null) {
            throw new BotCommandException(ExecuteQueryCommand.MESSAGES_GENERIC.getString("generic.validation.notEmpty", "fileName"));
        }
        command.setSessions(sessionMap);
        try {
            command.action(convertedParameters.get("sessionName"), convertedParameters.get("query"), convertedParameters.get("size"), convertedParameters.get("fileName"));
            final Optional<Value> result = Optional.empty();
            return (Optional<Value>)ExecuteQueryCommand.logger.traceExit((Object)result);
        }
        catch (ClassCastException e3) {
            throw new BotCommandException(ExecuteQueryCommand.MESSAGES_GENERIC.getString("generic.IllegalParameters", "action"));
        }
        catch (BotCommandException e) {
            ExecuteQueryCommand.logger.fatal(e.getMessage(), (Throwable)e);
            throw e;
        }
        catch (Throwable e2) {
            ExecuteQueryCommand.logger.fatal(e2.getMessage(), e2);
            throw new BotCommandException(ExecuteQueryCommand.MESSAGES_GENERIC.getString("generic.NotBotCommandException", e2.getMessage()), e2);
        }
    }
    
    static {
        logger = LogManager.getLogger((Class)ExecuteQueryCommand.class);
        MESSAGES_GENERIC = MessagesFactory.getMessages("com.automationanywhere.commandsdk.generic.messages");
    }
}
