package screenshot.asm;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import screenshot.asm.ClassTransformer;

import java.util.Map;

public class LoadingPlugin implements IFMLLoadingPlugin {
    public static boolean IN_MCP = false;

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{ClassTransformer.class.getName()};
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        IN_MCP = !(boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}