package org.hints.im.utils;

import org.hints.im.persist.DataBaseStore;
import org.hints.im.pojo.GroupBody;
import org.hints.im.pojo.MsgBody;
import org.hints.im.pojo.entity.GroupHistoryDO;

import java.util.TimerTask;

public class AsyncFactory
{
    public static TimerTask asyncStoreChat(final MsgBody msgBody)
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                SpringUtils.getBean(DataBaseStore.class).persistMessage(msgBody);
            }
        };
    }

    public static TimerTask asyncStoreGroup(final GroupHistoryDO groupHistoryDO)
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                SpringUtils.getBean(DataBaseStore.class).persistGroup(groupHistoryDO);
            }
        };
    }
}

