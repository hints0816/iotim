package org.hints.im.pojo.entity;

import lombok.Data;

import java.util.List;

/**
 * @Description TODO
 * @Author 180686
 * @Date 2022/8/17 17:25
 */
@Data
public class GroupDTO {

    private String groupId;
    private String owner;
    private String name;
    private Integer type;
    private Long memberNum;

    private List<String> userlist;

}
