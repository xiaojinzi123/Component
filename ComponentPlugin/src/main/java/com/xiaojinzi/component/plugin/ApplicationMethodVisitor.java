package com.xiaojinzi.component.plugin;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ApplicationMethodVisitor extends MethodVisitor {

    public ApplicationMethodVisitor(MethodVisitor methodVisitor) {
        super(Opcodes.ASM5, methodVisitor);
    }
    //开始访问方法
    @Override
    public void visitCode() {
        super.visitCode();
    }

    @Override
    public void visitInsn(int opcode) {
        //判断内部操作指令
        //当前指令是RETURN，表示方法内部的代码已经执行完
        if (opcode == Opcodes.RETURN) {

        }
        super.visitInsn(opcode);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        //访问结束
    }
}