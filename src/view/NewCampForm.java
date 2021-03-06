package view;

import java.time.LocalDate;

import java.awt.event.*;

import models.Camp;
import data.Db;
import utils.Parse;

public class NewCampForm extends Form {
    private Db db;

    public NewCampForm(Db db, String[] campKinds) {
        super(false, (Object) campKinds);
        this.db = db;
    }

    protected void buildForm(Object[] params) {
        var campKinds = (String[]) params[0];

        addRequiredField("Nombre");
        addField("Tipo", campKinds);
        addRequiredField("Descripción");
        addRequiredField("Lugar");
        addRequiredField("Comienzo (dd/mm/yyyy)", Parse.withParsedDate(
                date -> date.isAfter(LocalDate.now())
                        && date.isBefore(Parse.parseDate(inputs.get("Fin (dd/mm/yyyy)").getText()))));
        addRequiredField("Fin (dd/mm/yyyy)", Parse.withParsedDate(
                date -> date.isAfter(LocalDate.now())
                        && date.isAfter(Parse.parseDate(inputs.get("Comienzo (dd/mm/yyyy)").getText()))));
        addRequiredField("Edad Mínima", Parse.withParsedString(
                x -> 4 <= x && x <= Parse.parseIntOrDefault(inputs.get("Edad Máxima").getText(), 16)));
        addRequiredField("Edad Máxima", Parse.withParsedString(
                x -> Parse.parseIntOrDefault(inputs.get("Edad Mínima").getText(), 4) <= x && x <= 16));
    }

    protected void onSubmit(ActionEvent e) {
        var name = inputs.get("Nombre").getText();
        var kind = (String) dropdowns.get("Tipo").getSelectedItem();
        var desc = inputs.get("Descripción").getText();
        var loc = inputs.get("Lugar").getText();
        var startDate = Parse.parseDate(inputs.get("Comienzo (dd/mm/yyyy)").getText());
        var endDate = Parse.parseDate(inputs.get("Fin (dd/mm/yyyy)").getText());
        var minAge = Integer.parseInt(inputs.get("Edad Mínima").getText());
        var maxAge = Integer.parseInt(inputs.get("Edad Máxima").getText());

        db.addCamp(new Camp(0, name, kind, desc, loc, startDate, endDate, minAge, maxAge))
                .ifError(OperationFailure::new);
    }
}
