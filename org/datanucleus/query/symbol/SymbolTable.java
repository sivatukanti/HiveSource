// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.query.symbol;

import org.datanucleus.util.StringUtils;
import org.datanucleus.exceptions.NucleusException;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

public class SymbolTable implements Serializable
{
    SymbolTable parentSymbolTable;
    Map<String, Symbol> symbols;
    List<Symbol> symbolsTable;
    transient SymbolResolver resolver;
    
    public SymbolTable() {
        this.parentSymbolTable = null;
        this.symbols = new HashMap<String, Symbol>();
        this.symbolsTable = new ArrayList<Symbol>();
    }
    
    public void setSymbolResolver(final SymbolResolver resolver) {
        this.resolver = resolver;
    }
    
    public SymbolResolver getSymbolResolver() {
        return this.resolver;
    }
    
    public void setParentSymbolTable(final SymbolTable tbl) {
        this.parentSymbolTable = tbl;
    }
    
    public SymbolTable getParentSymbolTable() {
        return this.parentSymbolTable;
    }
    
    Symbol getSymbol(final int index) {
        synchronized (this.symbolsTable) {
            return this.symbolsTable.get(index);
        }
    }
    
    public Collection<String> getSymbolNames() {
        return new HashSet<String>(this.symbols.keySet());
    }
    
    public Symbol getSymbol(final String name) {
        synchronized (this.symbolsTable) {
            return this.symbols.get(name);
        }
    }
    
    public Symbol getSymbolIgnoreCase(final String name) {
        synchronized (this.symbolsTable) {
            for (final String key : this.symbols.keySet()) {
                if (key.equalsIgnoreCase(name)) {
                    return this.symbols.get(key);
                }
            }
            return null;
        }
    }
    
    public boolean hasSymbol(final String name) {
        synchronized (this.symbolsTable) {
            return this.symbols.containsKey(name);
        }
    }
    
    public int addSymbol(final Symbol symbol) {
        synchronized (this.symbolsTable) {
            if (this.symbols.containsKey(symbol.getQualifiedName())) {
                throw new NucleusException("Symbol " + symbol.getQualifiedName() + " already exists.");
            }
            this.symbols.put(symbol.getQualifiedName(), symbol);
            this.symbolsTable.add(symbol);
            return this.symbolsTable.size();
        }
    }
    
    public void removeSymbol(final Symbol symbol) {
        synchronized (this.symbolsTable) {
            if (!this.symbols.containsKey(symbol.getQualifiedName())) {
                throw new NucleusException("Symbol " + symbol.getQualifiedName() + " doesnt exist.");
            }
            this.symbols.remove(symbol.getQualifiedName());
            this.symbolsTable.remove(symbol);
        }
    }
    
    @Override
    public String toString() {
        return "SymbolTable : " + StringUtils.mapToString(this.symbols);
    }
}
