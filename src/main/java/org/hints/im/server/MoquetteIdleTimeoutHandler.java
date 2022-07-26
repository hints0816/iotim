package org.hints.im.server;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2022/7/20 16:48
 */
@ChannelHandler.Sharable
public class MoquetteIdleTimeoutHandler extends ChannelDuplexHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MoquetteIdleTimeoutHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState e = ((IdleStateEvent) evt).state();
            ctx.fireChannelInactive();
            ctx.close();
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("123");
            }
            super.userEventTriggered(ctx, evt);
        }
    }
}
