package cn.edu.zucc.personplan.comtrol.example;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import cn.edu.zucc.personplan.util.DbException;
import cn.edu.zucc.personplan.itf.IUserManager;
import cn.edu.zucc.personplan.model.BeanPlan;
import cn.edu.zucc.personplan.model.BeanStep;
import cn.edu.zucc.personplan.model.BeanUser;
import cn.edu.zucc.personplan.util.BaseException;
import cn.edu.zucc.personplan.util.DBUtil;
import cn.edu.zucc.personplan.util.HibernateUtil;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
public class ExampleUserManager implements IUserManager {

	@Override
	public BeanUser reg(String userid, String pwd,String pwd2,int type) throws BaseException {
		// TODO Auto-generated method stub
		Session session=HibernateUtil.getSession();
		BeanUser user=(BeanUser)session.get(BeanUser.class, userid);
		Transaction tx = session.beginTransaction();
		if(user!=null) throw new BaseException("用户已经存在");
		else if(!pwd.equals(pwd2) || pwd.equals("") || pwd2.equals("")) throw new BaseException("两次密码不一致or密码为空");
		else {
			BeanUser bu= new BeanUser();
			bu.setUser_id(userid);
			bu.setUser_pwd(pwd);
			bu.setRegister_time(new Date(System.currentTimeMillis()));
			bu.setUser_type(type);
			session.save(bu);
			tx.commit();
			return bu;
		}		
	}

	
	@Override
	public BeanUser login(String userid, String pwd) throws BaseException {
		// TODO Auto-generated method stub
		Session session = HibernateUtil.getSession();
		BeanUser bu = (BeanUser)session.get(BeanUser.class, userid);
		Transaction tx = session.beginTransaction();
		if(bu == null) throw new BaseException("账号不存在!");
		else if(bu.getUser_pwd().equals(pwd)) {
			return bu;
		}
		else throw new BaseException("密码有误!");

	}

	@Override
	public void changePwd(BeanUser user, String oldPwd, String newPwd,
			String newPwd2) throws BaseException {
		// TODO Auto-generated method stub
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		BeanUser bu = (BeanUser)session.get(BeanUser.class, user.getUser_id());
		if(!newPwd.equals(newPwd2)) throw new BaseException("两次输入密码不一样!");
		else if(!user.getUser_pwd().equals(oldPwd)) throw new BaseException("原密码不正确!");
		else if("".equals(newPwd)) throw new BaseException("新密码不能为空!");
		bu.setUser_pwd(newPwd);
		session.update(bu);
		tx.commit();	
		return ;
		
	}
	public List<BeanUser> loadAll() throws BaseException {
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		List <BeanUser> res= session.createQuery("from BeanUser").list();
		tx.commit();
		return res;
	}
	
	public void resetUser(BeanUser User) throws BaseException{
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		BeanUser bu = session.get(BeanUser.class, User.getUser_id());
		bu.setUser_pwd(User.getUser_id());
		session.update(bu);
		tx.commit();
	}
	public void deleteUser(BeanUser User) throws BaseException{
		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		BeanUser bu = session.get(BeanUser.class, User.getUser_id());
		String hql="from BeanPlan where user_id='"+User.getUser_id()+"'";
		List <BeanPlan> res= session.createQuery(hql).list();
		//if(res.size()>=1) throw new BaseException("该用户有计划存在！");
		if(res.size()>=1) throw new BaseException("该用户有计划存在！");
		else {
			session.delete(bu);
			tx.commit();
		}
		
	}
}
