package com.school.app.controller;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.school.app.model.Student;
import com.school.app.repository.StudentRepository;
import com.school.app.view.StudentView;

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
		when(this.studentRepository.findAll()).thenReturn(students);
		this.schoolController.allStudents();
		((StudentView) verify(this.studentView)).showAllStudents(students);
	}

	@Test
	public void testNewStudentWhenStudentDoesNotAlreadyExist() {
		Student student = new Student("1", "Student1");
		when(this.studentRepository.findById("1")).thenReturn(null);
		this.schoolController.newStudent(student);
		InOrder inOrder = inOrder(new Object[] { this.studentRepository, this.studentView });
		((StudentRepository) inOrder.verify(this.studentRepository)).save(student);
		((StudentView) inOrder.verify(this.studentView)).studentAdded(student);
	}

	@Test
	public void testNewStudentWhenStudentAlreadyExists() {
		Student student = new Student("1", "Student1");
		Student existingStudent = new Student("1", "Student2");
		when(this.studentRepository.findById("1")).thenReturn(existingStudent);
		this.schoolController.newStudent(student);
		((StudentView) verify(this.studentView)).showError("Already existing student with id 1", existingStudent);
		verifyNoMoreInteractions(ignoreStubs(new Object[] { this.studentRepository }));
	}

	@Test
	public void testDeleteStudentWhenStudentExists() {
		Student studentToDelete = new Student("1", "Student1");
		when(this.studentRepository.findById("1")).thenReturn(studentToDelete);
		this.schoolController.deleteStudent(studentToDelete);
		InOrder inOrder = inOrder(new Object[] { this.studentRepository, this.studentView });
		((StudentRepository) inOrder.verify(this.studentRepository)).delete("1");
		((StudentView) inOrder.verify(this.studentView)).studentRemoved(studentToDelete);
	}

	@Test
	public void testDeleteStudentWhenStudentDoesNotExists() {
		Student student = new Student("1", "test");
		when(studentRepository.findById("1")).thenReturn(null);
		schoolController.deleteStudent(student);
		verify(studentView).showError("No existing student with id 1", student);
		verifyNoMoreInteractions(ignoreStubs(studentRepository));
	}
}
