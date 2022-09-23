// 
// Decompiled by Procyon v0.5.36
// 

package org.jboss.netty.handler.codec.spdy;

import java.util.Iterator;
import org.jboss.netty.util.internal.StringUtil;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map;

public class DefaultSpdySettingsFrame implements SpdySettingsFrame
{
    private boolean clear;
    private final Map<Integer, Setting> settingsMap;
    
    public DefaultSpdySettingsFrame() {
        this.settingsMap = new TreeMap<Integer, Setting>();
    }
    
    public Set<Integer> getIds() {
        return this.settingsMap.keySet();
    }
    
    public boolean isSet(final int id) {
        final Integer key = id;
        return this.settingsMap.containsKey(key);
    }
    
    public int getValue(final int id) {
        final Integer key = id;
        if (this.settingsMap.containsKey(key)) {
            return this.settingsMap.get(key).getValue();
        }
        return -1;
    }
    
    public void setValue(final int id, final int value) {
        this.setValue(id, value, false, false);
    }
    
    public void setValue(final int id, final int value, final boolean persistValue, final boolean persisted) {
        if (id < 0 || id > 16777215) {
            throw new IllegalArgumentException("Setting ID is not valid: " + id);
        }
        final Integer key = id;
        if (this.settingsMap.containsKey(key)) {
            final Setting setting = this.settingsMap.get(key);
            setting.setValue(value);
            setting.setPersist(persistValue);
            setting.setPersisted(persisted);
        }
        else {
            this.settingsMap.put(key, new Setting(value, persistValue, persisted));
        }
    }
    
    public void removeValue(final int id) {
        final Integer key = id;
        if (this.settingsMap.containsKey(key)) {
            this.settingsMap.remove(key);
        }
    }
    
    public boolean isPersistValue(final int id) {
        final Integer key = id;
        return this.settingsMap.containsKey(key) && this.settingsMap.get(key).isPersist();
    }
    
    public void setPersistValue(final int id, final boolean persistValue) {
        final Integer key = id;
        if (this.settingsMap.containsKey(key)) {
            this.settingsMap.get(key).setPersist(persistValue);
        }
    }
    
    public boolean isPersisted(final int id) {
        final Integer key = id;
        return this.settingsMap.containsKey(key) && this.settingsMap.get(key).isPersisted();
    }
    
    public void setPersisted(final int id, final boolean persisted) {
        final Integer key = id;
        if (this.settingsMap.containsKey(key)) {
            this.settingsMap.get(key).setPersisted(persisted);
        }
    }
    
    public boolean clearPreviouslyPersistedSettings() {
        return this.clear;
    }
    
    public void setClearPreviouslyPersistedSettings(final boolean clear) {
        this.clear = clear;
    }
    
    private Set<Map.Entry<Integer, Setting>> getSettings() {
        return this.settingsMap.entrySet();
    }
    
    private void appendSettings(final StringBuilder buf) {
        for (final Map.Entry<Integer, Setting> e : this.getSettings()) {
            final Setting setting = e.getValue();
            buf.append("--> ");
            buf.append(e.getKey().toString());
            buf.append(':');
            buf.append(setting.getValue());
            buf.append(" (persist value: ");
            buf.append(setting.isPersist());
            buf.append("; persisted: ");
            buf.append(setting.isPersisted());
            buf.append(')');
            buf.append(StringUtil.NEWLINE);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.getClass().getSimpleName());
        buf.append(StringUtil.NEWLINE);
        this.appendSettings(buf);
        buf.setLength(buf.length() - StringUtil.NEWLINE.length());
        return buf.toString();
    }
    
    private static final class Setting
    {
        private int value;
        private boolean persist;
        private boolean persisted;
        
        Setting(final int value, final boolean persist, final boolean persisted) {
            this.value = value;
            this.persist = persist;
            this.persisted = persisted;
        }
        
        int getValue() {
            return this.value;
        }
        
        void setValue(final int value) {
            this.value = value;
        }
        
        boolean isPersist() {
            return this.persist;
        }
        
        void setPersist(final boolean persist) {
            this.persist = persist;
        }
        
        boolean isPersisted() {
            return this.persisted;
        }
        
        void setPersisted(final boolean persisted) {
            this.persisted = persisted;
        }
    }
}
