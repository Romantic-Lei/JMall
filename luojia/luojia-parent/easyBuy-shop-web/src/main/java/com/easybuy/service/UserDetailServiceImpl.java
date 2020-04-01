package com.easybuy.service;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.easybuy.pojo.TbSeller;
import com.easybuy.sellergoods.service.SellerService;

/**
 * 认证类
 * @author Romantic
 * @CreateDate 2020年4月1日
 * @Description
 */
public class UserDetailServiceImpl implements UserDetailsService {

	private SellerService sellerService;
	
	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		// 构建角色列表
		Collection<GrantedAuthority> authorities = new ArrayList();
		authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
		
		TbSeller seller = sellerService.findOne(username);
		if(seller != null) {
			// 商家状态必须是已审核通过才让登陆
			if(seller.getStatus().equals("1")) {
				// springsecurity 会自动将用户名和密码拿去比对看是否正确
				return new User(username, seller.getPassword(), authorities);
			}
			// 为通过审核
			return null;
		}else {
			// 商家不存在
			return null;
		}
		
	}

}
