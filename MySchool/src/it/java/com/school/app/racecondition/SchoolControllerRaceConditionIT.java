package com.school.app.racecondition;

import static com.school.app.repository.StudentMongoRepository.SCHOOL_DB_NAME;
import static com.school.app.repository.StudentMongoRepository.STUDENT_COLLECTION_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.school.app.controller.SchoolController;
import com.school.app.model.Student;
import com.school.app.repository.StudentMongoRepository;
import com.school.app.repository.StudentRepository;
import com.school.app.view.StudentView;

public class SchoolControllerRaceConditionIT {
	@Mock
	private StudentView studentView;
	@ClassRule
	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");
	private MongoClient client;
	private StudentRepository studentRepository;
	private AutoCloseable closeable;

	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		client = new MongoClient(new ServerAddress(mongo.getHost(), mongo.getFirstMappedPort()));
		studentRepository = new StudentMongoRepository(this.client);
		MongoDatabase database = client.getDatabase(SCHOOL_DB_NAME);
		database.drop();
		MongoCollection<Document> studentCollection = database.getCollection(STUDENT_COLLECTION_NAME);
		studentCollection.createIndex(Indexes.ascending("id"), new IndexOptions().unique(true));
		studentRepository = new StudentMongoRepository(client);
	}

	@After
	public void tearDown() {
		client.close();
	}

	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}

	@Test
	public void testNewStudentConcurrent() {
		Student student = new Student("1", "name");
		// start the threads calling newStudent concurrently
		// on different SchoolController instances, so 'synchronized'
		// methods in the controller will not help...
		List<Thread> threads = IntStream.range(0, 10).mapToObj(i -> new Thread(() -> {
			try {
				new SchoolController(studentView, studentRepository).newStudent(student);
			} catch (MongoWriteException e) {
				// E11000 duplicate key error collection:
				// school.student index: id_1 dup key: { id: "1" }
				e.printStackTrace();
			}
		})).peek(t -> t.start()).collect(Collectors.toList());
		// wait for all the threads to finish
		await().atMost(10, TimeUnit.SECONDS).until(() -> threads.stream().noneMatch(t -> t.isAlive()));
		// there should be a single element in the list
		assertThat(studentRepository.findAll()).containsExactly(student);
	}

}
