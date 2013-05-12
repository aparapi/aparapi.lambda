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
    int lastNewLineIndex = 0;

    public RegISARenderer append(String s) {
        sb.append(s);
        return (this);
    }

    public RegISARenderer label(int _pc) {
        return (append(String.format("@L%d", _pc)));
    }


    public RegISARenderer append(int i) {
        return (append("" + i));

    }

    public RegISARenderer append(double d) {
        return (append("" + d));
    }

    public RegISARenderer append(float f) {
        return (append("" + f));
    }

    public RegISARenderer append(long l) {
        return (append("" + l));
    }

    public RegISARenderer array_len_offset() {
        return (append(24));
    }

    public RegISARenderer separator() {
        return (append(", "));
    }

    public RegISARenderer nl() {
        append("\n");
        lastMark = lastNewLineIndex = sb.length();
        return (this);
    }


    public RegISARenderer pad(int n) {
        while (sb.length() - lastNewLineIndex < n) {
            append(" ");
        }
        return (this);
    }

    int lastMark = 0;

    public RegISARenderer mark() {
        lastMark = sb.length();
        return (this);
    }

    public RegISARenderer relpad(int n) {
        while (sb.length() - lastMark < n) {
            append(" ");
        }
        return (this);
    }

    public RegISARenderer space() {
        return (append(" "));
    }

    public RegISARenderer semicolon() {
        return (append(";"));
    }

    public RegISARenderer dot() {
        return (append("."));
    }

    @Override
    public String toString() {
        return (sb.toString());
    }

    public RegISARenderer typeName(RegISA.Reg _reg) {
        return (this.append(_reg.type.getHSAName()));
    }

    public RegISARenderer regName(RegISA.Reg _reg) {
        switch (_reg.type.getBits()) {
            case 32:
                append("s");
                break;
            case 64:
                append("d");
                break;
            default:
                append("?");
                break;
        }
        return (this.append(_reg.index));
    }

    public RegISARenderer i(Instruction from) {

        mark().append(from.getByteCode().getName()).relpad(8);//InstructionHelper.getLabel(i, false, false, false);

        if (from.isBranch()) {
            append(" " + from.asBranch().getAbsolute());
        } else if (from.isFieldAccessor()) {
            append(" " + from.asFieldAccessor().getConstantPoolFieldEntry().getType());
            append(" " + from.asFieldAccessor().getConstantPoolFieldEntry().getClassEntry().getDotClassName());
            append(" " + from.asFieldAccessor().getConstantPoolFieldEntry().getName());
        } else if (from.isLocalVariableAccessor()) {
            append(" var#" + from.asLocalVariableAccessor().getLocalVariableInfo().getSlot());
            append("(" + from.asLocalVariableAccessor().getLocalVariableInfo().getVariableName());
            TypeHelper.JavaType type = from.asLocalVariableAccessor().getLocalVariableInfo().getType();
            append(" ");
            if (type == null) {
                // trust me it's an array
                append("?[]");
            } else {
                append(type.toString());
            }
            append(")");

        } else if (from.isMethodCall()) {
            append(" " + from.asMethodCall().getConstantPoolMethodEntry().getArgsAndReturnType().getReturnType());
            append(" " + from.asMethodCall().getConstantPoolMethodEntry().getClassEntry().getDotClassName());
            append("." + from.asMethodCall().getConstantPoolMethodEntry().getName());
        } else if (from.isConstant()) {
            append("." + from.asConstant().getValue());
        }
        return (this);
    }
}