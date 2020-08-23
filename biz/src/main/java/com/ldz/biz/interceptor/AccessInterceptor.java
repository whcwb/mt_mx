package com.ldz.biz.interceptor;

import com.ldz.sys.constant.Dict;
import com.ldz.sys.mapper.SysYhJsMapper;
import com.ldz.sys.model.SysGn;
import com.ldz.sys.model.SysYh;
import com.ldz.sys.service.GnService;
import com.ldz.sys.service.YhService;
import com.ldz.util.commonUtil.JwtUtil;
import com.ldz.util.spring.SpringContextUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 访问接口控制
 *
 * @author 李彬彬
 */
@Log4j2
public class AccessInterceptor extends HandlerInterceptorAdapter {

	private final GnService gnService;

	private final YhService yhService;

	private final SysYhJsMapper sysYhJsMapper;

	private final StringRedisTemplate redisDao;

	public AccessInterceptor(StringRedisTemplate redisTemp) {
		this.gnService = SpringContextUtil.getBean(GnService.class);
		this.yhService = SpringContextUtil.getBean(YhService.class);
		this.sysYhJsMapper = SpringContextUtil.getBean(SysYhJsMapper.class);
		this.redisDao = redisTemp;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		// 查看请求类型
		String method = request.getMethod();
		if ("OPTIONS".equals(method)) {
			// 如果收到的是跨域预请求消息，直接响应，返回true，以便后续跨域请求成功
			return true;
		}

		// 测试代码
		// 访问权限值
		// String userid = "1";
		// String token =
		// "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3Y3BtcyIsImF1ZCI6IndjcG1zIiwibG9naW5OYW1lIjoiYWRtaW5pIiwiaXNzIjoid2NwbXMiLCJ1c2VySWQiOiIxIn0.vok82zo-zveVlXrjKxgJiRRdXqKGpv1PFBngxhyR-Cg";
		String userid = request.getHeader("userid");
		String token = request.getHeader("token");

		if (token == null)
		{
			token = request.getParameter("token");
		}
		if (userid == null)
		{
			userid = request.getParameter("userid");
		}

		if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(token)){
			request.getRequestDispatcher("/authFiled").forward(request, response);
			return false;
		}

		// 验证用户状态
		SysYh user = yhService.findById(userid);
		if (!Dict.UserStatus.VALID.getCode().equals(user.getZt())){
			request.getRequestDispatcher("/authFiled").forward(request, response);
			return false;
		}
		try {
			// 验证访问者是否合法
			String userId = JwtUtil.getClaimAsString(token, "userId");
			if (!userid.equals(userId)){
				request.getRequestDispatcher("/authFiled").forward(request, response);
				return false;
			}
			String value = redisDao.boundValueOps(userid).get();
			if (StringUtils.isEmpty(value) || !value.equals(token)){
				request.getRequestDispatcher("/authFiled").forward(request, response);
				return false;
			}
			request.setAttribute("userInfo", user);
			request.setAttribute("orgCode", user.getJgdm());
			request.setAttribute("orgCodes",user.getJgdms());
		} catch (Exception e) {
			return false;
		}

		return super.preHandle(request, response, handler);
	}

	private boolean authCheck(boolean expression,HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("/999").forward(request, response);
		return true;
	}

	private boolean checkPermission(SysYh user, HttpServletRequest request) {
		return checkPermissionByRedis(user,request);
	}
	private boolean checkPermissionNew(SysYh user, HttpServletRequest request) {
		String redisVal = redisDao.boundValueOps(user.getYhid()+"-apiQz").get();
		if (StringUtils.isEmpty(redisVal)) {
			return false;
		}

		List<String> qzs = Arrays.asList(redisVal.split(","));
		if (CollectionUtils.isEmpty(qzs)){
			return false;
		}

		String apiqz = getApiQz(request.getRequestURI());
		if (StringUtils.isEmpty(apiqz)){
			return false;
		}
		return qzs.contains(apiqz);
	}

	private boolean checkPermissionByRedis(SysYh user, HttpServletRequest request){
		boolean flag =false;
		// 获取用户的所有角色 Id
		List<String> sysYhJs = sysYhJsMapper.findJsIdByYhId(user.getYhid());
		if(CollectionUtils.isEmpty(sysYhJs)){
			return false;
		}
		String apiqz = getApiQz(request.getRequestURI());
		if (StringUtils.isEmpty(apiqz)) {
			return false;
		}
		for(String s : sysYhJs){
			String s1 = redisDao.boundValueOps("permission_" + s).get();
			List<String> list = Arrays.asList(s1.split(","));
			if(CollectionUtils.isNotEmpty(list) && list.contains(apiqz)){
				flag = true;
				break;
			}
		}
		return flag;
	}


	private boolean checkPermissionOld(SysYh user, HttpServletRequest request) {
		List<SysGn> functions = gnService.getUserFunctions(user);
		if (functions == null || functions.size() == 0)
		{
			return false;
		}

		String uri = request.getRequestURI();
		String apiPrefix = uri.substring(0, uri.indexOf("/", 5) + 1);
		for (SysGn function : functions) {
			if (StringUtils.isEmpty(function.getApiQz()))
			{
				continue;
			}
			if (function.getApiQz().contains(apiPrefix))
			{
				return true;
			}
		}
		return false;
	}

	private String getApiQz(String uri){
		return uri.substring(0, uri.indexOf("/", 5) + 1);
	}

}
