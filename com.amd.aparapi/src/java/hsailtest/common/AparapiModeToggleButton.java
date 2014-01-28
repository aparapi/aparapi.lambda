package hsailtest.common;

import com.amd.aparapi.Device;

/**
 * Created with IntelliJ IDEA.
 * User: user1
 * Date: 11/5/13
 * Time: 12:42 PM
 * To change this template use File | Settings | File Templates.
 */
public  class AparapiModeToggleButton extends ModeToggleButton {

    static Device seq = Device.seq(); // must be static because setMode below is called from super constructor
    static Device jtp = Device.jtp();
    static Device hsa = Device.hsa();
    Device device;

    @Override public void setMode(Mode _mode){
        super.setMode(_mode);
        if (getMode().equals(Mode.SingleCore)){
            device = seq;
           // System.out.println(this+"device is seq"+seq);
        }else if (getMode().equals(Mode.MultiCore)){
           // System.out.println(this+"device is jtp"+jtp);
            device = jtp;
        } else{
            //System.out.println(this+"device is hsa"+hsa);
            device = hsa;
        }
    }


    public AparapiModeToggleButton(int _size, Mode _initialMode) {
        super(_size, _initialMode);

    }

    @Override protected boolean gpuIsAnOption(){
        return(hsa != null);
    }


    public Device getDevice(){
     //  System.out.println(this+"returning device "+device);
       return(device);
    }


}

