package com.lingyuan.simplesql.ui.controller;

import com.lingyuan.simplesql.common.db.TableDatabaseHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.text.SimpleDateFormat;
import java.util.List;

public class TableDatabaseViewController extends BaseController {

    @FXML private TextField tableNameField;
    @FXML private TextField databaseNameField;
    @FXML private Button addBtn;
    @FXML private Button clearBtn;
    
    @FXML private TextField searchTableNameField;
    @FXML private TextField searchDatabaseNameField;
    @FXML private Button searchBtn;
    @FXML private Button resetBtn;
    
    @FXML private TableView<TableDatabaseHelper.TableDatabaseInfo> tableView;
    @FXML private TableColumn<TableDatabaseHelper.TableDatabaseInfo, Integer> idColumn;
    @FXML private TableColumn<TableDatabaseHelper.TableDatabaseInfo, String> tableNameColumn;
    @FXML private TableColumn<TableDatabaseHelper.TableDatabaseInfo, String> databaseNameColumn;
    @FXML private TableColumn<TableDatabaseHelper.TableDatabaseInfo, java.sql.Timestamp> createTimeColumn;
    @FXML private TableColumn<TableDatabaseHelper.TableDatabaseInfo, java.sql.Timestamp> updateTimeColumn;
    @FXML private TableColumn<TableDatabaseHelper.TableDatabaseInfo, Void> actionColumn;

    private TableDatabaseHelper dbHelper;
    private ObservableList<TableDatabaseHelper.TableDatabaseInfo> dataList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
        updateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("updateTime"));
        
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
        
        updateTimeColumn.setCellFactory(column -> new TableCell<TableDatabaseHelper.TableDatabaseInfo, java.sql.Timestamp>() {
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
        
        // 设置删除按钮
        actionColumn.setCellFactory(column -> new TableCell<TableDatabaseHelper.TableDatabaseInfo, Void>() {
            private final Button deleteBtn = new Button("删除");
            
            {
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                deleteBtn.setOnAction(event -> {
                    TableDatabaseHelper.TableDatabaseInfo info = getTableView().getItems().get(getIndex());
                    deleteRecord(info);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteBtn);
                }
            }
        });
        
        tableView.setItems(dataList);
        
        // 设置表格自动调整大小
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        
        // 设置表格的最小高度
        tableView.setMinHeight(200);
    }

    private void initButtonEvents() {
        // 添加按钮事件
        addBtn.setOnAction(e -> addRecord());
        
        // 清空按钮事件
        clearBtn.setOnAction(e -> clearFields());
        
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

    private void addRecord() {
        String tableName = tableNameField.getText().trim();
        String databaseName = databaseNameField.getText().trim();
        
        if (tableName.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "表名不能为空", "请输入表名", null);
            return;
        }
        
        try {
            dbHelper.addTableDatabase(tableName, databaseName);
            showAlert(Alert.AlertType.INFORMATION, "添加成功", "记录已成功添加到数据库", null);
            clearFields();
            loadData();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "添加失败", "无法添加记录到数据库", e.getMessage());
        }
    }

    private void deleteRecord(TableDatabaseHelper.TableDatabaseInfo info) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("确认删除");
        confirm.setHeaderText(null);
        confirm.setContentText("确定要删除表名 '" + info.getTableName() + "' 的记录吗？");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    dbHelper.deleteTableDatabase(info.getId());
                    showAlert(Alert.AlertType.INFORMATION, "删除成功", "记录已成功删除", null);
                    loadData();
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "删除失败", "无法删除记录", e.getMessage());
                }
            }
        });
    }

    private void clearFields() {
        tableNameField.clear();
        databaseNameField.clear();
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
} 