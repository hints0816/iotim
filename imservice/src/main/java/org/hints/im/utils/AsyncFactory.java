package org.hints.im.utils;

import org.hints.im.persist.DataBaseStore;
import org.hints.im.pojo.MsgBody;

import java.util.TimerTask;

public class AsyncFactory
{
    public static TimerTask asyncStore(final MsgBody msgBody)
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


}

