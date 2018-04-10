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
public class TextSprite {

    char[] characters;
    byte[] fgColors;
    byte[] bgColors;

    private int width;
    private int height;
    private int length;
    private int xOfs;
    private int yOfs;
    private int nextLine;

    public TextSprite(int width, int height) {
        this.width = width;
        this.height = height;
        length = width * height;
        xOfs = 0;
        yOfs = 0;
        characters = new char[length];
        fgColors = new byte[length];
        bgColors = new byte[length];
        clear();
        nextLine = 0;
    }

    public TextSprite setOffset(int x, int y) {
        this.xOfs = x;
        this.yOfs = y;
        return this;
    }

    private int getIndexFromNextLine() {
        int index = nextLine * width;
        nextLine++;
        return index;
    }

    public TextSprite editLine(int line) {
        nextLine = line;
        return this;
    }

    public TextSprite setLine(String lineChars, byte color) {
        String lineColors = makeColorLine(color, lineChars.length());
        return setLine(lineChars, lineColors, null);
    }

    public TextSprite setLine(String lineChars, String lineColors) {
        return setLine(lineChars, lineColors, null);
    }

    public TextSprite setLine(String lineChars, byte color, byte bgColor) {
        String lineColors = makeColorLine(color, lineChars.length());
        String lineBgColors = makeColorLine(bgColor, lineChars.length());
        return setLine(lineChars, lineColors, lineBgColors);
    }

    public TextSprite setLine(String lineChars, String lineColors, byte bgColor) {
        String lineBgColors = makeColorLine(bgColor, lineChars.length());
        return setLine(lineChars, lineColors, lineBgColors);
    }

    public TextSprite setLine(String lineChars, String lineColors, String lineBgColors) {
        int index0 = getIndexFromNextLine();
        for (int i = 0; i < width; i++) {
            int index = index0 + i;
            characters[index] = lineChars.charAt(i);
            if (lineColors != null) {
                fgColors[index] = TextColor.colorFromChar(lineColors.charAt(i));
            }
            if (lineBgColors != null) {
                bgColors[index] = TextColor.colorFromChar(lineBgColors.charAt(i));
            }
        }
        return this;
    }

    private String makeColorLine(byte col, int len) {
        char c = TextColor.charForColor(col);
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    public void draw(int x0, int y0, TextScreenBuffer buffer) {
        int xBase = x0 - xOfs;
        int yBase = y0 - yOfs;
        for (int v = 0; v < height; v++) {
            for (int u = 0; u < width; u++) {
                int index = v * width + u;
                int x = xBase + u;
                int y = yBase + v;
                char c = characters[index];
                if (c != ' ') {
                    buffer.setCharacter(x, y, c);
                    byte fg = fgColors[index];
                    if (fg > TextColor.NO_COLOR) {
                        buffer.setFgColor(x, y, fg);
                    }
                    byte bg = bgColors[index];
                    if (bg > TextColor.NO_COLOR) {
                        buffer.setBgColor(x, y, bg);
                    }
                }
            }
        }
    }

    private void clear() {
        for (int i = 0; i < length; i++) {
            characters[i] = ' ';
            fgColors[i] = TextColor.NO_COLOR;
            bgColors[i] = TextColor.NO_COLOR;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        int count = 0;
        for (char c : characters) {
            sb.append(c);
            count++;
            if (count % width == 0) {
                sb.append('\n');
            }
        }

        return sb.toString();
    }

}
