/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mush;

import com.mush.textscreen.ConfigSprites;
import com.mush.textscreen.TextColor;
import com.mush.textscreen.TextDraw;
import com.mush.textscreen.TextScreenBuffer;

/**
 *
 * @author Mirko Stancic, Dhimahi
 */
public class Example implements Runnable {

    private TextScreenBuffer buffer;
    private TextDraw draw;
    ConfigSprites sprites;
    private double fi = 0;
    private double avgFps = 0;

    public Example() {
        buffer = new TextScreenBuffer();
        buffer.init(160, 45);

        draw = new TextDraw(buffer);

        sprites = new ConfigSprites();
        sprites.load("sprites.properties");

        System.out.print(buffer.outputClearScreen());
    }

    private void drawBg() {
        draw.line(0, 35, 40, 2, '~', TextColor.YELLOW);
        draw.line(40, 37, 50, -3, '~', TextColor.YELLOW);
        draw.line(90, 34, 10, 0, '~', TextColor.YELLOW);
        draw.line(100, 34, 60, 4, '~', TextColor.YELLOW);
        
        sprites.get("castle").draw(30, 30, buffer);
        
//        buffer.clear(0, 35, 160, 10, '#', TextColor.GREEN, TextColor.YELLOW);
//        sprites.get("big-face").draw(0 + 4, 0 + 3, buffer);

//        fi += 0.05;
//        int dx = (int) (Math.cos(fi) * 10);
//        int dy = (int) (Math.sin(fi) * 10);
//        sprites.get("big-face").draw(40 + dx, 10 + dy, buffer);
//
//        sprites.get("big-face").draw(50, 30, buffer);
//        sprites.get("big-face").draw(70, 5, buffer);
//        sprites.get("big-face").draw(90, 20, buffer);

//        sprites.get("face-box").draw(50, 10, buffer);
//        sprites.get("face-box").draw(90, 20, buffer);


        sprites.get("color-box").draw(80, 0, buffer);
    }
    
    private void drawFg() {
        sprites.get("chest").draw(100, 38, buffer);
    }
    
    /*
    private void drawSmiley(int x, int y) {
        draw.line(x + 6, y + 7, 8, 0, 'O', TextColor.WHITE);
        draw.line(x + 4, y + 5, 2, 2, 'O', TextColor.WHITE);
        draw.line(x + 15, y + 5, -2, 2, 'O', TextColor.WHITE);

        draw.line(x + 7, y + 3, 0, 2, 'O', TextColor.WHITE);
        draw.line(x + 12, y + 3, 0, 2, 'O', TextColor.WHITE);
    }
     */
    @Override
    public void run() {
        boolean loop = true;
        long targetFps = 30;
        long targetMillisPerFrame = 1000 / targetFps;

//        int x = 0;
//        int y = 0;
//        int dx = 1;
//        int dy = 1;
        int count = 0;

        int fishCount = 20;
        Fish[] fishes = new Fish[fishCount];
        for (int i = 0; i < fishCount; i++) {
            fishes[i] = new Fish();
        }
        
        int bubbleCount = 30;
        Bubble[] bubbles = new Bubble[bubbleCount];
        for (int i = 0; i < bubbleCount; i++) {
            bubbles[i] = new Bubble((int) (103 + Math.random() * 3), 38);
        }

        buffer.setDirtyFramesUntilFullFrame((int) (targetFps * 15));
        long lastTime = System.currentTimeMillis();

        while (loop) {
            long time0 = System.currentTimeMillis();

            buffer.clear('.', TextColor.BLUE, TextColor.BLACK);

            drawBg();

//            boolean hit = false;
//            char c = buffer.getCharacter(x + dx, y + dy);
//            if (c == '.') {
//                x += dx;
//                y += dy;
//            } else {
//                hit = true;
//                char cx = buffer.getCharacter(x - dx, y + dy);
//                char cy = buffer.getCharacter(x + dx, y - dy);
//                if (cx == '.') {
//                    dx = -dx;
//                } else if (cy == '.') {
//                    dy = -dy;
//                } else {
//                    dx = -dx;
//                    dy = -dy;
//                }
//            }
            // draw.text(x, y, ":)", hit ? TextColor.RED : TextColor.GREEN, TextColor.YELLOW);
//            if (hit) { 
//                sprites.get("fish-mid-hit").draw(x+2 , y, buffer);
//            } else {
//                sprites.get("fish-mid").draw(x+2, y, buffer);
//            }
            for (Fish fish : fishes) {
                fish.update();
                String name = fish.dx == 0
                        ? "fish-mid-front"
                        : (fish.dx > 0
                                ? "fish-mid-right"
                                : "fish-mid-left");
                
                double inf = 1 - fish.f;
                sprites.get(name).draw((int)(fish.x * fish.f + inf * 80), (int)(fish.y * fish.f + inf * 20), buffer);
            }
            
            drawFg();
            
            for (Bubble bubble : bubbles) {
                bubble.update();
                buffer.setCharacter(bubble.x, bubble.y, bubble.c);
                buffer.setFgColor(bubble.x, bubble.y, TextColor.WHITE);
            }

            System.out.print(buffer.output());

            long time = System.currentTimeMillis();
            long delay = time - time0;
            long frameDelay = time - lastTime;
            lastTime = time;
            if (delay < 0) {
                delay = 0;
            }

            long wait = targetMillisPerFrame - delay;
            if (wait < 0) {
                wait = 0;
            }
//            long fps = 1000 / (delay + wait);
            if (frameDelay == 0) {
                frameDelay = 1;
            }
            long fps = 1000 / (frameDelay);
            avgFps = avgFps * 0.9 + fps * 0.1;

            System.out.println("\nFps: " + Math.round(avgFps) + "      ");
//            System.out.println("x: " + x + " y:" + y + "              ");
//            System.out.println("dx: " + dx + " dy:" + dy + "      ");
            System.out.println("Frame count: " + count + "      ");
            System.out.println("Delay: " + delay + " ms      ");
            System.out.println("Wait: " + wait + " ms      ");

            try {
                Thread.sleep(wait);
            } catch (InterruptedException ex) {
                loop = false;
            }

            count++;
        }
        System.out.println("\nFinished");
    }
}
