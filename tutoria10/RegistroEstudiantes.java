package sv.edu.ues.www.ejemplo0805;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author pavil
 */
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
 
/**
 * Aplicacion de Registro de Estudiantes.
 *
 * Demuestra:
 *   - BorderLayout  : organiza las zonas principales de la ventana
 *   - GridBagLayout : alinea los campos del formulario en filas y columnas
 *   - FlowLayout    : acomoda los botones de accion
 *   - FocusListener : valida cada campo cuando el usuario lo abandona
 *   - KeyListener   : restringe la entrada de caracteres en campos numericos
 */
public class RegistroEstudiantes extends JFrame {
 
    // -------------------------------------------------------------------------
    // Campos del formulario — se declaran como atributos para que todos
    // los metodos de la clase puedan acceder a ellos (encapsulamiento basico).
    // -------------------------------------------------------------------------
    private JTextField campoNombre;
    private JTextField campoApellido;
    private JTextField campoCarne;
    private JTextField campoCorreo;
    private JComboBox<String> comboCarrera;
    private JTable tablaEstudiantes;
    private DefaultTableModel modeloTabla;
 
    // Lista interna que almacena los estudiantes registrados.
    // Separar los datos de la presentacion es un principio basico de POO.
    private List<Estudiante> listaEstudiantes;
 
    // -------------------------------------------------------------------------
    // Constructor — configura la ventana y construye la interfaz
    // -------------------------------------------------------------------------
    public RegistroEstudiantes() {
        super("Registro de Estudiantes");
 
        listaEstudiantes = new ArrayList<>();
 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setMinimumSize(new Dimension(750, 500));
        setLocationRelativeTo(null);
 
        // BorderLayout divide la ventana en cinco zonas: NORTH, SOUTH, EAST, WEST, CENTER.
        // Es el layout raiz mas comun en aplicaciones Swing.
        setLayout(new BorderLayout(10,10));
 
        add(construirPanelFormulario(), BorderLayout.WEST);
        add(construirPanelTabla(),      BorderLayout.CENTER);
        add(construirPanelBotones(),    BorderLayout.SOUTH);
    }
 
    // =========================================================================
    // PANEL FORMULARIO — usa GridBagLayout
    // =========================================================================
 
    /**
     * Construye el panel izquierdo con los campos de entrada.
     * GridBagLayout permite posicionar componentes en una cuadricula flexible,
     * controlando cuantas columnas ocupa cada uno y como se alinea.
     */
    private JPanel construirPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Datos del Estudiante",
            TitledBorder.LEFT,
            TitledBorder.TOP
        ));
        panel.setPreferredSize(new Dimension(320, 0));
 
        // GridBagConstraints define las reglas de posicionamiento de cada componente.
        // Se reutiliza el mismo objeto modificando sus atributos segun se necesite.
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(6, 8, 6, 8); // margen alrededor de cada celda
        gbc.anchor  = GridBagConstraints.WEST; // alinear contenido a la izquierda
        gbc.fill    = GridBagConstraints.HORIZONTAL; // estirar horizontalmente
 
        // --- Fila 0: Nombre ---
        campoNombre = new JTextField(18);
        agregarFila(panel, gbc, 0, "Nombre:", campoNombre);
        campoNombre.addFocusListener(new ValidadorCampoTexto(campoNombre, "Nombre"));
 
        // --- Fila 1: Apellido ---
        campoApellido = new JTextField(18);
        agregarFila(panel, gbc, 1, "Apellido:", campoApellido);
        campoApellido.addFocusListener(new ValidadorCampoTexto(campoApellido, "Apellido"));
 
        // --- Fila 2: Carne (solo numeros) ---
        campoCarne = new JTextField(18);
        
        agregarFila(panel, gbc, 2, "Carne:", campoCarne);
        campoCarne.addFocusListener(new ValidadorCampoTexto(campoCarne, "Carne"));
        // KeyListener para bloquear cualquier caracter que no sea digito
        campoCarne.addKeyListener(new FiltroSoloNumeros());
 
        // --- Fila 3: Correo ---
        campoCorreo = new JTextField(18);
        agregarFila(panel, gbc, 3, "Correo:", campoCorreo);
        campoCorreo.addFocusListener(new ValidadorCorreo(campoCorreo));
 
        // --- Fila 4: Carrera ---
        String[] carreras = { "-- Seleccione --", "Ingenieria en Sistemas",
                              "Ingenieria Civil", "Administracion de Empresas",
                              "Contaduria Publica", "Derecho" };
        comboCarrera = new JComboBox<>(carreras);
        agregarFila(panel, gbc, 4, "Carrera:", comboCarrera);
 
        return panel;
    }
 
    /**
     * Metodo auxiliar que agrega una etiqueta y un campo en la fila indicada.
     * Centralizar esta logica evita repetir configuracion de GridBagConstraints
     * para cada par etiqueta-campo.
     *
     * @param panel     el panel destino
     * @param gbc       restricciones reutilizables
     * @param fila      numero de fila en la cuadricula
     * @param etiqueta  texto descriptivo
     * @param campo     componente de entrada
     */
    private void agregarFila(JPanel panel, GridBagConstraints gbc,
                              int fila, String etiqueta, JComponent campo) {
        // Columna 0: etiqueta
        gbc.gridx    = 0;
        gbc.gridy    = fila;
        gbc.weightx  = 0.0; // la etiqueta no se estira
        gbc.fill     = GridBagConstraints.NONE;
        panel.add(new JLabel(etiqueta), gbc);
 
        // Columna 1: campo de entrada
        gbc.gridx    = 1;
        gbc.weightx  = 1.0; // el campo ocupa el espacio sobrante
        gbc.fill     = GridBagConstraints.HORIZONTAL;
        panel.add(campo, gbc);
    }
 
    // =========================================================================
    // PANEL TABLA — zona central
    // =========================================================================
 
    /**
     * Construye el panel derecho con la tabla de estudiantes registrados.
     * DefaultTableModel permite agregar y eliminar filas en tiempo de ejecucion.
     */
    private JPanel construirPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(),
            "Estudiantes Registrados",
            TitledBorder.LEFT,
            TitledBorder.TOP
        ));
 
        String[] columnas = { "Carne", "Nombre completo", "Correo", "Carrera" };
 
        // DefaultTableModel con false en el segundo parametro hace las celdas
        // no editables directamente desde la tabla.
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
 
        tablaEstudiantes = new JTable(modeloTabla);
        tablaEstudiantes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEstudiantes.setRowHeight(24);
        tablaEstudiantes.getTableHeader().setReorderingAllowed(false);
 
        panel.add(new JScrollPane(tablaEstudiantes), BorderLayout.CENTER);
        return panel;
    }
 
    // =========================================================================
    // PANEL BOTONES — usa FlowLayout
    // =========================================================================
 
    /**
     * Construye la barra inferior de botones.
     * FlowLayout acomoda los componentes uno tras otro de izquierda a derecha,
     * como palabras en un parrafo. Es el layout mas sencillo de Swing.
     */
    private JPanel construirPanelBotones() {
        // FlowLayout.RIGHT alinea los botones hacia la derecha
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        panel.setBorder(BorderFactory.createEtchedBorder());
 
        JButton btnRegistrar = new JButton("Registrar");
        JButton btnLimpiar   = new JButton("Limpiar");
        JButton btnEliminar  = new JButton("Eliminar seleccionado");
        JButton btnSalir     = new JButton("Salir");
 
        btnRegistrar.addActionListener(e -> registrarEstudiante());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnEliminar.addActionListener(e -> eliminarSeleccionado());
        btnSalir.addActionListener(e -> confirmarSalida());
 
        panel.add(btnRegistrar);
        panel.add(btnLimpiar);
        panel.add(btnEliminar);
        panel.add(btnSalir);
 
        return panel;
    }
 
    // =========================================================================
    // LOGICA DE NEGOCIO
    // =========================================================================
 
    /**
     * Valida el formulario completo y, si todo es correcto, crea un objeto
     * Estudiante y lo agrega tanto a la lista interna como a la tabla visual.
     */
    private void registrarEstudiante() {
        if (!formularioEsValido()) {
            JOptionPane.showMessageDialog(this,
                "Por favor corrija los campos marcados en rojo antes de continuar.",
                "Formulario incompleto",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        // Se crea el objeto Estudiante con los datos del formulario.
        // La clase Estudiante encapsula los datos — no los manipulamos directamente.
        Estudiante estudiante = new Estudiante(
            campoCarne.getText().trim(),
            campoNombre.getText().trim(),
            campoApellido.getText().trim(),
            campoCorreo.getText().trim(),
            (String) comboCarrera.getSelectedItem()
        );
 
        listaEstudiantes.add(estudiante);
 
        // Agregar una fila a la tabla con los datos del estudiante
        modeloTabla.addRow(new Object[]{
            estudiante.getCarne(),
            estudiante.getNombreCompleto(),
            estudiante.getCorreo(),
            estudiante.getCarrera()
        });
 
        limpiarFormulario();
        JOptionPane.showMessageDialog(this,
            "Estudiante registrado correctamente.",
            "Registro exitoso",
            JOptionPane.INFORMATION_MESSAGE);
    }
 
    /**
     * Verifica que todos los campos obligatorios tengan contenido valido.
     */
    private boolean formularioEsValido() {
        if (campoNombre.getText().trim().isEmpty())    return false;
        if (campoApellido.getText().trim().isEmpty())  return false;
        if (campoCarne.getText().trim().isEmpty())     return false;
        if (!campoCorreo.getText().contains("@"))      return false;
        if (comboCarrera.getSelectedIndex() == 0)     return false;
        return true;
    }
 
    /**
     * Restaura todos los campos a su estado inicial.
     */
    private void limpiarFormulario() {
        campoNombre.setText("");
        campoApellido.setText("");
        campoCarne.setText("");
        campoCorreo.setText("");
        comboCarrera.setSelectedIndex(0);
 
        // Quitar el fondo rojo de validacion de todos los campos
        Color fondo = UIManager.getColor("TextField.background");
        campoNombre.setBackground(fondo);
        campoApellido.setBackground(fondo);
        campoCarne.setBackground(fondo);
        campoCorreo.setBackground(fondo);
 
        campoNombre.requestFocus();
    }
 
    /**
     * Elimina la fila seleccionada de la tabla y su objeto correspondiente
     * de la lista interna.
     */
    private void eliminarSeleccionado() {
        int filaSeleccionada = tablaEstudiantes.getSelectedRow();
 
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this,
                "Seleccione un estudiante de la tabla para eliminarlo.",
                "Sin seleccion",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
 
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "Esta seguro que desea eliminar este registro?",
            "Confirmar eliminacion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
 
        if (confirmacion == JOptionPane.YES_OPTION) {
            listaEstudiantes.remove(filaSeleccionada);
            modeloTabla.removeRow(filaSeleccionada);
        }
    }
 
    /**
     * Pide confirmacion antes de cerrar la aplicacion.
     */
    private void confirmarSalida() {
        int opcion = JOptionPane.showConfirmDialog(this,
            "Desea salir de la aplicacion?",
            "Confirmar salida",
            JOptionPane.YES_NO_OPTION);
 
        if (opcion == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }
 
    // =========================================================================
    // CLASES INTERNAS — Eventos avanzados
    //
    // Usar clases internas es un patron comun en Swing. Permite que el listener
    // acceda directamente a los atributos del JFrame sin necesidad de pasarlos
    // como parametros.
    // =========================================================================
 
    /**
     * FocusListener reutilizable para campos de texto obligatorios.
     *
     * FocusListener tiene dos metodos:
     *   focusGained  — se ejecuta cuando el campo recibe el cursor
     *   focusLost    — se ejecuta cuando el usuario pasa a otro componente
     *
     * La validacion ocurre en focusLost: si el campo quedo vacio,
     * se pinta de rojo para alertar al usuario visualmente.
     */
    private static class ValidadorCampoTexto implements FocusListener {
 
        private final JTextField campo;
        private final String nombreCampo;
        private static final Color COLOR_ERROR   = new Color(255, 200, 200);
        private static final Color COLOR_NORMAL  = Color.WHITE;
 
        public ValidadorCampoTexto(JTextField campo, String nombreCampo) {
            this.campo       = campo;
            this.nombreCampo = nombreCampo;
        }
 
        @Override
        public void focusGained(FocusEvent e) {
            // Al entrar al campo, restaurar el color normal
            campo.setBackground(COLOR_NORMAL);
        }
 
        @Override
        public void focusLost(FocusEvent e) {
            // Al salir del campo, verificar que no este vacio
            if (campo.getText().trim().isEmpty()) {
                campo.setBackground(COLOR_ERROR);
                campo.setToolTipText(nombreCampo + " es obligatorio");
            } else {
                campo.setBackground(COLOR_NORMAL);
                campo.setToolTipText(null);
            }
        }
    }
 
    /**
     * FocusListener especializado para validar formato de correo electronico.
     * Extiende la logica basica: ademas de verificar que no este vacio,
     * comprueba que el texto contenga el caracter '@'.
     */
    private static class ValidadorCorreo implements FocusListener {
 
        private final JTextField campo;
        private static final Color COLOR_ERROR  = new Color(255, 200, 200);
        private static final Color COLOR_NORMAL = Color.WHITE;
 
        public ValidadorCorreo(JTextField campo) {
            this.campo = campo;
        }
 
        @Override
        public void focusGained(FocusEvent e) {
            campo.setBackground(COLOR_NORMAL);
        }
 
        @Override
        public void focusLost(FocusEvent e) {
            String texto = campo.getText().trim();
            boolean valido = !texto.isEmpty() && texto.contains("@");
 
            if (!valido) {
                campo.setBackground(COLOR_ERROR);
                campo.setToolTipText("Ingrese un correo valido (debe contener @)");
            } else {
                campo.setBackground(COLOR_NORMAL);
                campo.setToolTipText(null);
            }
        }
    }
 
    /**
     * KeyListener que bloquea cualquier tecla que no sea un digito numerico.
     *
     * KeyListener tiene tres metodos:
     *   keyPressed  — antes de que el caracter aparezca en el campo
     *   keyReleased — despues de soltar la tecla
     *   keyTyped    — cuando se produce un caracter imprimible
     *
     * La restriccion se aplica en keyTyped usando e.consume(), que cancela
     * el evento y evita que el caracter llegue al campo de texto.
     */
    private static class FiltroSoloNumeros implements KeyListener {
 
        @Override
        public void keyTyped(KeyEvent e) {
            char caracter = e.getKeyChar();
 
            // Si el caracter no es un digito, cancelar el evento
            if (!Character.isDigit(caracter)) {
                e.consume();
            }
        }
 
        // Estos dos metodos son obligatorios al implementar KeyListener,
        // pero no necesitan logica en este caso.
        @Override public void keyPressed (KeyEvent e) {}
        @Override public void keyReleased(KeyEvent e) {}
    }
     }
    