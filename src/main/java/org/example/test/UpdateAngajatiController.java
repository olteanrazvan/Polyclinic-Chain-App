package org.example.test;

import javafx.fxml.Initializable;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.sql.Date;
import java.time.LocalDate;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class UpdateAngajatiController implements Initializable {

    @FXML
    private TextField CNPFld;

    @FXML
    private TextField IBANFld;

    @FXML
    private TextField adresaFld;

    @FXML
    private DatePicker dataAngajariiFld;

    @FXML
    private TextField emailFld;

    @FXML
    private ChoiceBox<String> functieFld;

    @FXML
    private Button inapoiButton;

    @FXML
    private Label messageLabel;

    @FXML
    private TextField nrTelFld;

    @FXML
    private TextField numeFld;

    @FXML
    private TextField oreFld;

    @FXML
    private TextField parolaFld;

    @FXML
    private TextField prenumeFld;

    @FXML
    private TextField salariuFld;

    private int utilizatorID = 0;

    private String initialEmail;

    private String initialCNP;

    private String initialIBAN;

    @FXML
    void inapoiButton(ActionEvent e) {
        Stage stage = (Stage) inapoiButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void modificaButton(ActionEvent e){
        if(numeFld.getText().isBlank() || prenumeFld.getText().isBlank() ||
                emailFld.getText().isBlank() || parolaFld.getText().isBlank() ||
                CNPFld.getText().isBlank() || IBANFld.getText().isBlank() ||
                adresaFld.getText().isBlank() || nrTelFld.getText().isBlank() ||
                dataAngajariiFld.getValue() == null ||
                salariuFld.getText().isBlank() || oreFld.getText().isBlank())
            messageLabel.setText("Completeaza toate campurile");
        else{
            String emailExist = "{CALL VerificaExistentaEmail(?, ?)}";
            String CNPExist = "{CALL VerificaExistentaCNP(?, ?)}";
            String IBANExist = "{CALL VerificaExistentaContIBAN(?, ?)}";
            Connection connection = null;
            Statement statement = null;
            PreparedStatement preparedStatement;
            ResultSet resultSet = null;
            DatabaseConnection connectNow = new DatabaseConnection();
            connection = connectNow.getConnection();
            CallableStatement callableStatement;
            int exista = 0;
            try {
                int valoare = 0;
                if(!emailFld.getText().equals(initialEmail)){
                    callableStatement = connection.prepareCall(emailExist);
                    callableStatement.setString(1, emailFld.getText());
                    callableStatement.registerOutParameter(2, Types.INTEGER);
                    callableStatement.execute();
                    valoare = callableStatement.getInt(2);
                    if(valoare == 1){
                        messageLabel.setText("Email existent");
                        exista = 1;
                    }
                }
                if(!CNPFld.getText().equals(initialCNP)){
                    callableStatement = connection.prepareCall(CNPExist);
                    callableStatement.setString(1, CNPFld.getText());
                    callableStatement.registerOutParameter(2, Types.INTEGER);
                    callableStatement.execute();
                    valoare = callableStatement.getInt(2);
                    if(valoare == 1){
                        messageLabel.setText("CNP existent");
                        exista = 1;
                    }
                }
                if(!IBANFld.getText().equals(initialIBAN)){
                    callableStatement = connection.prepareCall(IBANExist);
                    callableStatement.setString(1, IBANFld.getText());
                    callableStatement.registerOutParameter(2, Types.INTEGER);
                    callableStatement.execute();
                    valoare = callableStatement.getInt(2);
                    if(valoare == 1){
                        messageLabel.setText("IBAN existent");
                        exista = 1;
                    }
                }
                if(exista == 0){
                    String updateUtilizator = "{call UpdateUtilizator(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
                    callableStatement = connection.prepareCall(updateUtilizator);
                    callableStatement.setInt(1, utilizatorID);
                    callableStatement.setString(2, CNPFld.getText());
                    callableStatement.setString(3, numeFld.getText());
                    callableStatement.setString(4, prenumeFld.getText());
                    callableStatement.setString(5, adresaFld.getText());
                    callableStatement.setString(6, nrTelFld.getText());
                    callableStatement.setString(7, emailFld.getText());
                    callableStatement.setString(8, parolaFld.getText());
                    callableStatement.setString(9, IBANFld.getText());
                    callableStatement.execute();

                    String updateAngajat = "{call UpdateAngajat(?, ?, ?, ?, ?)}";
                    callableStatement = connection.prepareCall(updateAngajat);
                    callableStatement.setInt(1, utilizatorID);
                    callableStatement.setString(2, salariuFld.getText());
                    callableStatement.setString(3, oreFld.getText());
                    callableStatement.setString(4, String.valueOf(functieFld.getValue()));
                    callableStatement.setString(5, String.valueOf(dataAngajariiFld.getValue()));
                    callableStatement.execute();
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            if(String.valueOf(functieFld.getValue()).equals("Super Admin")){
                UserData.setEmail(emailFld.getText());
            }
            Stage stage = (Stage) inapoiButton.getScene().getWindow();
            stage.close();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String selectUser = "SELECT * FROM Utilizatori WHERE Email = '" + UserToUpdate.getEmail() + "'";
        Connection connection = null;
        DatabaseConnection connectionNow = new DatabaseConnection();
        connection = connectionNow.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(selectUser);
            while (resultSet.next()) {
                utilizatorID = resultSet.getInt("ID_Utilizator");
                numeFld.setText(resultSet.getString("Nume"));
                prenumeFld.setText(resultSet.getString("Prenume"));
                emailFld.setText(resultSet.getString("Email"));
                initialEmail = resultSet.getString("Email");
                parolaFld.setText(resultSet.getString("Parola"));
                CNPFld.setText(resultSet.getString("CNP"));
                initialCNP = resultSet.getString("CNP");
                IBANFld.setText(resultSet.getString("ContIBAN"));
                initialIBAN = resultSet.getString("ContIBAN");
                adresaFld.setText(resultSet.getString("Adresa"));
                nrTelFld.setText(resultSet.getString("NumarTelefon"));
                functieFld.setValue(resultSet.getString("Functie"));
            }
            String angajatInfo = "SELECT * FROM Angajat WHERE ID_Utilizator = " + utilizatorID;
            resultSet = statement.executeQuery(angajatInfo);
            while (resultSet.next()) {
                Date dataAngajarii = resultSet.getDate("DataAngajarii");
                LocalDate dataAngajariiLocalDate = dataAngajarii.toLocalDate();
                dataAngajariiFld.setValue(dataAngajariiLocalDate);
                salariuFld.setText(resultSet.getString("Salariu"));
                oreFld.setText(resultSet.getString("NumarOre"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
