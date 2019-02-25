package screenshot.util;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.nio.ByteOrder.LITTLE_ENDIAN;
import static java.nio.channels.FileChannel.MapMode.READ_WRITE;
import static java.nio.file.StandardOpenOption.*;
import static net.minecraft.event.ClickEvent.Action.OPEN_FILE;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_BGR;
import static screenshot.util.BMPUtil.getFileSize;

public class ScreenshotUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_hh-mm-ss-nnnnnnnnn");

    public static IChatComponent saveScreen(Framebuffer framebuffer) {
        try {
            String name = LocalDateTime.now().format(FORMATTER);
            Path screenPath = saveBmpScreen(name, framebuffer);
            ChatComponentText chatcomponenttext = new ChatComponentText(screenPath.getFileName().toString());
            chatcomponenttext.getChatStyle().setChatClickEvent(
                    new ClickEvent(OPEN_FILE, screenPath.toAbsolutePath().toString())
            );
            chatcomponenttext.getChatStyle().setUnderlined(true);
            return new ChatComponentTranslation("screenshot.success", chatcomponenttext);
        } catch (Exception exception) {
            exception.printStackTrace();
            return new ChatComponentTranslation("screenshot.failure", exception.getMessage());
        }
    }

    private static Path saveBmpScreen(String name, Framebuffer framebuffer) throws IOException {
        int w = framebuffer.framebufferTextureWidth;
        int h = framebuffer.framebufferTextureHeight;
        Path out = Paths.get(name + ".bmp");
        try (FileChannel channel = FileChannel.open(out, READ, WRITE, CREATE)) {
            ByteBuffer imgBuf = channel.map(READ_WRITE, 0, getFileSize(w, h)).order(LITTLE_ENDIAN);

            BMPUtil.fillHeader(imgBuf, w, h);
            glPixelStorei(GL_PACK_ALIGNMENT, 4); // For bmp
            if (OpenGlHelper.isFramebufferEnabled()) {
                glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
                glGetTexImage(GL_TEXTURE_2D, 0, GL_BGR, GL_UNSIGNED_BYTE, imgBuf);
            } else {
                glReadPixels(0, 0, w, h, GL_BGR, GL_UNSIGNED_BYTE, imgBuf);
            }
        }
        return out;
    }
}
