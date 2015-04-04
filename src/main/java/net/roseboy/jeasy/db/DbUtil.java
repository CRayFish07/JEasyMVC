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
 * ͨ�����ݿ⹤��
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
	 * �ر����ݿ��ѯ��������ض���
	 * 
	 * @param param �������ݿ����ض���
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
	 * ����Ԥ���������Ķ�������ķ���
	 * 
	 * @param pst Ԥ���������Ķ���
	 * @param param ����Ĳ������󡶸�������Ŀ����ǵ�������Ҳ���������顷
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
	 * ResultSetתList
	 * 
	 * @param rs ��ѯ�����
	 * @return list �����
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
	 * ִ�в�ѯ���
	 * 
	 * @param conn ���ݿ�����
	 * @param sql ��ѯ���
	 * @param param ��ѯ����
	 * @return ��ѯ�����list
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
	 * ִ����ɾ�����
	 * 
	 * @param conn ���ݿ�����
	 * @param sql ���
	 * @param param ����
	 * @return Ӱ���¼��
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
