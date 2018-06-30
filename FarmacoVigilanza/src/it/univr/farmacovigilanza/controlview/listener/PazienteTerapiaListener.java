package it.univr.farmacovigilanza.controlview.listener;

import it.univr.farmacovigilanza.controlview.InsertTerapyController;
import it.univr.farmacovigilanza.dao.PazienteDAO;
import it.univr.farmacovigilanza.model.Paziente;
import java.time.LocalDate;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;


public class PazienteTerapiaListener<Integer> implements ChangeListener{
    
    private final InsertTerapyController controller;
    private PazienteDAO pazDao;

    public PazienteTerapiaListener(InsertTerapyController controller,PazienteDAO pazDao) {
        this.controller=controller;
        this.pazDao=pazDao;
    }
    
    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        int idPaziente = controller.comboBoxGet((int) newValue);
        Paziente paziente = pazDao.getPaziente(idPaziente);
        controller.setPaziente(paziente);
        controller.setDateFactory(controller.getInizio(), controller.getDataNascita(), LocalDate.now());
        controller.setDateFactory(controller.getFine(),  controller.getDataNascita(), LocalDate.now());
        controller.getInizio().setValue(null);
        controller.getFine().setValue(null);
        controller.getFine().setDisable(true);
        controller.clean();
    }
    
}