// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.common;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import java.util.List;
import org.apache.kerby.config.Config;
import org.apache.kerby.config.ConfigKey;
import java.io.IOException;
import org.apache.kerby.config.Resource;
import java.io.File;
import java.util.Map;
import org.apache.kerby.config.Conf;

public class Krb5Conf extends Conf
{
    private static final String LIST_SPLITTER = " |,";
    private Map<String, Object> krb5Map;
    
    public void addKrb5Config(final File krb5File) throws IOException {
        final Krb5Parser krb5Parser = new Krb5Parser(krb5File);
        krb5Parser.load();
        this.krb5Map = krb5Parser.getItems();
        this.addResource(Resource.createMapResource(this.krb5Map));
    }
    
    protected String getString(final ConfigKey key, final boolean useDefault, final String... sections) {
        String value = this.getString(key, false);
        if (value == null) {
            for (final String section : sections) {
                final Config subConfig = this.getConfig(section);
                if (subConfig != null) {
                    value = subConfig.getString(key, false);
                    if (value != null) {
                        break;
                    }
                }
            }
        }
        if (value == null && useDefault) {
            value = (String)key.getDefaultValue();
        }
        return value;
    }
    
    protected Boolean getBoolean(final ConfigKey key, final boolean useDefault, final String... sections) {
        Boolean value = this.getBoolean(key, false);
        if (value == null) {
            for (final String section : sections) {
                final Config subConfig = this.getConfig(section);
                if (subConfig != null) {
                    value = subConfig.getBoolean(key, false);
                    if (value != null) {
                        break;
                    }
                }
            }
        }
        if (value == null && useDefault) {
            value = (Boolean)key.getDefaultValue();
        }
        return value;
    }
    
    protected Long getLong(final ConfigKey key, final boolean useDefault, final String... sections) {
        Long value = this.getLong(key, false);
        if (value == null) {
            for (final String section : sections) {
                final Config subConfig = this.getConfig(section);
                if (subConfig != null) {
                    value = subConfig.getLong(key, false);
                    if (value != null) {
                        break;
                    }
                }
            }
        }
        if (value == null && useDefault) {
            value = (Long)key.getDefaultValue();
        }
        return value;
    }
    
    protected Integer getInt(final ConfigKey key, final boolean useDefault, final String... sections) {
        Integer value = this.getInt(key, false);
        if (value == null) {
            for (final String section : sections) {
                final Config subConfig = this.getConfig(section);
                if (subConfig != null) {
                    value = subConfig.getInt(key, false);
                    if (value != null) {
                        break;
                    }
                }
            }
        }
        if (value == null && useDefault) {
            value = (Integer)key.getDefaultValue();
        }
        return value;
    }
    
    protected List<EncryptionType> getEncTypes(final ConfigKey key, final boolean useDefault, final String... sections) {
        final String[] encTypesNames = this.getStringArray(key, useDefault, sections);
        return this.getEncryptionTypes(encTypesNames);
    }
    
    protected List<EncryptionType> getEncryptionTypes(final String[] encTypeNames) {
        return this.getEncryptionTypes(Arrays.asList(encTypeNames));
    }
    
    protected List<EncryptionType> getEncryptionTypes(final List<String> encTypeNames) {
        final List<EncryptionType> results = new ArrayList<EncryptionType>(encTypeNames.size());
        for (final String eTypeName : encTypeNames) {
            final EncryptionType eType = EncryptionType.fromName(eTypeName);
            if (eType != EncryptionType.NONE) {
                results.add(eType);
            }
        }
        return results;
    }
    
    protected String[] getStringArray(final ConfigKey key, final boolean useDefault, final String... sections) {
        final String value = this.getString(key, useDefault, sections);
        if (value != null) {
            return value.split(" |,");
        }
        return new String[0];
    }
    
    protected Object getSection(final String sectionName) {
        if (this.krb5Map != null) {
            for (final Map.Entry<String, Object> entry : this.krb5Map.entrySet()) {
                if (entry.getKey().equals(sectionName)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }
}
