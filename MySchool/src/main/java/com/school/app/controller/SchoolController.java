package com.school.app.controller;

import com.school.app.model.Student;
import com.school.app.repository.StudentRepository;
import com.school.app.view.StudentView;
import java.util.List;

public class SchoolController {
	private StudentView studentView;
	private StudentRepository studentRepository;

	public SchoolController(StudentView studentView, StudentRepository studentRepository) {
		this.studentView = studentView;
		this.studentRepository = studentRepository;
	}

	public void allStudents() {
		List<Student> students = this.studentRepository.findAll();
		this.studentView.showAllStudents(students);
	}

	public void newStudent(Student student) {
		Student existingStudent = this.studentRepository.findById(student.getId());
		if (existingStudent != null) {
			this.studentView.showError("Already existing student with id " + student.getId(), existingStudent);
		} else {
			this.studentRepository.save(student);
			this.studentView.studentAdded(student);
		}
	}

	public void deleteStudent(Student studentToDelete) {
		Student existingStudent = this.studentRepository.findById(studentToDelete.getId());
		if (existingStudent == null) {
			this.studentView.showError("No existing student with id " + studentToDelete.getId(), studentToDelete);
		} else {
			this.studentRepository.delete(studentToDelete.getId());
			this.studentView.studentRemoved(studentToDelete);
		}
	}
}
