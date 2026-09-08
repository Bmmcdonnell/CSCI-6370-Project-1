/**
 * Copyright (c) 2025 Sami Menik, PhD. All rights reserved.
 * 
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 * This software is provided "as is," without warranty of any kind.
 */
package uga.csx370.mydbimpl;

import java.util.List;

import uga.csx370.mydb.Relation;
import uga.csx370.mydb.RelationBuilder;
import uga.csx370.mydb.Type;

public class Driver {
    
    public static void main(String[] args) {
        // Following is an example of how to use the relation class.
        // This creates a table with three columns with below mentioned
        // column names and data types.
        // After creating the table, data is loaded from a CSV file.
        // Path should be replaced with a correct file path for a compatible
        // CSV file.
        System.out.println("bmm34505");
        Relation rel1 = new RelationBuilder()
                .attributeNames(List.of("ID", "name", "dept_name", "salary"))
                .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.STRING, Type.DOUBLE))
                .build();
        rel1.loadData("C:/Users/brend/CSCI6370Files/mysql-files/instructor_export.csv");
        rel1.print();
        Relation rel2 = new RelationBuilder()
                .attributeNames(List.of("ID", "name", "dept_name", "total_credit"))
                .attributeTypes(List.of(Type.INTEGER, Type.STRING, Type.STRING, Type.INTEGER))
                .build();
        rel2.loadData("C:/Users/brend/CSCI6370Files/mysql-files/student_export.csv");
        rel2.print();
        System.out.println("bmm34505");
        //Because of the size of the student export, I commented it out to see the instructor export.
        //As well as a second print statement for the screenshot
    }

}
