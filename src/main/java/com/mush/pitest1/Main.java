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
public class Main implements Runnable {

    TextScreenBuffer buffer;
    private double fi = 0;
    private double avgFps = 0;

    public Main() {
        buffer = new TextScreenBuffer();
        buffer.init(160, 45);

        System.out.print(buffer.outputClearScreen());
    }

    private void drawLine(int x, int y, int dx, int dy, char c, byte fgCol) {
        int steps = (int) Math.sqrt(dx * dx + dy * dy);
        for (int i = 0; i < steps; i++) {
            double f = (double) i / steps;
            int u = (int) (x + dx * f);
            int v = (int) (y + dy * f);
            if (fgCol >= 0) {
                buffer.setFgColor(u, v, fgCol);
            }
            buffer.setCharacter(u, v, c);
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        new Thread(main).start();
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
    }
    
    private void drawSmiley(int x, int y) {
        drawLine(x + 6, y + 7, 8, 0, 'O', TextScreenBuffer.WHITE);
        drawLine(x + 4, y + 5, 2, 2, 'O', TextScreenBuffer.WHITE);
        drawLine(x + 15, y + 5, -2, 2, 'O', TextScreenBuffer.WHITE);

        drawLine(x + 7, y + 3, 0, 2, 'O', TextScreenBuffer.WHITE);
        drawLine(x + 12, y + 3, 0, 2, 'O', TextScreenBuffer.WHITE);
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

            buffer.clear('.', TextScreenBuffer.BLUE, TextScreenBuffer.BLUE);

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

            buffer.write(x, y, ":)", hit ? TextScreenBuffer.RED : TextScreenBuffer.GREEN, (byte) -1);

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
