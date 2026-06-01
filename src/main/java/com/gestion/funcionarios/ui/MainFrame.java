package com.gestion.funcionarios.ui;

import com.gestion.funcionarios.config.DatabaseConnection;
import com.gestion.funcionarios.security.SessionContext;
import com.gestion.funcionarios.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Ventana principal de la aplicación de escritorio.
 * Contiene la barra de título institucional y aloja el
 * {@link FuncionariosPanel} como único contenido del módulo.
 */
public class MainFrame extends JFrame {

    private static final Color COLOR_PRIMARIO   = new Color(26, 86, 160);
    private static final Color COLOR_SECUNDARIO = new Color(15, 55, 115);

    public MainFrame() {
        initFrame();
        initUI();
    }

    private void initFrame() {
        setTitle("Sistema de Gestión de Funcionarios");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1200, 700);
        setMinimumSize(new Dimension(900, 550));
        setLocationRelativeTo(null);

        // Cerrar pool de BD al salir
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int resp = JOptionPane.showConfirmDialog(
                    MainFrame.this,
                    "¿Desea cerrar la aplicación?",
                    "Confirmar salida",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                if (resp == JOptionPane.YES_OPTION) {
                    DatabaseConnection.closePool();
                    System.exit(0);
                }
            }
        });
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(new Color(245, 247, 250));

        root.add(crearHeader(),            BorderLayout.NORTH);
        root.add(crearContenidoSegunRol(), BorderLayout.CENTER);

        setContentPane(root);
    }

    private JComponent crearContenidoSegunRol() {
        // Reglas:
        // - ADMINISTRADOR: acceso a Usuarios, Inventarios y Catálogos (CRUD).
        // - DOCENTE: solo lectura de Inventarios.
        SessionContext session = SessionContext.getInstance();
        if (!session.isAdmin()) {
            return new InventariosPanel();
        }

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabs.addTab("Usuarios", new FuncionariosPanel());
        tabs.addTab("Inventarios", new InventariosPanel());
        tabs.addTab("Catálogos", new CatalogosPanel());
        return tabs;
    }

    /** Encabezado institucional con logo textual y nombre del sistema. */
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_SECUNDARIO);
        header.setBorder(new EmptyBorder(0, 0, 0, 0));

        SessionContext session = SessionContext.getInstance();

        // ── Barra superior (fondo oscuro) ──
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(COLOR_PRIMARIO);
        topBar.setBorder(new EmptyBorder(14, 24, 14, 24));

        // Logo + nombre sistema
        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        izquierda.setOpaque(false);

        // Ícono circular simulado
        JLabel icono = new JLabel("🏛") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillOval(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        icono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        icono.setPreferredSize(new Dimension(40, 40));
        icono.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 0));
        textos.setOpaque(false);
        JLabel lblSistema = new JLabel("SISTEMA DE GESTIÓN DE FUNCIONARIOS");
        lblSistema.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblSistema.setForeground(Color.WHITE);
        String modulo = session.isAdmin() ? "Administración de Personal" : "Inventarios (solo lectura)";
        JLabel lblSub = new JLabel("Módulo: " + modulo + "  •  v1.0");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSub.setForeground(new Color(180, 200, 240));
        textos.add(lblSistema);
        textos.add(lblSub);

        izquierda.add(icono);
        izquierda.add(textos);

        // Información derecha
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        derecha.setOpaque(false);
        String usuarioTxt = session.isActive() ? session.getEmail() : "Sin sesión";
        String rolTxt = session.isActive() && session.getRole() != null ? session.getRole().name() : "";

        JLabel lblUsuario = new JLabel(usuarioTxt + (rolTxt.isBlank() ? "" : "  •  " + rolTxt));
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblUsuario.setForeground(new Color(210, 225, 250));

        JLabel lblEntidad = new JLabel("Entidad Pública  •  2025");
        lblEntidad.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblEntidad.setForeground(new Color(180, 200, 240));

        JButton btnSalir = new JButton("Salir");
        btnSalir.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setBackground(new Color(192, 57, 43));
        btnSalir.setFocusPainted(false);
        btnSalir.setBorderPainted(false);
        btnSalir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSalir.setBorder(new EmptyBorder(8, 14, 8, 14));
        btnSalir.addActionListener(e -> onLogout());

        derecha.add(lblUsuario);
        derecha.add(lblEntidad);
        derecha.add(btnSalir);

        topBar.add(izquierda, BorderLayout.WEST);
        topBar.add(derecha,   BorderLayout.EAST);

        // ── Breadcrumb ──
        JPanel breadcrumb = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 5));
        breadcrumb.setBackground(COLOR_SECUNDARIO);
        String crumb = session.isAdmin()
            ? "  Inicio  ›  Personal  ›  Funcionarios"
            : "  Inicio  ›  Inventarios";
        JLabel bc = new JLabel(crumb);
        bc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        bc.setForeground(new Color(160, 190, 230));
        breadcrumb.add(bc);

        header.add(topBar,      BorderLayout.NORTH);
        header.add(breadcrumb,  BorderLayout.SOUTH);
        return header;
    }

    private void onLogout() {
        int resp = JOptionPane.showConfirmDialog(
            this,
            "¿Desea cerrar sesión y cambiar de usuario?",
            "Cerrar sesión",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (resp != JOptionPane.YES_OPTION) return;

        new AuthService().logout();
        dispose();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
