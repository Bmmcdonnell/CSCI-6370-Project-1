/**
 * Copyright (c) 2025 Sami Menik, PhD. All rights reserved.
 * 
 * Unauthorized copying of this file, via any medium, is strictly prohibited.
 * This software is provided "as is," without warranty of any kind.
 */
package uga.csx370.mydb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the Relation interface.
 */
class RelationImpl implements Relation {

    private List<String> attributes;
    private List<Type> types;
    private List<List<Cell>> rows;
    private Map<String, Integer> attrIndex;

    RelationImpl(List<Type> types, List<String> attributes) {
        this.types = types;
        this.attributes = attributes;
        attrIndex = new HashMap<>();
        for (int i = 0; i < attributes.size(); ++i) {
            attrIndex.put(attributes.get(i), i);
        }
        rows = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize() {
        return rows.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Cell> getRow(int i) {
        if (i < 0 || i >= getSize()) {
            throw new IllegalArgumentException("Row index out of bounds.");
        }
        return new ArrayList<>(rows.get(i));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Type> getTypes() {
        return new ArrayList<>(types);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAttrs() {
        return new ArrayList<>(attributes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasAttr(String attr) {
        return attrIndex.containsKey(attr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAttrIndex(String attr) {
        if (!attrIndex.containsKey(attr)) {
            throw new IllegalArgumentException("Attribute does not exist: " + attr);
        }
        return attrIndex.get(attr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert(List<Cell> row) {
        if (row.size() != types.size()) {
            throw new IllegalArgumentException("Row size does not match the relation schema.");
        }
        for (int i = 0; i < row.size(); ++i) {
            if (row.get(i).getType() != types.get(i)) {
                throw new IllegalArgumentException("Value types in the row do not "
                        + "match the schema.");
            }
        }
        rows.add(new ArrayList<>(row));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadData(String path) {
        // Check if the file exists
        if (!Files.exists(Paths.get(path))) {
            throw new IllegalArgumentException("File does not exist: " + path);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split the line into values, considering the CSV format
                String[] values = line.split("\",\"", -1);
                if (values.length != this.types.size()) {
                    throw new IllegalArgumentException("CSV file format does not match the "
                            + "schema of the relation.");
                }
                // Remove leading and trailing quotes from the first and last elements
                values[0] = values[0].replaceFirst("^\"", "");
                values[values.length - 1] = values[values.length - 1].replaceAll("\"$", "");

                List<Cell> row = new ArrayList<>();
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].replace("\"\"", "\""); // unescape double quotes.
                    Type type = this.types.get(i);
                    try {
                        switch (type) {
                            case INTEGER:
                                row.add(Cell.val(Integer.parseInt(values[i])));
                                break;
                            case DOUBLE:
                                row.add(Cell.val(Double.parseDouble(values[i])));
                                break;
                            case STRING:
                                row.add(Cell.val(values[i]));
                                break;
                        }
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Error parsing value '" + values[i] + "' for type " + type);
                    }
                }
                this.insert(row); // Insert the parsed row into the relation
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read data from path: " + path);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void print() {
        // Maximum width for any column
        final int MAX_COLUMN_WIDTH = 20;

        // Calculate the width of each column
        List<Integer> columnWidths = new ArrayList<>();
        for (int i = 0; i < attributes.size(); i++) {
            int maxWidth = attributes.get(i).length();
            for (List<Cell> row : rows) {
                String cellValue = row.get(i).toString();
                int cellWidth = cellValue.length();
                if (cellWidth > maxWidth) {
                    maxWidth = cellWidth;
                }
            }
            columnWidths.add(Math.min(maxWidth, MAX_COLUMN_WIDTH));
        }

        // Print row divider
        System.out.print("+");
        for (int width : columnWidths) {
            System.out.print("-".repeat(width + 2) + "+");
        }
        System.out.println();

        // Print table header
        System.out.print("| ");
        for (int i = 0; i < attributes.size(); i++) {
            System.out.print(String.format("%-" + columnWidths.get(i) + "s | ", attributes.get(i)));
        }
        System.out.println();

        // Print row divider
        System.out.print("+");
        for (int width : columnWidths) {
            System.out.print("-".repeat(width + 2) + "+");
        }
        System.out.println();

        // Print each row
        for (List<Cell> row : rows) {
            System.out.print("| ");
            for (int i = 0; i < row.size(); i++) {
                Cell cell = row.get(i);
                int width = columnWidths.get(i);
                switch (cell.getType()) {
                    case INTEGER:
                        System.out.printf("%" + width + "d | ", cell.getAsInt());
                        break;
                    case DOUBLE:
                        // Assuming you want to limit the decimal places to 2 for display
                        System.out.printf("%" + width + ".2f | ", cell.getAsDouble());
                        break;
                    case STRING:
                        System.out.printf("%-" + width + "s | ", cell.getAsString());
                        break;
                }
            }
            System.out.println();
        }

        // Print row divider
        System.out.print("+");
        for (int width : columnWidths) {
            System.out.print("-".repeat(width + 2) + "+");
        }
        System.out.println();
    }

}