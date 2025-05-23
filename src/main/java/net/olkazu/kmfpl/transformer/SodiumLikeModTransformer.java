package net.olkazu.kmfpl.transformer;

import javassist.CtClass;
import javassist.CtMethod;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class SodiumLikeModTransformer implements BaseTransformer {
    @Override
    public String getTargetClassName() {
        return "";
    }

    @Override
    public List<String> getTargetClassNames() {
        List<String> nameList = new ArrayList<>();
        nameList.add("net.caffeinemc.mods.sodium.client.compatibility.checks.PostLaunchChecks");
        nameList.add("me.jellysquid.mods.sodium.client.compatibility.checks.PostLaunchChecks");
        nameList.add("org.embeddedt.embeddium.impl.compatibility.checks.LateDriverScanner");
        nameList.add("me.jellysquid.mods.sodium.client.compatibility.checks.LateDriverScanner");
        return nameList;
    }

    @Override
    public byte[] transform(byte[] buffer) throws Throwable {
        CtClass clazz;
        clazz = pool.makeClass(new ByteArrayInputStream(buffer));
        CtMethod method = clazz.getDeclaredMethod("isUsingPojavLauncher");
        method.setBody("{return false;}");
        byte[] bytes = clazz.toBytecode();
        clazz.detach();
        return bytes;
    }
}
