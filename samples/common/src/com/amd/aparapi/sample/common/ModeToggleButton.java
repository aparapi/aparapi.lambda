package com.amd.aparapi.sample.common;


import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: user1
 * Date: 11/5/13
 * Time: 12:42 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ModeToggleButton extends JButton {

    public enum Mode{
        SingleCore,
        MultiCore,
        GPU;
     //   public boolean isParallel(){
      //     return(this.equals(Mode.GPU) || this.equals(Mode.MultiCore));
       // }
    };


    private Mode mode;

    private Font font;

    public void setMode(Mode _mode){
       mode = _mode;
    }

    public ModeToggleButton( int _size, Mode _initialMode) {
        setIcon( IconManager.resize(IconManager.chipIcon, _size, _size));
        Font oldFont = getFont();
        font = new Font(oldFont.getName(), Font.BOLD, _size/6);
        setMode(_initialMode);
        addActionListener(e -> {
            next();
            System.out.println("mode = "+getMode());
        });
    }

    protected abstract boolean gpuIsAnOption();

    public void  next(){
        if (getMode().equals(Mode.SingleCore)){
            setMode(Mode.MultiCore);

        }else  if (getMode().equals(Mode.MultiCore)){
            if (gpuIsAnOption()){
                setMode(Mode.GPU);

            }else{
                setMode(Mode.SingleCore);
            }
        } else{               // GPU!
            setMode(Mode.SingleCore);
        }
    }

    @Override
    public void paint(Graphics _g) {
        super.paint(_g);

        Graphics2D g = (Graphics2D) _g;
        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g.setFont(font);
        String type;
        Color color;

        int cx = getWidth()/ 2;
        int cy = getHeight() / 2 + getHeight()/10;


        int cpuCoreBoxDim = getWidth()/8;
        int cpuCoreLeft = cx-(3*cpuCoreBoxDim)/2;
        int cpuCoreGutter = cpuCoreBoxDim/10;
        int cpuCoreTop = cy-(8*cpuCoreBoxDim)/5;

        boolean first = true;

        for (int x:new int[]{cpuCoreLeft, cpuCoreLeft+cpuCoreBoxDim+cpuCoreGutter}){
            for (int y:new int[]{cpuCoreTop, cpuCoreTop+cpuCoreBoxDim+cpuCoreGutter}){
                if (mode.equals(Mode.MultiCore) || ( mode.equals(Mode.SingleCore) && first)){
                    g.setColor(Color.GREEN.darker());
                    g.fillRoundRect(x-cpuCoreBoxDim/2,y-cpuCoreBoxDim/2,cpuCoreBoxDim,cpuCoreBoxDim,cpuCoreBoxDim/3,cpuCoreBoxDim/3);
                    first = false;
                }
                g.setColor(Color.ORANGE.darker());
                g.drawRoundRect(x-cpuCoreBoxDim/2,y-cpuCoreBoxDim/2,cpuCoreBoxDim,cpuCoreBoxDim,cpuCoreBoxDim/3,cpuCoreBoxDim/3);
            }
        }


        int gpuCoreBoxDim = getWidth()/12;
        int gpuCoreLeft = cx+(3*gpuCoreBoxDim)/2;
        int gpuCoreGutter = gpuCoreBoxDim/10;
        int gpuCoreTop = cy-(3*gpuCoreBoxDim);
        int gpuPitch = gpuCoreBoxDim+gpuCoreGutter;
        for (int x:new int[]{gpuCoreLeft, gpuCoreLeft+gpuCoreBoxDim+gpuCoreGutter}){
            for (int y:new int[]{gpuCoreTop, gpuCoreTop+gpuPitch,gpuCoreTop+gpuPitch*2,gpuCoreTop+gpuPitch*3 }){
                if (mode.equals(Mode.GPU)){
                    g.setColor(Color.GREEN.darker());
                    g.fillRoundRect(x-gpuCoreBoxDim/2,y-gpuCoreBoxDim/2,gpuCoreBoxDim,gpuCoreBoxDim,gpuCoreBoxDim/3,gpuCoreBoxDim/3);
                }
                g.setColor(Color.ORANGE.darker());
                g.drawRoundRect(x-gpuCoreBoxDim/2,y-gpuCoreBoxDim/2,gpuCoreBoxDim,gpuCoreBoxDim,gpuCoreBoxDim/3,gpuCoreBoxDim/3);
            }
        }
        if (mode.equals(Mode.GPU)) {
            type = "GPU";
            g.setColor(Color.GREEN);

        } else {
            type = "CPU";
            g.setColor(Color.YELLOW);
        }

        FontMetrics fm = g.getFontMetrics();
        g.drawString(type, cx - (fm.stringWidth(type)/2), cy +cpuCoreBoxDim+ ((fm.getDescent() + fm.getAscent()) / 2));


    }



    public Mode getMode(){
        return(mode);
    }
}

