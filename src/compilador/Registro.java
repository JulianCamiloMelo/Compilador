/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

/*******************************************************************************
 * Nombre:          Registro
 Propósito:       Este objeto se encarga de tener la estructura de nuestro token
                  para mostrar en nuestro arreglo final.
 * Variables:       linea (Número de línea que se esta leyendo)
 *                  token (palabra que se está validando)
 *                  tipo  (El tipo de palabra que se esta validando)
 *                  Descripcion (Validación si la sintaxis es correcta o no)
 * Precondición:    N/A
 * Postcondición:   N/A
*******************************************************************************/
public class Registro {

    private int linea;
    private String token;
    private String tipo;
    private String descripcion;

    public Registro() {
        super();
    }

    public Registro(int linea, String token, String tipo, String descripcion) {
        this.linea = linea;
        this.token = token;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    public int getLinea() {
        return linea;
    }

    public String getToken() {
        return token;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setLinea(int linea) {
        this.linea = linea;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
