/*package cn.edu.zucc.personplan.comtrol.example;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.edu.zucc.personplan.itf.IPlanManager;
import cn.edu.zucc.personplan.model.BeanPlan;
import cn.edu.zucc.personplan.model.BeanUser;
import cn.edu.zucc.personplan.util.BaseException;
import cn.edu.zucc.personplan.util.DBUtil;
//import cn.edu.zucc.personplan.util.DBUtil2;

public class ExamplePlanManager implements IPlanManager {
	/**
	 * 添加计划
	 * 要求新增的计划的排序号为当前用户现有最大排序号+1
	 * 注意：当前登陆用户可通过 BeanUser.currentLoginUser获取
	 * @param name  计划名称
	 * @throws BaseException
	 */
	@Override
	public BeanPlan addPlan(String name) throws BaseException {
		Connection conn = null;
		
		try {
			BeanUser bu =  new BeanUser();
			conn = DBUtil2.getConnection();
			String sql = "select max(plan_order) from tbl_plan where user_id=? order by plan_id";
			java.sql.PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, BeanUser.currentLoginUser.getUser_id());
			java.sql.ResultSet rs = pst.executeQuery();
			int num=1;
			if(rs.next())
				num = rs.getInt(1)+1;
			sql="insert into tbl_plan(user_id,plan_order,plan_name,create_time,step_count,start_step_count,finished_step_count) values(?,?,?,?,0,0,0)";
			pst = conn.prepareStatement(sql);
			pst.setString(1, BeanUser.currentLoginUser.getUser_id());
			pst.setInt(2, num);
			pst.setString(3, name);
			pst.setDate(4, new Date(System.currentTimeMillis()));
			pst.execute();
			BeanPlan bp = new BeanPlan();
			bp.setCreate_time(new Date(System.currentTimeMillis()));
			bp.setFinished_step_count(0);
			bp.setPlan_name(name);
			bp.setPlan_order(num);
			bp.setStart_step_count(0);
			bp.setUser_id(BeanUser.currentLoginUser.getUser_id());
			return bp;
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
	
	/**
	 * 提取当前用户所有计划
	 * @return
	 * @throws BaseException
	 */
	@Override
	public List<BeanPlan> loadAll() throws BaseException {
		Connection conn = null;
		List<BeanPlan> result=new ArrayList<BeanPlan>();
		try {
			conn = DBUtil2.getConnection();
			String sql="select * from tbl_plan where user_id=?";
			java.sql.PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, BeanUser.currentLoginUser.getUser_id());
			java.sql.ResultSet rs = pst.executeQuery();
		
			while(rs.next()) {
				BeanPlan p=new BeanPlan();
				p.setPlan_id(rs.getInt(1));
				p.setUser_id(rs.getString(2));
				p.setPlan_order(rs.getInt(3));
				p.setPlan_name(rs.getString(4));
				p.setCreate_time(rs.getDate(5));
				p.setStep_count(rs.getInt(6));
				p.setStart_step_count(rs.getInt(7));
				p.setFinished_step_count(rs.getInt(8));
				result.add(p);
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}

		return result;
	}
	/**
	 * 删除计划，如果计划下存在步骤，则不允许删除
	 * @param plan
	 * @throws BaseException
	 */
	@Override
	public void deletePlan(BeanPlan plan) throws BaseException {
		Connection conn = null;
		try {
			conn = DBUtil2.getConnection();
			int num = plan.getPlan_order();
			String sql="select step_count from tbl_plan where plan_id=?";
			java.sql.PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, plan.getPlan_id());
			java.sql.ResultSet rs = pst.executeQuery();
			if(rs.next()) {
				System.out.println("tag:"+rs.getInt(1));
				if(rs.getInt(1)!=0)
					throw new BaseException("有步骤存在!");
				else {
					System.out.println("plan_id="+plan.getPlan_id());
					sql="delete from tbl_plan where plan_id = ? ";
					pst = conn.prepareStatement(sql);
					pst.setInt(1, plan.getPlan_id());
					pst.execute();
					sql="update tbl_plan set plan_order = plan_order-1 where plan_order>?";
					pst = conn.prepareStatement(sql);
					pst.setInt(1, num);
					pst.execute();
				}

			}else 
				throw new BaseException("");
			
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	public static void main (String args[]) throws BaseException{
		ExamplePlanManager ep = new ExamplePlanManager();
		//ep.addPlan("first_plan");
	}
}*/
