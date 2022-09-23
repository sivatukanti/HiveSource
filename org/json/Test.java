// 
// Decompiled by Procyon v0.5.36
// 

package org.json;

import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.io.Writer;
import java.io.StringWriter;

public class Test
{
    public static void main(final String[] array) {
        class Obj implements JSONString
        {
            public String aString = "A beany object";
            public double aNumber = 42.0;
            public boolean aBoolean = true;
            
            public Obj(final String aString, final double aNumber, final boolean aBoolean) {
            }
            
            public double getNumber() {
                return this.aNumber;
            }
            
            public String getString() {
                return this.aString;
            }
            
            public boolean isBoolean() {
                return this.aBoolean;
            }
            
            public String getBENT() {
                return "All uppercase key";
            }
            
            public String getX() {
                return "x";
            }
            
            @Override
            public String toJSONString() {
                return "{" + JSONObject.quote(this.aString) + ":" + JSONObject.doubleToString(this.aNumber) + "}";
            }
            
            @Override
            public String toString() {
                return this.getString() + " " + this.getNumber() + " " + this.isBoolean() + "." + this.getBENT() + " " + this.getX();
            }
        }
        final Obj obj = new Obj(42.0);
        try {
            System.out.println(XML.toJSONObject("<![CDATA[This is a collection of test patterns and examples for org.json.]]>  Ignore the stuff past the end.  ").toString());
            final JSONObject jsonObject = new JSONObject("{     \"list of lists\" : [         [1, 2, 3],         [4, 5, 6],     ] }");
            System.out.println(jsonObject.toString(4));
            System.out.println(XML.toString(jsonObject));
            final String s = "<recipe name=\"bread\" prep_time=\"5 mins\" cook_time=\"3 hours\"> <title>Basic bread</title> <ingredient amount=\"8\" unit=\"dL\">Flour</ingredient> <ingredient amount=\"10\" unit=\"grams\">Yeast</ingredient> <ingredient amount=\"4\" unit=\"dL\" state=\"warm\">Water</ingredient> <ingredient amount=\"1\" unit=\"teaspoon\">Salt</ingredient> <instructions> <step>Mix all ingredients together.</step> <step>Knead thoroughly.</step> <step>Cover with a cloth, and leave for one hour in warm room.</step> <step>Knead again.</step> <step>Place in a bread baking tin.</step> <step>Cover with a cloth, and leave for one hour in warm room.</step> <step>Bake in the oven at 180(degrees)C for 30 minutes.</step> </instructions> </recipe> ";
            System.out.println(XML.toJSONObject(s).toString(4));
            System.out.println();
            final JSONObject jsonObject2 = JSONML.toJSONObject(s);
            System.out.println(jsonObject2.toString());
            System.out.println(JSONML.toString(jsonObject2));
            System.out.println();
            final JSONArray jsonArray = JSONML.toJSONArray(s);
            System.out.println(jsonArray.toString(4));
            System.out.println(JSONML.toString(jsonArray));
            System.out.println();
            final String s2 = "<div id=\"demo\" class=\"JSONML\"><p>JSONML is a transformation between <b>JSON</b> and <b>XML</b> that preserves ordering of document features.</p><p>JSONML can work with JSON arrays or JSON objects.</p><p>Three<br/>little<br/>words</p></div>";
            final JSONObject jsonObject3 = JSONML.toJSONObject(s2);
            System.out.println(jsonObject3.toString(4));
            System.out.println(JSONML.toString(jsonObject3));
            System.out.println();
            final JSONArray jsonArray2 = JSONML.toJSONArray(s2);
            System.out.println(jsonArray2.toString(4));
            System.out.println(JSONML.toString(jsonArray2));
            System.out.println();
            System.out.println(XML.toJSONObject("<person created=\"2006-11-11T19:23\" modified=\"2006-12-31T23:59\">\n <firstName>Robert</firstName>\n <lastName>Smith</lastName>\n <address type=\"home\">\n <street>12345 Sixth Ave</street>\n <city>Anytown</city>\n <state>CA</state>\n <postalCode>98765-4321</postalCode>\n </address>\n </person>").toString(4));
            System.out.println(new JSONObject(obj).toString());
            System.out.println(new JSONObject("{ \"entity\": { \"imageURL\": \"\", \"name\": \"IXXXXXXXXXXXXX\", \"id\": 12336, \"ratingCount\": null, \"averageRating\": null } }").toString(2));
            System.out.println(new JSONStringer().object().key("single").value("MARIE HAA'S").key("Johnny").value("MARIE HAA\\'S").key("foo").value("bar").key("baz").array().object().key("quux").value("Thanks, Josh!").endObject().endArray().key("obj keys").value(JSONObject.getNames(obj)).endObject().toString());
            System.out.println(new JSONStringer().object().key("a").array().array().array().value("b").endArray().endArray().endArray().endObject().toString());
            final JSONStringer jsonStringer = new JSONStringer();
            jsonStringer.array();
            jsonStringer.value(1L);
            jsonStringer.array();
            jsonStringer.value(null);
            jsonStringer.array();
            jsonStringer.object();
            jsonStringer.key("empty-array").array().endArray();
            jsonStringer.key("answer").value(42L);
            jsonStringer.key("null").value(null);
            jsonStringer.key("false").value(false);
            jsonStringer.key("true").value(true);
            jsonStringer.key("big").value(1.23456789E96);
            jsonStringer.key("small").value(1.23456789E-80);
            jsonStringer.key("empty-object").object().endObject();
            jsonStringer.key("long");
            jsonStringer.value(Long.MAX_VALUE);
            jsonStringer.endObject();
            jsonStringer.value("two");
            jsonStringer.endArray();
            jsonStringer.value(true);
            jsonStringer.endArray();
            jsonStringer.value(98.6);
            jsonStringer.value(-100.0);
            jsonStringer.object();
            jsonStringer.endObject();
            jsonStringer.object();
            jsonStringer.key("one");
            jsonStringer.value(1.0);
            jsonStringer.endObject();
            jsonStringer.value(obj);
            jsonStringer.endArray();
            System.out.println(jsonStringer.toString());
            System.out.println(new JSONArray(jsonStringer.toString()).toString(4));
            System.out.println(new JSONArray(new int[] { 1, 2, 3 }).toString());
            final JSONObject jsonObject4 = new JSONObject(obj, new String[] { "aString", "aNumber", "aBoolean" });
            jsonObject4.put("Testing JSONString interface", obj);
            System.out.println(jsonObject4.toString(4));
            final JSONObject jsonObject5 = new JSONObject("{slashes: '///', closetag: '</script>', backslash:'\\\\', ei: {quotes: '\"\\''},eo: {a: '\"quoted\"', b:\"don't\"}, quotes: [\"'\", '\"']}");
            System.out.println(jsonObject5.toString(2));
            System.out.println(XML.toString(jsonObject5));
            System.out.println("");
            final JSONObject jsonObject6 = new JSONObject("{foo: [true, false,9876543210,    0.0, 1.00000001,  1.000000000001, 1.00000000000000001, .00000000000000001, 2.00, 0.1, 2e100, -32,[],{}, \"string\"],   to   : null, op : 'Good',ten:10} postfix comment");
            jsonObject6.put("String", "98.6");
            jsonObject6.put("JSONObject", new JSONObject());
            jsonObject6.put("JSONArray", new JSONArray());
            jsonObject6.put("int", 57);
            jsonObject6.put("double", 1.2345678901234568E29);
            jsonObject6.put("true", true);
            jsonObject6.put("false", false);
            jsonObject6.put("null", JSONObject.NULL);
            jsonObject6.put("bool", "true");
            jsonObject6.put("zero", -0.0);
            jsonObject6.put("\\u2028", "\u2028");
            jsonObject6.put("\\u2029", "\u2029");
            final JSONArray jsonArray3 = jsonObject6.getJSONArray("foo");
            jsonArray3.put(666);
            jsonArray3.put(2001.99);
            jsonArray3.put("so \"fine\".");
            jsonArray3.put("so <fine>.");
            jsonArray3.put(true);
            jsonArray3.put(false);
            jsonArray3.put(new JSONArray());
            jsonArray3.put(new JSONObject());
            jsonObject6.put("keys", JSONObject.getNames(jsonObject6));
            System.out.println(jsonObject6.toString(4));
            System.out.println(XML.toString(jsonObject6));
            System.out.println("String: " + jsonObject6.getDouble("String"));
            System.out.println("  bool: " + jsonObject6.getBoolean("bool"));
            System.out.println("    to: " + jsonObject6.getString("to"));
            System.out.println("  true: " + jsonObject6.getString("true"));
            System.out.println("   foo: " + jsonObject6.getJSONArray("foo"));
            System.out.println("    op: " + jsonObject6.getString("op"));
            System.out.println("   ten: " + jsonObject6.getInt("ten"));
            System.out.println("  oops: " + jsonObject6.optBoolean("oops"));
            final String s3 = "<xml one = 1 two=' \"2\" '><five></five>First \t&lt;content&gt;<five></five> This is \"content\". <three>  3  </three>JSON does not preserve the sequencing of elements and contents.<three>  III  </three>  <three>  T H R E E</three><four/>Content text is an implied structure in XML. <six content=\"6\"/>JSON does not have implied structure:<seven>7</seven>everything is explicit.<![CDATA[CDATA blocks<are><supported>!]]></xml>";
            final JSONObject jsonObject7 = XML.toJSONObject(s3);
            System.out.println(jsonObject7.toString(2));
            System.out.println(XML.toString(jsonObject7));
            System.out.println("");
            final JSONArray jsonArray4 = JSONML.toJSONArray(s3);
            System.out.println(jsonArray4.toString(4));
            System.out.println(JSONML.toString(jsonArray4));
            System.out.println("");
            final JSONArray jsonArray5 = JSONML.toJSONArray("<xml do='0'>uno<a re='1' mi='2'>dos<b fa='3'/>tres<c>true</c>quatro</a>cinqo<d>seis<e/></d></xml>");
            System.out.println(jsonArray5.toString(4));
            System.out.println(JSONML.toString(jsonArray5));
            System.out.println("");
            final String s4 = "<mapping><empty/>   <class name = \"Customer\">      <field name = \"ID\" type = \"string\">         <bind-xml name=\"ID\" node=\"attribute\"/>      </field>      <field name = \"FirstName\" type = \"FirstName\"/>      <field name = \"MI\" type = \"MI\"/>      <field name = \"LastName\" type = \"LastName\"/>   </class>   <class name = \"FirstName\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class>   <class name = \"MI\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class>   <class name = \"LastName\">      <field name = \"text\">         <bind-xml name = \"text\" node = \"text\"/>      </field>   </class></mapping>";
            final JSONObject jsonObject8 = XML.toJSONObject(s4);
            System.out.println(jsonObject8.toString(2));
            System.out.println(XML.toString(jsonObject8));
            System.out.println("");
            final JSONArray jsonArray6 = JSONML.toJSONArray(s4);
            System.out.println(jsonArray6.toString(4));
            System.out.println(JSONML.toString(jsonArray6));
            System.out.println("");
            final JSONObject jsonObject9 = XML.toJSONObject("<?xml version=\"1.0\" ?><Book Author=\"Anonymous\"><Title>Sample Book</Title><Chapter id=\"1\">This is chapter 1. It is not very long or interesting.</Chapter><Chapter id=\"2\">This is chapter 2. Although it is longer than chapter 1, it is not any more interesting.</Chapter></Book>");
            System.out.println(jsonObject9.toString(2));
            System.out.println(XML.toString(jsonObject9));
            System.out.println("");
            final JSONObject jsonObject10 = XML.toJSONObject("<!DOCTYPE bCard 'http://www.cs.caltech.edu/~adam/schemas/bCard'><bCard><?xml default bCard        firstname = ''        lastname  = '' company   = '' email = '' homepage  = ''?><bCard        firstname = 'Rohit'        lastname  = 'Khare'        company   = 'MCI'        email     = 'khare@mci.net'        homepage  = 'http://pest.w3.org/'/><bCard        firstname = 'Adam'        lastname  = 'Rifkin'        company   = 'Caltech Infospheres Project'        email     = 'adam@cs.caltech.edu'        homepage  = 'http://www.cs.caltech.edu/~adam/'/></bCard>");
            System.out.println(jsonObject10.toString(2));
            System.out.println(XML.toString(jsonObject10));
            System.out.println("");
            final JSONObject jsonObject11 = XML.toJSONObject("<?xml version=\"1.0\"?><customer>    <firstName>        <text>Fred</text>    </firstName>    <ID>fbs0001</ID>    <lastName> <text>Scerbo</text>    </lastName>    <MI>        <text>B</text>    </MI></customer>");
            System.out.println(jsonObject11.toString(2));
            System.out.println(XML.toString(jsonObject11));
            System.out.println("");
            final JSONObject jsonObject12 = XML.toJSONObject("<!ENTITY tp-address PUBLIC '-//ABC University::Special Collections Library//TEXT (titlepage: name and address)//EN' 'tpspcoll.sgm'><list type='simple'><head>Repository Address </head><item>Special Collections Library</item><item>ABC University</item><item>Main Library, 40 Circle Drive</item><item>Ourtown, Pennsylvania</item><item>17654 USA</item></list>");
            System.out.println(jsonObject12.toString());
            System.out.println(XML.toString(jsonObject12));
            System.out.println("");
            final JSONObject jsonObject13 = XML.toJSONObject("<test intertag status=ok><empty/>deluxe<blip sweet=true>&amp;&quot;toot&quot;&toot;&#x41;</blip><x>eks</x><w>bonus</w><w>bonus2</w></test>");
            System.out.println(jsonObject13.toString(2));
            System.out.println(XML.toString(jsonObject13));
            System.out.println("");
            final JSONObject jsonObject14 = HTTP.toJSONObject("GET / HTTP/1.0\nAccept: image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, */*\nAccept-Language: en-us\nUser-Agent: Mozilla/4.0 (compatible; MSIE 5.5; Windows 98; Win 9x 4.90; T312461; Q312461)\nHost: www.nokko.com\nConnection: keep-alive\nAccept-encoding: gzip, deflate\n");
            System.out.println(jsonObject14.toString(2));
            System.out.println(HTTP.toString(jsonObject14));
            System.out.println("");
            final JSONObject jsonObject15 = HTTP.toJSONObject("HTTP/1.1 200 Oki Doki\nDate: Sun, 26 May 2002 17:38:52 GMT\nServer: Apache/1.3.23 (Unix) mod_perl/1.26\nKeep-Alive: timeout=15, max=100\nConnection: Keep-Alive\nTransfer-Encoding: chunked\nContent-Type: text/html\n");
            System.out.println(jsonObject15.toString(2));
            System.out.println(HTTP.toString(jsonObject15));
            System.out.println("");
            final JSONObject jsonObject16 = new JSONObject("{nix: null, nux: false, null: 'null', 'Request-URI': '/', Method: 'GET', 'HTTP-Version': 'HTTP/1.0'}");
            System.out.println(jsonObject16.toString(2));
            System.out.println("isNull: " + jsonObject16.isNull("nix"));
            System.out.println("   has: " + jsonObject16.has("nix"));
            System.out.println(XML.toString(jsonObject16));
            System.out.println(HTTP.toString(jsonObject16));
            System.out.println("");
            final JSONObject jsonObject17 = XML.toJSONObject("<?xml version='1.0' encoding='UTF-8'?>\n\n<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\"><SOAP-ENV:Body><ns1:doGoogleSearch xmlns:ns1=\"urn:GoogleSearch\" SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><key xsi:type=\"xsd:string\">GOOGLEKEY</key> <q xsi:type=\"xsd:string\">'+search+'</q> <start xsi:type=\"xsd:int\">0</start> <maxResults xsi:type=\"xsd:int\">10</maxResults> <filter xsi:type=\"xsd:boolean\">true</filter> <restrict xsi:type=\"xsd:string\"></restrict> <safeSearch xsi:type=\"xsd:boolean\">false</safeSearch> <lr xsi:type=\"xsd:string\"></lr> <ie xsi:type=\"xsd:string\">latin1</ie> <oe xsi:type=\"xsd:string\">latin1</oe></ns1:doGoogleSearch></SOAP-ENV:Body></SOAP-ENV:Envelope>");
            System.out.println(jsonObject17.toString(2));
            System.out.println(XML.toString(jsonObject17));
            System.out.println("");
            final JSONObject jsonObject18 = new JSONObject("{Envelope: {Body: {\"ns1:doGoogleSearch\": {oe: \"latin1\", filter: true, q: \"'+search+'\", key: \"GOOGLEKEY\", maxResults: 10, \"SOAP-ENV:encodingStyle\": \"http://schemas.xmlsoap.org/soap/encoding/\", start: 0, ie: \"latin1\", safeSearch:false, \"xmlns:ns1\": \"urn:GoogleSearch\"}}}}");
            System.out.println(jsonObject18.toString(2));
            System.out.println(XML.toString(jsonObject18));
            System.out.println("");
            final JSONObject jsonObject19 = CookieList.toJSONObject("  f%oo = b+l=ah  ; o;n%40e = t.wo ");
            System.out.println(jsonObject19.toString(2));
            System.out.println(CookieList.toString(jsonObject19));
            System.out.println("");
            final JSONObject jsonObject20 = Cookie.toJSONObject("f%oo=blah; secure ;expires = April 24, 2002");
            System.out.println(jsonObject20.toString(2));
            System.out.println(Cookie.toString(jsonObject20));
            System.out.println("");
            System.out.println(new JSONObject("{script: 'It is not allowed in HTML to send a close script tag in a string<script>because it confuses browsers</script>so we insert a backslash before the /'}").toString());
            System.out.println("");
            final JSONTokener jsonTokener = new JSONTokener("{op:'test', to:'session', pre:1}{op:'test', to:'session', pre:2}");
            final JSONObject jsonObject21 = new JSONObject(jsonTokener);
            System.out.println(jsonObject21.toString());
            System.out.println("pre: " + jsonObject21.optInt("pre"));
            System.out.println((int)jsonTokener.skipTo('{'));
            System.out.println(new JSONObject(jsonTokener).toString());
            System.out.println("");
            final JSONArray jsonArray7 = CDL.toJSONArray("No quotes, 'Single Quotes', \"Double Quotes\"\n1,'2',\"3\"\n,'It is \"good,\"', \"It works.\"\n\n");
            System.out.println(CDL.toString(jsonArray7));
            System.out.println("");
            System.out.println(jsonArray7.toString(4));
            System.out.println("");
            final JSONArray jsonArray8 = new JSONArray(" [\"<escape>\", next is an implied null , , ok,] ");
            System.out.println(jsonArray8.toString());
            System.out.println("");
            System.out.println(XML.toString(jsonArray8));
            System.out.println("");
            final JSONObject jsonObject22 = new JSONObject("{ fun => with non-standard forms ; forgiving => This package can be used to parse formats that are similar to but not stricting conforming to JSON; why=To make it easier to migrate existing data to JSON,one = [[1.00]]; uno=[[{1=>1}]];'+':+6e66 ;pluses=+++;empty = '' , 'double':0.666,true: TRUE, false: FALSE, null=NULL;[true] = [[!,@;*]]; string=>  o. k. ; \r oct=0666; hex=0x666; dec=666; o=0999; noh=0x0x}");
            System.out.println(jsonObject22.toString(4));
            System.out.println("");
            if (jsonObject22.getBoolean("true") && !jsonObject22.getBoolean("false")) {
                System.out.println("It's all good");
            }
            System.out.println("");
            final JSONObject jsonObject23 = new JSONObject(jsonObject22, new String[] { "dec", "oct", "hex", "missing" });
            System.out.println(jsonObject23.toString(4));
            System.out.println("");
            System.out.println(new JSONStringer().array().value(jsonArray8).value(jsonObject23).endArray());
            final JSONObject jsonObject24 = new JSONObject("{string: \"98.6\", long: 2147483648, int: 2147483647, longer: 9223372036854775807, double: 9223372036854775808}");
            System.out.println(jsonObject24.toString(4));
            System.out.println("\ngetInt");
            System.out.println("int    " + jsonObject24.getInt("int"));
            System.out.println("long   " + jsonObject24.getInt("long"));
            System.out.println("longer " + jsonObject24.getInt("longer"));
            System.out.println("double " + jsonObject24.getInt("double"));
            System.out.println("string " + jsonObject24.getInt("string"));
            System.out.println("\ngetLong");
            System.out.println("int    " + jsonObject24.getLong("int"));
            System.out.println("long   " + jsonObject24.getLong("long"));
            System.out.println("longer " + jsonObject24.getLong("longer"));
            System.out.println("double " + jsonObject24.getLong("double"));
            System.out.println("string " + jsonObject24.getLong("string"));
            System.out.println("\ngetDouble");
            System.out.println("int    " + jsonObject24.getDouble("int"));
            System.out.println("long   " + jsonObject24.getDouble("long"));
            System.out.println("longer " + jsonObject24.getDouble("longer"));
            System.out.println("double " + jsonObject24.getDouble("double"));
            System.out.println("string " + jsonObject24.getDouble("string"));
            jsonObject24.put("good sized", Long.MAX_VALUE);
            System.out.println(jsonObject24.toString(4));
            System.out.println(new JSONArray("[2147483647, 2147483648, 9223372036854775807, 9223372036854775808]").toString(4));
            System.out.println("\nKeys: ");
            final Iterator keys = jsonObject24.keys();
            while (keys.hasNext()) {
                final String str = keys.next();
                System.out.println(str + ": " + jsonObject24.getString(str));
            }
            System.out.println("\naccumulate: ");
            final JSONObject jsonObject25 = new JSONObject();
            jsonObject25.accumulate("stooge", "Curly");
            jsonObject25.accumulate("stooge", "Larry");
            jsonObject25.accumulate("stooge", "Moe");
            jsonObject25.getJSONArray("stooge").put(5, "Shemp");
            System.out.println(jsonObject25.toString(4));
            System.out.println("\nwrite:");
            System.out.println(jsonObject25.write(new StringWriter()));
            final JSONObject jsonObject26 = XML.toJSONObject("<xml empty><a></a><a>1</a><a>22</a><a>333</a></xml>");
            System.out.println(jsonObject26.toString(4));
            System.out.println(XML.toString(jsonObject26));
            final String s5 = "<book><chapter>Content of the first chapter</chapter><chapter>Content of the second chapter      <chapter>Content of the first subchapter</chapter>      <chapter>Content of the second subchapter</chapter></chapter><chapter>Third Chapter</chapter></book>";
            final JSONObject jsonObject27 = XML.toJSONObject(s5);
            System.out.println(jsonObject27.toString(4));
            System.out.println(XML.toString(jsonObject27));
            final JSONArray jsonArray9 = JSONML.toJSONArray(s5);
            System.out.println(jsonArray9.toString(4));
            System.out.println(JSONML.toString(jsonArray9));
            final Collection collection = null;
            final Map map = null;
            final JSONObject jsonObject28 = new JSONObject(map);
            final JSONArray jsonArray10 = new JSONArray(collection);
            jsonObject28.append("stooge", "Joe DeRita");
            jsonObject28.append("stooge", "Shemp");
            jsonObject28.accumulate("stooges", "Curly");
            jsonObject28.accumulate("stooges", "Larry");
            jsonObject28.accumulate("stooges", "Moe");
            jsonObject28.accumulate("stoogearray", jsonObject28.get("stooges"));
            jsonObject28.put("map", map);
            jsonObject28.put("collection", collection);
            jsonObject28.put("array", jsonArray10);
            jsonArray10.put(map);
            jsonArray10.put(collection);
            System.out.println(jsonObject28.toString(4));
            System.out.println(new JSONObject("{plist=Apple; AnimalSmells = { pig = piggish; lamb = lambish; worm = wormy; }; AnimalSounds = { pig = oink; lamb = baa; worm = baa;  Lisa = \"Why is the worm talking like a lamb?\" } ; AnimalColors = { pig = pink; lamb = black; worm = pink; } } ").toString(4));
            JSONArray jsonArray11 = new JSONArray(" (\"San Francisco\", \"New York\", \"Seoul\", \"London\", \"Seattle\", \"Shanghai\")");
            System.out.println(jsonArray11.toString());
            final String s6 = "<a ichi='1' ni='2'><b>The content of b</b> and <c san='3'>The content of c</c><d>do</d><e></e><d>re</d><f/><d>mi</d></a>";
            JSONObject jsonObject29 = XML.toJSONObject(s6);
            System.out.println(jsonObject29.toString(2));
            System.out.println(XML.toString(jsonObject29));
            System.out.println("");
            final JSONArray jsonArray12 = JSONML.toJSONArray(s6);
            System.out.println(jsonArray12.toString(4));
            System.out.println(JSONML.toString(jsonArray12));
            System.out.println("");
            System.out.println("\nTesting Exceptions: ");
            System.out.print("Exception: ");
            try {
                jsonArray11 = new JSONArray();
                jsonArray11.put(Double.NEGATIVE_INFINITY);
                jsonArray11.put(Double.NaN);
                System.out.println(jsonArray11.toString());
            }
            catch (Exception x) {
                System.out.println(x);
            }
            System.out.print("Exception: ");
            try {
                System.out.println(jsonObject29.getDouble("stooge"));
            }
            catch (Exception x2) {
                System.out.println(x2);
            }
            System.out.print("Exception: ");
            try {
                System.out.println(jsonObject29.getDouble("howard"));
            }
            catch (Exception x3) {
                System.out.println(x3);
            }
            System.out.print("Exception: ");
            try {
                System.out.println(jsonObject29.put(null, "howard"));
            }
            catch (Exception x4) {
                System.out.println(x4);
            }
            System.out.print("Exception: ");
            try {
                System.out.println(jsonArray11.getDouble(0));
            }
            catch (Exception x5) {
                System.out.println(x5);
            }
            System.out.print("Exception: ");
            try {
                System.out.println(jsonArray11.get(-1));
            }
            catch (Exception x6) {
                System.out.println(x6);
            }
            System.out.print("Exception: ");
            try {
                System.out.println(jsonArray11.put(Double.NaN));
            }
            catch (Exception x7) {
                System.out.println(x7);
            }
            System.out.print("Exception: ");
            try {
                jsonObject29 = XML.toJSONObject("<a><b>    ");
            }
            catch (Exception x8) {
                System.out.println(x8);
            }
            System.out.print("Exception: ");
            try {
                jsonObject29 = XML.toJSONObject("<a></b>    ");
            }
            catch (Exception x9) {
                System.out.println(x9);
            }
            System.out.print("Exception: ");
            try {
                jsonObject29 = XML.toJSONObject("<a></a    ");
            }
            catch (Exception x10) {
                System.out.println(x10);
            }
            System.out.print("Exception: ");
            try {
                System.out.println(new JSONArray(new Object()).toString());
            }
            catch (Exception x11) {
                System.out.println(x11);
            }
            System.out.print("Exception: ");
            try {
                System.out.println(new JSONArray("[)").toString());
            }
            catch (Exception x12) {
                System.out.println(x12);
            }
            System.out.print("Exception: ");
            try {
                System.out.println(JSONML.toJSONArray("<xml").toString(4));
            }
            catch (Exception x13) {
                System.out.println(x13);
            }
            System.out.print("Exception: ");
            try {
                System.out.println(JSONML.toJSONArray("<right></wrong>").toString(4));
            }
            catch (Exception x14) {
                System.out.println(x14);
            }
            System.out.print("Exception: ");
            try {
                jsonObject29 = new JSONObject("{\"koda\": true, \"koda\": true}");
                System.out.println(jsonObject29.toString(4));
            }
            catch (Exception x15) {
                System.out.println(x15);
            }
            System.out.print("Exception: ");
            try {
                new JSONStringer().object().key("bosanda").value("MARIE HAA'S").key("bosanda").value("MARIE HAA\\'S").endObject().toString();
                System.out.println(jsonObject29.toString(4));
            }
            catch (Exception x16) {
                System.out.println(x16);
            }
        }
        catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }
}
