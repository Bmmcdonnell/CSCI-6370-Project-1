package uga.csx370.mydbimpl;

import java.util.ArrayList;
import java.util.List;

import uga.csx370.mydb.Cell;
import uga.csx370.mydb.Predicate;
import uga.csx370.mydb.RA;
import uga.csx370.mydb.Relation;
import uga.csx370.mydb.RelationBuilder;
import uga.csx370.mydb.Type;

public class RAImpl implements RA {

    @Override
    public Relation select(Relation rel, Predicate p) {
        Relation result = new RelationBuilder()
                .attributeNames(rel.getAttrs())
                .attributeTypes(rel.getTypes())
                .build();

        for (int i = 0; i < rel.getSize(); i++) {
            List<Cell> row = rel.getRow(i);

            if (p.check(row)) {
                result.insert(row);
            }
        }

        return result;
    }

    @Override
    public Relation project(Relation rel, List<String> attrs) {
        // Check that all attributes exist
        for (String attr : attrs) {
            if (!rel.hasAttr(attr)) {
                throw new IllegalArgumentException(
                        "Attribute does not exist: " + attr);
            }
        }

        List<Type> types = new ArrayList<>();

        for (String attr : attrs) {
            int index = rel.getAttrIndex(attr);
            types.add(rel.getTypes().get(index));
        }

        Relation result = new RelationBuilder()
                .attributeNames(attrs)
                .attributeTypes(types)
                .build();

        // Create each projected row
        for (int i = 0; i < rel.getSize(); i++) {
            List<Cell> oldRow = rel.getRow(i);
            List<Cell> newRow = new ArrayList<>();

            for (String attr : attrs) {
                int index = rel.getAttrIndex(attr);
                newRow.add(oldRow.get(index));
            }

            // Projection should not contain duplicate rows
            if (!containsRow(result, newRow)) {
                result.insert(newRow);
            }
        }

        return result;
    }

    @Override
    public Relation union(Relation rel1, Relation rel2) {
        // Relations must have the same attributes and types
        if (!rel1.getAttrs().equals(rel2.getAttrs())
                || !rel1.getTypes().equals(rel2.getTypes())) {
            throw new IllegalArgumentException(
                    "Relations are not compatible for union.");
        }

        Relation result = new RelationBuilder()
                .attributeNames(rel1.getAttrs())
                .attributeTypes(rel1.getTypes())
                .build();

        // Add rows from rel1
        for (int i = 0; i < rel1.getSize(); i++) {
            List<Cell> row = rel1.getRow(i);

            if (!containsRow(result, row)) {
                result.insert(row);
            }
        }

        // Add rows from rel2
        for (int i = 0; i < rel2.getSize(); i++) {
            List<Cell> row = rel2.getRow(i);

            if (!containsRow(result, row)) {
                result.insert(row);
            }
        }

        return result;
    }

    @Override
    public Relation intersect(Relation rel1, Relation rel2) {
        // Relations must have the same attributes and types
        if (!rel1.getAttrs().equals(rel2.getAttrs())
                || !rel1.getTypes().equals(rel2.getTypes())) {
            throw new IllegalArgumentException(
                    "Relations are not compatible for intersection.");
        }

        Relation result = new RelationBuilder()
                .attributeNames(rel1.getAttrs())
                .attributeTypes(rel1.getTypes())
                .build();

        for (int i = 0; i < rel1.getSize(); i++) {
            List<Cell> row = rel1.getRow(i);

            if (containsRow(rel2, row) && !containsRow(result, row)) {
                result.insert(row);
            }
        }

        return result;
    }

    @Override
    public Relation diff(Relation rel1, Relation rel2) {
        // Relations must have the same attributes and types
        if (!rel1.getAttrs().equals(rel2.getAttrs())
                || !rel1.getTypes().equals(rel2.getTypes())) {
            throw new IllegalArgumentException(
                    "Relations are not compatible for difference.");
        }

        Relation result = new RelationBuilder()
                .attributeNames(rel1.getAttrs())
                .attributeTypes(rel1.getTypes())
                .build();

        for (int i = 0; i < rel1.getSize(); i++) {
            List<Cell> row = rel1.getRow(i);

            if (!containsRow(rel2, row) && !containsRow(result, row)) {
                result.insert(row);
            }
        }

        return result;
    }

    @Override
    public Relation rename(Relation rel, List<String> origAttr,
            List<String> renamedAttr) {

        // The two lists must have the same number of attributes
        if (origAttr.size() != renamedAttr.size()) {
            throw new IllegalArgumentException(
                    "Attribute lists must have the same size.");
        }

        // Check that original attributes exist
        for (String attr : origAttr) {
            if (!rel.hasAttr(attr)) {
                throw new IllegalArgumentException(
                        "Attribute does not exist: " + attr);
            }
        }

        List<String> newAttrs = rel.getAttrs();

        // Rename the requested attributes
        for (int i = 0; i < origAttr.size(); i++) {
            int index = rel.getAttrIndex(origAttr.get(i));
            newAttrs.set(index, renamedAttr.get(i));
        }

        Relation result = new RelationBuilder()
                .attributeNames(newAttrs)
                .attributeTypes(rel.getTypes())
                .build();

        // Copy the rows
        for (int i = 0; i < rel.getSize(); i++) {
            result.insert(rel.getRow(i));
        }

        return result;
    }

    @Override
    public Relation cartesianProduct(Relation rel1, Relation rel2) {
        // Cartesian product cannot have common attributes
        for (String attr : rel1.getAttrs()) {
            if (rel2.hasAttr(attr)) {
                throw new IllegalArgumentException(
                        "Relations have common attributes: " + attr);
            }
        }

        List<String> attrs = new ArrayList<>();
        attrs.addAll(rel1.getAttrs());
        attrs.addAll(rel2.getAttrs());

        List<Type> types = new ArrayList<>();
        types.addAll(rel1.getTypes());
        types.addAll(rel2.getTypes());

        Relation result = new RelationBuilder()
                .attributeNames(attrs)
                .attributeTypes(types)
                .build();

        // Combine every row from rel1 with every row from rel2
        for (int i = 0; i < rel1.getSize(); i++) {
            List<Cell> row1 = rel1.getRow(i);

            for (int j = 0; j < rel2.getSize(); j++) {
                List<Cell> row2 = rel2.getRow(j);

                List<Cell> newRow = new ArrayList<>();
                newRow.addAll(row1);
                newRow.addAll(row2);

                result.insert(newRow);
            }
        }

        return result;
    }

    @Override
    public Relation join(Relation rel1, Relation rel2) {
        // Find common attributes
        List<String> commonAttrs = new ArrayList<>();

        for (String attr : rel1.getAttrs()) {
            if (rel2.hasAttr(attr)) {
                commonAttrs.add(attr);
            }
        }

        // Create result attributes
        List<String> resultAttrs = new ArrayList<>();
        resultAttrs.addAll(rel1.getAttrs());

        for (String attr : rel2.getAttrs()) {
            if (!commonAttrs.contains(attr)) {
                resultAttrs.add(attr);
            }
        }

        // Create result types
        List<Type> resultTypes = new ArrayList<>();
        resultTypes.addAll(rel1.getTypes());

        for (String attr : rel2.getAttrs()) {
            if (!commonAttrs.contains(attr)) {
                int index = rel2.getAttrIndex(attr);
                resultTypes.add(rel2.getTypes().get(index));
            }
        }

        Relation result = new RelationBuilder()
                .attributeNames(resultAttrs)
                .attributeTypes(resultTypes)
                .build();

        // Compare every row from rel1 with every row from rel2
        for (int i = 0; i < rel1.getSize(); i++) {
            List<Cell> row1 = rel1.getRow(i);

            for (int j = 0; j < rel2.getSize(); j++) {
                List<Cell> row2 = rel2.getRow(j);

                boolean matches = true;

                // Check common attributes
                for (String attr : commonAttrs) {
                    int index1 = rel1.getAttrIndex(attr);
                    int index2 = rel2.getAttrIndex(attr);

                    if (!row1.get(index1).equals(row2.get(index2))) {
                        matches = false;
                        break;
                    }
                }

                if (matches) {
                    List<Cell> newRow = new ArrayList<>();
                    newRow.addAll(row1);

                    // Add only non-common attributes from rel2
                    for (String attr : rel2.getAttrs()) {
                        if (!commonAttrs.contains(attr)) {
                            int index = rel2.getAttrIndex(attr);
                            newRow.add(row2.get(index));
                        }
                    }

                    if (!containsRow(result, newRow)) {
                        result.insert(newRow);
                    }
                }
            }
        }

        return result;
    }

    @Override
    public Relation join(Relation rel1, Relation rel2, Predicate p) {
        // Theta join cannot have common attributes
        for (String attr : rel1.getAttrs()) {
            if (rel2.hasAttr(attr)) {
                throw new IllegalArgumentException(
                        "Relations have common attributes: " + attr);
            }
        }

        // Combine attributes
        List<String> attrs = new ArrayList<>();
        attrs.addAll(rel1.getAttrs());
        attrs.addAll(rel2.getAttrs());

        // Combine types
        List<Type> types = new ArrayList<>();
        types.addAll(rel1.getTypes());
        types.addAll(rel2.getTypes());

        Relation result = new RelationBuilder()
                .attributeNames(attrs)
                .attributeTypes(types)
                .build();

        // Try every combination of rows
        for (int i = 0; i < rel1.getSize(); i++) {
            List<Cell> row1 = rel1.getRow(i);

            for (int j = 0; j < rel2.getSize(); j++) {
                List<Cell> row2 = rel2.getRow(j);

                List<Cell> combinedRow = new ArrayList<>();
                combinedRow.addAll(row1);
                combinedRow.addAll(row2);

                if (p.check(combinedRow)) {
                    if (!containsRow(result, combinedRow)) {
                        result.insert(combinedRow);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Checks whether a relation already contains a row.
     */
    private boolean containsRow(Relation rel, List<Cell> row) {
        for (int i = 0; i < rel.getSize(); i++) {
            if (rel.getRow(i).equals(row)) {
                return true;
            }
        }

        return false;
    }
}