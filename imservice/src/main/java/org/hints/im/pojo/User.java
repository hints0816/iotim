package org.hints.im.pojo;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;

/**
 * @Description TODO
 * @Author hints
 * @Date 2022/9/7 9:41
 */
@Data
@Table("SYS_USER")
public class User {

    @Column(hump = true)
    private Long userId;
    @Column(hump = true)
    private String userName;
    @Column(hump = true)
    private String nickName;
    @Column(hump = true)
    private String email;
    @Column(hump = true)
    private String phoneNumber;
    @Column(hump = true)
    private String sex;
    @Column(hump = true)
    private String avater;
    @Column(hump = true)
    private String salt;
    @Column(hump = true)
    private String status;
    @Column(hump = true)
    private String delFlag;
    @Column(hump = true)
    private String loginIp;
    @Column(hump = true)
    private Date loginDate;
}
