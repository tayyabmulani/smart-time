package smarttime.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import smarttime.model.Task;
import smarttime.service.TaskService;

public class SampleDataLoader {

    /**
     * Load tasks from a text file on disk.
     * Format (semicolon separated, one task per line):
     *
     * id;title;course;dueDate;minutes;difficulty;prereqIds
     *
     * prereqIds is a comma-separated list of task IDs or empty.
     */
    public static void loadFromFile(TaskService taskService, Path file) {
        Map<Integer, Task> idToTask = new HashMap<>();
        List<PendingDeps> pendingDeps = new ArrayList<>();

        try (BufferedReader reader =
                     Files.newBufferedReader(file, StandardCharsets.UTF_8)) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // skip comments / blank lines
                }

                String[] parts = line.split(";", -1);
                if (parts.length < 6) {
                    System.err.println("Skipping malformed line: " + line);
                    continue;
                }

                int id = Integer.parseInt(parts[0].trim());
                String title = parts[1].trim();
                String course = parts[2].trim();
                LocalDate dueDate = LocalDate.parse(parts[3].trim());
                int minutes = Integer.parseInt(parts[4].trim());
                int difficulty = Integer.parseInt(parts[5].trim());

                Task task = new Task(id, title, course, dueDate, minutes, difficulty);
                taskService.addTask(task);
                idToTask.put(id, task);

                // optional prereq IDs in column 7
                if (parts.length >= 7 && !parts[6].trim().isEmpty()) {
                    String[] prereqIds = parts[6].split(",");
                    List<Integer> ids = new ArrayList<>();
                    for (String s : prereqIds) {
                        s = s.trim();
                        if (!s.isEmpty()) {
                            ids.add(Integer.parseInt(s));
                        }
                    }
                    pendingDeps.add(new PendingDeps(id, ids));
                }
            }

        } catch (IOException ex) {
            System.err.println("Failed to load tasks from " + file + ": " + ex.getMessage());
            return;
        }

        // Second pass: hook up prerequisites using IDs
        for (PendingDeps pd : pendingDeps) {
            Task target = idToTask.get(pd.taskId);
            if (target == null) continue;
            for (int prereqId : pd.prereqIds) {
                Task prereq = idToTask.get(prereqId);
                if (prereq != null) {
                    taskService.addDependency(prereq, target);
                }
            }
        }
    }

    /**
     * Helper to load tasks.txt from the classpath.
     * Expected path for tasks.txt: /smarttime/util/tasks.txt
     */
    public static void loadFromResource(TaskService taskService, String resourcePath) {
        try {
            var url = SampleDataLoader.class.getResource(resourcePath);
            if (url == null) {
                System.err.println("Resource not found: " + resourcePath);
                return;
            }
            Path path = Path.of(url.toURI());
            loadFromFile(taskService, path);
        } catch (URISyntaxException e) {
            System.err.println("Failed to load resource " + resourcePath + ": " + e.getMessage());
        }
    }

    private static class PendingDeps {
        final int taskId;
        final List<Integer> prereqIds;

        PendingDeps(int taskId, List<Integer> prereqIds) {
            this.taskId = taskId;
            this.prereqIds = prereqIds;
        }
    }
}