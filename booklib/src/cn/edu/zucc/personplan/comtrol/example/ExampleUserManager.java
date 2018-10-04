package cn.edu.zucc.personplan.comtrol.example;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import cn.edu.zucc.personplan.util.DbException;
import cn.edu.zucc.personplan.itf.IUserManager;
import cn.edu.zucc.personplan.model.BeanUser;
import cn.edu.zucc.personplan.util.BaseException;
import cn.edu.zucc.personplan.util.DBUtil;
import cn.edu.zucc.personplan.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
public class ExampleUserManager implements IUserManager {

	@Override
	public BeanUser reg(String userid, String pwd,String pwd2) throws BaseException {
		// TODO Auto-generated method stub
		//Session session=HibernateUtil.getSession();
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			String sql="select * from tbl_user where user_id=?";
			java.sql.PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, userid);
			java.sql.ResultSet rs = pst.executeQuery();
			if(rs.next()) throw new BaseException("");
			else if(!pwd.equals(pwd2) || pwd.equals("") || pwd2.equals("")) throw new BaseException("");
			else {
				sql = "insert into tbl_user values(?,?,?)";
				pst = conn.prepareStatement(sql);
				pst.setString(1, userid);
				pst.setString(2, pwd);
				pst.setDate(3, new Date(System.currentTimeMillis()));
				pst.execute();
				BeanUser bu= new BeanUser();
				bu.setUser_id(userid);
				bu.setUser_pwd(pwd);
				bu.setRegister_time(new Date(System.currentTimeMillis()));
				return bu;
			}
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
		
		
	}

	
	@Override
	public BeanUser login(String userid, String pwd) throws BaseException {
		// TODO Auto-generated method stub
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			String sql="select * from tbl_user where user_id=? and user_pwd=?";
			java.sql.PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, userid);
			pst.setString(2, pwd);
			java.sql.ResultSet rs = pst.executeQuery();
			if(!rs.next() ) throw new BaseException("�û�����");
			else {
				BeanUser bs = new BeanUser();
				bs.setUser_id(userid);
				bs.setUser_pwd(pwd);
				bs.setRegister_time(new Date(System.currentTimeMillis()));
				return bs;
			}
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}

	@Override
	public void changePwd(BeanUser user, String oldPwd, String newPwd,
			String newPwd2) throws BaseException {
		// TODO Auto-generated method stub
		Connection conn = null;
		if(!newPwd.equals(newPwd2)) throw new BaseException("");
		else if(!user.getUser_pwd().equals(oldPwd)) throw new BaseException("");
		else if("".equals(newPwd)) throw new BaseException("");
		try {
			conn = DBUtil.getConnection();
			String sql="select * from tbl_user where user_id=? and user_pwd=?";
			java.sql.PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, user.getUser_id());
			pst.setString(2, user.getUser_pwd());
			java.sql.ResultSet rs = pst.executeQuery();
			if(rs.next()) {
				sql = " update tbl_user set user_pwd=? where user_id=?";
				pst = conn.prepareStatement(sql);
				pst.setString(1, newPwd);
				pst.setString(2, user.getUser_id());
				pst.execute();
			}else throw new BaseException("");
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return ;
	}
	public static void main(String[] args) throws BaseException{
		ExampleUserManager eum = new ExampleUserManager();
		BeanUser bu = new BeanUser();
		System.out.print(1);

	}
}
