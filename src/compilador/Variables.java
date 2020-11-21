package compilador;

/**
 *
 * @author Julian Camilo Melo
 */
public class Variables {
    private int linea;
    private String nombre;
    private String tipo;
    private String valor;

    public Variables() {
        super();
    }

    public Variables(int linea, String nombre, String tipo, String valor) {
        this.linea = linea;
        this.nombre = nombre;
        this.tipo = tipo;
        this.valor = valor;
    }

    public int getLinea() {
        return linea;
    }

    public void setLinea(int linea) {
        this.linea = linea;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
    
}
