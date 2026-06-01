package com.gestion.funcionarios.ui;

import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.exception.UnauthorizedException;
import com.gestion.funcionarios.model.*;
import com.gestion.funcionarios.service.CatalogoService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Diálogo modal para crear o editar un item de inventario. */
public class InventarioFormDialog extends JDialog {

    private static final Color  C_PRIMARIO = new Color(26, 86, 160);
    private static final Color  C_EXITO    = new Color(39, 174, 96);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JTextField              txtCodigo, txtNombre, txtDescripcion, txtFecha;
    private JComboBox<EstadoEquipo> cmbEstado;
    private JComboBox<Marca>        cmbMarca;
    private JComboBox<TipoEquipo>   cmbTipo;

    private Inventario inventario;
    private boolean    confirmado = false;

    private final CatalogoService cataloService;

    public InventarioFormDialog(Frame owner, Inventario inventario, CatalogoService cataloService) {
        super(owner, inventario == null ? "➕  Nuevo Equipo" : "✏️  Editar Equipo", true);
        this.inventario    = inventario;
        this.cataloService = cataloService;
        initUI();
        cargarCatalogos();
        if (inventario != null) precargar();
        pack();
        setMinimumSize(new Dimension(520, 460));
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        header.setBackground(C_PRIMARIO);
        JLabel titulo = new JLabel(getTitle());
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titulo.setForeground(Color.WHITE);
        header.add(titulo);
        root.add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 24, 10, 24));
        GridBagConstraints g = new GridBagConstraints();
        g.insets  = new Insets(6, 6, 6, 6);
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.weightx = 1.0;

        addPar(form, g, "Código *",      txtCodigo      = field("EQ-NNN"),       0);
        addPar(form, g, "Nombre *",      txtNombre      = field("Nombre equipo"), 1);
        addPar(form, g, "Descripción",   txtDescripcion = field("Descripción"),   2);
        addPar(form, g, "Fecha registro * (dd/MM/yyyy)", txtFecha = field("dd/MM/yyyy"), 3);

        addLabel(form, g, "Estado *",  0, 4);
        cmbEstado = new JComboBox<>(); cmbEstado.setFont(font());
        addFull(form, g, cmbEstado, 4);

        addLabel(form, g, "Marca *",   0, 5);
        cmbMarca = new JComboBox<>(); cmbMarca.setFont(font());
        addFull(form, g, cmbMarca, 5);

        addLabel(form, g, "Tipo *",    0, 6);
        cmbTipo = new JComboBox<>(); cmbTipo.setFont(font());
        addFull(form, g, cmbTipo, 6);

        root.add(new JScrollPane(form), BorderLayout.CENTER);

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        pie.setBackground(new Color(245, 247, 250));
        pie.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
        JButton btnCancelar = btnStyled("Cancelar", new Color(149, 165, 166));
        JButton btnGuardar  = btnStyled(inventario == null ? "Crear" : "Guardar", C_EXITO);
        pie.add(btnCancelar);
        pie.add(btnGuardar);
        root.add(pie, BorderLayout.SOUTH);

        setContentPane(root);
        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e  -> onGuardar());
    }

    private void onGuardar() {
        try {
            String codigo = txtCodigo.getText().trim();
            String nombre = txtNombre.getText().trim();
            String desc   = txtDescripcion.getText().trim();
            String fecha  = txtFecha.getText().trim();

            if (codigo.isEmpty()) throw new IllegalArgumentException("El código es obligatorio.");
            if (nombre.isEmpty()) throw new IllegalArgumentException("El nombre es obligatorio.");
            if (cmbEstado.getSelectedItem() == null) throw new IllegalArgumentException("Seleccione un estado.");
            if (cmbMarca.getSelectedItem() == null)  throw new IllegalArgumentException("Seleccione una marca.");
            if (cmbTipo.getSelectedItem() == null)   throw new IllegalArgumentException("Seleccione un tipo.");

            LocalDate fechaDate;
            try { fechaDate = LocalDate.parse(fecha, FMT); }
            catch (DateTimeParseException e) { throw new IllegalArgumentException("Fecha inválida (dd/MM/yyyy)."); }

            Inventario inv = inventario != null ? inventario : new Inventario();
            inv.setCodigo(codigo);
            inv.setNombre(nombre);
            inv.setDescripcion(desc.isEmpty() ? null : desc);
            inv.setEstado((EstadoEquipo) cmbEstado.getSelectedItem());
            inv.setMarca((Marca) cmbMarca.getSelectedItem());
            inv.setTipo((TipoEquipo) cmbTipo.getSelectedItem());
            inv.setFechaRegistro(fechaDate);
            inv.setActivo(true);

            this.inventario = inv;
            this.confirmado = true;
            dispose();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "⚠ " + e.getMessage(),
                "Validación", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cargarCatalogos() {
        try {
            cataloService.findEstadosActivos().forEach(cmbEstado::addItem);
            cataloService.findMarcasActivas().forEach(cmbMarca::addItem);
            cataloService.findTiposActivos().forEach(cmbTipo::addItem);
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this, "Error cargando catálogos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (UnauthorizedException e) {
            JOptionPane.showMessageDialog(this,
                "Acceso restringido: " + e.getMessage(),
                "Permisos", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void precargar() {
        txtCodigo.setText(inventario.getCodigo());
        txtNombre.setText(inventario.getNombre());
        txtDescripcion.setText(inventario.getDescripcion() != null ? inventario.getDescripcion() : "");
        txtFecha.setText(inventario.getFechaRegistro().format(FMT));
        selectById(cmbEstado, inventario.getEstado().getId());
        selectById(cmbMarca,  inventario.getMarca().getId());
        selectById(cmbTipo,   inventario.getTipo().getId());
    }

    private <T> void selectById(JComboBox<T> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            int itemId = -1;
            if (item instanceof EstadoEquipo) {
                itemId = ((EstadoEquipo) item).getId();
            } else if (item instanceof Marca) {
                itemId = ((Marca) item).getId();
            } else if (item instanceof TipoEquipo) {
                itemId = ((TipoEquipo) item).getId();
            }
            if (itemId == id) { combo.setSelectedIndex(i); return; }
        }
    }

    // ── Helpers UI ──────────────────────────────────────────────────────────
    private void addPar(JPanel p, GridBagConstraints g, String lbl, JTextField tf, int row) {
        addLabel(p, g, lbl, 0, row);
        g.gridx = 1; g.gridy = row; g.gridwidth = 3; p.add(tf, g);
        g.gridwidth = 1;
    }
    private void addFull(JPanel p, GridBagConstraints g, JComponent c, int row) {
        g.gridx = 1; g.gridy = row; g.gridwidth = 3; p.add(c, g); g.gridwidth = 1;
    }
    private void addLabel(JPanel p, GridBagConstraints g, String t, int x, int y) {
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g.gridx = x; g.gridy = y; g.fill = GridBagConstraints.NONE; p.add(l, g);
        g.fill = GridBagConstraints.HORIZONTAL;
    }
    private JTextField field(String tip) {
        JTextField tf = new JTextField(); tf.setFont(font()); tf.setToolTipText(tip);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200,200,200)), new EmptyBorder(5,8,5,8)));
        return tf;
    }
    private Font font() { return new Font("Segoe UI", Font.PLAIN, 13); }
    private JButton btnStyled(String txt, Color bg) {
        JButton b = new JButton(txt); b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(bg); b.setForeground(Color.WHITE); b.setFocusPainted(false);
        b.setBorderPainted(false); b.setBorder(new EmptyBorder(9, 20, 9, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public boolean    isConfirmado() { return confirmado; }
    public Inventario getInventario() { return inventario; }
}
