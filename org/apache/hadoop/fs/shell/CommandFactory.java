// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

import java.util.Arrays;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.util.StringUtils;
import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import java.util.Map;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.conf.Configured;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class CommandFactory extends Configured
{
    private Map<String, Class<? extends Command>> classMap;
    private Map<String, Command> objectMap;
    
    public CommandFactory() {
        this(null);
    }
    
    public CommandFactory(final Configuration conf) {
        super(conf);
        this.classMap = new HashMap<String, Class<? extends Command>>();
        this.objectMap = new HashMap<String, Command>();
    }
    
    public void registerCommands(final Class<?> registrarClass) {
        try {
            registrarClass.getMethod("registerCommands", CommandFactory.class).invoke(null, this);
        }
        catch (Exception e) {
            throw new RuntimeException(StringUtils.stringifyException(e));
        }
    }
    
    public void addClass(final Class<? extends Command> cmdClass, final String... names) {
        for (final String name : names) {
            this.classMap.put(name, cmdClass);
        }
    }
    
    public void addObject(final Command cmdObject, final String... names) {
        for (final String name : names) {
            this.objectMap.put(name, cmdObject);
            this.classMap.put(name, null);
        }
    }
    
    public Command getInstance(final String cmd) {
        return this.getInstance(cmd, this.getConf());
    }
    
    public Command getInstance(final String cmdName, final Configuration conf) {
        if (conf == null) {
            throw new NullPointerException("configuration is null");
        }
        Command instance = this.objectMap.get(cmdName);
        if (instance == null) {
            final Class<? extends Command> cmdClass = this.classMap.get(cmdName);
            if (cmdClass != null) {
                instance = ReflectionUtils.newInstance(cmdClass, conf);
                instance.setName(cmdName);
                instance.setCommandFactory(this);
            }
        }
        return instance;
    }
    
    public String[] getNames() {
        final String[] names = this.classMap.keySet().toArray(new String[0]);
        Arrays.sort(names);
        return names;
    }
}
