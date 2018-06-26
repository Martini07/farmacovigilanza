package it.univr.farmacovigilanza.controlview;

import it.univr.farmacovigilanza.dao.DAOFactory;
import it.univr.farmacovigilanza.dao.PazienteDAO;
import it.univr.farmacovigilanza.dao.UserDAO;
import it.univr.farmacovigilanza.model.Utente;
import it.univr.farmacovigilanza.model.Medico;
import it.univr.farmacovigilanza.model.Farmacologo;
import it.univr.farmacovigilanza.model.FattoreRischio;
import it.univr.farmacovigilanza.model.Paziente;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FXMLDocumentController implements Initializable {
    private static Utente logged=null;
    @FXML
    private Label login;
    @FXML
    private TextField password;
    @FXML
    private TextField username;
    @FXML
    private Button loginButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button pazientiButton;
    @FXML
    private ChoiceBox<Integer> sceltaPaziente;
    @FXML
    private Label pazienteLabel;
    @FXML
    private Label reazioneAvversaLabel;
    @FXML
    private ChoiceBox<?> sceltaReazioneAvversa;
    @FXML
    private Label dataReazioneAvversaLabel;
    @FXML
    private Label terapieLabel;
    @FXML
    private DatePicker dataReazioneAvversa;
    @FXML
    private Button segnalaPazienteButton;
    @FXML
    private Button terapiaPaziente;
    @FXML
    private Button aggiuntaPaziente;
    @FXML
    private TableView<Paziente> grid;
    @FXML
    private ListView<?> listaPazienti;
    @FXML
    private TableColumn<Paziente, Integer> id;
    @FXML
    private TableColumn<Paziente, Integer> anno;
    @FXML
    private TableColumn<Paziente, String> provincia;
    @FXML
    private TableColumn<Paziente, String> professione;
    @FXML
    private TableColumn<Paziente, List<FattoreRischio>> fattoriRischio;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        anno.setCellValueFactory(new PropertyValueFactory<>("annoNascita"));
        provincia.setCellValueFactory(new PropertyValueFactory<>("provinciaRes"));
        professione.setCellValueFactory(new PropertyValueFactory<>("professione"));
        fattoriRischio.setCellValueFactory(new PropertyValueFactory<>("fattoriRischio"));
    }    

    @FXML
    private void doLogin(ActionEvent event) {
        //try login
        DAOFactory test = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        UserDAO userDao = test.getUserDAO();
        Utente utente = userDao.getUtente(username.getText(), password.getText());
        if (utente != null) {
            if (utente instanceof Medico){
                logged=(Medico) utente;
                enableMedicoInterface();
            } else { //Farmacologo
                logged=(Farmacologo) utente;
            }
        } else {
            //login errata
            login.setText("Failed to login");
            //check if username exist...
            username.clear();
            password.clear();
        }     
        //logged in
        login.setText("Logged as " + utente.getNome() + " " + utente.getCognome());
        disableLoginFields();
        //enable logout
        logoutButton.setVisible(true);
        logoutButton.setDisable(false);
    }
    
    @FXML
    private void doLogout(ActionEvent event) {
        //deactivate logout button
        logoutButton.setVisible(false);
        logoutButton.setDisable(true);
        login.setText("");
        
        enableLoginFields();
        if(logged instanceof Medico){
            //disable Medico interface
            disableMedicoInterface();
            
        }
        logged=null;
    }
    
    private void enableLoginFields(){
        username.setVisible(true);
        username.setDisable(false);
        password.setVisible(true);
        password.setDisable(false);
        loginButton.setVisible(true);
        loginButton.setDisable(false);
        username.clear();
        password.clear();
    }
    
    private void disableLoginFields(){
        username.setVisible(false);
        username.setDisable(true);
        password.setVisible(false);
        password.setDisable(true);
        loginButton.setVisible(false);
        loginButton.setDisable(true);
    }
    
    private void enableMedicoInterface(){
        pazientiButton.setDisable(false);
        pazientiButton.setVisible(true);
        //Obtain pazienti
        DAOFactory test = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        PazienteDAO pazDao = test.getPazienteDAO();
        ObservableList<Paziente> mieiPazienti= pazDao.getPazienti(((Medico) logged).getIdUtente());
        ObservableList<Integer> idPazienti=FXCollections.observableArrayList();
        for(Paziente p: mieiPazienti){
            idPazienti.add(p.getId());
        }
        sceltaPaziente.setItems(idPazienti);
        enableSegnalazione();
    }

    private void disableMedicoInterface(){
        pazientiButton.setDisable(true);
        pazientiButton.setVisible(false);
        grid.setVisible(false);
        grid.setDisable(true);
        segnalaPazienteButton.setDisable(true);
        segnalaPazienteButton.setVisible(false);
        terapiaPaziente.setDisable(true);
        terapiaPaziente.setVisible(false);
        disableSegnalazione();
        grid.getItems().removeAll(grid.getItems());
    }
    
    private void enableSegnalazione(){
        pazienteLabel.setVisible(true);
        pazienteLabel.setDisable(false);
        segnalaPazienteButton.setDisable(true);
        segnalaPazienteButton.setVisible(false);
        terapiaPaziente.setDisable(true);
        terapiaPaziente.setVisible(false);
        reazioneAvversaLabel.setVisible(true);
        reazioneAvversaLabel.setDisable(false);
        dataReazioneAvversaLabel.setVisible(true);
        dataReazioneAvversaLabel.setDisable(false);
        terapieLabel.setVisible(true);
        terapieLabel.setDisable(false);
        sceltaPaziente.setVisible(true);
        sceltaPaziente.setDisable(false);
        sceltaReazioneAvversa.setVisible(true);
        sceltaReazioneAvversa.setDisable(false);
        dataReazioneAvversa.setVisible(true);
        dataReazioneAvversa.setDisable(false);
        aggiuntaPaziente.setVisible(true);
        aggiuntaPaziente.setDisable(false);
    }
    
    private void disableSegnalazione(){
        pazienteLabel.setVisible(false);
        pazienteLabel.setDisable(true);
        reazioneAvversaLabel.setVisible(false);
        reazioneAvversaLabel.setDisable(true);
        dataReazioneAvversaLabel.setVisible(false);
        dataReazioneAvversaLabel.setDisable(true);
        terapieLabel.setVisible(false);
        terapieLabel.setDisable(true);
        sceltaPaziente.setVisible(false);
        sceltaPaziente.setDisable(true);
        sceltaPaziente.getItems().removeAll(sceltaPaziente.getItems());
        sceltaReazioneAvversa.setVisible(false);
        sceltaReazioneAvversa.setDisable(true);
        sceltaReazioneAvversa.getItems().removeAll(sceltaReazioneAvversa.getItems());
        dataReazioneAvversa.setVisible(false);
        dataReazioneAvversa.setDisable(true);
        aggiuntaPaziente.setVisible(false);
        aggiuntaPaziente.setDisable(true);
    }
    
    @FXML
    private void showListaPazienti(ActionEvent event) {
        //disable lista pazienti button
        pazientiButton.setDisable(true);
        pazientiButton.setVisible(false);
        segnalaPazienteButton.setDisable(false);
        segnalaPazienteButton.setVisible(true);
        terapiaPaziente.setDisable(false);
        terapiaPaziente.setVisible(true);
        //disable segnalazione
        disableSegnalazione();
        grid.setVisible(true);
        grid.setDisable(false);
        //listaPazienti.setOnMousePressed(new ListViewHandler(listaPazienti, this));
        DAOFactory test = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
        PazienteDAO pazDao = test.getPazienteDAO();
        ObservableList<Paziente> mieiPazienti= pazDao.getPazienti(((Medico) logged).getIdUtente());
        grid.setItems(mieiPazienti);
    }
    
    public void enableSegnalazione(int id){
        grid.setVisible(false);
        grid.setDisable(true);
        sceltaPaziente.setValue(sceltaPaziente.getItems().indexOf(id));
        //set this patient
        enableSegnalazione();
    }

    @FXML
    private void segnalaPaziente(ActionEvent event) {
    }

    @FXML
    private void addTerapiaPaziente(ActionEvent event) {
    }

    @FXML
    private void addPaziente(ActionEvent event) {
            try{
                Parent root = FXMLLoader.load(getClass().getResource("InsertPatient.fxml"));
                Stage stage = new Stage();
                stage.setTitle("Inserimento paziente");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                //update choicebox with new pazienti
                ObservableList<Integer> idInseriti = (ObservableList<Integer>) stage.getUserData();
                sceltaPaziente.getItems().addAll(idInseriti);
                enableSegnalazione(idInseriti.get(idInseriti.size()-1));
            }catch(Exception e){
                System.out.println("Creazione finestra: "+e);
            }
    }
    
    public static Utente getUtente(){
        return logged;
    }

}
