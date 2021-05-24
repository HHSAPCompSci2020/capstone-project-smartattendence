# attendencesystem

Automated Attendance System Using Facial Recognition
Authors: Arya Khokhar, Xinyu Zhao
Revision: 5/23/2021

**Introduction: 

Our program is an automated attendance system that uses machine learning based facial recognition to recognize students in the classroom and mark them as present or absent. This program can be used for a visual check in system and will be based on a java package library for OpenCV (Open Source Computer Vision Library). This program can also save attendance for later access. The purpose of this program is to allow teachers and/or instructors to automate their attendance so they do not have to manually do it every day. 

In order to take attendance for a particular class, our program needs to know which students are in the class, their ID photos, names, as well as a video feed of the students who are in the class. The program will train itself to recognize and correlate the photos to the names, and once a photo of a student is given or shown, the program will automatically check the student as present. The program will have two main parts for facial recognition: the first part will detect the faces, and the second part will match the faces detected to the ID photos and the name. 

This program will also have a classroom feature, where students can be added to different classes. In the same way, students can also be removed from a class. If there is a student who does not have an ID photo yet but is present in the class, they will by default be marked as absent as they are not yet in the system. Attendance for the students will be saved for later access and teachers can see the past history of a student's attendance by selecting the class and date they want to check.

A GUI will also be available to display the attendance saved by the program, which can be later checked by the teachers. It will be two columns: one column of the names of students who were present, and one column of the students who were absent. No photos will be displayed on the GUI, aside from the video feed for attendance and the adding of photos into the database. Once photos are added, they will no longer be displayed. 

**Instructions:

In order to run, eclipse must have permission to access your device camera.

The teacher will start the application (by clicking run on eclipse) with the camera facing the students, or have a zoom meeting or anything that shows student’s faces is fine. To take attendance, the teacher will click on the Attendance tab at the top. As students walk past the camera, or a picture of the student is shown, the application will record their names in the attendance system. Later, the teacher can use the application to view the attendance of the student by going under the records tab and selecting their class. To see the present and absent students in a class, select a particular date and class in the Records tab. To edit, remove, or add a new student or classroom, click on the Students tab and click on the appropriate button and fill out the fields. To add a student, you must then go to the PhotoShoot tab in order to add their face to the database. The good news is that the program does not need many images of a student in order to detect them, so teachers can use their school-given student photo as the baseline image. To stop the application, close the window using the “x” mark on the top right corner of the windows.

**Features List (THE ONLY SECTION THAT CANNOT CHANGE LATER):

_Must-have Features: All Completed 

- Face detection in a picture: Using the laptop's camera, the system should be able to detect any faces in the picture. These face pictures are then going to be a part of the training data set. The system should allow for face pictures to be taken and saved if it detects a face in it. 
- Add students into the system: The program should be able to see a list of faces in the database and tag these pictures with a student id and name. This step needs to be done every time a new student joins a class.
- Face recognition: Once the program has enough tagged pictures, the system should be able to train/re-train itself on the current set of images. Once trained, the application should be able to recognize and match a given face to a pre-identified name. 
- Create a classroom: The system should make it easy to add students in the class with names and grade levels. A classroom can be identified by teacher and course name. A typical GUI-based list of students should allow students to be added to a class. If a student’s picture is not known, it may not be identified but we should allow the student to be added to the class. Later we should be able to add a picture id tag of the student when we get the picture. 
- GUI that shows all students and their attendance in the class. Simple navigation will be implemented using menus and buttons. For example, a menu for the teacher to see which students are absent and present on a given day.

_Want-to-have Features:

Completed:
- Support for multiple classes because the system may be used by one teacher teaching multiple classes and multiple teachers who are using the system for their own class.
- Feeding a multi-face picture of students instead of one student per picture. This reduces the work of the teacher in taking multiple pictures for the program training.  
- Similarly, feeding a multi-face picture of students for attendance so students do not have to wait in a line.
- Have help buttons to help users navigate the system.
- Ability to remove students from a class (based on a suggestion from a classmate)

Partially implemented:
- Allow teachers to edit/change attendance (As of now teachers can retake attendance as a whole on the same day, but not individual students yet)

Not completed:
- Networking support if the camera and application are running on separate computer systems. This way the teacher can have a camera running on one device by the door and access the data at their desk.
- See the history of student attendance for a class by semester or year. This way the teacher can view the past attendance of any student.
- Create a set of search queries - for example - highlight students if a student is absent for more than a specified number of days.



_Stretch Features:

- Create a website extension: Teachers can use the program with their designated school system so they do not have to transfer the information from the application to their school database.
- Support for multiple cameras: Different types of cameras will be compatible with the software so there is no limitation for the teachers.
- Continuous monitoring of the class and flag unknown faces: This way the teacher will be able to know if there is someone who is not supposed to be present or if someone leaves during class and does not come back. 

Class List:

- Attendance (This class is responsible for the attendance panel and respective GUI. It is also responsible for recognizing faces and taking attendance.)
- Classroom (This class represents the classroom and contains an ArrayList of Students. Students can be added into a class and new classes can be made.)
- Main (This class is the main class and creates the window and panels. Has an Attendance, PhotoShoot and SQLiteManager objects.)
- PhotoShoot (uses the device camera to add the photo of a new student to the database.)
- RecordsPanel (This class represents the RecordsPanel tab GUI and users will be able to view attendance history from here.)
- SQLiteManager (This class represents the SQLite database that stores the facial recognition and attendance data.)
- Student (Students will require a name, photo, and id. This class represents a Student.)
- StudentPanel (This is the class that represents the GUI for the Student tab and can add/remove/edit students/classrooms here.)
- WebCam (This class extends JPanel and is responsible for the video camera feed in different tabs of the window and used in PhotoShoot and Attendance.)


Credits:
[Gives credit for project components. This includes both internal credit (your group members) and external credit (other people, websites, libraries). To do this:
List the group members and describe how each member contributed to the completion of the final program. This could be classes written, art assets created, leadership/organizational skills exercises, or other tasks.
Give credit to all outside resources used. This includes downloaded images or sounds, external java libraries, parent/tutor/student coding help, etc.]

Arya:

Overall: in charge of facial recognition aspect and database
Classes:
   - Attendence
   - Main (both, resource portion)
   - PhotoShoot
   - SQLiteManager
   - WebCam

Xinyu:

Overall: in charge of GUI components and creating the hierarchy of the school
   - RecordsPanel
   - Main (both, created panel portion)
   - StudentPanel (Arya helped Xinyu understand how to access the database for GUI implementation)
   - Classroom
   - Student

https://mvnrepository.com
Allows the program to run without downloading any dependencies.

https://www.sqlite.org/index.html
SQLite is a database management system that is embedded into the end program.

http://bytedeco.org/
Bytedeco makes native libraries available to the Java platform by offering ready-to-use bindings. 
OpenCV libraries for different platforms (macOS & Windows) are used in our project.

https://opencv-java-tutorials.readthedocs.io/en/latest/
Face detection and tracking tutorial using OpenCV.

https://github.com/bytedeco/javacv/blob/master/samples/OpenCVFaceRecognizer.java
Explains how to use face recognition models.

https://www.w3schools.com/sql/sql_quickref.asp
Contains basic SQL reference and it is a good source for other things like HTML.

https://github.com/java-tester-x/JavaExercises3/blob/master/resources/JDatePickerDemo.java
Explains how to use JDatePicker (used in the RecordsPanel for attendance).

http://www.javased.com/?api=java.util.jar.JarInputStream
Sample codes on manipulation of jar files.

http://repast.sourceforge.net/docs/api/repastjava/org/jdesktop/swingx/autocomplete/AutoCompleteDecorator.html
A swingx class used for autocompletion on the PhotoShoot panel in the application.
