package com.lingyuan.simplesql.ui.controller;

import com.lingyuan.simplesql.common.db.TableDatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.text.SimpleDateFormat;
import java.util.List;

public class TableDatabaseSelectionDialogController extends BaseController {

    @FXML private TextField searchTableNameField;
    @FXML private TextField searchDatabaseNameField;
    @FXML private Button searchBtn;
    @FXML private Button resetBtn;
    
    @FXML private TableView<TableDatabaseHelper.TableDatabaseInfo> tableView;
    @FXML private TableColumn<TableDatabaseHelper.TableDatabaseInfo, Integer> idColumn;
    @FXML private TableColumn<TableDatabaseHelper.TableDatabaseInfo, String> tableNameColumn;
    @FXML private TableColumn<TableDatabaseHelper.TableDatabaseInfo, String> databaseNameColumn;
    @FXML private TableColumn<TableDatabaseHelper.TableDatabaseInfo, java.sql.Timestamp> createTimeColumn;

    private TableDatabaseHelper dbHelper;
    private ObservableList<TableDatabaseHelper.TableDatabaseInfo> dataList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    // 选择结果
    private TableDatabaseHelper.TableDatabaseInfo selectedInfo;

    @FXML
    public void initialize() {
        dbHelper = new TableDatabaseHelper();
        dataList = FXCollections.observableArrayList();
        
        // 初始化表格
        initTableView();
        
        // 初始化按钮事件
        initButtonEvents();
        
        // 加载数据
        loadData();
    }

    private void initTableView() {
        // 设置表格列
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableNameColumn.setCellValueFactory(new PropertyValueFactory<>("tableName"));
        databaseNameColumn.setCellValueFactory(new PropertyValueFactory<>("databaseName"));
        createTimeColumn.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        
        // 格式化时间显示
        createTimeColumn.setCellFactory(column -> new TableCell<TableDatabaseHelper.TableDatabaseInfo, java.sql.Timestamp>() {
            @Override
            protected void updateItem(java.sql.Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(dateFormat.format(item));
                }
            }
        });
        
        // 设置表格选择事件
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedInfo = newSelection;
        });
        
        // 设置双击选择事件
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                TableDatabaseHelper.TableDatabaseInfo selected = tableView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    selectedInfo = selected;
                    // 直接关闭对话框，结果会在getSelectedInfo()中返回
                    Stage stage = (Stage) tableView.getScene().getWindow();
                    stage.close();
                }
            }
        });
        
        tableView.setItems(dataList);
        
        // 设置表格自动调整大小
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        
        // 设置表格的最小高度
        tableView.setMinHeight(150);
    }

    private void initButtonEvents() {
        // 搜索按钮事件
        searchBtn.setOnAction(e -> searchRecords());
        
        // 重置按钮事件
        resetBtn.setOnAction(e -> resetSearch());
    }

    private void loadData() {
        try {
            List<TableDatabaseHelper.TableDatabaseInfo> records = dbHelper.getAllTableDatabase();
            dataList.clear();
            dataList.addAll(records);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "加载数据失败", "无法从数据库加载数据", e.getMessage());
        }
    }

    private void searchRecords() {
        String tableName = searchTableNameField.getText().trim();
        String databaseName = searchDatabaseNameField.getText().trim();
        
        try {
            List<TableDatabaseHelper.TableDatabaseInfo> records;
            
            if (!tableName.isEmpty() && !databaseName.isEmpty()) {
                // 两个字段都有值，先按表名搜索，再按库名过滤
                records = dbHelper.searchByTableName(tableName);
                records = records.stream()
                    .filter(info -> info.getDatabaseName() != null && 
                                  info.getDatabaseName().toLowerCase().contains(databaseName.toLowerCase()))
                    .toList();
            } else if (!tableName.isEmpty()) {
                records = dbHelper.searchByTableName(tableName);
            } else if (!databaseName.isEmpty()) {
                records = dbHelper.searchByDatabaseName(databaseName);
            } else {
                records = dbHelper.getAllTableDatabase();
            }
            
            dataList.clear();
            dataList.addAll(records);
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "搜索失败", "无法搜索记录", e.getMessage());
        }
    }

    private void resetSearch() {
        searchTableNameField.clear();
        searchDatabaseNameField.clear();
        loadData();
    }



    /**
     * 获取选择的记录
     */
    public TableDatabaseHelper.TableDatabaseInfo getSelectedInfo() {
        return selectedInfo;
    }
} 