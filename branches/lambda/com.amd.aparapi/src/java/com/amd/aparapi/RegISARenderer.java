package com.amd.aparapi;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 4/27/13
 * Time: 9:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class RegISARenderer {
    StringBuilder sb = new StringBuilder();
    public RegISARenderer label(int _pc){
        sb.append(String.format("@L%d", _pc));
        return(this);
    }

    public RegISARenderer append(String s){
        sb.append(s);
        return(this);
    }
    public RegISARenderer append(int i){
        sb.append(""+i);
        return(this);
    }
    public RegISARenderer append(double d){
        sb.append(""+d);
        return(this);
    }
    public RegISARenderer append(float f){
        sb.append(""+f);
        return(this);
    }
    public RegISARenderer append(long l){
        sb.append(""+l);
        return(this);
    }


    public RegISARenderer array_len_offset(){
        append(4);
        return(this);
    }

    public RegISARenderer sizeof_s32(){
        append(4);
        return(this);
    }
    public RegISARenderer sizeof_s64(){
        append(8);
        return(this);
    }
    public RegISARenderer sizeof_f32(){
        append(4);
        return(this);
    }
    public RegISARenderer sizeof_f64(){
        append(8);
        return(this);
    }


    public RegISARenderer regNum( int reg){
        append(reg);
        return(this);
    }

    public RegISARenderer regPrefix(){
        append("$");
        return(this);
    }

    public RegISARenderer s32Name( int reg){
        regPrefix().append("s").regNum(reg);
        return(this);
    }

    public RegISARenderer s64Name( int reg){
        regPrefix();
        append("d");
        regNum(reg);
        return(this);
    }
    public RegISARenderer u64Name( int reg){
        regPrefix();
        append("d");
        regNum(reg);
        return(this);
    }
    public RegISARenderer f64Name( int reg){
        regPrefix();
        append("d");
        regNum(reg);
        return(this);
    }

    public RegISARenderer f32Name( int reg){
        regPrefix();
        append("s");
        regNum(reg);
        return(this);
    }

    public RegISARenderer separator(){
        append(", ");
        return(this);
    }

    public RegISARenderer nl(){
        append("\n");
        return(this);
    }

    public RegISARenderer indent(){
        append("      ");
        return(this);
    }

    public RegISARenderer s32Array(int arr_reg, int index){
        append("[");
        u64Name(arr_reg);
        append("+");
        array_len_offset();
        append("+(");
        sizeof_s32();
        append("*");
        s32Name(index);
        append(")]");
        return(this);
    }

    public RegISARenderer s64Array(int arr_reg, int index){
        append("[");
        u64Name(arr_reg);
        append("+");
        array_len_offset();
        append("+(");
        sizeof_s64();
        append("*");
        s64Name(index);
        append(")]");
        return(this);
    }

    public RegISARenderer f64Array(int arr_reg, int index){
        append("[");
        u64Name(arr_reg);
        append("+");
        array_len_offset();
        append("+(");
        sizeof_f64();
        append("*");
        f64Name(index);
        append(")]");
        return(this);
    }

    public RegISARenderer f32Array(int arr_reg, int index){
        append("[");
        u64Name(arr_reg);
        append("+");
        array_len_offset();
        append("+(");
        sizeof_f32();
        append("*");
        f32Name(index);
        append(")]");
        return(this);
    }

    public RegISARenderer mov(){
        append("mov_");
        return(this);
    }

    public RegISARenderer load(){
        return(append("ld_global_"));
    }

    public RegISARenderer store(){
        append("st_global_");
        return(this);
    }

    public RegISARenderer s(){
        return(append("s"));
    }

    public RegISARenderer d(){
        return(append("d"));
    }

    public RegISARenderer s32(){
        return(append("s32"));
    }

    public RegISARenderer s64(){
        return(append("s64"));
    }
    public RegISARenderer f32(){
        return(append("f32"));
    }
    public RegISARenderer f64(){
        return(append("f64"));
    }
    public RegISARenderer u64(){
        return(append("u64"));
    }
    public RegISARenderer space(){
        return(append(" "));
    }
    public RegISARenderer add(){
        return(append("add_"));
    }
   public RegISARenderer arg(){
      return(append("ld_kernarg_"));
   }
   public RegISARenderer argRef(int _argc){
     return(append("[%_argc").append(_argc).append("]"));
   }
    public RegISARenderer sub(){
        return(append("sub_"));
    }

    public RegISARenderer mul(){
        return(append("mul_"));
    }
    public RegISARenderer div(){
        return(append("div_"));
    }

    public RegISARenderer rem(){
        return(append("rem_"));
    }

    public RegISARenderer cvt(){
        return(append("cvt_"));
    }

    public RegISARenderer semicolon(){
        return(append(";"));
    }

    public RegISARenderer dot(){
        return(append("."));
    }

    public RegISARenderer field(){
        return(append("field_"));
    }
    public RegISARenderer call(){
        return(append("call_"));
    }

    @Override public String toString(){
        return(sb.toString());
    }

}
