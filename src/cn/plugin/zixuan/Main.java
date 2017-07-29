package cn.plugin.zixuan;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Position;
import cn.nukkit.level.particle.FloatingTextParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.PluginTask;
import cn.nukkit.utils.Config;
import cn.plugin.zixuan.Command.zdtt;
import cn.plugin.zixuan.Listener.setGame;
import cn.plugin.zixuan.Listener.startGame;
import cn.plugin.zixuan.Task.SetTopFloatingText;
import com.sun.javafx.collections.MappingChange;


import javax.sound.sampled.Line;
import java.util.*;

/**
 * Created by zixuan on 2017/7/22.
 */
public class Main extends PluginBase {
    public static Main INSTANCE;
    private Config config;
    private ArrayList<Map.Entry<String, Double>> top = new ArrayList<Map.Entry<String, Double>>();
    private LinkedHashMap<String, Integer> setGame = new LinkedHashMap<String, Integer>();
    private LinkedHashMap<String, Position[][]> blockPos = new LinkedHashMap<>();
    private LinkedHashMap<String, String> startGameName = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, String> setGameName = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, Position> startSigns = new LinkedHashMap<String, Position>();
    private FloatingTextParticle topFloatingTextParticle;
    private Position TopPos;
    private SetTopFloatingText setTopFloatingTextParticle;

    public void onEnable() {
        if (INSTANCE == null)
            INSTANCE = this;
        getServer().getLogger().info("§a============================================================");
        getServer().getLogger().info("§e[§1别§2踩§3白§4块儿§5插§6件§e]§c支持多个设置多个别踩白块儿游戏,§6作者:§bzixuan§a =");
        getServer().getLogger().info("§a============================================================");
        getServer().getCommandMap().register("zdtt", new zdtt());
        config = new Config(getDataFolder() + "//Config.yml", Config.YAML);
        if (!config.getAll().isEmpty()) {
            LinkedHashMap<String, LinkedHashMap<String, List<Object>>> DataMap = (LinkedHashMap<String, LinkedHashMap<String, List<Object>>>) config.get("blockpos");
            for (Map.Entry<String, LinkedHashMap<String, List<Object>>> entry : DataMap.entrySet()) {
                String key = entry.getKey();
                LinkedHashMap<String, List<Object>> posData = entry.getValue();
                Position[][] positions = new Position[5][4];
                for (int line = 0; line < 5; line++) {
                    for (int list = 0; list < 4; list++) {
                        String index = String.valueOf(line) + String.valueOf(list);
                        positions[line][list] = new Position((int) posData.get(index).get(0), (int) posData.get(index).get(1), (int) posData.get(index).get(2), getServer().getLevelByName((String) posData.get(index).get(3)));
                    }
                }
                blockPos.put(key, positions);
            }
            LinkedHashMap<String, ArrayList<Object>> startSignPos = (LinkedHashMap<String, ArrayList<Object>>) config.get("startsignpos");
            for (Map.Entry<String, ArrayList<Object>> entry : startSignPos.entrySet()) {
                String key = entry.getKey();
                ArrayList<Object> startSign = entry.getValue();
                Position Temp = new Position((int) startSign.get(0), (int) startSign.get(1), (int) startSign.get(2), getServer().getLevelByName((String) startSign.get(3)));
                startSigns.put(key, Temp);
            }
            if (config.get("top") != null) {
                ArrayList<LinkedHashMap<String, Double>> configTop = (ArrayList<LinkedHashMap<String, Double>>) config.get("top");
                for (LinkedHashMap<String, Double> map : configTop) {
                    Map.Entry<String, Double>[] entries = new Map.Entry[1];
                    map.entrySet().toArray(entries);
                    top.add(entries[0]);
                }
            }
            if (config.get("TopPos") != null) {
                ArrayList<Object> TopPos = (ArrayList<Object>) config.get("TopPos");
                this.TopPos = new Position((Integer) TopPos.get(0), (Integer) TopPos.get(1), (Integer) TopPos.get(2), getServer().getLevelByName((String) TopPos.get(3)));
                String Message = "";
                for (int i = 0; i < top.size(); i++) {
                    Message += "§e第§2" + (i + 1) + "§e名  玩家:§b " + top.get(i).getKey() + " §e用时:§9 " + top.get(i).getValue() + "§e秒\n";
                }
                topFloatingTextParticle = new FloatingTextParticle(this.TopPos, Message, "        §e<<§1别§2踩§3白§4块§5儿§6排§7行§a榜§e>>\n");
                setTopFloatingTextParticle = new SetTopFloatingText(this, this.TopPos.getLevel().getName(), this.TopPos);
                getServer().getScheduler().scheduleRepeatingTask(setTopFloatingTextParticle,50 * 20);
            }
        }
        getServer().getPluginManager().registerEvents(new setGame(), this);
        getServer().getPluginManager().registerEvents(new startGame(), this);
    }

    public void onDisable() {
        for (Map.Entry entry : setGame.entrySet()) {
            setGame.put((String) entry.getKey(), 0);
        }
        getServer().getLogger().info("§e[§1别§2踩§3白§4块儿§5插§6件§e]§c插件关闭中....");
    }

    public Config getConfig() {
        return config;
    }

    public boolean isSetGame(String name) {
        if (setGame.get(name) != null)
            return true;
        return false;
    }

    public void addSetGame(String name, Integer step) {
        setGame.put(name, step);
    }

    public void removeSetGame(String GameName) {
        setGame.remove(GameName);
    }

    public void removeSetGameName(String name) {
        setGameName.remove(name);
    }

    public boolean islocationName(String name) {
        if (blockPos.get(name) != null)
            return true;
        return false;
    }


    public void removeGame(String name) {
        blockPos.remove(name);
        startSigns.remove(name);
        setGameConfig();
    }

    public void setGameConfig() {
        LinkedHashMap<String, LinkedHashMap<String, List<Object>>> DataMap = new LinkedHashMap<String, LinkedHashMap<String, List<Object>>>();
        LinkedHashMap<String, List<Object>> posData;
        ArrayList<Object> posList;
        ArrayList<Object> startSignList;
        LinkedHashMap<String, ArrayList<Object>> startSignPos = new LinkedHashMap<String, ArrayList<Object>>();

        for (Map.Entry entry : blockPos.entrySet()) {
            String key = (String) entry.getKey();
            posList = new ArrayList<Object>();
            posData = new LinkedHashMap<String, List<Object>>();
            for (int line = 0; line < 5; line++) {
                for (int list = 0; list < 4; list++) {
                    posList = new ArrayList<Object>();
                    String index = String.valueOf(line).toString() + String.valueOf(list).toString();
                    posList.add(blockPos.get(key)[line][list].getFloorX());
                    posList.add(blockPos.get(key)[line][list].getFloorY());
                    posList.add(blockPos.get(key)[line][list].getFloorZ());
                    posList.add(blockPos.get(key)[line][list].getLevel().getName());
                    posData.put(index, posList);
                }

            }
            startSignList = new ArrayList<Object>();
            DataMap.put(key, posData);
            startSignList.add(getStartSigns().get(key).getFloorX());
            startSignList.add(getStartSigns().get(key).getFloorY());
            startSignList.add(getStartSigns().get(key).getFloorZ());
            startSignList.add(getStartSigns().get(key).getLevel().getName());
            startSignPos.put(key, startSignList);

        }
        config.set("startsignpos", startSignPos);
        config.set("blockpos", DataMap);
        config.save();
    }


    public boolean isStartGameName(String name) {
        for (Map.Entry<String, String> entry : startGameName.entrySet()) {
            String value = entry.getValue();
            if (value.equals(name))
                return true;
        }
        return false;
    }

    public void removeStartGameName(String GameName) {
        startGameName.remove(GameName);
    }

    public void gameTimeOut(Player player, String GameName) {
        player.sendMessage("§e[§a别踩白块儿§e]§4游戏超时了");
        Position[][] blockPos = this.blockPos.get(GameName);
        startGame.getTasks().get(player.getName()).getHandler().cancel();
        generateBlock(blockPos);
    }

    public void generateBlock(Position[][] blockPos) {
        int random = 1;
        Block whiteBlock = Block.get(35, 0);
        Block blackBlock = Block.get(35, 15);
        for (int line = 0; line < 5; line++) {
            for (int list = 0; list < 4; list++) {
                if (random % 2 == 0) {
                    blockPos[line][list].getLevel().setBlock(blockPos[line][list], blackBlock);
                } else {
                    blockPos[line][list].getLevel().setBlock(blockPos[line][list], whiteBlock);
                }
                random++;
            }
            random++;
        }
    }

    public String getGameName(String name) {
        return setGameName.get(name);
    }

    public String getStartGameName(String name) {
        for (Map.Entry<String, String> entry : startGameName.entrySet()) {
            String playerName = entry.getValue();
            if (playerName.equals(name)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private String getGameName(Block block) {
        for (Map.Entry<String, Position> entry : startSigns.entrySet()) {
            Position position = entry.getValue();
            if (position.getFloorX() == block.getFloorX() && position.getFloorY() == block.getFloorY() && position.getFloorZ() == block.getFloorZ()) {
                return entry.getKey();
            }
        }
        return null;
    }

    public boolean isGameStart(Block block) {
        String gameName = getGameName(block);
        if (startGameName.get(gameName) != null) {
            return true;
        }
        return false;
    }

    public void gameTimeOut(String name, String GameName) {
        gameTimeOut(getServer().getPlayer(name), GameName);
    }

    public Integer getSetStep(String name) {
        return setGame.get(name);
    }

    public void setSetStep(String name, Integer step) {
        setGame.put(name, step);
    }

    public void remove(String name) {
        setGame.remove(name);
    }

    public void setBlockPos(String gameName, Position[][] pos) {
        blockPos.put(gameName, pos);

    }

    public void setSetGameName(String playerName, String setGameName) {
        this.setGameName.put(playerName, setGameName);
    }

    public Position[][] getBlockPos(String name) {
        return blockPos.get(name);
    }


    public LinkedHashMap<String, Position> getStartSigns() {
        return startSigns;
    }

    public void setStartGameName(String GameName, String playerName) {
        startGameName.put(GameName, playerName);
    }

    private Position getStartPos(Position[][] blockPos) {
        return blockPos[0][0];
    }

    private Position getEndPos(Position[][] blockPos) {
        return blockPos[4][3];
    }

    public boolean isGameBlock(Block block) {
        for (Map.Entry<String, Position[][]> entry : blockPos.entrySet()) {
            Position startPos = getStartPos(entry.getValue());
            Position endPos = getEndPos(entry.getValue());
            if (startPos != null && endPos != null) {
                if (startPos.getFloorX() - 1 <= block.getFloorX() && startPos.getFloorY() - 1 <= block.getFloorY() && startPos.getFloorZ() - 1 <= block.getFloorZ() &&
                        endPos.getFloorX() + 1 >= block.getFloorX() && endPos.getFloorY() + 1 >= block.getFloorY() && endPos.getFloorZ() + 1 >= block.getFloorZ()) {
                    return true;
                }
            }
        }
        for (Map.Entry<String, Position> entry : startSigns.entrySet()) {
            Position startSign = entry.getValue();
            if (startSign.getLevel().getName().equals(block.getLevel().getName()) && startSign.getFloorX() == block.getFloorX() && startSign.getFloorY() == block.getFloorY() &&
                    startSign.getFloorZ() == block.getFloorZ()) {
                return true;
            }
        }
        return false;
    }


    public void changeBlock(int number, Position[][] positions, String playerName) {
        Block[][] blockColor = startGame.getBlockColor().get(playerName);
        Block blackBlock = Block.get(35, 15);
        Block whiteBlock = Block.get(35, 0);
        if (number == 0) {
            startGame.setStartTime(System.currentTimeMillis());
            int index = 0;
            for (int line = 0; line < 5; line++) {
                for (int list = 0; list < 4; list++) {
                    blockColor[line][list] = whiteBlock;
                }
                index = (int) (Math.random() * 4);
                blockColor[line][index] = blackBlock;
            }
            for (int line = 0; line < 5; line++) {
                for (int list = 0; list < 4; list++) {
                    positions[line][list].getLevel().setBlock(positions[line][list], blockColor[line][list]);
                }
            }
        } else if (number != 0 && number <= 45) {
            for (int line = 0; line < 4; line++) {
                for (int list = 0; list < 4; list++) {
                    blockColor[line][list] = blockColor[line + 1][list];
                }
            }
            int index = 0;
            index = (int) (Math.random() * 4);
            blockColor[4][0] = whiteBlock;
            blockColor[4][1] = whiteBlock;
            blockColor[4][2] = whiteBlock;
            blockColor[4][3] = whiteBlock;
            blockColor[4][index] = blackBlock;
            for (int line = 0; line < 5; line++) {
                for (int list = 0; list < 4; list++) {
                    positions[line][list].getLevel().setBlock(positions[line][list], blockColor[line][list]);
                }
            }
        } else {
            for (int i = 0; i < 50 - number; i++) {
                for (int list = 0; list < 4; list++) {
                    blockColor[i][list] = blockColor[i + 1][list];
                }
            }
            Block greenBlock = Block.get(35, 5);
            for (int i = 4; i > 49 - number; i--) {
                blockColor[i][0] = greenBlock;
                blockColor[i][1] = greenBlock;
                blockColor[i][2] = greenBlock;
                blockColor[i][3] = greenBlock;

            }
            for (int line = 0; line < 5; line++) {
                for (int list = 0; list < 4; list++) {
                    positions[line][list].getLevel().setBlock(positions[line][list], blockColor[line][list]);
                }
            }
        }
    }

    public void changeTop(String playerName, Double time) {
        ListIterator<Map.Entry<String, Double>> it = top.listIterator();
        LinkedHashMap<String, Double> TempTop1 = new LinkedHashMap<String, Double>();
        Map.Entry<String, Double>[] entries1 = new Map.Entry[1];
        while (it.hasNext()) {
            Map.Entry<String, Double> next = it.next();
            if (next.getKey().equals(playerName)) {
                if (next.getValue() > time) {
                    it.remove();
                    TempTop1.put(playerName, time);
                    TempTop1.entrySet().toArray(entries1);
                }
            }
        }
        if (entries1[0] != null) {
            top.add(entries1[0]);
        }
        if (top.size() < 6) {
            if (!isTopName(playerName)) {
                LinkedHashMap<String, Double> TempTop = new LinkedHashMap<String, Double>();
                TempTop.put(playerName, time);
                Map.Entry<String, Double>[] entries = new Map.Entry[1];
                TempTop.entrySet().toArray(entries);
                top.add(entries[0]);
            }
            Collections.sort(top, new Comparator<Map.Entry<String, Double>>() {
                @Override
                public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                    if (o1.getValue() > o2.getValue()) {
                        return 1;
                    } else if (o1.getValue() == o2.getValue()) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
        }
        if (!isTopName(playerName)) {
            for (Map.Entry<String, Double> entry : top) {
                if (time < entry.getValue()) {
                    LinkedHashMap<String, Double> TempTop = new LinkedHashMap<String, Double>();
                    TempTop.put(playerName, time);
                    Map.Entry<String, Double>[] entries = new Map.Entry[1];
                    TempTop.entrySet().toArray(entries);
                    top.add(entries[0]);
                    break;
                }
            }
            Collections.sort(top, new Comparator<Map.Entry<String, Double>>() {
                @Override
                public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                    if (o1.getValue() > o2.getValue()) {
                        return 1;
                    } else if (o1.getValue() == o2.getValue()) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });
        }
        int index = 0;
        ListIterator<Map.Entry<String, Double>> itTop = top.listIterator();
        while (itTop.hasNext()) {
            Map.Entry<String, Double> next = itTop.next();
            index++;
            if (index == 7) {
                itTop.remove();
            }
        }
        ArrayList<LinkedHashMap<String, Double>> ConfigList = new ArrayList<LinkedHashMap<String, Double>>();
        for (Map.Entry<String, Double> entry : top) {
            ConfigList.add(new LinkedHashMap<String, Double>() {
                {
                    put(entry.getKey(), entry.getValue());
                }
            });
        }
        config.set("top", ConfigList);
        config.save();
    }

    private boolean isTopName(String playerName) {
        for (Map.Entry<String, Double> entry : top) {
            if (playerName.equals(entry.getKey())) {
                return true;
            }
        }
        return false;
    }

    public boolean setTopFloatingText(String levelName, Position position) {
        if (top.size() < 6)
            return false;
        String Message = "";
        for (int i = 0; i < top.size(); i++) {
            Message += "§e第§2" + (i + 1) + "§e名  玩家:§b " + top.get(i).getKey() + " §e用时:§9 " + top.get(i).getValue() + "§e秒\n";
        }
        if (TopPos == null)
            topFloatingTextParticle = new FloatingTextParticle(position, Message, "       §e<<§1别§2踩§3白§4块§5儿§6排§7行§a榜§e>>\n");
        getServer().getLevelByName(levelName).addParticle(topFloatingTextParticle);
        if (setTopFloatingTextParticle == null) {
            setTopFloatingTextParticle = new SetTopFloatingText(this, levelName, position);
            getServer().getScheduler().scheduleRepeatingTask(setTopFloatingTextParticle, 50 * 20);
        }
        return true;
    }

    public void setTopPos(Position pos) {
        TopPos = pos;
    }

    public Position getTopPos() {
        return TopPos;
    }

    public void setSetTopFloatingTextParticle(SetTopFloatingText pluginTask){
        setTopFloatingTextParticle=pluginTask;
    }

    public SetTopFloatingText getSetTopFloatingTextParticle(){
        return setTopFloatingTextParticle;
    }

    public void setTopFloatingTextParticle(FloatingTextParticle floatingTextParticle){
       topFloatingTextParticle=floatingTextParticle;
    }

    public FloatingTextParticle getTopFloatingTextParticle(){
        return topFloatingTextParticle;
    }

    public Boolean isSetTop(){
        if(setTopFloatingTextParticle == null && topFloatingTextParticle==null){
            return false;
        }
        return true;
    }

}

