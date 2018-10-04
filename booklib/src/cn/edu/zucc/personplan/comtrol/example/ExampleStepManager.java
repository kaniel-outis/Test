package cn.edu.zucc.personplan.comtrol.example;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.edu.zucc.personplan.itf.IStepManager;
import cn.edu.zucc.personplan.model.BeanPlan;
import cn.edu.zucc.personplan.model.BeanStep;
import cn.edu.zucc.personplan.util.BaseException;
import cn.edu.zucc.personplan.util.DBUtil;

public class ExampleStepManager implements IStepManager {
	/**
	 * 添加步骤
	 * 新填的步骤序号为当前计划最大步骤序号+1
	 * 注意：需完成字符串和时间类型的转换，添加后需调整计划表中相应的数量值
	 * @param plan
	 * @param name
	 * @param planstartdate
	 * @param planfinishdate
	 * @throws BaseException
	 */
	@Override
	public void add(BeanPlan plan, String name, String planstartdate,
			String planfinishdate) throws BaseException {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			String sql = "select max(step_order) from tbl_step where plan_id=?";
			java.sql.PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, plan.getPlan_id());
			java.sql.ResultSet rs = pst.executeQuery();
			int mx=1;
			if(rs.next()) 
				mx=rs.getInt(1)+1;
			System.out.println("mx="+mx);
			sql = "insert into tbl_step(plan_id,step_order,step_name,plan_begin_time,plan_end_time) values(?,?,?,?,?)";
			pst = conn.prepareStatement(sql);
			// 数据应该只能从显示的列中读取？
			pst.setInt(1, plan.getPlan_id());
			System.out.println("order="+plan.getPlan_order());
			System.out.println("plan_id="+plan.getPlan_id());
			System.out.println("plan_name="+plan.getPlan_name());
			pst.setInt(2, mx);
			pst.setString(3, name);
			pst.setDate(4, java.sql.Date.valueOf(planstartdate));
			pst.setDate(5, java.sql.Date.valueOf(planfinishdate));
			pst.execute();
			//step_cnt++
			sql = "update tbl_plan set step_count=step_count+1 where plan_id = ?";
			pst = conn.prepareStatement(sql);
			System.out.println("plan_id222="+plan.getPlan_id());
			pst.setInt(1, plan.getPlan_id());
			pst.execute();
				
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return;
	}
	/**
	 * 提取当前plan的步骤
	 * @return
	 * @throws BaseException
	 */
	@Override
	public List<BeanStep> loadSteps(BeanPlan plan) throws BaseException {
		Connection conn = null;
		List<BeanStep> result=new ArrayList<BeanStep>();
		try {
			conn = DBUtil.getConnection();
			String sql="select step_id,plan_id,step_order,step_name,plan_begin_time,plan_end_time,real_begin_time,real_end_time from tbl_step where plan_id= ? ";
			java.sql.PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, plan.getPlan_id());
			java.sql.ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				BeanStep p=new BeanStep();
				p.setStep_id(rs.getInt(1));
				p.setPlan_id(rs.getInt(2));			
				p.setStep_order(rs.getInt(3));
				p.setStep_name(rs.getString(4));
				p.setPlan_begin_time(rs.getDate(5));
				p.setPlan_end_time(rs.getDate(6));
				p.setReal_begin_time(rs.getDate(7));
				p.setReal_end_time(rs.getDate(8));
				result.add(p);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}	
		return result;
	}
	/**
	 * 删除步骤，
	 * 注意删除后需调整计划表中对应的步骤数量
	 * @param step
	 * @throws BaseException
	 */
	@Override
	public void deleteStep(BeanStep step) throws BaseException {
		// TODO Auto-generated method stub
		Connection conn = null;
		System.out.println("getStep_id="+step.getStep_id()+"etPlan_id="+step.getPlan_id()+"getStep_orde="+step.getStep_order());
		System.out.println("name="+step.getStep_name()+"begin_time="+step.getPlan_begin_time());
		try {
			conn = DBUtil.getConnection();
		//	String sql= "select * from ";
			String sql="delete from tbl_step where step_id=?";
			java.sql.PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, step.getStep_id());
			int num = pst.executeUpdate();
			if(num==0) throw new BaseException("没有该步骤");
			else {
				System.out.println("plan_id="+step.getPlan_id());
				sql = "update tbl_plan set step_count=step_count-1 where plan_id = ?";
				pst = conn.prepareStatement(sql);
				pst.setInt(1, step.getPlan_id());
				pst.execute();
				//调整序号 从 1-n
				sql = "update tbl_step set step_order = step_order-1 where step_order > ?";
				pst = conn.prepareStatement(sql);
				pst.setInt(1, step.getStep_order());
				pst.execute();						
			}
		//	Date dt = (Date) step.getReal_begin_time();
		//	Date dt2 =(Date) step.getReal_end_time();
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return;
		}
	/**
	 * 设置当前步骤的实际开始时间，及对应的计划表中已开始步骤数量
	 * 
	 * @param step
	 * @throws BaseException
	 */
	@Override
	public void startStep(BeanStep step) throws BaseException {
		// TODO Auto-generated method stub
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			String sql="select real_begin_time from tbl_step where step_id = ?";
			java.sql.PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, step.getStep_id());
			java.sql.ResultSet rs = pst.executeQuery();
			if(rs.next()) {
				if(rs.getDate(1)!=null) 
					throw new BaseException("当前步骤已经开始!");
			}
			sql="update tbl_step set real_begin_time = ? where step_id = ?";
			pst = conn.prepareStatement(sql);
			/*
			Date d = new Date();
			setDate(1,new java.sql.Date(d.getTime());
			 */
			pst.setDate(1, new java.sql.Date(System.currentTimeMillis()));
			pst.setInt(2, step.getStep_id());
			pst.execute();
			sql = "update tbl_plan set start_step_count=start_step_count+1 where plan_id = ?";
			pst = conn.prepareStatement(sql);
			pst.setInt(1, step.getPlan_id());
			pst.execute();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 设置当前步骤的实际完成时间，及对应的计划表中已完成步骤数量
	 * @param step
	 * @throws BaseException
	 */
	@Override
	public void finishStep(BeanStep step) throws BaseException {
		// TODO Auto-generated method stub
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			String sql="select real_end_time from tbl_step where step_id = ?";
			java.sql.PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, step.getStep_id());
			java.sql.ResultSet rs = pst.executeQuery();
			if(rs.next()) {
				if(rs.getDate(1)!=null) 
					throw new BaseException("当前步骤已经结束!");
			}
			sql = "update tbl_step set real_end_time = ? where step_id = ?";
			pst = conn.prepareStatement(sql);
			pst.setDate(1, new java.sql.Date(System.currentTimeMillis()));
			pst.setInt(2, step.getStep_id());
			pst.execute();
			//开始步骤-1
			sql = "update tbl_plan set start_step_count=start_step_count-1 where plan_id = ?";
			pst = conn.prepareStatement(sql);
			pst.setInt(1, step.getPlan_id());
			pst.execute();
			//完成步骤+1
			sql = "update tbl_plan set finished_step_count=finished_step_count+1 where plan_id = ?";
			pst = conn.prepareStatement(sql);
			pst.setInt(1, step.getPlan_id());
			pst.execute();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 调整当前步骤的顺序号
	 * 注意：数据库表中，plan_id,step_order上建立了唯一索引，调整当前步骤的序号值和上一步骤的序号值时不能出现序号值一样的情况
	 * @param step
	 * @throws BaseException
	 */
	@Override
	public void moveUp(BeanStep step) throws BaseException {
		// TODO Auto-generated method stub
		//找到序号值比他大且没有被占用的地方
		Connection conn = null;
		try {
			//注意事务处理
			conn = DBUtil.getConnection();
			int now_step_order = step.getStep_order();
			if(now_step_order==1) throw new BaseException("当前序号为1，不能上移!");
			String sql = "update tbl_step set step_order = "+0+" where step_order = ?";
			java.sql.PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, step.getStep_order());
			pst.execute();
			sql = "update tbl_step set step_order = "+now_step_order+" where step_order = ?";
			pst = conn.prepareStatement(sql);
			pst.setInt(1, now_step_order-1);
			pst.execute();
			sql = "update tbl_step set step_order = ? where step_order = "+0+"";
			pst = conn.prepareStatement(sql);
			pst.setInt(1, now_step_order-1);
			pst.execute();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void moveDown(BeanStep step) throws BaseException {
		// TODO Auto-generated method stub
		Connection conn = null;
		try {
			//注意事务处理
			conn = DBUtil.getConnection();
			String sq = "select count(*) from tbl_step where plan_id=?";
			java.sql.PreparedStatement pst = conn.prepareStatement(sq);
			pst.setInt(1, step.getPlan_id());
			java.sql.ResultSet rs = pst.executeQuery();			
			int now_step_order = step.getStep_order();
			if(rs.next())
				if(now_step_order==rs.getInt(1)) throw new BaseException("已为最后一个,无法下移!");
			String sql = "update tbl_step set step_order = "+0+" where step_order = ?";
			pst = conn.prepareStatement(sql);
			pst.setInt(1, step.getStep_order());
			pst.execute();
			sql = "update tbl_step set step_order = "+now_step_order+" where step_order = ?";
			pst = conn.prepareStatement(sql);
			pst.setInt(1, now_step_order+1);
			pst.execute();
			sql = "update tbl_step set step_order = ? where step_order = "+0+"";
			pst = conn.prepareStatement(sql);
			pst.setInt(1, now_step_order+1);
			pst.execute();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}

}
