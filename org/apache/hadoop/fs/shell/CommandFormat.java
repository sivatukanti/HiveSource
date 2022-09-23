// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class CommandFormat
{
    final int minPar;
    final int maxPar;
    final Map<String, Boolean> options;
    final Map<String, String> optionsWithValue;
    boolean ignoreUnknownOpts;
    
    @Deprecated
    public CommandFormat(final String name, final int min, final int max, final String... possibleOpt) {
        this(min, max, possibleOpt);
    }
    
    public CommandFormat(final int min, final int max, final String... possibleOpt) {
        this.options = new HashMap<String, Boolean>();
        this.optionsWithValue = new HashMap<String, String>();
        this.ignoreUnknownOpts = false;
        this.minPar = min;
        this.maxPar = max;
        for (final String opt : possibleOpt) {
            if (opt == null) {
                this.ignoreUnknownOpts = true;
            }
            else {
                this.options.put(opt, Boolean.FALSE);
            }
        }
    }
    
    public void addOptionWithValue(final String option) {
        if (this.options.containsKey(option)) {
            throw new DuplicatedOptionException(option);
        }
        this.optionsWithValue.put(option, null);
    }
    
    public List<String> parse(final String[] args, final int pos) {
        final List<String> parameters = new ArrayList<String>(Arrays.asList(args));
        parameters.subList(0, pos).clear();
        this.parse(parameters);
        return parameters;
    }
    
    public void parse(final List<String> args) {
        int pos = 0;
        while (pos < args.size()) {
            String arg = args.get(pos);
            if (!arg.startsWith("-")) {
                break;
            }
            if (arg.equals("-")) {
                break;
            }
            if (arg.equals("--")) {
                args.remove(pos);
                break;
            }
            final String opt = arg.substring(1);
            if (this.options.containsKey(opt)) {
                args.remove(pos);
                this.options.put(opt, Boolean.TRUE);
            }
            else if (this.optionsWithValue.containsKey(opt)) {
                args.remove(pos);
                if (pos < args.size() && args.size() > this.minPar && !args.get(pos).startsWith("-")) {
                    arg = args.get(pos);
                    args.remove(pos);
                }
                else {
                    arg = "";
                }
                if (arg.startsWith("-") && !arg.equals("-")) {
                    continue;
                }
                this.optionsWithValue.put(opt, arg);
            }
            else {
                if (!this.ignoreUnknownOpts) {
                    throw new UnknownOptionException(arg);
                }
                ++pos;
            }
        }
        final int psize = args.size();
        if (psize < this.minPar) {
            throw new NotEnoughArgumentsException(this.minPar, psize);
        }
        if (psize > this.maxPar) {
            throw new TooManyArgumentsException(this.maxPar, psize);
        }
    }
    
    public boolean getOpt(final String option) {
        return this.options.containsKey(option) && this.options.get(option);
    }
    
    public String getOptValue(final String option) {
        return this.optionsWithValue.get(option);
    }
    
    public Set<String> getOpts() {
        final Set<String> optSet = new HashSet<String>();
        for (final Map.Entry<String, Boolean> entry : this.options.entrySet()) {
            if (entry.getValue()) {
                optSet.add(entry.getKey());
            }
        }
        return optSet;
    }
    
    public abstract static class IllegalNumberOfArgumentsException extends IllegalArgumentException
    {
        private static final long serialVersionUID = 0L;
        protected int expected;
        protected int actual;
        
        protected IllegalNumberOfArgumentsException(final int want, final int got) {
            this.expected = want;
            this.actual = got;
        }
        
        @Override
        public String getMessage() {
            return "expected " + this.expected + " but got " + this.actual;
        }
    }
    
    public static class TooManyArgumentsException extends IllegalNumberOfArgumentsException
    {
        private static final long serialVersionUID = 0L;
        
        public TooManyArgumentsException(final int expected, final int actual) {
            super(expected, actual);
        }
        
        @Override
        public String getMessage() {
            return "Too many arguments: " + super.getMessage();
        }
    }
    
    public static class NotEnoughArgumentsException extends IllegalNumberOfArgumentsException
    {
        private static final long serialVersionUID = 0L;
        
        public NotEnoughArgumentsException(final int expected, final int actual) {
            super(expected, actual);
        }
        
        @Override
        public String getMessage() {
            return "Not enough arguments: " + super.getMessage();
        }
    }
    
    public static class UnknownOptionException extends IllegalArgumentException
    {
        private static final long serialVersionUID = 0L;
        protected String option;
        
        public UnknownOptionException(final String unknownOption) {
            super("Illegal option " + unknownOption);
            this.option = null;
            this.option = unknownOption;
        }
        
        public String getOption() {
            return this.option;
        }
    }
    
    public static class DuplicatedOptionException extends IllegalArgumentException
    {
        private static final long serialVersionUID = 0L;
        
        public DuplicatedOptionException(final String duplicatedOption) {
            super("option " + duplicatedOption + " already exists!");
        }
    }
}
