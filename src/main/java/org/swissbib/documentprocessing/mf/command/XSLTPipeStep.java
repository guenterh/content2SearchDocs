package org.swissbib.documentprocessing.mf.command;

import org.culturegraph.mf.framework.ObjectReceiver;
import org.culturegraph.mf.framework.helpers.DefaultObjectPipe;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.regex.Pattern;

/**
 * Created by swissbib on 26.05.17.
 */
public class XSLTPipeStep extends DefaultObjectPipe<String, ObjectReceiver<String>> {


    private String templatePath;
    private Pattern linePattern = Pattern.compile("<record .*?>.*?</record>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    Transformer transformer;

    public void setTemplate(String templatePath) {

        System.setProperty("javax.xml.transform.TransformerFactory","net.sf.saxon.TransformerFactoryImpl");


        this.templatePath = templatePath;

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        StreamSource source = null;

        if (new File(templatePath).exists()) {
            source = new StreamSource(templatePath);
        }


        try {
            transformer = transformerFactory.newTransformer(source);
        } catch (TransformerConfigurationException ex) {
            ex.printStackTrace();

        }

    }

    public void setUseLineWith(String regex) {
        linePattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    }

    @Override
    public void process(String obj) {
        if (linePattern.matcher(obj).find()) {
            Source recordSource =  new StreamSource(new StringReader(obj));
            StringWriter recordTargetBuffer = new StringWriter();
            StreamResult recordTarget = new StreamResult(recordTargetBuffer);

            try {
                transformer.transform(recordSource, recordTarget);
                getReceiver().process(recordTargetBuffer.toString());

            } catch (TransformerException transException) {
                transException.printStackTrace();
            }


        }

    }

}
