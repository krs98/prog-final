package view;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

import view.components.*;

public abstract class Form extends Frame {
    LinkedHashMap<String, FormInput> inputs;
    LinkedHashMap<String, FormDropdown> dropdowns;

    private Object[] params;

    private Panel panel;
    private int rows;

    public Form() {
        super();

        this.inputs    = new LinkedHashMap<>();
        this.dropdowns = new LinkedHashMap<>();
        this.params = null;
        this.rows = 0;

        withPanel(this::build);
    }

    public Form(Object... params) {
        super();

        this.inputs    = new LinkedHashMap<>();
        this.dropdowns = new LinkedHashMap<>();

        // Guardamos un array de objetos al que podremos acceder durante la 
        // construcción del formulario. Esto nos permitirá usar sus atributos 
        // como valores por defecto, entre otros.
        this.params = params;
        this.rows = 0;

        withPanel(this::build);
    }

    protected abstract void buildForm(Object[] params);

    protected void build(Panel panel) {
        this.panel = panel;
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(30, 44, 40, 44));

        buildForm(params);

        var submitButton = new FormSubmitButton(this::handleSubmit);
        panel.add(submitButton, submitButton.constraints(rows));
    }

    void addRequiredField(String name) {
        addRequiredField(name, t -> true);
    }

    <T> void addRequiredField(String name, T def) {
        addRequiredField(name, def, t -> true);
    }

    void addRequiredField(String name, Predicate<String> validator) {
        addRequiredField(name, "", validator);
    }

    <T> void addRequiredField(String name, T def, Predicate<String> validator) {
        addField(name, def, validator);
        inputs.get(name).required();
    }

    void addField(String name) {
        addField(name, "", t -> true);
    }

    <T> void addField(String name, T def) {
        addField(name, def, t -> true); 
    }

    void addField(String name, Predicate<String> validator) {
        addField(name, "", validator);
    }

    <T> void addField(String name, T def, Predicate<String> validator) {
        var label = new FormLabel(name);
        var input = new FormInput(validator);
        input.setText(def.toString());
        input.setCaretPosition(0);

        inputs.put(name, input);
        panel.add(label, label.constraints(rows));
        panel.add(input, input.constraints(rows++));
    }

    void addField(String name, String def, String[] options) {
        var label    = new FormLabel(name);
        var dropdown = new FormDropdown(options);
        dropdown.setSelectedItem(def);

        dropdowns.put(name, dropdown);
        panel.add(label, label.constraints(rows));
        panel.add(dropdown, dropdown.constraints(rows++));
    }

    private boolean valid() {
        return inputs.entrySet().stream()
            .map(Map.Entry::getValue)
            /* Usamos map->reduce en vez de allMatch(FormInput::valid)
             * para asegurarnos de que todos los elementos son validados
             * y alterados como corresponda */
            .map(FormInput::valid)
            .reduce(true, (acc, x) -> acc && x);
    }

    private void handleSubmit(ActionEvent e) {
        if (valid()) {
            onSubmit(e);
            dispose();
        }
    }

    abstract void onSubmit(ActionEvent e);
}
