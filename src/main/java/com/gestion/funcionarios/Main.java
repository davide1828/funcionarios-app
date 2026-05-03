package com.gestion.funcionarios;

import com.gestion.funcionarios.ui.MainFrame;

import javax.swing.*;

/**
 * Punto de entrada de la aplicación.
 *
 * Configura el Look & Feel del sistema operativo y lanza
 * la ventana principal en el Event Dispatch Thread (EDT)
 * de Swing para garantizar la seguridad de hilos.
 */
public class Main {

    public static void main(String[] args) {

        // Aplicar Look & Feel nativo del SO (Windows / macOS / Linux)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si falla, Swing usará su L&F por defecto; no es crítico
            System.err.println("No se pudo cargar el Look & Feel nativo: " + e.getMessage());
        }

        // Lanzar la UI en el EDT (buena práctica obligatoria en Swing)
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
