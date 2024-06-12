package com.school.app.repository;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.school.app.model.Student;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.assertj.core.api.Assertions;
import org.bson.Document;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.school.app.repository.StudentMongoRepository.SCHOOL_DB_NAME;
import static com.school.app.repository.StudentMongoRepository.STUDENT_COLLECTION_NAME;

public class StudentMongoRepositoryTest {
	private static MongoServer server;
	private static InetSocketAddress serverAddress;
	private MongoClient client;
	private StudentMongoRepository studentRepository;
	private MongoCollection<Document> studentCollection;

	@BeforeClass
	public static void setupServer() {
		server = new MongoServer(new MemoryBackend());
		serverAddress = server.bind();
	}

	@AfterClass
	public static void shutdownServer() {
		server.shutdown();
	}

	@Before
	public void setup() {
		this.client = new MongoClient(new ServerAddress(serverAddress));
		this.studentRepository = new StudentMongoRepository(this.client);
		MongoDatabase database = client.getDatabase(SCHOOL_DB_NAME);
		database.drop();
		this.studentCollection = database.getCollection(STUDENT_COLLECTION_NAME);
	}

	@Test
	public void testFindAllWhenDatabaseIsEmpty() {
		Assertions.assertThat(this.studentRepository.findAll()).isEmpty();
	}

	@Test
	public void testFindAllWhenDatabaseIsNotEmpty() {
		this.addTestStudentToDatabase("1", "test1");
		this.addTestStudentToDatabase("2", "test2");
		Assertions.assertThat(this.studentRepository.findAll())
				.containsExactly(new Student[] { new Student("1", "test1"), new Student("2", "test2") });
	}

	@Test
	public void testFindByIdNotFound() {
		Assertions.assertThat(this.studentRepository.findById("1")).isNull();
	}

	@Test
	public void testFindByIdFound() {
		this.addTestStudentToDatabase("1", "test1");
		this.addTestStudentToDatabase("2", "test2");
		Assertions.assertThat(this.studentRepository.findById("2")).isEqualTo(new Student("2", "test2"));
	}

	@Test
	public void testSave() {
		Student student = new Student("1", "added student");
		this.studentRepository.save(student);
		Assertions.assertThat(this.readAllStudentsFromDatabase()).containsExactly(new Student[] { student });
	}

	@Test
	public void testDelete() {
		this.addTestStudentToDatabase("1", "test1");
		this.studentRepository.delete("1");
		Assertions.assertThat(this.readAllStudentsFromDatabase()).isEmpty();
	}

	private List<Student> readAllStudentsFromDatabase() {
		return (List) StreamSupport.stream(this.studentCollection.find().spliterator(), false).map((d) -> {
			return new Student("" + d.get("id"), "" + d.get("name"));
		}).collect(Collectors.toList());
	}

	private void addTestStudentToDatabase(String id, String name) {
		this.studentCollection.insertOne((new Document()).append("id", id).append("name", name));
	}
}
