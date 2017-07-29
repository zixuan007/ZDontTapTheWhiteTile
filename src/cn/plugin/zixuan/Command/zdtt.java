package cn.plugin.zixuan.Command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Position;
import cn.plugin.zixuan.Main;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zixuan on 2017/7/22.
 */
public class zdtt extends Command {
    private Main plugin = Main.INSTANCE;

    public zdtt() {
        super("zdtt", "别踩白块儿主指令");
        getCommandParameters().put("default", new CommandParameter[]{
                new CommandParameter("set|del|top", CommandParameter.ARG_TYPE_RAW_TEXT, true),
                new CommandParameter("名称", CommandParameter.ARG_TYPE_RAW_TEXT, true)
        });
        setAliases(new String[]{
                "zdtt set [名称] §d>>§2设置别踩白块儿游戏",
                "zdtt del [名称] §d>>§2移除别踩白块儿游戏",
                "zdtt del top §d>>§2移除设置过的浮空排行榜",
                "zdtt top §d>>§2设置别踩白块儿浮空排行榜  §4注意:§6必须有6人玩了才可以设置"
        });
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        String name = sender.getName();
        Player player = (Player) sender;
        if (getName().toLowerCase() == "zdtt") {
            if (!sender.isOp()) {
                sender.sendMessage("§e[§2别踩白块儿§e]§c你不是op无法使用该指令");
                return false;
            }
            if (args.length < 1) {
                sender.sendMessage("§b/zdtt help §d>>§2获取帮助");
                return true;
            }
            switch (args[0]) {
                case "set":
                    if (args.length < 2) {
                        sender.sendMessage("§b/zdtt set [名称] §d>>§2开始设置游戏");
                        return false;
                    }
                    if (args[1].equals("") || args[1].equals(" ")) {
                        sender.sendMessage("§e[§2别踩白块儿§e]§c游戏名字不能为空");
                        return false;
                    }
                    if (plugin.isSetGame(name)) {
                        sender.sendMessage("§e[§2别踩白块儿§e]§c你已经在设置了游戏了");
                        return false;
                    }
                    if (plugin.islocationName(args[1])) {
                        sender.sendMessage("§e[§2别踩白块儿§e]§c已经设置过这个名字了");
                        return false;
                    }
                    plugin.addSetGame(name, 0);
                    plugin.setSetGameName(name, args[1]);
                    sender.sendMessage("§e[§2别踩白块儿§e]§a请开始设置游戏,请点击4×5方框里面的左下角和右上角的白块和黑块,和一个开始木牌");
                    return true;
                case "del":
                    if(args[1].equals("top")){
                        if(plugin.isSetTop()){
                            plugin.getTopFloatingTextParticle().setInvisible(true);
                            plugin.getServer().getLevelByName(plugin.getSetTopFloatingTextParticle().levelName).addParticle(plugin.getTopFloatingTextParticle());
                            plugin.getSetTopFloatingTextParticle().getHandler().cancel();
                            plugin.setSetTopFloatingTextParticle(null);
                            plugin.setTopFloatingTextParticle(null);
                            plugin.getConfig().remove("TopPos");
                            plugin.getConfig().save();
                            sender.sendMessage("§e[§2别踩白块儿§e]§a成功移除浮空排行榜");
                            return true;
                        }else{
                            sender.sendMessage("§e[§2别踩白块儿§e]§c你还没有设置过浮空字排行榜");
                            return false;
                        }
                    }
                    if (args.length < 2) {
                        sender.sendMessage("§b/zdtt del [名称] §d>>§2删除游戏");
                        return false;
                    }
                    if (!plugin.islocationName(args[1])) {
                        sender.sendMessage("§e[§2别踩白块儿§e]§c不存在这个名称");
                        return false;
                    }
                    plugin.removeGame(args[1]);
                    plugin.removeSetGame(sender.getName());
                    sender.sendMessage("§e[§2别踩白块儿§e]§c游戏移除成功");
                    return true;
                case "help":
                    sender.sendMessage("§b/zdtt set [名称]§d>>§2开始设置游戏");
                    sender.sendMessage("§b/zdtt del [名称]§d>>§2删除设置过的游戏");
                    sender.sendMessage("§b/zdtt top §d>>§2设置别踩白块儿浮空榜  §4注意:§6至少要6个人玩了才能设置");
                    return true;
                case "top":
                    if (args.length < 1) {
                        sender.sendMessage("§b/zdtt top §d>>§2设置别踩白块儿浮空文字榜");
                        return false;
                    }
                    if(plugin.getTopPos() != null){
                        sender.sendMessage("§e[§2别踩白块儿§e]§c你已经设置了排行榜了,请勿再次设置");
                        return false;
                    }
                    Position position = new Position(player.getFloorX(), player.getFloorY(), player.getFloorZ(), player.getLevel());
                    if (plugin.setTopFloatingText(player.getLevel().getName(), position)) {
                        sender.sendMessage("§e[§2别踩白块儿§e]§c设置成功");
                        ArrayList<Object> pos = new ArrayList<Object>();
                        pos.add(player.getFloorX());
                        pos.add(player.getFloorY());
                        pos.add(player.getFloorZ());
                        pos.add(player.getLevel().getName());
                        plugin.getConfig().set("TopPos", pos);
                        plugin.getConfig().save();
                        plugin.setTopPos(new Position(player.getFloorX(),player.getFloorY(),player.getFloorZ(),player.getLevel()));
                    } else {
                        sender.sendMessage("§e[§2别踩白块儿§e]§c排行榜数据少于6人,无法设置");
                        return false;
                    }
            }
        }
        return false;
    }
}
