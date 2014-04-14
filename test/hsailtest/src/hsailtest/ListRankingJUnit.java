package hsailtest;

import com.amd.aparapi.Device;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ListRankingJUnit{

   int randint(int _from, int _to){
      int value = ((int)(Math.random()*(_to-_from)))+_from;
      //cerr << " _from="<< _from <<" to="<<_to<<" value="<<value<<endl;

      return (value);
   }

   // Create a random set of lists…
   int[] getLinks(int _size, boolean _verbose){
      // srand (time(NULL));
      int[] links = new int[_size];
      // initialize with -1
      for (int i = 0; i<_size; i++){
         links[i] = -1;
         if (_verbose){
            System.out.println(i+" "+links[i]);
         }
      }

      // to simplify building the graph we always create links to lower
      // indexes this removes the possibility of loops in the graph
      //
      int routes = _size/2; // we create 10% routes
      for (int route = 0; route<routes; route++){
         int index = randint(0, _size); // our start point 1..size
         while (index>1 && links[index] == -1){
            int next = randint(0, index); // between 1 and index
            if (_verbose){
               System.out.println(" linking "+index+" to "+next+" ");
            }
            index = links[index] = next;
            if (index<2 || (Math.random()*1000)<5){
               if (_verbose){
                  System.out.println(" out! ");
               }
               break;
            }
         }
      }
      if (_verbose){
         for (int i = 0; i<_size; i++){
            System.out.println(i+" "+links[i]);
         }
      }
      return (links);
   }

   // sequential version of the algorithm
   int[] getRanks(int _size, int[] _links, boolean _verbose){
      int[] ranks = new int[_size];
      for (int i = 0; i<_size; i++){
         if (_verbose){
            System.out.println(i+" ");
         }
         ranks[i] = 0;
         int index = i;
         while (_links[index] != -1){
            if (_verbose){
               System.out.println(" "+_links[index]);
            }
            ranks[i]++;
            index = _links[index];
         }
         if (_verbose){
            System.out.println(" #"+ranks[i]);
         }
      }
      return (ranks);
   }

   @Test
   public void test(){
      int size = 256;
      int[] links = getLinks(size, false);
      int[] referenceRanks = getRanks(size, links, false);
      int[] linksX2 = new int[size*2]; // we will destroy this one
      int[] ranksX2 = new int[size*2];
      for (int i = 0; i<size; i++){
         linksX2[i*2] = links[i];
         linksX2[i*2+1] = -1;
         ranksX2[i*2+1] = 0;
         ranksX2[i*2] = 0;
      }
      int[] again = new int[1]; // use this to determine if we are done
      again[0] = 1;
      int[] fromTo = new int[]{1, 0};
      while (again[0] == 1){
         fromTo[0] = fromTo[0] == 0?1:0;
         fromTo[1] = fromTo[1] == 0?1:0;

         again[0] = 0;

         Device.hsa().forEach(size, gid -> {
            int from = fromTo[0];
            int to = fromTo[1];
            int value = linksX2[from+gid*2];
            if (value != -1){
               linksX2[to+gid*2] = linksX2[from+value*2];
               ranksX2[gid*2+to] = ranksX2[gid*2+from]+ranksX2[from+value*2]+1;
               again[0] = 1;
            }else{
               linksX2[to+gid*2] = value;
               ranksX2[gid*2+to] = ranksX2[gid*2+from];
            }

         });

         for (int i = 0; i<20; i++){
            System.out.printf(" %3d", i);
         }
         System.out.println(" // #");
         for (int i = 0; i<20; i++){
            System.out.printf(" %3d", links[i]);
         }
         System.out.println(" // links orig");
         for (int i = 0; i<20; i++){
            System.out.printf(" %3d", linksX2[i*2+fromTo[0]]);
         }
         System.out.println(" // links[from]");
         for (int i = 0; i<20; i++){
            System.out.printf(" %3d", linksX2[i*2+fromTo[1]]);
         }
         System.out.println(" // links[to]");
         for (int i = 0; i<20; i++){
            System.out.printf(" %3d", ranksX2[i*2+fromTo[0]]);
         }
         System.out.println(" // ranks[from]");
         for (int i = 0; i<20; i++){
            System.out.printf(" %3d", ranksX2[i*2+fromTo[1]]);
         }
         System.out.println(" // ranks[to]");
         for (int i = 0; i<20; i++){
            System.out.printf(" %3d", referenceRanks[i]);
         }
         System.out.println(" // referenceRanks");
         System.out.println("---------------");

      }
      int[] outRanks = new int[size];
      for (int i = 0; i<size; i++){
         outRanks[i] = ranksX2[i*2+fromTo[1]];
      }

      assertTrue("HSA equals JTP results", JunitHelper.compare(referenceRanks, outRanks));

   }
}
