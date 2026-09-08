/**
 * Copyright (c) 2025 Sami Menik, PhD. All rights reserved.
 * 
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 * This software is provided "as is," without warranty of any kind.
 */
package uga.csx370.mydb;

import java.util.List;

/**
 * Functional interface to represent a predicate
 * in relational algebra operators.
 */
public interface Predicate {

    /**
     * Checks a row for a condition and returns true
     * if the row passes the predicate.
     */
    public boolean check(List<Cell> row);
    
}
