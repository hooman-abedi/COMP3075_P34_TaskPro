# TaskPro – Functional Prototype Demo Video
Team P34 · COMP3074 · Target length: 6–7 minutes

---------------------------------------------------------------------
## BEFORE YOU RECORD (setup checklist)
---------------------------------------------------------------------
1. Build & run the app in Android Studio on an emulator (Pixel, API 34 or 35).
2. Push the latest code to GitHub so the repo you show matches the app.
3. Start from an empty app: uninstall/reinstall, or Settings → "Delete all courses & tasks".
4. In Settings, leave Dark theme OFF and Reminders ON.
5. Recorder: macOS → Cmd + Shift + 5 → "Record Selected Portion" around the emulator
   (or QuickTime → New Screen Recording). Turn on the microphone.
6. Have open in tabs: the GitHub repo page and Android Studio with the project tree expanded.
7. Rehearse once. Speak slowly; pause 1–2 s after each tap so viewers can follow.
8. Reminder timing: the reminder notification can arrive a minute or two late.
   You create the reminder task in Part 3 and show the notification in Part 6,
   so a few minutes pass in between. If it doesn't show, record Part 6b as a separate clip.

Speaker split (change names as you like):
  Part 1–2  Martins · Part 3  Hooman · Part 4  Bak · Part 5  Martins/Bak · Part 6–7  Winthrop
Each person can record their own part and you join the clips (iMovie / Clipchamp / CapCut).

---------------------------------------------------------------------
## PART 1 – Introduction (≈40 s) · Martins
---------------------------------------------------------------------
SHOW: Title slide or the app icon on the emulator home screen.
SAY:
"Hi, we're Team P34 for COMP3074: Martins, Hooman, Bak and Winthrop.
Our project is TaskPro, a student planner for Android. Students take several courses at once,
so it's easy to lose track of assignments and quizzes. TaskPro keeps all courses and deadlines
in one place. In this video we'll show our progress and every feature that works in the
functional prototype."

---------------------------------------------------------------------
## PART 2 – Progress & code structure (≈60 s) · Martins
---------------------------------------------------------------------
SHOW: GitHub repo page → README feature table. Then Android Studio project tree:
data/, ui/screens/, notifications/.
SAY:
"In the UI prototype we only had screen layouts with sample text.
Since then we've built the real app. It's written in Kotlin with Jetpack Compose,
uses Navigation Compose to move between screens, and stores everything locally in a
Room database, so it works offline.
We follow an MVVM structure: the data folder has our Course and Task entities, DAOs and a
repository. One course can have many tasks. The ViewModel holds the state, and the
screens folder has one file per screen. All twelve requirements from our proposal are now
implemented. The table in our README shows where each one is."

---------------------------------------------------------------------
## PART 3 – Courses (FR-01, FR-07) (≈75 s) · Hooman
---------------------------------------------------------------------
DO / SHOW:
1. Open the app → empty Dashboard with "Start by adding your courses" → tap "Go to Courses".
2. Tap "Add Course" → tap Save with empty fields → show the red validation messages.
3. Enter: COMP3074 / Mobile Application Development I / your instructor → Save.
4. Add a second course: COMP3123 / Full Stack Development I.
5. Tap the pencil on COMP3123 → change the name slightly → Save (shows edit).
6. Tap the COMP3074 card → Course Details screen.
7. Tap "Add Task": title "Functional prototype video", date = today,
   time = about 4 minutes from now, priority High, Reminder "At due time" → Save.
   (Allow the notification permission if Android asks.)
SAY:
"I worked on the data model and course screens. Every task belongs to a course,
so the first step is adding courses. The form checks that the code and name are filled in.
I can edit a course with the pencil icon, and delete it with the trash icon.
Deleting a course asks for confirmation and also removes its tasks.
Tapping a course opens its details: name, instructor, progress, and all its tasks split into
To do and Completed. I'll add a task right from here. The course is already selected.
I'll set it due in a few minutes with a reminder, and we'll come back to that at the end."

---------------------------------------------------------------------
## PART 4 – Tasks (FR-02 – FR-05) (≈90 s) · Bak
---------------------------------------------------------------------
DO / SHOW:
1. Back on Course Details, tap "Add Task" again → tap Save with no title → show error.
2. Fill: "Assignment 3", open the date picker and choose a date next week, open the time
   picker and choose 11:59 PM, priority Medium, Reminder "1 day before",
   notes "Upload APK and report" → Save.
3. Tap the task → Task Details (priority, status, course, due date, time left, reminder, notes).
4. Tap the pencil → change priority to High → Save → details update.
5. Tap "Mark as Completed" → go back → task is crossed out under Completed.
   Tick its checkbox again to make it incomplete.
6. Open it → "Delete Task" → confirm.
SAY:
"I worked on navigation and the task features. Each task has a title, course, due date and
time, a priority, an optional reminder and notes. The title is required.
For dates we use date and time pickers, so deadlines are stored as real dates.
That lets the app sort tasks and detect overdue work.
The details screen shows everything about the task. From here I can edit it,
mark it completed or incomplete, or delete it. Delete always asks first.
Completing a task also works straight from the checkbox in any list."

---------------------------------------------------------------------
## PART 5 – Dashboard, Tasks list & Calendar (FR-06, FR-08, FR-09, FR-12) (≈90 s) · Martins/Bak
---------------------------------------------------------------------
DO / SHOW:
1. Settings → "Load sample courses & tasks" (gives realistic data).
2. Home: point at Due soon / To do / Overdue, the progress card, Upcoming tasks
   (earliest first; overdue items in red), and "Progress by course" at the bottom.
3. Tasks tab: tap filters To do → Due soon → Overdue → Completed → All.
   Tap a course chip (e.g. COMP3123). Open the sort menu → Priority, then Course.
4. Calendar tab: point at the dots (blue = due, red = overdue, grey = done).
   Tap a day with a dot → its tasks appear. Go to next month and back, tap "Today".
   Select an empty day → tap + → the date is already filled in → Cancel.
SAY:
"To show the remaining screens with more data, we load some sample courses and tasks.
The dashboard is the home screen. It counts tasks due in the next three days, all open tasks,
and overdue ones, and lists the next five deadlines in order. At the bottom we show study
progress for each course.
The Tasks tab has every task. You can filter by status or course and sort by due date,
priority, course or title.
The Calendar shows deadlines by date. Days with tasks have a dot, and red means overdue.
Tapping a day lists what's due. Adding a task from here fills in that date automatically."

---------------------------------------------------------------------
## PART 6 – Reminders, dark theme, saved data (FR-10, FR-11) (≈60 s) · Winthrop
---------------------------------------------------------------------
DO / SHOW:
1. Press Home on the emulator. Wait for (or pull down) the notification for
   "COMP3074: Functional prototype video" from Part 3. Tap it → app opens.
   (6b backup: if it was late, show the separately recorded clip here.)
2. Settings → toggle Dark theme ON → visit Home and Calendar → toggle back if you like.
3. Show the Reminders switch.
4. Open recent apps → swipe TaskPro away → reopen → all data is still there.
SAY:
"Here's the reminder from the task Hooman created. It's a local Android notification.
No internet is needed, and reminders are rescheduled if the phone restarts.
They can be turned off in Settings.
Settings also has a dark theme. Finally, persistence: I'll close the app completely and
reopen it. Everything is still there, because it's saved in the Room database on the device."

---------------------------------------------------------------------
## PART 7 – Testing & next steps (≈40 s) · Winthrop
---------------------------------------------------------------------
SHOW: README on GitHub, or the app's Dashboard.
SAY:
"We tested the main flows by hand: form validation, editing, completing and deleting,
deleting a course along with its tasks, filters and sorting, overdue and today dates,
dark theme, reminders, and data staying after a restart.
For the final submission we plan to polish the UI, add automated tests for the repository
and ViewModel, and improve the statistics. Thanks for watching!"

---------------------------------------------------------------------
## AFTER RECORDING
---------------------------------------------------------------------
- Check total length (6–7 min) and that your voice is audible.
- Export as MP4 (1080p). Upload to D2L directly, or to YouTube (Unlisted) / OneDrive
  and submit the link, depending on what the instructor allows.
- Submit: GitHub repo link + video.
