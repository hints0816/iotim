package org.hints.im.pojo.entity;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;
import java.util.List;

/**
 * @Description TODO
 * @Author hints
 * @Date 2022/8/17 17:25
 */
@Data
@Table("SYS_GROUP")
public class GroupDTO {

    @Column(hump = true)
    private String groupId;

    @Column(hump = true)
    private Long owner;

    @Column(hump = true)
    private String name;

    @Column(hump = true)
    private Integer type;

    @Column(hump = true)
    private Long membernum;

    @Column(hump = true)
    private Date createdate;

    @Column(hump = true)
    private String img;

    private List<Long> userlist;

}
