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
import com.mush.textscreen.TextSprite;

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
    boolean loop = false;

    public Example() {
        int width = jline.TerminalFactory.get().getWidth() - 0;
        int height = jline.TerminalFactory.get().getHeight() - 0;

        width = Math.max(20, width);
        height = Math.max(10, height);

        width = Math.min(160, width);
        height = Math.min(50, height);

        buffer = new TextScreenBuffer();

        buffer.setShowStats(false);

        if (buffer.getShowStats()) {
            height -= 5; // status lines
        }

        buffer.init(width, height);

        draw = new TextDraw(buffer);

        sprites = new ConfigSprites();
        sprites.load("sprites.properties");

        System.out.print(buffer.outputClearScreen());
        System.out.print(buffer.outputHideCursor());
    }

    public void onClose() {
        loop = false;
        System.out.print(buffer.outputShowCursor());
        System.out.println("\nkthxbye!");
    }

    private void drawBg() {

        int groundY = (int)(0.66 * buffer.getHeight());

        draw.line(0, groundY + 5, 40, 2, '~', TextColor.YELLOW);
        draw.line(40, groundY +  7, 50, -3, '~', TextColor.YELLOW);
        draw.line(90, groundY + 4, 10, 0, '~', TextColor.YELLOW);
        draw.line(100, groundY + 4, buffer.getWidth() - 100, 4, '~', TextColor.YELLOW);

        TextSprite castle = sprites.get("castle");
        castle.draw((int)(0.18 * buffer.getWidth()), groundY + 9 - castle.getHeight(), buffer);

        sprites.get("color-box").draw(buffer.getWidth() / 2, 0, buffer);
    }

    private void drawFg() {
        TextSprite chest = sprites.get("chest");
        chest.draw((int)(0.625 * buffer.getWidth()), (int)(buffer.getHeight() * 38.0/45.0), buffer);
    }

    @Override
    public void run() {
        loop = true;
        long targetFps = 30;
        long targetMillisPerFrame = 1000 / targetFps;

        int count = 0;

        int fishCount = 20;
        Fish[] fishes = new Fish[fishCount];
        for (int i = 0; i < fishCount; i++) {
            fishes[i] = new Fish();
        }

        int bubbleCount = 30;
        int bubbleX = (int)(0.625 * buffer.getWidth()) + 3;
        int bubbleY = (int)(buffer.getHeight() * 38.0/45.0);
        Bubble[] bubbles = new Bubble[bubbleCount];
        for (int i = 0; i < bubbleCount; i++) {
            bubbles[i] = new Bubble((int) (bubbleX + Math.random() * 3), bubbleY);
        }

        buffer.setDirtyFramesUntilFullFrame((int) (targetFps * 15));
        long lastTime = System.currentTimeMillis();

        while (loop) {
            long time0 = System.currentTimeMillis();

            buffer.clear('.', TextColor.BLUE, TextColor.BLACK);

            drawBg();

            for (Fish fish : fishes) {
                fish.update();
                String name = fish.dx == 0
                        ? "fish-mid-front"
                        : (fish.dx > 0
                                ? "fish-mid-right"
                                : "fish-mid-left");

                double inf = 1 - fish.f;
                sprites.get(name).draw((int) (fish.x * fish.f + inf * 80), (int) (fish.y * fish.f + inf * 20), buffer);
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

            if (frameDelay == 0) {
                frameDelay = 1;
            }
            long fps = 1000 / (frameDelay);
            avgFps = avgFps * 0.9 + fps * 0.1;

            if (buffer.getShowStats()) {
                System.out.println("\nFps: " + Math.round(avgFps) + "      ");
                System.out.println("Frame count: " + count + "      ");
                System.out.println("Delay: " + delay + " ms      ");
                System.out.print("Wait: " + wait + " ms      ");
            }

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
