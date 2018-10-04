package cn.edu.zucc.personplan.model;

import java.sql.Date;
import java.util.Set;


public class BeanUser {
	public static BeanUser currentLoginUser=null;
	public static final String[] tblUserTitle={"用户名","注册时间","用户类型"};
	public static final String[] tableTitles2={"用户名","总计划数","总步骤数","已完成步骤数"};
	public String getCell2(int col){
		if(col==0) return String.valueOf(user_id);
		else return "";
	}
	public static String[] getTabletitles2() {
		return tableTitles2;
	}
	// 增加 planId
	/**
	 * 请自行根据javabean的设计修改本函数代码，col表示界面表格中的列序号，0开始
	 */
	public String getCell(int col){
		if(col==0) return String.valueOf(user_id);
		else if(col==1) return  String.valueOf(register_time);
		else if(col==2) {
			if(user_type==1)
				return "管理员";
			else 
				return "普通用户";
		}
		else return "";
	}
	public static String[] getTblusertitle() {
		return tblUserTitle;
	}
	private String user_id;
	private String user_pwd;
	private Date register_time;
	private int user_type;
	public int getUser_type() {
		return user_type;
	}
	public void setUser_type(int user_type) {
		this.user_type = user_type;
	}
	private Set<BeanPlan> plans;
	public Set<BeanPlan> getPlans() {
		return plans;
	}
	public void setPlans(Set<BeanPlan> plans) {
		this.plans = plans;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_pwd() {
		return user_pwd;
	}
	public void setUser_pwd(String user_pwd) {
		this.user_pwd = user_pwd;
	}
	public Date getRegister_time() {
		return register_time;
	}
	public void setRegister_time(Date register_time) {
		this.register_time = register_time;
	}
	

}
