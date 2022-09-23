// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.ssl;

import java.util.ArrayList;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.Iterator;
import java.util.Comparator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.regex.Pattern;
import java.util.List;
import java.util.Arrays;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.ContainerLifeCycle;

public class SslSelectionDump extends ContainerLifeCycle implements Dumpable
{
    private final String type;
    private CaptionedList enabled;
    private CaptionedList disabled;
    
    public SslSelectionDump(final String type, final String[] supportedByJVM, final String[] enabledByJVM, final String[] excludedByConfig, final String[] includedByConfig) {
        this.enabled = new CaptionedList("Enabled");
        this.disabled = new CaptionedList("Disabled");
        this.type = type;
        this.addBean(this.enabled);
        this.addBean(this.disabled);
        final List<String> jvmEnabled = Arrays.asList(enabledByJVM);
        final List<Pattern> excludedPatterns = Arrays.stream(excludedByConfig).map(entry -> Pattern.compile(entry)).collect((Collector<? super Object, ?, List<Pattern>>)Collectors.toList());
        final List<Pattern> includedPatterns = Arrays.stream(includedByConfig).map(entry -> Pattern.compile(entry)).collect((Collector<? super Object, ?, List<Pattern>>)Collectors.toList());
        boolean isPresent;
        final StringBuilder s;
        final List list;
        final List<Pattern> list2;
        final Iterator<Pattern> iterator;
        Pattern pattern;
        Matcher m;
        final List list3;
        boolean isIncluded;
        final Iterator<Pattern> iterator2;
        Pattern pattern2;
        Matcher i;
        Arrays.stream(supportedByJVM).sorted(Comparator.naturalOrder()).forEach(entry -> {
            isPresent = true;
            s = new StringBuilder();
            s.append(entry);
            if (!list.contains(entry)) {
                if (isPresent) {
                    s.append(" -");
                    isPresent = false;
                }
                s.append(" JreDisabled:java.security");
            }
            list2.iterator();
            while (iterator.hasNext()) {
                pattern = iterator.next();
                m = pattern.matcher(entry);
                if (m.matches()) {
                    if (isPresent) {
                        s.append(" -");
                        isPresent = false;
                    }
                    else {
                        s.append(",");
                    }
                    s.append(" ConfigExcluded:'").append(pattern.pattern()).append('\'');
                }
            }
            if (!list3.isEmpty()) {
                isIncluded = false;
                list3.iterator();
                while (iterator2.hasNext()) {
                    pattern2 = iterator2.next();
                    i = pattern2.matcher(entry);
                    if (i.matches()) {
                        isIncluded = true;
                        break;
                    }
                }
                if (!isIncluded) {
                    if (isPresent) {
                        s.append(" -");
                        isPresent = false;
                    }
                    else {
                        s.append(",");
                    }
                    s.append(" ConfigIncluded:NotSpecified");
                }
            }
            if (isPresent) {
                this.enabled.add(s.toString());
            }
            else {
                this.disabled.add(s.toString());
            }
        });
    }
    
    @Override
    public String dump() {
        return ContainerLifeCycle.dump(this);
    }
    
    @Override
    public void dump(final Appendable out, final String indent) throws IOException {
        this.dumpBeans(out, indent, (Collection<?>[])new Collection[0]);
    }
    
    @Override
    protected void dumpThis(final Appendable out) throws IOException {
        out.append(this.type).append(" Selections").append(System.lineSeparator());
    }
    
    private static class CaptionedList extends ArrayList<String> implements Dumpable
    {
        private final String caption;
        
        public CaptionedList(final String caption) {
            this.caption = caption;
        }
        
        @Override
        public String dump() {
            return ContainerLifeCycle.dump(this);
        }
        
        @Override
        public void dump(final Appendable out, final String indent) throws IOException {
            out.append(this.caption);
            out.append(" (size=").append(Integer.toString(this.size())).append(")");
            out.append(System.lineSeparator());
            ContainerLifeCycle.dump(out, indent, this);
        }
    }
}
