package org.example.test;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class SuperAdminController implements Initializable{
    @FXML
    private TableView<UsersSA> TableSuperAdmin;

    @FXML
    private TableColumn<UsersSA, String> colAdresaSA;

    @FXML
    private TableColumn<UsersSA, String> colCNPSA;

    @FXML
    private TableColumn<UsersSA, String> colEmailSA;

    @FXML
    private TableColumn<UsersSA, String> colFunctieSA;

    @FXML
    private TableColumn<UsersSA, String> colIBANSA;

    @FXML
    private TableColumn<UsersSA, String> colNrTelSA;

    @FXML
    private TableColumn<UsersSA, String> colNumeSA;

    @FXML
    private TableColumn<UsersSA, String> colPrenumeSA;

    @FXML
    private TableColumn<UsersSA, String> colPoliclinicaSA;

    @FXML
    private Label errorMessage;

    @FXML
    protected void doneazaSange(ActionEvent e){
        Connection connection = null;
        DatabaseConnection connectionNow = new DatabaseConnection();
        connection = connectionNow.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            String cautaDonator = "SELECT Utilizatori.ID_Utilizator FROM Utilizatori, DonatoriSange " +
                                      "WHERE Utilizatori.ID_Utilizator = DonatoriSange.ID_Utilizator AND " +
                                      "Email = '" + UserData.getEmail() + "'";
            statement = connection.createStatement();
            resultSet = statement.executeQuery(cautaDonator);
            if(resultSet.next()){
                errorMessage.setText("Ai donat deja");
            }
            else{
                FXMLLoader fxmlLoader = new FXMLLoader(SuperAdminController.class.getResource("DoneazaSange.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 600, 430);
                Stage stageDS = new Stage();
                stageDS.setScene(scene);
                stageDS.show();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    ObservableList<UsersSA> list;

    @FXML
    protected void logOutButtonAction(ActionEvent e){
        HelloController.logOutButtonAction(e, SuperAdminController.class);
    }
    public void getUsers(){
        Connection connection = null;
        Statement selectStatement = null;
        ResultSet resultSet = null;
        ObservableList<UsersSA> tempList = FXCollections.observableArrayList();

        DatabaseConnection connectNow = new DatabaseConnection();
        connection = connectNow.getConnection();

        try{
            int superAdminID = 0;
            String getSuperAdminPolyclinicID = "SELECT ID_Policlinici FROM Utilizatori WHERE Email = '" + UserData.getEmail() + "'";
            selectStatement = connection.createStatement();
            resultSet = selectStatement.executeQuery(getSuperAdminPolyclinicID);
            while(resultSet.next()){
                superAdminID = resultSet.getInt("ID_Policlinici");
            }

            String getAllData  = "SELECT * FROM utilizatori, policlinici WHERE policlinici.ID_Policlinici = utilizatori.ID_Policlinici AND utilizatori.ID_Policlinici = " + superAdminID;
            resultSet = selectStatement.executeQuery(getAllData);

            while(resultSet.next()){
                tempList.add(new UsersSA(resultSet.getString("Nume"), resultSet.getString("Prenume"), resultSet.getString("Email"), resultSet.getString("Adresa"), resultSet.getString("NumarTelefon"), resultSet.getString("CNP"), resultSet.getString("ContIBAN"), resultSet.getString("Functie"), resultSet.getString("NumePoliclinica")));
            }
        }catch (Exception sqlex){
            System.err.println("An SQL Exception occured. Details are provided below:");
            sqlex.printStackTrace(System.err);
        }
        finally {
            closeOperation(connection);
            closeOperation(selectStatement);
            closeOperation(resultSet);
        }
        list = tempList;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateTable();
    }
    public void updateTable(){
        colNumeSA.setCellValueFactory(new PropertyValueFactory<UsersSA, String>("Nume"));
        colPrenumeSA.setCellValueFactory(new PropertyValueFactory<UsersSA, String>("Prenume"));
        colEmailSA.setCellValueFactory(new PropertyValueFactory<UsersSA, String>("Email"));
        colAdresaSA.setCellValueFactory(new PropertyValueFactory<UsersSA, String>("Adresa"));
        colNrTelSA.setCellValueFactory(new PropertyValueFactory<UsersSA, String>("NumarTelefon"));
        colCNPSA.setCellValueFactory(new PropertyValueFactory<UsersSA, String>("CNP"));
        colIBANSA.setCellValueFactory(new PropertyValueFactory<UsersSA, String>("IBAN"));
        colFunctieSA.setCellValueFactory(new PropertyValueFactory<UsersSA, String>("Functie"));
        colPoliclinicaSA.setCellValueFactory(new PropertyValueFactory<UsersSA, String>("Policlinica"));
        getUsers();
        TableSuperAdmin.setItems(list);
    }
    public void addUser(ActionEvent e){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(SuperAdminController.class.getResource("SelectUserTypeSuperAdmin.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            Stage stageASA = new Stage();
            stageASA.setScene(scene);
            stageASA.show();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    public void removeUser(ActionEvent e){
        Connection connection;
        PreparedStatement preparedStatement = null;
        Statement statement = null;
        ResultSet resultSet = null;
        CallableStatement callableStatement = null;
        String userToDeletePacient = "DELETE FROM Pacienti WHERE ID_Utilizator = ?";
        String userToDelete = "DELETE FROM Utilizatori WHERE ID_Utilizator = ?";
        String verifyPolyclinicID = "SELECT * FROM Utilizatori WHERE Email = ?";
        String verifySuperAdminPolyclinicID = "SELECT * FROM Utilizatori WHERE Email = '" + UserData.getEmail() + "'";

        DatabaseConnection connectNow = new DatabaseConnection();
        connection = connectNow.getConnection();

        try{
            TableView.TableViewSelectionModel<UsersSA> selectedID = TableSuperAdmin.getSelectionModel();
            UsersSA selectedUser = selectedID.getSelectedItem();
            if(selectedUser != null){
                if(selectedUser.getFunctie().equals("Super Admin")){
                    errorMessage.setText("Nu poti sterge un Super Admin");
                }else if(selectedUser.getFunctie().equals("Pacient")){
                    errorMessage.setText("");
                    int pacientID = 0;
                    int utilizatorID = 0;
                    String getPacientID = "SELECT Pacienti.ID_Pacient, Pacienti.ID_Utilizator " +
                                          "FROM Utilizatori, Pacienti " +
                                          "WHERE Utilizatori.ID_Utilizator = Pacienti.ID_Utilizator AND Utilizatori.Email = '" + selectedUser.getEmail() + "'";
                    statement = connection.createStatement();
                    resultSet = statement.executeQuery(getPacientID);
                    while(resultSet.next()){
                        utilizatorID = resultSet.getInt("ID_Utilizator");
                        pacientID = resultSet.getInt("ID_Pacient");
                    }
                    String deleteBon = "DELETE FROM bonurifiscale WHERE ID_Pacient = " + pacientID;
                    String deleteServicii = "DELETE FROM serviciimedicalerealizate WHERE ID_Pacient = " + pacientID;
                    String deleteRapoarte = "DELETE FROM rapoartemedicale WHERE ID_Pacient = " + pacientID;
                    String deleteProgramare = "DELETE FROM programarepacienti WHERE ID_Pacient = " + pacientID;
                    String deletePacient = "DELETE FROM Pacienti WHERE ID_Utilizator = " + utilizatorID;
                    String deleteSange = "DELETE FROM DonatoriSange WHERE ID_Utilizator = " + utilizatorID;
                    String deleteUtilizator = "DELETE FROM Utilizatori WHERE ID_Utilizator = " + utilizatorID;
                    statement.execute(deleteBon);
                    statement.execute(deleteServicii);
                    statement.execute(deleteRapoarte);
                    statement.execute(deleteProgramare);
                    statement.execute(deletePacient);
                    statement.execute(deleteSange);
                    statement.execute(deleteUtilizator);
                    TableSuperAdmin.getItems().remove(selectedID.getSelectedIndex());
                } else if(selectedUser.getFunctie().equals("Medic")){
                    errorMessage.setText("");
                    int utilizatorID = 0;
                    int angajatID = 0;
                    int medicID = 0;
                    int polyclinicID = 0;
                    int nrMedici = 0;
                    int newMedic = 0;
                    String getMedicID = "SELECT Utilizatori.ID_Policlinici, Utilizatori.id_utilizator, angajat.id_angajat, medic.ID_Medic FROM utilizatori, angajat, medic WHERE utilizatori.id_utilizator = angajat.id_utilizator AND angajat.id_angajat = medic.id_angajat AND utilizatori.email = '" + selectedUser.getEmail() + "'";
                    statement = connection.createStatement();
                    resultSet = statement.executeQuery(getMedicID);
                    while(resultSet.next()){
                        polyclinicID = resultSet.getInt("ID_Policlinici");
                        utilizatorID = resultSet.getInt("ID_Utilizator");
                        angajatID = resultSet.getInt("ID_Angajat");
                        medicID = resultSet.getInt("ID_Medic");
                    }
                    String getNumberOfMedici = "SELECT COUNT(*) AS NrMedici FROM utilizatori, angajat, medic WHERE utilizatori.id_utilizator = angajat.id_utilizator AND angajat.id_angajat = medic.id_angajat AND utilizatori.id_policlinici = " + polyclinicID;
                    String getNewMedicId = "SELECT Utilizatori.ID_Policlinici, Utilizatori.id_utilizator, angajat.id_angajat, medic.ID_Medic FROM utilizatori, angajat, medic WHERE utilizatori.id_utilizator = angajat.id_utilizator AND angajat.id_angajat = medic.id_angajat AND utilizatori.id_policlinici = " + polyclinicID;
                    resultSet = statement.executeQuery(getNumberOfMedici);
                    while(resultSet.next()){
                        nrMedici = resultSet.getInt("NrMedici");
                    }
                    resultSet = statement.executeQuery(getNewMedicId);
                    while(resultSet.next()){
                        if(resultSet.getInt("ID_Medic") != medicID)
                            newMedic = resultSet.getInt("ID_Medic");
                    }
                    if(nrMedici == 1){
                        errorMessage.setText("Nu poti sterge singurul medic");
                    }
                    else{
                        String deleteAcreditari = "DELETE FROM acreditarispeciale WHERE ID_Medic = " + medicID;
                        String updatePacient = "UPDATE pacienti SET ID_Medic = " + newMedic + " WHERE ID_Medic = " + medicID;
                        String deleteMedic = "DELETE FROM medic WHERE ID_Medic = " + medicID;
                        String deleteAngajat = "DELETE FROM angajat WHERE ID_Angajat = " + angajatID;
                        String deleteDonator = "DELETE FROM donatorisange WHERE ID_Utilizator = " + utilizatorID;
                        String deleteUtilizator = "DELETE FROM utilizatori WHERE ID_Utilizator = " + utilizatorID;
                        statement.execute(deleteAcreditari);
                        statement.executeUpdate(updatePacient);
                        statement.execute(deleteMedic);
                        statement.execute(deleteAngajat);
                        statement.execute(deleteDonator);
                        statement.execute(deleteUtilizator);
                        TableSuperAdmin.getItems().remove(selectedID.getSelectedIndex());
                    }
                }
                else if(selectedUser.getFunctie().equals("Asistent Medical")){
                    errorMessage.setText("");
                    String getAsistentID = "SELECT Angajat.ID_Utilizator, ID_Angajat FROM Angajat, Utilizatori WHERE Utilizatori.ID_Utilizator = Angajat.ID_Utilizator AND Email = '" + selectedUser.getEmail() + "'";
                    statement = connection.createStatement();
                    resultSet = statement.executeQuery(getAsistentID);
                    int angajatID = 0;
                    int utilizatorID = 0;
                    while(resultSet.next()){
                        angajatID = resultSet.getInt("ID_Angajat");
                        utilizatorID = resultSet.getInt("ID_Utilizator");
                    }
                    String deleteAsistent = "{CALL DeleteAsistent(?, ?)}";
                    callableStatement = connection.prepareCall(deleteAsistent);
                    callableStatement.setInt(1, utilizatorID);
                    callableStatement.setInt(2, angajatID);
                    callableStatement.execute();
                    TableSuperAdmin.getItems().remove(selectedID.getSelectedIndex());
                }
                else {
                    errorMessage.setText("");
                    String getAngajatID = "SELECT Angajat.ID_Utilizator FROM Angajat, Utilizatori WHERE Utilizatori.ID_Utilizator = Angajat.ID_Utilizator AND Email = '" + selectedUser.getEmail() + "'";
                    int utilizatorID = 0;
                    statement = connection.createStatement();
                    resultSet = statement.executeQuery(getAngajatID);
                    while(resultSet.next()){
                        utilizatorID = resultSet.getInt("ID_Utilizator");
                    }
                    String deleteAsistent = "{CALL DeleteAngajat(?)}";
                    callableStatement = connection.prepareCall(deleteAsistent);
                    callableStatement.setInt(1, utilizatorID);
                    callableStatement.execute();
                    TableSuperAdmin.getItems().remove(selectedID.getSelectedIndex());
                }
            }
            else{
                errorMessage.setText("Selecteaza un utilizator");
            }
        }catch (Exception ex){
            System.err.println("An SQL Exception occured. Details are provided below:");
            ex.printStackTrace(System.err);
        }
        finally {
            closeOperation(connection);
            closeOperation(preparedStatement);
            closeOperation(statement);
            closeOperation(resultSet);
        }
    }
    public void updateUser(ActionEvent e){
        Connection connection = null;
        DatabaseConnection connectionNow = new DatabaseConnection();
        connection = connectionNow.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        try{
            TableView.TableViewSelectionModel<UsersSA> selectedID = TableSuperAdmin.getSelectionModel();
            UsersSA selectedUser = selectedID.getSelectedItem();

            if(selectedUser != null){
                if(selectedUser.getFunctie().equals("Medic")){
                    errorMessage.setText("");
                    UserToUpdate.setEmail(selectedUser.getEmail());
                    FXMLLoader fxmlLoader = new FXMLLoader(SuperAdminController.class.getResource("UpdateMedic.fxml"));
                    Scene scene = new Scene(fxmlLoader.load(), 930, 780);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.show();
                }else if(selectedUser.getFunctie().equals("Asistent Medical")){
                    errorMessage.setText("");
                    UserToUpdate.setEmail(selectedUser.getEmail());
                    FXMLLoader fxmlLoader = new FXMLLoader(SuperAdminController.class.getResource("UpdateAsistent.fxml"));
                    Scene scene = new Scene(fxmlLoader.load(), 930, 780);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.show();
                }else if(selectedUser.getFunctie().equals("Pacient")) {
                    errorMessage.setText("");
                    UserToUpdate.setEmail(selectedUser.getEmail());
                    FXMLLoader fxmlLoader = new FXMLLoader(SuperAdminController.class.getResource("UpdatePacient.fxml"));
                    Scene scene = new Scene(fxmlLoader.load(), 520, 700);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.show();
                }else if(!selectedUser.getFunctie().equals("Super Admin") || selectedUser.getEmail().equals(UserData.getEmail())) {
                    errorMessage.setText("");
                    UserToUpdate.setEmail(selectedUser.getEmail());
                    FXMLLoader fxmlLoader = new FXMLLoader(SuperAdminController.class.getResource("UpdateAngajati.fxml"));
                    Scene scene = new Scene(fxmlLoader.load(), 410, 850);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.show();
                }else {
                    errorMessage.setText("Nu poti modifica un Super Admin");
                }
            }
            else{
                errorMessage.setText("Selecteaza un utilizator");
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            closeOperation(connection);
            closeOperation(statement);
            closeOperation(resultSet);
        }
    }

    @FXML
    public void refreshTable(ActionEvent e){
        list.clear();
        updateTable();
    }

    public void closeOperation(AutoCloseable operation){
        try {
            if (operation != null) {
                operation.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
