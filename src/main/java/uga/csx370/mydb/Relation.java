/**
 * Copyright (c) 2025 Sami Menik, PhD. All rights reserved.
 * 
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 * This software is provided "as is," without warranty of any kind.
 */
package uga.csx370.mydb;

import java.util.List;

/**
 * Represents a relation instance.
 */
public interface Relation {

    /**
     * Returns the row count of the relation.
     */
    public int getSize();

    /**
     * Get the row at position i in the relation. i is a 0 based index.
     * Return a deep copy of the row to avoid modifications to the 
     * returned row by the caller of this method.
     * 
     * @param i position of the row to return.
     * 
     * @throws IllegalArgumentException if i < 0 or i >= getSize().
     */
    public List<Cell> getRow(int i);

    /**
     * Return the type of each column in a list.
     */
    public List<Type> getTypes();

    /**
     * Returns the list of attributes of the relation.
     */
    public List<String> getAttrs();

    /**
     * Returns true only if attr exist in the relation.
     */
    public boolean hasAttr(String attr);

    /**
     * Returns the index of the attr.
     * 
     * @throws IllegalArgumentException if attr does not 
     * exist in the relation.
     */
    public int getAttrIndex(String attr);

    /**
     * Inserts a row in the relation.
     * 
     * @throws IllegalArgumentException if the cell types do not correspond 
     * to the attribute types of the relation.
     */
    public void insert(List<Cell> row);

    /**
     * Load data from a CSV file given by the path.
     * 
     * @param path a path to a valid CSV file that matches the relation schema.
     * 
     * @throws IllegalArgumentException if the file does not exist or if
     * the file format is not correct or if the CSV file does not match the schema
     * of the relation.
     */
    public void loadData(String path);

    /**
     * Print the relation properly formatted as a table 
     * to the standard output.
     * The result should look similar to MySql table outputs.
     */
    public void print();

}
