package com.easybuy.user.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 认证类
 * @author Romantic
 * @CreateDate 2020年4月12日
 * @Description
 */
public class UserDetailServiceImpl implements UserDetailsService {

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		System.out.println("username:"+username);
		//构建角色集合
		List<GrantedAuthority> authorities=new ArrayList();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
	
		return new User(username, ""  , authorities);	
		
	}

}
