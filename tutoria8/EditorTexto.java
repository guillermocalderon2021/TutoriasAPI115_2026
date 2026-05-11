/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.www.tutoria8;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.text.DefaultEditorKit;

/**
 * Editor de Texto Básico — Demostración de JMenuBar, JToolBar y AbstractAction
 *
 * Conceptos clave:
 *   1. AbstractAction  - centraliza lógica; se reutiliza en menú Y barra de herramientas
 *   2. JMenuBar / JMenu / JMenuItem - estructura de menús
 *   3. JToolBar        - acceso rápido con iconos unicode y tooltips
 *   4. Validación      - detectar cambios sin guardar antes de cerrar/nuevo
 */
public class EditorTexto extends JFrame {

    // ── Estado ──────────────────────────────────────────────────────────────
    private final JTextArea areaTexto = new JTextArea();
    private File archivoActual = null;
    private boolean modificado = false;

    // ── Constructor ─────────────────────────────────────────────────────────
    public EditorTexto() {
        super("Editor de Texto");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);   // manejamos el cierre manualmente
        setSize(800, 600);
        setLocationRelativeTo(null);

        configurarAreaTexto();

        // Las acciones se crean UNA sola vez y se comparten entre menú y toolbar
        Action accionNuevo   = crearAccionNuevo();
        Action accionAbrir   = crearAccionAbrir();
        Action accionGuardar = crearAccionGuardar();
        Action accionSalir   = crearAccionSalir();

        setJMenuBar(construirMenuBar(accionNuevo, accionAbrir, accionGuardar, accionSalir));
        add(construirToolBar(accionNuevo, accionAbrir, accionGuardar, accionSalir), BorderLayout.NORTH);
        add(new JScrollPane(areaTexto), BorderLayout.CENTER);
        add(construirBarraEstado(), BorderLayout.SOUTH);

        // Interceptar el botón X de la ventana
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                accionSalir.actionPerformed(null);
            }
        });
    }

    // ── Configuración del área de texto ─────────────────────────────────────
    private void configurarAreaTexto() {
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 14));
        areaTexto.setLineWrap(true);
        areaTexto.setWrapStyleWord(true);
        areaTexto.setMargin(new Insets(8, 8, 8, 8));

        // Marcar el documento como modificado cuando el usuario escribe
        areaTexto.getDocument().addDocumentListener(
            new javax.swing.event.DocumentListener() {
                public void insertUpdate (javax.swing.event.DocumentEvent e) { marcarModificado(); }
                public void removeUpdate (javax.swing.event.DocumentEvent e) { marcarModificado(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { marcarModificado(); }
            }
        );
    }

    // ────────────────────────────────────────────────────────────────────────
    //  ACCIONES  (AbstractAction)
    //  Cada acción encapsula: nombre, tooltip, atajo de teclado Y la lógica.
    //  Se asigna UNA VEZ y se reutiliza tanto en el menú como en la toolbar.
    // ────────────────────────────────────────────────────────────────────────

    private Action crearAccionNuevo() {
        Action a = new AbstractAction("Nuevo") {
            @Override public void actionPerformed(ActionEvent e) {
                if (confirmarDescarte()) {
                    areaTexto.setText("");
                    archivoActual = null;
                    modificado = false;
                    actualizarTitulo();
                }
            }
        };
        a.putValue(Action.SHORT_DESCRIPTION, "Nuevo documento (Ctrl+N)");
        a.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        
        return a;
    }

    private Action crearAccionAbrir() {
        Action a = new AbstractAction("Abrir…") {
            @Override public void actionPerformed(ActionEvent e) {
                if (!confirmarDescarte()) return;

                JFileChooser fc = new JFileChooser();
                fc.setFileFilter(new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt"));
                if (fc.showOpenDialog(EditorTexto.this) != JFileChooser.APPROVE_OPTION) return;

                try (BufferedReader br = new BufferedReader(new FileReader(fc.getSelectedFile()))) {
                    areaTexto.read(br, null);
                    archivoActual = fc.getSelectedFile();
                    modificado = false;
                    actualizarTitulo();
                } catch (IOException ex) {
                    mostrarError("No se pudo abrir el archivo:\n" + ex.getMessage());
                }
            }
        };
        a.putValue(Action.SHORT_DESCRIPTION, "Abrir archivo (Ctrl+O)");
        a.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        
        return a;
    }

    private Action crearAccionGuardar() {
        Action a = new AbstractAction("Guardar") {
            @Override public void actionPerformed(ActionEvent e) {
                guardar();
            }
        };
        a.putValue(Action.SHORT_DESCRIPTION, "Guardar archivo (Ctrl+S)");
        a.putValue(Action.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        
        return a;
    }

    private Action crearAccionSalir() {
        Action a = new AbstractAction("Salir") {
            @Override public void actionPerformed(ActionEvent e) {
                if (confirmarDescarte()) {
                    dispose();
                    System.exit(0);
                }
            }
        };
        a.putValue(Action.SHORT_DESCRIPTION, "Cerrar la aplicación");
        
        return a;
    }

    // ────────────────────────────────────────────────────────────────────────
    //  MENÚ BAR
    // ────────────────────────────────────────────────────────────────────────

    private JMenuBar construirMenuBar(Action nuevo, Action abrir,
                                     Action guardar, Action salir) {
        JMenuBar barra = new JMenuBar();

        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");
        menuArchivo.setMnemonic(KeyEvent.VK_A);
        menuArchivo.add(new JMenuItem(nuevo));
        menuArchivo.add(new JMenuItem(abrir));
        menuArchivo.add(new JMenuItem(guardar));
        menuArchivo.addSeparator();
        menuArchivo.add(new JMenuItem(salir));

        // Menú Editar (sin AbstractAction propio — usa las acciones integradas de JTextArea)
        JMenu menuEditar = new JMenu("Editar");
        menuEditar.setMnemonic(KeyEvent.VK_E);
        menuEditar.add(new JMenuItem(new DefaultEditorKit.CutAction()  {{ putValue(NAME, "Cortar");  putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK)); }}));
        menuEditar.add(new JMenuItem(new DefaultEditorKit.CopyAction() {{ putValue(NAME, "Copiar");  putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK)); }}));
        menuEditar.add(new JMenuItem(new DefaultEditorKit.PasteAction(){{ putValue(NAME, "Pegar");   putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK)); }}));

        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");
        menuAyuda.setMnemonic(KeyEvent.VK_Y);
        JMenuItem acercaDe = new JMenuItem("Acerca de…");
        acercaDe.addActionListener(e ->
            JOptionPane.showMessageDialog(this,
                "Editor de Texto — Demo Swing\nJMenuBar + JToolBar + AbstractAction \n  Guillermo Calderón - Desarrollo de Aplicaciones de Escritorio",
                "Acerca de", JOptionPane.INFORMATION_MESSAGE));
        menuAyuda.add(acercaDe);

        barra.add(menuArchivo);
        barra.add(menuEditar);
        barra.add(menuAyuda);
        return barra;
    }

    // ────────────────────────────────────────────────────────────────────────
    //  TOOLBAR
    //  Mismas acciones → mismo comportamiento, distinta presentación visual
    // ────────────────────────────────────────────────────────────────────────

    private JToolBar construirToolBar(Action nuevo, Action abrir,
                                     Action guardar, Action salir) {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);  // evita que el usuario la arrastre fuera de lugar

        tb.add(botonToolBar("📄", nuevo));
        tb.add(botonToolBar("📂", abrir));
        tb.add(botonToolBar("💾", guardar));
        tb.addSeparator();
        tb.add(botonToolBar("🚪", salir));

        return tb;
    }

    /** Crea un JButton para la toolbar con icono emoji y tooltip de la acción. */
    private JButton botonToolBar(String icono, Action accion) {
        JButton btn = new JButton(icono);
        btn.addActionListener(accion);
        btn.setToolTipText((String) accion.getValue(Action.SHORT_DESCRIPTION));
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ────────────────────────────────────────────────────────────────────────
    //  BARRA DE ESTADO
    // ────────────────────────────────────────────────────────────────────────

    private JLabel lblEstado;

    private JPanel construirBarraEstado() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        panel.setBorder(BorderFactory.createEtchedBorder());
        lblEstado = new JLabel("Listo");
        panel.add(lblEstado);
        return panel;
    }

    // ────────────────────────────────────────────────────────────────────────
    //  MÉTODOS AUXILIARES
    // ────────────────────────────────────────────────────────────────────────

    /** Guarda en el archivo actual; si no hay uno, abre el diálogo "Guardar como". */
    private boolean guardar() {
        if (archivoActual == null) {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Archivos de texto (*.txt)", "txt"));
            if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return false;

            File f = fc.getSelectedFile();
            if (!f.getName().endsWith(".txt")) f = new File(f.getAbsolutePath() + ".txt");
            archivoActual = f;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoActual))) {
            areaTexto.write(bw);
            modificado = false;
            actualizarTitulo();
            lblEstado.setText("Guardado: " + archivoActual.getName());
            return true;
        } catch (IOException ex) {
            mostrarError("No se pudo guardar:\n" + ex.getMessage());
            return false;
        }
    }

    /**
     * Validación: si hay cambios sin guardar, pregunta al usuario qué desea hacer.
     * Devuelve true si es seguro continuar (descartó o guardó), false si canceló.
     */
    private boolean confirmarDescarte() {
        if (!modificado) return true;

        int opcion = JOptionPane.showConfirmDialog(
            this,
            "El documento tiene cambios sin guardar.\n¿Desea guardarlos antes de continuar?",
            "Cambios sin guardar",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (opcion == JOptionPane.YES_OPTION)    return guardar();
        if (opcion == JOptionPane.NO_OPTION)     return true;
        return false;  // CANCEL_OPTION o cerrar el diálogo
    }

    private void marcarModificado() {
        if (!modificado) {
            modificado = true;
            actualizarTitulo();
        }
    }

    private void actualizarTitulo() {
        String nombre = (archivoActual != null) ? archivoActual.getName() : "Sin título";
        setTitle((modificado ? "* " : "") + nombre + " — Editor de Texto");
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // ── Punto de entrada ────────────────────────────────────────────────────
    public static void main(String[] args) {
        // Usar el look & feel del sistema operativo
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new EditorTexto().setVisible(true));
    }
}