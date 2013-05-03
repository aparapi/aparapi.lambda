package com.amd.aparapi;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 4/27/13
 * Time: 9:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class RegISARenderer{
   StringBuilder sb = new StringBuilder();
   int lastNewLineIndex = 0;

   public RegISARenderer label(int _pc){
      sb.append(String.format("@L%d", _pc));
      return (this);
   }

   public RegISARenderer append(String s){
      sb.append(s);
      return (this);
   }

   public RegISARenderer append(int i){
      sb.append("" + i);
      return (this);
   }

   public RegISARenderer append(double d){
      sb.append("" + d);
      return (this);
   }

   public RegISARenderer append(float f){
      sb.append("" + f);
      return (this);
   }

   public RegISARenderer append(long l){
      sb.append("" + l);
      return (this);
   }

   public RegISARenderer array_len_offset(){
      append(24);
      return (this);
   }

   public RegISARenderer separator(){
      append(", ");
      return (this);
   }

   public RegISARenderer nl(){
      append("\n");
      lastNewLineIndex = sb.length();
      return (this);
   }

   public RegISARenderer indent(){
      append("      ");
      return (this);
   }

   public  RegISARenderer pad(int n){
       while (sb.length() - lastNewLineIndex < n){
           sb.append(" ");
       }
       return(this);
   }

    public RegISARenderer comment(String _comment){
        sb.append("// " + _comment);
        return (this);
    }

   public RegISARenderer space(){
      return (append(" "));
   }

   public RegISARenderer semicolon(){
      return (append(";"));
   }

   public RegISARenderer dot(){
      return (append("."));
   }

   @Override public String toString(){
      return (sb.toString());
   }

   public RegISARenderer typeName(RegISA.Reg _reg){
      return (this.append(_reg.type.getTypeName()));
   }

   public RegISARenderer regName(RegISA.Reg _reg){
      return (this.append(_reg.type.getRegName(_reg.index)));
   }
}
