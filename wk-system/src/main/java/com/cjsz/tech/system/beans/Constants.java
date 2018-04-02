package com.cjsz.tech.system.beans;

/**
 * Created by Administrator on 2016/11/8 0008.
 */
public class Constants {

	/**
     * 新增成功！
     */
    public static final String ACTION_ADD = "新增成功！";

    /**
     * 更新成功！
     */
    public static final String ACTION_UPDATE = "更新成功！";
    
    /**
     * 删除成功！
     */
    public static final String ACTION_DELETE = "删除成功！";
    /**
     * 操作失败！
     */
    public static final String ACTION_ERROR = "操作失败！";

    /**
     * 操作成功！
     */
    public static final String ACTION_SUCCESS = "操作成功！";

    /**
     * 数据异常！
     */
    public static final String EXCEPTION = "数据异常！";
    
    /**
     * 数据加载成功！
     */
    public static final String LOAD_SUCCESS = "数据加载成功！";
    
    /**
     * 登录超时！
     */
    public static final String OVER_TIME ="登录超时,请重新登录！";
    
    /**
     * 登录成功！
     */
    public static final String LOGIN_SUCCESS ="登录成功！";

    /**
     * 登录失败！
     */
    public static final String LOGIN_FAIL ="登录失败！";

    /**
     * 注册成功！
     */
    public static final String REG_SUCCESS ="注册成功！";

    /**
     * 注册失败！
     */
    public static final String REG_FAIL ="注册失败！";

    /**
     * 签到成功！
     */
    public static final String SIGN_SUCCESS ="签到成功！";

    /**
     * 签到失败！
     */
    public static final String SIGN_FAIL ="签到失败！";

    /**
     * 是否删除关联用户？
     */
    public static final String DELETE_USER_REL = "是否删除关联用户？";
    
    /**
     * 角色与菜单或用户存在关联,是否删除？
     */
    public static final String DELETE_ROLE_USER_RES_REL = "角色与菜单或用户存在关联,是否删除？";

    /**
     * 角色与菜单户存在关联,是否删除？
     */
    public static final String DELETE_ROLE_RES_REL = "角色与菜单存在关联,是否删除？";
   
    /**
     * admin
     */
    public static final String ADMIN_USER_NAME = "admin";
    
    /**
     * 管理员
     */
    public static final String ADMIN_REAL_NAME = "管理员";
    /**
     * 系统管理员
     */
    public static final String ADMIN_ROLE_NAME = "系统管理员";
    
    /**
     * 组织名称不可为空！
     */
    public static final String ORG_NAME_NULL = "组织名称不可为空！";
    
    /**
     * 机构名称不可为空！
     */
    public static final String ORGEXTEND_NAME_NULL = "机构名称不可为空！";
    
    /**
     * 机构名称被占用！
     */
    public static final String ORGEXTEND_NAME_EXIST = "机构名称被占用！";
    
    /**
     * 机构简称不可为空！
     */
    public static final String SHORT_NAME_NULL = "机构简称不可为空！";
    
    /**
     * 机构简称被占用！
     */
    public static final String SHORT_NAME_EXIST = "机构简称被占用！";
    
    /**
     * 用户名称不可为空！
     */
    public static final String USER_NAME_NULL = "用户名称不可为空！";

    /**
     * 用户名已被注册！
     */
    public static final String USER_NAME_REPEAT = "用户名已被注册！";
    
    /**
     * 用户密码不可为空！
     */
    public static final String USER_PWD_NULL = "用户密码不可为空！";

    /**
     * 用户手机不可为空！
     */
    public static final String USER_PHONE_NULL = "用户手机不可为空！";

    /**
     * 该手机号已绑定其他账号！
     */
    public static final String USER_PHONE_REPEAT = "该手机号已绑定其他账号！";

    /**
     * 用户邮箱不可为空！
     */
    public static final String USER_EMAIL_NULL = "用户邮箱不可为空！";

    /**
     * 该邮箱已绑定其他账号！
     */
    public static final String USER_EMAIL_REPEAT = "该邮箱已绑定其他账号！";

    /**
     * 未绑定用户！
     */
    public static final String NUMBER_NOT_BIND = "未绑定用户！";
    
    /**
     * 请选择组织！
     */
    public static final String ORG_PID_NULL = "请选择组织！";
    
    /**
     * 请选择角色！
     */
    public static final String ROLE_NOT_NULL = "请选择角色！";
    
    /**
     * 角色名称被占用！
     */
    public static final String ROLE_NAME_EXIST = "角色名称被占用！";

    /**
     * 项目名称被占用！
     */
    public static final String PROJECT_NAME_EXIST = "项目名称被占用！";

    /**
     * 项目编号被占用！
     */
    public static final String PROJECT_CODE_EXIST = "项目编号被占用！";
    
    /**
     * 组织不存在！
     */
    public static final String ORG_NOT_EXIST = "组织不存在!";
    
    /**
     * 角色不存在！
     */
    public static final String ROLE_NOT_EXIST = "角色不存在!";

    /**
     * 项目不存在！
     */
    public static final String PROJECT_NOT_EXIST = "项目不存在!";
    
    /**
     * 用户不存在！
     */
    public static final String USER_NOT_EXIST = "用户不存在!";
    
    /**
     * 启用
     */
    public static final Integer ENABLE = 1; //启用
    
    /**
     * 停用
     */
    public static final Integer DISABLE = 2;  //停用
    
    /**
     * 已删除
     */
    public static final Integer IS_DELETE = 1; //已删除
    
    /**
     * 未删除
     */
    public static final Integer NOT_DELETE = 2;  //未删除
    
    /**
     * 标记角色类型1为系统管理员
     */
    public static final Integer IS_ADMIN_ROLE = 1;
    
    /**
     * 标记角色类型2为不是系统管理员
     */
    public static final Integer NO_ADMIN_ROLE = 2;
    
    
    /**
     * 超级管理员角色id
     */
    public static final String SUPER_ADMIN_ROLE = "1"; //超级管理员角色id
    
    /**
     * 没有权限!
     */
    public static final String INIT_REFUSED = "没有权限!";
    
    /**
     * 默认密码为123456
     */
    public static final String ADMIN_PWD = "123456";
    
    /**
     * token失效！
     */
    public static final String TOKEN_FAILED = "登录超时，请重新登录!";
    
    /**
     * 密码错误！
     */
    public static final String PWD_ERROR = "密码错误!";
    
    /**
     * 旧密码输入错误！
     */
    public static final String OLDPWD_ERROR = "旧密码输入错误！";
    
    /**
     * 用户名已存在！
     */
    public static final String USER_NAME_EXIST = "用户名已存在！";
    
    /**
     * 不可删除该组织！
     */
    public static final String ORG_NOT_DELETE = "不可删除该组织！";
    
    /**
     * 不可删除管理员角色！
     */
    public static final String ROLE_NOT_DELETE = "不可删除管理员角色！";
    
    /**
     * 角色下有用户，不可删除！
     */
    public static final String USER_IN_ROLE = "角色下有用户，不可删除！";

    /**
     * 用户已冻结，登录失败！
     */
    public static final String USER_DISABLE = "用户已冻结，登录失败！";
    
    /**
     * 系统管理员用户不可删除！
     */
    public static final String USER_NOT_DELETE = "系统管理员用户不可删除！";
    
    /**
     * 驳回
     */
    public static final Integer AUDIT_STATUS_NO = 1; //驳回
    
    /**
     * 同意
     */
    public static final Integer AUDIT_STATUS_YES = 2; //同意
    
    /**
     * 待审核
     */
    public static final Integer AUDIT_STATUS_WAIT = 3; //待审核
    
    /**
     * 已审核，不能重复审核！
     */
    public static final String IS_AUDIT = "已审核，不能重复审核！";
    
    /**
     * 设备审核记录不存在！
     */
    public static final String DEVICE_AUDIT_NOT_EXIST = "设备审核记录不存在!";
    
    /**
     * 设备不存在！
     */
    public static final String DEVICE_NOT_EXIST = "设备不存在!";
    
    /**
     * 用户反馈记录不存在!！
     */
    public static final String FEEDBACK_NOT_EXIST = "用户反馈记录不存在!";
    
    /**
     * 未回复
     */
    public static final Integer REPLY_STATUS_NO = 1; 
    
    /**
     * 已回复
     */
    public static final Integer REPLY_STATUS_YES = 2;

    /**
     * 资讯不存在！
     */
    public static final String NEW_NOT_EXIST = "资讯不存在！";

    /**
     * 期刊不存在！
     */
    public static final String JOURNAL_NOT_EXIST = "期刊不存在！";
    
    /**
     * 报纸不存在！
     */
    public static final String PAPER_NOT_EXIST = "报纸不存在！";
    
    /**
     * 版本使用中，不可删除！
     */
    public static final String DEVICE_REl_EXIST = "版本使用中，不可删除！";

    /**
     * 数据包已分配给机构，不可删除！
     */
    public static final String PKG_ORG_EXIST = "数据包已分配给机构，不可删除！";



    /**
     * 资源类型为图书
     */
	public static Integer BOOK_TYPE = 1;
	
	/**
     * 资源类型为新闻
     */
	public static Integer NEWS_TYPE = 2;

    /**
     * 新闻未发布！
     */
    public static final String NEW_NOT_PUBLISH = "新闻未发布！";

    /**
     * 期刊未发布！
     */
    public static final String JOURNAL_NOT_PUBLISH = "期刊未发布！";

    /**
     * 分类不存在！
     */
    public static final String CAT_NOT_EXIST = "分类不存在！";

    /**
     * 分类名称重复！
     */
    public static final String CATNAME_REPETITION = "分类名称重复！";

    /**
     * 分类编号重复！
     */
    public static final String CATCODE_REPETITION = "分类编号重复！";

    /**
     * 登录方式不能为空！
     */
    public static final String LOGIN_BIND_NULLTYPE = "登录方式不能为空！";

    /**
     * 机构不能为空！
     */
    public static final String ORG_NULL_FALSE = "机构不能为空！";

    /**
     * 请选择正确的父级！
     */
    public static final String CLASS_PID_ERROR = "请选择正确的父级！";


    /**
     * 登录OPENID不能为空！
     */
    public static final String LOGIN_BIND_NULLOPENID = "登录OPENID不能为空！";

    public static final String LOGIN_BIND_NOUSER = "没有该绑定账号！";

    public static final String LOGIN_BIND_NOTSUPPORT = "不支持的绑定类型";

    public static final String LOGIN_BIND_AREADYBIND = "已绑定过该账号";

    public static final String LOGIN_BIND_SUCCESS = "绑定登录成功";

    public static final String MEMBER_NOTREG = "用户不存在";

    /**
     * 邮件发送成功！
     */
    public static final String EMAIL_SEND_SUCCESS = "邮件发送成功！";
    /**
     * 邮件发送失败！
     */
    public static final String EMAIL_SEND_FAILED = "邮件发送失败！";
    /**
     * 邮箱已被注册！
     */
    public static final String EMAIL_REG = "邮箱已被注册！";

    /**
     * 充值成功！
     */
    public static final String PAY_SUCCESS = "充值成功！";

}
