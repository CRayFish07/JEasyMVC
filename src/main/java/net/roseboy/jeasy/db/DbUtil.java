package net.roseboy.jeasy.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * 通用数据库工具
 * @author roseboy.net
 *
 */
public class DbUtil {

	public static Connection getConnection() {
		return DBPool.getInstance().getConnection();
	}

	public static Connection getConnection(String ds) {
		return DBPool.getInstance().getConnection(ds);
	}

	/**
	 * 关闭数据库查询操作的相关对象
	 * 
	 * @param param 操作数据库的相关对象
	 */
	private static void closeDB(Object... param) {
		if (param != null) {
			for (Object obj : param)
				try {
					if (obj instanceof ResultSet) {
						((ResultSet) obj).close();
					}
					if (obj instanceof PreparedStatement) {
						((PreparedStatement) obj).close();
					}
					if (obj instanceof Connection) {
						((Connection) obj).close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * 设置预编译上下文对象参数的方法
	 * 
	 * @param pst 预编译上下文对象
	 * @param param 传入的参数对象《该数传入的可以是单个对象也可以是数组》
	 */
	private static void setParam(PreparedStatement pst, Object... param) {
		int length = param.length;
		for (int i = 0; i < length; i++) {
			try {
				pst.setObject(i + 1, param[i]);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * ResultSet转List
	 * 
	 * @param rs 查询结果集
	 * @return list 结果集
	 * @throws SQLException
	 */
	private static List<Map<String, Object>> convertList(ResultSet rs) throws SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		ResultSetMetaData md = rs.getMetaData();
		int columnCount = md.getColumnCount();
		while (rs.next()) {
			Map<String, Object> rowData = new HashMap<String, Object>();
			for (int i = 1; i <= columnCount; i++) {
				if (rs.getObject(i) == null) {
					rowData.put(md.getColumnName(i), "NULL");
				} else {
					rowData.put(md.getColumnName(i), rs.getObject(i));
				}
			}
			list.add(rowData);
		}
		return list;
	}

	/**
	 * 执行查询语句
	 * 
	 * @param conn 数据库连接
	 * @param sql 查询语句
	 * @param param 查询参数
	 * @return 查询结果解list
	 */
	public static List<Map<String, Object>> exeQuery(Connection conn, String sql, Object... param) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			PreparedStatement perstmp = conn.prepareStatement(sql);
			setParam(perstmp, param);
			ResultSet rs = perstmp.executeQuery();
			list = convertList(rs);
			closeDB(rs, perstmp, conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 执行增删改语句
	 * 
	 * @param conn 数据库链接
	 * @param sql 语句
	 * @param param 参数
	 * @return 影响记录数
	 */
	public static int exeUpdate(Connection conn, String sql, Object... param) {
		int r = 0;
		try {
			PreparedStatement perstmp = conn.prepareStatement(sql);
			setParam(perstmp, param);
			r = perstmp.executeUpdate();
			closeDB(perstmp, conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return r;
	}

	@Test
	public void test() throws SQLException {
		Connection conn = getConnection();
		String sql = "SELECT * FROM  content limit ?";
		List<Map<String, Object>> list = DbUtil.exeQuery(conn, sql, 10);

		for (Map<String, Object> map : list) {
			for (String key : map.keySet()) {
				System.out.print(key + "=" + map.get(key) + "     ");
			}
			System.out.println("");
		}
	}
}
