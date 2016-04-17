package de.timmeey.oeffiwatch.util.parser;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.timmeey.oeffiwatch.util.parser.HtmlStationParserImpl;

public class HtmlStationParserImplTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	
	@Test
	public final void testParseStationLineInfo() throws IOException {
		HtmlStationParserImpl parser = new HtmlStationParserImpl();
		ParseResult result = parser.parseStationLineInfo(TEST_HTML);
		assertEquals(24,result.getLines().length);
		assertEquals("Grüntaler Str. (Berlin)",result.getStationName());
		

	}
	
	public final static String TEST_HTML ="\n" + 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<!DOCTYPE html>\n" + 
			"<html>\n" + 
			"<head>\n" + 
			"<title>mobil.BVG.de - fahrinfo</title>\n" + 
			"<link href=\"http://www.bvg.de/themes/bvg-1-0/img/favicon.ico\" rel=\"shortcut icon\" />\n" + 
			"<meta http-equiv=\"expires\" content=\"0\" />\n" + 
			"<meta http-equiv=\"Content-Style-Type\" content=\"text/css\" />\n" + 
			" <meta name=\"keywords\" lang=\"de\" content=\"Fahrinfo, Berlin, Handy, Mobiltelefon, Fahrplan, Auskunft, Fahrplanauskunft, NV, BVG, Bus, Bahn, iPhone, Blackberry, Verkehr\" />\n" + 
			" <meta name=\"description\" lang=\"de\" content=\"Fahrinfo - Die mobile Fahrplanauskunft der BVG von Berlin fr ihr Handy. Es werden zahlreiche Mobiltelefone untersttzt; auch iPhone und Blackberry.\" />\n" + 
			" <meta name=\"DC.title\" lang=\"de\" content=\"Fahrinfo-Berlin - Ihre mobile Fahrplanauskunft der BVG fr Berlin\" />\n" + 
			" <meta name=\"DC.subject\" lang=\"de\" content=\"Fahrplanauskunft, Fahrinfo, Berlin, Handy, Mobiltelefon, Fahrplan, Auskunft, NV, BVG, Bus, Bahn, iPhone, Blackberry, Verkehr\" />\n" + 
			" <meta name=\"DC.description\" content=\"Fahrinfo - Die mobile Fahrplanauskunft der BVG von Berlin fr ihr Handy. Es werden zahlreiche Mobiltelefone untersttzt; auch iPhone und Blackberry.\" />\n" + 
			"<meta name=\"DC.format\" content=\"text/xhtml\" />\n" + 
			"<meta name=\"DC.publisher\" content=\"Berliner Verkehrsbetriebe (BVG)\" />\n" + 
			"<meta name=\"DC.type\" content=\"Service\" />\n" + 
			"<meta name=\"DC.coverage\" content=\"Berlin\" scheme=\"DCTERMS.TGN\" />\n" + 
			"<meta name=\"robots\" content=\"index, nofollow\" />\n" + 
			"<link rel=\"stylesheet\" type=\"text/css\" href=\"/Fahrinfo/css/ivuXhtml.css\" />\n" + 
			"<meta name=\"viewport\" content=\"initial-scale = 1.0, maximum-scale = 5.0, user-scalable = yes\" />\n" + 
			"</head>\n" + 
			"<body>\n" + 
			"<div class=\"main\">\n" + 
			"<div id=\"header\">\n" + 
			"<img src=\"/Fahrinfo/img//ua_xhtml/logo.gif\" alt=\"BVG\" />\n" + 
			"<a href=\"http://mobil.bvg.de/Fahrinfo/bin/detect.bin/eox?\">English</a><br />\n" + 
			"</div>\n" + 
			"<h1 class=\"ptitle\">Abfahrtsplan</h1>\n" + 
			"<div id=\"ivu_overview_input\">\n" + 
			"<strong>Gr&#252;ntaler Str. (Berlin)</strong><br />\n" + 
			"Datum: 08.04.16\n" + 
			"</div>\n" + 
			"<div class=\"ivu_result_box\">\n" + 
			"<table class=\"ivu_table\" cellspacing=\"0\" border=\"0\" cellpadding=\"0\">\n" + 
			"<!-- Kopfzeile -->\n" + 
			"<thead>\n" + 
			"<tr>\n" + 
			"<th>\n" + 
			"</th>\n" + 
			"<th >\n" + 
			"Fahrt/Linie\n" + 
			"</th>\n" + 
			"<th>\n" + 
			"Richtung\n" + 
			"</th>\n" + 
			"</tr>\n" + 
			"</thead>\n" + 
			"<!-- Tabellendatenzeilen -->\n" + 
			"<tbody>\n" + 
			"<tr class=\"ivu_table_bg1\">\n" + 
			"<td><strong>16:24</strong></td>\n" + 
			"<td>\n" + 
			"<a href=\"/Fahrinfo/bin/traininfo.bin/dox/697752/248733/971274/253114/80/ld=0.1&amp;backLink=sq&amp;input=9007160&amp;boardType=&amp;time=actual&productsFilter=1111111111111111&amp;maxJourneys=\n" + 
			"\">\n" + 
			"<strong>Bus  M13</strong>\n" + 
			"</a>\n" + 
			"</td>\n" + 
			"<td>\n" + 
			"Ersatz Richtung Virchow-Klinikum\n" + 
			"</td>\n" + 
			"</tr>\n" + 
			"<tr class=\"ivu_table_bg2\">\n" + 
			"<td><strong>16:25</strong></td>\n" + 
			"<td>\n" + 
			"<a href=\"/Fahrinfo/bin/traininfo.bin/dox/546678/185008/444932/40301/80/ld=0.1&amp;backLink=sq&amp;input=9007160&amp;boardType=&amp;time=actual&productsFilter=1111111111111111&amp;maxJourneys=\n" + 
			"\">\n" + 
			"<strong>Bus   50</strong>\n" + 
			"</a>\n" + 
			"</td>\n" + 
			"<td>\n" + 
			"Ersatz Richtung Guyotstr.\n" + 
			"</td>\n" + 
			"</tr>\n" + 
			"<tr class=\"ivu_table_bg1\">\n" + 
			"<td><strong>16:29</strong></td>\n" + 
			"<td>\n" + 
			"<a href=\"/Fahrinfo/bin/traininfo.bin/dox/801903/270080/378658/78034/80/ld=0.1&amp;backLink=sq&amp;input=9007160&amp;boardType=&amp;time=actual&productsFilter=1111111111111111&amp;maxJourneys=\n" + 
			"\">\n" + 
			"<strong>Bus   50</strong>\n" + 
			"</a>\n" + 
			"</td>\n" + 
			"<td>\n" + 
			"Ersatz Richtung U Seestr.\n" + 
			"</td>\n" + 
			"</tr>\n" + 
			"<tr class=\"ivu_table_bg2\">\n" + 
			"<td><strong>16:30</strong></td>\n" + 
			"<td>\n" + 
			"<a href=\"/Fahrinfo/bin/traininfo.bin/dox/997578/348653/9354/327910/80/ld=0.1&amp;backLink=sq&amp;input=9007160&amp;boardType=&amp;time=actual&productsFilter=1111111111111111&amp;maxJourneys=\n" + 
			"\">\n" + 
			"<strong>Bus  M13</strong>\n" + 
			"</a>\n" + 
			"</td>\n" + 
			"<td>\n" + 
			"Ersatz Richtung S Warschauer Str.\n" + 
			"</td>\n" + 
			"</tr>\n" + 
			"<tr class=\"ivu_table_bg1\">\n" + 
			"<td><strong>16:34</strong></td>\n" + 
			"<td>\n" + 
			"<a href=\"/Fahrinfo/bin/traininfo.bin/dox/425715/158054/536150/126232/80/ld=0.1&amp;backLink=sq&amp;input=9007160&amp;boardType=&amp;time=actual&productsFilter=1111111111111111&amp;maxJourneys=\n" + 
			"\">\n" + 
			"<strong>Bus  M13</strong>\n" + 
			"</a>\n" + 
			"</td>\n" + 
			"<td>\n" + 
			"Ersatz Richtung Virchow-Klinikum\n" + 
			"</td>\n" + 
			"</tr>\n" + 
			"<tr class=\"ivu_table_bg2\">\n" + 
			"<td><strong>16:35</strong></td>\n" + 
			"<td>\n" + 
			"<a href=\"/Fahrinfo/bin/traininfo.bin/dox/668619/225655/456886/5632/80/ld=0.1&amp;backLink=sq&amp;input=9007160&amp;boardType=&amp;time=actual&productsFilter=1111111111111111&amp;maxJourneys=\n" + 
			"\">\n" + 
			"<strong>Bus   50</strong>\n" + 
			"</a>\n" + 
			"</td>\n" + 
			"<td>\n" + 
			"Ersatz Richtung Guyotstr.\n" + 
			"</td>\n" + 
			"</tr>\n" + 
			"<tr class=\"ivu_table_bg1\">\n" + 
			"<td><strong>16:39</strong></td>\n" + 
			"<td>\n" + 
			"<a href=\"/Fahrinfo/bin/traininfo.bin/dox/31086/13141/744596/361999/80/ld=0.1&amp;backLink=sq&amp;input=9007160&amp;boardType=&amp;time=actual&productsFilter=1111111111111111&amp;maxJourneys=\n" + 
			"\">\n" + 
			"<strong>Bus   50</strong>\n" + 
			"</a>\n" + 
			"</td>\n" + 
			"<td>\n" + 
			"Ersatz Richtung U Seestr.\n" + 
			"</td>\n" + 
			"</tr>\n" + 
			"<tr class=\"ivu_table_bg2\">\n" + 
			"<td><strong>16:40</strong></td>\n" + 
			"<td>\n" + 
			"<a href=\"/Fahrinfo/bin/traininfo.bin/dox/95427/47936/965420/450963/80/ld=0.1&amp;backLink=sq&amp;input=9007160&amp;boardType=&amp;time=actual&productsFilter=1111111111111111&amp;maxJourneys=\n" + 
			"\">\n" + 
			"<strong>Bus  M13</strong>\n" + 
			"</a>\n" + 
			"</td>\n" + 
			"<td>\n" + 
			"Ersatz Richtung S Warschauer Str.\n" + 
			"</td>\n" + 
			"</tr>\n" + 
			"<tr class=\"ivu_table_bg1\">\n" + 
			"<td><strong>16:44</strong></td>\n" + 
			"<td>\n" + 
			"<a href=\"/Fahrinfo/bin/traininfo.bin/dox/561978/203475/910948/268211/80/ld=0.1&amp;backLink=sq&amp;input=9007160&amp;boardType=&amp;time=actual&productsFilter=1111111111111111&amp;maxJourneys=\n" + 
			"\">\n" + 
			"<strong>Bus  M13</strong>\n" + 
			"</a>\n" + 
			"</td>\n" + 
			"<td>\n" + 
			"Ersatz Richtung Virchow-Klinikum\n" + 
			"</td>\n" + 
			"</tr>\n" + 
			"<tr class=\"ivu_table_bg2\">\n" + 
			"<td><strong>16:45</strong></td>\n" + 
			"<td>\n" + 
			"<a href=\"/Fahrinfo/bin/traininfo.bin/dox/687594/231980/126418/166052/80/ld=0.1&amp;backLink=sq&amp;input=9007160&amp;boardType=&amp;time=actual&productsFilter=1111111111111111&amp;maxJourneys=\n" + 
			"\">\n" + 
			"<strong>Bus   50</strong>\n" + 
			"</a>\n" + 
			"</td>\n" + 
			"<td>\n" + 
			"Ersatz Richtung Guyotstr.\n" + 
			"</td>\n" + 
			"</tr>\n" + 
			"<tr class=\"ivu_table_bg1\">\n" + 
			"<td><strong>16:49</strong></td>\n" + 
			"<td>\n" + 
			"<a href=\"/Fahrinfo/bin/traininfo.bin/dox/945180/360299/577344/26388/80/ld=0.1&amp;backLink=sq&amp;input=9007160&amp;boardType=&amp;time=actual&productsFilter=1111111111111111&amp;maxJourneys=\n" + 
			"\">\n" + 
			"<strong>Bus   50</strong>\n" + 
			"</a>\n" + 
			"</td>\n" + 
			"<td>\n" + 
			"Ersatz Richtung U Seestr.\n" + 
			"</td>\n" + 
			"</tr>\n" + 
			"<tr class=\"ivu_table_bg2\">\n" + 
			"<td><strong>16:50</strong></td>\n" + 
			"<td>\n" + 
			"<a href=\"/Fahrinfo/bin/traininfo.bin/dox/160125/69502/343032/118204/80/ld=0.1&amp;backLink=sq&amp;input=9007160&amp;boardType=&amp;time=actual&productsFilter=1111111111111111&amp;maxJourneys=\n" + 
			"\">\n" + 
			"<strong>Bus  M13</strong>\n" + 
			"</a>\n" + 
			"</td>\n" + 
			"<td>\n" + 
			"Ersatz Richtung S Warschauer Str.\n" + 
			"</td>\n" + 
			"</tr>\n" + 
			"<tr class=\"ivu_table_bg1\">\n" + 
			"<td><strong>16:54</strong></td>\n" + 
			"<td>\n" + 
			"<a href=\"/Fahrinfo/bin/traininfo.bin/dox/985548/374710/426930/115051/80/ld=0.1&amp;backLink=sq&amp;input=9007160&amp;boardType=&amp;time=actual&productsFilter=1111111111111111&amp;maxJourneys=\n" + 
			"\">\n" + 
			"<strong>Bus  M13</strong>\n" + 
			"</a>\n" + 
			"</td>\n" + 
			"<td>\n" + 
			"Ersatz Richtung Virchow-Klinikum\n" + 
			"</td>\n" + 
			"</tr>\n" + 
			"<tr class=\"ivu_table_bg2\">\n" + 
			"<td><strong>16:55</strong></td>\n" + 
			"<td>\n" + 
			"<a href=\"/Fahrinfo/bin/traininfo.bin/dox/29796/55173/337728/158932/80/ld=0.1&amp;backLink=sq&amp;input=9007160&amp;boardType=&amp;time=actual&productsFilter=1111111111111111&amp;maxJourneys=\n" + 
			"\">\n" + 
			"<strong>Bus   50</strong>\n" + 
			"</a>\n" + 
			"</td>\n" + 
			"<td>\n" + 
			"Ersatz Richtung Guyotstr.\n" + 
			"</td>\n" + 
			"</tr>\n" + 
			"<tr class=\"ivu_table_bg1\">\n" + 
			"<td><strong>16:59</strong></td>\n" + 
			"<td>\n" + 
			"<a href=\"/Fahrinfo/bin/traininfo.bin/dox/925512/353743/513510/51750/80/ld=0.1&amp;backLink=sq&amp;input=9007160&amp;boardType=&amp;time=actual&productsFilter=1111111111111111&amp;maxJourneys=\n" + 
			"\">\n" + 
			"<strong>Bus   50</strong>\n" + 
			"</a>\n" + 
			"</td>\n" + 
			"<td>\n" + 
			"Ersatz Richtung U Seestr.\n" + 
			"</td>\n" + 
			"</tr>\n" + 
			"<tr class=\"ivu_table_bg2\">\n" + 
			"<td><strong>17:00</strong></td>\n" + 
			"<td>\n" + 
			"<a href=\"/Fahrinfo/bin/traininfo.bin/dox/989418/375994/418956/120328/80/ld=0.1&amp;backLink=sq&amp;input=9007160&amp;boardType=&amp;time=actual&productsFilter=1111111111111111&amp;maxJourneys=\n" + 
			"\">\n" + 
			"<strong>Bus  M13</strong>\n" + 
			"</a>\n" + 
			"</td>\n" + 
			"<td>\n" + 
			"Ersatz Richtung S Warschauer Str.\n" + 
			"</td>\n" + 
			"</tr>\n" + 
			"<tr class=\"ivu_table_bg1\">\n" + 
			"<td><strong>17:04</strong></td>\n" + 
			"<td>\n" + 
			"<a href=\"/Fahrinfo/bin/traininfo.bin/dox/226206/121596/53384/48711/80/ld=0.1&amp;backLink=sq&amp;input=9007160&amp;boardType=&amp;time=actual&productsFilter=1111111111111111&amp;maxJourneys=\n" + 
			"\">\n" + 
			"<strong>Bus  M13</strong>\n" + 
			"</a>\n" + 
			"</td>\n" + 
			"<td>\n" + 
			"Ersatz Richtung Virchow-Klinikum\n" + 
			"</td>\n" + 
			"</tr>\n" + 
			"<tr class=\"ivu_table_bg2\">\n" + 
			"<td><strong>17:05</strong></td>\n" + 
			"<td>\n" + 
			"<a href=\"/Fahrinfo/bin/traininfo.bin/dox/789498/308407/687464/80567/80/ld=0.1&amp;backLink=sq&amp;input=9007160&amp;boardType=&amp;time=actual&productsFilter=1111111111111111&amp;maxJourneys=\n" + 
			"\">\n" + 
			"<strong>Bus   50</strong>\n" + 
			"</a>\n" + 
			"</td>\n" + 
			"<td>\n" + 
			"Ersatz Richtung Guyotstr.\n" + 
			"</td>\n" + 
			"</tr>\n" + 
			"<tr class=\"ivu_table_bg1\">\n" + 
			"<td><strong>17:09</strong></td>\n" + 
			"<td>\n" + 
			"<a href=\"/Fahrinfo/bin/traininfo.bin/dox/182952/106223/219950/48993/80/ld=0.1&amp;backLink=sq&amp;input=9007160&amp;boardType=&amp;time=actual&productsFilter=1111111111111111&amp;maxJourneys=\n" + 
			"\">\n" + 
			"<strong>Bus   50</strong>\n" + 
			"</a>\n" + 
			"</td>\n" + 
			"<td>\n" + 
			"Ersatz Richtung U Seestr.\n" + 
			"</td>\n" + 
			"</tr>\n" + 
			"<tr class=\"ivu_table_bg2\">\n" + 
			"<td><strong>17:10</strong></td>\n" + 
			"<td>\n" + 
			"<a href=\"/Fahrinfo/bin/traininfo.bin/dox/39456/59340/741270/357484/80/ld=0.1&amp;backLink=sq&amp;input=9007160&amp;boardType=&amp;time=actual&productsFilter=1111111111111111&amp;maxJourneys=\n" + 
			"\">\n" + 
			"<strong>Bus  M13</strong>\n" + 
			"</a>\n" + 
			"</td>\n" + 
			"<td>\n" + 
			"Ersatz Richtung S Warschauer Str.\n" + 
			"</td>\n" + 
			"</tr>\n" + 
			"<tr class=\"ivu_table_bg1\">\n" + 
			"<td><strong>17:14</strong></td>\n" + 
			"<td>\n" + 
			"<a href=\"/Fahrinfo/bin/traininfo.bin/dox/631590/256724/834972/206958/80/ld=0.1&amp;backLink=sq&amp;input=9007160&amp;boardType=&amp;time=actual&productsFilter=1111111111111111&amp;maxJourneys=\n" + 
			"\">\n" + 
			"<strong>Bus  M13</strong>\n" + 
			"</a>\n" + 
			"</td>\n" + 
			"<td>\n" + 
			"Ersatz Richtung Virchow-Klinikum\n" + 
			"</td>\n" + 
			"</tr>\n" + 
			"<tr class=\"ivu_table_bg2\">\n" + 
			"<td><strong>17:15</strong></td>\n" + 
			"<td>\n" + 
			"<a href=\"/Fahrinfo/bin/traininfo.bin/dox/332400/156041/445536/111970/80/ld=0.1&amp;backLink=sq&amp;input=9007160&amp;boardType=&amp;time=actual&productsFilter=1111111111111111&amp;maxJourneys=\n" + 
			"\">\n" + 
			"<strong>Bus   50</strong>\n" + 
			"</a>\n" + 
			"</td>\n" + 
			"<td>\n" + 
			"Ersatz Richtung Guyotstr.\n" + 
			"</td>\n" + 
			"</tr>\n" + 
			"<tr class=\"ivu_table_bg1\">\n" + 
			"<td><strong>17:19</strong></td>\n" + 
			"<td>\n" + 
			"<a href=\"/Fahrinfo/bin/traininfo.bin/dox/866982/334233/980768/201393/80/ld=0.1&amp;backLink=sq&amp;input=9007160&amp;boardType=&amp;time=actual&productsFilter=1111111111111111&amp;maxJourneys=\n" + 
			"\">\n" + 
			"<strong>Bus   50</strong>\n" + 
			"</a>\n" + 
			"</td>\n" + 
			"<td>\n" + 
			"Ersatz Richtung U Seestr.\n" + 
			"</td>\n" + 
			"</tr>\n" + 
			"<tr class=\"ivu_table_bg2\">\n" + 
			"<td><strong>17:20</strong></td>\n" + 
			"<td>\n" + 
			"<a href=\"/Fahrinfo/bin/traininfo.bin/dox/610539/249701/315944/45543/80/ld=0.1&amp;backLink=sq&amp;input=9007160&amp;boardType=&amp;time=actual&productsFilter=1111111111111111&amp;maxJourneys=\n" + 
			"\">\n" + 
			"<strong>Bus  M13</strong>\n" + 
			"</a>\n" + 
			"</td>\n" + 
			"<td>\n" + 
			"Ersatz Richtung S Warschauer Str.\n" + 
			"</td>\n" + 
			"</tr>\n" + 
			"</table>\n" + 
			"</div>\n" + 
			"<p class=\"links\">\n" + 
			"<a href=\"/Fahrinfo/bin/stboard.bin/dox?input=9007160&amp;boardType=&amp;time=17:20%2B1&amp;productsFilter=1111111111111111&amp;date=08.04.16&amp;maxJourneys=&amp;\">Haltestelleninfo</a>\n" + 
			"<br />\n" + 
			"<br />\n" + 
			"<a href=\"http://mobil.bvg.de/Fahrinfo/bin/detect.bin/dox?\">Fahrplanauskunft</a>\n" + 
			"<br />\n" + 
			"</p>\n" + 
			"<div id=\"ivu_footer\">\n" + 
			"<a href=\"http://www.bvg.de/de/Serviceseiten/Impressum\" title=\"Impressum\" class=\"catlink\">Impressum</a>\n" + 
			"</div>\n" + 
			"</div>\n" + 
			"</body>\n" + 
			"</html>\n";

}
