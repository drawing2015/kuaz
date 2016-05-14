package io.openmg.kuaz.structure.io;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zizai (http://github.com/zizai).
 */
public enum StandardStoreManager {
    IGNITE("io.openmg.kuaz.structure.io.ignite.IgniteStoreManager", "ignite");

    private final String managerClass;
    private final ImmutableList<String> shorthands;

    StandardStoreManager(String managerClass, ImmutableList<String> shorthands) {
        this.managerClass = managerClass;
        this.shorthands = shorthands;
    }

    StandardStoreManager(String managerClass, String shorthand) {
        this(managerClass, ImmutableList.of(shorthand));
    }

    public List<String> getShorthands() {
        return shorthands;
    }

    public String getManagerClass() {
        return managerClass;
    }

    private static final ImmutableList<String> ALL_SHORTHANDS;
    private static final ImmutableMap<String, String> ALL_MANAGER_CLASSES;

    static {
        StandardStoreManager backends[] = values();
        List<String> tempShorthands = new ArrayList<String>();
        Map<String, String> tempClassMap = new HashMap<String, String>();
        for (int i = 0; i < backends.length; i++) {
            tempShorthands.addAll(backends[i].getShorthands());
            for (String shorthand : backends[i].getShorthands()) {
                tempClassMap.put(shorthand, backends[i].getManagerClass());
            }
        }
        ALL_SHORTHANDS = ImmutableList.copyOf(tempShorthands);
        ALL_MANAGER_CLASSES = ImmutableMap.copyOf(tempClassMap);
    }

    public static List<String> getAllShorthands() {
        return ALL_SHORTHANDS;
    }

    public static Map<String, String> getAllManagerClasses() {
        return ALL_MANAGER_CLASSES;
    }
}
