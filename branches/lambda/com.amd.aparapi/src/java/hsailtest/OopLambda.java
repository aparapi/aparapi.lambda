package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.util.function.IntConsumer;


public class OopLambda {
    public static class P {
       private int x;
       private  int y;


       int getX(){
          return(x);
       }
       int getY(){
          return(y);
       }

       void setX(int _x){
          x = _x;
       }
       void setY(int _y){
          y = _y;
       }


       @Override
        public String toString() {
            return ("(" + x + ", " + y + ")");
        }
    }

    static void dump(String type, P[] points) {
        System.out.print(type + " ->");
        for (int i = 0; i < points.length; i++) {
            if (i != 0) {
                System.out.print(", ");
            }
            System.out.print(points[i]);
        }
        System.out.println();
    }


    public static void main(String[] args) throws AparapiException {
        int len = 10;
        P[] points = new P[len];

        for (int i = 0; i < len; i++) {
            points[i] = new P();
        }

        IntConsumer ic = gid -> {
            points[gid].setX(gid);
            points[gid].setY(gid * 2);
        };

        Device.hsa().forEach(len, ic);
        dump("hsa", points);
        Device.jtp().forEach(len, ic);
        dump("jtp", points);
        Device.seq().forEach(len, ic);
        dump("seq", points);
    }
}
