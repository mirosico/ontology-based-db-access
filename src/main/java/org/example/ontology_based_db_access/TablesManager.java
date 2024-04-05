package org.example.ontology_based_db_access;

import org.example.ontology_based_db_access.databases.DB;
import org.example.ontology_based_db_access.databases.Neo4j;
import org.example.ontology_based_db_access.databases.MongoDB;
import org.example.ontology_based_db_access.databases.MySQL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class TablesManager {
    private static Map<String, Set<String>> connectedTables = new java.util.HashMap<>();

    public Set<String> getConnectedTables(String tableType) {
        return connectedTables.get(tableType);
    }

    public void addConnectedTable(String tableType, String tableName) {
        if (connectedTables.get(tableType) == null) {
            connectedTables.put(tableType, new java.util.HashSet<>());
        }
        connectedTables.get(tableType).add(tableName);
    }

    public void removeConnectedTable(String tableType, String tableName) {
        if (connectedTables.get(tableType) != null) {
            connectedTables.get(tableType).remove(tableName);
        }
    }


    private final String[] tableTypes = {"rdb", "odb", "gdb"};
    public String[] getTableTypes() {
        return tableTypes;
    }


    private static ArrayList<ArrayList<String>> linkedTablesData = new ArrayList<>();
    public ArrayList<ArrayList<String>> getLinkedTablesData() {
        return linkedTablesData;
    }

    public void addLinkedTablesData(ArrayList<String> linkedTables) {
        linkedTablesData.add(linkedTables);
    }
    public void removeLinkedTablesData(int index) {
        linkedTablesData.remove(index);
    }


    public Map<String, String[]> getMappedLinkedData() {
        Map<String, String[]> linkedData = new java.util.HashMap<>();
        for (ArrayList<String> row : linkedTablesData) {
            for (String item: row) {
                String[] allItemsExceptCurrent = Arrays.stream(row.toArray(new String[0])).filter(x -> !x.equals(item)).toArray(String[]::new);
                linkedData.put(item, allItemsExceptCurrent);
            }
        }
        return linkedData;
    }

    public DB getDB(String tableType) {
        switch (tableType) {
            case "rdb":
                return new MySQL();
            case "odb":
                return new MongoDB();
            case "gdb":
                return new Neo4j();
            default:
                return null;
        }
    }

    public String[] getColumns(String tableType, String tableName) {
        DB db = getDB(tableType);
        return db != null ? db.getCollectionProps(tableName) : null;
    }


}
