/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.pitest1;

/**
 *
 * @author cic
 */
public class TextScreenBuffer {

    public static final byte BLACK = 0;
    public static final byte RED = 1;
    public static final byte GREEN = 2;
    public static final byte YELLOW = 3;
    public static final byte BLUE = 4;
    public static final byte MAGENTA = 5;
    public static final byte CYAN = 6;
    public static final byte WHITE = 7;

    private final char ESC = '\033';

    private char[] characters;
    private byte[] fgColors;
    private byte[] bgColors;
    private boolean[] dirty;
    private int dirtyCount;
    private int width;
    private int height;

    public TextScreenBuffer() {
        init(5, 5);
    }

    public void init(int w, int h) {
        width = w;
        height = h;
        characters = new char[getWidth() * getHeight()];
        fgColors = new byte[getWidth() * getHeight()];
        bgColors = new byte[getWidth() * getHeight()];
        dirty = new boolean[getWidth() * getHeight()];
        clear(' ', WHITE, BLACK);
    }

    public void clear(char fillC, byte fgCol, byte bgCol) {
        dirtyCount = 0;
        for (int i = 0; i < this.characters.length; i++) {
            this.dirty[i] = false;
            setCharacter(i, fillC);
            setFgColor(i, fgCol);
            setBgColor(i, bgCol);
        }
    }

    public String outputClearScreen() {
        return ESC + "[2J";
    }

    public String output() {
        StringBuilder sb = new StringBuilder();

        outputResetColor(sb);

        double dirtyPercent = (double) (dirtyCount) / dirty.length;

        if (dirtyPercent > 0.5) {
            outputWholeBuffer(sb);
        } else {
            outputDirtyBuffer(sb);
        }

        sb.append("\ndirty: ").append(dirtyPercent).append(" ").append(dirtyCount).append("       ");

        clearDirty();

        return sb.toString();
    }

    public void outputWholeBuffer(StringBuilder sb) {
        byte prevFg = -1;
        byte prevBg = -1;
        for (int line = 0; line < height; line++) {
            placeCursor(0, line, sb);
            for (int x = 0; x < width; x++) {
                int index = getValidIndex(x, line);
                byte fg = this.fgColors[index];
                byte bg = this.bgColors[index];
                boolean fgChanged = fg != prevFg;
                boolean bgChanged = bg != prevBg;
                if (fgChanged || bgChanged) {
                    outputColor(fgChanged ? fg : -1, bgChanged ? bg : -1, sb);
                }
                sb.append(this.characters[index]);
                prevFg = fg;
                prevBg = bg;
            }
        }
        outputResetColor(sb);
        placeCursor(0, height, sb);
        sb.append("\nfull               ");
    }

    public void outputDirtyBuffer(StringBuilder sb) {
        byte prevFg = -1;
        byte prevBg = -1;
        int segments = 0;
        for (int line = 0; line < height; line++) {
            boolean wasDirty = false;
            for (int x = 0; x < width; x++) {
                int index = getValidIndex(x, line);
                if (dirty[index]) {
                    if (!wasDirty) {
                        placeCursor(x, line, sb);
                        segments++;
                    }
                    // output
                    byte fg = this.fgColors[index];
                    byte bg = this.bgColors[index];
                    boolean fgChanged = fg != prevFg;
                    boolean bgChanged = bg != prevBg;
                    if (fgChanged || bgChanged) {
                        outputColor(fgChanged ? fg : -1, bgChanged ? bg : -1, sb);
                    }
                    sb.append(this.characters[index]);
                    prevFg = fg;
                    prevBg = bg;
                    wasDirty = true;
                } else {
                    wasDirty = false;
                    prevFg = -1;
                    prevBg = -1;
                }
            }
        }
        outputResetColor(sb);
        placeCursor(0, height, sb);
        sb.append("\nsegments: ").append(segments).append("      ");
    }

    private void outputResetColor(StringBuilder sb) {
        sb.append(ESC).append("[0m");
    }

    private void outputColor(byte fg, byte bg, StringBuilder sb) {
        sb.append(ESC).append('[');
        if (fg >= 0) {
            sb.append(fg + 30);
            if (bg >= 0) {
                sb.append(';');
            }
        }
        if (bg >= 0) {
            sb.append(bg + 40);
        }
        sb.append('m');
    }

    private void placeCursor(int x, int y, StringBuilder sb) {
        sb.append(ESC).append('[').append(y + 1).append(';').append(x + 1).append('H');
    }

    public void write(int x, int y, String s, byte fg, byte bg) {
        for (int i = 0; i < s.length(); i++) {
            int u = x + i;
            setCharacter(u, y, s.charAt(i));
            if (fg >= 0) {
                setFgColor(u, y, fg);
            }
            if (bg >= 0) {
                setBgColor(u, y, bg);
            }
        }
    }

    public void setCharacter(int x, int y, char c) {
        int index = getIndex(x, y);
        if (index >= 0 && index < characters.length) {
            setCharacter(index, c);
        }
    }

    private void setCharacter(int index, char c) {
        if (characters[index] != c) {
            setDirty(index);
        }
        characters[index] = c;
    }

    public void setFgColor(int x, int y, byte fg) {
        int index = getIndex(x, y);
        if (index >= 0 && index < fgColors.length) {
            setFgColor(index, fg);
        }
    }

    private void setFgColor(int index, byte fg) {
        if (fgColors[index] != fg) {
            setDirty(index);
        }
        fgColors[index] = fg;
    }

    public void setBgColor(int x, int y, byte bg) {
        int index = getIndex(x, y);
        if (index >= 0 && index < bgColors.length) {
            setBgColor(index, bg);
        }
    }

    public void setBgColor(int index, byte bg) {
        if (bgColors[index] != bg) {
            setDirty(index);
        }
        bgColors[index] = bg;
    }

    private void setDirty(int index) {
        if (!dirty[index]) {
            dirty[index] = true;
            dirtyCount++;
        }
    }

    private void clearDirty() {
        for (int i = 0; i < dirty.length; i++) {
            dirty[i] = false;
        }
        dirtyCount = 0;
    }

    public char getCharacter(int x, int y) {
        int index = getIndex(x, y);
        if (index < 0 || index >= characters.length) {
            return ' ';
        }
        return characters[getIndex(x, y)];
    }

    public byte getFgColor(int x, int y) {
        int index = getIndex(x, y);
        if (index < 0 || index >= fgColors.length) {
            return 0;
        }
        return fgColors[index];
    }

    public byte getBgColor(int x, int y) {
        int index = getIndex(x, y);
        if (index < 0 || index >= bgColors.length) {
            return 0;
        }
        return bgColors[index];
    }

    private int getIndex(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return -1;
        }
        return getValidIndex(x, y);
    }

    private int getValidIndex(int x, int y) {
        return y * getWidth() + x;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

}
