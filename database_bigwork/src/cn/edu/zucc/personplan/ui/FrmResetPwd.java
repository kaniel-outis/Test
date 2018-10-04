package cn.edu.zucc.personplan.ui;


import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.hibernate.Session;
import org.hibernate.Transaction;

import cn.edu.zucc.personplan.PersonPlanUtil;
import cn.edu.zucc.personplan.model.BeanPlan;
import cn.edu.zucc.personplan.model.BeanUser;
import cn.edu.zucc.personplan.util.BaseException;
import cn.edu.zucc.personplan.util.HibernateUtil;
public class FrmResetPwd extends JFrame implements ActionListener {
		//new
		private Object tblUserTitle[]=BeanUser.tblUserTitle;
		private Object tblUserData[][];
		DefaultTableModel tabUserModel=new DefaultTableModel();
		private JTable dataTableUser=new JTable(tabUserModel);
		List<BeanUser> allUser = null;
		
		private void reloadUserTable(){//这是测试数据，需要用实际数替换
			try {
				allUser=PersonPlanUtil.userManager.loadAll();
			} catch (BaseException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误",JOptionPane.ERROR_MESSAGE);
				return;
			}
			tblUserData =  new Object[allUser.size()][BeanUser.tblUserTitle.length];
			for(int i=0;i<allUser.size();i++){
				for(int j=0;j<BeanUser.tblUserTitle.length;j++)
					tblUserData[i][j]=allUser.get(i).getCell(j);
			}
			tabUserModel.setDataVector(tblUserData,tblUserTitle);
			this.dataTableUser.validate();
			this.dataTableUser.repaint();
		}
		private JPanel toolBar = new JPanel();
		private JPanel workPane = new JPanel();
		private JButton btnOk = new JButton("确定重置");

		/*
		Session session=HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();
		String hql="from BeanUser";
		List<BeanUser> bu = session.createQuery(hql).list();
		*/
		public FrmResetPwd() {
		this.getContentPane().add(new JScrollPane(this.dataTableUser), BorderLayout.CENTER);
		this.reloadUserTable();
		toolBar.setLayout(new FlowLayout(FlowLayout.RIGHT));
		toolBar.add(this.btnOk);
		this.getContentPane().add(toolBar, BorderLayout.SOUTH);
		this.setSize(500, 200);
		this.btnOk.addActionListener(this);
		
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.btnOk){	
			int i=this.dataTableUser.getSelectedRow();
			if(i<0) {
				JOptionPane.showMessageDialog(null, "请选择重置用户", "错误",JOptionPane.ERROR_MESSAGE);
				return;
			}
			try {
				PersonPlanUtil.userManager.resetUser(this.allUser.get(i));
			} catch (BaseException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "错误",JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
			
		
	}
}
