// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp.view;

import org.apache.commons.lang.StringEscapeUtils;
import java.util.Iterator;
import org.apache.hadoop.yarn.util.StringHelper;
import org.apache.hadoop.yarn.webapp.hamlet.HamletSpec;
import java.util.List;
import com.google.common.collect.Lists;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public class JQueryUI extends HtmlBlock
{
    public static final String ACCORDION = "ui.accordion";
    public static final String ACCORDION_ID = "ui.accordion.id";
    public static final String DATATABLES = "ui.dataTables";
    public static final String DATATABLES_ID = "ui.dataTables.id";
    public static final String DATATABLES_SELECTOR = "ui.dataTables.selector";
    public static final String DIALOG = "ui.dialog";
    public static final String DIALOG_ID = "ui.dialog.id";
    public static final String DIALOG_SELECTOR = "ui.dialog.selector";
    public static final String PROGRESSBAR = "ui.progressbar";
    public static final String PROGRESSBAR_ID = "ui.progressbar.id";
    public static final String _PROGRESSBAR = ".ui-progressbar.ui-widget.ui-widget-content.ui-corner-all";
    public static final String C_PROGRESSBAR;
    public static final String _PROGRESSBAR_VALUE = ".ui-progressbar-value.ui-widget-header.ui-corner-left";
    public static final String C_PROGRESSBAR_VALUE;
    public static final String _INFO_WRAP = ".info-wrap.ui-widget-content.ui-corner-bottom";
    public static final String _TH = ".ui-state-default";
    public static final String C_TH;
    public static final String C_TABLE = "table";
    public static final String _INFO = ".info";
    public static final String _ODD = ".odd";
    public static final String _EVEN = ".even";
    
    @Override
    protected void render(final Block html) {
        html.link(this.root_url("static/jquery/themes-1.9.1/base/jquery-ui.css")).link(this.root_url("static/dt-1.9.4/css/jui-dt.css")).script(this.root_url("static/jquery/jquery-1.8.2.min.js")).script(this.root_url("static/jquery/jquery-ui-1.9.1.custom.min.js")).script(this.root_url("static/dt-1.9.4/js/jquery.dataTables.min.js")).script(this.root_url("static/yarn.dt.plugins.js")).style("#jsnotice { padding: 0.2em; text-align: center; }", ".ui-progressbar { height: 1em; min-width: 5em }");
        final List<String> list = (List<String>)Lists.newArrayList();
        this.initAccordions(list);
        this.initDataTables(list);
        this.initDialogs(list);
        this.initProgressBars(list);
        if (!list.isEmpty()) {
            html.script().$type("text/javascript")._("$(function() {")._(list.toArray())._("});")._();
        }
    }
    
    public static void jsnotice(final HamletSpec.HTML html) {
        html.div("#jsnotice.ui-state-error")._("This page works best with javascript enabled.")._();
        html.script().$type("text/javascript")._("$('#jsnotice').hide();")._();
    }
    
    protected void initAccordions(final List<String> list) {
        for (final String id : StringHelper.split(this.$("ui.accordion.id"))) {
            if (Html.isValidId(id)) {
                String init = this.$(initID("ui.accordion", id));
                if (init.isEmpty()) {
                    init = "{autoHeight: false}";
                }
                list.add(StringHelper.join("  $('#", id, "').accordion(", init, ");"));
            }
        }
    }
    
    protected void initDataTables(final List<String> list) {
        final String defaultInit = "{bJQueryUI: true, sPaginationType: 'full_numbers'}";
        final String stateSaveInit = "bStateSave : true, \"fnStateSave\": function (oSettings, oData) { sessionStorage.setItem( oSettings.sTableId, JSON.stringify(oData) ); }, \"fnStateLoad\": function (oSettings) { return JSON.parse( sessionStorage.getItem(oSettings.sTableId) );}, ";
        for (final String id : StringHelper.split(this.$("ui.dataTables.id"))) {
            if (Html.isValidId(id)) {
                String init = this.$(initID("ui.dataTables", id));
                if (init.isEmpty()) {
                    init = defaultInit;
                }
                final int pos = init.indexOf(123) + 1;
                init = new StringBuffer(init).insert(pos, stateSaveInit).toString();
                list.add(StringHelper.join(id, "DataTable =  $('#", id, "').dataTable(", init, ").fnSetFilteringDelay(188);"));
                final String postInit = this.$(postInitID("ui.dataTables", id));
                if (postInit.isEmpty()) {
                    continue;
                }
                list.add(postInit);
            }
        }
        final String selector = this.$("ui.dataTables.selector");
        if (!selector.isEmpty()) {
            String init2 = this.$(initSelector("ui.dataTables"));
            if (init2.isEmpty()) {
                init2 = defaultInit;
            }
            final int pos2 = init2.indexOf(123) + 1;
            init2 = new StringBuffer(init2).insert(pos2, stateSaveInit).toString();
            list.add(StringHelper.join("  $('", StringEscapeUtils.escapeJavaScript(selector), "').dataTable(", init2, ").fnSetFilteringDelay(288);"));
        }
    }
    
    protected void initDialogs(final List<String> list) {
        final String defaultInit = "{autoOpen: false, show: transfer, hide: explode}";
        for (final String id : StringHelper.split(this.$("ui.dialog.id"))) {
            if (Html.isValidId(id)) {
                String init = this.$(initID("ui.dialog", id));
                if (init.isEmpty()) {
                    init = defaultInit;
                }
                final String opener = this.$(StringHelper.djoin("ui.dialog", id, "opener"));
                list.add(StringHelper.join("  $('#", id, "').dialog(", init, ");"));
                if (opener.isEmpty() || !Html.isValidId(opener)) {
                    continue;
                }
                list.add(StringHelper.join("  $('#", opener, "').click(function() { ", "$('#", id, "').dialog('open'); return false; });"));
            }
        }
        final String selector = this.$("ui.dialog.selector");
        if (!selector.isEmpty()) {
            String init2 = this.$(initSelector("ui.dialog"));
            if (init2.isEmpty()) {
                init2 = defaultInit;
            }
            list.add(StringHelper.join("  $('", StringEscapeUtils.escapeJavaScript(selector), "').click(function() { $(this).children('.dialog').dialog(", init2, "); return false; });"));
        }
    }
    
    protected void initProgressBars(final List<String> list) {
        for (final String id : StringHelper.split(this.$("ui.progressbar.id"))) {
            if (Html.isValidId(id)) {
                final String init = this.$(initID("ui.progressbar", id));
                list.add(StringHelper.join("  $('#", id, "').progressbar(", init, ");"));
            }
        }
    }
    
    public static String initID(final String name, final String id) {
        return StringHelper.djoin(name, id, "init");
    }
    
    public static String postInitID(final String name, final String id) {
        return StringHelper.djoin(name, id, "postinit");
    }
    
    public static String initSelector(final String name) {
        return StringHelper.djoin(name, "selector.init");
    }
    
    public static StringBuilder tableInit() {
        return new StringBuilder("{bJQueryUI:true, ").append("sPaginationType: 'full_numbers', iDisplayLength:20, ").append("aLengthMenu:[20, 40, 60, 80, 100]");
    }
    
    static {
        C_PROGRESSBAR = ".ui-progressbar.ui-widget.ui-widget-content.ui-corner-all".replace('.', ' ').trim();
        C_PROGRESSBAR_VALUE = ".ui-progressbar-value.ui-widget-header.ui-corner-left".replace('.', ' ').trim();
        C_TH = ".ui-state-default".replace('.', ' ').trim();
    }
}
