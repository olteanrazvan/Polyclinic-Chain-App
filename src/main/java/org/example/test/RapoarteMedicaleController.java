package org.example.test;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class RapoarteMedicaleController implements Initializable{

    @FXML
    private Button inapoiButton;

    @FXML
    private TextField raportFld;



    @FXML
    public void inapoiButton(ActionEvent e) {
        Stage stage = (Stage) inapoiButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void adaugaButton(ActionEvent e){

        Stage stage = (Stage) inapoiButton.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //raportFld.setText();
    }
}
