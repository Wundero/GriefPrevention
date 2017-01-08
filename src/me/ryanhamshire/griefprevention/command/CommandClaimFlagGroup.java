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
package me.ryanhamshire.griefprevention.command;

import me.ryanhamshire.griefprevention.GPPlayerData;
import me.ryanhamshire.griefprevention.GriefPreventionPlugin;
import me.ryanhamshire.griefprevention.claim.GPClaim;
import me.ryanhamshire.griefprevention.command.CommandClaimFlag.FlagType;
import me.ryanhamshire.griefprevention.message.TextMode;
import me.ryanhamshire.griefprevention.permission.GPPermissions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class CommandClaimFlagGroup implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) {
        Player player;
        try {
            player = GriefPreventionPlugin.checkPlayer(src);
        } catch (CommandException e) {
            src.sendMessage(e.getText());
            return CommandResult.success();
        }

        String group = ctx.<String>getOne("group").get();
        String flag = ctx.<String>getOne("flag").orElse(null);
        String source = ctx.<String>getOne("source").orElse(null);
        String target = ctx.<String>getOne("target").orElse(null);
        if (source != null && source.equalsIgnoreCase(target)) {
            source = null;
        }

        Tristate value = ctx.<Tristate>getOne("value").orElse(null);
        String context = ctx.<String>getOne("context").orElse(null);
        GPPlayerData playerData = GriefPreventionPlugin.instance.dataStore.getOrCreatePlayerData(player.getWorld(), player.getUniqueId());
        GPClaim claim = GriefPreventionPlugin.instance.dataStore.getClaimAtPlayer(playerData, player.getLocation(), false);
        if (claim == null) {
            GriefPreventionPlugin.sendMessage(player, Text.of(TextMode.Err, "No claim found."));
            return CommandResult.success();
        } else if (flag == null && value == null) {
            Set<Context> contexts = new HashSet<>();
            contexts.add(claim.getContext());

            PermissionService service = Sponge.getServiceManager().provide(PermissionService.class).get();
            Subject subj = service.getGroupSubjects().get(group);
            if (subj != null) {
                Map<String, Text> flagList = new TreeMap<>();
                Map<String, Boolean> permissions = subj.getSubjectData().getPermissions(contexts);
                for (Map.Entry<String, Boolean> permissionEntry : permissions.entrySet()) {
                    String flagPermission = permissionEntry.getKey();
                    String baseFlagPerm = flagPermission.replace(GPPermissions.FLAG_BASE + ".",  "");
                    Text baseFlagText = Text.builder().append(Text.of(TextColors.GREEN, baseFlagPerm))
                            .onHover(TextActions.showText(CommandHelper.getBaseFlagOverlayText(baseFlagPerm))).build();
                    Text flagText = Text.of(
                            TextColors.GREEN, baseFlagText, "  ",
                            TextColors.WHITE, "[",
                            TextColors.LIGHT_PURPLE, CommandHelper.getClickableText(src, subj, group, contexts, claim, flagPermission, Tristate.fromBoolean(permissionEntry.getValue()), source, FlagType.GROUP),
                            TextColors.WHITE, "]");
                    flagList.put(flagPermission, flagText);
                }

                List<Text> textList = new ArrayList<>(flagList.values());
                PaginationService paginationService = Sponge.getServiceManager().provide(PaginationService.class).get();
                PaginationList.Builder paginationBuilder = paginationService.builder()
                        .title(Text.of(TextColors.GOLD, group, TextColors.AQUA, " Flag Permissions")).padding(Text.of("-")).contents(textList);
                paginationBuilder.sendTo(src);
            }
            return CommandResult.success();
        }

        Subject subj = GriefPreventionPlugin.instance.permissionService.getGroupSubjects().get(group);
        if (subj == null) {
            GriefPreventionPlugin.sendMessage(src, Text.of(TextMode.Err, "Not a valid group."));
            return CommandResult.success();
        }
        return CommandHelper.addFlagPermission(src, subj, group, claim, flag, source, target, value, context);
    }
}
