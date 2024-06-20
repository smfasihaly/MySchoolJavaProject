package com.school.app.repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.school.app.model.Student;

public class StudentMongoRepository implements StudentRepository {
	public static final String SCHOOL_DB_NAME = "school";
	public static final String STUDENT_COLLECTION_NAME = "student";
	private MongoCollection<Document> studentCollection;

	public StudentMongoRepository(MongoClient client) {
		this.studentCollection = client.getDatabase("school").getCollection("student");
	}

	@Override
	public List<Student> findAll() {
		return StreamSupport.stream(studentCollection.find().spliterator(), false).map(this::fromDocumentToStudent)
				.collect(Collectors.toList());
	}

	private Student fromDocumentToStudent(Document d) {
		return new Student("" + d.get("id"), "" + d.get("name"));
	}

	@Override
	public Student findById(String id) {
		Document d = (Document) this.studentCollection.find(Filters.eq("id", id)).first();
		return d != null ? this.fromDocumentToStudent(d) : null;
	}

	@Override
	public void save(Student student) {
		this.studentCollection
				.insertOne((new Document()).append("id", student.getId()).append("name", student.getName()));
	}

	@Override
	public void delete(String id) {
		this.studentCollection.deleteOne(Filters.eq("id", id));
	}
}
