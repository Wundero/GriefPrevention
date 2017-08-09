/*
 * This file is part of GriefPrevention, licensed under the MIT License (MIT).
 *
 * Copyright (c) bloodmc
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package me.ryanhamshire.griefprevention.configuration;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Maps;
import me.ryanhamshire.griefprevention.api.claim.ClaimType;
import me.ryanhamshire.griefprevention.api.data.EconomyData;
import me.ryanhamshire.griefprevention.claim.GPClaim;
import me.ryanhamshire.griefprevention.configuration.category.ConfigCategory;
import me.ryanhamshire.griefprevention.util.BlockUtils;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tristate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ConfigSerializable
public class ClaimDataConfig extends ConfigCategory implements IClaimData {

    private boolean requiresSave = false;
    private Vector3i lesserPos;
    private Vector3i greaterPos;
    private Vector3i spawnPos;
    private ClaimStorageData claimStorage;

    @Setting
    private UUID parent;
    @Setting(value = ClaimStorageData.MAIN_INHERIT_PARENT)
    public boolean inheritParent = true;
    @Setting(value = ClaimStorageData.MAIN_WORLD_UUID)
    private UUID worldUniqueId;
    @Setting(value = ClaimStorageData.MAIN_OWNER_UUID)
    private UUID ownerUniqueId;
    @Setting(value = ClaimStorageData.MAIN_CLAIM_TYPE)
    private ClaimType claimType = ClaimType.BASIC;
    @Setting(value = ClaimStorageData.MAIN_CLAIM_CUBOID)
    private boolean isCuboid = false;
    @Setting(value = ClaimStorageData.MAIN_CLAIM_RESIZABLE)
    private boolean isResizable = true;
    @Setting
    private boolean isExpired = false;
    @Setting
    private boolean sizeRestrictions = true;
    @Setting(value = ClaimStorageData.MAIN_ALLOW_DENY_MESSAGES)
    private boolean allowDenyMessages = true;
    @Setting(value = ClaimStorageData.MAIN_ALLOW_CLAIM_EXPIRATION)
    private boolean allowClaimExpiration = true;
    @Setting(value = ClaimStorageData.MAIN_ALLOW_FLAG_OVERRIDES)
    private boolean allowFlagOverrides = true;
    @Setting(value = ClaimStorageData.MAIN_REQUIRES_CLAIM_BLOCKS)
    private boolean requiresClaimBlocks = true;
    @Setting(value = ClaimStorageData.MAIN_CLAIM_PVP)
    private Tristate pvpOverride = Tristate.UNDEFINED;
    @Setting(value = ClaimStorageData.MAIN_CLAIM_DATE_CREATED)
    private String dateCreated = Instant.now().toString();
    @Setting(value = ClaimStorageData.MAIN_CLAIM_DATE_LAST_ACTIVE)
    private String dateLastActive = Instant.now().toString();
    @Setting(value = ClaimStorageData.MAIN_CLAIM_NAME)
    private Text claimName;
    @Setting(value = ClaimStorageData.MAIN_CLAIM_GREETING)
    private Text claimGreetingMessage;
    @Setting(value = ClaimStorageData.MAIN_CLAIM_FAREWELL)
    private Text claimFarewellMessage;
    @Setting(value = ClaimStorageData.MAIN_CLAIM_SPAWN)
    private String claimSpawn;
    @Setting(value = ClaimStorageData.MAIN_LESSER_BOUNDARY_CORNER)
    private String lesserBoundaryCornerPos;
    @Setting(value = ClaimStorageData.MAIN_GREATER_BOUNDARY_CORNER)
    private String greaterBoundaryCornerPos;
    @Setting(value = ClaimStorageData.MAIN_ACCESSORS)
    private List<UUID> accessors = new ArrayList<>();
    @Setting(value = ClaimStorageData.MAIN_BUILDERS)
    private List<UUID> builders = new ArrayList<>();
    @Setting(value = ClaimStorageData.MAIN_CONTAINERS)
    private List<UUID> containers = new ArrayList<>();
    @Setting(value = ClaimStorageData.MAIN_MANAGERS)
    private List<UUID> managers = new ArrayList<>();
    @Setting(value = ClaimStorageData.MAIN_ACCESSOR_GROUPS)
    private List<String> accessorGroups = new ArrayList<>();
    @Setting(value = ClaimStorageData.MAIN_BUILDER_GROUPS)
    private List<String> builderGroups = new ArrayList<>();
    @Setting(value = ClaimStorageData.MAIN_CONTAINER_GROUPS)
    private List<String> containerGroups = new ArrayList<>();
    @Setting(value = ClaimStorageData.MAIN_MANAGER_GROUPS)
    private List<String> managerGroups = new ArrayList<>();
    @Setting
    private EconomyDataConfig economyData = new EconomyDataConfig();
    @Setting
    private Map<UUID, ClaimDataConfig> subdivisions = Maps.newHashMap();

    public ClaimDataConfig() {

    }

    public ClaimDataConfig(GPClaim claim) {
        this.lesserBoundaryCornerPos = BlockUtils.positionToString(claim.lesserBoundaryCorner);
        this.greaterBoundaryCornerPos = BlockUtils.positionToString(claim.greaterBoundaryCorner);
        this.isCuboid = claim.cuboid;
        this.claimType = claim.getType();
        this.ownerUniqueId = claim.getOwnerUniqueId();
    }

    public ClaimDataConfig(LinkedHashMap<String, Object> dataMap) {
        for (Map.Entry<String, Object> mapEntry : dataMap.entrySet()) {
            if (mapEntry.getKey().equals("world-uuid")) {
                this.worldUniqueId = (UUID) UUID.fromString((String) mapEntry.getValue());
            } else if (mapEntry.getKey().equals("owner-uuid")) {
                this.ownerUniqueId = (UUID) UUID.fromString((String) mapEntry.getValue());
            } else if (mapEntry.getKey().equals("claim-type")) {
                this.claimType = ClaimType.valueOf((String) mapEntry.getValue());
            } else if (mapEntry.getKey().equals("cuboid")) {
                this.isCuboid = (boolean) mapEntry.getValue();
            } else if (mapEntry.getKey().equals("claim-expiration")) {
                this.allowClaimExpiration = (boolean) mapEntry.getValue();
            } else if (mapEntry.getKey().equals("date-created")) {
                this.dateCreated = (String) mapEntry.getValue();
            } else if (mapEntry.getKey().equals("date-last-active")) {
                this.dateLastActive = (String) mapEntry.getValue();
            } else if (mapEntry.getKey().equals("deny-messages")) {
                this.allowDenyMessages = (boolean) mapEntry.getValue();
            } else if (mapEntry.getKey().equals("flag-overrides")) {
                this.allowFlagOverrides = (boolean) mapEntry.getValue();
            } else if (mapEntry.getKey().equals("greater-boundary-corner")) {
                this.greaterBoundaryCornerPos = (String) mapEntry.getValue();
            } else if (mapEntry.getKey().equals("lesser-boundary-corner")) {
                this.lesserBoundaryCornerPos = (String) mapEntry.getValue();
            } else if (mapEntry.getKey().equals("pvp")) {
                this.pvpOverride = Tristate.valueOf((String) mapEntry.getValue());
            } else if (mapEntry.getKey().equals("resizeable")) {
                this.isResizable = (boolean) mapEntry.getValue();
            } else if (mapEntry.getKey().equals("accessors")) {
                List<String> stringList = (List<String>) mapEntry.getValue();
                if (stringList != null) {
                    for (String str : stringList) {
                        this.accessors.add(UUID.fromString(str));
                    }
                }
            } else if (mapEntry.getKey().equals("builders")) {
                List<String> stringList = (List<String>) mapEntry.getValue();
                if (stringList != null) {
                    for (String str : stringList) {
                        this.builders.add(UUID.fromString(str));
                    }
                }
            } else if (mapEntry.getKey().equals("containers")) {
                List<String> stringList = (List<String>) mapEntry.getValue();
                if (stringList != null) {
                    for (String str : stringList) {
                        this.containers.add(UUID.fromString(str));
                    }
                }
            } else if (mapEntry.getKey().equals("managers")) {
                List<String> stringList = (List<String>) mapEntry.getValue();
                if (stringList != null) {
                    for (String str : stringList) {
                        this.managers.add(UUID.fromString(str));
                    }
                }
            }
        }
    }

    @Override
    public UUID getWorldUniqueId() {
        return this.worldUniqueId;
    }

    @Override
    public UUID getOwnerUniqueId() {
        return this.ownerUniqueId;
    }

    @Override
    public boolean allowExpiration() {
        return this.allowClaimExpiration;
    }

    @Override
    public boolean allowFlagOverrides() {
        return this.allowFlagOverrides;
    }

    @Override
    public boolean isCuboid() {
        return this.isCuboid;
    }

    @Override
    public boolean allowDenyMessages() {
        return this.allowDenyMessages;
    }

    @Override
    public Tristate getPvpOverride() {
        return this.pvpOverride;
    }

    @Override
    public boolean isResizable() {
        return this.isResizable;
    }

    @Override
    public boolean hasSizeRestrictions() {
        if (this.claimType == ClaimType.ADMIN || this.claimType == ClaimType.WILDERNESS) {
            this.sizeRestrictions = false;
            return false;
        }
        return this.sizeRestrictions;
    }

    @Override
    public ClaimType getType() {
        return this.claimType;
    }

    @Override
    public Instant getDateCreated() {
        return Instant.parse(this.dateCreated);
    }

    @Override
    public Instant getDateLastActive() {
        return Instant.parse(this.dateLastActive);
    }

    @Override
    public Optional<Text> getName() {
        return Optional.ofNullable(this.claimName);
    }

    @Override
    public Optional<Text> getGreeting() {
        return Optional.ofNullable(this.claimGreetingMessage);
    }

    @Override
    public Optional<Text> getFarewell() {
        return Optional.ofNullable(this.claimFarewellMessage);
    }

    @Override
    public Optional<Vector3i> getSpawnPos() {
        if (this.spawnPos == null && this.claimSpawn != null) {
            try {
                this.spawnPos = BlockUtils.positionFromString(this.claimSpawn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return Optional.ofNullable(this.spawnPos);
    }

    @Override
    public Vector3i getLesserBoundaryCornerPos() {
        if (this.lesserPos == null) {
            try {
                this.lesserPos = BlockUtils.positionFromString(this.lesserBoundaryCornerPos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return this.lesserPos;
    }

    @Override
    public Vector3i getGreaterBoundaryCornerPos() {
        if (this.greaterPos == null) {
            try {
                this.greaterPos = BlockUtils.positionFromString(this.greaterBoundaryCornerPos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return this.greaterPos;
    }

    public List<UUID> getAccessors() {
        return this.accessors;
    }

    public List<UUID> getBuilders() {
        return this.builders;
    }

    public List<UUID> getContainers() {
        return this.containers;
    }

    public List<UUID> getManagers() {
        return this.managers;
    }

    public List<String> getAccessorGroups() {
        return this.accessorGroups;
    }

    public List<String> getBuilderGroups() {
        return this.builderGroups;
    }

    public List<String> getContainerGroups() {
        return this.containerGroups;
    }

    public List<String> getManagerGroups() {
        return this.managerGroups;
    }

    @Override
    public void setDenyMessages(boolean flag) {
        this.requiresSave = true;
        this.allowDenyMessages = flag;
    }

    @Override
    public void setExpiration(boolean flag) {
        this.requiresSave = true;
        this.allowClaimExpiration = flag;
    }

    @Override
    public void setFlagOverrides(boolean flag) {
        this.allowFlagOverrides = flag;
    }

    @Override
    public void setCuboid(boolean cuboid) {
        this.isCuboid = cuboid;
    }

    @Override
    public void setPvpOverride(Tristate pvp) {
        this.requiresSave = true;
        this.pvpOverride = pvp;
    }

    @Override
    public void setResizable(boolean resizable) {
        this.requiresSave = true;
        this.isResizable = resizable;
    }

    @Override
    public void setType(ClaimType type) {
        this.requiresSave = true;
        this.claimType = type;
    }

    @Override
    public void setDateLastActive(Instant date) {
        this.requiresSave = true;
        this.dateLastActive = date.toString();
    }

    @Override
    public void setName(Text name) {
        this.requiresSave = true;
        this.claimName = name;
    }

    @Override
    public void setGreeting(Text message) {
        this.requiresSave = true;
        this.claimGreetingMessage = message;
    }

    @Override
    public void setFarewell(Text message) {
        this.requiresSave = true;
        this.claimFarewellMessage = message;
    }

    @Override
    public void setLesserBoundaryCorner(String location) {
        this.requiresSave = true;
        this.lesserBoundaryCornerPos = location;
        this.lesserPos = null;
    }

    @Override
    public void setGreaterBoundaryCorner(String location) {
        this.requiresSave = true;
        this.greaterBoundaryCornerPos = location;
        this.greaterPos = null;
    }

    @Override
    public void setAccessors(List<UUID> accessors) {
        this.requiresSave = true;
        this.accessors = accessors;
    }

    @Override
    public void setBuilders(List<UUID> builders) {
        this.requiresSave = true;
        this.builders = builders;
    }

    @Override
    public void setContainers(List<UUID> containers) {
        this.requiresSave = true;
        this.containers = containers;
    }

    @Override
    public void setManagers(List<UUID> coowners) {
        this.requiresSave = true;
        this.managers = coowners;
    }

    public Map<UUID, ClaimDataConfig> getSubdivisions() {
        return this.subdivisions;
    }

    public boolean requiresSave() {
        return this.requiresSave;
    }

    @Override
    public void setRequiresSave(boolean flag) {
        this.requiresSave = flag;
    }

    @Override
    public void setSizeRestrictions(boolean sizeRestrictions) {
        this.sizeRestrictions = sizeRestrictions;
    }

    @Override
    public boolean doesInheritParent() {
        // admin claims never inherit from parent
        if (this.claimType == ClaimType.ADMIN) {
            return false;
        }
        return this.inheritParent;
    }

    @Override
    public void setInheritParent(boolean flag) {
        this.requiresSave = true;
        this.inheritParent = flag;
    }

    @Override
    public void setOwnerUniqueId(UUID newClaimOwner) {
        this.requiresSave = true;
        this.ownerUniqueId = newClaimOwner;
    }

    @Override
    public void setWorldUniqueId(UUID uuid) {
        this.requiresSave = true;
        this.worldUniqueId = uuid;
    }

    public void setClaimStorageData(ClaimStorageData claimStorage) {
        this.claimStorage = claimStorage;
    }

    @Override
    public void save() {
        this.claimStorage.save();
    }

    @Override
    public void setSpawnPos(Vector3i spawnPos) {
        if (spawnPos == null) {
            return;
        }

        this.requiresSave = true;
        this.spawnPos = spawnPos;
        this.claimSpawn = BlockUtils.positionToString(spawnPos);
    }

    @Override
    public boolean requiresClaimBlocks() {
        return this.requiresClaimBlocks;
    }

    @Override
    public void setRequiresClaimBlocks(boolean requiresClaimBlocks) {
        this.requiresSave = true;
        this.requiresClaimBlocks = requiresClaimBlocks;
    }

    @Override
    public void setParent(UUID uuid) {
        this.requiresSave = true;
        this.parent = uuid;
    }

    @Override
    public Optional<UUID> getParent() {
        return Optional.ofNullable(this.parent);
    }

    public boolean isExpired() {
        return this.isExpired;
    }

    public void setExpired(boolean expire) {
        this.isExpired = expire;
    }

    @Override
    public EconomyData getEconomyData() {
        return this.economyData;
    }
}