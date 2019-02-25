package screenshot.asm;

import net.minecraft.client.shader.Framebuffer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.util.IChatComponent;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;

import static org.objectweb.asm.Opcodes.*;
import static screenshot.asm.LoadingPlugin.IN_MCP;

public class ClassTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.util.ScreenShotHelper")) {
            System.out.println("Entering: " + transformedName);
            return patch(basicClass);
        }
        return basicClass;
    }

    private <T> String typeName(Class<T> clazz) {
        return "L" + clazz.getName().replace('.', '/') + ";";
    }

    private byte[] patch(byte[] basicClass) {
        String method = IN_MCP ? "saveScreenshot" : "a";
        String desc = "(" + typeName(File.class) + "II" + typeName(Framebuffer.class) + ")" + typeName(IChatComponent.class);

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        for (MethodNode mn : classNode.methods) {
            if (mn.name.equals(method) && mn.desc.equals(desc)) {
                System.out.println("Enter: " + method + desc);
                mn.instructions.clear(); // Delete original opcodes
                mn.visitVarInsn(ALOAD, 3);
                mn.visitMethodInsn(
                        INVOKESTATIC,
                        "screenshot/util/ScreenshotUtil",
                        "saveScreen",
                        "(" + typeName(Framebuffer.class) + ")" + typeName(IChatComponent.class),
                        false);
                mn.visitInsn(ARETURN);
                mn.visitEnd();
                break;
            }
        }
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}