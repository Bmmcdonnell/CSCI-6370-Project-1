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

        // Classroom table
        Relation classroom = new RelationBuilder()
                .attributeNames(List.of(
                        "building",
                        "room_number",
                        "capacity"))
                .attributeTypes(List.of(
                        Type.STRING,
                        Type.STRING,
                        Type.INTEGER))
                .build();

        classroom.loadData("classroom_export.csv");

        // Section table
        Relation section = new RelationBuilder()
                .attributeNames(List.of(
                        "course_id",
                        "sec_id",
                        "semester",
                        "year",
                        "building",
                        "room_number",
                        "time_slot_id"))
                .attributeTypes(List.of(
                        Type.STRING,
                        Type.STRING,
                        Type.STRING,
                        Type.INTEGER,
                        Type.STRING,
                        Type.STRING,
                        Type.STRING))
                .build();

        section.loadData("section_export.csv");

        // Advisor table
        Relation advisor = new RelationBuilder()
                .attributeNames(List.of(
                        "s_ID",
                        "i_ID"))
                .attributeTypes(List.of(
                        Type.INTEGER,
                        Type.INTEGER))
                .build();

        advisor.loadData("advisor_export.csv");

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
                        .equals("Comp. Sci."));

        Relation studentInfo = ra.project(
                csStudents,
                List.of("ID", "name"));

        studentInfo = ra.rename(
                studentInfo,
                List.of("name"),
                List.of("student"));

        int gradeIndex = takes.getAttrIndex("grade");
        int takesYearIndex = takes.getAttrIndex("year");

        Relation aPlus2010 = ra.select(
                takes,
                row -> row.get(gradeIndex)
                        .getAsString()
                        .equals("A+")
                        &&
                        row.get(takesYearIndex)
                                .getAsInt() == 2010);

        Relation studentTakes = ra.join(
                studentInfo,
                aPlus2010);

        Relation courseInfo = ra.project(
                course,
                List.of("course_id", "title"));

        courseInfo = ra.rename(
                courseInfo,
                List.of("title"),
                List.of("course"));

        Relation query1Joined = ra.join(
                studentTakes,
                courseInfo);

        Relation query1Result = ra.project(
                query1Joined,
                List.of(
                        "student",
                        "course",
                        "semester",
                        "year",
                        "grade"));

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
                List.of("ID", "name"));

        instructorInfo = ra.rename(
                instructorInfo,
                List.of("name"),
                List.of("instructor"));

        int teachesYearIndex = teaches.getAttrIndex("year");

        Relation teaches2010 = ra.select(
                teaches,
                row -> row.get(teachesYearIndex)
                        .getAsInt() == 2010);

        Relation instructorTeaches = ra.join(
                instructorInfo,
                teaches2010);

        int creditsIndex = course.getAttrIndex("credits");

        Relation fourCreditCourses = ra.select(
                course,
                row -> row.get(creditsIndex)
                        .getAsInt() == 4);

        Relation fourCreditCourseInfo = ra.project(
                fourCreditCourses,
                List.of(
                        "course_id",
                        "title",
                        "dept_name",
                        "credits"));

        fourCreditCourseInfo = ra.rename(
                fourCreditCourseInfo,
                List.of("title", "dept_name"),
                List.of("course", "course_department"));

        Relation query2Joined = ra.join(
                instructorTeaches,
                fourCreditCourseInfo);

        Relation query2Result = ra.project(
                query2Joined,
                List.of(
                        "instructor",
                        "course",
                        "course_department",
                        "credits",
                        "semester",
                        "year"));

        query2Result.print();

        /*
         * QUERY 3 (Ido)
         *
         * For each student, which courses are they taking and
         * what grade did they receive?
         *
         * Tables:
         * student + takes + course
         */

        System.out.println();
        System.out.println("QUERY 3:");
        System.out.println(
                "For each Statistics student, which courses did they take "
                        + "in Fall 2008 and what grade did they receive?");
        System.out.println();

        int studentDeptIndexQ3 = student.getAttrIndex("dept_name");

        Relation statsStudentsQ3 = ra.select(
                student,
                row -> row.get(studentDeptIndexQ3)
                        .getAsString()
                        .equals("Statistics"));

        Relation studentNamesQ3 = ra.project(
                statsStudentsQ3,
                List.of("ID", "name"));

        studentNamesQ3 = ra.rename(
                studentNamesQ3,
                List.of("name"),
                List.of("student"));

        Relation courseNamesQ3 = ra.project(
                course,
                List.of("course_id", "title"));

        courseNamesQ3 = ra.rename(
                courseNamesQ3,
                List.of("title"),
                List.of("course"));

        int takesSemesterIndexQ3 = takes.getAttrIndex("semester");
        int takesYearIndexQ3 = takes.getAttrIndex("year");

        Relation takesFall2008Q3 = ra.select(
                takes,
                row -> row.get(takesSemesterIndexQ3)
                        .getAsString()
                        .equals("Fall")
                        &&
                        row.get(takesYearIndexQ3)
                                .getAsInt() == 2008);

        Relation studentTakesQ3 = ra.join(
                studentNamesQ3,
                takesFall2008Q3);

        Relation query3Joined = ra.join(
                studentTakesQ3,
                courseNamesQ3);

        Relation query3Result = ra.project(
                query3Joined,
                List.of(
                        "student",
                        "course",
                        "semester",
                        "year",
                        "grade"));

        query3Result.print();

        /*
         * QUERY 4 (Ido)
         *
         * For each instructor, which courses do they teach and
         * what department offers those courses?
         *
         * Tables:
         * instructor + teaches + course
         */

        System.out.println();
        System.out.println("QUERY 4:");
        System.out.println(
                "For each instructor, which courses did they teach in 2003 "
                        + "and what department offers those courses?");
        System.out.println();

        Relation instructorNamesQ4 = ra.project(
                instructor,
                List.of("ID", "name"));

        instructorNamesQ4 = ra.rename(
                instructorNamesQ4,
                List.of("name"),
                List.of("instructor"));

        Relation courseInfoQ4 = ra.project(
                course,
                List.of("course_id", "title", "dept_name", "credits"));

        courseInfoQ4 = ra.rename(
                courseInfoQ4,
                List.of("title", "dept_name"),
                List.of("course", "offering_department"));

        int teachesYearIndexQ4 = teaches.getAttrIndex("year");

        Relation teaches2003Q4 = ra.select(
                teaches,
                row -> row.get(teachesYearIndexQ4).getAsInt() == 2003);

        Relation instructorTeachesQ4 = ra.join(
                instructorNamesQ4,
                teaches2003Q4);

        Relation query4Joined = ra.join(
                instructorTeachesQ4,
                courseInfoQ4);

        Relation query4Result = ra.project(
                query4Joined,
                List.of(
                        "instructor",
                        "course",
                        "offering_department",
                        "credits",
                        "semester",
                        "year"));

        query4Result.print();

        /*
         * QUERY 5 (Nash)
         *
         * Which instructors taught which courses in 2010, and in what
         * room (building, room number, capacity)?
         *
         * Tables:
         * instructor + teaches + section + classroom + course
         */

        System.out.println();
        System.out.println("QUERY 5:");
        System.out.println(
                "Which instructors taught which courses in 2010, "
                        + "and in what room (building, room number, capacity)?");
        System.out.println();

        Relation instructorInfoQ5 = ra.project(
                instructor,
                List.of("ID", "name"));

        instructorInfoQ5 = ra.rename(
                instructorInfoQ5,
                List.of("name"),
                List.of("instructor"));

        int teachesYearIndexQ5 = teaches.getAttrIndex("year");

        Relation teaches2010Q5 = ra.select(
                teaches,
                row -> row.get(teachesYearIndexQ5).getAsInt() == 2010);

        Relation instructorTeachesQ5 = ra.join(
                instructorInfoQ5,
                teaches2010Q5);

        Relation instructorTeachesSectionQ5 = ra.join(
                instructorTeachesQ5,
                section);

        Relation instructorTeachesRoomQ5 = ra.join(
                instructorTeachesSectionQ5,
                classroom);

        Relation courseInfoQ5 = ra.project(
                course,
                List.of("course_id", "title"));

        courseInfoQ5 = ra.rename(
                courseInfoQ5,
                List.of("title"),
                List.of("course"));

        Relation query5Joined = ra.join(
                instructorTeachesRoomQ5,
                courseInfoQ5);

        Relation query5Result = ra.project(
                query5Joined,
                List.of(
                        "instructor",
                        "course",
                        "semester",
                        "year",
                        "building",
                        "room_number",
                        "capacity"));

        query5Result.print();

        /*
         * QUERY 6 (Nash)
         *
         * For Statistics students with at least 80 total credits,
         * who is their advisor and what department is the advisor in?
         *
         * Tables:
         * student + advisor + instructor
         */

        System.out.println();
        System.out.println("QUERY 6:");
        System.out.println(
                "For Statistics students with at least 80 total credits, "
                        + "who is their advisor and what department is the advisor in?");
        System.out.println();

        int studentDeptIndexQ6 = student.getAttrIndex("dept_name");
        int studentTotCredIndexQ6 = student.getAttrIndex("tot_cred");

        Relation statsStudentsQ6 = ra.select(
                student,
                row -> row.get(studentDeptIndexQ6)
                        .getAsString()
                        .equals("Statistics")
                        &&
                        row.get(studentTotCredIndexQ6)
                                .getAsInt() >= 80);

        Relation studentInfoQ6 = ra.project(
                statsStudentsQ6,
                List.of("ID", "name", "dept_name", "tot_cred"));

        studentInfoQ6 = ra.rename(
                studentInfoQ6,
                List.of("name", "dept_name"),
                List.of("student", "student_department"));

        Relation advisorRenamedQ6 = ra.rename(
                advisor,
                List.of("s_ID"),
                List.of("ID"));

        Relation studentAdvisorQ6 = ra.join(
                studentInfoQ6,
                advisorRenamedQ6);

        Relation instructorInfoQ6 = ra.project(
                instructor,
                List.of("ID", "name", "dept_name"));

        instructorInfoQ6 = ra.rename(
                instructorInfoQ6,
                List.of("ID", "name", "dept_name"),
                List.of("i_ID", "advisor", "advisor_department"));

        Relation query6Joined = ra.join(
                studentAdvisorQ6,
                instructorInfoQ6);

        Relation query6Result = ra.project(
                query6Joined,
                List.of(
                        "student",
                        "student_department",
                        "tot_cred",
                        "advisor",
                        "advisor_department"));

        query6Result.print();

        /*
         * QUERY 3 (Brendan)
         *
         * Which advisors advise History students with at least 90
         * total credits, and what department is each advisor in?
         *
         * Tables:
         * student + advisor + instructor
         */

        System.out.println();
        System.out.println("QUERY 7:");
        System.out.println(
                "Which advisors advise History students with at least 90 "
                        + "total credits, and what department is each advisor in?");
        System.out.println();

        int studentDeptIndexQ7 = student.getAttrIndex("dept_name");
        int studentTotCredIndexQ7 = student.getAttrIndex("tot_cred");

        Relation historyStudentsQ7 = ra.select(
                student,
                row -> row.get(studentDeptIndexQ7)
                        .getAsString()
                        .equals("History")
                        &&
                        row.get(studentTotCredIndexQ7)
                                .getAsInt() >= 90);

        Relation studentInfoQ7 = ra.project(
                historyStudentsQ7,
                List.of("ID", "name", "dept_name"));

        studentInfoQ7 = ra.rename(
                studentInfoQ7,
                List.of("ID", "name", "dept_name"),
                List.of("s_ID", "student", "student_department"));

        Relation studentAdvisorQ7 = ra.join(
                studentInfoQ7,
                advisor);

        Relation instructorInfoQ7 = ra.project(
                instructor,
                List.of("ID", "name", "dept_name"));

        instructorInfoQ7 = ra.rename(
                instructorInfoQ7,
                List.of("ID", "name", "dept_name"),
                List.of("i_ID", "advisor", "advisor_department"));

        Relation query7Joined = ra.join(
                studentAdvisorQ7,
                instructorInfoQ7);

        Relation query7Result = ra.project(
                query7Joined,
                List.of(
                        "student",
                        "student_department",
                        "advisor",
                        "advisor_department"));

        query7Result.print();
    }
}
