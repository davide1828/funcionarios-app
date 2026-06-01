package com.gestion.funcionarios.ui;

import com.gestion.funcionarios.dao.CargoDAO;
import com.gestion.funcionarios.dao.MunicipioDAO;
import com.gestion.funcionarios.dao.TipoDocumentoDAO;
import com.gestion.funcionarios.dao.impl.CargoDAOImpl;
import com.gestion.funcionarios.dao.impl.MunicipioDAOImpl;
import com.gestion.funcionarios.dao.impl.TipoDocumentoDAOImpl;
import com.gestion.funcionarios.exception.DAOException;
import com.gestion.funcionarios.exception.ValidationException;
import com.gestion.funcionarios.model.*;
import com.gestion.funcionarios.security.Role;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Diálogo modal para crear o editar un funcionario.
 *
 * Modo creación : se llama con {@code funcionario = null}.
 * Modo edición  : se llama con el objeto a modificar.
 */
public class FuncionarioFormDialog extends JDialog {

    // ── Constantes de estilo ────────────────────────────────────────────────
    private static final Color  COLOR_PRIMARIO  = new Color(26, 86, 160);
    private static final Color  COLOR_EXITO     = new Color(39, 174, 96);
    private static final Color  COLOR_CANCELAR  = new Color(149, 165, 166);
    private static final Font   FUENTE_LABEL    = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font   FUENTE_CAMPO    = new Font("Segoe UI", Font.PLAIN, 13);
    private static final DateTimeFormatter FMT  = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Campos del formulario ───────────────────────────────────────────────
    private JTextField  txtNombres, txtApellidos, txtDocumento;
    private JTextField  txtEmail, txtTelefono;
    private JTextField  txtFechaNacimiento, txtFechaIngreso;
    private JComboBox<TipoDocumento> cmbTipoDoc;
    private JComboBox<Cargo>         cmbCargo;
    private JComboBox<Municipio>     cmbMunicipio;
    private JComboBox<String>        cmbEstado;
    private JComboBox<Role>          cmbRol;

    // ── Estado ──────────────────────────────────────────────────────────────
    private Funcionario funcionario;   // null = modo creación
    private boolean     confirmado = false;

    // ── DAOs de catálogos ───────────────────────────────────────────────────
    private final TipoDocumentoDAO tipoDocDAO  = new TipoDocumentoDAOImpl();
    private final CargoDAO         cargoDAO    = new CargoDAOImpl();
    private final MunicipioDAO     municipioDAO = new MunicipioDAOImpl();

    // ── Constructor ─────────────────────────────────────────────────────────
    public FuncionarioFormDialog(Frame owner, Funcionario funcionario) {
        super(owner,
              funcionario == null ? "➕  Nuevo Funcionario" : "✏️  Editar Funcionario",
              true);
        this.funcionario = funcionario;
        initUI();
        cargarCatalogos();
        if (funcionario != null) precargarDatos();
        pack();
        setMinimumSize(new Dimension(580, 580));
        setLocationRelativeTo(owner);
    }

    // ── Construcción de la interfaz ─────────────────────────────────────────
    private void initUI() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel contenedor = new JPanel(new BorderLayout(0, 0));
        contenedor.setBackground(Color.WHITE);

        // ── Encabezado ──
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        header.setBackground(COLOR_PRIMARIO);
        JLabel lblTitulo = new JLabel(getTitle());
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE);
        header.add(lblTitulo);
        contenedor.add(header, BorderLayout.NORTH);

        // ── Formulario ──
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(20, 25, 10, 25));
        GridBagConstraints gbc = defaultGBC();

        // Fila 0: Nombres / Apellidos
        addLabel(form, gbc, "Nombres *", 0, 0);
        txtNombres = campo("Ej: Juan David");
        addField(form, gbc, txtNombres, 1, 0);
        addLabel(form, gbc, "Apellidos *", 2, 0);
        txtApellidos = campo("Ej: Ospina Ríos");
        addField(form, gbc, txtApellidos, 3, 0);

        // Fila 1: Tipo doc / Número doc
        addLabel(form, gbc, "Tipo documento *", 0, 1);
        cmbTipoDoc = comboBox();
        addField(form, gbc, cmbTipoDoc, 1, 1);
        addLabel(form, gbc, "Número documento *", 2, 1);
        txtDocumento = campo("Ej: 1032109876");
        addField(form, gbc, txtDocumento, 3, 1);

        // Fila 2: Fecha nacimiento / Fecha ingreso
        addLabel(form, gbc, "Fecha nacimiento * (dd/MM/yyyy)", 0, 2);
        txtFechaNacimiento = campo("dd/MM/yyyy");
        addField(form, gbc, txtFechaNacimiento, 1, 2);
        addLabel(form, gbc, "Fecha ingreso * (dd/MM/yyyy)", 2, 2);
        txtFechaIngreso = campo("dd/MM/yyyy");
        addField(form, gbc, txtFechaIngreso, 3, 2);

        // Fila 3: Email / Teléfono
        addLabel(form, gbc, "Correo electrónico *", 0, 3);
        txtEmail = campo("usuario@entidad.gov.co");
        addField(form, gbc, txtEmail, 1, 3);
        addLabel(form, gbc, "Teléfono", 2, 3);
        txtTelefono = campo("Ej: 3001234567");
        addField(form, gbc, txtTelefono, 3, 3);

        // Fila 4: Cargo (ancho completo)
        addLabel(form, gbc, "Cargo *", 0, 4);
        cmbCargo = comboBox();
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(cmbCargo, gbc);
        resetGBC(gbc);

        // Fila 5: Municipio (ancho completo)
        addLabel(form, gbc, "Municipio *", 0, 5);
        cmbMunicipio = comboBox();
        gbc.gridx = 1; gbc.gridy = 5; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        form.add(cmbMunicipio, gbc);
        resetGBC(gbc);

        // Fila 6: Estado
        addLabel(form, gbc, "Estado *", 0, 6);
        cmbEstado = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO"});
        cmbEstado.setFont(FUENTE_CAMPO);
        addField(form, gbc, cmbEstado, 1, 6);

        // Fila 7: Rol
        addLabel(form, gbc, "Rol *", 2, 6);
        cmbRol = new JComboBox<>(Role.values());
        cmbRol.setFont(FUENTE_CAMPO);
        cmbRol.setSelectedItem(Role.DOCENTE); // valor por defecto al crear
        addField(form, gbc, cmbRol, 3, 6);

        contenedor.add(new JScrollPane(form), BorderLayout.CENTER);

        // ── Botones ──
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        panelBotones.setBackground(new Color(245, 247, 250));
        panelBotones.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));

        JButton btnCancelar = boton("Cancelar", COLOR_CANCELAR);
        JButton btnGuardar  = boton(funcionario == null ? "  Crear Funcionario  " : "  Guardar Cambios  ",
                                    funcionario == null ? COLOR_EXITO : COLOR_PRIMARIO);
        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);
        contenedor.add(panelBotones, BorderLayout.SOUTH);

        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> onGuardar());

        setContentPane(contenedor);
    }

    // ── Lógica de guardado ──────────────────────────────────────────────────
    private void onGuardar() {
        try {
            Funcionario f = validarYConstruir();
            this.funcionario = f;
            this.confirmado  = true;
            dispose();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this,
                "⚠  Campo: " + ex.getField() + "\n" + ex.getMessage(),
                "Error de validación", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Valida todos los campos y construye el objeto {@link Funcionario}.
     * Lanza {@link ValidationException} ante el primer error encontrado.
     */
    private Funcionario validarYConstruir() throws ValidationException {
        String nombres  = txtNombres.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String documento = txtDocumento.getText().trim();
        String email    = txtEmail.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String fnacStr  = txtFechaNacimiento.getText().trim();
        String fingStr  = txtFechaIngreso.getText().trim();

        if (nombres.isEmpty())   throw new ValidationException("Nombres", "El campo Nombres es obligatorio.");
        if (apellidos.isEmpty()) throw new ValidationException("Apellidos", "El campo Apellidos es obligatorio.");
        if (documento.isEmpty()) throw new ValidationException("Número documento", "El número de documento es obligatorio.");
        if (email.isEmpty())     throw new ValidationException("Correo", "El correo electrónico es obligatorio.");
        if (!email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$"))
            throw new ValidationException("Correo", "El correo electrónico no tiene un formato válido.");

        LocalDate fechaNac, fechaIng;
        try { fechaNac = LocalDate.parse(fnacStr, FMT); }
        catch (DateTimeParseException e) {
            throw new ValidationException("Fecha nacimiento", "Formato inválido. Use dd/MM/yyyy.");
        }
        try { fechaIng = LocalDate.parse(fingStr, FMT); }
        catch (DateTimeParseException e) {
            throw new ValidationException("Fecha ingreso", "Formato inválido. Use dd/MM/yyyy.");
        }
        if (fechaNac.isAfter(LocalDate.now()))
            throw new ValidationException("Fecha nacimiento", "La fecha de nacimiento no puede ser futura.");
        if (fechaIng.isBefore(fechaNac))
            throw new ValidationException("Fecha ingreso", "La fecha de ingreso no puede ser anterior al nacimiento.");

        if (cmbTipoDoc.getSelectedItem() == null)
            throw new ValidationException("Tipo documento", "Seleccione un tipo de documento.");
        if (cmbCargo.getSelectedItem() == null)
            throw new ValidationException("Cargo", "Seleccione un cargo.");
        if (cmbMunicipio.getSelectedItem() == null)
            throw new ValidationException("Municipio", "Seleccione un municipio.");
        if (cmbRol.getSelectedItem() == null)
            throw new ValidationException("Rol", "Seleccione un rol.");

        Funcionario f = (funcionario != null) ? funcionario : new Funcionario();
        f.setNombres(nombres);
        f.setApellidos(apellidos);
        f.setTipoDocumento((TipoDocumento) cmbTipoDoc.getSelectedItem());
        f.setNumeroDocumento(documento);
        f.setFechaNacimiento(fechaNac);
        f.setFechaIngreso(fechaIng);
        f.setEmail(email);
        f.setTelefono(telefono.isEmpty() ? null : telefono);
        f.setCargo((Cargo) cmbCargo.getSelectedItem());
        f.setMunicipio((Municipio) cmbMunicipio.getSelectedItem());
        f.setEstado((String) cmbEstado.getSelectedItem());
        f.setRol((Role) cmbRol.getSelectedItem());
        return f;
    }

    // ── Carga de catálogos ──────────────────────────────────────────────────
    private void cargarCatalogos() {
        try {
            List<TipoDocumento> tipos = tipoDocDAO.findAllActivos();
            tipos.forEach(t -> cmbTipoDoc.addItem(t));

            List<Cargo> cargos = cargoDAO.findAllActivos();
            cargos.forEach(c -> cmbCargo.addItem(c));

            List<Municipio> municipios = municipioDAO.findAllActivos();
            municipios.forEach(m -> cmbMunicipio.addItem(m));
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                "No se pudieron cargar los catálogos:\n" + e.getMessage(),
                "Error de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Precarga los datos del funcionario en modo edición. */
    private void precargarDatos() {
        txtNombres.setText(funcionario.getNombres());
        txtApellidos.setText(funcionario.getApellidos());
        txtDocumento.setText(funcionario.getNumeroDocumento());
        txtEmail.setText(funcionario.getEmail());
        txtTelefono.setText(funcionario.getTelefono() != null ? funcionario.getTelefono() : "");
        txtFechaNacimiento.setText(funcionario.getFechaNacimiento().format(FMT));
        txtFechaIngreso.setText(funcionario.getFechaIngreso().format(FMT));
        cmbEstado.setSelectedItem(funcionario.getEstado());
        cmbRol.setSelectedItem(funcionario.getRol() != null ? funcionario.getRol() : Role.DOCENTE);

        // Seleccionar items de los combos por id
        selectCombo(cmbTipoDoc, funcionario.getTipoDocumento().getId());
        selectCombo(cmbCargo,   funcionario.getCargo().getId());
        selectCombo(cmbMunicipio, funcionario.getMunicipio().getId());
    }

    private <T> void selectCombo(JComboBox<T> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object item = combo.getItemAt(i);
            int itemId = -1;
            if (item instanceof TipoDocumento td) {
                itemId = td.getId();
            } else if (item instanceof Cargo c) {
                itemId = c.getId();
            } else if (item instanceof Municipio m) {
                itemId = m.getId();
            }
            if (itemId == id) { combo.setSelectedIndex(i); return; }
        }
    }

    // ── Helpers de construcción UI ──────────────────────────────────────────
    private JTextField campo(String placeholder) {
        JTextField tf = new JTextField(16);
        tf.setFont(FUENTE_CAMPO);
        tf.setToolTipText(placeholder);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 8, 5, 8)));
        return tf;
    }

    private <T> JComboBox<T> comboBox() {
        JComboBox<T> cb = new JComboBox<>();
        cb.setFont(FUENTE_CAMPO);
        cb.setBackground(Color.WHITE);
        return cb;
    }

    private JButton boton(String texto, Color fondo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(fondo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(9, 20, 9, 20));
        return btn;
    }

    private void addLabel(JPanel p, GridBagConstraints gbc, String texto, int x, int y) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(FUENTE_LABEL);
        lbl.setForeground(new Color(60, 60, 60));
        gbc.gridx = x; gbc.gridy = y; gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.WEST;
        p.add(lbl, gbc);
    }

    private void addField(JPanel p, GridBagConstraints gbc, JComponent comp, int x, int y) {
        gbc.gridx = x; gbc.gridy = y; gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        p.add(comp, gbc);
        resetGBC(gbc);
    }

    private GridBagConstraints defaultGBC() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        return gbc;
    }

    private void resetGBC(GridBagConstraints gbc) {
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
    }

    // ── API pública ─────────────────────────────────────────────────────────
    public boolean isConfirmado()    { return confirmado; }
    public Funcionario getFuncionario() { return funcionario; }
}
