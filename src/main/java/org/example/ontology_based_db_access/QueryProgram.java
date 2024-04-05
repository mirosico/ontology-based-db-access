package org.example.ontology_based_db_access;

import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.example.ontology_based_db_access.databases.DB;
import org.example.ontology_based_db_access.ontology.OWLModel;
import org.example.ontology_based_db_access.ontology.RDFModel;
import org.example.ontology_based_db_access.ontology.SparqlModelExecutor;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryProgram {
    private TablesManager tablesManager;
    private ArrayList<Model> models = new ArrayList<>();
    private String prefixString = "";
    private OWLModel owlModel;


    public QueryProgram(OWLModel owlModel, TablesManager tablesManager) {
        this.owlModel = owlModel;
        this.tablesManager = tablesManager;
    }


    private String findOtherFieldName(String t1, String type1, String t2, String type2, String field2) {
        String key = type2 + "." + t2 + "." + field2;
        TablesManager tablesManager = new TablesManager();
        String[] linkedKeys = tablesManager.getMappedLinkedData().get(key);
        if (linkedKeys == null) {
            return null;
        }
        for (String linkedKey : linkedKeys) {
            if (!linkedKey.startsWith(type1 + "." + t1)) continue;
            return linkedKey.split("\\.")[2];
        }
        return null;
    }

    private String getFieldForQuery(String t1, String type1, String field2) {
        DB db = tablesManager.getDB(type1);
        if (db != null && Arrays.asList(db.getCollectionProps(t1)).contains(field2)) {
            return field2;
        }
        String[] otherTypes = Arrays.stream(tablesManager.getTableTypes()).filter(s -> !s.equals(type1)).toArray(String[]::new);
        for (String otherType : otherTypes) {
            Set<String> otherTables = tablesManager.getConnectedTables(otherType);
            if (otherTables == null) continue;
            for (String otherTable : otherTables) {
                String resField = findOtherFieldName(t1, type1, otherTable, otherType, field2);
                if (resField == null) continue;
                return resField;
            }
        }
        return "";
    }


    private ArrayList<String> getTableFieldsToQuery(String table, String tableType, String[] fields) {
        ArrayList<String> resFields = new ArrayList<>();
        for (String field : fields) {
            String fieldForQuerry = getFieldForQuery(table, tableType, field);
            if (fieldForQuerry == null) continue;
            resFields.add(fieldForQuerry);
        }
        return resFields;
    }


    private String[] parseSelectFields(String query) {
        String selectString = Arrays.stream(query.split("\n")).filter(s -> s.matches("(?i)SELECT\\s+.*")).findFirst().orElse("");
        String[] fields = selectString.replaceFirst("(?i)SELECT\\s+", "").split("\\s+");
        String[] resFields = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            resFields[i] = fields[i].replaceFirst("\\?", "");
        }
        return resFields;
    }

    private List<String> parseFilter(String query) {
        String[] lines = query.split("\n");
        List<String> matches = new ArrayList<>();
        for (String line : lines) {
            if (line.matches("(?i)FILTER\\s+.*")) {
                String REGEX = "\\?\\w+\\s*=\\s*\".*\"";
                Pattern pattern = Pattern.compile(REGEX);
                Matcher matcher = pattern.matcher(line);

                while (matcher.find()) {
                    String match = matcher.group();
                    matches.add(match);
                }
            };
        }
        return matches;
    }

    private String parseQueryAdditionalInfo(String query) {
        String[] lines = query.split("\n");
        String additionalInfo = "";
        for (String line : lines) {
            if (line.matches("(?i)SELECT\\s+.*")) continue;
            if (line.matches("(?i)FILTER\\s+.*")) continue;
            additionalInfo += line + "\n";
        }
        return additionalInfo;
    }


    private String getSPARQLPrefix(String table, String tableType) {
        RDFModel rdfModel = new RDFModel(tableType, table);
        return "PREFIX " + rdfModel.getPrefix() + ": <" + rdfModel.getNS() + "> ";
    }

    private String getSPARQLUnion(String table, String tableType, String[] fields, ArrayList<String> resFields, String queryMode, String filter) {
        RDFModel rdfModel = new RDFModel(tableType, table);
        String resQuery = "";
        int index = 0;
        for (String field : resFields) {
            if (!field.isEmpty()) {
                resQuery += "?" + table + " " + rdfModel.getPrefix() + ":" + field + " ?" + fields[index] + " . ";
            }
            index++;
        }
        if (queryMode.equals("UNION")) {
            return " { " + resQuery + filter + " } UNION ";
        }
        return resQuery;
    }

    private String getRDBQuery(String table, ArrayList<String> resFields) {
        String[] resFieldsWithoutEmpty = Arrays.stream(resFields.toArray(new String[0])).filter(s -> !s.isEmpty()).toArray(String[]::new);
        if (resFieldsWithoutEmpty.length == 0) {
            return null;
        }
        String resFieldsString = String.join(", ", resFieldsWithoutEmpty);
        if (!resFieldsString.contains("id")) {
            resFieldsString = "id, " + resFieldsString;
        }
        return "SELECT " + resFieldsString + " FROM " + table;
    }

    private Model getUnitedModel() {
        Model unionModel = ModelFactory.createDefaultModel();
        for (Model model : models) {
            unionModel = unionModel.union(model);
        }
        return unionModel;
    }

    private String getFilterQueryLine(String originalQuery) {
        String[] lines = originalQuery.split("\n");
        for (String line : lines) {
            if (line.matches("(?i)FILTER\\s+.*")) {
                return line;
            };
        }
        return null;
    }

    private String getFilterValueFromFilterKeyValue(String filterKeyValue) {
        String[] parts = filterKeyValue.split("=");
        return parts[1].replace("\"", "").trim();
    }

    private String getFilterKeyFromFilterKeyValue(String filterKeyValue) {
        String[] parts = filterKeyValue.split("=");
        return parts[0];
    }

    private String getFilterKeyValueString(String filterKey, List<String> filterValues) {
        String res = "";
        for (String filterValue : filterValues) {
            res += filterKey + " = \"" + filterValue + "\" || ";
        }
        return res.substring(0, res.length() - 4);
    }


    private String getFilterQuery(String originalQuery) {
        String filterQueryLine = getFilterQueryLine(originalQuery);
        if (filterQueryLine == null) return "";
        List<String> filterValues = parseFilter(originalQuery);
        for (String filterValueString : filterValues) {
            String filterValue = getFilterValueFromFilterKeyValue(filterValueString);
            String filterKey = getFilterKeyFromFilterKeyValue(filterValueString);
            List<String> newFilterValues = owlModel.getAllPossibleSameValues(filterValue);
            String newFilterValueString = getFilterKeyValueString(filterKey, newFilterValues);
            filterQueryLine = filterQueryLine.replace(filterValueString, newFilterValueString);
        }
        return filterQueryLine;
    }


    private String getSPARQLQuery(String originalQuery, String queryMode) {
        String[] fields = parseSelectFields(originalQuery);
        String filterQuery = getFilterQuery(originalQuery);
        String resQuery = "SELECT ";
        for (String field : fields) {
            resQuery += "?" + field + " ";
        }
        String unionQuery = getUnionQuery(fields, queryMode, filterQuery);
        resQuery += "WHERE { " + unionQuery;
        if (queryMode.equals("UNION")) {
            resQuery = resQuery.substring(0, resQuery.length() - 7);
        } else {
            resQuery += " " + filterQuery;
        }

        String extraQueryOptions = parseQueryAdditionalInfo(originalQuery);

        return prefixString + resQuery + " }" + extraQueryOptions;
    }

    private String getDBQuery(String tableType, String table, ArrayList<String> resFields) {
        return tableType.equals("rdb") ? getRDBQuery(table, resFields) : table;
    }

    private String getUnionQuery(String[] fields, String queryMode, String filterQuery) {
        String unionQuery = "";
        for (String tableType : tablesManager.getTableTypes()) {
            DB db = tablesManager.getDB(tableType);
            Set<String> connectedCollections = tablesManager.getConnectedTables(tableType);
            if (connectedCollections == null || connectedCollections.isEmpty()) {
                continue;
            }
            for (String table : connectedCollections) {
                ArrayList<String> resFields = getTableFieldsToQuery(table, tableType, fields);
                String dbQuery = getDBQuery(tableType, table, resFields);
                if (dbQuery != null) {
                    Model tableModel = db.convertToRDF(dbQuery);
                    models.add(tableModel);
                    prefixString += getSPARQLPrefix(table, tableType);
                    unionQuery += getSPARQLUnion(table, tableType, fields, resFields, queryMode, filterQuery);
                }

            }
        }
        return unionQuery;
    }

    public ResultSet executeQuery(String query, String queryMode) {
        prefixString = "";
        models = new ArrayList<>();

        String sparqlQuery = getSPARQLQuery(query, queryMode);
        Model unionModel = getUnitedModel();

        System.out.println(sparqlQuery);

        SparqlModelExecutor sparqlModelExecutor = new SparqlModelExecutor(unionModel);

        return sparqlModelExecutor.executeQuery(sparqlQuery);
    }
}
