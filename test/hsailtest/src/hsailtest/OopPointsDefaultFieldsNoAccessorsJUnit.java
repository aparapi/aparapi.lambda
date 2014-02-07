package hsailtest;

import com.amd.aparapi.Device;
import org.junit.Test;

import java.util.function.IntConsumer;

import static junit.framework.Assert.assertTrue;


public class OopPointsDefaultFieldsNoAccessorsJUnit {
    public static class P {
       int x;
        int y;

       void clear(){
           x=y=0;
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


    @Test
    public void test() {

        int len = 12;
        P[] points = new P[len];

        for (int i = 0; i < len; i++) {
            points[i] = new P();
        }

        IntConsumer ic = gid -> {
            P p = points[gid];

            p.x=gid;
            p.y = gid*2;

        };

        Device.hsa().forEach(len, ic);
        dump("hsa", points);
        P[] hsaPoints = new P[points.length];
        for (int i=0; i<points.length; i++){
            hsaPoints[i]=points[i];
            points[i]=new P();
        }
        Device.jtp().forEach(len, i-> points[i].clear());
        Device.jtp().forEach(len, ic);
        dump("jtp", points);

        for (int i=0; i<points.length; i++){
            assertTrue("hsaPoint["+i+"]==points["+i+"]",
                    hsaPoints[i].x==points[i].x &&
                    hsaPoints[i].y==points[i].y );

        }

    }
}
