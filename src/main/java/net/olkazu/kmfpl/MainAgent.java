package net.olkazu.kmfpl;

import net.olkazu.kmfpl.transformer;
import net.olkazu.kmfpl.transformer.BaseTransformer;
import net.olkazu.kmfpl.transformer.FabricLoaderTransformer;
import net.olkazu.kmfpl.transformer.LibraryTransformer;
import net.olkazu.kmfpl.transformer.RandomPatchesTransformer;
import net.olkazu.kmfpl.transformer.SQLTransformer;
import net.olkazu.kmfpl.transformer.SodiumLikeModTransformer;
import net.olkazu.kmfpl.transformer.SystemInfoTransformer;
import net.olkazu.kmfpl.transformer.TTSTransformer;

import net.olkazu.kmfpl.transformer.oshi.CentralProcessor;
import net.olkazu.kmfpl.transformer.oshi.ProcessorIdentifierTransformer;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ArrayList;
import java.util.List;

public class MainAgent {
    public static String TAG = "[KMFPatch]";
    private static final List<String> classList = new ArrayList<>();

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println(TAG + ": KMFPatch is running!");
        addTransformer(inst, false);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        addTransformer(inst, true);
    }

    private static void addTransformer(Instrumentation inst, boolean isAgentmain) {
        List<BaseTransformer> transformers = new ArrayList<>();
        transformers.add(new TTSTransformer());
        transformers.add(new LibraryTransformer());
        transformers.add(new SystemInfoTransformer());
        transformers.add(new RandomPatchesTransformer());
        transformers.add(new ProcessorIdentifierTransformer());
        transformers.add(new CentralProcessor());
        transformers.add(new SodiumLikeModTransformer());
        transformers.add(new SQLTransformer());
        transformers.add(new FabricLoaderTransformer());
        transformers.forEach(baseTransformer -> {
            inst.addTransformer(baseTransformer, true);
            if (isAgentmain) {
                String className = baseTransformer.getTargetClassName();
                if (!className.isEmpty()) {
                    classList.add(className);
                } else {
                    classList.addAll(baseTransformer.getTargetClassNames());
                }
            }
        });
        if (isAgentmain) {
            Class<?>[] classes = inst.getAllLoadedClasses();
            for (Class<?> aClass : classes) {
                if (classList.contains(aClass.getName())) {
                    System.out.println(TAG + ": Transform:" + aClass.getName());
                    try {
                        inst.retransformClasses(aClass);
                    } catch (UnmodifiableClassException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

}