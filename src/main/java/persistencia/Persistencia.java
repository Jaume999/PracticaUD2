package persistencia;

import modelo.Articulo;
import modelo.Auditoria;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Persistencia {
    private static Connection con;
    private static PreparedStatement psObtenerArticulos;
    private static PreparedStatement psObtenerArticuloById;

    private static PreparedStatement psInsertarArticulo;

    private static PreparedStatement psModificarArticulo;
    private static PreparedStatement psEliminarArticulo;

    private static PreparedStatement psAuditoria;

    private static PreparedStatement psInsertarRegistroEnAuditoria;
    private static PreparedStatement psModificarRegistroEnAuditoria;
    private static PreparedStatement psEliminarRegistroEnAuditoria;
    public Persistencia() throws SQLException {
        con = ConexionBD.getConexion();

        psObtenerArticulos = con.prepareStatement("SELECT * FROM empresa.articulo");
        psObtenerArticuloById = con.prepareStatement("SELECT * FROM empresa.articulo WHERE id = ?");
        psInsertarArticulo = con.prepareStatement("INSERT INTO empresa.articulo (nombre, precio, codigo, grupo, stock) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        psModificarArticulo = con.prepareStatement("UPDATE empresa.articulo SET nombre = ?, precio = ?, codigo = ?, grupo = ?, stock = ? WHERE id = ?");
        psEliminarArticulo = con.prepareStatement("DELETE FROM empresa.articulo WHERE id = ?");

        psAuditoria = con.prepareStatement("INSERT INTO empresa.auditoria_articulos (idart, codigo, nombrenew, nombreold, precionew, precioold, gruponew, grupoold, stocknew, stockold) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

        psInsertarRegistroEnAuditoria = con.prepareStatement("INSERT INTO empresa.auditoria_articulos (tipooperacion, fechahora, idart, codigo, nombrenew, precionew, gruponew, stocknew) VALUES ('alta', NOW(), ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        psModificarRegistroEnAuditoria = con.prepareStatement("INSERT INTO empresa.auditoria_articulos (tipooperacion, fechahora, idart, codigo, nombrenew, nombreold, precionew, precioold, gruponew, grupoold, stocknew, stockold) VALUES ('modificacion', NOW(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        psEliminarRegistroEnAuditoria = con.prepareStatement("INSERT INTO empresa.auditoria_articulos (tipooperacion, fechahora, idart, codigo, nombreold, precioold, grupoold, stockold) VALUES ('baja', NOW(), ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

    }

    public List<Articulo> listaArticulos(){
        List<Articulo> listaArticulos = new ArrayList<>();

        try (ResultSet rs = psObtenerArticulos.executeQuery()) {

            rs.beforeFirst();
            while (rs.next()) {

                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                float precio = rs.getFloat("precio");
                String codigo = rs.getString("codigo");
                int stock = rs.getInt("stock");
                int grupo = rs.getInt("grupo");

                listaArticulos.add(new Articulo(id, nombre, precio, codigo, grupo, stock));
            }
        } catch (SQLException e) {
            System.err.println("Recorrido resultset....." + e.getMessage());
        }
        return listaArticulos;
    }


    public Articulo accionBuscarPorId(int id){
        Articulo art = new Articulo();

        try {
            psObtenerArticuloById.setInt(1, id);

            try (ResultSet rs = psObtenerArticuloById.executeQuery()) {
                if (rs.next()) {
                    art = new Articulo(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getFloat("precio"),
                            rs.getString("codigo"),
                            rs.getInt("grupo"),
                            rs.getInt("stock"));
                }
            } catch (SQLException e) {
                System.out.println("Algo anda mal...");
            }
        } catch (Exception e) {
            System.out.println("Algo anda mal...");
        }
        return art;
    }

    //Insertar
    public Articulo guardarArticuloNuevo(Articulo art) throws SQLException {
        try {
            con.setAutoCommit(false);

            psInsertarArticulo.setString(1,art.getNombre());
            psInsertarArticulo.setFloat(2, art.getPrecio());
            psInsertarArticulo.setString(3, art.getCodigo());
            psInsertarArticulo.setInt(4, art.getGrupo());
            psInsertarArticulo.setInt(5, art.getStock());


            psInsertarRegistroEnAuditoria.setString(2, art.getCodigo());
            psInsertarRegistroEnAuditoria.setString(3, art.getNombre());
            psInsertarRegistroEnAuditoria.setFloat(4, art.getPrecio());
            psInsertarRegistroEnAuditoria.setInt(5, art.getGrupo());
            psInsertarRegistroEnAuditoria.setInt(6, art.getStock());

            int filasAfectadas = psInsertarArticulo.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet rs = psInsertarArticulo.getGeneratedKeys()) {
                    if (rs.next()) {
                        art.setId(rs.getInt(1));
                        psInsertarRegistroEnAuditoria.setInt(1, rs.getInt(1));
                        psInsertarRegistroEnAuditoria.executeUpdate();
                        con.commit();

                    } else {
                        return null;
                    }
                }
                return art;
            } else {
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Algo ha ido mal....." + e.getMessage());
            con.rollback();
        }finally {
            con.setAutoCommit(true);
        }
        return null;
    }

    public Articulo modificarArticulo(Articulo artNuevo, Articulo artViejo) throws SQLException {
        try {
            con.setAutoCommit(false);

            psModificarArticulo.setString(1, artNuevo.getNombre());
            psModificarArticulo.setFloat(2, artNuevo.getPrecio());
            psModificarArticulo.setString(3, artNuevo.getCodigo());
            psModificarArticulo.setInt(4, artNuevo.getGrupo());
            psModificarArticulo.setInt(5, artNuevo.getStock());
            psModificarArticulo.setInt(6, artNuevo.getId());

            psModificarRegistroEnAuditoria.setInt(1, artNuevo.getId());
            psModificarRegistroEnAuditoria.setString(2, artNuevo.getCodigo());
            psModificarRegistroEnAuditoria.setString(3, artNuevo.getNombre());
            psModificarRegistroEnAuditoria.setString(4, artViejo.getNombre());
            psModificarRegistroEnAuditoria.setFloat(5, artNuevo.getPrecio());
            psModificarRegistroEnAuditoria.setFloat(6, artViejo.getPrecio());
            psModificarRegistroEnAuditoria.setInt(7, artNuevo.getGrupo());
            psModificarRegistroEnAuditoria.setInt(8, artViejo.getGrupo());
            psModificarRegistroEnAuditoria.setInt(9, artNuevo.getStock());
            psModificarRegistroEnAuditoria.setInt(10, artViejo.getStock());

            int filasAfectadas = psModificarArticulo.executeUpdate();
            int filasAfectadas2 = psModificarRegistroEnAuditoria.executeUpdate();

            if (filasAfectadas > 0 && filasAfectadas2 > 0) {
                con.commit();
                return artNuevo;
            }
        } catch (SQLException e) {
            System.err.println("Algo ha ido mal...." + e.getMessage());
            con.rollback();
        }finally {
            con.setAutoCommit(true);
        }
        return null;
    }

    public static Articulo eliminarArticulo(Articulo art) throws SQLException{

        try {
            con.setAutoCommit(false);

            psEliminarRegistroEnAuditoria.setInt(1, art.getId());
            psEliminarRegistroEnAuditoria.setString(2, art.getCodigo());
            psEliminarRegistroEnAuditoria.setString(3, art.getNombre());
            psEliminarRegistroEnAuditoria.setFloat(4, art.getPrecio());
            psEliminarRegistroEnAuditoria.setInt(5, art.getGrupo());
            psEliminarRegistroEnAuditoria.setInt(6, art.getStock());

            psEliminarArticulo.setInt(1, art.getId());


            int filasAfectadas1 = psEliminarArticulo.executeUpdate();
            int filasAfectadas2 = psEliminarRegistroEnAuditoria.executeUpdate();

            if (filasAfectadas1 > 0 && filasAfectadas2 > 0) {
                con.commit();
                return art;
            }
        } catch (SQLException e) {
            System.err.println("Algo ha ido mal..." + e.getMessage());
        }finally {
            con.setAutoCommit(true);
        }
        return null;
    }


    public Articulo leerArticulo(ResultSet rs) throws SQLException {
        int articuloId = rs.getInt("id");
        String nombre = rs.getString("nombre");
        float precio = rs.getFloat("precio");
        String codigo = rs.getString("codigo");
        int stock = rs.getInt("stock");
        int grupo = rs.getInt("grupo");
        return new Articulo(articuloId, nombre, precio, codigo, grupo, stock);
    }


    public Articulo obtenerArticuloPorCodigo(String codigo) {
        String sql = "SELECT * FROM empresa.articulo WHERE codigo = ?";
        try (PreparedStatement stmt = ConexionBD.getConexion().prepareStatement(sql)) {
            stmt.setString(1, codigo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return leerArticulo(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener artículo por código: " + codigo, e);
        }
        return null;
    }
}
