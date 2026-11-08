# TaskPro – Student Planner (COMP3074, Team P34)

Android app that helps college students keep track of courses, assignments, quizzes and deadlines.
Built with **Kotlin, Jetpack Compose (Material 3), Navigation Compose and Room**. All data is stored on the device and works offline.

**Team:** Martins Maduabuchi · Hooman Abedi · Bak Aghedo · Winthrop Oviosun

## Functional prototype – implemented features

| ID | Requirement | Status | Where |
|----|-------------|--------|-------|
| FR-01 | Create, edit, delete courses | ✅ | Courses tab, Course details |
| FR-02 | Create, edit, delete tasks | ✅ | + button, Task details |
| FR-03 | Task linked to a course, with due date & time | ✅ | Add/Edit Task (course dropdown, date & time pickers) |
| FR-04 | Priority Low / Medium / High | ✅ | Add/Edit Task |
| FR-05 | Mark complete / incomplete | ✅ | Checkbox on any task, Task details |
| FR-06 | Upcoming incomplete tasks on dashboard | ✅ | Home (Due soon / To do / Overdue counters + next 5 tasks) |
| FR-07 | Tasks for a selected course | ✅ | Course details |
| FR-08 | Calendar / deadline view | ✅ | Calendar tab (month grid, dots on days with deadlines) |
| FR-09 | Filter & sort tasks | ✅ | Tasks tab (status + course filters, sort by date/priority/course/title) |
| FR-10 | Local reminder notifications | ✅ | Reminder option per task, on/off in Settings |
| FR-11 | Dark theme | ✅ | Settings |
| FR-12 | Study-progress statistics | ✅ | Home – overall and per-course progress |

Also included: input validation, delete confirmations, an overdue warning, a "Load sample data" button for demos, and a name shown on the dashboard.

## Project structure

```
app/src/main/java/com/example/taskpro/
├── MainActivity.kt            – creates database, settings, ViewModel; applies theme
├── data/
│   ├── Course.kt, Task.kt     – Room entities (1 course → many tasks, cascade delete)
│   ├── CourseDao.kt, TaskDao.kt
│   ├── TaskDatabase.kt        – Room database (v2)
│   ├── TaskRepository.kt      – data access used by the ViewModel
│   ├── SettingsStore.kt       – name / dark theme / reminders (SharedPreferences)
│   └── SampleData.kt          – demo courses and tasks
├── notifications/             – ReminderScheduler (AlarmManager), ReminderReceiver, BootReceiver
├── util/DateUtils.kt          – date conversion and formatting
└── ui/
    ├── TaskViewModel.kt       – MVVM state (StateFlow) and actions
    ├── TaskProApp.kt          – Navigation Compose graph and bottom navigation
    ├── components/            – shared UI pieces (task row, dialogs, chips...)
    ├── screens/               – Dashboard, Courses, CourseDetail, TaskList, TaskEdit, TaskDetail, Calendar, Settings
    └── theme/Theme.kt         – light & dark color schemes
```

## Run it
1. Open the project folder in Android Studio and let Gradle sync (JDK 17+).
2. Run on an emulator or a device with Android 8.0 (API 26) or newer.
3. For a quick demo: **Settings → Load sample courses & tasks**.

> If you installed the earlier UI prototype, the database is recreated automatically (schema changed from v1 to v2).

## Notes
- Reminders use inexact alarms, so a notification can arrive a few minutes late. On Android 13+ the app asks for notification permission the first time a task with a reminder is saved.
- No external web API is used (as planned in the proposal).
