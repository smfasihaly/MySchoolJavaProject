package com.school.app.model;

import java.util.Objects;

public class Student {
   private String id;
   private String name;

   public Student() {
   }

   public Student(String id, String name) {
      this.id = id;
      this.name = name;
   }

   public String getId() {
      return this.id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String toString() {
      return "Student [id=" + this.id + ", name=" + this.name + "]";
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.id, this.name});
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         Student other = (Student)obj;
         return Objects.equals(this.id, other.id) && Objects.equals(this.name, other.name);
      }
   }
}
