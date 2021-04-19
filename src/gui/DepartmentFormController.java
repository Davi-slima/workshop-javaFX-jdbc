package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import db.DbException;
import gui.listeners.DataChangelistener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {
	
	private Department entity;
	private DepartmentService service;
	private List<DataChangelistener> dataChangeListeners = new ArrayList<>();
	
	@FXML private TextField txtId;
	@FXML private TextField txtName;
	@FXML private Label labelErrorName;
	@FXML private Button btSave;
	@FXML private Button btCancel;
	
//	CONTROLE DOS BOT�ES SAVE E CANCEL;
	
	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChancheListeners();
			utils.currentStage(event).close();
		}catch (DbException e) {
			Alerts.showAlerts("Error saving object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void notifyDataChancheListeners() {
		for (DataChangelistener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
}

	private Department getFormData() {
	Department obj = new Department();
	
	obj.setId(utils.tryParseToInt(txtId.getText()));
	obj.setName(txtName.getText());
	return obj;
}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		utils.currentStage(event).close();
	}
	
	// # DEPENDENCIAS #
	
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	public void subscribeDataChangeListener(DataChangelistener listener) {
		dataChangeListeners.add(listener);
	}
	
//	INICIALIZA��O
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}
	
//	 # M�TODO PARA POPULAR AS CAIXAS DE TEXTO DO FORMUL�RIO #
	
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}


}