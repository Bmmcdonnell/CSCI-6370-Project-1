/**
 * Copyright (c) 2025 Sami Menik, PhD. All rights reserved.
 * 
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 * This software is provided "as is," without warranty of any kind.
 */
package uga.csx370.mydb;

import java.util.HashSet;
import java.util.List;

/**
 * Builder class to create a new relation.
 */
public class RelationBuilder {

    private List<String> attrNames; // Attribute names to be used when building the relation.
    private List<Type> attrTypes; // Attribute types to be used when building the relation.

    /**
     * Set the attribute names for the new relation to be built.
     * 
     * @param attrNames list of attribute names.
     * 
     * @return the same instance of the builder for config chaining.
     * 
     * @throws IllegalArgumentException if the attrNames are invalid.
     */
    public RelationBuilder attributeNames(List<String> attrNames) {
        if (attrNames == null) {
            throw new NullPointerException("Attribute names can not be null.");
        }
        if (attrTypes != null && attrTypes.size() != attrNames.size()) {
            throw new IllegalArgumentException("Number of attribute names is not equal to the " 
                            + "number of attribute types.");
        }
        for (String attrName: attrNames) {
            if (attrName == null || attrName.trim().length() == 0) {
                throw new IllegalArgumentException("No attribute names can be null or empty.");
            }
        }
        if (new HashSet<>(attrNames).size() != attrNames.size()) {
            throw new IllegalArgumentException("Attribute names are not unique.");
        }
        this.attrNames = attrNames;
        return this;
    }

    /**
     * Set the attribute types for the new relation to be built.
     * 
     * @param attrTypes list of attribute types.
     * 
     * @return the same instance of the builder for config chaining.
     * 
     * @throws IllegalArgumentException if the attibute types are invalid.
     */
    public RelationBuilder attributeTypes(List<Type> attrTypes) {
        if (attrTypes == null) {
            throw new NullPointerException("Attribute types can not be null.");
        }
        if (attrNames != null && attrNames.size() != attrTypes.size()) {
            throw new IllegalArgumentException("Number of attribute types is not equal to the " 
                            + "number of attribute names.");
        }
        for (Type type: attrTypes) {
            if (type == null) {
                throw new IllegalArgumentException("No attribute type can be null.");
            }
        }
        this.attrTypes = attrTypes;
        return this;
    }
    
    /**
     * Create a new Relation instance.
     * 
     * @throws IllegalStateException if attribute names or types are not set.
     * 
     * @return new Relation instance with the properties configured using the builder.
     */
    public Relation build() {
        if (attrTypes == null || attrNames == null) {
            throw new IllegalArgumentException("Attribute names or attribute types are not set.");
        }
        return new RelationImpl(attrTypes, attrNames);
    }
    
}
