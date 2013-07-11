package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.util.function.IntConsumer;


public class OopLambda {
    public static class P {
        int x;
        int y;
         int xy;

        int getXY(){
           return(x*y);
        }

        P(int _x, int _y) {
            x = _x;
            y = _y;
            xy=0;
        }

        @Override
        public String toString() {
            return ("(" + x + ", " + y + ", "+xy+ ")");
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
            points[i] = new P(0, 0);
        }

        IntConsumer ic = gid -> {
            points[gid].x = gid;
            points[gid].y = gid * 2;
            points[gid].xy = points[gid].getXY();
        };

        Device.hsa().forEach(len, ic);
        dump("hsa", points);
        Device.jtp().forEach(len, ic);
        dump("jtp", points);
        Device.seq().forEach(len, ic);
        dump("seq", points);
    }
}
