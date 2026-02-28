package com.example.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    ListView listViewTasks;
    ArrayAdapter<String> adapter;
    List<Task> tasks = new ArrayList<>();
    int selectedTaskId = -1;

    EditText etTitle, etDesc, etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* ===== Навигация по экранам ===== */
        ListView lvScreens = findViewById(R.id.lvScreens);

        String[] screens = {
                "Открыть профиль",
                "Открыть экран с расчётом",
                "Открыть экран настроек"
        };

        ArrayAdapter<String> screensAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                screens
        );

        lvScreens.setAdapter(screensAdapter);

        lvScreens.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else if (position == 1) {
                startActivity(new Intent(this, CalcActivity.class));
            } else if (position == 2) {
                startActivity(new Intent(this, SettingsActivity.class));
            }
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        /* ===== SQLite ===== */
        dbHelper = new DatabaseHelper(this);
        listViewTasks = findViewById(R.id.listViewTasks);

        etTitle = findViewById(R.id.etTitle);
        etDesc = findViewById(R.id.etDesc);
        etSearch = findViewById(R.id.etSearch);

        // Добавить задачу
        findViewById(R.id.btnAdd).setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "Введите название задачи", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.addTask(title, desc)) {
                Toast.makeText(this, "Задача добавлена!", Toast.LENGTH_SHORT).show();
                refreshList();
                etTitle.setText("");
                etDesc.setText("");
            } else {
                Toast.makeText(this, "Ошибка при добавлении", Toast.LENGTH_SHORT).show();
            }
        });

        // Обновить список
        findViewById(R.id.btnRefresh).setOnClickListener(v -> refreshList());

        // Выбор задачи
        listViewTasks.setOnItemClickListener((parent, view, position, id) -> {
            selectedTaskId = tasks.get(position).getId();
            Toast.makeText(this,
                    "Выбрана задача ID: " + selectedTaskId,
                    Toast.LENGTH_SHORT).show();
        });

        // Редактировать выбранную задачу
        findViewById(R.id.btnEdit).setOnClickListener(v -> {
            if (selectedTaskId == -1) {
                Toast.makeText(this, "Сначала выберите задачу", Toast.LENGTH_SHORT).show();
                return;
            }

            // Заполняем EditText выбранной задачей
            for (Task task : tasks) {
                if (task.getId() == selectedTaskId) {
                    etTitle.setText(task.getTitle());
                    etDesc.setText(task.getDescription());
                    break;
                }
            }

            // После изменения, btnAdd обновит задачу
            findViewById(R.id.btnAdd).setOnClickListener(v2 -> {
                String newTitle = etTitle.getText().toString().trim();
                String newDesc = etDesc.getText().toString().trim();

                if (newTitle.isEmpty()) {
                    Toast.makeText(this, "Введите название задачи", Toast.LENGTH_SHORT).show();
                    return;
                }

                Task updatedTask = new Task();
                updatedTask.setId(selectedTaskId);
                updatedTask.setTitle(newTitle);
                updatedTask.setDescription(newDesc);

                if (dbHelper.updateTask(updatedTask)) {
                    Toast.makeText(this, "Задача обновлена!", Toast.LENGTH_SHORT).show();
                    refreshList();
                    etTitle.setText("");
                    etDesc.setText("");
                    selectedTaskId = -1;
                }
            });
        });

        // Поиск задач
        findViewById(R.id.btnSearch).setOnClickListener(v -> {
            String query = etSearch.getText().toString().trim();
            tasks.clear();
            tasks.addAll(dbHelper.searchTasks(query));
            refreshList();
        });

        // Удаление задачи
        findViewById(R.id.btnDeleteSelected).setOnClickListener(v -> {
            if (selectedTaskId == -1) {
                Toast.makeText(this, "Сначала выберите задачу", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.deleteTask(selectedTaskId)) {
                Toast.makeText(this, "Задача удалена!", Toast.LENGTH_SHORT).show();
                refreshList();
                selectedTaskId = -1;
            } else {
                Toast.makeText(this, "Ошибка при удалении", Toast.LENGTH_SHORT).show();
            }
        });

        // Загрузка данных при старте
        refreshList();
    }

    // 🔹 refreshList с сортировкой по title
    private void refreshList() {
        tasks.clear();
        // Используем метод с сортировкой по названию
        tasks.addAll(dbHelper.getAllTasksSorted());

        List<String> taskTitles = new ArrayList<>();
        for (Task task : tasks) {
            taskTitles.add(task.getTitle() + " (" + task.getDescription() + ")");
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, taskTitles);
        listViewTasks.setAdapter(adapter);
    }
}