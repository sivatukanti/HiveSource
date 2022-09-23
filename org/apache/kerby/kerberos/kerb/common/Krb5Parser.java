// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.common;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.IdentityHashMap;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Map;
import java.io.File;

public class Krb5Parser
{
    private File krb5conf;
    private Map<String, Object> items;
    
    public Krb5Parser(final File confFile) {
        this.krb5conf = confFile;
        this.items = null;
    }
    
    public void load() throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(this.krb5conf.toPath(), new OpenOption[0]), StandardCharsets.UTF_8));
        this.items = new IdentityHashMap<String, Object>();
        for (String originLine = br.readLine(); originLine != null; originLine = br.readLine()) {
            final String line = originLine.trim();
            if (!line.startsWith("#") && line.length() != 0) {
                if (line.startsWith("[")) {
                    this.insertSections(line, br, this.items);
                }
                else {
                    if (!line.startsWith("include")) {
                        throw new RuntimeException("Unable to parse:" + originLine);
                    }
                    final String[] splited = line.trim().split("\\s+");
                    if (splited.length != 2) {
                        throw new RuntimeException("Unable to parse:" + originLine);
                    }
                    this.items.put(splited[0], splited[1]);
                }
            }
        }
        br.close();
    }
    
    public Map<String, Object> getItems() {
        return this.items;
    }
    
    public List<String> getSections() {
        final List<String> al = new ArrayList<String>(this.items.keySet());
        return al;
    }
    
    public Object getSection(final String sectionName, final String... keys) {
        Object value = null;
        for (final Map.Entry<String, Object> item : this.items.entrySet()) {
            if (item.getKey().equals(sectionName)) {
                value = item.getValue();
                if (keys.length == 0) {
                    return value;
                }
                final Map<String, Object> map = item.getValue();
                for (final Map.Entry<String, Object> entry : map.entrySet()) {
                    if (entry.getKey().equals(keys[0])) {
                        value = entry.getValue();
                    }
                }
            }
        }
        for (int i = 1; i < keys.length; ++i) {
            final Map<String, Object> map2 = (Map<String, Object>)value;
            for (final Map.Entry<String, Object> entry2 : map2.entrySet()) {
                if (entry2.getKey().equals(keys[i])) {
                    value = entry2.getValue();
                }
            }
        }
        return value;
    }
    
    public void dump() {
        this.printSection(this.items);
    }
    
    private void insertSections(String line, final BufferedReader br, final Map<String, Object> items) throws IOException {
        while (line.startsWith("[")) {
            final String sectionName = line.substring(1, line.length() - 1);
            final Map<String, Object> entries = new IdentityHashMap<String, Object>();
            line = br.readLine();
            if (line == null) {
                break;
            }
            while (line.startsWith("#")) {
                line = br.readLine();
                if (line == null) {
                    break;
                }
            }
            if (line != null) {
                line = line.trim();
                line = this.insertEntries(line, br, entries);
                items.put(sectionName, entries);
            }
            if (line == null) {
                break;
            }
        }
    }
    
    private String insertEntries(String line, final BufferedReader br, final Map<String, Object> entries) throws IOException {
        if (line == null) {
            return line;
        }
        if (line.startsWith("[")) {
            return line;
        }
        if (line.startsWith("}")) {
            line = br.readLine();
            if (line != null) {
                line = line.trim();
            }
            return line;
        }
        if (line.length() == 0 || line.startsWith("#")) {
            line = br.readLine();
            if (line != null) {
                line = line.trim();
                line = this.insertEntries(line, br, entries);
            }
            return line;
        }
        final String[] kv = line.split("=", 2);
        kv[0] = kv[0].trim();
        kv[1] = kv[1].trim();
        if (kv[1].startsWith("{")) {
            final Map<String, Object> meValue = new IdentityHashMap<String, Object>();
            line = br.readLine();
            if (line != null) {
                line = line.trim();
                line = this.insertEntries(line, br, meValue);
                entries.put(kv[0], meValue);
                line = this.insertEntries(line, br, entries);
            }
        }
        else {
            entries.put(kv[0], kv[1]);
            line = br.readLine();
            if (line != null) {
                line = line.trim();
                line = this.insertEntries(line, br, entries);
            }
        }
        return line;
    }
    
    private void printSection(final Map<String, Object> map) {
        for (final Map.Entry entry : map.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            System.out.println("[" + key + "]");
            if (!(value instanceof Map)) {
                throw new RuntimeException("Unable to print contents of [" + key + "]");
            }
            final int count = 0;
            this.printEntry((Map<String, Object>)value, count);
        }
    }
    
    private void printEntry(final Map<String, Object> map, final int count) {
        for (final Map.Entry entry : map.entrySet()) {
            final String key = entry.getKey();
            final Object value = entry.getValue();
            for (int i = 0; i < count; ++i) {
                System.out.print("\t");
            }
            if (value instanceof String) {
                System.out.println(key + " = " + (String)value);
            }
            if (value instanceof Map) {
                System.out.println(key + " = {");
                this.printEntry((Map<String, Object>)value, count + 1);
                for (int i = 0; i < count; ++i) {
                    System.out.print("\t");
                }
                System.out.println("}");
            }
        }
    }
}
