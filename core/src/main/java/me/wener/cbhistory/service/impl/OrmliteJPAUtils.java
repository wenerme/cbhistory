package me.wener.cbhistory.service.impl;

import com.google.common.collect.Lists;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.DataPersisterManager;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseFieldConfig;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.support.ConnectionSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import javax.persistence.*;

public class OrmliteJPAUtils
{
    /**
     * 获取所有的字段配置,在这里考虑了JAP相关的注解,改善了一些 Ormlite 的行为
     *
     * @throws SQLException
     */
    public static List<DatabaseFieldConfig> getFieldConfig(ConnectionSource connectionSource, String tableName, Class<?> clazz)
            throws SQLException
    {
        List<DatabaseFieldConfig> fieldConfigs = Lists.newArrayList();
        DatabaseType databaseType = connectionSource.getDatabaseType();
        boolean isEntity = clazz.isAnnotationPresent(Entity.class);

        for (Class<?> classWalk = clazz; classWalk != null; classWalk = classWalk.getSuperclass()) {
            for (Field field : classWalk.getDeclaredFields()) {
                DatabaseFieldConfig fieldConfig = DatabaseFieldConfig.fromField(databaseType, tableName, field);

                // 如果为实体类,即便是没有 Column 也将该值作为数据库列

                if (fieldConfig == null && isEntity
                        && !(Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())))
                {

                    DatabaseFieldConfig config = new DatabaseFieldConfig();
                    String fieldName = field.getName();
                    if (databaseType.isEntityNamesMustBeUpCase()) {
                        fieldName = fieldName.toUpperCase();
                    }
                    config.setFieldName(fieldName);
                    config.setColumnName(fieldName);
                    config.setWidth(255);
                    config.setCanBeNull(true);
                    config.setUnique(false);

                    fieldConfig = config;
                }

                if (fieldConfig != null) {
                    fieldConfigs.add(fieldConfig);
                }
            }
        }

        return fieldConfigs;
    }

    public static List<FieldType> getFieldTypes(ConnectionSource connectionSource, String tableName, Class<?> clazz)
            throws SQLException
    {
        List<FieldType> fieldTypes = Lists.newArrayList();
        DatabaseType databaseType = connectionSource.getDatabaseType();
        boolean isEntity = clazz.isAnnotationPresent(Entity.class);

        for (Class<?> classWalk = clazz; classWalk != null; classWalk = classWalk.getSuperclass()) {
            for (Field field : classWalk.getDeclaredFields()) {
                DatabaseFieldConfig fieldConfig = DatabaseFieldConfig.fromField(databaseType, tableName, field);

                // 如果为实体类,即便是没有 Column 也将该值作为数据库列
                if (fieldConfig == null && isEntity) {
                    DatabaseFieldConfig config = new DatabaseFieldConfig();
                    String fieldName = field.getName();
                    if (databaseType.isEntityNamesMustBeUpCase()) {
                        fieldName = fieldName.toUpperCase();
                    }
                    config.setFieldName(fieldName);
                    config.setColumnName(fieldName);
                    config.setWidth(255);
                    config.setCanBeNull(true);
                    config.setUnique(false);

                    fieldConfig = config;
                }

                if (fieldConfig != null) {
                    fieldConfig.setDataPersister(DataPersisterManager.lookupForField(field));

                    FieldType fieldType = new FieldType(connectionSource, tableName, field, fieldConfig, clazz);
                    fieldTypes.add(fieldType);
                }
            }
        }

        return fieldTypes;
    }

    private static DatabaseFieldConfig getDatabaseFieldConfig(String tableName, DatabaseType databaseType, Field field)
            throws SQLException
    {
        DatabaseFieldConfig fieldConfig = DatabaseFieldConfig.fromField(databaseType, tableName, field);

        return fieldConfig;
    }


    /**
     * Create a field config from the javax.persistence annotations associated with the field argument. Returns null if
     * none.
     */
    public static DatabaseFieldConfig createFieldConfig(DatabaseType databaseType, Field field) throws SQLException
    {
        Column columnAnnotation = null;
        Basic basicAnnotation = null;
        javax.persistence.Id idAnnotation = null;
        GeneratedValue generatedValueAnnotation = null;
        OneToOne oneToOneAnnotation = null;
        ManyToOne manyToOneAnnotation = null;
        JoinColumn joinColumnAnnotation = null;
        Enumerated enumeratedAnnotation = null;
        Version versionAnnotation = null;

        for (Annotation annotation : field.getAnnotations()) {
            Class<?> annotationClass = annotation.annotationType();
            if (annotationClass == Column.class) {
                columnAnnotation = (Column) annotation;
            }
            if (annotationClass == Basic.class) {
                basicAnnotation = (Basic) annotation;
            }
            if (annotationClass == Id.class) {
                idAnnotation = (Id) annotation;
            }
            if (annotationClass == GeneratedValue.class) {
                generatedValueAnnotation = (GeneratedValue) annotation;
            }
            if (annotationClass == OneToOne.class) {
                oneToOneAnnotation = (OneToOne) annotation;
            }
            if (annotationClass == ManyToOne.class) {
                manyToOneAnnotation = (ManyToOne) annotation;
            }
            if (annotationClass == JoinColumn.class) {
                joinColumnAnnotation = (JoinColumn) annotation;
            }
            if (annotationClass == Enumerated.class) {
                enumeratedAnnotation = (Enumerated) annotation;
            }
            if (annotationClass == Version.class) {
                versionAnnotation = (Version) annotation;
            }
        }

        if (columnAnnotation == null && basicAnnotation == null && idAnnotation == null && oneToOneAnnotation == null
                && manyToOneAnnotation == null && enumeratedAnnotation == null && versionAnnotation == null)
        {
            return null;
        }

        DatabaseFieldConfig config = new DatabaseFieldConfig();
        String fieldName = field.getName();
        if (databaseType.isEntityNamesMustBeUpCase()) {
            fieldName = fieldName.toUpperCase();
        }
        config.setFieldName(fieldName);

        if (columnAnnotation != null) {
            String name = columnAnnotation.name();
            if (name != null && name.length() > 0)
                config.setColumnName(name);
            String columnDefinition = columnAnnotation.columnDefinition();
            if (columnDefinition != null && columnDefinition.length() > 0)
                config.setColumnDefinition(columnDefinition);
            config.setWidth(columnAnnotation.length());
            config.setCanBeNull(columnAnnotation.nullable());
            config.setUnique(columnAnnotation.unique());
        }
        if (basicAnnotation != null) {
            Boolean optional = basicAnnotation.optional();
            if (optional == null) {
                config.setCanBeNull(true);
            } else {
                config.setCanBeNull(optional);
            }
        }
        if (idAnnotation != null) {
            if (generatedValueAnnotation == null) {
                config.setId(true);
            } else {
                // generatedValue only works if it is also an id according to {@link GeneratedValue)
                config.setGeneratedId(true);
            }
        }
        if (oneToOneAnnotation != null || manyToOneAnnotation != null) {
            // if we have a collection then make it a foreign collection
            if (Collection.class.isAssignableFrom(field.getType())
                    || ForeignCollection.class.isAssignableFrom(field.getType()))
            {
                config.setForeignCollection(true);
                if (joinColumnAnnotation != null) {
                    String name = joinColumnAnnotation.name();
                    if (name != null && name.length() > 0) {
                        config.setForeignCollectionColumnName(name);
                    }
                    // 没有在 JoinColum 上找到该值
//                        method = joinColumnAnnotation.getClass().getMethod("fetch");
//                        Object fetchType = method.invoke(joinColumnAnnotation);
//                        if (fetchType != null && fetchType.toString().equals("EAGER"))
//                        {
//                            config.setForeignCollectionEager(true);
//                        }
                }
            } else {
                // otherwise it is a foreign field
                config.setForeign(true);
                if (joinColumnAnnotation != null) {
                    String name = joinColumnAnnotation.name();
                    if (name != null && name.length() > 0) {
                        config.setColumnName(name);
                    }

                    config.setCanBeNull(joinColumnAnnotation.nullable());
                    config.setUnique(joinColumnAnnotation.unique());
                }
            }
        }
        if (enumeratedAnnotation != null) {
            if (enumeratedAnnotation.value().equals(EnumType.STRING)) {
                config.setDataType(DataType.ENUM_STRING);
            } else {
                config.setDataType(DataType.ENUM_INTEGER);
            }
        }
        if (versionAnnotation != null) {
            // just the presence of the version...
            config.setVersion(true);
        }
        if (config.getDataPersister() == null) {
            config.setDataPersister(DataPersisterManager.lookupForField(field));
        }
        config.setUseGetSet(DatabaseFieldConfig.findGetMethod(field, false) != null
                && DatabaseFieldConfig.findSetMethod(field, false) != null);
        return config;
    }

    /**
     * Return the javax.persistence.Entity annotation name for the class argument or null if none or if there was no
     * entity name.
     */
    public static String getEntityName(Class<?> clazz)
    {
        Annotation entityAnnotation = null;
        for (Annotation annotation : clazz.getAnnotations()) {
            Class<?> annotationClass = annotation.annotationType();
            if (annotationClass.getName().equals("javax.persistence.Entity")) {
                entityAnnotation = annotation;
            }
        }

        if (entityAnnotation == null) {
            return null;
        }
        try {
            Method method = entityAnnotation.getClass().getMethod("name");
            String name = (String) method.invoke(entityAnnotation);
            if (name != null && name.length() > 0) {
                return name;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Could not get entity name from class " + clazz, e);
        }
    }
}
