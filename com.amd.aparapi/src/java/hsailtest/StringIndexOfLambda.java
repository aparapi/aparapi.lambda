package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.Device;

import java.util.Arrays;
import java.util.function.IntConsumer;


public class StringIndexOfLambda {


    static void dump(String type, String[] _strings, int[] indices) {
        System.out.print(type + " ->");
        for (int i = 0; i < _strings.length; i++) {
            if (i != 0) {
                System.out.print(", ");
            }
            System.out.print(_strings[i]+(indices[i]>-1?"*":"?")+"="+indices[i]);
        }
        System.out.println();
    }


    static int indexOf(char[] source, int sourceOffset, int sourceCount,
                       char[] target, int targetOffset, int targetCount,
                       int fromIndex) {
     //   if (fromIndex >= sourceCount) {
      //      return (targetCount == 0 ? sourceCount : -1);
       // }
      //  if (fromIndex < 0) {
      //      fromIndex = 0;
       // }
       // if (targetCount == 0) {
       //     return fromIndex;
      //  }

        char first = target[targetOffset];
        int max = sourceOffset + (sourceCount - targetCount);
        int found = -1;

        for (int i = sourceOffset + fromIndex; found == -1 && i <= max; i++) {
            /* Look for first character. */
            if (source[i] != first) {
                while (++i <= max && source[i] != first);
            }

            /* Found first character, now look at the rest of v2 */
            if (i <= max) {
                int j = i + 1;
                int end = j + targetCount - 1;
                for (int k = targetOffset + 1; j < end && source[j]
                        == target[k]; j++, k++);

                if (j == end) {
                    /* Found whole string. */
                    found =  i - sourceOffset;
                }
            }
        }
        return found;
    }

    public static void main(String[] args) throws AparapiException {
        String[] strings = new String[]{"cat","mat","dog"};
        char[][] chars = new char[strings.length][];
        for (int i=0; i< strings.length;i++){
            chars[i] = strings[i].toCharArray();
        }
        int len = strings.length;
        String text = "the cat sat on the mat";
        char[] textChars = text.toCharArray();
        int[] indices = new int[len];



        IntConsumer ic = gid -> {
            indices[gid] = indexOf(textChars, 0, textChars.length,
                    chars[gid], 0, chars[gid].length, 0);
           // indices[gid] = text.indexOf(strings[gid], 0);



        };

        Arrays.fill(indices, -1);
        Device.hsa().forEach(len, ic);
        dump("hsa", strings,  indices);

        Arrays.fill(indices, -1);
        Device.seq().forEach(len, ic);
        dump("seq", strings, indices);

    }
}
