package org.hints.im.utils;

import io.netty.util.AttributeKey;
import org.hints.im.pojo.User;

/**
 * @Description TODO
 * @Author hints
 * @Date 2022/8/5 17:29
 */
public interface Attributes {
    AttributeKey<User> SESSION = AttributeKey.newInstance("session");
}
