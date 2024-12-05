package controlador;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.stage.FileChooser;
import modelo.Articulo;
import persistencia.ConexionBD;
import persistencia.Persistencia;
import vista.Util;

public class ControllerArticulo implements Initializable {

//	private final static int MODO_NAVEGACION = 0;
//	private final static int MODO_NUEVO_REGISTRO = 1;

	@FXML
	private Button btnNuevo;
	@FXML
	private Button btnBorrar;
	@FXML
	private Button btnGuardar;
	@FXML
	private Button btnBuscar;
	@FXML
	private ToggleButton btnImportar;
	@FXML
	private TextField tfID;
	@FXML
	private TextField tfNombre;
	@FXML
	private TextField tfPrecio;
	@FXML
	private TextField tfCodigo;
	@FXML
	private TextField tfGrupo;
	@FXML
	private TextField tfStock;
	@FXML
	private Label lblInfo;

	private List<Articulo> registros;
	private int posicionRegistro;
	private Articulo registroActual;

	private Persistencia p;

	private boolean nuevo = false;

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		tfID.setDisable(true);
		try {
			posicionRegistro = 0;
			p = new Persistencia();
			registros = new ArrayList<>();
			registros = p.listaArticulos();
			// registros = ... obtener articulos.



			registroActual = registros.get(posicionRegistro);
			mostrarRegistro();
		} catch (Exception ex) {
			Util.mensajeExcepcion(ex, "Conectando/Consultando con la base de datos...");
			Platform.exit();
		}

	}

	// ******************************************************************************
	// ACCIONES ASOCIADAS A BOTONES
	// ******************************************************************************
	@FXML
	private void accionPrimero() {
		posicionRegistro = 0;
		nuevo = false;
		mostrarRegistro();
	}

	@FXML
	private void accionAtras() {
		if (posicionRegistro > 0)
			posicionRegistro--;
		nuevo = false;
		mostrarRegistro();
	}

	@FXML
	private void accionAdelante() {
		if (posicionRegistro < registros.size() - 1)
			posicionRegistro++;
		nuevo = false;
		mostrarRegistro();
	}

	@FXML
	private void accionUltimo() {
		posicionRegistro = registros.size() - 1;
		nuevo = false;
		mostrarRegistro();
	}

	@FXML
	private void accionBuscar() {
		nuevo = false;

		TextInputDialog indicaId = new TextInputDialog("<id>");
		indicaId.setHeaderText("Indica id articulo a buscar:");
		indicaId.showAndWait();

		String respuesta = indicaId.getEditor().getText();
		if (!respuesta.isEmpty()) {

			try {
				int id = Integer.parseInt(respuesta);
				Articulo art;

				p = new Persistencia();
				art = p.accionBuscarPorId(id);

				Alert d;
				String mensaje = "No encontrado";

				if (art != null) {
					mensaje = art.toString();
				}

				d = new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK);
				d.setHeaderText("Búsqueda por articulo ID " + id);
				d.showAndWait();
			} catch (NumberFormatException e) {
				Util.mensajeExcepcion(e, "Id no es número válido...");
			} catch (Exception e) {
				Util.mensajeExcepcion(e, "Error buscando id...");
			}

		}

	}

	@FXML
	private void accionImportar() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Seleccionar archivo CSV");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
		File archivoSeleccionado = fileChooser.showOpenDialog(null);

		if (archivoSeleccionado != null) {
			try (FileReader fileReader = new FileReader(archivoSeleccionado)) {
				int procesados = 0, insertados = 0, actualizados = 0, ignorados = 0;
				StringBuilder informeErrores = new StringBuilder();
				StringBuilder informeEjecutados = new StringBuilder();

				CsvToBean<Articulo> csvToBean = new CsvToBeanBuilder<Articulo>(fileReader)
						.withType(Articulo.class)
						.withSeparator(';') // Separador
						.withIgnoreLeadingWhiteSpace(true)
						.withThrowExceptions(false) // Evita que explote en parseos
						.build();

				Connection conexion = ConexionBD.getConexion();
				conexion.setAutoCommit(false);

				try (PreparedStatement stmtInsert = conexion.prepareStatement(
						"INSERT INTO empresa.articulo (nombre, precio, codigo, grupo, stock) VALUES (?, ?, ?, ?, ?)");
					 PreparedStatement stmtUpdate = conexion.prepareStatement(
							 "UPDATE empresa.articulo SET precio = ? WHERE codigo = ?")) {

					// Procesar cada registro
					int registroActual = 0;
					for (Articulo articulo : csvToBean) {
						registroActual++;
						try {
							procesados++;

							// Validar campos obligatorios
							if (articulo.getCodigo() == null || articulo.getNombre() == null) {
								ignorados++;
								informeErrores.append("Artículo (registro ").append(registroActual)
										.append("): Error -> Código o Nombre nulos\n");
								continue; // Ignorar este registro
							}

							// Verificar si el artículo existe
							Articulo articuloExistente = p.obtenerArticuloPorCodigo(articulo.getCodigo());

							if (articuloExistente == null) {
								// Configurar batch para inserción
								stmtInsert.setString(1, articulo.getNombre());
								stmtInsert.setFloat(2, articulo.getPrecio());
								stmtInsert.setString(3, articulo.getCodigo());
								stmtInsert.setInt(4, articulo.getGrupo());
								stmtInsert.setInt(5, articulo.getStock());
								stmtInsert.addBatch();
								insertados++;
								informeEjecutados.append("Artículo (registro ").append(registroActual)
										.append("): ").append(articulo.getNombre())
										.append(" -> INSERT OK\n");
								registros.add(articulo);
								accionUltimo();
							} else {
								// Configurar batch para actualización
								stmtUpdate.setFloat(1, articulo.getPrecio());
								stmtUpdate.setString(2, articulo.getCodigo());
								stmtUpdate.addBatch();
								actualizados++;
								informeEjecutados.append("Artículo (registro ").append(registroActual)
										.append("): ").append(articulo.getNombre())
										.append(" -> UPDATE OK\n");
							}
						} catch (NumberFormatException e) {
							ignorados++;
							informeErrores.append("Artículo (registro ").append(registroActual)
									.append("): Error de formato numérico (NumberFormat) -> ").append(e.getMessage()).append("\n");
						} catch (SQLException e) {
							informeEjecutados.append("Artículo (registro ").append(registroActual)
									.append("): Error SQL -> ").append(e.getMessage()).append("\n");
						} catch (Exception e) {
							ignorados++;
							informeErrores.append("Artículo (registro ").append(registroActual)
									.append("): Error inesperado -> ").append(e.getMessage()).append("\n");
						}
					}

					// Capturar errores de parseo global
					if (!csvToBean.getCapturedExceptions().isEmpty()) {
						for (CsvException error : csvToBean.getCapturedExceptions()) {
							ignorados++;
							informeErrores.append("Registro ").append(error.getLineNumber())
									.append(": Error de parseo -> ").append(error.getMessage()).append("\n");
						}
					}

					// Ejecutar lotes
					stmtInsert.executeBatch();
					stmtUpdate.executeBatch();
					conexion.commit(); // Confirmar los cambios

				} catch (SQLException e) {
					//conexion.rollback();
					informeErrores.append("Error al ejecutar lotes: ").append(e.getMessage()).append("\n");
				} finally {
					conexion.setAutoCommit(true);
				}

				generarInforme(procesados, insertados, actualizados, ignorados, informeErrores.toString(), informeEjecutados.toString());

			} catch (IOException e) {
				Util.mensajeExcepcion(e, "Error al leer el archivo CSV");
			} catch (SQLException e) {
				Util.mensajeExcepcion(e, "Error al conectar con la base de datos");
			}
		}
	}

	@FXML
	private void accionNuevo() {
		tfID.setText("<autonum>");
		tfNombre.setText("");
		tfPrecio.setText("");
		tfCodigo.setText("");
		tfGrupo.setText("");
		tfStock.setText("");

		nuevo = true;

	}

	@FXML
	private void accionGuardar() {
		Articulo art = new Articulo();
		Articulo artViejo = registros.get(posicionRegistro);

		art.setNombre(tfNombre.getText());
		art.setCodigo(tfCodigo.getText());
		art.setPrecio(Float.parseFloat(tfPrecio.getText()));
		art.setGrupo(Integer.parseInt(tfGrupo.getText()));
		art.setStock(Integer.parseInt(tfStock.getText()));


		try {

			p = new Persistencia();

			if (nuevo){
				Articulo articuloAnyadir = p.guardarArticuloNuevo(art);
				registros.add(articuloAnyadir);
				nuevo = false;
				System.out.println("Artículo guardado");
			}else {
				art.setId(Integer.parseInt(tfID.getText()));
				Articulo articuloAnyadir = p.modificarArticulo(art, artViejo);

				registros.get(posicionRegistro).setNombre(articuloAnyadir.getNombre());
				registros.get(posicionRegistro).setPrecio(articuloAnyadir.getPrecio());
				registros.get(posicionRegistro).setCodigo(articuloAnyadir.getCodigo());
				registros.get(posicionRegistro).setGrupo(articuloAnyadir.getGrupo());
				registros.get(posicionRegistro).setStock(articuloAnyadir.getStock());

				System.out.println("Artículo modificado");
			}
		} catch (Exception ex) {
			Util.mensajeExcepcion(ex, "Actualizando registro...");
		}

	}

	@FXML
	private void accionBorrar() {
		nuevo = false;


		try {
				String mensaje = "¿Estás seguro de borrar el registro [" + tfID.getText() + "]?";
				Alert d = new Alert(Alert.AlertType.CONFIRMATION, mensaje, ButtonType.YES, ButtonType.NO);
				d.setTitle("Borrado de registro");
				d.showAndWait();

				if (d.getResult() == ButtonType.YES) {
					try {
						Articulo art = new Articulo();

						art.setId(Integer.parseInt(tfID.getText()));
						art.setNombre(tfNombre.getText());
						art.setCodigo(tfCodigo.getText());
						art.setPrecio(Float.parseFloat(tfPrecio.getText()));
						art.setGrupo(Integer.parseInt(tfGrupo.getText()));
						art.setStock(Integer.parseInt(tfStock.getText()));

						p = new Persistencia();
						Articulo articuloEliminar = p.eliminarArticulo(art);
						registros.remove(articuloEliminar);

						if (posicionRegistro > 0){
							posicionRegistro--;
							mostrarRegistro();
						}

					} catch (NumberFormatException e) {
						throw new RuntimeException(e);
					} catch (SQLException e) {
						throw new RuntimeException(e);
					}
				}

		} catch (Exception ex) {
			Util.mensajeExcepcion(ex, "Borrando registro...");
		}
	}

	private void mostrarRegistro() {

		lblInfo.setText("Registro " + (posicionRegistro + 1) + " de " + registros.size());

		registroActual = registros.get(posicionRegistro);

		tfID.setText(String.valueOf(registroActual.getId()));
		tfNombre.setText(registroActual.getNombre());
		tfPrecio.setText(String.valueOf(registroActual.getPrecio()));
		tfCodigo.setText(registroActual.getCodigo());
		tfGrupo.setText(String.valueOf(registroActual.getGrupo()));
		tfStock.setText(String.valueOf(registroActual.getStock()));
	}



	private void generarInforme(int procesados, int insertados, int actualizados, int ignorados, String errores, String ejecutados) {
		StringBuilder informe = new StringBuilder();
		informe.append("Informe de Importación de Artículos:\n");
		informe.append("Total registros procesados: ").append(procesados).append("\n");
		informe.append("Artículos insertados correctamente: ").append(insertados).append("\n");
		informe.append("Artículos actualizados correctamente: ").append(actualizados).append("\n");
		informe.append("Registros ignorados por errores de formato: ").append(ignorados).append("\n\n");

		informe.append("REGISTROS IGNORADOS:\n").append(errores).append("\n");
		informe.append("REGISTROS EJECUTADOS:\n").append(ejecutados).append("\n");

		System.out.println(informe);
	}

}
