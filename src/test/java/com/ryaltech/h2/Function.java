package com.ryaltech.h2;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * H2 user defined functions
 * http://h2database.com/html/features.html#user_defined_functions
 */
public class Function {

	
	/**
	 * method to mimic SP_DROPTABLE stored func
	 * @param conn
	 * @param table
	 * @throws Exception
	 */
	public static void sp_dropatable(Connection conn, String table)throws Exception{
		PreparedStatement dropTable = conn.prepareStatement(
				String.format("DROP TABLE IF EXISTS %s CASCADE", table));
		try{
			dropTable.execute();
		}finally{
			try{
				dropTable.close();
			}catch(Exception ex){				
			}
		}
	}

}
