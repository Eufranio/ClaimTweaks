package io.github.eufranio.claimtweaks.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.github.eufranio.claimtweaks.ClaimTweaks;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Frani on 14/01/2018.
 */
@ConfigSerializable
public class ClaimStorage {

    @Setting
    public Map<String, Data> dataMap = Maps.newHashMap();

    @ConfigSerializable
    public static class Data {

        @Setting
        public long timeLock = 0;

        @Setting
        public boolean clearWeather = false;

        @Setting
        public List<String> enterCommands = Lists.newArrayList();

        @Setting
        public List<String> playerEnterCommands = Lists.newArrayList();

        @Setting
        public List<String> leaveCommands = Lists.newArrayList();

        @Setting
        public List<String> playerLeaveCommands = Lists.newArrayList();

    }

    public static Data of(UUID claim) {
        return ClaimTweaks.getStorage().dataMap.get(claim.toString());
    }

    public static Data getOrCreateData(UUID claim) {
        Data data = of(claim);
        if (data == null) {
            data = new Data();
            ClaimTweaks.getStorage().dataMap.put(claim.toString(), data);
        }
        return data;
    }

}
