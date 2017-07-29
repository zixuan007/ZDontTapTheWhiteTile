package cn.plugin.zixuan.Task;

import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;
import cn.plugin.zixuan.Main;

public class CheckGameTimeOut extends PluginTask<Main> {
    private String GameName;
    private Main plugin;
    private Player player;

    public CheckGameTimeOut(Main plugin, Player player,String GameName) {
        super(plugin);
        this.plugin=plugin;
        this.player=player;
        this.GameName=GameName;
    }

    @Override
    public void onRun(int i) {
        plugin.gameTimeOut(player,GameName);
        plugin.removeStartGameName(GameName);
    }
}
