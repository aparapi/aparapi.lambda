package hsailtest;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.util.ArrayList;
import java.util.List;

public class JUnitTester{
   public static void main(String[] args){
      List<Class<?>> classList = new ArrayList<Class<?>>();
      for (String arg : args){
         Class<?> clazz = null;
         try{
            clazz = Class.forName("hsailtest."+arg+"JUnit");
            classList.add(clazz);
         }catch (ClassNotFoundException cnf){
            System.out.println("skipping "+arg);
         }
      }
      Class<?>[] classes = classList.toArray(new Class<?>[0]);
      JUnitCore tester = new JUnitCore();
      Result result = tester.run(classes);
      System.out.println("--------------------------------");
      System.out.println("Ran "+result.getRunCount()+" tests");
      if (result.getFailures().size()>1){
         System.out.println(result.getFailures().size()+" FAILURES follow");
         for (Failure f : result.getFailures()){
            System.out.println("failure ="+f.getDescription()+" "+f.getMessage());
         }
      }
   }

}
