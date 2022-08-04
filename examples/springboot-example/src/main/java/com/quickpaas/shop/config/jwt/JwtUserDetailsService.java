package com.quickpaas.shop.config.jwt;

import com.quickpaas.framework.quickql.Query;
import com.quickpaas.shop.system.domain.SysUser;
import com.quickpaas.shop.system.service.SysUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class JwtUserDetailsService implements UserDetailsService {
    @Autowired
    SysUserService userService;

    public SysUser getByAccount(String name) {
        Query query = new Query();
        query.eq("account", name);
        return userService.findOne(query);
    }



    @Override
    public JwtUserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        if(StringUtils.isEmpty(account)) {
            return null;
        }
        SysUser entity = getByAccount(account);
        if(entity != null) {
            JwtUserDetails jwtUserDetails = new JwtUserDetails(account, entity.getPassword(), new ArrayList<>());
            jwtUserDetails.setName(entity.getName());
            return jwtUserDetails;
        }else {
            return null;
        }
    }
}
