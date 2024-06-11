package com.school.app.repository;

import com.school.app.model.Student;
import java.util.List;

public interface StudentRepository {
   List<Student> findAll();

   Student findById(String var1);

   void save(Student var1);

   void delete(String var1);
}
