/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush.textscreen;

/**
 *
 * @author cic
 */
public class TextScreenBuffer {

    private final char ESC = '\033';

    // Active drawing maps
    private char[] characters;
    private byte[] fgColors;
    private byte[] bgColors;

    // Previous output content
    private char[] charactersBuffer;
    private byte[] fgColorsBuffer;
    private byte[] bgColorsBuffer;

    private int width;
    private int height;
    private int length;
    private int fullFrameCountdown;
    private int dirtyFramesUntilFullFrame = 100;
    private boolean showStats = false;

    public TextScreenBuffer() {
        init(5, 5);
    }

    public void init(int w, int h) {
        width = w;
        height = h;
        length = w * h;
        characters = new char[length];
        fgColors = new byte[length];
        bgColors = new byte[length];
        charactersBuffer = new char[length];
        fgColorsBuffer = new byte[length];
        bgColorsBuffer = new byte[length];
        clear(' ', TextColor.WHITE, TextColor.BLACK);
        fullFrameCountdown = dirtyFramesUntilFullFrame;
    }

    public void setShowStats(boolean v) {
        showStats = v;
    }

    public boolean getShowStats() {
        return showStats;
    }

    public void setDirtyFramesUntilFullFrame(int v) {
        dirtyFramesUntilFullFrame = v;
    }

    public void clear(char fillC, byte fgCol, byte bgCol) {
        for (int i = 0; i < length; i++) {
            setCharacter(i, fillC);
            setFgColor(i, fgCol);
            setBgColor(i, bgCol);
        }
    }

    public void clear(int x, int y, int w, int h, char fillC, byte fgCol, byte bgCol) {
        for (int v = 0; v < h; v++) {
            for (int u = 0; u < w; u++) {
                int i = (y + v) * width + x + u;
                setCharacter(i, fillC);
                setFgColor(i, fgCol);
                setBgColor(i, bgCol);
            }
        }
    }

    public String outputClearScreen() {
        return ESC + "[2J";
    }

    public String outputHideCursor() {
        return ESC + "[?25l";
    }

    public String outputShowCursor() {
        return ESC + "[?25h";
    }

    public String output() {
        StringBuilder sb = new StringBuilder();

        outputResetColor(sb);

        if (fullFrameCountdown <= 0) {
            outputWholeBuffer(sb);
            fullFrameCountdown = dirtyFramesUntilFullFrame;
        } else {
            outputDirtyBuffer(sb);
            fullFrameCountdown--;
        }

        copyToBuffers();

        return sb.toString();
    }

    private void outputWholeBuffer(StringBuilder sb) {
        byte prevFg = TextColor.NO_COLOR;
        byte prevBg = TextColor.NO_COLOR;
        for (int line = 0; line < height; line++) {
            outputPlaceCursor(0, line, sb);
            for (int x = 0; x < width; x++) {
                int index = getValidIndex(x, line);
                byte fg = this.fgColors[index];
                byte bg = this.bgColors[index];
                boolean fgChanged = fg != prevFg;
                boolean bgChanged = bg != prevBg;
                if (fgChanged || bgChanged) {
                    outputColor(fgChanged ? fg : TextColor.NO_COLOR, bgChanged ? bg : TextColor.NO_COLOR, sb);
                }
                sb.append(this.characters[index]);
                prevFg = fg;
                prevBg = bg;
            }
        }
        outputResetColor(sb);
        if (showStats) {
            outputPlaceCursor(0, height, sb);
            sb.append("Full frame              ");
        }
        outputPlaceCursor(width, height, sb);
    }

    private void outputDirtyBuffer(StringBuilder sb) {
        byte prevFg = TextColor.NO_COLOR;
        byte prevBg = TextColor.NO_COLOR;
        int segments = 0;
        for (int line = 0; line < height; line++) {
            boolean wasDirty = false;
            for (int x = 0; x < width; x++) {
                int index = getValidIndex(x, line);
                if (isDirty(index)) {
                    if (!wasDirty) {
                        outputPlaceCursor(x, line, sb);
                        segments++;
                    }
                    // output
                    byte fg = this.fgColors[index];
                    byte bg = this.bgColors[index];
                    boolean fgChanged = fg != prevFg;
                    boolean bgChanged = bg != prevBg;
                    if (fgChanged || bgChanged) {
                        outputColor(fgChanged ? fg : TextColor.NO_COLOR, bgChanged ? bg : TextColor.NO_COLOR, sb);
                    }
                    sb.append(this.characters[index]);
                    prevFg = fg;
                    prevBg = bg;
                    wasDirty = true;
                } else {
                    wasDirty = false;
                    prevFg = TextColor.NO_COLOR;
                    prevBg = TextColor.NO_COLOR;
                }
            }
        }
        outputResetColor(sb);
        if (showStats) {
            outputPlaceCursor(0, height, sb);
            sb.append("Segments: ").append(segments).append("      ");
        }
        outputPlaceCursor(width, height, sb);
    }

    private void copyToBuffers() {
        System.arraycopy(characters, 0, charactersBuffer, 0, length);
        System.arraycopy(fgColors, 0, fgColorsBuffer, 0, length);
        System.arraycopy(bgColors, 0, bgColorsBuffer, 0, length);
    }

    private void outputResetColor(StringBuilder sb) {
        sb.append(ESC).append("[0m");
    }

    private void outputColor(byte fg, byte bg, StringBuilder sb) {
        sb.append(ESC).append('[');
        if (fg > TextColor.NO_COLOR) {
            sb.append(fg + 30);
            if (bg > TextColor.NO_COLOR) {
                sb.append(';');
            }
        }
        if (bg >= 0) {
            sb.append(bg + 40);
        }
        sb.append('m');
    }

    private void outputPlaceCursor(int x, int y, StringBuilder sb) {
        sb.append(ESC).append('[').append(y + 1).append(';').append(x + 1).append('H');
    }

    public void setCharacter(int x, int y, char c) {
        int index = getIndex(x, y);
        if (index >= 0 && index < length) {
            setCharacter(index, c);
        }
    }

    private void setCharacter(int index, char c) {
        characters[index] = c;
    }

    public void setFgColor(int x, int y, byte fg) {
        int index = getIndex(x, y);
        if (index >= 0 && index < length) {
            setFgColor(index, fg);
        }
    }

    private void setFgColor(int index, byte fg) {
        if (fg > TextColor.NO_COLOR) {
            fgColors[index] = fg;
        }
    }

    public void setBgColor(int x, int y, byte bg) {
        int index = getIndex(x, y);
        if (index >= 0 && index < length) {
            setBgColor(index, bg);
        }
    }

    public void setBgColor(int index, byte bg) {
        if (bg > TextColor.NO_COLOR) {
            bgColors[index] = bg;
        }
    }

    private boolean isDirty(int index) {
        return characters[index] != charactersBuffer[index]
                || fgColors[index] != fgColorsBuffer[index]
                || bgColors[index] != bgColorsBuffer[index];
    }

    public char getCharacter(int x, int y) {
        int index = getIndex(x, y);
        if (index < 0 || index >= length) {
            return ' ';
        }
        return charactersBuffer[index];
    }

    public byte getFgColor(int x, int y) {
        int index = getIndex(x, y);
        if (index < 0 || index >= length) {
            return 0; //?
        }
        return fgColorsBuffer[index];
    }

    public byte getBgColor(int x, int y) {
        int index = getIndex(x, y);
        if (index < 0 || index >= length) {
            return 0; //?
        }
        return bgColorsBuffer[index];
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

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
