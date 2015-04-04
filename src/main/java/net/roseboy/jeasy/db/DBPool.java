package net.roseboy.jeasy.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.beans.PropertyVetoException;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import net.roseboy.jeasy.util.PropertyUtil;

/**
 * ���ݿ����ӳ�
 * ʹ��c3p0
 * 
 * @author roseboy.net
 *
 */
public class DBPool {
	private static DBPool dbPool;
	private ComboPooledDataSource[] dataSource;
	private String DBSource;
	// ��Ŀ����ʱ������ʵ��
	static {
		dbPool = new DBPool();
	}

	public final String getDBSource() {
		return DBSource;
	}

	public final void setDBSource(String dBSource) {
		DBSource = dBSource;
	}

	/**
	 * ��ʼ�������ӳ�
	 */
	public void initDBPool() {
		if (DBSource == null || "".equals(DBSource))
			return;
		String dbs[] = DBSource.split("\\|");
		dataSource = new ComboPooledDataSource[dbs.length];
		for (int i = 0; i < dbs.length; i++) {
			try {
				dataSource[i] = new ComboPooledDataSource();
				dataSource[i].setUser(PropertyUtil.getPropertyString(dbs[i] + ".properties", "jdbc.Username".toLowerCase()));
				dataSource[i].setPassword(PropertyUtil.getPropertyString(dbs[i] + ".properties", "jdbc.Password".toLowerCase()));
				dataSource[i].setJdbcUrl(PropertyUtil.getPropertyString(dbs[i] + ".properties", "jdbc.Url".toLowerCase()));
				dataSource[i].setDriverClass(PropertyUtil.getPropertyString(dbs[i] + ".properties", "jdbc.driverclassname".toLowerCase()));
				dataSource[i].setInitialPoolSize(PropertyUtil.getPropertyInt(dbs[i] + ".properties", "jdbc.InitialPoolSize".toLowerCase()));
				dataSource[i].setMinPoolSize(PropertyUtil.getPropertyInt(dbs[i] + ".properties", "jdbc.MinPoolSize".toLowerCase()));
				dataSource[i].setMaxPoolSize(PropertyUtil.getPropertyInt(dbs[i] + ".properties", "jdbc.MaxPoolSize".toLowerCase()));
				dataSource[i].setMaxStatements(PropertyUtil.getPropertyInt(dbs[i] + ".properties", "jdbc.MaxStatements".toLowerCase()));
				dataSource[i].setMaxIdleTime(PropertyUtil.getPropertyInt(dbs[i] + ".properties", "jdbc.MaxIdleTime".toLowerCase()));
			} catch (PropertyVetoException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * ����ģʽ���ص�ǰ����
	 * @return �������ӳ�
	 */
	public static DBPool getInstance() {
		return dbPool;
	}

	/**
	 * �����ݿ���л�ȡ����(Ĭ������Դ����һ��)
	 * @param type ���ݿ�����
	 * @return ���ݿ�����
	 */
	public final Connection getConnection() {
		try {
			return dataSource[0].getConnection();
		} catch (SQLException e) {
			throw new RuntimeException("�޷�������Դ��ȡ����", e);
		}
	}

	/**
	 * ��ָ������Դ��ȡ����
	 * @param ds
	 * @return
	 */
	public final Connection getConnection(String ds) {
		if (DBSource == null || "".equals(DBSource))
			return null;
		int index = 0;
		String dbs[] = DBSource.split("\\|");
		for (int i = 0; i < dbs.length; i++) {
			if (dbs[i].equals(ds)) {
				index = i;
				break;
			}
		}
		try {
			return dataSource[index].getConnection();
		} catch (SQLException e) {
			throw new RuntimeException("�޷�������Դ[" + ds + "]��ȡ����", e);
		}
	}
}
