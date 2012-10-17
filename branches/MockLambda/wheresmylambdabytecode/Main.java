public class Main{
   public static void main(String[] args){
      final int in[] = new int[100];
      final int squares[] = new int[100];
      // fill in[]
      Aparapi.forEach(in.length, 
            (gid)->{ squares[gid]=in[gid]*in[gid];}
            );
      // use squares[]
   }
}

