package org.hints.auth.service;

import org.hints.auth.mapper.ClientMapper;
import org.hints.auth.mapper.UserMapper;
import org.hints.auth.model.Role;
import org.hints.auth.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Service
public class UserServiceDetail implements UserDetailsService {

    Logger logger = LoggerFactory.getLogger(UserServiceDetail.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ClientMapper clientMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.finduser(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在!");
        }
        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(getAuthorities(username))
                .build();
        return userDetails;
    }

    public UserDetails loadUserByUsernameAndVerifycode(String username,String verifycode) throws UsernameNotFoundException {
        User user = userMapper.finduser(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在!");
        }
        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(getAuthorities(username))
                .build();
        logger.info("verifycode:"+verifycode);
        return userDetails;
    }

    //查询该用户的权限集
    public Collection<? extends GrantedAuthority> getAuthorities(String username) {
        List<Role> list1 = new ArrayList<Role>();
        List<HashMap> list = userMapper.getAuth(username);
        for (HashMap record : list) {
            Role role = new Role();
            role.setRoleId(Long.valueOf(record.get("ROLE_ID").toString()));
            role.setRoleName(record.get("ROLE_NAME").toString());
            list1.add(role);
        }
        return list1;
    }
}