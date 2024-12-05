package modelo;


import com.opencsv.bean.CsvBindByName;

import java.util.Objects;

public class Articulo {
	@CsvBindByName(column="ProductID")
	private int id;
	@CsvBindByName(column="ProductName")
	private String nombre;
	@CsvBindByName(column="UnitPrice")
	private float precio;
	@CsvBindByName(column="Code")
	private String codigo;
	@CsvBindByName(column="CategoryID")
	private int grupo;
	@CsvBindByName(column="UnitsInStock")
	private int stock;

	public Articulo() {
		super();
	}

	public Articulo(String nombre, float precio, String codigo, int grupo, int stock) {
		super();
		this.nombre = nombre;
		this.precio = precio;
		this.codigo = codigo;
		this.grupo = grupo;
		this.stock = stock;
	}
	
	

	public Articulo(int id, String nombre, float precio, String codigo, int grupo, int stock) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.precio = precio;
		this.codigo = codigo;
		this.grupo = grupo;
		this.stock = stock;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public float getPrecio() {
		return precio;
	}

	public void setPrecio(float precio) {
		this.precio = precio;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public int getGrupo() {
		return grupo;
	}

	public void setGrupo(int grupo) {
		this.grupo = grupo;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	@Override
	public String toString() {
		return "[" + id + " - " + nombre + " - " + precio + " - " + codigo + " - " + stock + " - " + grupo + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(codigo, grupo, id, nombre, precio, stock);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Articulo other = (Articulo) obj;
		return Objects.equals(codigo, other.codigo) && grupo == other.grupo && id == other.id
				&& Objects.equals(nombre, other.nombre);
	}
	
}
