/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.textscreen;

/**
 *
 * @author Mirko Stancic, Dhimahi
 */
public class TextDraw {

    private TextScreenBuffer buffer;

    public TextDraw(TextScreenBuffer buffer) {
        this.buffer = buffer;
    }

    public void line(int x, int y, int dx, int dy, char c, byte fgCol) {
        int steps = (int) Math.sqrt(dx * dx + dy * dy);
        for (int i = 0; i < steps; i++) {
            double f = (double) i / steps;
            int u = (int) (x + dx * f);
            int v = (int) (y + dy * f);
            if (fgCol >= 0) {
                buffer.setFgColor(u, v, fgCol);
            }
            buffer.setCharacter(u, v, c);
        }
    }

    public void text(int x, int y, String s, byte fgCol, byte bgCol) {
        for (int i = 0; i < s.length(); i++) {
            int u = x + i;
            buffer.setCharacter(u, y, s.charAt(i));
            if (fgCol >= 0) {
                buffer.setFgColor(u, y, fgCol);
            }
            if (bgCol >= 0) {
                buffer.setBgColor(u, y, bgCol);
            }
        }
    }

}
