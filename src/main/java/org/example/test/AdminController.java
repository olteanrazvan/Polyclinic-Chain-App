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

import javax.swing.*;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class AdminController implements Initializable{
    @FXML
    private TableView<UsersSA> TableAdmin;

    @FXML
    private TableColumn<UsersSA, String> colAdresaA;

    @FXML
    private TableColumn<UsersSA, String> colCNPA;

    @FXML
    private TableColumn<UsersSA, String> colEmailA;

    @FXML
    private TableColumn<UsersSA, String> colFunctieA;

    @FXML
    private TableColumn<UsersSA, String> colIBANA;

    @FXML
    private TableColumn<UsersSA, String> colNrTelA;

    @FXML
    private TableColumn<UsersSA, String> colNumeA;

    @FXML
    private TableColumn<UsersSA, String> colPrenumeA;

    @FXML
    private TableColumn<UsersSA, String> colPoliclinicaA;

    @FXML
    private Label errorMessage;

    ObservableList<UsersSA> list;

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
                FXMLLoader fxmlLoader = new FXMLLoader(AdminController.class.getResource("DoneazaSange.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 600, 430);
                Stage stageDS = new Stage();
                stageDS.setScene(scene);
                stageDS.show();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @FXML
    protected void logOutButtonAction(ActionEvent e){
        HelloController.logOutButtonAction(e, AdminController.class);
    }
    public void getUsers(){
        Connection connection = null;
        Statement selectStatement = null;
        ResultSet resultSet = null;
        ObservableList<UsersSA> tempList = FXCollections.observableArrayList();

        String getAllData  = "SELECT * FROM utilizatori, policlinici WHERE policlinici.ID_Policlinici = utilizatori.ID_Policlinici";

        DatabaseConnection connectNow = new DatabaseConnection();
        connection = connectNow.getConnection();

        try{
            selectStatement = connection.createStatement();
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
        colNumeA.setCellValueFactory(new PropertyValueFactory<UsersSA, String>("Nume"));
        colPrenumeA.setCellValueFactory(new PropertyValueFactory<UsersSA, String>("Prenume"));
        colEmailA.setCellValueFactory(new PropertyValueFactory<UsersSA, String>("Email"));
        colAdresaA.setCellValueFactory(new PropertyValueFactory<UsersSA, String>("Adresa"));
        colNrTelA.setCellValueFactory(new PropertyValueFactory<UsersSA, String>("NumarTelefon"));
        colCNPA.setCellValueFactory(new PropertyValueFactory<UsersSA, String>("CNP"));
        colIBANA.setCellValueFactory(new PropertyValueFactory<UsersSA, String>("IBAN"));
        colFunctieA.setCellValueFactory(new PropertyValueFactory<UsersSA, String>("Functie"));
        colPoliclinicaA.setCellValueFactory(new PropertyValueFactory<UsersSA, String>("Policlinica"));
        getUsers();
        TableAdmin.setItems(list);
    }
    public void addUser(ActionEvent e){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(AdminController.class.getResource("AddUserAdmin.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 410, 670);
            Stage stageAA = new Stage();
            stageAA.setScene(scene);
            stageAA.show();
        }catch (Exception ex){
        }
    }
    public void removeUser(ActionEvent e){
        Connection connection;
        PreparedStatement preparedStatement = null;
        Statement statement = null;
        ResultSet resultSet = null;
        String userToDelete = "DELETE FROM Utilizatori WHERE ID_Utilizator = ?";
        String verifyPolyclinicID = "SELECT * FROM Utilizatori WHERE Email = ?";
        String verifyAdminPolyclinicID = "SELECT * FROM Utilizatori WHERE Email = '" + UserData.getEmail() + "'";

        DatabaseConnection connectNow = new DatabaseConnection();
        connection = connectNow.getConnection();

        try{
            TableView.TableViewSelectionModel<UsersSA> selectedID = TableAdmin.getSelectionModel();
            UsersSA selectedUser = selectedID.getSelectedItem();
            if(selectedUser != null){
                if(selectedUser.getFunctie().equals("Admin")){
                    errorMessage.setText("Nu poti sterge un Admin");
                }
                else if(selectedUser.getFunctie().equals("Super Admin")){
                    errorMessage.setText("Nu poti sterge un Super Admin");
                }
                else{
                    int userPolyclinicID;
                    int userID;
                    int AdminPolyclinicID = 0;
                    statement = connection.createStatement();
                    statement.execute(verifyAdminPolyclinicID);
                    resultSet = statement.executeQuery(verifyAdminPolyclinicID);
                    while(resultSet.next()){
                        AdminPolyclinicID = resultSet.getInt("ID_Policlinici");
                    }
                    preparedStatement = connection.prepareStatement(verifyPolyclinicID);
                    preparedStatement.setString(1, selectedUser.getEmail());
                    resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        userPolyclinicID = resultSet.getInt("ID_Policlinici");
                        userID = resultSet.getInt("ID_Utilizator");
                        if(AdminPolyclinicID == userPolyclinicID){
                            errorMessage.setText("");
                            TableAdmin.getItems().remove(selectedID.getSelectedIndex());
                            preparedStatement = connection.prepareStatement(userToDelete);
                            preparedStatement.setInt(1, userID);
                            preparedStatement.execute();
                        }
                        else{
                            errorMessage.setText("Nu poti sterge un utilizator de la o alta policlinica");
                        }
                    }
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
            TableView.TableViewSelectionModel<UsersSA> selectedID = TableAdmin.getSelectionModel();
            UsersSA selectedUser = selectedID.getSelectedItem();

            if(selectedUser != null){
                String verifyAdminPolyclinicID = "SELECT * FROM Utilizatori WHERE Email = '" + UserData.getEmail() + "'";
                String verifyUserPolyclinicID = "SELECT * FROM Utilizatori WHERE Email = '" + selectedUser.getEmail() + "'";
                int AdminPolyclinicID = 0;
                int userPolyclinicID = 0;

                statement = connection.createStatement();
                resultSet = statement.executeQuery(verifyAdminPolyclinicID);
                while(resultSet.next()){
                    AdminPolyclinicID = resultSet.getInt("ID_Policlinici");
                }

                statement = connection.createStatement();
                resultSet = statement.executeQuery(verifyUserPolyclinicID);
                while(resultSet.next()){
                    userPolyclinicID = resultSet.getInt("ID_Policlinici");
                }

                if(AdminPolyclinicID == userPolyclinicID){
                    if(selectedUser.getFunctie().equals("Admin")){
                        errorMessage.setText("Nu poti modifica un Admin");
                    }
                    else if(selectedUser.getFunctie().equals("Super Admin")){
                        errorMessage.setText("Nu poti modifica un Super Admin");
                    }
                    else{
                        errorMessage.setText("");
                        UserToUpdate.setEmail(selectedUser.getEmail());
                        FXMLLoader fxmlLoader = new FXMLLoader(AdminController.class.getResource("UpdateUserAdmin.fxml"));
                        Scene scene = new Scene(fxmlLoader.load(), 410, 670);
                        Stage stageUA = new Stage();
                        stageUA.setScene(scene);
                        stageUA.show();
                    }
                }
                else{
                    errorMessage.setText("Nu poti modifica un utilizator de la o alta policlinica");
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
