package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangelistener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationExceptions;
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
	
//	CONTROLE DOS BOTÕES SAVE E CANCEL;
	
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
			Utils.currentStage(event).close();
		}catch (ValidationExceptions e) {
			setErrorMessages(e.getErrors());
		}
		catch (DbException e) {
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
	
	ValidationExceptions exception = new ValidationExceptions("Validation error");
	
	obj.setId(Utils.tryParseToInt(txtId.getText()));
	
	if (txtName.getText() == null || txtName.getText().trim().equals("")) {
		exception.addError("name", "Field can't be enmpty");
	}
	obj.setName(txtName.getText());
	
	if (exception.getErrors().size() > 0) {
		throw exception;
	}
	return obj;
}

	@FXML
	public void onBtCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
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
	
//	INICIALIZAÇÃO
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}
	
//	 # MÉTODO PARA POPULAR AS CAIXAS DE TEXTO DO FORMULÁRIO #
	
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entity was null");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}

	// # MÉTODO PARA ESCREVER OS ERROS NA TELA #
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if (fields.contains("name")) {
			labelErrorName.setText(errors.get("name"));
		}
	}

}
