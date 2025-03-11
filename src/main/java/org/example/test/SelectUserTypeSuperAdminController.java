package org.example.test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.Node;

import javax.swing.plaf.nimbus.State;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;


public class SelectUserTypeSuperAdminController implements Initializable {

    @FXML
    private Button adaugaButtonType;

    @FXML
    private Button inapoiButtonType;

    @FXML
    private ChoiceBox<String> selectFunctieFld;

    @FXML
    private Label errorMessage;

    private String[] functii = {"Inspector Resurse Umane", "Expert Financiar Contabil", "Receptioner", "Asistent Medical", "Medic", "Admin", "Pacient"};

    public void adaugaButtonAction(ActionEvent e){
        if(selectFunctieFld.getValue() == null){
            errorMessage.setText("Te rog selecteaza o functie");
        }
        else{
            errorMessage.setText("");
            try {
                Parent root;
                FXMLLoader loader;
                Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                if(String.valueOf(selectFunctieFld.getValue()).equals("Medic")){
                    loader = new FXMLLoader(SelectUserTypeSuperAdminController.class.getResource("addMedicSA.fxml"));
                    root = loader.load();
                    stage.setScene(new Scene(root, 930, 780));
                }
                else if(String.valueOf(selectFunctieFld.getValue()).equals("Asistent Medical")){
                    loader = new FXMLLoader(SelectUserTypeSuperAdminController.class.getResource("addAsistentSA.fxml"));
                    root = loader.load();
                    stage.setScene(new Scene(root, 930, 780));
                }
                else if(String.valueOf(selectFunctieFld.getValue()).equals("Pacient")){
                    loader = new FXMLLoader(SelectUserTypeSuperAdminController.class.getResource("addPacient.fxml"));
                    root = loader.load();
                    stage.setScene(new Scene(root, 520, 700));
                }
                else{
                    loader = new FXMLLoader(SelectUserTypeSuperAdminController.class.getResource("AddAngajati.fxml"));
                    root = loader.load();
                    stage.setScene(new Scene(root, 410, 850));
                }
                stage.show();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public static void inapoiFunctiiButtonAction(ActionEvent e, Class<?> controllerClass) {
        try {
            Parent root;
            FXMLLoader loader = new FXMLLoader(controllerClass.getResource("SelectUserTypeSuperAdmin.fxml"));
            root = loader.load();
            SelectUserTypeSuperAdminController selectUserTypeSuperAdminController = loader.getController();
            Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void inapoiButtonAction(ActionEvent e){
        Stage stage = (Stage) inapoiButtonType.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectFunctieFld.getItems().addAll(functii);
    }
}
