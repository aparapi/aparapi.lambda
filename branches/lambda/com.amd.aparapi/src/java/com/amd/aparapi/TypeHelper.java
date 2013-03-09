package com.amd.aparapi;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 3/8/13
 * Time: 5:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class TypeHelper{
   /**
    * Convert a signature form "Lpackage/Name;" or array form to dot class form.
    * <p/>
    * signatureToDotClassName("Lpackage/Outer$Name;", 0) -> "package.Outer.Name"
    * signatureToDotClassName("[Lpackage/Outer$Name;", 1) -> "package.Outer.Name"
    *
    * @param _signature
    * @param _dims
    * @return
    */
   public static String signatureToDotClassName(String _signature, int _dims){
      return (slashClassNameToDotClassName(_signature.substring(1 + _dims)));
   }

   /**
    * @param _dotClassName
    * @param _dims
    * @return
    */
   public static String dotClassNameToSignature(String _dotClassName, int _dims){
      StringBuilder sb = new StringBuilder();
      for(int i = 0; i < _dims; i++){
         sb.append("[");
      }
      sb.append("L").append(dotClassNameToSlashClassName(_dotClassName)).append(";");
      return (sb.toString());
   }

   /**
    * @param _dotClassName
    * @return
    */
   public static String dotClassNameToSlashClassName(String _dotClassName){
      return (_dotClassName.replace(".", "/"));
   }

   /**
    * @param _slashClassName
    * @return
    */
   public static String slashClassNameToDotClassName(String _slashClassName){
      return (_slashClassName.replace("/", "."));
   }
}
