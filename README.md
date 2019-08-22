# Overview

ToDoList- a simple app created as learning project. Uses JavaFX and txt file as datasource:

![!screen_overall](screens/1_overall.jpg?raw=true "ToDoList - overall view")

Still under development. Featurea added so far:

- adding new task
- deleting task
- filtering task (all-tasks for today only)
- exit program

### requirements:
- Java 11

### How to run

- clone and download repository
- make sure you have switched to Java 11*
- go to project file (/todolist/) - make sure you see folder "target" and file "toolist.txt"
- while in "todolist" folder, run jar file from console using: 
```$xslt
java -jar target/todolist-1.0-SNAPSHOT.jar
```



_*due to an error in Maven plugin "maven-compiler-plugin" app doesn't work with Java 12 and higher so far_

### Add Task Dialog

Dialog Window available from both File Menu and quick access button:

![!screen_taskDialog](screens/2_addTask.jpg?raw=true "ToDoList - addTask Dialog")

### Contet Menu

Available via right-click on list view:

![!screen_cntextMenu](screens/3_contextMenu.jpg?raw=true "ToDoList - context menu")
