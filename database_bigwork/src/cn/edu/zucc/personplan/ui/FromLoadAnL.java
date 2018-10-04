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
public class FromLoadAnL extends JFrame implements ActionListener {
		//new
		private Object tableTitles2[]=BeanUser.tableTitles2;
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
			tblUserData =  new Object[allUser.size()][4];
			for(int i=0;i<allUser.size();i++){
				Session session = HibernateUtil.getSession();
				String hql="from BeanPlan where user_id='"+allUser.get(i).getCell(0)+"'";
				List<BeanPlan> allplan=session.createQuery(hql).list();
				int allstep=0;
				int finishstep=0;
				for(int j=0;j<allplan.size();j++) {
					allstep+=allplan.get(j).getStep_count();
					finishstep+=allplan.get(j).getFinished_step_count();
				}
				tblUserData[i][0]=allUser.get(i).getCell(0);
				tblUserData[i][1]=allplan.size();
				tblUserData[i][2]=allstep;
				tblUserData[i][3]=finishstep;
			}
			tabUserModel.setDataVector(tblUserData,tableTitles2);
			this.dataTableUser.validate();
			this.dataTableUser.repaint();
		}



		public FromLoadAnL() {
		this.getContentPane().add(new JScrollPane(this.dataTableUser), BorderLayout.CENTER);
		this.reloadUserTable();
		this.setSize(500, 200);
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}
}
