/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Ignacio
 */
@Entity
public class Producto implements Serializable{
  private static final long serialVersionUID = 99488577171L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(nullable = false)
    private Double precio;
    
    @Column(nullable = false, length=24)
    private String barCode;     
    
    protected Producto ()
    {
        
    }
    
    public Producto(String nombre, double precio, String barCode)
    {
        this.nombre=nombre;
        this.precio=precio>=0?precio:0;
        this.barCode=barCode;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }      

    @Override
    public String toString() {
        return String.format("ejemplo04.model.Producto[ id=%d;"
                + "nombre=%s;"
                + "precio=%f;"
                + "c?digo de barras=%s ]",
                id,nombre,precio,barCode);
        
    }
     
    
}
