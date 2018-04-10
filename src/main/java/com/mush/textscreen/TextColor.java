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
public class TextColor {

    public static final byte NO_COLOR = -1;

    public static final byte BLACK = 0;
    public static final byte RED = 1;
    public static final byte GREEN = 2;
    public static final byte YELLOW = 3;
    public static final byte BLUE = 4;
    public static final byte MAGENTA = 5;
    public static final byte CYAN = 6;
    public static final byte WHITE = 7;

    private static final char C_BLACK = 'K';
    private static final char C_RED = 'R';
    private static final char C_GREEN = 'G';
    private static final char C_YELLOW = 'Y';
    private static final char C_BLUE = 'B';
    private static final char C_MAGENTA = 'M';
    private static final char C_CYAN = 'C';
    private static final char C_WHITE = 'W';

    public static byte colorFromChar(char c) {
        byte color;
        switch (Character.toUpperCase(c)) {
            case C_BLACK:
                color = BLACK;
                break;
            case C_RED:
                color = RED;
                break;
            case C_GREEN:
                color = GREEN;
                break;
            case C_YELLOW:
                color = YELLOW;
                break;
            case C_BLUE:
                color = BLUE;
                break;
            case C_MAGENTA:
                color = MAGENTA;
                break;
            case C_CYAN:
                color = CYAN;
                break;
            case C_WHITE:
                color = WHITE;
                break;
            default:
                color = NO_COLOR;
        }
        return color;
    }

    public static char charForColor(byte col) {
        char c;
        switch (col) {
            case BLACK:
                c = C_BLACK;
                break;
            case RED:
                c = C_RED;
                break;
            case GREEN:
                c = C_GREEN;
                break;
            case YELLOW:
                c = C_YELLOW;
                break;
            case BLUE:
                c = C_BLUE;
                break;
            case MAGENTA:
                c = C_MAGENTA;
                break;
            case CYAN:
                c = C_CYAN;
                break;
            case WHITE:
                c = C_WHITE;
                break;
            default:
                c = ' ';
        }
        return c;
    }

}
