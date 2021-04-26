package gui;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangelistener;
import gui.util.Alerts;
import gui.util.utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangelistener {

	private SellerService service;
	@FXML private TableView<Seller> tableViewSeller;
	@FXML private TableColumn<Seller, Integer> tableColumnId;
	@FXML private TableColumn<Seller, String> tableColumnName;
	@FXML private TableColumn<Seller, Seller> tableColumnEDIT;
	@FXML private TableColumn<Seller, Seller> tableColumnREMOVE;
	@FXML private Button btNew;
	private ObservableList<Seller> obsList;

//	A��O DO BOT�O NEW

	@FXML
	public void onBtNewAction(ActionEvent event) {
		Stage parentStage = utils.currentStage(event);
		Seller obj = new Seller();
		createDialogForm(obj, "/gui/SellerForm.fxml", parentStage);
	}

//	INJE��O DE CONTROLE DE DEPEND�NCIA MANUAl

	public void setSellerService(SellerService service) {
		this.service = service;
	}

	@Override
	public void initialize(URL url, ResourceBundle rs) {
		initializeNodes();
	}

	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
	}

	public void updateTableView() {
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Seller> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewSeller.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}

	// # CAIXA DE DI�LOGO (FORMUL�RIO DE DEPARTAMENTO) #

	private void createDialogForm(Seller obj, String absolutName, Stage parentStage) {
//		try {
//			FXMLLoader loader = new FXMLLoader(getClass().getResource(absolutName));
//			Pane pane = loader.load();
//
//			SellerFormController controller = loader.getController();
//			controller.setSeller(obj);
//			controller.setSellerService(new SellerService());
//			controller.subscribeDataChangeListener(this);
//			controller.updateFormData();
//
//			Stage dialogStage = new Stage();
//			dialogStage.setTitle("Enter department data");
//			dialogStage.setScene(new Scene(pane));
//			dialogStage.setResizable(false);
//			dialogStage.initOwner(parentStage);
//			dialogStage.initModality(Modality.WINDOW_MODAL);
//			dialogStage.showAndWait();
//		} catch (IOException e) {
//			Alerts.showAlerts("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
//		}
	}

	// # ATUALIZA��O DOS DADOS DO FORMUL�RIO NA TELA #

	@Override
	public void onDataChanged() {
		updateTableView();
	}

//		M�TODO PARA CRIAR UM BOT�O DE EKDI��O EM CADA LINHA DA TABELA

	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("EDIT");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(obj, "/gui/SellerForm.fxml", utils.currentStage(event)));
			}
		});
	}

//		M�TODO PARA CRIAR UM BOT�O DE REMOVER EM CADA LINHA DA TABELA

	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});
	}

	private void removeEntity(Seller obj) {
		Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");

		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Service was null");
			}
			try {
				service.remove(obj);
				updateTableView();
			}
			catch (DbIntegrityException e) {
				Alerts.showAlerts("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}

}
