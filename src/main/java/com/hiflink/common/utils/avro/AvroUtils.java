package com.hiflink.common.utils.avro;

import com.hiflink.common.utils.exceptions.AvroFieldNotFoundException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AvroUtils {


    public  Map<String, String> getParameters(String schemaToString) {
        Map<String, String> mapFields = new HashMap<>();
        JSONObject schema = new JSONObject(schemaToString);
        JSONArray arr = schema.getJSONArray("fields");
        for (int i = 0; i < arr.length(); i++) {
            String value = arr.getJSONObject(i).getString("name");
            //System.out.println(value);
            JSONArray arrType = arr.getJSONObject(i).getJSONArray("type");

            for (int j = 1; j < arrType.length(); j++) {
                try {

                    String type = arrType.getJSONObject(j).getString("type");

                    mapFields.put(value, type);
                    break;
                } catch (JSONException e) {
                    String type = arrType.get(1).toString();
                    if (type.equals("null")) {
                        type = arrType.getJSONObject(1).getString("type");
                        if (!type.equals("map")) {
                            try {
                                type = type + "-" + arrType.getJSONObject(1).getJSONObject("items").getString("type");
                            } catch (JSONException ex) {
                                type = type + "-" + arrType.getJSONObject(1).getString("items");
                            }
                        }
                    }
                    mapFields.put(value, type);
                    break;
                }
            }
        }
        return mapFields;
    }

    public Schema.Type inferType(Schema schema) throws Exception {
        try {
            Schema.Type resultType = Schema.Type.NULL;


            if (schema.getType().equals(Schema.Type.ENUM) ||
                    schema.getType().equals(Schema.Type.FIXED) ||
                    schema.getType().equals(Schema.Type.STRING) ||
                    schema.getType().equals(Schema.Type.BYTES) ||
                    schema.getType().equals(Schema.Type.INT) ||
                    schema.getType().equals(Schema.Type.LONG) ||
                    schema.getType().equals(Schema.Type.FLOAT) ||
                    schema.getType().equals(Schema.Type.DOUBLE) ||
                    schema.getType().equals(Schema.Type.BOOLEAN) ||
                    schema.getType().equals(Schema.Type.RECORD) ||
                    schema.getType().equals(Schema.Type.NULL)) {
                return schema.getType();
            }


            List<Schema> attrArray = schema.getTypes();
            for (Schema attrRecord : attrArray) {
                resultType = attrRecord.getType();
                if (resultType != Schema.Type.NULL) {
                    break;
                }
            }
            return resultType;
        } catch (Exception e) {
            throw e;
        }
    }

    private boolean isBasicType(Schema.Type type) throws Exception {
        try {
            return type.equals(Schema.Type.ENUM) ||
                    type.equals(Schema.Type.FIXED) ||
                    type.equals(Schema.Type.STRING) ||
                    type.equals(Schema.Type.BYTES) ||
                    type.equals(Schema.Type.INT) ||
                    type.equals(Schema.Type.LONG) ||
                    type.equals(Schema.Type.FLOAT) ||
                    type.equals(Schema.Type.DOUBLE) ||
                    type.equals(Schema.Type.BOOLEAN) ||
                    type.equals(Schema.Type.NULL);
        } catch (Exception e) {
            throw e;
        }

    }

    public Schema inferSubSchema(Schema schema) throws Exception {

        try {
            Schema.Type resultType = Schema.Type.NULL;

            Schema resultSchema = null;

            List<Schema> attrArray = schema.getTypes();
            for (Schema attrRecord : attrArray) {
                resultType = attrRecord.getType();
                if (resultType == Schema.Type.MAP) {
                    resultSchema = attrRecord.getValueType();
                    break;
                } else if (resultType != Schema.Type.NULL) {
                    resultSchema = attrRecord.getElementType();
                    break;
                }
            }
            return resultSchema;
        } catch (Exception e) {
            throw e;
        }
    }


    public Object inferType(GenericRecord genericRecord) throws Exception {
        return inferType(genericRecord.getSchema());
    }

    public Object extractSchemaFromName(GenericRecord genericRecord, String name) {
        return genericRecord.getSchema().getField(name).schema();
    }

    public GenericRecord getGenericRecord(GenericRecord parentGenericRecord, String fieldName) throws Exception {
        try {
            GenericRecord newGenericRecord = null;


            //Gets from sended GenericRecord a new GenericRecord with searched type
            //Search generic record Type based on name
            Schema.Field parentField = parentGenericRecord.getSchema().getField(fieldName);

            //Search for type in sended schema
            Schema.Type parentType = inferType(parentField.schema());
            if (parentType.equals(Schema.Type.RECORD)) {
                newGenericRecord = new GenericData.Record(parentField.schema());
                parentGenericRecord.put(fieldName, newGenericRecord);
            } else if (parentType.equals(Schema.Type.ARRAY)) {
                Schema subtypeSchema = inferSubSchema(parentField.schema());
                newGenericRecord = new GenericData.Record(subtypeSchema);
                Schema arraySchema = Schema.createArray(subtypeSchema);
                GenericArray arrayRecord = parentGenericRecord.get(fieldName) == null ? new GenericData.Array(100, arraySchema) : ((GenericArray) parentGenericRecord.get(fieldName));
                arrayRecord.add(newGenericRecord);
                parentGenericRecord.put(fieldName, arrayRecord);
            } else if (parentType.equals(Schema.Type.MAP)) {
                Schema subtypeSchema = inferSubSchema(parentField.schema());
                newGenericRecord = new GenericData.Record(subtypeSchema);
                parentGenericRecord.put(fieldName, newGenericRecord);
            } else {
                newGenericRecord = parentGenericRecord;
            }

            return newGenericRecord;
        } catch (Exception e) {
            throw e;
        }

    }

    public Object getValue(GenericRecord genericRecord, String fieldPath) throws AvroFieldNotFoundException {
        return this.getValue(genericRecord, fieldPath, 0);
    }

    public void setValue(GenericRecord genericRecord, String fieldPath, Object dataToSet) throws AvroFieldNotFoundException {
        this.setValue(genericRecord, fieldPath, dataToSet, 0);
    }

    private Object getValue(GenericRecord genericRecord, String fieldPath, int currentPosition) throws AvroFieldNotFoundException {
        try {
            Object gettedValue = null;

            if (genericRecord != null) {
                String currentPath = fieldPath.split("\\.")[currentPosition];
                String fieldName = currentPath.split("\\[")[0];

                if (currentPath.contains("[") && currentPath.contains("]")) {
                    String element = currentPath.split("\\[")[1].replace("]", "");
                    int elementPosition = element != null ? Integer.parseInt(element) : 0;
                    if (genericRecord.get(fieldName) instanceof List) {
                        gettedValue = ((List) genericRecord.get(fieldName)).get(elementPosition);
                    } else {
                        gettedValue = ((GenericArray) genericRecord.get(fieldName)).get(elementPosition);
                    }

                } else {
                    gettedValue = genericRecord.get(fieldName);
                }

                if (!fieldPath.contains(".") || (fieldPath.contains(".") && (fieldPath.split("\\.").length - 1 == currentPosition))) {
                    return gettedValue;
                } else {
                    return getValue((GenericRecord) gettedValue, fieldPath, ++currentPosition);
                }
            }
//            throw new AvroFieldNotFoundException("Error reading data from: " + genericRecord.getSchema().getName() + " at field: " + fieldPath);
            return null;
        } catch (Exception e) {
            throw new AvroFieldNotFoundException("Error reading data from: " + genericRecord.getSchema().getName() + " at field: " + fieldPath);
        }

    }


    private void setValue(GenericRecord genericRecord, String fieldPath, Object dataToSet, int currentPosition) throws AvroFieldNotFoundException {
        Object placeToPut = null;
        Boolean hasPlaceToPut = false;
        try {
            if (genericRecord != null) {
                String currentPath = fieldPath.split("\\.")[currentPosition];
                String fieldName = currentPath.split("\\[")[0];

                if (currentPath.contains("[") && currentPath.contains("]")) {
                    String element = currentPath.split("\\[")[1].replace("]", "");
                    int elementPosition = element != null ? Integer.parseInt(element) : 0;
                    hasPlaceToPut = genericRecord.get(fieldName) != null && ((GenericArray) genericRecord.get(fieldName)).size() > elementPosition;
                    if (hasPlaceToPut) {
                        placeToPut = ((GenericArray) genericRecord.get(fieldName)).get(elementPosition);
                    }
                } else {
                    hasPlaceToPut = genericRecord.get(fieldName) != null;
                    if (hasPlaceToPut) {
                        if (genericRecord.get(fieldName) instanceof GenericArray) {
                            placeToPut = ((GenericArray) genericRecord.get(fieldName)).get(0);
                        } else if (isBasicType(inferType(genericRecord.getSchema().getField(fieldName).schema()))) {
                            placeToPut = genericRecord;
                        } else {
                            placeToPut = genericRecord.get(fieldName);
                        }
                    }
                }

                if (!hasPlaceToPut) {
                    placeToPut = this.getGenericRecord(genericRecord, fieldName);
                }

                if (!fieldPath.contains(".") || (fieldPath.contains(".") && (fieldPath.split("\\.").length - 1 == currentPosition))) {
                    ((GenericRecord) placeToPut).put(fieldName, getValueForData(dataToSet, ((GenericRecord) placeToPut).getSchema().getField(fieldName).schema()));//dataToSet);//getValue(dataToSet, ((GenericRecord) placeToPut).getSchema().getField(fieldName).schema())
                } else {
                    setValue((GenericRecord) placeToPut, fieldPath, dataToSet, ++currentPosition);
                }
            } else {
                throw new AvroFieldNotFoundException("Error reading data from: " + genericRecord.getSchema().getName() + " at field: " + fieldPath);
            }
        } catch (Exception e) {
            throw new AvroFieldNotFoundException("Error reading data from: " + genericRecord.getSchema().getName() + " at field: " + fieldPath);
        }
    }

    private Object getValueForData(Object value, Schema schema) throws Exception {
        try {
            if (schema.getName().toLowerCase().contains("union")) {
                List<Schema> types = schema.getTypes();
                for (Schema type : types) {
                    if (type.getType() != Schema.Type.NULL) {
                        return getValueForData(value, type);
                    }
                }
            } else {
                String evaluableName = schema.getType().getName();
                if (evaluableName.toLowerCase().contains("string")) {
                    return value.toString();
                } else if (evaluableName.toLowerCase().contains("double")) {
                    return Double.parseDouble(value.toString());
                } else if (evaluableName.toLowerCase().contains("int")) {
                    return Integer.parseInt(value.toString());
                } else if (evaluableName.toLowerCase().contains("float")) {
                    return Float.parseFloat(value.toString());
                } else if (evaluableName.toLowerCase().contains("long")) {
                    return Long.parseLong(value.toString());
                } else if (evaluableName.toLowerCase().contains("boolean")) {
                    return Boolean.parseBoolean(value.toString());
                }
            }
            return value.toString();
        } catch (Exception e) {
            throw e;
        }
    }

    public GenericRecord cloneGenericRecord(GenericRecord genericRecord) throws Exception {
        if (genericRecord != null && genericRecord instanceof GenericRecord) {
            GenericRecord retornableGenericRecord = new GenericData.Record(genericRecord.getSchema());
            for (Schema.Field field : genericRecord.getSchema().getFields()) {
                try {
                    Object extractedValue = genericRecord.get(field.name());
                    if (extractedValue != null) {
                        if (extractedValue instanceof GenericArray) {
                            Schema subtypeSchema = this.inferSubSchema(field.schema());
                            Schema arraySchema = Schema.createArray(subtypeSchema);
                            GenericArray arrayRecord = retornableGenericRecord.get(field.name()) == null ? new GenericData.Array(100, arraySchema) : ((GenericArray) retornableGenericRecord.get(field.name()));
                            for (Object o : ((GenericArray) extractedValue)) {
                                arrayRecord.add(cloneGenericRecord((GenericRecord) o));
                            }
                        } else if (extractedValue instanceof GenericRecord) {
                            retornableGenericRecord.put(field.name(), cloneGenericRecord((GenericRecord) extractedValue));
                        } else if (this.isBasicType(this.inferType(field.schema()))) {
                            retornableGenericRecord.put(field.name(), getValueForData(extractedValue, field.schema()));
                        } else {
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return retornableGenericRecord;
        } else {
            throw new Exception("Provided instance is not a GenericRecord instance: " + genericRecord.getClass());
        }
    }
}
