package com.amd.aparapi.sample.common;

/**
 * Created with IntelliJ IDEA.
 * User: garyfrost
 * Date: 10/27/13
 * Time: 10:23 AM
 * To change this template use File | Settings | File Templates.
 */
//http://www.coderanch.com/t/342909/GUI/java/Quick-drawing-LED-style-text

import java.util.Arrays;

public class FPSCounter extends LEDWidget {

    private int frames = 0;

    private final  int windowLen;
    private final long[] window;

    public FPSCounter(int _windowLen){
        windowLen = 20;
        window = new long[windowLen];
    }

    public void start() {
        long startTime = System.currentTimeMillis();
        Arrays.fill(window, startTime);
    }

    ;

    public void nextFrame() {
        frames++;
        long now = window[frames % windowLen] = System.currentTimeMillis();
        long then = window[(frames + 1) % windowLen];

        float elapsedSecs = (now - then) / 1000f;
        float fps = ((frames >= windowLen) ? (windowLen - 1) : frames) / elapsedSecs;
        setMilliValue((int) (fps * 1000));

    }


}

