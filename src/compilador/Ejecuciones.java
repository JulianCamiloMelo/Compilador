package compilador;

/**
 *
 * @author Julian Camilo Melo
 */
public class Ejecuciones {
    private int posicion;
    private String accion;
    private String variable;
    private String mensaje;
    private String var1;
    private String operacion;
    private String var2;

    public Ejecuciones() {
        super();
    }

    public Ejecuciones(int posicion, String accion, String variable, String mensaje, String var1, String operacion, String var2) {
        this.posicion = posicion;
        this.accion = accion;
        this.variable = variable;
        this.mensaje = mensaje;
        this.var1 = var1;
        this.operacion = operacion;
        this.var2 = var2;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getVar1() {
        return var1;
    }

    public void setVar1(String var1) {
        this.var1 = var1;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public String getVar2() {
        return var2;
    }

    public void setVar2(String var2) {
        this.var2 = var2;
    }

    
}
