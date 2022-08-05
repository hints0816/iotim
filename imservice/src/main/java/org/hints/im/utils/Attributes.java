package org.hints.im.utils;

import io.netty.util.AttributeKey;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2022/8/5 17:29
 */
public interface Attributes {
    AttributeKey<String> SESSION = AttributeKey.newInstance("session");
}
