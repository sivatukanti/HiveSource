// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.tools;

import java.util.LinkedList;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public class TableListing
{
    private final Column[] columns;
    private int numRows;
    private final boolean showHeader;
    private final int wrapWidth;
    
    TableListing(final Column[] columns, final boolean showHeader, final int wrapWidth) {
        this.columns = columns;
        this.numRows = 0;
        this.showHeader = showHeader;
        this.wrapWidth = wrapWidth;
    }
    
    public void addRow(final String... row) {
        if (row.length != this.columns.length) {
            throw new RuntimeException("trying to add a row with " + row.length + " columns, but we have " + this.columns.length + " columns.");
        }
        for (int i = 0; i < this.columns.length; ++i) {
            this.columns[i].addRow(row[i]);
        }
        ++this.numRows;
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        int width = (this.columns.length - 1) * 2;
        for (int i = 0; i < this.columns.length; ++i) {
            width += this.columns[i].maxWidth;
        }
        while (width > this.wrapWidth) {
            boolean modified = false;
            for (int j = 0; j < this.columns.length; ++j) {
                final Column column = this.columns[j];
                if (column.wrap) {
                    final int maxWidth = column.getMaxWidth();
                    if (maxWidth > 4) {
                        column.setWrapWidth(maxWidth - 1);
                        modified = true;
                        if (--width <= this.wrapWidth) {
                            break;
                        }
                    }
                }
            }
            if (!modified) {
                break;
            }
        }
        int startrow = 0;
        if (!this.showHeader) {
            startrow = 1;
        }
        final String[][] columnLines = new String[this.columns.length][];
        for (int k = startrow; k < this.numRows + 1; ++k) {
            int maxColumnLines = 0;
            for (int l = 0; l < this.columns.length; ++l) {
                columnLines[l] = this.columns[l].getRow(k);
                if (columnLines[l].length > maxColumnLines) {
                    maxColumnLines = columnLines[l].length;
                }
            }
            for (int c = 0; c < maxColumnLines; ++c) {
                String prefix = "";
                for (int m = 0; m < this.columns.length; ++m) {
                    builder.append(prefix);
                    prefix = " ";
                    if (columnLines[m].length > c) {
                        builder.append(columnLines[m][c]);
                    }
                    else {
                        builder.append(StringUtils.repeat(" ", this.columns[m].maxWidth));
                    }
                }
                builder.append("\n");
            }
        }
        return builder.toString();
    }
    
    public enum Justification
    {
        LEFT, 
        RIGHT;
    }
    
    private static class Column
    {
        private final ArrayList<String> rows;
        private final Justification justification;
        private final boolean wrap;
        private int wrapWidth;
        private int maxWidth;
        
        Column(final String title, final Justification justification, final boolean wrap) {
            this.wrapWidth = Integer.MAX_VALUE;
            this.rows = new ArrayList<String>();
            this.justification = justification;
            this.wrap = wrap;
            this.maxWidth = 0;
            this.addRow(title);
        }
        
        private void addRow(String val) {
            if (val == null) {
                val = "";
            }
            if (val.length() + 1 > this.maxWidth) {
                this.maxWidth = val.length() + 1;
            }
            if (this.maxWidth > this.wrapWidth) {
                this.maxWidth = this.wrapWidth;
            }
            this.rows.add(val);
        }
        
        private int getMaxWidth() {
            return this.maxWidth;
        }
        
        private void setWrapWidth(final int width) {
            this.wrapWidth = width;
            if (this.maxWidth > this.wrapWidth) {
                this.maxWidth = this.wrapWidth;
            }
            else {
                this.maxWidth = 0;
                for (int i = 0; i < this.rows.size(); ++i) {
                    final int length = this.rows.get(i).length();
                    if (length > this.maxWidth) {
                        this.maxWidth = length;
                    }
                }
            }
        }
        
        String[] getRow(final int idx) {
            final String raw = this.rows.get(idx);
            String[] lines = { raw };
            if (this.wrap) {
                lines = org.apache.hadoop.util.StringUtils.wrap(lines[0], this.wrapWidth, "\n", true).split("\n");
            }
            for (int i = 0; i < lines.length; ++i) {
                if (this.justification == Justification.LEFT) {
                    lines[i] = StringUtils.rightPad(lines[i], this.maxWidth);
                }
                else if (this.justification == Justification.RIGHT) {
                    lines[i] = StringUtils.leftPad(lines[i], this.maxWidth);
                }
            }
            return lines;
        }
    }
    
    public static class Builder
    {
        private final LinkedList<Column> columns;
        private boolean showHeader;
        private int wrapWidth;
        
        public Builder() {
            this.columns = new LinkedList<Column>();
            this.showHeader = true;
            this.wrapWidth = Integer.MAX_VALUE;
        }
        
        public Builder addField(final String title) {
            return this.addField(title, Justification.LEFT, false);
        }
        
        public Builder addField(final String title, final Justification justification) {
            return this.addField(title, justification, false);
        }
        
        public Builder addField(final String title, final boolean wrap) {
            return this.addField(title, Justification.LEFT, wrap);
        }
        
        public Builder addField(final String title, final Justification justification, final boolean wrap) {
            this.columns.add(new Column(title, justification, wrap));
            return this;
        }
        
        public Builder hideHeaders() {
            this.showHeader = false;
            return this;
        }
        
        public Builder showHeaders() {
            this.showHeader = true;
            return this;
        }
        
        public Builder wrapWidth(final int width) {
            this.wrapWidth = width;
            return this;
        }
        
        public TableListing build() {
            return new TableListing(this.columns.toArray(new Column[0]), this.showHeader, this.wrapWidth);
        }
    }
}
