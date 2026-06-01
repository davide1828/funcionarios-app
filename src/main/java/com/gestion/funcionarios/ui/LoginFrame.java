package com.gestion.funcionarios.ui;

import com.gestion.funcionarios.exception.AuthException;
import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Ventana de inicio de sesión.
 * Autentica al usuario y, si es exitoso, abre el {@link MainFrame}.
 */
public class LoginFrame extends JFrame {

    private static final Color COLOR_PRIMARIO  = new Color(26, 86, 160);
    private static final Color COLOR_FONDO     = new Color(236, 241, 250);

    private JTextField     txtEmail;
    private JPasswordField txtPassword;
    private JLabel         lblError;
    private JButton        btnIngresar;

    private final AuthService authService = new AuthService();

    public LoginFrame() {
        initFrame();
        initUI();
    }

    private void initFrame() {
        setTitle("Iniciar Sesión – Sistema de Gestión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 480);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_FONDO);

        // ── Header ──
        JPanel header = new JPanel(new GridLayout(3, 1, 0, 4));
        header.setBackground(COLOR_PRIMARIO);
        header.setBorder(new EmptyBorder(30, 30, 20, 30));
        JLabel icono  = new JLabel("🏛", SwingConstants.CENTER);
        icono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 38));
        icono.setForeground(Color.WHITE);
        JLabel titulo = new JLabel("GESTIÓN DE FUNCIONARIOS", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Ingrese sus credenciales para continuar", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(new Color(180, 205, 245));
        header.add(icono);
        header.add(titulo);
        header.add(sub);
        root.add(header, BorderLayout.NORTH);

        // ── Formulario ──
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(30, 35, 30, 35));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets  = new Insets(6, 0, 6, 0);

        gbc.gridy = 0; form.add(label("Correo electrónico"), gbc);
        txtEmail = campo("usuario@entidad.gov.co");
        gbc.gridy = 1; form.add(txtEmail, gbc);

        gbc.gridy = 2; form.add(label("Contraseña"), gbc);
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(campoBorde());
        gbc.gridy = 3; form.add(txtPassword, gbc);

        lblError = new JLabel(" ");
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblError.setForeground(new Color(192, 57, 43));
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 4; form.add(lblError, gbc);

        btnIngresar = new JButton("Ingresar");
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIngresar.setBackground(COLOR_PRIMARIO);
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setBorderPainted(false);
        btnIngresar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnIngresar.setBorder(new EmptyBorder(11, 20, 11, 20));
        gbc.gridy = 5; gbc.insets = new Insets(14, 0, 0, 0);
        form.add(btnIngresar, gbc);

        root.add(form, BorderLayout.CENTER);

        // ── Footer ──
        JLabel footer = new JLabel("v2.0  •  Solo usuarios autorizados", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        footer.setForeground(new Color(150, 150, 150));
        footer.setBorder(new EmptyBorder(0, 0, 12, 0));
        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);

        // ── Eventos ──
        btnIngresar.addActionListener(e -> onLogin());
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) onLogin();
            }
        });
    }

    private void onLogin() {
        String email    = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        lblError.setText(" ");
        btnIngresar.setEnabled(false);
        btnIngresar.setText("Verificando...");

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override protected String doInBackground() throws Exception {
                return authService.login(email, password);
            }
            @Override protected void done() {
                btnIngresar.setEnabled(true);
                btnIngresar.setText("Ingresar");
                try {
                    get(); // lanza excepción si falló
                    abrirMainFrame();
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    lblError.setText("⚠ " + cause.getMessage());
                    txtPassword.setText("");
                }
            }
        };
        worker.execute();
    }

    private void abrirMainFrame() {
        dispose();
        new MainFrame().setVisible(true);
    }

    private JLabel label(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(60, 60, 60));
        return lbl;
    }

    private JTextField campo(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setToolTipText(placeholder);
        tf.setBorder(campoBorde());
        return tf;
    }

    private javax.swing.border.Border campoBorde() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(8, 10, 8, 10));
    }
}
