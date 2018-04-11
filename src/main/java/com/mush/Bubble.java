/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush;

/**
 *
 * @author cic
 */
public class Bubble {

    public int x;
    public int x0;
    public int y;
    public int y0;
    public double vy;
    private double fy;
    public char c;

    public Bubble(int x, int y) {
        this.x0 = x;
        this.y0 = y;
        this.x = this.x0;
        this.y = this.y0;
        this.fy = this.y;
        this.vy = 0.5 + Math.random() * 1;
        this.c = Math.random() < 0.5 ? 'O' : 'o';
    }

    public void update() {
        if (y < 0) {
            y = y0;
            x = x0;
            fy = y;
        } else {
            fy -= vy;
            y = (int)fy;
            if (Math.random() < 0.1) {
                x += Math.random() < 0.5 ? -1 : 1;
            }
        }
    }

}
