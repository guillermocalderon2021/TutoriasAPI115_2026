/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package sv.edu.ues.www.ejemplo0805;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author pavil
 */
public class Ejemplo0805 {

public static void main(String[] args) {
        // Usar el look & feel del sistema operativo anfitrion
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si falla, Swing usa su propio estilo por defecto
        }
 
        // SwingUtilities.invokeLater garantiza que la interfaz se construya
        // en el hilo de eventos de Swing (Event Dispatch Thread),
        // que es el unico hilo donde es seguro modificar componentes visuales.
        SwingUtilities.invokeLater(() -> new RegistroEstudiantes().setVisible(true));
    }
}
