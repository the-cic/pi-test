/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush;

import com.mush.textscreen.TextScreenBuffer;
import com.mush.textscreen.TextSprite;

/**
 *
 * @author cic
 */
public class Fish {

    public double x;
    public double y;
    public double vx;
    public double vy;
    public int wait;

    private TextSprite[] sprites;

    private TextScreenBuffer buffer;

    private int minX = 10;
    private int minY = 5;
    private int maxX;
    private int maxY;

    public Fish(TextScreenBuffer buffer, TextSprite sl, TextSprite sm, TextSprite sr) {
        this.sprites = new TextSprite[]{sl, sm, sr};
        this.buffer = buffer;
        maxX = buffer.getWidth() - minX;
        maxY = buffer.getHeight() - minY;

        x = minX + Math.random() * (maxX - minX);
        y = minY + Math.random() * (maxY - minY) * 0.5;

        vx = 1;
        changeSpeed();
        if (Math.random() < 0.5) {
            turn();
        }
        wait = 0;
    }

    public void changeSpeed() {
        vx = (0.3 + Math.random() * 1.5) * 0.3 * Math.signum(vx);
        vy = (Math.random() * 1.0 - 0.5) * 0.1;
    }

    public void turn() {
        vx = -vx;
        vy = -vy;
        wait = 5;
    }

    public int getDirection() {
        return wait > 0
                ? 0
                : (vx < 0 ? -1 : 1);
    }

    public TextSprite getSprite() {
        return sprites[getDirection() + 1];
    }

    public void update() {
        if (wait > 0) {
            wait--;
            return;
        }
        x += vx;
        y += vy;

        if (x < minX && vx < 0) {
            turn();
            return;
        }
        if (x > maxX && vx > 0) {
            turn();
            return;
        }

        TextSprite sprite = getSprite();
        int dir = getDirection();

        int ix = (int) x + (sprite.getXOffset() + 1) * dir;
        int iy = (int) y;

        int c1 = buffer.getCharacter(ix, iy - 1);
        int c2 = buffer.getCharacter(ix, iy);
        int c3 = buffer.getCharacter(ix, iy + 1);

        if (c1 != '.' || c2 != '.' || c3 != '.') {
            turn();
            vy -= (y - (maxY - minY)*0.5) * 0.001;
            return;
        }

        if (Math.random() < 0.03) {
            changeSpeed();
        }
        if (Math.random() < 0.005) {
            turn();
        }
        if (y < minY) {
            y = minY;
        }
        if (y > maxY) {
            y = maxY;
        }
    }

}
