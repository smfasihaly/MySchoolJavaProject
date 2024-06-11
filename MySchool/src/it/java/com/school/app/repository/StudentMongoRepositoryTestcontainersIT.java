package com.school.app.repository;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.school.app.model.Student;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.assertj.core.api.Assertions;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

public class StudentMongoRepositoryTestcontainersIT {
   @ClassRule
   public static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");
   private MongoClient client;
   private StudentRepository studentRepository;
   private MongoCollection<Document> studentCollection;

   @Before
   public void setup() {
      this.client = new MongoClient(new ServerAddress(mongo.getHost(), mongo.getFirstMappedPort()));
      this.studentRepository = new StudentMongoRepository(this.client);
      MongoDatabase database = this.client.getDatabase("school");
      database.drop();
      this.studentCollection = database.getCollection("student");
   }

   @After
   public void tearDown() {
      this.client.close();
   }

   @Test
   public void testFindAll() {
      this.addTestStudentToDatabase("1", "test1");
      this.addTestStudentToDatabase("2", "test2");
      Assertions.assertThat(this.studentRepository.findAll()).containsExactly(new Student[]{new Student("1", "test1"), new Student("2", "test2")});
   }

   @Test
   public void testFindById() {
      this.addTestStudentToDatabase("1", "test1");
      this.addTestStudentToDatabase("2", "test2");
      Assertions.assertThat(this.studentRepository.findById("2")).isEqualTo(new Student("2", "test2"));
   }

   @Test
   public void testSave() {
      Student student = new Student("1", "added student");
      this.studentRepository.save(student);
      Assertions.assertThat(this.readAllStudentsFromDatabase()).containsExactly(new Student[]{student});
   }

   @Test
   public void testDelete() {
      this.addTestStudentToDatabase("1", "test1");
      this.studentRepository.delete("1");
      Assertions.assertThat(this.readAllStudentsFromDatabase()).isEmpty();
   }

   private List<Student> readAllStudentsFromDatabase() {
      return (List)StreamSupport.stream(this.studentCollection.find().spliterator(), false).map((d) -> {
         return new Student("" + d.get("id"), "" + d.get("name"));
      }).collect(Collectors.toList());
   }

   private void addTestStudentToDatabase(String id, String name) {
      this.studentCollection.insertOne((new Document()).append("id", id).append("name", name));
   }
}
