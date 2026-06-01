package com.gestion.funcionarios.ui;

import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.exception.UnauthorizedException;
import com.gestion.funcionarios.model.*;
import com.gestion.funcionarios.security.SessionContext;
import com.gestion.funcionarios.service.CatalogoService;
import com.gestion.funcionarios.service.InventarioService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel de inventarios con CRUD para administradores y solo lectura para docentes.
 */
public class InventariosPanel extends JPanel {

    private static final Color  C_PRIMARIO  = new Color(26, 86, 160);
    private static final Color  C_EXITO     = new Color(39, 174, 96);
    private static final Color  C_PELIGRO   = new Color(192, 57, 43);
    private static final Color  C_EDITAR    = new Color(243, 156, 18);
    private static final Color  C_FILA_PAR  = new Color(236, 242, 252);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JTable            tabla;
    private DefaultTableModel modeloTabla;
    private JTextField        txtBuscar;
    private JLabel            lblEstado;
    private JButton           btnNuevo, btnEditar, btnEliminar;

    private final InventarioService inventarioService = new InventarioService();
    private final CatalogoService   cataloService     = new CatalogoService();
    private final boolean           esAdmin           = SessionContext.getInstance().isAdmin();

    public InventariosPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        construirUI();
        cargarDatos();
    }

    private void construirUI() {
        add(crearBarra(), BorderLayout.NORTH);
        add(crearTabla(), BorderLayout.CENTER);
        add(crearPieDePagina(), BorderLayout.SOUTH);
    }

    private JPanel crearBarra() {
        JPanel barra = new JPanel(new BorderLayout(10, 0));
        barra.setBackground(Color.WHITE);
        barra.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            new EmptyBorder(12, 16, 12, 16)));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        izquierda.setOpaque(false);
        txtBuscar = new JTextField(22);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscar.setToolTipText("Buscar por código o nombre");
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 8, 5, 8)));
        JButton btnBuscar = boton("Buscar", C_PRIMARIO, "🔍");
        izquierda.add(txtBuscar);
        izquierda.add(btnBuscar);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        derecha.setOpaque(false);

        if (esAdmin) {
            btnNuevo    = boton("Nuevo",    C_EXITO,   "➕");
            btnEditar   = boton("Editar",   C_EDITAR,  "✏️");
            btnEliminar = boton("Eliminar", C_PELIGRO, "🗑");
            derecha.add(btnNuevo);
            derecha.add(btnEditar);
            derecha.add(btnEliminar);
        } else {
            JLabel lblSoloLectura = new JLabel("👁  Modo solo lectura");
            lblSoloLectura.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            lblSoloLectura.setForeground(new Color(120, 120, 120));
            derecha.add(lblSoloLectura);
        }

        JButton btnRefrescar = boton("Actualizar", C_PRIMARIO, "🔄");
        derecha.add(btnRefrescar);

        barra.add(izquierda, BorderLayout.WEST);
        barra.add(derecha,   BorderLayout.EAST);

        btnBuscar.addActionListener(e -> buscar());
        txtBuscar.addActionListener(e -> buscar());
        btnRefrescar.addActionListener(e -> cargarDatos());
        if (esAdmin) {
            btnNuevo.addActionListener(e    -> abrirFormNuevo());
            btnEditar.addActionListener(e   -> abrirFormEditar());
            btnEliminar.addActionListener(e -> eliminar());
        }
        return barra;
    }

    private JScrollPane crearTabla() {
        String[] cols = {"ID","Código","Nombre","Tipo","Marca","Estado","Asignado a","Fecha Reg."};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return c == 0 ? Integer.class : String.class; }
        };

        tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(32);
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setShowVerticalLines(false);
        tabla.setSelectionBackground(C_PRIMARIO);
        tabla.setSelectionForeground(Color.WHITE);

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBackground(!sel ? (row % 2 == 0 ? Color.WHITE : C_FILA_PAR) : C_PRIMARIO);
                setForeground(sel ? Color.WHITE : Color.DARK_GRAY);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        });

        JTableHeader header = tabla.getTableHeader();
        header.setPreferredSize(new Dimension(0, 38));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setBackground(C_PRIMARIO);
                setForeground(Color.WHITE);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(40, 100, 180)));
                return this;
            }
        });

        int[] anchos = {45, 90, 200, 140, 120, 110, 160, 95};
        for (int i = 0; i < anchos.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        tabla.getColumnModel().getColumn(0).setMaxWidth(50);

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
        return scroll;
    }

    private JPanel crearPieDePagina() {
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 6));
        pie.setBackground(new Color(238, 241, 246));
        pie.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(215, 215, 215)));
        lblEstado = new JLabel("Cargando...");
        lblEstado.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblEstado.setForeground(new Color(100, 100, 100));
        pie.add(lblEstado);
        return pie;
    }

    public void cargarDatos() {
        try {
            poblar(inventarioService.findAll());
        } catch (DAOException e) {
            mostrarError("Error al cargar inventarios", e.getMessage());
        }
    }

    private void buscar() {
        try {
            String texto = txtBuscar.getText().trim();
            poblar(texto.isEmpty() ? inventarioService.findAll() : inventarioService.findByTexto(texto));
        } catch (DAOException e) {
            mostrarError("Error en búsqueda", e.getMessage());
        }
    }

    private void abrirFormNuevo() {
        InventarioFormDialog dlg = new InventarioFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), null, cataloService);
        dlg.setVisible(true);
        if (dlg.isConfirmado()) {
            try {
                inventarioService.create(dlg.getInventario());
                cargarDatos();
                JOptionPane.showMessageDialog(this, "Inventario creado exitosamente.");
            } catch (DAOException | UnauthorizedException e) {
                mostrarError("Error al crear inventario", e.getMessage());
            }
        }
    }

    private void abrirFormEditar() {
        Inventario inv = obtenerSeleccionado();
        if (inv == null) return;
        InventarioFormDialog dlg = new InventarioFormDialog(
            (Frame) SwingUtilities.getWindowAncestor(this), inv, cataloService);
        dlg.setVisible(true);
        if (dlg.isConfirmado()) {
            try {
                inventarioService.update(dlg.getInventario());
                cargarDatos();
                JOptionPane.showMessageDialog(this, "Inventario actualizado exitosamente.");
            } catch (DAOException | UnauthorizedException e) {
                mostrarError("Error al actualizar inventario", e.getMessage());
            }
        }
    }

    private void eliminar() {
        Inventario inv = obtenerSeleccionado();
        if (inv == null) return;
        int resp = JOptionPane.showConfirmDialog(this,
            "<html>¿Eliminar el equipo <b>" + inv.getNombre() + "</b>?</html>",
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (resp == JOptionPane.YES_OPTION) {
            try {
                inventarioService.deleteById(inv.getId());
                cargarDatos();
            } catch (DAOException | UnauthorizedException e) {
                mostrarError("Error al eliminar", e.getMessage());
            }
        }
    }

    private void poblar(List<Inventario> lista) {
        modeloTabla.setRowCount(0);
        for (Inventario inv : lista) {
            String asignado = inv.getFuncionarioAsignado() != null
                ? inv.getFuncionarioAsignado().getNombreCompleto() : "—";
            modeloTabla.addRow(new Object[]{
                inv.getId(), inv.getCodigo(), inv.getNombre(),
                inv.getTipo().getNombre(), inv.getMarca().getNombre(),
                inv.getEstado().getNombre(), asignado,
                inv.getFechaRegistro().format(FMT)
            });
        }
        lblEstado.setText("Total registros: " + lista.size());
    }

    private Inventario obtenerSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return null;
        int id = (int) modeloTabla.getValueAt(fila, 0);
        try {
            return inventarioService.findById(id).orElse(null);
        } catch (DAOException e) {
            mostrarError("Error al obtener inventario", e.getMessage());
            return null;
        }
    }

    private JButton boton(String txt, Color fondo, String icono) {
        JButton btn = new JButton(icono + "  " + txt);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(fondo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 14, 7, 14));
        return btn;
    }

    private void mostrarError(String titulo, String msg) {
        JOptionPane.showMessageDialog(this, msg, titulo, JOptionPane.ERROR_MESSAGE);
    }
}
