package com.gestion.funcionarios.ui;

import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.exception.UnauthorizedException;
import com.gestion.funcionarios.model.*;
import com.gestion.funcionarios.service.CatalogoService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

/**
 * Panel de catálogos: EstadosEquipo, Marcas y TiposEquipo.
 * Solo accesible para ADMINISTRADOR.
 * Cada catálogo se muestra en una sub-pestaña con su propia tabla CRUD.
 */
public class CatalogosPanel extends JPanel {

    private static final Color C_PRIMARIO = new Color(26, 86, 160);
    private static final Color C_EXITO    = new Color(39, 174, 96);
    private static final Color C_PELIGRO  = new Color(192, 57, 43);
    private static final Color C_EDITAR   = new Color(243, 156, 18);

    private final CatalogoService service = new CatalogoService();

    public CatalogosPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabs.addTab("Estados equipo", buildCrudTab(
            "Estado",
            () -> { try { return service.findAllEstados(); } catch (Exception e) { error(e.getMessage()); return List.of(); } },
            this::crearEstado
        ));
        tabs.addTab("Marcas", buildCrudTab(
            "Marca",
            () -> { try { return service.findAllMarcas(); } catch (Exception e) { error(e.getMessage()); return List.of(); } },
            this::crearMarca
        ));
        tabs.addTab("Tipos de equipo", buildCrudTab(
            "Tipo",
            () -> { try { return service.findAllTipos(); } catch (Exception e) { error(e.getMessage()); return List.of(); } },
            this::crearTipo
        ));
        add(tabs, BorderLayout.CENTER);
    }

    // ── Builder genérico de sub-panel CRUD ──────────────────────────────────
    private <T extends Object> JPanel buildCrudTab(
            String entidad,
            Supplier<List<T>> loader,
            Runnable onNuevo) {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Tabla
        String[] cols = {"ID", "Nombre", "Descripción", "Activo"};
        DefaultTableModel modelo = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setRowHeight(30);
        tabla.setSelectionBackground(C_PRIMARIO);
        tabla.setSelectionForeground(Color.WHITE);
        JTableHeader header = tabla.getTableHeader();
        header.setPreferredSize(new Dimension(0, 32));
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
        tabla.getColumnModel().getColumn(0).setMaxWidth(50);
        tabla.getColumnModel().getColumn(3).setMaxWidth(60);

        // Barra botones
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        barra.setBackground(new Color(245, 247, 250));
        JButton btnNuevo    = boton("Nuevo",    C_EXITO,   "➕");
        JButton btnEditar   = boton("Editar",   C_EDITAR,  "✏️");
        JButton btnEliminar = boton("Eliminar", C_PELIGRO, "🗑");
        JButton btnRefresh  = boton("Actualizar", C_PRIMARIO, "🔄");
        barra.add(btnNuevo); barra.add(btnEditar);
        barra.add(btnEliminar); barra.add(btnRefresh);

        // Cargar datos helper
        Runnable cargar = () -> {
            modelo.setRowCount(0);
            for (Object item : loader.get()) {
                if (item instanceof EstadoEquipo e) modelo.addRow(new Object[]{e.getId(), e.getNombre(), e.getDescripcion(), e.isActivo()});
                else if (item instanceof Marca m)   modelo.addRow(new Object[]{m.getId(), m.getNombre(), m.getDescripcion(), m.isActivo()});
                else if (item instanceof TipoEquipo t) modelo.addRow(new Object[]{t.getId(), t.getNombre(), t.getDescripcion(), t.isActivo()});
            }
        };

        btnNuevo.addActionListener(e    -> { onNuevo.run(); cargar.run(); });
        btnRefresh.addActionListener(e  -> cargar.run());
        btnEditar.addActionListener(e   -> {
            int row = tabla.getSelectedRow();
            if (row < 0) return;
            try {
                int id = (int) modelo.getValueAt(row, 0);
                String nombreActual = String.valueOf(modelo.getValueAt(row, 1));
                String descActual = String.valueOf(modelo.getValueAt(row, 2));
                boolean activoActual = Boolean.TRUE.equals(modelo.getValueAt(row, 3));

                JTextField txtNombre = new JTextField(nombreActual);
                JTextField txtDesc = new JTextField("null".equals(descActual) ? "" : descActual);
                JCheckBox chkActivo = new JCheckBox("Activo", activoActual);
                Object[] msg = {"Nombre:", txtNombre, "Descripción:", txtDesc, chkActivo};
                int resp = JOptionPane.showConfirmDialog(this, msg,
                    "Editar " + entidad, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (resp != JOptionPane.OK_OPTION) return;

                String nombre = txtNombre.getText().trim();
                String desc = txtDesc.getText().trim();
                boolean activo = chkActivo.isSelected();
                if (nombre.isBlank()) return;

                if ("Estado".equals(entidad)) {
                    service.updateEstado(new EstadoEquipo(id, nombre, desc.isBlank() ? null : desc, activo));
                } else if ("Marca".equals(entidad)) {
                    service.updateMarca(new Marca(id, nombre, desc.isBlank() ? null : desc, activo));
                } else if ("Tipo".equals(entidad)) {
                    service.updateTipo(new TipoEquipo(id, nombre, desc.isBlank() ? null : desc, activo));
                }
                cargar.run();
            } catch (DAOException | UnauthorizedException ex) {
                error(ex.getMessage());
            } catch (RuntimeException ex) {
                error(ex.getMessage());
            }
        });
        btnEliminar.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row < 0) return;
            try {
                int id = (int) modelo.getValueAt(row, 0);
                String nombreActual = String.valueOf(modelo.getValueAt(row, 1));
                int resp = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar " + entidad + " \"" + nombreActual + "\"?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if (resp != JOptionPane.YES_OPTION) return;

                if ("Estado".equals(entidad)) {
                    service.deleteEstadoById(id);
                } else if ("Marca".equals(entidad)) {
                    service.deleteMarcaById(id);
                } else if ("Tipo".equals(entidad)) {
                    service.deleteTipoById(id);
                }
                cargar.run();
            } catch (DAOException | UnauthorizedException ex) {
                error(ex.getMessage());
            } catch (RuntimeException ex) {
                error(ex.getMessage());
            }
        });

        tabla.getSelectionModel().addListSelectionListener(ev -> {
            boolean sel = tabla.getSelectedRow() >= 0;
            btnEditar.setEnabled(sel); btnEliminar.setEnabled(sel);
        });
        btnEditar.setEnabled(false); btnEliminar.setEnabled(false);

        panel.add(barra, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        cargar.run();
        return panel;
    }

    // ── Acciones Estados ────────────────────────────────────────────────────
    private void crearEstado() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre del estado:");
        if (nombre == null || nombre.isBlank()) return;
        String desc = JOptionPane.showInputDialog(this, "Descripción:");
        try { service.createEstado(new EstadoEquipo(0, nombre.trim(), desc, true)); }
        catch (DAOException | UnauthorizedException e) { error(e.getMessage()); }
    }

    // ── Acciones Marcas ─────────────────────────────────────────────────────
    private void crearMarca() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre de la marca:");
        if (nombre == null || nombre.isBlank()) return;
        String desc = JOptionPane.showInputDialog(this, "Descripción:");
        try { service.createMarca(new Marca(0, nombre.trim(), desc, true)); }
        catch (DAOException | UnauthorizedException e) { error(e.getMessage()); }
    }

    // ── Acciones Tipos ──────────────────────────────────────────────────────
    private void crearTipo() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre del tipo:");
        if (nombre == null || nombre.isBlank()) return;
        String desc = JOptionPane.showInputDialog(this, "Descripción:");
        try { service.createTipo(new TipoEquipo(0, nombre.trim(), desc, true)); }
        catch (DAOException | UnauthorizedException e) { error(e.getMessage()); }
    }

    private JButton boton(String txt, Color bg, String icon) {
        JButton b = new JButton(icon + "  " + txt);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12)); b.setBackground(bg);
        b.setForeground(Color.WHITE); b.setFocusPainted(false); b.setBorderPainted(false);
        b.setBorder(new EmptyBorder(6, 12, 6, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
