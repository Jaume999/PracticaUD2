package persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// Patrón singleton
public class ConexionBD {

    private static final String JDBC_URL = "jdbc:mariadb://192.168.56.102/empresa";
//    private static final String JDBC_URL = "jdbc:postgresql://192.168.56.102:5432/batoi?currentSchema=empresa";
    private static Connection con = null;    

    public static Connection getConexion() throws SQLException {
        if (con == null) {
            Properties pc = new Properties();
            pc.put("user", "batoi");
            pc.put("password", "1234");
            con = DriverManager.getConnection(JDBC_URL, pc);
        }
        return con;
    }

    public static void cerrar() throws SQLException {
        if (con != null) {
            con.close();
            con = null;
        }
    }

}
