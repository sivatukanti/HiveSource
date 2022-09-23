// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp.view;

import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public class LipsumBlock extends HtmlBlock
{
    public void render(final Block html) {
        html.p()._("Lorem ipsum dolor sit amet, consectetur adipiscing elit.", "Vivamus eu dui in ipsum tincidunt egestas ac sed nibh.", "Praesent quis nisl lorem, nec interdum urna.", "Duis sagittis dignissim purus sed sollicitudin.", "Morbi quis diam eu enim semper suscipit.", "Nullam pretium faucibus sapien placerat tincidunt.", "Donec eget lorem at quam fermentum vulputate a ac purus.", "Cras ac dui felis, in pulvinar est.", "Praesent tempor est sed neque pulvinar dictum.", "Nullam magna augue, egestas luctus sollicitudin sed,", "venenatis nec turpis.", "Ut ante enim, congue sed laoreet et, accumsan id metus.", "Mauris tincidunt imperdiet est, sed porta arcu vehicula et.", "Etiam in nisi nunc.", "Phasellus vehicula scelerisque quam, ac dignissim felis euismod a.", "Proin eu ante nisl, vel porttitor eros.", "Aliquam gravida luctus augue, at scelerisque enim consectetur vel.", "Donec interdum tempor nisl, quis laoreet enim venenatis eu.", "Quisque elit elit, vulputate eget porta vel, laoreet ac lacus.")._();
    }
}
