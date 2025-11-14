package me.pintoadmin.pintoPlaytime;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;

import java.util.UUID;

public class LuckPermsHook {
    private LuckPerms api = null;
    private boolean luckPermsInstalled = false;
    public LuckPermsHook(PintoPlaytime plugin) {
        luckPermsInstalled = plugin.getLuckPermsInstalled();
        if(plugin.getLuckPermsInstalled()){
            api = LuckPermsProvider.get();
        }
    }

    public void givePermission(UUID uuid, String permission) {
        if(!luckPermsInstalled) return;
        Node permissionNode = Node.builder(permission).value(true).build();
        User user = api.getUserManager().getUser(uuid);
        if(user != null){
            if(!user.getNodes().contains(permissionNode)) {
                user.data().add(permissionNode);
                api.getUserManager().saveUser(user);
            }
        }
    }
}
