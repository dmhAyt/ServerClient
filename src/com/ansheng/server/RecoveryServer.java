package com.ansheng.server;

import com.ansheng.myenum.IdentifyEnum;
import com.ansheng.single.ConfigSingle;

import java.util.Objects;

public class RecoveryServer {

    public void workerRecovery() {
        ConfigSingle configSingle = ConfigSingle.getInstance();
        if(Objects.isNull(configSingle)) return;
        switch (configSingle.getIdentity()){
            case GROUP_LEADER:

                break;
            case Deputy_GROUP_LEADER:

                break;
            case GROUP_NORMAL:

                break;
            case GROUP_DELETE:

                break;
            default:
                break;
        }
    }

    private void CheckTeamMembersOnline(){
        // 拿组员配置文件


    }

}
