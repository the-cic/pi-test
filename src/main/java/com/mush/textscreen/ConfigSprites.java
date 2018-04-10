/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.textscreen;

import java.util.HashMap;
import java.util.Set;
import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.SubnodeConfiguration;

/**
 *
 * @author cic
 */
public class ConfigSprites {
    
    private final HashMap<String, TextSprite> sprites;
    
    public ConfigSprites() {
        sprites = new HashMap<>();
    }
    
    public TextSprite get(String spriteName) {
        return sprites.get(spriteName);
    }
    
    public Set<String> getNames() {
        return sprites.keySet();
    }

    public void load(String configFile) {
        INIConfiguration config = Config.readConfig(configFile);

        Set<String> sections = config.getSections();

        for (String spriteName : sections) {
            loadSprite(spriteName, config.getSection(spriteName));
        }
    }

    private void loadSprite(String spriteName, SubnodeConfiguration section) {
        int width = section.getInt("width");
        int height = section.getInt("height");
        TextSprite sprite = new TextSprite(width, height);

        sprite.setOffset(section.getInt("offsetX", 0), section.getInt("offsetY", 0));

        String[] shape = section.getStringArray("shape");
        String[] color = section.getStringArray("color");
        String[] bgColor = section.getStringArray("bgColor");

        if (shape.length != height) {
            throw new IllegalArgumentException("Wrong number of lines for sprite " + spriteName);
        }

        for (int i = 0; i < shape.length; i++) {
            String colorLine = i < color.length ? color[i] : null;
            String bgColorLine = i < bgColor.length ? bgColor[i] : null;
            sprite.setLine(shape[i], colorLine, bgColorLine);
        }
        
        System.out.println("Loaded sprite: " + spriteName);
        System.out.println(sprite.toString());
        
        sprites.put(spriteName, sprite);
    }

}
