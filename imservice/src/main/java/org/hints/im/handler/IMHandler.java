package org.hints.im.handler;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2022/8/11 10:01
 */
abstract public class IMHandler<T> {

    protected long publish(){
        return -1;
    }

    protected long saveAndPublish(){
        return -1;
    }

}
