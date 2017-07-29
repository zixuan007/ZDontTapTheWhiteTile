package cn.plugin.zixuan.Listener;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.level.Position;
import cn.nukkit.scheduler.PluginTask;
import cn.plugin.zixuan.Main;
import cn.plugin.zixuan.Task.CheckGameTimeOut;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class startGame implements Listener {
    public Main plugin = Main.INSTANCE;
    private static Long startTime = 0L;
    private static Long endTime = 0L;
    private static LinkedHashMap<String, PluginTask<Main>> Tasks = new LinkedHashMap<String, PluginTask<Main>>();
    private static LinkedHashMap<String, Block[][]> blockColor = new LinkedHashMap<String, Block[][]>();
    private int number;


    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        Block block = event.getBlock();
        if (!plugin.isStartGameName(playerName)) {
            if (plugin.isGameStart(block)) {
                player.sendMessage("§e[§2别踩白块儿§e]§c这个游戏已经有人开始了");
            }
            for (Map.Entry<String, Position> entry : plugin.getStartSigns().entrySet()) {
                Position startSignPos = entry.getValue();
                if (startSignPos.getLevel().getName().equals(block.getLevel().getName()) && startSignPos.getFloorX() == block.getFloorX() && startSignPos.getFloorY() == block.getFloorY() &&
                        startSignPos.getFloorZ() == block.getFloorZ()) {
                    if (block.getId() == 323 || block.getId() == 68 || block.getId() == 63) {
                        if(plugin.isSetGame(playerName)){
                            return;
                        }
                        plugin.setStartGameName(entry.getKey(), playerName);
                        player.sendTitle("§e游戏开始,请开始点击方块");
                        blockColor.put(playerName, new Block[5][4]);
                        number = 0;
                        plugin.changeBlock(number, plugin.getBlockPos(entry.getKey()),playerName);
                        PluginTask<Main> task = new CheckGameTimeOut(plugin, player, entry.getKey());
                        plugin.getServer().getScheduler().scheduleDelayedTask(task, 30*20);
                        Tasks.put(playerName, task);
                        return;
                    }
                }
            }
        } else {
            for(Map.Entry<String,Position> entry:plugin.getStartSigns().entrySet()){
                Position startSign=entry.getValue();
                if(block.getLevel().getName().equals(startSign.getLevel().getName())&&block.getFloorX()==startSign.getFloorX() && block.getFloorY()==startSign.getFloorY() && block.getFloorZ()==block.getFloorZ()){
                    player.sendTitle("§e你已经加入游戏了");
                    event.setCancelled(true);
                }
            }
            Position[][] blockPos = plugin.getBlockPos(plugin.getStartGameName(playerName));
            if (blockPos[0][0].getLevel().getName().equals(block.getLevel().getName()) && blockPos[0][0].getFloorX() <= block.getFloorX() && blockPos[0][0].getFloorY() <= block.getFloorY() && blockPos[0][0].getFloorZ() <= block.getFloorZ() &&
                    blockPos[4][3].getFloorX() >= block.getFloorX() && blockPos[4][3].getFloorY() >= block.getFloorY() && blockPos[4][3].getFloorZ() >= block.getFloorZ()) {
                if (blockPos[0][0].getFloorY() == block.getFloorY()) {
                    if (block.getId() == 35 && block.getDamage() == 0) {
                        player.sendMessage("§e[§2别踩白块儿§e]§4游戏结束,你踩到了白色方块");
                        Tasks.get(playerName).getHandler().cancel();
                        GenerateRedBlock(plugin.getBlockPos(plugin.getStartGameName(playerName)));
                        plugin.removeStartGameName(plugin.getStartGameName(playerName));
                        number = 0;
                    } else if (block.getId() == 35 && block.getDamage() == 15) {
                        if (number == 49) {
                            endTime = System.currentTimeMillis();
                            Double time =(endTime - startTime) / 1000.0;
                            plugin.changeBlock(number, plugin.getBlockPos(plugin.getStartGameName(playerName)),playerName);
                            player.sendMessage("§e[§2别踩白块儿§e]§4游戏结束");
                            player.sendTitle("§e用时" + time + "秒");
                            plugin.changeTop(playerName,time);
                            Tasks.get(playerName).getHandler().cancel();
                            plugin.removeStartGameName(plugin.getStartGameName(playerName));
                        } else {
                            number++;
                            plugin.changeBlock(number, plugin.getBlockPos(plugin.getStartGameName(playerName)),playerName);
                        }
                    }
                }

            }
        }
    }

    public static void setStartTime(Long time) {
        startTime = time;
    }

    private void GenerateRedBlock(Position[][] positions) {
        for (int line = 0; line < 5; line++) {
            for (int list = 0; list < 4; list++) {
                positions[line][list].getLevel().setBlock(positions[line][list], Block.get(35, 14));
            }
        }
    }

    public static LinkedHashMap<String, Block[][]> getBlockColor() {
        return blockColor;
    }

    public static LinkedHashMap<String,PluginTask<Main>> getTasks(){
        return Tasks;
    }
}


