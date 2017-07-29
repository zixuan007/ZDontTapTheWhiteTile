package cn.plugin.zixuan.Listener;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;

import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import cn.plugin.zixuan.Main;
import com.sun.javafx.collections.MappingChange;


import java.util.LinkedHashMap;
import java.util.Map;


public class setGame implements Listener {


    public static Main plugin = Main.INSTANCE;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        if (plugin.isStartGameName(playerName)) {
            plugin.gameTimeOut(player, plugin.getGameName(playerName));
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerQuitE(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        if (plugin.isStartGameName(playerName)) {
            plugin.gameTimeOut(player, plugin.getGameName(playerName));
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void BlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (plugin.isGameBlock(block)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void BlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (plugin.isGameBlock(block)) {
            event.setCancelled(true);
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        Level level = player.getLevel();
        Block block = event.getBlock();
        if (plugin.isSetGame(player.getName())) {
            switch (plugin.getSetStep(playerName)) {
                case 0:
                    plugin.setBlockPos(plugin.getGameName(playerName), new Position[5][5]);
                    plugin.getBlockPos(plugin.getGameName(playerName))[0][0] = new Position(block.getFloorX(), block.getFloorY(), block.getFloorZ(), block.getLevel());
                    player.sendMessage("§e[§2别踩白块儿§e]§7请点击右上角方块");
                    player.sendMessage("§a左下角方块 X：" + block.getFloorX() + " Y:" + block.getFloorY() + " Z:" + block.getFloorZ() + " 所在的世界" + block.getLevel().getName());
                    plugin.setSetStep(playerName, 1);
                    event.setCancelled(true);
                    return;
                case 1:
                    if (plugin.getBlockPos(plugin.getGameName(playerName))[0][0].getLevel().getName().equals(level.getName()) &&
                            plugin.getBlockPos(plugin.getGameName(playerName))[0][0].getFloorX() == block.getFloorX()) {
                        if (Math.abs((plugin.getBlockPos(plugin.getGameName(playerName))[0][0].getFloorY()) - (block.getFloorY())) == 4 &&
                                Math.abs((plugin.getBlockPos(plugin.getGameName(playerName))[0][0].getFloorZ()) - (block.getFloorZ())) == 3) {
                            plugin.getBlockPos(plugin.getGameName(playerName))[0][1] = new Position(block.getFloorX(), block.getFloorY(), block.getFloorZ(), block.getLevel());
                            int x = plugin.getBlockPos(plugin.getGameName(playerName))[0][0].getFloorX();
                            int y;
                            int z;
                            if (plugin.getBlockPos(plugin.getGameName(playerName))[0][0].getFloorY() > plugin.getBlockPos(plugin.getGameName(playerName))[0][1].getFloorY()) {
                                y = plugin.getBlockPos(plugin.getGameName(playerName))[0][1].getFloorY();
                            } else {
                                y = plugin.getBlockPos(plugin.getGameName(playerName))[0][0].getFloorY();
                            }
                            if (plugin.getBlockPos(plugin.getGameName(playerName))[0][0].getFloorZ() > plugin.getBlockPos(plugin.getGameName(playerName))[0][1].getFloorZ()) {
                                z = plugin.getBlockPos(plugin.getGameName(playerName))[0][1].getFloorZ();
                            } else {
                                z = plugin.getBlockPos(plugin.getGameName(playerName))[0][0].getFloorZ();
                            }
                            for (int i = 0; i < 5; i++) {
                                for (int n = 0; n < 4; n++) {
                                    plugin.getBlockPos(plugin.getGameName(playerName))[i][n] = new Position(x, y + i, z + n, plugin.getBlockPos(plugin.getGameName(playerName))[0][0].getLevel());
                                }
                            }
                            plugin.generateBlock(plugin.getBlockPos(plugin.getGameName(playerName)));
                            plugin.setSetStep(playerName, 2);
                            player.sendMessage("§e[§2别踩白块儿§e]§7请点击要开始游戏的木牌");
                            event.setCancelled(true);
                            return;
                        } else {
                            player.sendMessage("§a右上角角方块 X：" + block.getFloorX() + " Y:" + block.getFloorY() + " Z:" + block.getFloorZ() + " 所在的世界" + block.getLevel().getName());
                            player.sendMessage("§e[§2别踩白块儿§e]§7请确认设置是在同一地图的 4 × 5 的方框,重新设置点两点");
                            plugin.setSetStep(playerName, 0);
                            event.setCancelled(true);
                            return;
                        }

                    } else if (plugin.getBlockPos(plugin.getGameName(playerName))[0][0].getLevel().getName().equals(level.getName()) &&
                            plugin.getBlockPos(plugin.getGameName(playerName))[0][0].getFloorZ() == block.getFloorZ()) {
                        if (Math.abs((plugin.getBlockPos(plugin.getGameName(playerName))[0][0].getFloorY()) - (block.getFloorY())) == 4 &&
                                Math.abs((plugin.getBlockPos(plugin.getGameName(playerName))[0][0].getFloorX()) - (block.getFloorX())) == 3) {
                            plugin.getBlockPos(plugin.getGameName(playerName))[0][1] = new Position(block.getFloorX(), block.getFloorY(), block.getFloorZ(), block.getLevel());
                            int z = plugin.getBlockPos(plugin.getGameName(playerName))[0][0].getFloorZ();
                            int y;
                            int x;
                            if (plugin.getBlockPos(plugin.getGameName(playerName))[0][0].getFloorY() > plugin.getBlockPos(plugin.getGameName(playerName))[0][1].getFloorY()) {
                                y = plugin.getBlockPos(plugin.getGameName(playerName))[0][1].getFloorY();
                            } else {
                                y = plugin.getBlockPos(plugin.getGameName(playerName))[0][0].getFloorY();
                            }
                            if (plugin.getBlockPos(plugin.getGameName(playerName))[0][0].getFloorX() > plugin.getBlockPos(plugin.getGameName(playerName))[0][1].getFloorX()) {
                                x = plugin.getBlockPos(plugin.getGameName(playerName))[0][1].getFloorX();
                            } else {
                                x = plugin.getBlockPos(plugin.getGameName(playerName))[0][0].getFloorX();
                            }
                            for (int i = 0; i < 5; i++) {
                                for (int n = 0; n < 4; n++) {
                                    plugin.getBlockPos(plugin.getGameName(playerName))[i][n] = new Position(x + n, y + i, z, plugin.getBlockPos(plugin.getGameName(playerName))[0][0].getLevel());
                                }
                            }

                            plugin.generateBlock(plugin.getBlockPos(plugin.getGameName(playerName)));
                            plugin.setSetStep(playerName, 2);
                            player.sendMessage("§e[§2别踩白块儿§e]§7请点击要开始游戏的木牌");
                            event.setCancelled(true);
                            return;
                        } else {
                            player.sendMessage("§e[§2别踩白块儿§e]§7 请确认设置是在同一地图的 4 × 5 的方框,重新设置点两点");
                            plugin.setSetStep(playerName, 0);
                            event.setCancelled(true);
                            return;
                        }

                    } else {
                        player.sendMessage("§e[§2别踩白块儿§e]§c 请确认设置是在同一地图的 4 × 5 的方框,重新设置点两点");
                        plugin.setSetStep(playerName, 0);
                        event.setCancelled(true);
                        return;
                    }
                case 2:
                    if (block.getId() == 323 || block.getId() == 68 || block.getId() == 63) {
                        for (Map.Entry<String, Position> entry : plugin.getStartSigns().entrySet()) {
                            if (entry.getValue().getLevel().getName().equals(block.getLevel().getName()) && block.getFloorX() == entry.getValue().getFloorX() && block.getFloorY() == entry.getValue().getFloorY() &&
                                    block.getFloorZ() == entry.getValue().getFloorZ()) {
                                player.sendMessage("§e[§2别踩白块儿§e]§c这块木牌已经是开始木牌了");
                                event.setCancelled(true);
                                return;
                            }
                        }
                        plugin.getStartSigns().put(plugin.getGameName(playerName), new Position(block.getFloorX(), block.getFloorY(), block.getFloorZ(), block.getLevel()));
                        BlockEntity startSign = block.getLevel().getBlockEntity(new Position(block.getFloorX(), block.getFloorY(), block.getFloorZ(), block.getLevel()));
                        if (startSign instanceof BlockEntitySign) {
                            ((BlockEntitySign) startSign).setText("§e[§3别踩白块儿§e]", "§5游戏开始木牌");
                            plugin.getStartSigns().put(plugin.getGameName(playerName), new Position(startSign.getFloorX(), startSign.getFloorY(), startSign.getFloorZ(), startSign.getLevel()));
                            player.sendMessage("§e[§2别踩白块儿§e]§a成功设置开始木牌");
                            plugin.removeSetGame(playerName);
                            plugin.removeStartGameName(playerName);
                            plugin.setGameConfig();
                            event.setCancelled(true);
                        }
                    } else {
                        player.sendMessage("§e[§2别踩白块儿§e]§7请点击开始木牌");
                        event.setCancelled(true);
                        return;
                    }
            }

        }
    }
}
