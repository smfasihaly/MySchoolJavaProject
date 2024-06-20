package com.school.app.controller;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.school.app.model.Student;
import com.school.app.repository.StudentMongoRepository;
import com.school.app.repository.StudentRepository;
import com.school.app.view.StudentView;

public class SchoolControllerIT {
	@Mock
	private StudentView studentView;
	private StudentRepository studentRepository;
	private SchoolController schoolController;
	private AutoCloseable closeable;
	private static int mongoPort = Integer.parseInt(System.getProperty("mongo.port", "27017"));

	@Before
	public void setUp() {
		closeable = MockitoAnnotations.openMocks(this);
		studentRepository = new StudentMongoRepository(new MongoClient(new ServerAddress("localhost", mongoPort)));
		// explicit empty the database through the repository
		for (Student student : studentRepository.findAll()) {
			studentRepository.delete(student.getId());
		}
		schoolController = new SchoolController(studentView, studentRepository);
	}

	@After
	public void releaseMocks() throws Exception {
		this.closeable.close();
	}

	@Test
	public void testAllStudents() {
		Student student = new Student("1", "test");
		this.studentRepository.save(student);
		this.schoolController.allStudents();
		((StudentView) Mockito.verify(this.studentView)).showAllStudents(Arrays.asList(student));
	}

	@Test
	public void testNewStudent() {
		Student student = new Student("1", "test");
		this.schoolController.newStudent(student);
		((StudentView) Mockito.verify(this.studentView)).studentAdded(student);
		((StudentView) Mockito.verify(this.studentView)).studentAdded(student);
	}

	@Test
	public void testDeleteStudent() {
		Student studentToDelete = new Student("1", "test");
		this.studentRepository.save(studentToDelete);
		this.schoolController.deleteStudent(studentToDelete);
		((StudentView) Mockito.verify(this.studentView)).studentRemoved(studentToDelete);
	}
}
