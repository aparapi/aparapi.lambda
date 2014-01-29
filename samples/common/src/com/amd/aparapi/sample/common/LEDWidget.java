package com.amd.aparapi.sample.common;

/**
 * Created with IntelliJ IDEA.
 * User: garyfrost
 * Date: 10/27/13
 * Time: 10:23 AM
 * To change this template use File | Settings | File Templates.
 */
//http://www.coderanch.com/t/342909/GUI/java/Quick-drawing-LED-style-text

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

public class LEDWidget {
    JPanel panel;


    enum SEGMENT {
        TOP(1), UPL(2), UPR(4), MID(8), LOL(16), LOR(32), LOW(64);

        SEGMENT(int _bits) {
            bits = _bits;
        }

        int bits;
    }

    /**
     * TOP
     * UPL   UPR
     * MID
     * LOL   LOR
     * LOW
     */
    enum DIGIT {

        ZERO(SEGMENT.TOP, SEGMENT.UPL, SEGMENT.UPR, SEGMENT.LOL, SEGMENT.LOR, SEGMENT.LOW),

        ONE(SEGMENT.UPR, SEGMENT.LOR),
        TWO(SEGMENT.TOP, SEGMENT.UPR, SEGMENT.MID, SEGMENT.LOL, SEGMENT.LOW),
        THREE(SEGMENT.TOP, SEGMENT.UPR, SEGMENT.MID, SEGMENT.LOR, SEGMENT.LOW),
        FOUR(SEGMENT.LOR, SEGMENT.MID, SEGMENT.UPR, SEGMENT.UPL),
        FIVE(SEGMENT.TOP, SEGMENT.UPL, SEGMENT.MID, SEGMENT.LOR, SEGMENT.LOW),
        SIX(SEGMENT.TOP, SEGMENT.UPL, SEGMENT.MID, SEGMENT.LOL, SEGMENT.LOR, SEGMENT.LOW),
        SEVEN(SEGMENT.TOP, SEGMENT.UPR, SEGMENT.LOR),
        EIGHT(SEGMENT.TOP, SEGMENT.UPL, SEGMENT.UPR, SEGMENT.MID, SEGMENT.LOL, SEGMENT.LOR, SEGMENT.LOW),
        NINE(SEGMENT.TOP, SEGMENT.UPL, SEGMENT.UPR, SEGMENT.MID, SEGMENT.LOR, SEGMENT.LOW),
        EMPTY();

        DIGIT(SEGMENT... _segments) {
            segments = _segments;
        }

        SEGMENT[] segments;

        static DIGIT get(int value, boolean emptyIfZero) {
            if (value == 0 && emptyIfZero) {
                return (EMPTY);
            } else {
                return (values()[value]);
            }
        }
    }

    public void setMilliValue(int _milliValue) {

        milliValue = _milliValue;
        panel.updateUI();
    }

    public LEDWidget() {
        panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                render(g);
            }
        };
        panel.setBackground(Color.BLACK);
        panel.setPreferredSize(new Dimension(250, 80));


        milliValue = 0;

    }

    protected int milliValue = 0;


    public Container getContainer() {
        return (panel);
    }

    Color color = Color.GREEN;

    public void setColor(Color _color) {
        color = _color;
    }


    public void render(Graphics g) {

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double
                width = panel.getWidth(),
                height = panel.getHeight(),
                cut = 4,
                vertMargin = .08 * height,
                glyphWidth = .100 * width,
                horzPad = glyphWidth / 2,
                glyphHeight = .375 * height;

        double
                left = glyphWidth,
                right = left + glyphWidth,
                top = vertMargin,
                mid = top + cut + glyphHeight,
                bot = mid + cut + glyphHeight;
        int fudge = 12;
        g2.setColor(color);
        Stroke s = new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
        g2.setStroke(s);
        g2.draw(new Line2D.Double(left + 4 * glyphWidth + fudge + cut / 2, top + glyphHeight / 2, left + 4 * glyphWidth + fudge + cut / 2, top + glyphHeight / 2));
        g2.draw(new Line2D.Double(left + 4 * glyphWidth + fudge + cut / 2, top + glyphHeight * 7 / 4, left + 4 * glyphWidth + fudge + cut / 2, top + glyphHeight * 7 / 4));

        int hundreds = (milliValue / 100000) % 10;
        int tens = (milliValue / 10000) % 10;
        int units = (milliValue / 1000) % 10;
        int tenths = (milliValue / 100) % 10;
        int hundredths = (milliValue / 10) % 10;
        DIGIT digits[] = new DIGIT[]{
                DIGIT.get(hundreds, true),
                DIGIT.get(tens, hundreds == 0),
                DIGIT.get(units, hundreds == 0 && tens == 0),
                DIGIT.get(tenths, false),
                DIGIT.get(hundredths, false),

        };


        for (int j = 0; j < 5; j++) {
            DIGIT digit = digits[j];
            if (j == 3) {
                left += glyphWidth - horzPad;
                right += glyphWidth - horzPad;
            }
            for (SEGMENT seg : digit.segments) {
                switch (seg) {
                    case TOP:
                        g2.draw(new Line2D.Double(left + cut, top, left + glyphWidth, top));
                        break;
                    case UPL:
                        g2.draw(new Line2D.Double(left, top + cut, left, top + glyphHeight));
                        break;
                    case UPR:
                        g2.draw(new Line2D.Double(right + cut, top + cut, right + cut, top + glyphHeight));
                        break;
                    case MID:
                        g2.draw(new Line2D.Double(left + cut, mid, left + glyphWidth, mid));
                        break;
                    case LOL:
                        g2.draw(new Line2D.Double(left, mid + cut, left, mid + glyphHeight));
                        break;
                    case LOR:
                        g2.draw(new Line2D.Double(right + cut, mid + cut, right + cut, mid + glyphHeight));
                        break;
                    case LOW:
                        g2.draw(new Line2D.Double(left + cut, bot, left + glyphWidth, bot));
                        break;
                }
            }


            left += horzPad + glyphWidth;
            right += horzPad + glyphWidth;
        }
        panel.updateUI();
    }


}

