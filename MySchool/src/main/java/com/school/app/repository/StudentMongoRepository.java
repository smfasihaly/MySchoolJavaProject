package com.school.app.repository;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.school.app.model.Student;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.bson.Document;

public class StudentMongoRepository implements StudentRepository {
   public static final String SCHOOL_DB_NAME = "school";
   public static final String STUDENT_COLLECTION_NAME = "student";
   private MongoCollection<Document> studentCollection;

   public StudentMongoRepository(MongoClient client) {
      this.studentCollection = client.getDatabase("school").getCollection("student");
   }

   public List<Student> findAll() {
      return (List)StreamSupport.stream(this.studentCollection.find().spliterator(), false).map(this::fromDocumentToStudent).collect(Collectors.toList());
   }

   private Student fromDocumentToStudent(Document d) {
      return new Student("" + d.get("id"), "" + d.get("name"));
   }

   public Student findById(String id) {
      Document d = (Document)this.studentCollection.find(Filters.eq("id", id)).first();
      return d != null ? this.fromDocumentToStudent(d) : null;
   }

   public void save(Student student) {
      this.studentCollection.insertOne((new Document()).append("id", student.getId()).append("name", student.getName()));
   }

   public void delete(String id) {
      this.studentCollection.deleteOne(Filters.eq("id", id));
   }
}
