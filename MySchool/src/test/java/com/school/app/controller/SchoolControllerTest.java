package com.school.app.controller;

import com.school.app.model.Student;
import com.school.app.repository.StudentRepository;
import com.school.app.view.StudentView;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class SchoolControllerTest {
   @Mock
   private StudentRepository studentRepository;
   @Mock
   private StudentView studentView;
   @InjectMocks
   private SchoolController schoolController;
   private AutoCloseable closeable;

   @Before
   public void setup() {
      this.closeable = MockitoAnnotations.openMocks(this);
   }

   @After
   public void releaseMocks() throws Exception {
      this.closeable.close();
   }

   @Test
   public void testAllStudent() {
      List<Student> students = Arrays.asList(new Student());
      Mockito.when(this.studentRepository.findAll()).thenReturn(students);
      this.schoolController.allStudents();
      ((StudentView)Mockito.verify(this.studentView)).showAllStudents(students);
   }

   @Test
   public void testNewStudentWhenStudentDoesNotAlreadyExist() {
      Student student = new Student("1", "Student1");
      Mockito.when(this.studentRepository.findById("1")).thenReturn(null);
      this.schoolController.newStudent(student);
      InOrder inOrder = Mockito.inOrder(new Object[]{this.studentRepository, this.studentView});
      ((StudentRepository)inOrder.verify(this.studentRepository)).save(student);
      ((StudentView)inOrder.verify(this.studentView)).studentAdded(student);
   }

   @Test
   public void testNewStudentWhenStudentAlreadyExists() {
      Student student = new Student("1", "Student1");
      Student existingStudent = new Student("1", "Student2");
      Mockito.when(this.studentRepository.findById("1")).thenReturn(existingStudent);
      this.schoolController.newStudent(student);
      ((StudentView)Mockito.verify(this.studentView)).showError("Already existing student with id 1", existingStudent);
      Mockito.verifyNoMoreInteractions(Mockito.ignoreStubs(new Object[]{this.studentRepository}));
   }

   @Test
   public void testDeleteStudentWhenStudentExists() {
      Student studentToDelete = new Student("1", "Student1");
      Mockito.when(this.studentRepository.findById("1")).thenReturn(studentToDelete);
      this.schoolController.deleteStudent(studentToDelete);
      InOrder inOrder = Mockito.inOrder(new Object[]{this.studentRepository, this.studentView});
      ((StudentRepository)inOrder.verify(this.studentRepository)).delete("1");
      ((StudentView)inOrder.verify(this.studentView)).studentRemoved(studentToDelete);
   }

   @Test
   public void testDeleteStudentWhenStudentDoesNotExists() {
      Student studentToDelete = new Student("1", "Student1");
      Mockito.when(this.studentRepository.findById("1")).thenReturn(null);
      this.schoolController.deleteStudent(studentToDelete);
      InOrder inOrder = Mockito.inOrder(new Object[]{this.studentRepository, this.studentView});
      ((StudentView)Mockito.verify(this.studentView)).showError("No existing student with id 1", studentToDelete);
      Mockito.verifyNoMoreInteractions(Mockito.ignoreStubs(new Object[]{this.studentRepository}));
   }
}
