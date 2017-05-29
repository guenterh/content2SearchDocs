package org.swissbib.documentprocessing;

import org.culturegraph.mf.io.FileOpener;
import org.culturegraph.mf.io.LineReader;
import org.culturegraph.mf.io.ObjectWriter;
import org.swissbib.documentprocessing.exceptions.XML2SolrInitException;
import org.swissbib.documentprocessing.mf.command.XSLTPipeStart;
import org.swissbib.documentprocessing.mf.command.XSLTPipeStep;
import org.swissbib.documentprocessing.mf.command.XSLTPipeStop;
import org.swissbib.documentprocessing.plugins.IDocProcPlugin;
import org.swissbib.documentprocessing.utils.ReadFileProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by swissbib on 26.05.17.
 */
public class MFDocProcFlow {

    protected static HashMap<String,String> configuration;

    public static void main (String[] args) {

        MFDocProcFlow flow = new MFDocProcFlow();

        try {

            HashMap<String, String> configuration =  flow.initMandatoryTransformationProps(System.getProperties());
            ArrayList<IDocProcPlugin> plugins = new ArrayList<>();
            ReadFileProperties.readProperties(configuration, plugins);

            FileOpener fo = new FileOpener();
            LineReader lr = new LineReader();

            //swissbib.solr.step1.xsl

            XSLTPipeStart step1 =  new XSLTPipeStart();
            step1.setTemplate("xslt/swissbib.solr.step1.xsl");
            step1.setWeedingTemplate("xslt/weedholdings.xsl");
            step1.setHoldingsTemplate("xslt/collect.holdings.xsl");

            lr.setReceiver(step1);


            ObjectWriter<String> ow = new ObjectWriter<String>("data/all.out.xml");
            XSLTPipeStep step2 =  new XSLTPipeStep();
            step2.setTemplate("xslt/vufind.special.green.xsl");

            step1.setReceiver(step2);

            XSLTPipeStop step3 =  new XSLTPipeStop();
            step3.setTemplate("xslt/swissbib.solr.vufind2.xsl");
            step3.setReceiver(ow);
            step2.setReceiver(step3);

            fo.setReceiver(lr);
            fo.process("data/job1r127A149.format.xml.gz");



        } catch (XML2SolrInitException initExec) {
            initExec.printStackTrace();
        }


    }


    protected HashMap<String,String> initMandatoryTransformationProps(Properties mandatoryProps) throws XML2SolrInitException {

        //marcXMLlogger.info("\n\n\n\n\n\ninitialization of mandatory program arguments");


        String[] tMandatoryProps = new String[] {"CONF.ADDITIONAL.PROPS.FILE","INPUT.FILE","OUTPUT.DIR","XPATH.DIR","SKIPRECORDS"};

        //Properties programProps = new Properties();

        HashMap<String,String> configuration = new HashMap<>();

        for (String ts : tMandatoryProps) {
            if (!mandatoryProps.containsKey(ts)) {

                throw new XML2SolrInitException(new Exception("missing mandatory property: " + ts));

            } else {
                configuration.put(ts.toUpperCase(),mandatoryProps.getProperty(ts)) ;

            }
        }

        return configuration;

    }



}
