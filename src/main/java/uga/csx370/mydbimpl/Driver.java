/**
 * Copyright (c) 2025 Sami Menik, PhD. All rights reserved.
 * 
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 * This software is provided "as is," without warranty of any kind.
 */
package uga.csx370.mydbimpl;

import java.util.List;

import uga.csx370.mydb.RA;
import uga.csx370.mydb.Relation;
import uga.csx370.mydb.RelationBuilder;
import uga.csx370.mydb.Type;

public class Driver {

    public static void main(String[] args) {

        RA ra = new RAImpl();

        // LOAD TABLES //

        // Student table
        Relation student = new RelationBuilder()
                .attributeNames(List.of(
                        "ID",
                        "name",
                        "dept_name",
                        "tot_cred"))
                .attributeTypes(List.of(
                        Type.INTEGER,
                        Type.STRING,
                        Type.STRING,
                        Type.INTEGER))
                .build();

        student.loadData("student_export.csv");


        // Takes table
        Relation takes = new RelationBuilder()
                .attributeNames(List.of(
                        "ID",
                        "course_id",
                        "sec_id",
                        "semester",
                        "year",
                        "grade"))
                .attributeTypes(List.of(
                        Type.INTEGER,
                        Type.STRING,
                        Type.STRING,
                        Type.STRING,
                        Type.INTEGER,
                        Type.STRING))
                .build();

        takes.loadData("takes_export.csv");


        // Course table
        Relation course = new RelationBuilder()
                .attributeNames(List.of(
                        "course_id",
                        "title",
                        "dept_name",
                        "credits"))
                .attributeTypes(List.of(
                        Type.STRING,
                        Type.STRING,
                        Type.STRING,
                        Type.INTEGER))
                .build();

        course.loadData("course_export.csv");


        // Instructor table
        Relation instructor = new RelationBuilder()
                .attributeNames(List.of(
                        "ID",
                        "name",
                        "dept_name",
                        "salary"))
                .attributeTypes(List.of(
                        Type.INTEGER,
                        Type.STRING,
                        Type.STRING,
                        Type.DOUBLE))
                .build();

        instructor.loadData("instructor_export.csv");


        // Teaches table
        Relation teaches = new RelationBuilder()
                .attributeNames(List.of(
                        "ID",
                        "course_id",
                        "sec_id",
                        "semester",
                        "year"))
                .attributeTypes(List.of(
                        Type.INTEGER,
                        Type.STRING,
                        Type.STRING,
                        Type.STRING,
                        Type.INTEGER))
                .build();

        teaches.loadData("teaches_export.csv");


        /*
         * QUERY 1 (Sai)
         *
         * Which Computer Science students earned an A+ in 2010,
         * and what courses did they earn it in?
         *
         * Tables:
         * student + takes + course
         */

        System.out.println();
        System.out.println("QUERY 1:");
        System.out.println(
                "Which Computer Science students earned an A+ in 2010, "
                + "and what courses did they earn it in?");
        System.out.println();

        int studentDeptIndex = student.getAttrIndex("dept_name");

        Relation csStudents = ra.select(
                student,
                row -> row.get(studentDeptIndex)
                          .getAsString()
                          .equals("Comp. Sci.")
        );

        Relation studentInfo = ra.project(
                csStudents,
                List.of("ID", "name")
        );

        studentInfo = ra.rename(
                studentInfo,
                List.of("name"),
                List.of("student")
        );

        int gradeIndex = takes.getAttrIndex("grade");
        int takesYearIndex = takes.getAttrIndex("year");

        Relation aPlus2010 = ra.select(
                takes,
                row ->
                        row.get(gradeIndex)
                           .getAsString()
                           .equals("A+")
                        &&
                        row.get(takesYearIndex)
                           .getAsInt() == 2010
        );

        Relation studentTakes = ra.join(
                studentInfo,
                aPlus2010
        );

        Relation courseInfo = ra.project(
                course,
                List.of("course_id", "title")
        );

        courseInfo = ra.rename(
                courseInfo,
                List.of("title"),
                List.of("course")
        );

        Relation query1Joined = ra.join(
                studentTakes,
                courseInfo
        );

        Relation query1Result = ra.project(
                query1Joined,
                List.of(
                        "student",
                        "course",
                        "semester",
                        "year",
                        "grade")
        );

        query1Result.print();


        /*
         * QUERY 2 (Sai)
         *
         * Which instructors taught 4-credit courses in 2010?
         *
         * Tables:
         * instructor + teaches + course
         */

        System.out.println();
        System.out.println("QUERY 2:");
        System.out.println(
                "Which instructors taught 4-credit courses in 2010?");
        System.out.println();

        Relation instructorInfo = ra.project(
                instructor,
                List.of("ID", "name")
        );

        instructorInfo = ra.rename(
                instructorInfo,
                List.of("name"),
                List.of("instructor")
        );

        int teachesYearIndex = teaches.getAttrIndex("year");

        Relation teaches2010 = ra.select(
                teaches,
                row -> row.get(teachesYearIndex)
                          .getAsInt() == 2010
        );

        Relation instructorTeaches = ra.join(
                instructorInfo,
                teaches2010
        );

        int creditsIndex = course.getAttrIndex("credits");

        Relation fourCreditCourses = ra.select(
                course,
                row -> row.get(creditsIndex)
                          .getAsInt() == 4
        );

        Relation fourCreditCourseInfo = ra.project(
                fourCreditCourses,
                List.of(
                        "course_id",
                        "title",
                        "dept_name",
                        "credits")
        );

        fourCreditCourseInfo = ra.rename(
                fourCreditCourseInfo,
                List.of("title", "dept_name"),
                List.of("course", "course_department")
        );

        Relation query2Joined = ra.join(
                instructorTeaches,
                fourCreditCourseInfo
        );

        Relation query2Result = ra.project(
                query2Joined,
                List.of(
                        "instructor",
                        "course",
                        "course_department",
                        "credits",
                        "semester",
                        "year")
        );

        query2Result.print();
    }
}
