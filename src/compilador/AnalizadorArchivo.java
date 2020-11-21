package compilador;

//Importación de librerias
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import static jdk.nashorn.internal.objects.NativeString.indexOf;

/*******************************************************************************
* Nombre:          AnalizadorArchivo
* Propósito:       Esta clase se encarga de realizar el análisis léxico, sintactico
*                  y semantico del código que se esta generando.
*                  Así mismo genera un archivo de salida con todos los token 
*                  generados en la lectura del archivo y los errores generados 
*                  en el código tanto sintacticos como semánticos.
* Precondición:    Debe existir un código de programa escrito en la pantalla 
*                  principal del programa.
* Postcondición:   El código escrito no debe tener ningún error semantico ni 
*                  sintactico para poder ejecutar el programa.
*******************************************************************************/
public class AnalizadorArchivo{
    //Inicialización de variables privadas dentro de nuestra clase AnalizadorArchivo
    private PalabrasReservadas palabras;                                        
    private List<String> listaMensajes;
    private List<String> listaMensajesSemanticos;
    private List<Registro> listaTokens;
    private List<Registro> listaSentencias;
    private List<Variables> listaVariables;
    private List<Ejecuciones> listaEjecuciones;
    
    //Constructor
    public AnalizadorArchivo(){
        palabras = new PalabrasReservadas();
    }

    public void iniciarAnalisis(String archivo){
        //Definición de variables locales
        boolean band = true;
        
        //Llamado a la función que inicializa los objetos en los que almacenamos
        //la información
        inicializarVariables();

        //Mientras no existan errores se ejecuta la lectura del archivo
        try {
            if (null != archivo && !archivo.isEmpty()) {
                //Llamado a la función que me separa cada fila del programa
                procesarArchivo(archivo);
                
                //Llamado a la función que valida la estructura del programa
                band = validarEstructuraInicial(archivo);
                if (!band){
                    return;
                }
                
                //Llamado a la función que genera los tokens linea a linea
                band = ActualizarTokens();
                
                //Llamado a la función que ejecuta el programa
                ejecutarPrograma();
                
            } else {
                //Se meustra mensaje para cuando el archivo se encuentra vacío
                JOptionPane.showMessageDialog(null, "Error: El arhivo que se desea analizar esta vacío");
            }
        } catch (Exception e) {
            //Mensaje de error durante la lectura del archivo
            JOptionPane.showMessageDialog(null, "Error: El archivo no pudo ser analizado (" + e.getMessage()+").");
        }
    }
    
    //Función que se encarga de inicializar los arreglos que se manejan en la 
    //ejecución del programa.
    private void inicializarVariables() {
        this.listaSentencias = new ArrayList<>();
        this.listaMensajes = new ArrayList<>();
        this.listaMensajesSemanticos = new ArrayList<>();
        this.listaTokens = new ArrayList<>();
        this.listaVariables = new ArrayList<>();
        this.listaEjecuciones = new ArrayList<>();
    }

    //Función que genera el procesamiento lexico del codigo generando una lista
    //linea a linea por cada linea de codigo en el archivo.
    private void procesarArchivo(String archivo) {
        //Declaración de variables locales
        String[] arregloPalabras = archivo.trim().split("\n");
        List<String> listaConvertida = Arrays.asList(arregloPalabras);
        Registro fila;
        //Inicialización de variables locales
        int cont = 1;
        //Cargue de inicio de analizador de lexico
        listaMensajes.add("************************** ANÁLISIS DE LÉXICO **************************");
        //ciclo que recorre el archivo linea a linea para sacar las lineas del
        //código
        for (String linea: listaConvertida) {
            fila = new Registro(cont, linea.replace("\n","").trim(),"","");
            //Validamos que la linea no sea nula para insertarla
            if(fila.getToken().length()>0){
                //Insertamos una nueva sentencia a validar
                getListaSentencias().add(fila);
                cont++;
            }    
        }
        //Finalización del analizador lexico
        listaMensajes.add("************************* ANÁLISIS COMPLETADO **************************");
    }
    
    //Función que valida que la estructura del programa tengan las 3 palabras principales
    //PROGRAMA, INICIO Y FIN
    private boolean validarEstructuraInicial(String archivo) {
        //Definición de variables locales
        boolean band = true;
        //Inicio del validador sintactico
        listaMensajes.add("************************** ANÁLISIS SINTÁCTICO **************************");
        //Validamos si nuestro archivo tiene la palabra programa
        if (archivo.contains("programa") || archivo.contains("PROGRAMA")) {
            //Validamos si el archivo tiene la palabra inicio y fin
            if ((archivo.contains("inicio") || archivo.contains("INICIO")) && (archivo.contains("fin") || archivo.contains("FIN"))) {
            } else {
                listaMensajes.add("Error: En la estructura del programa debe existir la palabra INICIO y FIN.");
                band = false;
            }
        } else {
            listaMensajes.add("Error: En la estructura del programa debe tener la palabra PROGRAMA para indicar el nombre del mismo.");
            band = false;
        }
        return band;
    }

    //Función que valida linea a linea su estructura y su posición dentro del 
    //código generado.
    private boolean ActualizarTokens() {
        //Declaración de variables locales
        Registro token;
        Variables var;
        Ejecuciones ejec;
        boolean band=true, band2=true, band3=false, vald_texto = false;
        String palabra,palabraReservada="";
        String tipoVariable = ""; 
        int pos, posf, cont, longitud, pose, si=0, sino=0, para=0;        
        longitud = getListaSentencias().size();
        
        //Inicialización del analisis semantico
        listaMensajesSemanticos.add("************************** ANÁLISIS SEMÁNTICO **************************");
        //Ciclo que recorre linea a linea el programa
        for (Registro fila: getListaSentencias()) {
            //Inicialización de variables de control
            pos = 0;
            posf = 0;
            pose = 0;
            band = false;
            band2 = true;
            vald_texto = false;
            palabra="";
            tipoVariable = "";
            cont = 0;
                    
            //Ciclo para validar que la lectura de la fila no ha terminado
            while(posf>=0){
                //Se adiciona un espacio en blanco al final de la sentencia 
                fila.setToken(fila.getToken().trim()+" ");
                //Buscamos el indice del primer espacio en blanco
                posf = fila.getToken().indexOf(" ", pos);
                //Validamos si encontro el indice o no
                if (posf>0){
                    //cargamos el token
                    palabra = fila.getToken().substring(pos, posf).toLowerCase();
                    //validamos si es la primera linea del programa
                    if(fila.getLinea()==1){
                        band2 = false;
                        //Se valida si es la primera palabra a validar
                        if( pos==0 ){
                            //Esta primera palabra debe ser la inicialización del programa
                            if(palabra.equals(palabras.getListaPalabrasReservadasIniciales().get(0))){
                                //Cargamos el token
                                token = new Registro(fila.getLinea(),"Palabra Reservada", palabra,"Sintaxis correcta");
                                getListaTokens().add(token);
                            }else{
                                //Generamos el error y retornamos
                                getListaMensajes().add("Error de Sintaxis: En la primera línea del programa debe ir la palabra PROGRAMA junto al nombre del programa. Linea: "+fila.getLinea());
                            }
                        }else{
                            //Validamos si existe más de una palabra para el nombre del programa
                            if(cont==2){
                                //Generamos el error y retornamos
                                getListaMensajes().add("Error de Sintaxis: EL nombre del programa debe ser único y debe ir sin espacios en blanco. Linea: "+fila.getLinea());
                            }else{
                                //Validación si no es una palabra reservada
                                if(validarReservadasIniciales(palabra) || validarReservadas(palabra) || validarReservadasCierre(palabra) || validarAritmeticos(palabra) || validarLogicos(palabra) || validarTiposVariables(palabra)){
                                    //Generamos el error y retornamos
                                    getListaMensajes().add("Error de Sintaxis: EL nombre del programa no puede ser una palabra reservada. Linea: "+fila.getLinea());
                                }else{
                                    //Cargamos el token del nombre del programa
                                    token = new Registro(fila.getLinea(),"Identificador", palabra,"Sintaxis correcta");
                                    getListaTokens().add(token);
                                    band2 = true;
                                }
                            }
                        }
                    }else{
                        //Validamos si es la segunda linea del programa donde debe dar inicio al programa
                        if(fila.getLinea()==2){
                            if(pos==0){
                                //La única palabra que debe ir en la segunda linea es inicio
                                if(palabra.equals(palabras.getListaPalabrasReservadasIniciales().get(1))){
                                    //Cargamos el token
                                    token = new Registro(fila.getLinea(),"Palabra Reservada", palabra,"Sintaxis correcta");
                                    getListaTokens().add(token);
                                }else{
                                    //Generamos el error y retornamos
                                    getListaMensajes().add("Error de Sintaxis: Para incializar el programa se debe hacer únicamente con la palabra INICIO. Linea: "+fila.getLinea());
                                }
                            }else{
                                //Generamos el error y retornamos
                                getListaMensajes().add("Error de Sintaxis: La definición de inicio de programa debe ser única y no puede estar acompañada de otras palabras. Linea: "+fila.getLinea());
                            }
                        }else{
                            //Se valida si se va a evaluar la última fila del archivo
                            if(fila.getLinea() == longitud){
                                //Se valida si se va a evaluar la primera palabra de la liena o tiene más
                                if(pos==0){
                                    //La única palabra que debe ir en la segunda linea es inicio
                                    if(palabra.equals(palabras.getListaPalabrasReservadasIniciales().get(2))){
                                        //Cargamos el token
                                        token = new Registro(fila.getLinea(),"Palabra Reservada", palabra,"Sintaxis correcta");
                                        getListaTokens().add(token);
                                    }else{
                                        //Generamos el error y retornamos
                                        getListaMensajes().add("Error de Sintaxis: Para finalizar el programa se debe hacer únicamente con la palabra FIN. Linea: "+fila.getLinea());
                                    }
                                }else{
                                    //Generamos el error y retornamos
                                    getListaMensajes().add("Error de Sintaxis: La definición de finalización de programa debe ser única y no puede estar acompañada de otras palabras. Linea: "+fila.getLinea());
                                }
                            }else{
                                //cuerpo del programa
                                if(pos==0){
                                    //Se valida que la palabra inicial de la linea contenga una palabra reservada
                                    if(validarReservadas(palabra) || validarReservadasCierre(palabra)){
                                        palabraReservada = palabra;
                                        band2 = false;
                                        //Cargamos el token
                                        token = new Registro(fila.getLinea(),"Palabra Reservada", palabra,"Sintaxis correcta");
                                        getListaTokens().add(token);
                                        
                                        if(palabra.equals("finsi") ){
                                            si--;
                                            band2 = true;
                                        }
                                        if(palabra.equals("sino")){
                                            band2 = true;
                                        }
                                        if(palabra.equals("finpara") ){
                                            para--;
                                            band2 = true;
                                        }
                                    }
                                    else{ 
                                        //Generamos el error y retornamos
                                        getListaMensajes().add("Error de Sintaxis. La linea no comienza con una palabra reservada. Linea: "+fila.getLinea());
                                    }
                                }else{
                                    //Declaración de switch para saber que palabra reservada es
                                    switch(palabraReservada){
                                        //Validación cuando se esta definiciendo una variable.
                                        case "var":
                                            //Validación de la posición de la linea que se va a revisar y así saber si la
                                            //la sintaxis esta correcta o no
                                            switch(cont){
                                                case 1:
                                                    if(validarReservadasIniciales(palabra) || validarReservadas(palabra) || validarReservadasCierre(palabra) || validarLogicos(palabra) || validarAritmeticos(palabra) || validarTiposVariables(palabra)){
                                                        //Generamos el error
                                                        getListaMensajes().add("Error de Sintaxis. El nombre de la variable a definir no puede ser una palabra reservada. Linea: "+fila.getLinea());
                                                    }else{
                                                        band3 = false;
                                                        //Ciclo que valida que no exista una variable con el mismo nombre
                                                        for (int i=0; i<listaVariables.size(); i++){
                                                            if(palabra.equals(listaVariables.get(i).getNombre())){
                                                                band3 = true;
                                                            }
                                                        }
                                                        if(band3){
                                                            //Generamos el error
                                                            getListaMensajesSemanticos().add("Error. El nombre de la variable que esta intentando crear ("+palabra+") ya existe en el programa. Linea: "+fila.getLinea());
                                                        }else{
                                                            //Cargamos el token de nombre de la variable
                                                            token = new Registro(fila.getLinea(),"Identificador", palabra,"Sintaxis correcta");
                                                            getListaTokens().add(token);
                                                            var = new Variables(fila.getLinea(),palabra,"","");
                                                            getListaVariables().add(var);
                                                        }
                                                    }
                                                    break;
                                                case 2:
                                                    if(palabra.equals(palabras.getListaPalabrasReservadasCierre().get(0))){
                                                        //Cargamos el token de nombre de la variable
                                                        token = new Registro(fila.getLinea(),"Palabra Reservada", palabra,"Sintaxis correcta");
                                                        getListaTokens().add(token);
                                                    }else{
                                                        //Generamos el error y retornamos
                                                        getListaMensajes().add("Error de Sintaxis. Cuando se define una variable se debe usar la palabra COMO luego del nombre de la variable. Linea: "+fila.getLinea());
                                                    }
                                                    break;
                                                case 3:
                                                    band2 = true;
                                                    //Ciclo que busca la variable que se esta creando para asignar el tipo de 
                                                    //variable que se esta creando
                                                    if(validarTiposVariables(palabra)){
                                                        for (int i=0; i<listaVariables.size(); i++){
                                                            if(listaVariables.get(i).getLinea() == fila.getLinea()){
                                                                var = new Variables(listaVariables.get(i).getLinea(),listaVariables.get(i).getNombre(),palabra,"");
                                                                listaVariables.remove(i);
                                                                getListaVariables().add(var);
                                                            }
                                                        }
                                                        //Cargamos el token de nombre de la variable
                                                        token = new Registro(fila.getLinea(),"Palabra Reservada", palabra,"Sintaxis correcta");
                                                        getListaTokens().add(token);
                                                    }else{
                                                        //Generamos el error y retornamos
                                                        getListaMensajes().add("Error de Sintaxis. Cuando se define una variable despues de la palabra COMO, se debe definir el tipo de variable a crear. Linea: "+fila.getLinea());
                                                    }
                                                    break;
                                                default:
                                                    //Generamos el error y retornamos
                                                    getListaMensajes().add("Error de Sintaxis. Cuando se defie una variable no debe ir ninguna linea despues de haber definido el tipo de variable. Linea: "+fila.getLinea());
                                                    break;
                                            }
                                            break;
                                        case "asignar":
                                            //Validación de la posición de la linea que se va a revisar y así saber si la
                                            //la sintaxis esta correcta o no
                                            switch(cont){
                                                case 1:
                                                    if(validarAritmeticos(palabra)  || validarLogicos(palabra) || validarReservadas(palabra) || validarReservadasCierre(palabra) || validarReservadasIniciales(palabra) || validarTiposVariables(palabra)){
                                                        //Generamos el error y retornamos
                                                        getListaMensajesSemanticos().add("Error de Sintaxis. Usted esta tratando de asignar un valor a una palabra reservada. Linea: "+fila.getLinea());
                                                    }else{
                                                        band3 = false;
                                                        //Ciclo que valida que no exista una variable con el mismo nombre
                                                        for (int i=0; i<listaVariables.size(); i++){
                                                            if(palabra.equals(listaVariables.get(i).getNombre())){
                                                                tipoVariable = listaVariables.get(i).getTipo();
                                                                band3 = true;
                                                            }
                                                        }
                                                        if(band3){
                                                            //Cargamos el token de nombre de la variable
                                                            token = new Registro(fila.getLinea(),"Identificador", palabra,"Sintaxis correcta");
                                                            getListaTokens().add(token);
                                                            
                                                            ejec = new Ejecuciones(fila.getLinea(),palabraReservada,palabra,"","","","");
                                                            getListaEjecuciones().add(ejec);
                                                        }else{
                                                            //Generamos el error y retornamos
                                                            getListaMensajesSemanticos().add("Error de Sintaxis.  La variable que esta tratando de asignar aún no ha sido definida. Linea: "+fila.getLinea());
                                                        }
                                                    }
                                                    break;
                                                case 2:
                                                    if(palabra.equals(palabras.getListaOperadoresLogicos().get(5))){
                                                        //Cargamos el token de nombre de la variable
                                                        token = new Registro(fila.getLinea(),"Operador Aritmetico", palabra,"Sintaxis correcta");
                                                        getListaTokens().add(token);
                                                    }else{
                                                        getListaMensajes().add("Error de Sintaxis. Para asignar un valor a una variable se debe usar el simbolo =. Linea: "+fila.getLinea());
                                                    }
                                                    break;
                                                case 3:    
                                                    band2 = true;
                                                    if(validarAritmeticos(palabra)  || validarLogicos(palabra) || validarReservadas(palabra) || validarReservadasCierre(palabra) || validarReservadasIniciales(palabra) || validarTiposVariables(palabra)){
                                                        //Generamos el error y retornamos
                                                        getListaMensajesSemanticos().add("Error de Sintaxis. Usted esta tratando de asignar a una variable una palabra reservada. Linea: "+fila.getLinea());
                                                    }else{
                                                        band3 = false;
                                                        //Ciclo que valida que no exista una variable con el mismo nombre
                                                        for (int i=0; i<listaVariables.size(); i++){
                                                            if(palabra.equals(listaVariables.get(i).getNombre())){
                                                                //Cargamos el token de nombre de la variable
                                                                token = new Registro(fila.getLinea(),"Identificador", palabra,"Sintaxis correcta");
                                                                getListaTokens().add(token);
                                                                for(int j=0; j<getListaEjecuciones().size(); j++){
                                                                    if(fila.getLinea() == getListaEjecuciones().get(j).getPosicion()){
                                                                        ejec = new Ejecuciones(fila.getLinea(),palabraReservada,getListaEjecuciones().get(j).getVariable(),"",palabra,"","");
                                                                        listaEjecuciones.set(j, ejec);
                                                                    }
                                                                }
                                                                //Se valida que los tipos de variables a operar sean de la misma familia
                                                                if(tipoVariable.equals(listaVariables.get(i).getTipo())){
                                                                }else{
                                                                    //Generamos el error y retornamos
                                                                    getListaMensajesSemanticos().add("Error de Sintaxis. Usted esta tratando de asignar variables de tipos diferentes. Linea: "+fila.getLinea());
                                                                }
                                                                band3 = true;
                                                            }
                                                        }
                                                        if(!band3){
                                                            //Generamos el error y retornamos
                                                            getListaMensajesSemanticos().add("Error de Sintaxis. Usted esta tratando de asignar una variable que no ha sido definida. Linea: "+fila.getLinea());
                                                        }
                                                    }
                                                    break;
                                                case 4:   
                                                    band2 = false;
                                                    if(validarAritmeticos(palabra)){
                                                        if(tipoVariable.equals(palabras.getListaTiposVariables().get(2))){
                                                            if(palabra.equals(palabras.getListaOperadoresAritmeticos().get(0))){
                                                                //Cargamos el token del operador de concatenacion
                                                                token = new Registro(fila.getLinea(),"Operador Aritmetico", palabra,"Sintaxis correcta");
                                                                getListaTokens().add(token);
                                                            }else{
                                                                //Generamos el error y retornamos
                                                                getListaMensajesSemanticos().add("Error de Sintaxis. Las variables de tipo cadena no pueden ser operables. Linea: "+fila.getLinea());
                                                            }
                                                        }else{
                                                            //Cargamos el token del operador matematico
                                                            token = new Registro(fila.getLinea(),"Operador Aritmetico", palabra,"Sintaxis correcta");
                                                            getListaTokens().add(token);
                                                            
                                                            for(int j=0; j<getListaEjecuciones().size(); j++){
                                                                if(fila.getLinea() == getListaEjecuciones().get(j).getPosicion()){
                                                                    ejec = new Ejecuciones(fila.getLinea(),palabraReservada,getListaEjecuciones().get(j).getVariable(),"",getListaEjecuciones().get(j).getVar1(),palabra,"");
                                                                    listaEjecuciones.set(j, ejec);
                                                                }
                                                            }
                                                        }
                                                    }else{
                                                        //Generamos el error y retornamos
                                                        getListaMensajes().add("Error de Sintaxis. Para operar dos variables se debe usar un operador aritmetico. Linea: "+fila.getLinea());
                                                    } 
                                                    break;
                                                case 5:
                                                    band2 = true;
                                                    if(validarAritmeticos(palabra)  || validarLogicos(palabra) || validarReservadas(palabra) || validarReservadasCierre(palabra) || validarReservadasIniciales(palabra) || validarTiposVariables(palabra)){
                                                        //Generamos el error y retornamos
                                                        getListaMensajesSemanticos().add("Error de Sintaxis. Usted esta tratando de asignar a una variable una palabra reservada. Linea: "+fila.getLinea());
                                                    }else{
                                                        band3 = false;
                                                        //Ciclo que valida que no exista una variable con el mismo nombre
                                                        for (int i=0; i<listaVariables.size(); i++){
                                                            if(palabra.equals(listaVariables.get(i).getNombre())){
                                                                //Cargamos el token de nombre de la variable
                                                                token = new Registro(fila.getLinea(),"Identificador", palabra,"Sintaxis correcta");
                                                                getListaTokens().add(token);
                                                                
                                                                for(int j=0; j<getListaEjecuciones().size(); j++){
                                                                    if(fila.getLinea() == getListaEjecuciones().get(j).getPosicion()){
                                                                        ejec = new Ejecuciones(fila.getLinea(),palabraReservada,getListaEjecuciones().get(j).getVariable(),"",getListaEjecuciones().get(j).getVar1(),getListaEjecuciones().get(j).getOperacion(),palabra);
                                                                        listaEjecuciones.set(j, ejec);
                                                                    }
                                                                }
                                                                //Se valida que los tipos de variables a operar sean de la misma familia
                                                                if(tipoVariable.equals(listaVariables.get(i).getTipo())){
                                                                }else{
                                                                    //Generamos el error y retornamos
                                                                    getListaMensajesSemanticos().add("Error de Sintaxis. Usted esta tratando de asignar variables de tipos diferentes. Linea: "+fila.getLinea());
                                                                }
                                                                band3 = true;
                                                            }
                                                        }
                                                        if(!band3){
                                                            //Generamos el error y retornamos
                                                            getListaMensajesSemanticos().add("Error de Sintaxis. Usted esta tratando de asignar una variable que no ha sido definida. Linea: "+fila.getLinea());
                                                        }
                                                    }
                                                    break;    
                                                default:
                                                    //Generamos el error y retornamos
                                                    getListaMensajes().add("Error de Sintaxis. La asignación de una variable únicamente se pueden operar máximo dos variables. Linea: "+fila.getLinea());
                                                    break;
                                            }        
                                            break;
                                        case "escribir":
                                            //Validación de la posición de la linea que se va a revisar y así saber si la
                                            //la sintaxis esta correcta o no
                                            switch(cont){
                                                case 1:
                                                    band2 = true;
                                                    if(validarAritmeticos(palabra)  || validarLogicos(palabra) || validarReservadas(palabra) || validarReservadasCierre(palabra) || validarReservadasIniciales(palabra) || validarTiposVariables(palabra)){
                                                        //Generamos el error y retornamos
                                                        getListaMensajesSemanticos().add("Error de Sintaxis. Usted esta tratando de escribir una palabra reservada. Linea: "+fila.getLinea());
                                                    }else{
                                                        band3 = false;
                                                        //Ciclo que valida que no exista una variable con el mismo nombre
                                                        for (int i=0; i<listaVariables.size(); i++){
                                                            if(palabra.equals(listaVariables.get(i).getNombre())){
                                                                band3 = true;
                                                            }
                                                        }
                                                        if(band3){                                                                                                                            //Cargamos el token de nombre de la variable
                                                            token = new Registro(fila.getLinea(),"Identificador", palabra,"Sintaxis correcta");
                                                            getListaTokens().add(token);
                                                            //Asignación de las ejecuciones a realizar
                                                            ejec = new Ejecuciones(fila.getLinea(),palabraReservada,palabra,"","","","");
                                                            getListaEjecuciones().add(ejec);
                                                        }else{
                                                            if(palabra.substring(0,1).equals("'")){
                                                                pose = fila.getToken().indexOf("'", pos+1);
                                                                if(pose > 0){
                                                                    token = new Registro(fila.getLinea(),"Identificador", fila.getToken().substring(pos+1,pose),"Sintaxis correcta");
                                                                    getListaTokens().add(token);
                                                                    
                                                                    //Asignación de las ejecuciones a realizar
                                                                    ejec = new Ejecuciones(fila.getLinea(),palabraReservada,"",fila.getToken().substring(pos+1,pose),"","","");
                                                                    getListaEjecuciones().add(ejec);
                                                                    
                                                                    posf = pose+1;
                                                                }else{
                                                                    //Generamos el error y retornamos
                                                                    getListaMensajes().add("Error de Sintaxis. Para escribir un texto hace falta cerrar con '. Linea: "+fila.getLinea());
                                                                    posf = fila.getToken().length();
                                                                }
                                                            }else{
                                                                //Generamos el error y retornamos
                                                                getListaMensajes().add("Error de Sintaxis. Usted no esta escribiendo ningún texto ó variable. Linea: "+fila.getLinea());
                                                            }
                                                        }
                                                    }
                                                    break;
                                                case 2:
                                                    band2 = false;
                                                    if(palabra.equals(palabras.getListaOperadoresAritmeticos().get(0))){
                                                        token = new Registro(fila.getLinea(),"Operador Aritmetico", palabra,"Sintaxis correcta");
                                                        getListaTokens().add(token);
                                                    }else{
                                                        //Generamos el error y retornamos
                                                        getListaMensajes().add("Error de Sintaxis. Para concatenar se debe usar el operador aritmetico +. Linea: "+fila.getLinea());
                                                    } 
                                                    break;
                                                case 3:
                                                    band2 = true;
                                                    if(validarAritmeticos(palabra)  || validarLogicos(palabra) || validarReservadas(palabra) || validarReservadasCierre(palabra) || validarReservadasIniciales(palabra) || validarTiposVariables(palabra)){
                                                        //Generamos el error y retornamos
                                                        getListaMensajes().add("Error de Sintaxis. Usted esta tratando de escribir una palabra reservada. Linea: "+fila.getLinea());
                                                    }else{
                                                        band3 = false;
                                                        //Ciclo que valida que no exista una variable con el mismo nombre
                                                        for (int i=0; i<listaVariables.size(); i++){
                                                            if(palabra.equals(listaVariables.get(i).getNombre())){
                                                                band3 = true;
                                                            }
                                                        }
                                                        if(band3){                                                                                                                            //Cargamos el token de nombre de la variable
                                                            token = new Registro(fila.getLinea(),"Identificador", palabra,"Sintaxis correcta");
                                                            getListaTokens().add(token);
                                                            
                                                            //Se valida si ya existe un escribir para esa linea
                                                            for(int i=0; i<getListaEjecuciones().size(); i++){
                                                                if(fila.getLinea() == getListaEjecuciones().get(i).getPosicion()){
                                                                    ejec = new Ejecuciones(fila.getLinea(),palabraReservada,palabra,"",getListaEjecuciones().get(i).getMensaje(),"+",palabra);
                                                                    listaEjecuciones.add(ejec);
                                                                    listaEjecuciones.remove(i);
                                                                }
                                                            }
                                                        }else{
                                                            if(palabra.substring(0,1).equals("'")){
                                                                pose = fila.getToken().indexOf("'", pos+1);
                                                                if(pose > 0){
                                                                    token = new Registro(fila.getLinea(),"Identificador", fila.getToken().substring(pos+1,posf),"Sintaxis correcta");
                                                                    getListaTokens().add(token);
                                                                    
                                                                    //Se valida si ya existe un escribir para esa linea
                                                                    for(int i=0; i<getListaEjecuciones().size(); i++){
                                                                        if(fila.getLinea() == getListaEjecuciones().get(i).getPosicion()){
                                                                            ejec = new Ejecuciones(fila.getLinea(),palabraReservada,"",getListaEjecuciones().get(i).getMensaje()+fila.getToken().substring(pos+1,posf),"","","");
                                                                            listaEjecuciones.add(ejec);
                                                                            listaEjecuciones.remove(i);
                                                                        }
                                                                    }
                                                                    
                                                                    posf = pose+1;
                                                                }else{
                                                                    //Generamos el error y retornamos
                                                                    getListaMensajes().add("Error de Sintaxis. Para escribir un texto hace falta cerrar con '. Linea: "+fila.getLinea());
                                                                    posf = fila.getToken().length();
                                                                }
                                                            }else{
                                                                //Generamos el error y retornamos
                                                                getListaMensajes().add("Error de Sintaxis. Usted no esta escribiendo ningún texto ó variable. Linea: "+fila.getLinea());
                                                            }
                                                        }
                                                    }
                                                    break;
                                                default:
                                                    //Generamos el error y retornamos
                                                    getListaMensajes().add("Error de Sintaxis. La escritura de un mensaje se puede realizar únicamente concatenando dos variables o texto como máximo. Linea: "+fila.getLinea());
                                                    break;
                                            }        
                                            break;
                                        case "leer":
                                            //Validación de la posición de la linea que se va a revisar y así saber si la
                                            //la sintaxis esta correcta o no
                                            switch(cont){
                                                case 1:
                                                    band2 = true;
                                                    if(validarAritmeticos(palabra)  || validarLogicos(palabra) || validarReservadas(palabra) || validarReservadasCierre(palabra) || validarReservadasIniciales(palabra) || validarTiposVariables(palabra)){
                                                        //Generamos el error y retornamos
                                                        getListaMensajes().add("Error de Sintaxis. Usted esta tratando de leer un valor en una palabra reservada. Linea: "+fila.getLinea());
                                                    }else{
                                                        band3 = false;
                                                        //Ciclo que valida que no exista una variable con el mismo nombre
                                                        for (int i=0; i<listaVariables.size(); i++){
                                                            if(palabra.equals(listaVariables.get(i).getNombre())){
                                                                band3 = true;
                                                            }
                                                        }
                                                        if(band3){
                                                            token = new Registro(fila.getLinea(),"Identificador", palabra,"Sintaxis correcta");
                                                            getListaTokens().add(token);
                                                            
                                                            ejec = new Ejecuciones(fila.getLinea(),palabraReservada,palabra,"","","","");
                                                            listaEjecuciones.add(ejec);
                                                        }else{
                                                            //Generamos el error y retornamos
                                                            getListaMensajesSemanticos().add("Error de Sintaxis. la variable donde desea capturar la información no ha sido definida. Linea: "+fila.getLinea());
                                                        }
                                                    }
                                                    break;
                                                default:
                                                    getListaMensajes().add("Error de Sintaxis. Para la lectura de datos no se puede adicionar más de una variable. Linea: "+fila.getLinea());
                                                    break;
                                            }    
                                            break;
                                        case "si(":
                                            //Validación de la posición de la linea que se va a revisar y así saber si la
                                            //la sintaxis esta correcta o no
                                            switch(cont){
                                                case 1:
                                                    band2 = false;
                                                    band3 = false;
                                                    if(validarAritmeticos(palabra)  || validarLogicos(palabra) || validarReservadas(palabra) || validarReservadasCierre(palabra) || validarReservadasIniciales(palabra) || validarTiposVariables(palabra)){
                                                        //Generamos el error y retornamos
                                                        getListaMensajesSemanticos().add("Error de Sintaxis. Para iniciar una validacion no se puede realizar con una palabra reservada. Linea: "+fila.getLinea());
                                                    }else{
                                                        //Ciclo para validar si la variable existe
                                                        for(int i=0; i<getListaVariables().size(); i++){
                                                            if(palabra.equals(getListaVariables().get(i).getNombre())){
                                                                if(getListaVariables().get(i).getTipo().equals("cadena")){
                                                                    vald_texto = true;
                                                                }
                                                                band3 = true;
                                                            }
                                                        }
                                                        //Validación si encontro la variable
                                                        if(band3){
                                                            //Se carga el token como correcto
                                                            token = new Registro(fila.getLinea(),"Identificador", palabra,"Sintaxis correcta");
                                                            getListaTokens().add(token);
                                                        }else{
                                                            //Se valida si estamos comparando contra una cadena
                                                            if(palabra.substring(0, 1).equals("'")){
                                                                pose = fila.getToken().indexOf("'", pos+1);
                                                                if(pose > 0){
                                                                    token = new Registro(fila.getLinea(),"Identificador", fila.getToken().substring(pos+1,pose),"Sintaxis correcta");
                                                                    getListaTokens().add(token);
                                                                    vald_texto = true;
                                                                    posf = pose+1;
                                                                }else{
                                                                    //Generamos el error y retornamos
                                                                    getListaMensajes().add("Error de Sintaxis. Para validar contra un texto hace falta cerrar con '. Linea: "+fila.getLinea());
                                                                    posf = fila.getToken().length();
                                                                }
                                                            }else{
                                                                //Se valida si estamos comparando contra un numero
                                                                try{
                                                                    Integer.parseInt(palabra);
                                                                    token = new Registro(fila.getLinea(),"Identificador", palabra,"Sintaxis correcta");
                                                                    getListaTokens().add(token);
                                                                } catch (NumberFormatException nfe){
                                                                    getListaMensajes().add("Error de Sintaxis. La variable contra la que quiere validar no se ha definido en el programa. Linea: "+fila.getLinea());
                                                                }
                                                            }
                                                        }
                                                    }
                                                    break;
                                                case 2:
                                                    band2 = false;
                                                    if(validarLogicos(palabra)){
                                                        token = new Registro(fila.getLinea(),"Operador Logico", palabra,"Sintaxis correcta");
                                                        getListaTokens().add(token);
                                                    }else{
                                                        getListaMensajes().add("Error de Sintaxis. Para realizar una validación se debe realizar con un operador logico. Linea: "+fila.getLinea());
                                                    }
                                                    break;
                                                case 3:
                                                    band2 = false;
                                                    band3 = false;
                                                    if(validarAritmeticos(palabra)  || validarLogicos(palabra) || validarReservadas(palabra) || validarReservadasCierre(palabra) || validarReservadasIniciales(palabra) || validarTiposVariables(palabra)){
                                                        //Generamos el error y retornamos
                                                        getListaMensajesSemanticos().add("Error de Sintaxis. Para realizar una validacion no se puede hacer contra una palabra reservada. Linea: "+fila.getLinea());
                                                    }else{
                                                        //Ciclo para validar si la variable existe
                                                        for(int i=0; i<getListaVariables().size(); i++){
                                                            if(palabra.equals(getListaVariables().get(i).getNombre())){
                                                                if(vald_texto && getListaVariables().get(i).getTipo().equals("cadena")){                          
                                                                }else{
                                                                    //Generamos el error y retornamos
                                                                    getListaMensajesSemanticos().add("Error de Sintaxis. Usted esta intentando comparar variables de tipos diferentes. Linea: "+fila.getLinea());
                                                                }
                                                                band3 = true;
                                                            }
                                                        }
                                                        //Validación si encontro la variable
                                                        if(band3){
                                                            //Se carga el token como correcto
                                                            token = new Registro(fila.getLinea(),"Identificador", palabra,"Sintaxis correcta");
                                                            getListaTokens().add(token);
                                                        }else{
                                                            //Se valida si estamos comparando contra una cadena
                                                            if(palabra.substring(0, 1).equals("'")){
                                                                pose = fila.getToken().indexOf("'", pos+1);
                                                                if(pose > 0){
                                                                    token = new Registro(fila.getLinea(),"Identificador", fila.getToken().substring(pos+1,pose),"Sintaxis correcta");
                                                                    getListaTokens().add(token);
                                                                    //Validamos que la bandera de validar texto este prendida
                                                                    if(!vald_texto){
                                                                        //Generamos el error y retornamos
                                                                        getListaMensajesSemanticos().add("Error de Sintaxis. Usted esta intentando comparar variables de tipos diferentes. Linea: "+fila.getLinea());
                                                                    }
                                                                    posf = pose+1;
                                                                }else{
                                                                    //Generamos el error y retornamos
                                                                    getListaMensajes().add("Error de Sintaxis. Para validar contra un texto hace falta cerrar con '. Linea: "+fila.getLinea());
                                                                    posf = fila.getToken().length();
                                                                }
                                                            }else{
                                                                //Se valida si estamos comparando contra un numero
                                                                try{
                                                                    Integer.parseInt(palabra);
                                                                    if(vald_texto){
                                                                        //Generamos el error y retornamos
                                                                        getListaMensajesSemanticos().add("Error de Sintaxis. Usted esta intentando comparar variables de tipos diferentes. Linea: "+fila.getLinea());
                                                                    }
                                                                    token = new Registro(fila.getLinea(),"Identificador", palabra,"Sintaxis correcta");
                                                                    getListaTokens().add(token);
                                                                } catch (NumberFormatException nfe){
                                                                    getListaMensajes().add("Error de Sintaxis. La variable contra la que quiere validar no se ha definido en el programa. Linea: "+fila.getLinea());
                                                                }
                                                            }
                                                        }
                                                    }
                                                    break;
                                                case 4:
                                                    band2=true;
                                                    si++;
                                                    if(palabra.equals(")")){
                                                        token = new Registro(fila.getLinea(),"Identificador", palabra,"Sintaxis correcta");
                                                        getListaTokens().add(token);
                                                    }else{
                                                        getListaMensajes().add("Error de Sintaxis. Para cerrar el ciclo SI se debe hacer con un ). Linea: "+fila.getLinea());
                                                    }
                                                    break;
                                                default:
                                                    getListaMensajes().add("Error de Sintaxis. Despues de haber finalizado el ciclo SI no se puede adicionar ninguna linea adicional. Linea: "+fila.getLinea());
                                                    break;
                                            }
                                            break;
                                        case "sino":
                                            //Validación de la posición de la linea que se va a revisar y así saber si la
                                            //la sintaxis esta correcta o no
                                            switch(cont){
                                                case 1:
                                                    getListaMensajes().add("Error de Sintaxis. Cuando se esta generando un SINO, esta linea no debe ir acompañada de más sentencias. Linea: "+fila.getLinea());
                                                    break;
                                            }
                                            break;
                                        case "finsi":
                                            //Validación de la posición de la linea que se va a revisar y así saber si la
                                            //la sintaxis esta correcta o no
                                            switch(cont){
                                                case 1:
                                                    getListaMensajes().add("Error de Sintaxis. Cuando se esta finalizando un condicional SI, esta linea no debe ir acompañada de más sentencias. Linea: "+fila.getLinea());
                                                    break;
                                            }
                                            break;
                                        case "para(":
                                            //Validación de la posición de la linea que se va a revisar y así saber si la
                                            //la sintaxis esta correcta o no
                                            switch(cont){
                                                case 1:
                                                    band2 = false;
                                                    //Se valida si estamos comparando contra un numero
                                                    try{
                                                        Integer.parseInt(palabra);
                                                        token = new Registro(fila.getLinea(),"Identificador", palabra,"Sintaxis correcta");
                                                        getListaTokens().add(token);
                                                    } catch (NumberFormatException nfe){
                                                        getListaMensajesSemanticos().add("Error de Sintaxis. Para generar un ciclo PARA se debe iniciar con un valor numerico. Linea: "+fila.getLinea());
                                                    }
                                                    break;
                                                case 2:
                                                    band2 = false;
                                                    if(palabra.equals("hasta")){
                                                        token = new Registro(fila.getLinea(),"Palabra Reservada", palabra,"Sintaxis correcta");
                                                        getListaTokens().add(token);
                                                    }else{
                                                        getListaMensajesSemanticos().add("Error de Sintaxis. En la definición del ciclo PARA despues del primer número, debe ir la palabra HASTA. Linea: "+fila.getLinea());
                                                    }
                                                    break;
                                                case 3:
                                                    band2 = false;
                                                    //Se valida si estamos comparando contra un numero
                                                    try{
                                                        Integer.parseInt(palabra);
                                                        token = new Registro(fila.getLinea(),"Identificador", palabra,"Sintaxis correcta");
                                                        getListaTokens().add(token);
                                                    } catch (NumberFormatException nfe){
                                                        getListaMensajesSemanticos().add("Error de Sintaxis. Para generar un ciclo PARA se debe finalizar con un valor numerico. Linea: "+fila.getLinea());
                                                    }
                                                    break;
                                                case 4:
                                                    para++;
                                                    band2 = true;
                                                    if(palabra.equals(")")){
                                                        token = new Registro(fila.getLinea(),"Identificador", palabra,"Sintaxis correcta");
                                                        getListaTokens().add(token);
                                                    }else{
                                                        getListaMensajes().add("Error de Sintaxis. Para cerrar el ciclo PARA se debe hacer con un ). Linea: "+fila.getLinea());
                                                    }
                                                    break;
                                                default:
                                                    getListaMensajes().add("Error de Sintaxis. Después de haber finalizado el ciclo PARA no se puede adicionar ninguna palabra adicional. Linea: "+fila.getLinea());
                                                    break;
                                            }
                                            break;
                                        case "finpara":
                                            //Validación de la posición de la linea que se va a revisar y así saber si la
                                            //la sintaxis esta correcta o no
                                            switch(cont){
                                                case 1:
                                                    getListaMensajes().add("Error de Sintaxis. Cuando se esta finalizando un condicional PARA, esta linea no debe ir acompañada de más sentencias. Linea: "+fila.getLinea());
                                                    break;
                                            }
                                            break;
                                        }
                                }
                            }
                        }
                    }
                    pos = posf+1;
                    cont++;
                }else{            
                }
            }
            //Validación si la linea que se valido finalizo correctamente o no
            if (!band2){
                //Generamos el error y retornamos
                getListaMensajes().add("Error de Sintaxis. Error de finalización de linea. Linea: "+fila.getLinea());
            }
        }
        if(si>0){
            getListaMensajes().add("Error de Sintaxis. No se ha finalizado un condicional SI, por favor valide.");
        }
        if(para>0){
            getListaMensajes().add("Error de Sintaxis. No se ha finalizado un ciclo PARA, por favor valide.");
        }
        //Finalización de los análisis semanticos y sintacticos
        getListaMensajes().add("************************* ANÁLISIS COMPLETADO **************************");
        listaMensajesSemanticos.add("************************* ANÁLISIS COMPLETADO **************************");
        return band;
    }
    
    //Funcion que realiza la ejecución del código del programa si todo sale bien
    public void ejecutarPrograma(){
        String captura;
        Variables var;
        String var1="",var2="";
        
        //Se valida que no hayan errores en la validación del código
        if(getListaMensajes().size() < 5 && getListaMensajesSemanticos().size() < 3){
            //Ciclo que recorre la lista de las ejecuciones a realizar
            for(int i=0; i<getListaEjecuciones().size(); i++){
                //Se valida si la ejecución es escribir
                if(getListaEjecuciones().get(i).getAccion().equals("escribir")){
                    //Se valida si se debe escribri una variable o una palabra constante
                    if(getListaEjecuciones().get(i).getVariable().equals("")){}else{
                        //Ciclo que recorre el arreglo donde están almacenadas las variables
                        for(int j=0; j<getListaVariables().size(); j++){
                            //Validamos que la variable que vamos a imprimir sea la misma de la sentencia
                            if(getListaVariables().get(j).getNombre().equals(getListaEjecuciones().get(i).getVariable())){
                                //Se valida si el primer campo es una variable o es el segundo para saber la posición de impresión
                                if(getListaEjecuciones().get(i).getVariable().equals(getListaEjecuciones().get(i).getVar1())){
                                    JOptionPane.showMessageDialog(null,getListaVariables().get(j).getValor()+getListaEjecuciones().get(i).getVar2());
                                }else{
                                    JOptionPane.showMessageDialog(null,getListaEjecuciones().get(i).getVar1()+getListaVariables().get(j).getValor());
                                }
                            }
                        }
                    }
                    JOptionPane.showMessageDialog(null, getListaEjecuciones().get(i).getMensaje());
                }else{
                    //Se valida si la accion a realizar es leer
                    if(getListaEjecuciones().get(i).getAccion().equals("leer")){
                        //capturamos lo que el usuario ingreso
                        captura = JOptionPane.showInputDialog("");
                        //Ciclo que recorre el listado de las variables
                        for(int j=0; j<getListaVariables().size(); j++){
                            //Validación si la variable que se esta capturando es la misma de la lista de variables
                            if(getListaEjecuciones().get(i).getVariable().equals(getListaVariables().get(j).getNombre())){
                                //Si la variable es de tipo numerica
                                if(getListaVariables().get(j).getTipo().equals("numero")){
                                    //Se realiza un cast para validar que sea un numero si genera error 
                                    //quiere decir que tiene letras y no es numerica.
                                    try {
                                        Integer.parseInt(captura);
                                        var = new Variables(getListaVariables().get(j).getLinea(),getListaVariables().get(j).getNombre(),getListaVariables().get(j).getTipo(),captura);
                                        getListaVariables().add(var);
                                        getListaVariables().remove(j);
                                        
                                    } catch (NumberFormatException nfe){
                                        //Se meustra error en pantalla de la captura de variable.
                                        JOptionPane.showMessageDialog(null, "Error. Usted esta cargando en una variable numerica un tipo de dato incorrecto.");
                                        return;
                                    }
                                }else{
                                    //Se carga el valor de la variable de tipo cadena que se capturo en el programa
                                    var = new Variables(getListaVariables().get(j).getLinea(),getListaVariables().get(j).getNombre(),getListaVariables().get(j).getTipo(),captura);
                                    getListaVariables().add(var);
                                    getListaVariables().remove(j);
                                }
                            }
                        }
                    }else{
                        //Se valida cuando es la acción de asignar
                        if(getListaEjecuciones().get(i).getAccion().equals("asignar")){
                            //Se recorre la lsita de variables para identificar los valores de las variables a operar
                            for (int j=0; j<getListaVariables().size(); j++){
                                //Validación para cuando encuentre la pimera variable y se carga su valor
                                if(getListaEjecuciones().get(i).getVar1().equals(getListaVariables().get(j).getNombre())){
                                    var1 = getListaVariables().get(j).getValor();
                                }
                                //Validacion para cuando encuentre la segunda cariable y se carga su valor
                                if(getListaEjecuciones().get(i).getVar2().equals(getListaVariables().get(j).getNombre())){
                                    var2 = getListaVariables().get(j).getValor();
                                }
                            }
                            //Ciclo que recorre la lista de variables para validar cual es la posición de la variable a 
                            //modificar su valor
                            for(int j=0; j<getListaVariables().size(); j++){
                                if(getListaEjecuciones().get(i).getVariable().equals(getListaVariables().get(j).getNombre())){
                                    if(getListaVariables().get(j).getTipo().equals("cadena")){
                                        if(getListaEjecuciones().get(i).getVar2().equals("")){
                                            var = new Variables(getListaVariables().get(j).getLinea(),getListaVariables().get(j).getNombre(),getListaVariables().get(j).getTipo(),var1);
                                            getListaVariables().set(j, var);
                                        }else{
                                            var = new Variables(getListaVariables().get(j).getLinea(),getListaVariables().get(j).getNombre(),getListaVariables().get(j).getTipo(),var1+var2);
                                            getListaVariables().set(j, var);
                                        }
                                    }else{
                                        if(getListaEjecuciones().get(i).getVar2().equals("")){
                                            var = new Variables(getListaVariables().get(j).getLinea(),getListaVariables().get(j).getNombre(),getListaVariables().get(j).getTipo(),var1);
                                            getListaVariables().set(j, var);
                                        }else{
                                            switch(getListaEjecuciones().get(i).getOperacion()){
                                                case "+":
                                                    var = new Variables(getListaVariables().get(j).getLinea(),getListaVariables().get(j).getNombre(),getListaVariables().get(j).getTipo(),String.valueOf((Integer.parseInt(var1)+Integer.parseInt(var2))));
                                                    getListaVariables().set(j, var);
                                                    break;
                                                case "-":
                                                    var = new Variables(getListaVariables().get(j).getLinea(),getListaVariables().get(j).getNombre(),getListaVariables().get(j).getTipo(),String.valueOf((Integer.parseInt(var1)-Integer.parseInt(var2))));
                                                    getListaVariables().set(j, var);
                                                    break;
                                                case "*":
                                                    var = new Variables(getListaVariables().get(j).getLinea(),getListaVariables().get(j).getNombre(),getListaVariables().get(j).getTipo(),String.valueOf((Integer.parseInt(var1)*Integer.parseInt(var2))));
                                                    getListaVariables().set(j, var);
                                                    break;
                                                case "/":
                                                    if(var2.equals("0")){
                                                        JOptionPane.showMessageDialog(null, "No se puede dividir por 0");
                                                        return;
                                                    }else{
                                                        var = new Variables(getListaVariables().get(j).getLinea(),getListaVariables().get(j).getNombre(),getListaVariables().get(j).getTipo(),String.valueOf((Integer.parseInt(var1)/Integer.parseInt(var2))));
                                                        getListaVariables().set(j, var);
                                                    }
                                                    break;
                                                case "^":
                                                    var = new Variables(getListaVariables().get(j).getLinea(),getListaVariables().get(j).getNombre(),getListaVariables().get(j).getTipo(),String.valueOf((Math.pow(Integer.parseInt(var1),Integer.parseInt(var2)))));
                                                    getListaVariables().set(j, var);
                                                    break;
                                            }
                                        }
                                    }                                    
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    //Función que valida si una palabra es palabra reservada inicial
    public boolean validarReservadasIniciales(String palabra){
        boolean band=false;        
        for (int i=0; i<palabras.getListaPalabrasReservadasIniciales().size(); i++){
            if(palabra.equals(palabras.getListaPalabrasReservadasIniciales().get(i))){
                band = true;
            }
        }
        return band;
    }

    //Función que valida si una palabra es palabra reservada
    public boolean validarReservadas(String palabra){
        boolean band=false;        
        for (int i=0; i<palabras.getListaPalabrasReservadas().size(); i++){
            if(palabra.equals(palabras.getListaPalabrasReservadas().get(i))){
                band = true;
            }
        }
        return band;
    }
            
    //Función que valida si una palabra es un operador aritmetico
    public boolean validarAritmeticos(String palabra){
        boolean band=false;        
        for (int i=0; i<palabras.getListaOperadoresAritmeticos().size(); i++){
            if(palabra.equals(palabras.getListaOperadoresAritmeticos().get(i))){
                band = true;
            }
        }
        return band;
    }
    
    //Función que valida si una palabra es un operador logico
    public boolean validarLogicos(String palabra){
        boolean band=false;        
        for (int i=0; i<palabras.getListaOperadoresLogicos().size(); i++){
            if(palabra.equals(palabras.getListaOperadoresLogicos().get(i))){
                band = true;
            }
        }
        return band;
    }
    
    //Función que valida si una palabra es un tipo de variable
    public boolean validarTiposVariables(String palabra){
        boolean band=false;        
        for (int i=0; i<palabras.getListaTiposVariables().size(); i++){
            if(palabra.equals(palabras.getListaTiposVariables().get(i))){
                band = true;
            }
        }
        return band;
    }
    
    //Función que valida si una palabra es una palabra reservada de cierre
    public boolean validarReservadasCierre(String palabra){
        boolean band=false;        
        for (int i=0; i<palabras.getListaPalabrasReservadasCierre().size(); i++){
            if(palabra.equals(palabras.getListaPalabrasReservadasCierre().get(i))){
                band = true;
            }
        }
        return band;
    }
    
    public List<Registro> getListaSentencias() {
        return listaSentencias;
    }
    
    //Documentacion
    public List<Registro> getListaTokens() {
        return listaTokens;
    }
    
    //Documentacion
    public List<String> getListaMensajes() {
        return listaMensajes;
    }

    public List<Variables> getListaVariables() {
        return listaVariables;
    }

    public List<String> getListaMensajesSemanticos() {
        return listaMensajesSemanticos;
    }

    public List<Ejecuciones> getListaEjecuciones() {
        return listaEjecuciones;
    }
    
}