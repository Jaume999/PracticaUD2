package modelo;

import java.time.LocalDateTime;

public class Auditoria {

    private int id;
    private enum tipoOperacion {alta, baja, modificacion}
    private LocalDateTime fechaHora;
    private int idArt;
    private String codigo;
    private String nombreNuevo;
    private String nombreViejo;
    private float precioNuevo;
    private float precioViejo;
    private int grupoNuevo;
    private int grupoViejo;
    private int stockNuevo;
    private int stockViejo;


    public Auditoria() {
        super();
    }

    public Auditoria(int id, LocalDateTime fechaHora, int idArt, String codigo, String nombreNuevo, String nombreViejo, float precioNuevo, float precioViejo, int grupoNuevo, int grupoViejo, int stockNuevo, int stockViejo) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.idArt = idArt;
        this.codigo = codigo;
        this.nombreNuevo = nombreNuevo;
        this.nombreViejo = nombreViejo;
        this.precioNuevo = precioNuevo;
        this.precioViejo = precioViejo;
        this.grupoNuevo = grupoNuevo;
        this.grupoViejo = grupoViejo;
        this.stockNuevo = stockNuevo;
        this.stockViejo = stockViejo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public int getIdArt() {
        return idArt;
    }

    public void setIdArt(int idArt) {
        this.idArt = idArt;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombreNuevo() {
        return nombreNuevo;
    }

    public void setNombreNuevo(String nombreNuevo) {
        this.nombreNuevo = nombreNuevo;
    }

    public String getNombreViejo() {
        return nombreViejo;
    }

    public void setNombreViejo(String nombreViejo) {
        this.nombreViejo = nombreViejo;
    }

    public float getPrecioNuevo() {
        return precioNuevo;
    }

    public void setPrecioNuevo(float precioNuevo) {
        this.precioNuevo = precioNuevo;
    }

    public float getPrecioViejo() {
        return precioViejo;
    }

    public void setPrecioViejo(float precioViejo) {
        this.precioViejo = precioViejo;
    }

    public int getGrupoNuevo() {
        return grupoNuevo;
    }

    public void setGrupoNuevo(int grupoNuevo) {
        this.grupoNuevo = grupoNuevo;
    }

    public int getGrupoViejo() {
        return grupoViejo;
    }

    public void setGrupoViejo(int grupoViejo) {
        this.grupoViejo = grupoViejo;
    }

    public int getStockNuevo() {
        return stockNuevo;
    }

    public void setStockNuevo(int stockNuevo) {
        this.stockNuevo = stockNuevo;
    }

    public int getStockViejo() {
        return stockViejo;
    }

    public void setStockViejo(int stockViejo) {
        this.stockViejo = stockViejo;
    }
}
