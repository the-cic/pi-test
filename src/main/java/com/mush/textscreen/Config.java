/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.textscreen;

import java.nio.file.Paths;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.ex.ConversionException;

/**
 *
 * @author cic
 */
public class Config {

    private static final String CONFIG_DIR = "config";

    public static INIConfiguration readConfig(String configFile) {
        String configFilePath = Paths.get(CONFIG_DIR, configFile).toString();
        System.out.println("Reading config file: " + configFilePath);

        FileBasedConfigurationBuilder<INIConfiguration> builder
                = new FileBasedConfigurationBuilder<>(INIConfiguration.class)
                        .configure(new Parameters().properties()
                                .setFileName(configFilePath)
                        );

        try {
            return builder.getConfiguration();

        } catch (ConfigurationException | ConversionException ex) {
            ex.printStackTrace(System.out);
        }

        return null;
    }
}
