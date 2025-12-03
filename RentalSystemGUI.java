import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;

public class RentalSystemGUI extends Application {

    private RentalSystem rentalSystem;

    private TextField txtCustomerId;
    private TextField txtCustomerName;

    private TextField txtPlate;
    private TextField txtMake;
    private TextField txtModel;
    private TextField txtYear;

    private ComboBox<String> cmbCustomerIds;
    private ComboBox<String> cmbVehiclePlates;
    private TextField txtAmount;

    private ListView<String> lstAvailableVehicles;
    private ListView<String> lstHistory;

    private ObservableList<String> customerIdItems;
    private ObservableList<String> vehiclePlateItems;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        rentalSystem = RentalSystem.getInstance();

        customerIdItems = FXCollections.observableArrayList();
        vehiclePlateItems = FXCollections.observableArrayList();

        for (Customer c : rentalSystem.getCustomers()) {
            customerIdItems.add(String.valueOf(c.getCustomerId()));
        }
        for (Vehicle v : rentalSystem.getVehicles()) {
            vehiclePlateItems.add(v.getLicensePlate());
        }

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // Add Customer 
        Label lblCustomer = new Label("Add Customer");
        HBox customerRow = new HBox(5);
        txtCustomerId = new TextField();
        txtCustomerName = new TextField();
        txtCustomerId.setPromptText("ID");
        txtCustomerName.setPromptText("Name");
        Button btnAddCustomer = new Button("Add Customer");
        btnAddCustomer.setOnAction(e -> addCustomer());
        customerRow.getChildren().addAll(
                new Label("ID:"), txtCustomerId,
                new Label("Name:"), txtCustomerName,
                btnAddCustomer
        );

        //  Add Vehicle section 
        Label lblVehicle = new Label("Add Vehicle (Car only)");
        HBox vehicleRow = new HBox(5);
        txtPlate = new TextField();
        txtMake = new TextField();
        txtModel = new TextField();
        txtYear = new TextField();
        txtPlate.setPromptText("Plate");
        txtMake.setPromptText("Make");
        txtModel.setPromptText("Model");
        txtYear.setPromptText("Year");
        Button btnAddVehicle = new Button("Add Vehicle");
        btnAddVehicle.setOnAction(e -> addVehicle());
        vehicleRow.getChildren().addAll(
                new Label("Plate:"), txtPlate,
                new Label("Make:"), txtMake,
                new Label("Model:"), txtModel,
                new Label("Year:"), txtYear,
                btnAddVehicle
        );

        // Rent / Return 
        Label lblRentReturn = new Label("Rent / Return Vehicle");
        HBox rentRow1 = new HBox(5);
        cmbCustomerIds = new ComboBox<>(customerIdItems);
        cmbVehiclePlates = new ComboBox<>(vehiclePlateItems);
        cmbCustomerIds.setPromptText("Customer ID");
        cmbVehiclePlates.setPromptText("Vehicle Plate");
        rentRow1.getChildren().addAll(
                new Label("Customer:"), cmbCustomerIds,
                new Label("Vehicle:"), cmbVehiclePlates
        );

        HBox rentRow2 = new HBox(5);
        txtAmount = new TextField();
        txtAmount.setPromptText("Amount / Fees");
        Button btnRent = new Button("Rent");
        Button btnReturn = new Button("Return");
        btnRent.setOnAction(e -> rentVehicle());
        btnReturn.setOnAction(e -> returnVehicle());
        rentRow2.getChildren().addAll(
                new Label("Amount:"), txtAmount,
                btnRent, btnReturn
        );

        // Display 
        HBox displayRow = new HBox(10);

        VBox leftBox = new VBox(5);
        Label lblAvailable = new Label("Available Vehicles");
        lstAvailableVehicles = new ListView<>();
        leftBox.getChildren().addAll(lblAvailable, lstAvailableVehicles);

        VBox rightBox = new VBox(5);
        Label lblHistory = new Label("Rental History");
        lstHistory = new ListView<>();
        rightBox.getChildren().addAll(lblHistory, lstHistory);

        displayRow.getChildren().addAll(leftBox, rightBox);

        root.getChildren().addAll(
                lblCustomer, customerRow,
                lblVehicle, vehicleRow,
                lblRentReturn, rentRow1, rentRow2,
                displayRow
        );

        refreshAvailableVehicles();
        refreshHistory();

        Scene scene = new Scene(root, 900, 500);
        primaryStage.setTitle("Vehicle Rental System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addCustomer() {
        try {
            String idText = txtCustomerId.getText().trim();
            String name = txtCustomerName.getText().trim();

            if (idText.isEmpty() || name.isEmpty()) {
                showAlert("Error", "Both ID and Name are required.");
                return;
            }

            int id = Integer.parseInt(idText);
            Customer customer = new Customer(id, name);

            boolean added = rentalSystem.addCustomer(customer);
            if (added) {
                customerIdItems.add(String.valueOf(id));
                showAlert("Success", "Customer added.");
                txtCustomerId.clear();
                txtCustomerName.clear();
            } else {
                showAlert("Error", "Customer ID already exists.");
            }
        } catch (NumberFormatException ex) {
            showAlert("Error", "ID must be a number.");
        }
    }

    private void addVehicle() {
        try {
            String plate = txtPlate.getText().trim();
            String make = txtMake.getText().trim();
            String model = txtModel.getText().trim();
            String yearText = txtYear.getText().trim();

            if (plate.isEmpty() || make.isEmpty() || model.isEmpty() || yearText.isEmpty()) {
                showAlert("Error", "All vehicle fields are required.");
                return;
            }

            int year = Integer.parseInt(yearText);
            Vehicle vehicle = new Car(make, model, year, 5);

            vehicle.setLicensePlate(plate);

            boolean added = rentalSystem.addVehicle(vehicle);
            if (added) {
                vehiclePlateItems.add(plate);
                showAlert("Success", "Vehicle added.");
                txtPlate.clear();
                txtMake.clear();
                txtModel.clear();
                txtYear.clear();
                refreshAvailableVehicles();
            } else {
                showAlert("Error", "Vehicle plate already exists.");
            }

        } catch (NumberFormatException ex) {
            showAlert("Error", "Year must be a number.");
        } catch (IllegalArgumentException ex) {
            showAlert("Error", "Invalid plate: " + ex.getMessage());
        }
    }

    private void rentVehicle() {
        String customerIdText = cmbCustomerIds.getValue();
        String plate = cmbVehiclePlates.getValue();
        String amountText = txtAmount.getText().trim();

        if (customerIdText == null || plate == null || amountText.isEmpty()) {
            showAlert("Error", "Select customer, vehicle, and enter amount.");
            return;
        }

        try {
            int id = Integer.parseInt(customerIdText);
            double amount = Double.parseDouble(amountText);

            Customer customer = rentalSystem.findCustomerById(id);
            Vehicle vehicle = rentalSystem.findVehicleByPlate(plate);

            if (customer == null || vehicle == null) {
                showAlert("Error", "Customer or vehicle not found.");
                return;
            }

            boolean success = rentalSystem.rentVehicle(vehicle, customer, LocalDate.now(), amount);
            if (success) {
                showAlert("Success", "Vehicle rented.");
                refreshAvailableVehicles();
                refreshHistory();
            } else {
                showAlert("Error", "Vehicle is not available.");
            }

        } catch (NumberFormatException ex) {
            showAlert("Error", "Amount must be a number.");
        }
    }

    private void returnVehicle() {
        String customerIdText = cmbCustomerIds.getValue();
        String plate = cmbVehiclePlates.getValue();
        String amountText = txtAmount.getText().trim();

        if (customerIdText == null || plate == null || amountText.isEmpty()) {
            showAlert("Error", "Select customer, vehicle, and enter fees (0 if none).");
            return;
        }

        try {
            int id = Integer.parseInt(customerIdText);
            double fees = Double.parseDouble(amountText);

            Customer customer = rentalSystem.findCustomerById(id);
            Vehicle vehicle = rentalSystem.findVehicleByPlate(plate);

            if (customer == null || vehicle == null) {
                showAlert("Error", "Customer or vehicle not found.");
                return;
            }

            boolean success = rentalSystem.returnVehicle(vehicle, customer, LocalDate.now(), fees);
            if (success) {
                showAlert("Success", "Vehicle returned.");
                refreshAvailableVehicles();
                refreshHistory();
            } else {
                showAlert("Error", "Vehicle is not currently rented.");
            }

        } catch (NumberFormatException ex) {
            showAlert("Error", "Fees must be a number.");
        }
    }

    private void refreshAvailableVehicles() {
        ObservableList<String> items = FXCollections.observableArrayList();
        for (Vehicle v : rentalSystem.getVehicles()) {
            if (v.getStatus() == Vehicle.VehicleStatus.Available) {
                items.add(v.getLicensePlate() + " - " +
                          v.getMake() + " " + v.getModel() +
                          " (" + v.getYear() + ")");
            }
        }
        lstAvailableVehicles.setItems(items);
    }

    private void refreshHistory() {
        ObservableList<String> items = FXCollections.observableArrayList();
        for (RentalRecord r : rentalSystem.getRentalRecords()) {
            items.add(r.getRecordType() + " | " +
                      r.getVehicle().getLicensePlate() + " | " +
                      r.getCustomer().getCustomerName() + " | " +
                      r.getRecordDate() + " | $" +
                      String.format("%.2f", r.getTotalAmount()));
        }
        lstHistory.setItems(items);
    }

    private void showAlert(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}
