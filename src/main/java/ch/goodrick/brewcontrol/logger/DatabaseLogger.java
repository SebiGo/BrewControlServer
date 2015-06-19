package ch.goodrick.brewcontrol.logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.GregorianCalendar;

import org.slf4j.LoggerFactory;

/**
 * This logger class loggs to a h2 database.
 * 
 * @deprecated this class is only here for testing purposes
 * @author sebastian@goodrick.ch
 */
public class DatabaseLogger implements Logger {

	// TODO 5 use JPA

	private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
	private static Connection conn;
	private String name;

	public DatabaseLogger(String name) throws ClassNotFoundException, SQLException {
		this.name = name;
		Class.forName("org.h2.Driver");
		conn = DriverManager.getConnection("jdbc:h2:~/data");
		Statement statement = conn.createStatement();
		statement.execute("CREATE TABLE IF NOT EXISTS Temperature (Name VARCHAR(255), Timestamp TIMESTAMP, Temperature DOUBLE)");
		log.info("DatabaseLogger ready for logging");
		// conn.close();
	}

	@Override
	public void log(Double value) {
		try {
			PreparedStatement ps = conn.prepareStatement("INSERT INTO temperature (Name, Timestamp, Temperature) VALUES (?, ?, ?)");
			ps.setString(1, name);
			ps.setTimestamp(2, new Timestamp(new GregorianCalendar().getTimeInMillis()), new GregorianCalendar());
			ps.setDouble(3, value);
			ps.execute();
		} catch (SQLException e) {
			log.error("SQLException: ", e);
		}
	}
}
