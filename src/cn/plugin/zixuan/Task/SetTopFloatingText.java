package cn.plugin.zixuan.Task;

import cn.nukkit.level.Position;
import cn.nukkit.level.particle.FloatingTextParticle;
import cn.nukkit.scheduler.PluginTask;
import cn.plugin.zixuan.Main;

public class SetTopFloatingText extends PluginTask<Main> {
    public Position position;
    public String levelName;

    public SetTopFloatingText(Main plugin, String levelName, Position position) {
        super(plugin);
        this.levelName=levelName;
        this.position=position;
    }

    @Override
    public void onRun(int i) {
        getOwner().setTopFloatingText(levelName,position);
    }
}
