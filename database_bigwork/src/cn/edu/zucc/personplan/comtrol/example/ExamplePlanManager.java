package cn.edu.zucc.personplan.comtrol.example;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import cn.edu.zucc.personplan.itf.IPlanManager;
import cn.edu.zucc.personplan.model.BeanPlan;
import cn.edu.zucc.personplan.model.BeanUser;
import cn.edu.zucc.personplan.util.BaseException;
import cn.edu.zucc.personplan.util.DBUtil;
//import cn.edu.zucc.personplan.util.DBUtil2;
import cn.edu.zucc.personplan.util.HibernateUtil;

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
	//	Connection conn = null;
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		String hql="from BeanPlan where user_id='"+BeanUser.currentLoginUser.getUser_id()+"' order by plan_id DESC";
		List<BeanPlan> res = session.createQuery(hql).list();
		int plan_ord;
		if(res.size()==0)
			plan_ord=1;
		else 
			plan_ord=res.get(0).getPlan_order()+1;
		BeanPlan bp = new BeanPlan();
		bp.setUser(BeanUser.currentLoginUser);
		bp.setPlan_order(plan_ord);
		bp.setPlan_name(name);
		bp.setCreate_time(new Date(System.currentTimeMillis()));
		bp.setStep_count(0);
		bp.setFinished_step_count(0);
		bp.setStart_step_count(0);
		session.save(bp);
		tx.commit();
		return bp;
	}

	@Override
	public List<BeanPlan> loadAll() throws BaseException {
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		String hql="from BeanPlan  where user_id='"+BeanUser.currentLoginUser.getUser_id()+"'";
		List <BeanPlan> res= session.createQuery(hql).list();
		tx.commit();
		return res;
	}
	/**
	 * 删除计划，如果计划下存在步骤，则不允许删除
	 * @param plan
	 * @throws BaseException
	 */
	@Override
	public void deletePlan(BeanPlan plan) throws BaseException {
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		String hql="from BeanPlan where plan_id="+plan.getPlan_id()+"";
		List<BeanPlan> res = session.createQuery(hql).list();
		if(res.size()==0) 
			throw new BaseException("未选择计划!");
		else {
			int plan_order=res.get(0).getPlan_order();
			if(res.get(0).getStep_count()!=0)
				throw new BaseException("有步骤存在!");
			else {
				session.delete(res.get(0));
				tx.commit();
				session.clear();
				List<BeanPlan> bp_all = session.createQuery("from BeanPlan where user_id='"+BeanUser.currentLoginUser.getUser_id()+"'").list();
				Transaction txx = session.beginTransaction();
				for(BeanPlan bp:bp_all) {
					if(bp.getPlan_order()>plan_order) {
						bp.setPlan_order(bp.getPlan_order()-1);
						session.update(bp);
					}
				}
				txx.commit();
			}
		}
	}
	//new
	public void changePlan(String name) throws BaseException{
		Session session =HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		BeanPlan bp =session.get(BeanPlan.class, BeanPlan.currentPlan.getPlan_id());
		if(bp!=null) {
			bp.setPlan_name(name);
			session.update(bp);
			tx.commit();
		}
		return;
	}
	
}
