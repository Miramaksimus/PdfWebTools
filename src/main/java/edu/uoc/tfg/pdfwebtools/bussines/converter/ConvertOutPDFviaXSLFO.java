package edu.uoc.tfg.pdfwebtools.bussines.converter;


import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.docx4j.Docx4J;
import org.docx4j.XmlUtils;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.convert.out.fo.renderers.FORendererApacheFOP;
import org.docx4j.fonts.BestMatchingMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.jaxb.Context;
import org.docx4j.model.fields.FieldUpdater;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

import java.io.OutputStream;

/**
 * Demo of PDF output.
 * <p>
 * PDF output is via XSL FO.
 * First XSL FO is created, then FOP
 * is used to convert that to PDF.
 * <p>
 * Don't worry if you get a class not
 * found warning relating to batik. It
 * doesn't matter.
 * <p>
 * If you don't have logging configured,
 * your PDF will say "TO HIDE THESE MESSAGES,
 * TURN OFF debug level logging for
 * org.docx4j.convert.out.pdf.viaXSLFO".  The thinking is
 * that you need to be able to be warned if there
 * are things in your docx which the PDF output
 * doesn't support...
 * <p>
 * docx4j used to also support creating
 * PDF via iText and via HTML. As of docx4j 2.5.0,
 * only viaXSLFO is supported.  The viaIText and
 * viaHTML source code can be found in src/docx4j-extras directory
 *
 * @author jharrop
 */

public class ConvertOutPDFviaXSLFO {


    /*
     * NOT WORKING?
     *
     * If you are getting:
     *
     *   "fo:layout-master-set" must be declared before "fo:page-sequence"
     *
     * please check:
     *
     * 1.  the jaxb-xslfo jar is on your classpath
     *
     * 2.  that there is no stack trace earlier in the logs
     *
     * 3.  your JVM has adequate memory, eg
     *
     *           -Xmx1G -XX:MaxPermSize=128m
     *
     */

    protected static String inputfilepath;
    protected static String outputfilepath;


    // Config for non-command line use
    static {

        inputfilepath = null; // to generate a docx (and PDF output) containing font samples

        inputfilepath = System.getProperty("user.dir") + "/sample-docs/word/sample-docx.docx";

        saveFO = true;
    }


    // For demo/debugging purposes, save the intermediate XSL FO
    // Don't do this in production!
    static boolean saveFO;

    public static void main(String[] args)
            throws Exception {

        try {
            getInputFilePath(args);
        } catch (IllegalArgumentException e) {
        }

        // Font regex (optional)
        // Set regex if you want to restrict to some defined subset of fonts
        // Here we have to do this before calling createContent,
        // since that discovers fonts
        String regex = null;
        // Windows:
        // String
        // regex=".*(calibri|camb|cour|arial|symb|times|Times|zapf).*";
        //regex=".*(calibri|camb|cour|arial|times|comic|georgia|impact|LSANS|pala|tahoma|trebuc|verdana|symbol|webdings|wingding).*";
        // Mac
        // String
        // regex=".*(Courier New|Arial|Times New Roman|Comic Sans|Georgia|Impact|Lucida Console|Lucida Sans Unicode|Palatino Linotype|Tahoma|Trebuchet|Verdana|Symbol|Webdings|Wingdings|MS Sans Serif|MS Serif).*";
        PhysicalFonts.setRegex(regex);

        // Document loading (required)
        WordprocessingMLPackage wordMLPackage;
        if (inputfilepath == null) {
            // Create a docx
            System.out.println("No input path passed, creating dummy document");
            wordMLPackage = WordprocessingMLPackage.createPackage();
            SampleDocumentGenerator.createContent(wordMLPackage.getMainDocumentPart());
        } else {
            // Load .docx or Flat OPC .xml
            System.out.println("Loading file from " + inputfilepath);
            wordMLPackage = WordprocessingMLPackage.load(new java.io.File(inputfilepath));
        }

        // Refresh the values of DOCPROPERTY fields
        FieldUpdater updater = null;
//		updater = new FieldUpdater(wordMLPackage);
//		updater.update(true);

        // Set up font mapper (optional)
//		Mapper fontMapper = new IdentityPlusMapper();  // Only for Windows, unless you have Microsoft's fonts installed
        Mapper fontMapper = new BestMatchingMapper();  // Good for Linux (and OSX?)
        wordMLPackage.setFontMapper(fontMapper);

        // .. example of mapping font Times New Roman which doesn't have certain Arabic glyphs
        // eg Glyph "ي" (0x64a, afii57450) not available in font "TimesNewRomanPS-ItalicMT".
        // eg Glyph "ج" (0x62c, afii57420) not available in font "TimesNewRomanPS-ItalicMT".
        // to a font which does
        PhysicalFont font
                = PhysicalFonts.get("Arial Unicode MS");
        // make sure this is in your regex (if any)!!!
//		if (font!=null) {
//			fontMapper.put("Times New Roman", font);
//			fontMapper.put("Arial", font);
//		}
//		fontMapper.put("Libian SC Regular", PhysicalFonts.get("SimSun"));

        // Config - step 1
        // FO exporter setup (required)
        // .. the FOSettings object
        FOSettings foSettings = new FOSettings(wordMLPackage);
        // Now you can inspect generated font settings in case of any issues
        System.out.println(XmlUtils.marshaltoString(foSettings.getFopConfig(), Context.getFopConfigContext()));
        // You can also alter that Fop config object if you wish
        if (saveFO) {
            foSettings.setFoDumpFile(new java.io.File(inputfilepath + ".fo"));
        }

        // Config - step 2
        // Get FopFactoryBuilder, then its standard FOP config stuff
        FopFactoryBuilder fopFactoryBuilder = FORendererApacheFOP.getFopFactoryBuilder(foSettings);
        // or specify your own resolver.
        // FopFactoryBuilder fopFactoryBuilder = FORendererApacheFOP.getFopFactoryBuilder(foSettings, new ClasspathResolverURIAdapter()) ;
        // Either way, defaultBaseURI can be set at property "docx4j.Convert.Out.fop.FopConfParser.defaultBaseURI"

        // You can specify a HyphenBaseResourceResolver, but its probably not necessary if your specify your own resolver above
//		fopFactoryBuilder.setHyphenBaseResourceResolver(
//				ResourceResolverFactory.createInternalResourceResolver( (new File(".")).toURI(), new ClasspathResolverURIAdapter()));
        FopFactory fopFactory = fopFactoryBuilder.build();

        // Config - step 3
        FOUserAgent foUserAgent = FORendererApacheFOP.getFOUserAgent(foSettings, fopFactory);
        // configure foUserAgent as desired
        foUserAgent.setTitle("my title");

//	    foUserAgent.getRendererOptions().put("pdf-a-mode", "PDF/A-1b");

        // PDF/A-1a, PDF/A-2a and PDF/A-3a require accessibility to be enabled
        // see further https://stackoverflow.com/a/54587413/1031689
//	    foUserAgent.setAccessibility(true); // suppress "missing language information" messages from FOUserAgent .processEvent


        // Document format:
        // The default implementation of the FORenderer that uses Apache Fop will output
        // a PDF document if nothing is passed via
        // foSettings.setApacheFopMime(apacheFopMime)
        // apacheFopMime can be any of the output formats defined in org.apache.fop.apps.MimeConstants eg org.apache.fop.apps.MimeConstants.MIME_FOP_IF or
        // FOSettings.INTERNAL_FO_MIME if you want the fo document as the result.
        //foSettings.setApacheFopMime(FOSettings.INTERNAL_FO_MIME);

        // exporter writes to an OutputStream.
        String outputfilepath;
        if (inputfilepath == null) {
            outputfilepath = System.getProperty("user.dir") + "/OUT_FontContent.pdf";
        } else {
            outputfilepath = inputfilepath + ".pdf";
        }
        OutputStream os = new java.io.FileOutputStream(outputfilepath);

        // Specify whether PDF export uses XSLT or not to create the FO
        // (XSLT takes longer, but is more complete).

        // Don't care what type of exporter you use
        Docx4J.toFO(foSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);

        // Prefer the exporter, that uses a xsl transformation
        // Docx4J.toFO(foSettings, os, Docx4J.FLAG_EXPORT_PREFER_XSL);

        // Prefer the exporter, that doesn't use a xsl transformation (= uses a visitor)
        // .. faster, but not yet at feature parity
        // Docx4J.toFO(foSettings, os, Docx4J.FLAG_EXPORT_PREFER_NONXSL);

        System.out.println("Saved: " + outputfilepath);

        // Clean up, so any ObfuscatedFontPart temp files can be deleted
        if (wordMLPackage.getMainDocumentPart().getFontTablePart() != null) {
            wordMLPackage.getMainDocumentPart().getFontTablePart().deleteEmbeddedFontTempFiles();
        }
        // This would also do it, via finalize() methods
        updater = null;
        foSettings = null;
        wordMLPackage = null;


    }

    protected static void getInputFilePath(String[] args) throws IllegalArgumentException {

        if (args.length == 0) throw new IllegalArgumentException("Input file arg missing");

        inputfilepath = args[0];
    }

    protected static void getOutputFilePath(String[] args) throws IllegalArgumentException {

        if (args.length < 2) throw new IllegalArgumentException("Output file arg missing");

        outputfilepath = args[1];
    }


}
