package com.school.app.racecondition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.school.app.controller.SchoolController;
import com.school.app.model.Student;
import com.school.app.repository.StudentRepository;
import com.school.app.view.StudentView;

public class SchoolControllerRaceConditionTest {

	@Mock
	private StudentRepository studentRepository;
	@Mock
	private StudentView studentView;
	@InjectMocks
	private SchoolController schoolController;
	private AutoCloseable closeable;

	@Before
	public void setUp() throws Exception {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@After
	public void releaseMocks() throws Exception {
		this.closeable.close();
	}
	int ui = 0;
	@Test
	public void testNewStudentConcurrent() {
		List<Student> students = new ArrayList<>();
		Student student = new Student("1", "name");
		when(studentRepository.findById(anyString()))
				.thenAnswer(invocation -> students.stream().findFirst().orElse(null));
		doAnswer(invocation -> {
			students.add(student);
			return null;
		}).when(studentRepository).save(any(Student.class)
				);
		List<Thread> threads = IntStream.range(0, 10)
				.mapToObj(i -> new Thread(() -> schoolController.newStudent(student))).peek(t -> t.start())
				.collect(Collectors.toList());
		await().atMost(10, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(t -> t.isAlive()));
		assertThat(students).containsExactly(student);
	}
	
	@Test
	public void testDeleteStudentWhenStudentExists() {
		List<Student> students = new ArrayList<>();
		Student student = new Student("1", "name");

		// Stub the StudentRepository to return the same student for any ID
		when(studentRepository.findById(anyString()))
				.thenAnswer(invocation -> students.stream().findFirst().orElse(null));
		doAnswer(invocation -> {
			students.remove(student);
			return null;
		}).when(studentRepository).delete(anyString());

		students.add(student);

		// Start the threads calling deleteStudent concurrently
		List<Thread> threads = IntStream.range(0, 100)
				.mapToObj(i -> new Thread(() -> schoolController.deleteStudent(student))).peek(t -> t.start())
				.collect(Collectors.toList());

		await().atMost(10, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(Thread::isAlive));

		assertThat(students).isEmpty();

	}
}
