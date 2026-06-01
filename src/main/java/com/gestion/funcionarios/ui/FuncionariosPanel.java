package com.gestion.funcionarios.ui;

import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.exception.UnauthorizedException;
import com.gestion.funcionarios.exception.ValidationException;
import com.gestion.funcionarios.model.Funcionario;
import com.gestion.funcionarios.security.SessionContext;
import com.gestion.funcionarios.service.FuncionarioService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel principal que contiene:
 *  - Barra de herramientas (buscar, nuevo, editar, eliminar, refrescar)
 *  - Tabla con la lista de funcionarios
 *  - Barra de estado (total de registros)
 */
public class FuncionariosPanel extends JPanel {

    // ── Paleta de colores ───────────────────────────────────────────────────
    private static final Color COLOR_PRIMARIO  = new Color(26, 86, 160);
    private static final Color COLOR_PELIGRO   = new Color(192, 57, 43);
    private static final Color COLOR_EXITO     = new Color(39, 174, 96);
    private static final Color COLOR_EDITAR    = new Color(243, 156, 18);
    private static final Color COLOR_FONDO     = new Color(245, 247, 250);
    private static final Color COLOR_FILA_PAR  = new Color(236, 242, 252);
    private static final Color COLOR_SELECCION = new Color(26, 86, 160);

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Componentes ─────────────────────────────────────────────────────────
    private JTable          tabla;
    private DefaultTableModel modeloTabla;
    private JTextField      txtBuscar;
    private JLabel          lblEstado;
    private JButton         btnEditar, btnEliminar;

    // ── Servicio ────────────────────────────────────────────────────────────
    private final FuncionarioService funcionarioService = new FuncionarioService();
    private final boolean           esAdmin             = SessionContext.getInstance().isAdmin();

    // ── Constructor ─────────────────────────────────────────────────────────
    public FuncionariosPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(COLOR_FONDO);
        construirUI();
        cargarDatos();
    }

    // ── Construcción de la interfaz ─────────────────────────────────────────
    private void construirUI() {
        add(crearBarraHerramientas(), BorderLayout.NORTH);
        add(crearTabla(),            BorderLayout.CENTER);
        add(crearBarraEstado(),      BorderLayout.SOUTH);
    }

    /** Barra superior con búsqueda y botones de acción. */
    private JPanel crearBarraHerramientas() {
        JPanel barra = new JPanel(new BorderLayout(10, 0));
        barra.setBackground(Color.WHITE);
        barra.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            new EmptyBorder(12, 16, 12, 16)));

        // ── Búsqueda ──
        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelBuscar.setOpaque(false);
        JLabel lblBuscar = new JLabel("🔍");
        lblBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscar = new JTextField(22);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscar.setToolTipText("Buscar por nombre, apellido o documento");
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 8, 5, 8)));
        JButton btnBuscar = crearBoton("Buscar", COLOR_PRIMARIO, "🔍");
        panelBuscar.add(lblBuscar);
        panelBuscar.add(txtBuscar);
        panelBuscar.add(btnBuscar);

        // ── Botones CRUD ──
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        panelBotones.setOpaque(false);
        JButton btnNuevo = null;
        JButton btnRefrescar = crearBoton("Actualizar", COLOR_PRIMARIO, "🔄");

        if (esAdmin) {
            btnNuevo    = crearBoton("Nuevo",    COLOR_EXITO,   "➕");
            btnEditar   = crearBoton("Editar",   COLOR_EDITAR,  "✏️");
            btnEliminar = crearBoton("Eliminar", COLOR_PELIGRO, "🗑");
            panelBotones.add(btnNuevo);
            panelBotones.add(btnEditar);
            panelBotones.add(btnEliminar);
        } else {
            JLabel lblSoloLectura = new JLabel("👁  Modo solo lectura");
            lblSoloLectura.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblSoloLectura.setForeground(new Color(120, 120, 120));
            panelBotones.add(lblSoloLectura);
        }

        panelBotones.add(new JSeparator(SwingConstants.VERTICAL));
        panelBotones.add(btnRefrescar);

        barra.add(panelBuscar,  BorderLayout.WEST);
        barra.add(panelBotones, BorderLayout.EAST);

        // ── Eventos ──
        btnBuscar.addActionListener(e -> buscar());
        txtBuscar.addActionListener(e -> buscar());
        if (esAdmin && btnNuevo != null) {
            btnNuevo.addActionListener(e -> abrirFormNuevo());
        }
        if (esAdmin && btnEditar != null) {
            btnEditar.addActionListener(e -> abrirFormEditar());
        }
        if (esAdmin && btnEliminar != null) {
            btnEliminar.addActionListener(e -> eliminarSeleccionado());
        }
        btnRefrescar.addActionListener(e -> cargarDatos());

        return barra;
    }

    /** Tabla de funcionarios con scroll. */
    private JScrollPane crearTabla() {
        String[] columnas = {
            "ID", "Nombres", "Apellidos", "Tipo Doc",
            "Número Doc", "Cargo", "Área", "Municipio",
            "Fecha Ingreso", "Estado"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
            @Override public Class<?> getColumnClass(int col) {
                return col == 0 ? Integer.class : String.class;
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(32);
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setShowVerticalLines(false);
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setSelectionBackground(COLOR_SELECCION);
        tabla.setSelectionForeground(Color.WHITE);

        // Filas alternadas
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_FILA_PAR);
                }
                setBorder(new EmptyBorder(0, 8, 0, 8));
                // Chip de estado
                if (col == 9 && val != null) {
                    setText("ACTIVO".equals(val) ? "● ACTIVO" : "○ INACTIVO");
                    setForeground(sel ? Color.WHITE :
                        ("ACTIVO".equals(val) ? COLOR_EXITO : COLOR_PELIGRO));
                } else {
                    setForeground(sel ? Color.WHITE : Color.DARK_GRAY);
                }
                return c;
            }
        });

        // Encabezado
        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(COLOR_PRIMARIO);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 38));
        ((DefaultTableCellRenderer) header.getDefaultRenderer())
            .setHorizontalAlignment(SwingConstants.LEFT);

        // Anchos de columna
        int[] anchos = {45, 140, 140, 60, 110, 160, 140, 130, 100, 90};
        for (int i = 0; i < anchos.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
        tabla.getColumnModel().getColumn(0).setMaxWidth(50);

        // Habilitar/deshabilitar botones según selección (solo admin)
        if (esAdmin) {
            tabla.getSelectionModel().addListSelectionListener(e -> {
                boolean sel = tabla.getSelectedRow() >= 0;
                if (btnEditar != null)   btnEditar.setEnabled(sel);
                if (btnEliminar != null) btnEliminar.setEnabled(sel);
            });
            if (btnEditar != null)   btnEditar.setEnabled(false);
            if (btnEliminar != null) btnEliminar.setEnabled(false);
        }

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    /** Barra inferior con conteo de registros. */
    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 6));
        barra.setBackground(new Color(238, 241, 246));
        barra.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(215, 215, 215)));
        lblEstado = new JLabel("Cargando...");
        lblEstado.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblEstado.setForeground(new Color(100, 100, 100));
        barra.add(lblEstado);
        return barra;
    }

    // ── Operaciones CRUD ────────────────────────────────────────────────────

    /** Carga (o recarga) todos los funcionarios en la tabla. */
    public void cargarDatos() {
        try {
            List<Funcionario> lista = funcionarioService.findAll();
            poblarTabla(lista);
        } catch (DAOException | UnauthorizedException e) {
            mostrarError("Error al cargar funcionarios", e);
        }
    }

    /** Filtra funcionarios por texto de búsqueda. */
    private void buscar() {
        String texto = txtBuscar.getText().trim();
        try {
            List<Funcionario> lista = texto.isEmpty()
                ? funcionarioService.findAll()
                : funcionarioService.findByTexto(texto);
            poblarTabla(lista);
        } catch (DAOException | UnauthorizedException e) {
            mostrarError("Error en la búsqueda", e);
        }
    }

    /** Abre el formulario para crear un nuevo funcionario. */
    private void abrirFormNuevo() {
        if (!esAdmin) {
            JOptionPane.showMessageDialog(this,
                "No tiene permisos para crear funcionarios.",
                "Acceso restringido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        FuncionarioFormDialog dlg = new FuncionarioFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), null);
        dlg.setVisible(true);
        if (dlg.isConfirmado()) {
            try {
                String password = solicitarPasswordInicial();
                if (password == null) return; // cancelado
                funcionarioService.create(dlg.getFuncionario(), password);
                cargarDatos();
                mostrarExito("Funcionario creado exitosamente.");
            } catch (ValidationException e) {
                JOptionPane.showMessageDialog(this,
                    "⚠  Campo: " + e.getField() + "\n" + e.getMessage(),
                    "Error de validación", JOptionPane.WARNING_MESSAGE);
            } catch (DAOException | UnauthorizedException e) {
                mostrarError("Error al crear funcionario", e);
            } catch (RuntimeException e) {
                mostrarError("Error al crear funcionario", e);
            }
        }
    }

    /** Abre el formulario para editar el funcionario seleccionado. */
    private void abrirFormEditar() {
        if (!esAdmin) {
            JOptionPane.showMessageDialog(this,
                "No tiene permisos para editar funcionarios.",
                "Acceso restringido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Funcionario f = obtenerSeleccionado();
        if (f == null) return;
        FuncionarioFormDialog dlg = new FuncionarioFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), f);
        dlg.setVisible(true);
        if (dlg.isConfirmado()) {
            try {
                funcionarioService.update(dlg.getFuncionario(), null);
                cargarDatos();
                mostrarExito("Funcionario actualizado exitosamente.");
            } catch (ValidationException e) {
                JOptionPane.showMessageDialog(this,
                    "⚠  Campo: " + e.getField() + "\n" + e.getMessage(),
                    "Error de validación", JOptionPane.WARNING_MESSAGE);
            } catch (DAOException | UnauthorizedException e) {
                mostrarError("Error al actualizar funcionario", e);
            } catch (RuntimeException e) {
                mostrarError("Error al actualizar funcionario", e);
            }
        }
    }

    /** Elimina el funcionario seleccionado previa confirmación. */
    private void eliminarSeleccionado() {
        if (!esAdmin) {
            JOptionPane.showMessageDialog(this,
                "No tiene permisos para eliminar funcionarios.",
                "Acceso restringido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Funcionario f = obtenerSeleccionado();
        if (f == null) return;

        int respuesta = JOptionPane.showConfirmDialog(this,
            "<html>¿Desea eliminar al funcionario:<br><b>" +
            f.getNombreCompleto() + "</b>?<br>" +
            "<small>Esta acción no se puede deshacer.</small></html>",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (respuesta == JOptionPane.YES_OPTION) {
            try {
                funcionarioService.deleteById(f.getId());
                cargarDatos();
                mostrarExito("Funcionario eliminado exitosamente.");
            } catch (DAOException | UnauthorizedException e) {
                mostrarError("Error al eliminar funcionario", e);
            } catch (RuntimeException e) {
                mostrarError("Error al eliminar funcionario", e);
            }
        }
    }

    // ── Auxiliares ─────────────────────────────────────────────────────────

    /** Puebla la tabla con la lista recibida. */
    private void poblarTabla(List<Funcionario> lista) {
        modeloTabla.setRowCount(0);
        for (Funcionario f : lista) {
            modeloTabla.addRow(new Object[]{
                f.getId(),
                f.getNombres(),
                f.getApellidos(),
                f.getTipoDocumento().getCodigo(),
                f.getNumeroDocumento(),
                f.getCargo().getNombre(),
                f.getCargo().getArea().getNombre(),
                f.getMunicipio().toString(),
                f.getFechaIngreso().format(FMT),
                f.getEstado()
            });
        }
        lblEstado.setText("Total registros: " + lista.size());
    }

    /** Retorna el {@link Funcionario} de la fila seleccionada, o {@code null}. */
    private Funcionario obtenerSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return null;
        int id = (int) modeloTabla.getValueAt(fila, 0);
        try {
            return funcionarioService.findById(id).orElse(null);
        } catch (DAOException | UnauthorizedException e) {
            mostrarError("Error al obtener funcionario", e);
            return null;
        }
    }

    private JButton crearBoton(String texto, Color fondo, String icono) {
        JButton btn = new JButton(icono + "  " + texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(fondo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));
        return btn;
    }

    private void mostrarError(String titulo, Exception e) {
        // Mostrar error con detalle (stacktrace) para diagnóstico rápido
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String detalle = sw.toString();

        JTextArea area = new JTextArea(detalle);
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        area.setRows(18);
        area.setColumns(80);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(900, 360));

        JOptionPane.showMessageDialog(
            this,
            scroll,
            titulo + " — " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()),
            JOptionPane.ERROR_MESSAGE
        );
    }

    private void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this,
            mensaje, "Operación exitosa", JOptionPane.INFORMATION_MESSAGE);
    }

    private String solicitarPasswordInicial() {
        JPasswordField p1 = new JPasswordField();
        JPasswordField p2 = new JPasswordField();
        Object[] msg = {
            "Contraseña inicial:", p1,
            "Confirmar contraseña:", p2
        };
        int resp = JOptionPane.showConfirmDialog(
            this,
            msg,
            "Asignar contraseña inicial",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (resp != JOptionPane.OK_OPTION) return null;

        String s1 = new String(p1.getPassword());
        String s2 = new String(p2.getPassword());
        if (!s1.equals(s2)) {
            JOptionPane.showMessageDialog(this,
                "Las contraseñas no coinciden.",
                "Validación", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return s1;
    }
}
