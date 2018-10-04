package cn.edu.zucc.personplan.comtrol.example;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import cn.edu.zucc.personplan.itf.IStepManager;
import cn.edu.zucc.personplan.model.BeanPlan;
import cn.edu.zucc.personplan.model.BeanStep;
import cn.edu.zucc.personplan.model.BeanUser;
import cn.edu.zucc.personplan.util.BaseException;
import cn.edu.zucc.personplan.util.DBUtil;
import cn.edu.zucc.personplan.util.HibernateUtil;

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
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		String hql="from BeanStep where plan_id="+plan.getPlan_id()+" order by step_order DESC";
		List<BeanStep> res = session.createQuery(hql).list();
		int step_ord;
		if(res.size()==0)
			step_ord=1;
		else 
			step_ord=res.get(0).getStep_order()+1;
		System.out.println("step_ord="+step_ord);
		BeanStep bs = new BeanStep();
		bs.setPlan_id(plan.getPlan_id());
		bs.setStep_order(step_ord);
		bs.setStep_name(name);
		//pst.setString(4, planstartdate);
		bs.setPlan_begin_time(java.sql.Date.valueOf(planstartdate));
		bs.setPlan_end_time(java.sql.Date.valueOf(planfinishdate));
		bs.setCreate_time(new java.sql.Date(System.currentTimeMillis()));
		session.save(bs);
		tx.commit();
		session.clear();
		tx = session.beginTransaction();
		BeanPlan tmp = session.get(BeanPlan.class, plan.getPlan_id());
		tmp.setStep_count(tmp.getStep_count()+1);
		session.update(tmp);
		tx.commit();
		return;
	}
	/**
	 * 提取当前plan的步骤
	 * @return
	 * @throws BaseException
	 */
	@Override
	public List<BeanStep> loadSteps(BeanPlan plan) throws BaseException {
		Session session = HibernateUtil.getSession();
		String hql="from BeanStep where plan_id= "+plan.getPlan_id()+"";
		List<BeanStep> res = session.createQuery(hql).list();
		return res;
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
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		BeanStep bss = session.get(BeanStep.class, step.getStep_id());
		if(bss==null) 
			throw new BaseException("未选择步骤!");
		else {
			int flag;
			int step_order=bss.getStep_order();
			if(bss.getReal_end_time()!=null) flag=1;
			else flag=0;
			session.delete(bss);
			tx.commit();
			session.clear();
			List<BeanStep> bs_all = session.createQuery("from BeanStep where plan_id="+step.getPlan_id()+"").list();
			Transaction txx = session.beginTransaction();
			for(BeanStep bs:bs_all) {
				if(bs.getStep_order()>step_order) {
					bs.setStep_order(bs.getStep_order()-1);
					session.update(bs);
				}
			}
			txx.commit();
			session.clear();			
			
			Transaction txxx = session.beginTransaction();
			BeanPlan bp= session.get(BeanPlan.class, step.getPlan_id());
			//判断步骤是否完成，更新已完成数量
			bp.setStep_count(bp.getStep_count()-1);
			session.update(bp);
			if(flag==1) {
				bp.setFinished_step_count(bp.getFinished_step_count()-1);
				session.update(bp);
			}
			txxx.commit();
		}
		
	}
	/**
	 * 设置当前步骤的实际开始时间，及对应的计划表中已开始步骤数量
	 * 
	 * @param step
	 * @throws BaseException
	 */
	@Override
	public void startStep(BeanStep step) throws BaseException {
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		BeanStep bs = session.get(BeanStep.class, step.getStep_id());
		if(bs==null) throw new BaseException("不存在步骤!");
		else {
			if(bs.getReal_begin_time()!=null)
				throw new BaseException("当前步骤已经开始!");
			bs.setReal_begin_time(new java.sql.Date(System.currentTimeMillis()));
			session.update(bs);
			tx.commit();
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
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		BeanStep bs = session.get(BeanStep.class, step.getStep_id());
		if(bs==null) throw new BaseException("不存在步骤!");
		else {
			if(bs.getReal_end_time()!=null)
				throw new BaseException("当前步骤已经结束!");
			bs.setReal_end_time(new java.sql.Date(System.currentTimeMillis()));
			session.update(bs);
			tx.commit();
			session.clear();
			Transaction txx = session.beginTransaction();
			BeanPlan bp = session.get(BeanPlan.class, step.getPlan_id());
			bp.setFinished_step_count(bp.getFinished_step_count()+1);
			session.update(bp);
			txx.commit();
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
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		int now_step_order = step.getStep_order();
		if(now_step_order==1) throw new BaseException("当前序号为1，不能上移!");
		BeanStep bs = session.get(BeanStep.class, step.getStep_id());
		bs.setStep_order(0);
		session.update(bs);
		
		String hql="update BeanStep set step_order="+now_step_order+"";
		hql+="where step_order="+(now_step_order-1)+"";
		Query query = session.createQuery(hql);
		int i=query.executeUpdate();

		hql="update BeanStep set step_order="+(now_step_order-1)+"";
		hql+="where step_order="+0+"";
		query = session.createQuery(hql);
		i=query.executeUpdate();
		tx.commit();
		session.clear();
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
	//new
	public void changeStep(String name,String sd) throws BaseException{
		Session session =HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		BeanStep bs =session.get(BeanStep.class, BeanStep.currentStep.getStep_id());
		if(bs!=null) {
			bs.setStep_name(name);
			bs.setPlan_begin_time(java.sql.Date.valueOf(sd));
			session.update(bs);
			tx.commit();
		}
		return;
	}
}
