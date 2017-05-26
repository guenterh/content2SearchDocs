package org.swissbib.documentprocessing.utils;

import org.swissbib.documentprocessing.exceptions.XML2SolrInitException;
import org.swissbib.documentprocessing.plugins.IDocProcPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Created by swissbib on 26.05.17.
 */
public class ReadFileProperties {

    public static void readProperties(HashMap<String, String> configuration, ArrayList<IDocProcPlugin> plugins)
            throws XML2SolrInitException {

        try {
            //now load the initialization done via property file
            File configFile = new File(configuration.get("CONF.ADDITIONAL.PROPS.FILE"));
            FileInputStream fi = new FileInputStream(configFile);
            Properties configProps = new Properties();
            configProps.load(fi);

            Enumeration<Object> propKeys = configProps.keys();

            while (propKeys.hasMoreElements()) {
                String key = (String) propKeys.nextElement();
                configuration.put(key.toUpperCase(), (String) configProps.getProperty(key));
            }


            //are there any configured plugins?
            if (configuration.containsKey("PLUGINS.TO.LOAD".toUpperCase())) {

                String pluginsConf = configuration.get("PLUGINS.TO.LOAD");
                ArrayList<String> pluginClassNames = new ArrayList<String>();
                pluginClassNames.addAll(Arrays.asList(pluginsConf.split("###")));


                for (String cName : pluginClassNames) {
                    try {
                        Class tClass = Class.forName(cName);
                        IDocProcPlugin docProcPlugin = (IDocProcPlugin) tClass.newInstance();
                        docProcPlugin.initPlugin(configuration);
                        plugins.add(docProcPlugin);
                    } catch (ClassNotFoundException nfE) {
                        nfE.printStackTrace();
                    } catch (InstantiationException instE) {
                        instE.printStackTrace();
                    } catch (IllegalAccessException illegalAccessE) {
                        illegalAccessE.printStackTrace();
                    }
                    //patterns.add(Pattern.compile(regex));
                }

            }

            //marcXMLlogger.info("\n => loaded properties (mandatory and file: ");
            for (String key : configuration.keySet()) {

                System.out.println("=> key: " + key + "   ===>>>>   " + configuration.get(key));
                //marcXMLlogger.info("\n => key: " + key + "   ===>>>>   " + configuration.get(key));

            }


        } catch (FileNotFoundException fNFEx) {

            throw new XML2SolrInitException(fNFEx);

        } catch (IOException ioEx) {

            throw new XML2SolrInitException(ioEx);

        } catch (Exception ex) {
            throw new XML2SolrInitException(ex);
        }
    }





}
