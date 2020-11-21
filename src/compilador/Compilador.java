/**
 *
 * @author Julian Camilo Melo
 */
package compilador;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.time.Clock.system;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

public class Compilador extends JFrame implements ActionListener {

    File archivo = null;
    FileReader fr = null;
    BufferedReader br = null;  

    private final AnalizadorArchivo analizadorBean;
    private final MenuBar menuBar;
    private final Menu menuArc;
    private final Menu menuEdi;
    //private final Menu menuAna;
    private final Menu menuEje;
    private final Menu menuAyu;
    private final MenuItem menuArcAbr;
    private final MenuItem menuArcGua;
    private final MenuItem menuArcSal;
    private final MenuItem menuEdiCop;
    private final MenuItem menuEdiCor;
    private final MenuItem menuEdiPeg;
    private final MenuItem menuEdiSel;
    //private final MenuItem menuAnaCom;
    private final MenuItem menuEjeCor;
    private final MenuItem menuAyuAyu;
    //private final MenuItem menuAyuAce;
    private final JPanel status;
    private final JPanel panelpp;
    private final JLabel statusMsg1;
    private final JLabel statusMsg2;
    private final JScrollPane parea1;
    private final JScrollPane parea2;
    private final JScrollPane parea3;
    private final JTable tablaSimbo;
    private final DefaultTableModel dtm;
    private final JTextArea txtArea1;
    private final JTextArea txtArea2;
    private final String tablaSimbolos[][] = new String[50][3];

    @SuppressWarnings("LeakingThisInConstructor")
    public Compilador() {
        analizadorBean = new AnalizadorArchivo();
        super.setSize(new Dimension(900, 700));
        super.setLocationRelativeTo(null);
        super.getContentPane().setLayout(new BorderLayout());
        super.setBackground(Color.black);
        menuBar = new MenuBar();
        menuArc = new Menu();
        menuArcSal = new MenuItem();
        menuArcAbr = new MenuItem();
        menuArcGua = new MenuItem();
        menuArc.setLabel("Archivo");
        menuArcAbr.setLabel("Abrir");
        menuArcGua.setLabel("Guardar");
        menuArcSal.setLabel("Salir");
        menuArcSal.addActionListener(this);
        menuArcAbr.addActionListener(this);
        menuArcGua.addActionListener(this);
        menuArc.add(menuArcAbr);
        menuArc.add(menuArcGua);
        menuArc.add(menuArcSal);
        menuArc.insertSeparator(2);
        menuEdi = new Menu();
        menuEdiCop = new MenuItem();
        menuEdiCor = new MenuItem();
        menuEdiPeg = new MenuItem();
        menuEdiSel = new MenuItem();
        menuEdi.setLabel("Editar");
        menuEdiCop.setLabel("Copiar");
        menuEdiCor.setLabel("Cortar");
        menuEdiPeg.setLabel("Pegar");
        menuEdiSel.setLabel("Seleccionar Todo");
        menuEdiCop.addActionListener(this);
        menuEdiCor.addActionListener(this);
        menuEdiPeg.addActionListener(this);
        menuEdiSel.addActionListener(this);
        menuEdi.add(menuEdiCop);
        menuEdi.add(menuEdiCor);
        menuEdi.add(menuEdiPeg);
        menuEdi.add(menuEdiSel);
        //menuAna = new Menu();
        //menuAnaCom = new MenuItem();
        //menuAna.setLabel("Analizar");
        //menuAnaCom.setLabel("Compilar");
        //menuAnaCom.addActionListener(this);
        //menuAna.add(menuAnaCom);
        menuEje = new Menu();
        menuEjeCor = new MenuItem();
        menuEje.setLabel("Ejecutar");
        menuEjeCor.setLabel("Correr");
        menuEjeCor.addActionListener(this);
        menuEje.add(menuEjeCor);
        menuAyu = new Menu();
        menuAyuAyu = new MenuItem();
        //menuAyuAce = new MenuItem();
        menuAyu.setLabel("Ayuda");
        menuAyuAyu.setLabel("Manual de Uusuario");
        //menuAyuAce.setLabel("Acerca de...");
        menuAyuAyu.addActionListener(this);
        //menuAyuAce.addActionListener(this);
        menuAyu.add(menuAyuAyu);
        //menuAyu.add(menuAyuAce);
        menuBar.add(menuArc);
        menuBar.add(menuEdi);
        //menuBar.add(menuAna);
        menuBar.add(menuEje);
        menuBar.add(menuAyu);
        status = new JPanel();
        status.setLayout(new BorderLayout());
        statusMsg1 = new JLabel("Estado: ");
        statusMsg2 = new JLabel();
        status.add(statusMsg1, BorderLayout.WEST);
        status.add(statusMsg2, BorderLayout.CENTER);
        super.getContentPane().add(status, BorderLayout.SOUTH);
        txtArea1 = new JTextArea();
        txtArea2 = new JTextArea();
        txtArea2.setEditable(false);
        String[] columnNames = {"Token", "Linea", "Tipo"};
        this.dtm = new DefaultTableModel(tablaSimbolos, columnNames);
        this.tablaSimbo = new JTable(dtm);
        parea1 = new JScrollPane(txtArea1, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        parea2 = new JScrollPane(txtArea2, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        parea3 = new JScrollPane(tablaSimbo, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        panelpp = new JPanel();
        panelpp.setLayout(new GridLayout(3, 1));
        panelpp.add(parea1);
        panelpp.add(parea2);
        panelpp.add(parea3);
        super.getContentPane().add(panelpp, BorderLayout.CENTER);
        super.setTitle("Editor: Analizador de Sintaxis");
        super.setMenuBar(menuBar);
        super.setVisible(true);

        super.addWindowListener(new WindowAdapter() {
            public void WindowClosing(WindowEvent e) {
                System.exit(0);
            }
        }
        );
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == menuArcAbr) {
            
            String codigoLeido = cargarDatos();
            txtArea1.setText(codigoLeido);
            
        }
        if (e.getSource() == menuArcSal) {
            dispose();
            System.exit(0);
        }

        if (e.getSource() == menuEdiCop) {
            txtArea1.copy();
        }
        if (e.getSource() == menuEdiCor) {
            txtArea1.cut();
        }
        if (e.getSource() == menuEdiPeg) {
            txtArea1.paste();
        }

        if (e.getSource() == menuEdiSel) {
            txtArea1.selectAll();
        }
        //Creacion de la condicion para el boton Ejecutar 
        if (e.getSource() == menuEjeCor) {
            analizadorBean.iniciarAnalisis(txtArea1.getText());
            String cadena = "";
            cadena = analizadorBean.getListaMensajes().stream().map((mensaje) -> mensaje + "\n").reduce(cadena, String::concat)+analizadorBean.getListaMensajesSemanticos().stream().map((mensaje) -> mensaje + "\n").reduce(cadena, String::concat);
            txtArea2.setText(cadena);

            this.dtm.setRowCount(0);
            analizadorBean.getListaTokens().stream().forEach((token) -> {
                this.dtm.addRow(new Object[]{token.getTipo(), token.getLinea(), token.getToken()});
            });
            this.tablaSimbo.setModel(dtm);
        }
        
        if (e.getSource() == menuAyuAyu) {
            System.out.println("Abrir PDF");
        }
    }

    public static void main(String[] args) {
        Compilador analizadorView = new Compilador();
        analizadorView.setVisible(true);
        analizadorView.setLocationRelativeTo(null);
    }

    private String cargarDatos() {
        try {
            // Apertura del fichero y creacion de BufferedReader para poder
            // hacer una lectura comoda (disponer del metodo readLine()).
            archivo = new File("prueba.txt");
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);
            StringBuilder codigo = new StringBuilder();
            // Lectura del fichero
            String linea;
         
            while ((linea = br.readLine()) != null) {
             
             //System.out.println(linea);
             codigo.append(linea);
             codigo.append("\n");
            }
            return codigo.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            // En el finally cerramos el fichero, para asegurarnos
            // que se cierra tanto si todo va bien como si salta 
            // una excepcion.
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
}
