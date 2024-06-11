package com.school.app.view;

import com.school.app.model.Student;
import java.util.List;

public interface StudentView {
   void showAllStudents(List<Student> var1);

   void showError(String var1, Student var2);

   void studentAdded(Student var1);

   void studentRemoved(Student var1);
}
