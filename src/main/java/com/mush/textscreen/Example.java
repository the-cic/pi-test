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
public class Example implements Runnable {

    private TextScreenBuffer buffer;
    private TextDraw draw;
    private TextSprite sprite;
    private double fi = 0;
    private double avgFps = 0;

    public Example() {
        buffer = new TextScreenBuffer();
        buffer.init(160, 45);

        draw = new TextDraw(buffer);

        sprite = new TextSprite(5, 5)
                .setLine("+-.-+", "MMYMM", TextColor.YELLOW)
                .setLine("|---|", "MGGGM")
                .setLine(" :-) ", TextColor.GREEN)
                .setLine("|---|", "mgggm")
                .setLine("+- -+", "MMMMMM", "Y   Y");
        sprite.setOffset(2, 2);

        System.out.print(buffer.outputClearScreen());
    }

    private void drawBg() {
        drawSmiley(0, 0);

        fi += 0.01;
        int dx = (int) (Math.cos(fi) * 10);
        int dy = (int) (Math.sin(fi) * 10);
        drawSmiley(40 + dx, 10 + dy);

        drawSmiley(50, 30);
        drawSmiley(70, 5);
        drawSmiley(90, 20);

        sprite.draw(50, 10, buffer);
        sprite.draw(90, 20, buffer);
    }

    private void drawSmiley(int x, int y) {
        draw.line(x + 6, y + 7, 8, 0, 'O', TextColor.WHITE);
        draw.line(x + 4, y + 5, 2, 2, 'O', TextColor.WHITE);
        draw.line(x + 15, y + 5, -2, 2, 'O', TextColor.WHITE);

        draw.line(x + 7, y + 3, 0, 2, 'O', TextColor.WHITE);
        draw.line(x + 12, y + 3, 0, 2, 'O', TextColor.WHITE);
    }

    @Override
    public void run() {
        boolean loop = true;
        long targetFps = 12;
        long targetMillisPerFrame = 1000 / targetFps;

        int x = 0;
        int y = 0;
        int dx = 1;
        int dy = 1;
        int count = 0;

        buffer.setDirtyFramesUntilFullFrame((int) (targetFps * 15));
        long lastTime = System.currentTimeMillis();

        while (loop) {
            long time0 = System.currentTimeMillis();

            buffer.clear('.', TextColor.BLUE, TextColor.BLUE);

            drawBg();

            boolean hit = false;

            char c = buffer.getCharacter(x + dx, y + dy);
            if (c == '.') {
                x += dx;
                y += dy;
            } else {
                hit = true;
                char cx = buffer.getCharacter(x - dx, y + dy);
                char cy = buffer.getCharacter(x + dx, y - dy);
                if (cx == '.') {
                    dx = -dx;
                } else if (cy == '.') {
                    dy = -dy;
                } else {
                    dx = -dx;
                    dy = -dy;
                }
            }

            draw.text(x, y, ":)", hit ? TextColor.RED : TextColor.GREEN, TextColor.YELLOW);

            sprite.draw(x + 5, y, buffer);

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
            System.out.println("x: " + x + " y:" + y + "              ");
            System.out.println("dx: " + dx + " dy:" + dy + "      ");
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
