package compilador;

import java.util.Arrays;
import java.util.List;

/*******************************************************************************
 * Nombre:          PalabrasReservadas
 * Propósito:       Esta clase maneja las variables de tipo String que manejan
 *                  las palabras reservadas del programa, los operadores tanto 
 *                  aritmeticos como logicos.
 * Variables:       palabrasReservadas: String que almacena las palabras reservadas
 *                  del programa.
 *                  operadoresAritmeticos: String que almacena los operadores 
 *                  aritmeticos que se maneja en la aplicación.
 *                  operadoresLogicos: String que almacena los operadores logicos
 *                  que se manejan en la aplicación.
 * Precondición:    N/A
 * Postcondición:   N/A
*******************************************************************************/
public class PalabrasReservadas {
    
    private String palabrasReservadasIniciales = "programa, inicio, fin";
    private String palabrasReservadas = "var, asignar, escribir, leer, si(, para(";
    private String palabrasReservadasCierre = "como, sino, finsi, finpara";
    private String operadoresAritmeticos = "+,-,*,/,^";
    private String operadoresLogicos = "<=, >=, >, <, ==, =, <>";
    private String tiposVariables = "numero, decimal, cadena";

    private List<String> listaPalabrasReservadasIniciales;
    private List<String> listaPalabrasReservadas;
    private List<String> listaPalabrasReservadasCierre;    
    private List<String> listaOperadoresAritmeticos;
    private List<String> listaOperadoresLogicos;
    private List<String> listaTiposVariables;

    public PalabrasReservadas() {
        listaPalabrasReservadasIniciales = Arrays.asList(palabrasReservadasIniciales.replace(" ", "").split(","));
        listaPalabrasReservadas = Arrays.asList(palabrasReservadas.replace(" ", "").split(","));        
        listaPalabrasReservadasCierre = Arrays.asList(palabrasReservadasCierre.replace(" ", "").split(","));
        listaOperadoresAritmeticos = Arrays.asList(operadoresAritmeticos.replace(" ", "").split(","));
        listaOperadoresLogicos = Arrays.asList(operadoresLogicos.replace(" ", "").split(","));
        listaTiposVariables = Arrays.asList(tiposVariables.replace(" ", "").split(","));
    }

    /*public static void main(String[] args) {

    }*/

    public String getPalabrasReservadas() {
        return palabrasReservadas;
    }

    public void setPalabrasReservadas(String palabrasReservadas) {
        this.palabrasReservadas = palabrasReservadas;
    }

    public String getPalabrasReservadasIniciales() {
        return palabrasReservadasIniciales;
    }

    public void setPalabrasReservadasIniciales(String palabrasReservadasIniciales) {
        this.palabrasReservadasIniciales = palabrasReservadasIniciales;
    }

    
    public String getOperadoresAritmeticos() {
        return operadoresAritmeticos;
    }

    public void setOperadoresAritmeticos(String operadoresAritmeticos) {
        this.operadoresAritmeticos = operadoresAritmeticos;
    }

    public String getOperadoresLogicos() {
        return operadoresLogicos;
    }

    public void setOperadoresLogicos(String operadoresLogicos) {
        this.operadoresLogicos = operadoresLogicos;
    }

    public String getTiposVariables() {
        return tiposVariables;
    }

    public void setTiposVariables(String tiposVariables) {
        this.tiposVariables = tiposVariables;
    }

    public List<String> getListaTiposVariables() {
        return listaTiposVariables;
    }

    public void setListaTiposVariables(List<String> listaTiposVariables) {
        this.listaTiposVariables = listaTiposVariables;
    }

    public List<String> getListaPalabrasReservadas() {
        return listaPalabrasReservadas;
    }

    public List<String> getListaPalabrasReservadasIniciales() {
        return listaPalabrasReservadasIniciales;
    }

    public void setListaPalabrasReservadasIniciales(List<String> listaPalabrasReservadasIniciales) {
        this.listaPalabrasReservadasIniciales = listaPalabrasReservadasIniciales;
    }
    
    public void setListaPalabrasReservadas(List<String> listaPalabrasReservadas) {
        this.listaPalabrasReservadas = listaPalabrasReservadas;
    }

    public List<String> getListaOperadoresAritmeticos() {
        return listaOperadoresAritmeticos;
    }

    public void setListaOperadoresAritmeticos(List<String> listaOperadoresAritmeticos) {
        this.listaOperadoresAritmeticos = listaOperadoresAritmeticos;
    }

    public List<String> getListaOperadoresLogicos() {
        return listaOperadoresLogicos;
    }

    public void setListaOperadoresLogicos(List<String> listaOperadoresLogicos) {
        this.listaOperadoresLogicos = listaOperadoresLogicos;
    }
    
    public List<String> getlistaTiposVariables(){
        return listaTiposVariables;
    }
    
    public void setlistaTiposVariables(List<String> listaTiposVariables) {
        this.listaTiposVariables = listaTiposVariables;
    }

    public void setPalabrasReservadasCierre(String palabrasReservadasCierre) {
        this.palabrasReservadasCierre = palabrasReservadasCierre;
    }

    public String getPalabrasReservadasCierre() {
        return palabrasReservadasCierre;
    }

    public List<String> getListaPalabrasReservadasCierre() {
        return listaPalabrasReservadasCierre;
    }

    public void setListaPalabrasReservadasCierre(List<String> listaPalabrasReservadasCierre) {
        this.listaPalabrasReservadasCierre = listaPalabrasReservadasCierre;
    }
    
}
