package com.mirim.transaction.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.mirim.transaction.dto.TicketDto;

public class TicketDao {
	
	JdbcTemplate template;
	
	PlatformTransactionManager transactionManager;

	public void setTemplate(JdbcTemplate template) {		// 초기화
		this.template = template;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;	// DB와 연동
	}
	
	public TicketDao() {		// 생성자
		super();
		// TODO Auto-generated constructor stub
	}
	
	public void buyTicket(final TicketDto dto) {
		
		TransactionDefinition definition = new DefaultTransactionDefinition();
		TransactionStatus status = transactionManager.getTransaction(definition);
		
		try {
		// 두개의 조건이 Transaction묶어야함
		template.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				// TODO Auto-generated method stub
				
				String sql="INSERT INTO card(consumerid, amount) VALUES(?, ?)";
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setString(1, dto.getConsumerid());
				pstmt.setInt(2, dto.getAmount());
				
				return pstmt;
			}
		});
		
		template.update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				// TODO Auto-generated method stub
				String sql="INSERT INTO ticket(consumerid, countnum) VALUES(?, ?)";
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setString(1, dto.getConsumerid());
				pstmt.setInt(2, dto.getAmount());
				
				
				return pstmt;
				}
			});
			transactionManager.commit(status);
		
		} catch (Exception e) {
			e.printStackTrace();
			
			transactionManager.rollback(status);
		}
	}
}
