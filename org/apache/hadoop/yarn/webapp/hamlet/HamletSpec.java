// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp.hamlet;

import org.apache.hadoop.yarn.webapp.SubView;
import java.util.EnumSet;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public class HamletSpec
{
    public enum Shape
    {
        rect, 
        circle, 
        poly, 
        Default;
    }
    
    public enum Dir
    {
        ltr, 
        rtl;
    }
    
    public enum Media
    {
        screen, 
        tty, 
        tv, 
        projection, 
        handheld, 
        print, 
        braille, 
        aural, 
        all;
    }
    
    public enum LinkType
    {
        alternate, 
        stylesheet, 
        start, 
        next, 
        prev, 
        contents, 
        index, 
        glossary, 
        copyright, 
        chapter, 
        section, 
        subsection, 
        appendix, 
        help, 
        bookmark;
    }
    
    public enum Method
    {
        get, 
        post;
    }
    
    public enum InputType
    {
        text, 
        password, 
        checkbox, 
        radio, 
        submit, 
        reset, 
        file, 
        hidden, 
        image, 
        button;
    }
    
    public enum ButtonType
    {
        button, 
        submit, 
        reset;
    }
    
    public enum Scope
    {
        row, 
        col, 
        rowgroup, 
        colgroup;
    }
    
    public interface HTML extends I18nAttrs, _Html
    {
    }
    
    public interface I18nAttrs
    {
        I18nAttrs $lang(final String p0);
        
        I18nAttrs $dir(final Dir p0);
    }
    
    public interface _Html extends _Head, _Body, _
    {
        HEAD head();
        
        BODY body();
        
        BODY body(final String p0);
    }
    
    public interface HEAD extends I18nAttrs, _Head, _Child
    {
    }
    
    public interface _Head extends HeadMisc
    {
        TITLE title();
        
        _Head title(final String p0);
        
        BASE base();
        
        _Head base(final String p0);
    }
    
    public interface TITLE extends I18nAttrs, PCData, _Child
    {
    }
    
    public interface PCData extends _Content, _RawContent
    {
    }
    
    public interface _Content extends _Child
    {
        _Content _(final Object... p0);
    }
    
    public interface _Child extends _
    {
        _ _();
    }
    
    public interface _
    {
    }
    
    public interface _RawContent extends _Child
    {
        _RawContent _r(final Object... p0);
    }
    
    @Element(endTag = false)
    public interface BASE extends _Child
    {
        BASE $href(final String p0);
    }
    
    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Element {
        boolean startTag() default true;
        
        boolean endTag() default true;
    }
    
    public interface HeadMisc extends _Script, _Object
    {
        STYLE style();
        
        HeadMisc style(final Object... p0);
        
        META meta();
        
        HeadMisc meta(final String p0, final String p1);
        
        HeadMisc meta_http(final String p0, final String p1);
        
        LINK link();
        
        HeadMisc link(final String p0);
    }
    
    public interface STYLE extends I18nAttrs, _Content, _Child
    {
        STYLE $type(final String p0);
        
        STYLE $media(final EnumSet<Media> p0);
        
        STYLE $title(final String p0);
    }
    
    @Element(endTag = false)
    public interface META extends I18nAttrs, _Child
    {
        META $http_equiv(final String p0);
        
        META $name(final String p0);
        
        META $content(final String p0);
    }
    
    @Element(endTag = false)
    public interface LINK extends Attrs, _Child
    {
        LINK $href(final String p0);
        
        LINK $hreflang(final String p0);
        
        LINK $type(final String p0);
        
        LINK $rel(final EnumSet<LinkType> p0);
        
        LINK $rel(final String p0);
        
        LINK $media(final EnumSet<Media> p0);
        
        LINK $media(final String p0);
    }
    
    public interface Attrs extends CoreAttrs, I18nAttrs, EventsAttrs
    {
    }
    
    public interface CoreAttrs
    {
        CoreAttrs $id(final String p0);
        
        CoreAttrs $class(final String p0);
        
        CoreAttrs $style(final String p0);
        
        CoreAttrs $title(final String p0);
    }
    
    public interface EventsAttrs
    {
        EventsAttrs $onclick(final String p0);
        
        EventsAttrs $ondblclick(final String p0);
        
        EventsAttrs $onmousedown(final String p0);
        
        EventsAttrs $onmouseup(final String p0);
        
        EventsAttrs $onmouseover(final String p0);
        
        EventsAttrs $onmousemove(final String p0);
        
        EventsAttrs $onmouseout(final String p0);
        
        EventsAttrs $onkeypress(final String p0);
        
        EventsAttrs $onkeydown(final String p0);
        
        EventsAttrs $onkeyup(final String p0);
    }
    
    public interface _Script
    {
        SCRIPT script();
        
        _Script script(final String p0);
    }
    
    public interface SCRIPT extends _Content, _Child
    {
        SCRIPT $charset(final String p0);
        
        SCRIPT $type(final String p0);
        
        SCRIPT $src(final String p0);
        
        SCRIPT $defer(final String p0);
    }
    
    public interface _Object
    {
        OBJECT object();
        
        OBJECT object(final String p0);
    }
    
    public interface OBJECT extends Attrs, _Param, Flow, _Child
    {
        OBJECT $data(final String p0);
        
        OBJECT $type(final String p0);
        
        OBJECT $height(final int p0);
        
        OBJECT $height(final String p0);
        
        OBJECT $width(final int p0);
        
        OBJECT $width(final String p0);
        
        OBJECT $usemap(final String p0);
        
        OBJECT $name(final String p0);
        
        OBJECT $tabindex(final int p0);
    }
    
    public interface _Param extends _Child
    {
        PARAM param();
        
        _Param param(final String p0, final String p1);
    }
    
    @Element(endTag = false)
    public interface PARAM
    {
        PARAM $id(final String p0);
        
        PARAM $name(final String p0);
        
        PARAM $value(final String p0);
    }
    
    public interface Flow extends Block, Inline
    {
    }
    
    public interface Block extends _Block, _Form, _FieldSet
    {
    }
    
    public interface _Block extends Heading, Listing, Preformatted
    {
        P p();
        
        P p(final String p0);
        
        DL dl();
        
        DL dl(final String p0);
        
        DIV div();
        
        DIV div(final String p0);
        
        BLOCKQUOTE blockquote();
        
        BLOCKQUOTE bq();
        
        HR hr();
        
        _Block hr(final String p0);
        
        TABLE table();
        
        TABLE table(final String p0);
        
        ADDRESS address();
        
        _Block address(final String p0);
        
        _Block _(final Class<? extends SubView> p0);
    }
    
    @Element(endTag = false)
    public interface P extends Attrs, Inline, _Child
    {
    }
    
    public interface Inline extends PCData, FontStyle, Phrase, Special, FormCtrl
    {
    }
    
    public interface FontStyle extends _FontStyle, _FontSize
    {
    }
    
    public interface _FontStyle extends _Child
    {
        I i();
        
        _FontStyle i(final String p0);
        
        _FontStyle i(final String p0, final String p1);
        
        B b();
        
        _FontStyle b(final String p0);
        
        _FontStyle b(final String p0, final String p1);
    }
    
    public interface I extends Attrs, Inline, _Child
    {
    }
    
    public interface B extends Attrs, Inline, _Child
    {
    }
    
    public interface _FontSize extends _Child
    {
        SMALL small();
        
        _FontSize small(final String p0);
        
        _FontSize small(final String p0, final String p1);
    }
    
    public interface SMALL extends Attrs, Inline, _Child
    {
    }
    
    public interface Special extends _Anchor, _ImgObject, _SubSup, _Special
    {
    }
    
    public interface _Anchor
    {
        A a();
        
        A a(final String p0);
        
        _Anchor a(final String p0, final String p1);
        
        _Anchor a(final String p0, final String p1, final String p2);
    }
    
    public interface A extends Attrs, _Child, PCData, FontStyle, Phrase, _ImgObject, _Special, _SubSup, FormCtrl
    {
        A $type(final String p0);
        
        A $href(final String p0);
        
        A $hreflang(final String p0);
        
        A $rel(final EnumSet<LinkType> p0);
        
        A $rel(final String p0);
        
        A $accesskey(final String p0);
        
        A $tabindex(final int p0);
        
        A $onfocus(final String p0);
        
        A $onblur(final String p0);
    }
    
    public interface _ImgObject extends _Object, _Child
    {
        IMG img();
        
        _ImgObject img(final String p0);
    }
    
    @Element(endTag = false)
    public interface IMG extends Attrs, _Child
    {
        IMG $src(final String p0);
        
        IMG $alt(final String p0);
        
        IMG $height(final int p0);
        
        IMG $height(final String p0);
        
        IMG $width(final int p0);
        
        IMG $width(final String p0);
        
        IMG $usemap(final String p0);
        
        IMG $ismap();
    }
    
    public interface _Special extends _Script, _InsDel
    {
        BR br();
        
        _Special br(final String p0);
        
        MAP map();
        
        MAP map(final String p0);
        
        Q q();
        
        _Special q(final String p0);
        
        _Special q(final String p0, final String p1);
        
        SPAN span();
        
        _Special span(final String p0);
        
        _Special span(final String p0, final String p1);
        
        BDO bdo();
        
        _Special bdo(final Dir p0, final String p1);
    }
    
    @Element(endTag = false)
    public interface BR extends CoreAttrs, _Child
    {
    }
    
    public interface MAP extends Attrs, Block, _Child
    {
        AREA area();
        
        AREA area(final String p0);
        
        MAP $name(final String p0);
    }
    
    @Element(endTag = false)
    public interface AREA extends Attrs, _Child
    {
        AREA $shape(final Shape p0);
        
        AREA $coords(final String p0);
        
        AREA $href(final String p0);
        
        AREA $alt(final String p0);
        
        AREA $tabindex(final int p0);
        
        AREA $accesskey(final String p0);
        
        AREA $onfocus(final String p0);
        
        AREA $onblur(final String p0);
    }
    
    public interface Q extends Attrs, Inline, _Child
    {
        Q $cite(final String p0);
    }
    
    public interface SPAN extends Attrs, Inline, _Child
    {
    }
    
    public interface BDO extends CoreAttrs, I18nAttrs, Inline, _Child
    {
    }
    
    public interface _InsDel
    {
        INS ins();
        
        _InsDel ins(final String p0);
        
        DEL del();
        
        _InsDel del(final String p0);
    }
    
    public interface INS extends Attrs, Flow, _Child
    {
        INS $cite(final String p0);
        
        INS $datetime(final String p0);
    }
    
    public interface DEL extends Attrs, Flow, _Child
    {
        DEL $cite(final String p0);
        
        DEL $datetime(final String p0);
    }
    
    public interface _SubSup extends _Child
    {
        SUB sub();
        
        _SubSup sub(final String p0);
        
        _SubSup sub(final String p0, final String p1);
        
        SUP sup();
        
        _SubSup sup(final String p0);
        
        _SubSup sup(final String p0, final String p1);
    }
    
    public interface SUB extends Attrs, Inline, _Child
    {
    }
    
    public interface SUP extends Attrs, Inline, _Child
    {
    }
    
    public interface FormCtrl extends _Label, _FormCtrl
    {
    }
    
    public interface _Label extends _Child
    {
        LABEL label();
        
        _Label label(final String p0, final String p1);
    }
    
    public interface LABEL extends Attrs, _Child, PCData, FontStyle, Phrase, Special, _FormCtrl
    {
        LABEL $for(final String p0);
        
        LABEL $accesskey(final String p0);
        
        LABEL $onfocus(final String p0);
        
        LABEL $onblur(final String p0);
    }
    
    public interface _FormCtrl
    {
        INPUT input();
        
        INPUT input(final String p0);
        
        SELECT select();
        
        SELECT select(final String p0);
        
        TEXTAREA textarea();
        
        TEXTAREA textarea(final String p0);
        
        _FormCtrl textarea(final String p0, final String p1);
        
        BUTTON button();
        
        BUTTON button(final String p0);
        
        _FormCtrl button(final String p0, final String p1);
    }
    
    @Element(endTag = false)
    public interface INPUT extends Attrs, _Child
    {
        INPUT $type(final InputType p0);
        
        INPUT $name(final String p0);
        
        INPUT $value(final String p0);
        
        INPUT $checked();
        
        INPUT $disabled();
        
        INPUT $readonly();
        
        INPUT $size(final String p0);
        
        INPUT $maxlength(final int p0);
        
        INPUT $src(final String p0);
        
        INPUT $alt(final String p0);
        
        INPUT $ismap();
        
        INPUT $tabindex(final int p0);
        
        INPUT $accesskey(final String p0);
        
        INPUT $onfocus(final String p0);
        
        INPUT $onblur(final String p0);
        
        INPUT $onselect(final String p0);
        
        INPUT $onchange(final String p0);
        
        INPUT $accept(final String p0);
    }
    
    public interface SELECT extends Attrs, _Option, _Child
    {
        OPTGROUP optgroup();
        
        SELECT $name(final String p0);
        
        SELECT $size(final int p0);
        
        SELECT $multiple();
        
        SELECT $disabled();
        
        SELECT $tabindex(final int p0);
        
        SELECT $onfocus(final String p0);
        
        SELECT $onblur(final String p0);
        
        SELECT $onchange(final String p0);
    }
    
    public interface OPTGROUP extends Attrs, _Option, _Child
    {
        OPTGROUP $disabled();
        
        OPTGROUP $label(final String p0);
    }
    
    public interface _Option extends _Child
    {
        OPTION option();
        
        _Option option(final String p0);
    }
    
    @Element(endTag = false)
    public interface OPTION extends Attrs, PCData, _Child
    {
        OPTION $selected();
        
        OPTION $disabled();
        
        OPTION $label(final String p0);
        
        OPTION $value(final String p0);
    }
    
    public interface TEXTAREA extends Attrs, PCData, _Child
    {
        TEXTAREA $name(final String p0);
        
        TEXTAREA $rows(final int p0);
        
        TEXTAREA $cols(final int p0);
        
        TEXTAREA $disabled();
        
        TEXTAREA $readonly();
        
        TEXTAREA $tabindex(final int p0);
        
        TEXTAREA $accesskey(final String p0);
        
        TEXTAREA $onfocus(final String p0);
        
        TEXTAREA $onblur(final String p0);
        
        TEXTAREA $onselect(final String p0);
        
        TEXTAREA $onchange(final String p0);
    }
    
    public interface BUTTON extends _Block, PCData, FontStyle, Phrase, _Special, _ImgObject, _SubSup, Attrs
    {
        BUTTON $name(final String p0);
        
        BUTTON $value(final String p0);
        
        BUTTON $type(final ButtonType p0);
        
        BUTTON $disabled();
        
        BUTTON $tabindex(final int p0);
        
        BUTTON $accesskey(final String p0);
        
        BUTTON $onfocus(final String p0);
        
        BUTTON $onblur(final String p0);
    }
    
    public interface DL extends Attrs, _Dl, _Child
    {
    }
    
    public interface _Dl extends _Child
    {
        DT dt();
        
        _Dl dt(final String p0);
        
        DD dd();
        
        _Dl dd(final String p0);
    }
    
    @Element(endTag = false)
    public interface DT extends Attrs, Inline, _Child
    {
    }
    
    @Element(endTag = false)
    public interface DD extends Attrs, Flow, _Child
    {
    }
    
    public interface DIV extends Attrs, Flow, _Child
    {
    }
    
    public interface BLOCKQUOTE extends Attrs, Block, _Script, _Child
    {
        BLOCKQUOTE $cite(final String p0);
    }
    
    @Element(endTag = false)
    public interface HR extends Attrs, _Child
    {
    }
    
    public interface TABLE extends Attrs, _Table, _Child
    {
    }
    
    public interface _Table extends _TableRow, _TableCol
    {
        CAPTION caption();
        
        _Table caption(final String p0);
        
        COLGROUP colgroup();
        
        THEAD thead();
        
        THEAD thead(final String p0);
        
        TFOOT tfoot();
        
        TFOOT tfoot(final String p0);
        
        TBODY tbody();
        
        TBODY tbody(final String p0);
    }
    
    public interface CAPTION extends Attrs, Inline, _Child
    {
    }
    
    @Element(endTag = false)
    public interface COLGROUP extends Attrs, _TableCol, _Child
    {
        COLGROUP $span(final int p0);
    }
    
    public interface _TableCol extends _Child
    {
        COL col();
        
        _TableCol col(final String p0);
    }
    
    @Element(endTag = false)
    public interface COL extends Attrs, _Child
    {
        COL $span(final int p0);
    }
    
    @Element(endTag = false)
    public interface THEAD extends Attrs, _TableRow, _Child
    {
    }
    
    public interface _TableRow
    {
        TR tr();
        
        TR tr(final String p0);
    }
    
    @Element(endTag = false)
    public interface TR extends Attrs, _Tr, _Child
    {
    }
    
    public interface _Tr extends _Child
    {
        TH th();
        
        _Tr th(final String p0);
        
        _Tr th(final String p0, final String p1);
        
        TD td();
        
        _Tr td(final String p0);
        
        _Tr td(final String p0, final String p1);
    }
    
    @Element(endTag = false)
    public interface TH extends _Cell
    {
    }
    
    public interface _Cell extends Attrs, Flow, _Child
    {
        _Cell $headers(final String p0);
        
        _Cell $scope(final Scope p0);
        
        _Cell $rowspan(final int p0);
        
        _Cell $colspan(final int p0);
    }
    
    @Element(endTag = false)
    public interface TD extends _Cell
    {
    }
    
    @Element(endTag = false)
    public interface TFOOT extends Attrs, _TableRow, _Child
    {
    }
    
    public interface TBODY extends Attrs, _TableRow, _Child
    {
    }
    
    public interface ADDRESS extends Attrs, Inline, _Child
    {
    }
    
    public interface Heading
    {
        H1 h1();
        
        Heading h1(final String p0);
        
        Heading h1(final String p0, final String p1);
        
        H2 h2();
        
        Heading h2(final String p0);
        
        Heading h2(final String p0, final String p1);
        
        H3 h3();
        
        Heading h3(final String p0);
        
        Heading h3(final String p0, final String p1);
        
        H4 h4();
        
        Heading h4(final String p0);
        
        Heading h4(final String p0, final String p1);
        
        H5 h5();
        
        Heading h5(final String p0);
        
        Heading h5(final String p0, final String p1);
        
        H6 h6();
        
        Heading h6(final String p0);
        
        Heading h6(final String p0, final String p1);
    }
    
    public interface H1 extends Attrs, Inline, _Child
    {
    }
    
    public interface H2 extends Attrs, Inline, _Child
    {
    }
    
    public interface H3 extends Attrs, Inline, _Child
    {
    }
    
    public interface H4 extends Attrs, Inline, _Child
    {
    }
    
    public interface H5 extends Attrs, Inline, _Child
    {
    }
    
    public interface H6 extends Attrs, Inline, _Child
    {
    }
    
    public interface Listing
    {
        UL ul();
        
        UL ul(final String p0);
        
        OL ol();
        
        OL ol(final String p0);
    }
    
    public interface UL extends Attrs, _Li, _Child
    {
    }
    
    public interface _Li extends _Child
    {
        LI li();
        
        _Li li(final String p0);
    }
    
    @Element(endTag = false)
    public interface LI extends Attrs, Flow, _Child
    {
    }
    
    public interface OL extends Attrs, _Li, _Child
    {
    }
    
    public interface Preformatted
    {
        PRE pre();
        
        PRE pre(final String p0);
    }
    
    public interface PRE extends Attrs, _Child, PCData, _FontStyle, Phrase, _Anchor, _Special, FormCtrl
    {
    }
    
    public interface _Form
    {
        FORM form();
        
        FORM form(final String p0);
    }
    
    public interface FORM extends Attrs, _Child, _Script, _Block, _FieldSet
    {
        FORM $action(final String p0);
        
        FORM $method(final Method p0);
        
        FORM $enctype(final String p0);
        
        FORM $accept(final String p0);
        
        FORM $name(final String p0);
        
        FORM $onsubmit(final String p0);
        
        FORM $onreset(final String p0);
        
        FORM $accept_charset(final String p0);
    }
    
    public interface _FieldSet
    {
        FIELDSET fieldset();
        
        FIELDSET fieldset(final String p0);
    }
    
    public interface FIELDSET extends Attrs, _Legend, PCData, Flow, _Child
    {
    }
    
    public interface _Legend extends _Child
    {
        LEGEND legend();
        
        _Legend legend(final String p0);
    }
    
    public interface LEGEND extends Attrs, Inline, _Child
    {
        LEGEND $accesskey(final String p0);
    }
    
    public interface BODY extends Attrs, _Body, _Child
    {
        BODY $onload(final String p0);
        
        BODY $onunload(final String p0);
    }
    
    public interface _Body extends Block, _Script, _InsDel
    {
    }
    
    public interface ACRONYM extends Attrs, Inline, _Child
    {
    }
    
    public interface ABBR extends Attrs, Inline, _Child
    {
    }
    
    public interface CITE extends Attrs, Inline, _Child
    {
    }
    
    public interface VAR extends Attrs, Inline, _Child
    {
    }
    
    public interface KBD extends Attrs, Inline, _Child
    {
    }
    
    public interface SAMP extends Attrs, Inline, _Child
    {
    }
    
    public interface CODE extends Attrs, Inline, _Child
    {
    }
    
    public interface DFN extends Attrs, Inline, _Child
    {
    }
    
    public interface STRONG extends Attrs, Inline, _Child
    {
    }
    
    public interface EM extends Attrs, Inline, _Child
    {
    }
    
    public interface Phrase extends _Child
    {
        EM em();
        
        Phrase em(final String p0);
        
        Phrase em(final String p0, final String p1);
        
        STRONG strong();
        
        Phrase strong(final String p0);
        
        Phrase strong(final String p0, final String p1);
        
        DFN dfn();
        
        Phrase dfn(final String p0);
        
        Phrase dfn(final String p0, final String p1);
        
        CODE code();
        
        Phrase code(final String p0);
        
        Phrase code(final String p0, final String p1);
        
        SAMP samp();
        
        Phrase samp(final String p0);
        
        Phrase samp(final String p0, final String p1);
        
        KBD kbd();
        
        Phrase kbd(final String p0);
        
        Phrase kbd(final String p0, final String p1);
        
        VAR var();
        
        Phrase var(final String p0);
        
        Phrase var(final String p0, final String p1);
        
        CITE cite();
        
        Phrase cite(final String p0);
        
        Phrase cite(final String p0, final String p1);
        
        ABBR abbr();
        
        Phrase abbr(final String p0);
        
        Phrase abbr(final String p0, final String p1);
    }
}
