package com.amd.aparapi;

/**
 * Created with IntelliJ IDEA.
 * User: gfrost
 * Date: 4/16/13
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class RegIsaWriter {


    static void write(RegISA regISA) {


        // System.out.println(InstructionHelper.getJavapView(method));


        //System.out.println("MaxLocals=" + method.getCodeEntry().getMaxLocals());
        //System.out.println("MaxStack=" + method.getCodeEntry().getMaxStack());
        // Table table = new Table("|%2d ", "|%2d", "|%2d", "|%s", "|%d", "|%d","|%d", "|%-60s", "|%s");
        // table.header("|PC ", "|Depth", "|Block", "|Consumes + count", "|Produces", "|PreStackBase","|PostStackBase", "|Instruction", "|Branches");



      /*
      for(Instruction i : method.getInstructions()){

         StringBuilder consumes = new StringBuilder();
         for(Instruction.InstructionType instructionType : i.getConsumedInstructionTypes()){
            consumes.append(instructionType.getInstruction().getThisPC()).append(" ");
         }
         StringBuilder sb = new StringBuilder();
         for(InstructionHelper.BranchVector branchInfo : InstructionHelper.getBranches(method)){
            sb.append(branchInfo.render(i.getThisPC(), i.getStartPC()));
         }
         table.data(i.getThisPC());
          table.data(i.getDepth());
          table.data(i.getBlock());
         table.data("" + i.getStackConsumeCount() + " {" + consumes + "}");
         table.data(i.getStackProduceCount());
          table.data(i.getPreStackBaseN());
         table.data(i.getPostStackBase());
         table.data(label);
         table.data(sb + (i.isEndOfTernary() ? "*" : ""));
      }
       System.out.println("{\n" + table.toString() + "}\n");
       */
        ;

        System.out.println(regISA.render(new RegISARenderer()));
    }


}
