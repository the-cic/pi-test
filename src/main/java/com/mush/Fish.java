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
public class Fish {
    
    public int x;
    public int y;
    public int dx;
    public int nextDx;
    public int wait;
    public double f;
    
    public Fish() {
        x = 10 + (int) (Math.random() * 100);
        y = 10 + (int) (Math.random() * 30);
        dx = Math.random() < 0.5 ? 1 : -1;
        nextDx = 0;
        wait = 0;
        f = Math.random() * 0.5 + 0.5;
    }
    
    public void update() {
        if (wait > 0) {
            wait--;
            return;
        }
        x += dx;
        if (dx == 0) {
            dx = nextDx;
        }
        if (Math.random() < 0.03) {
            y += Math.random() < 0.5 ? 1 : -1;
        }
        if (dx != 0 && Math.random() < 0.01) {
            nextDx = -dx;
            dx = 0;
            wait = 3;
        }
        if (x < 10) {
            if (dx < 0) {
                dx = 0;
                wait = 3;
                nextDx = 1;
            }
        }
        if (x > 150) {
            if (dx > 0) {
                dx = 0;
                wait = 3;
                nextDx = -1;
            }
        }
        if (y < 5) {
            y = 5;
        }
        if (y > 40) {
            y = 40;
        }
    }
    
}
