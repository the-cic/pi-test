/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush;

import com.mush.textscreen.TextScreenBuffer;

/**
 *
 * @author cic
 */
public class Main {

    public static void main(String[] args) {
        Example example = new Example();
        new Thread(example).start();

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                example.onClose();
            }
        });
    }

}
