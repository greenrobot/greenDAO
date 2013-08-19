/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * This file is part of greenDAO Generator.
 * 
 * greenDAO Generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * greenDAO Generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with greenDAO Generator.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.greenrobot.daogenerator;

/**
 * Model class for an entity's property: a Java property mapped to a data base
 * column.
 */
public class Property {

  public static class PropertyBuilder {
    private final Property property;

    public PropertyBuilder(Schema schema, Entity entity, PropertyType propertyType, String propertyName) {
      this.property = new Property(schema, entity, propertyType, propertyName);
    }

    public PropertyBuilder columnName(String columnName) {
      this.property.columnName = columnName;
      return this;
    }

    public PropertyBuilder columnType(String columnType) {
      this.property.columnType = columnType;
      return this;
    }

    public PropertyBuilder primaryKey() {
      this.property.primaryKey = true;
      return this;
    }

    public PropertyBuilder primaryKeyAsc() {
      this.property.primaryKey = true;
      this.property.pkAsc = true;
      return this;
    }

    public PropertyBuilder primaryKeyDesc() {
      this.property.primaryKey = true;
      this.property.pkDesc = true;
      return this;
    }

    public PropertyBuilder autoincrement() {
      if (!this.property.primaryKey || (this.property.propertyType != PropertyType.Long)) {
        throw new RuntimeException("AUTOINCREMENT is only available to primary key properties of type long/Long");
      }
      this.property.pkAutoincrement = true;
      return this;
    }

    public PropertyBuilder unique() {
      this.property.unique = true;
      return this;
    }

    public PropertyBuilder notNull() {
      this.property.notNull = true;
      return this;
    }

    public PropertyBuilder complexJavaType() {
      this.property.complexJavaType = true;
      return this;
    }

    public PropertyBuilder index() {
      Index index = new Index();
      index.addProperty(this.property);
      this.property.entity.addIndex(index);
      return this;
    }

    public PropertyBuilder indexAsc(String indexNameOrNull, boolean isUnique) {
      Index index = new Index();
      index.addPropertyAsc(this.property);
      if (isUnique) {
        index.makeUnique();
      }
      index.setName(indexNameOrNull);
      this.property.entity.addIndex(index);
      return this;
    }

    public PropertyBuilder indexDesc(String indexNameOrNull, boolean isUnique) {
      Index index = new Index();
      index.addPropertyDesc(this.property);
      if (isUnique) {
        index.makeUnique();
      }
      index.setName(indexNameOrNull);
      this.property.entity.addIndex(index);
      return this;
    }

    public Property getProperty() {
      return this.property;
    }
  }

  private final Schema schema;
  private final Entity entity;
  private PropertyType propertyType;
  private final String propertyName;

  private String columnName;
  private String columnType;

  private boolean primaryKey;
  private boolean pkAsc;
  private boolean pkDesc;
  private boolean pkAutoincrement;

  private boolean unique;
  private boolean notNull;
  private boolean complexJavaType;

  /** Initialized in 2nd pass */
  private String constraints;

  private int ordinal;

  private String javaType;

  public Property(Schema schema, Entity entity, PropertyType propertyType, String propertyName) {
    this.schema = schema;
    this.entity = entity;
    this.propertyName = propertyName;
    this.propertyType = propertyType;
  }

  public String getPropertyName() {
    return this.propertyName;
  }

  public PropertyType getPropertyType() {
    return this.propertyType;
  }

  public void setPropertyType(PropertyType propertyType) {
    this.propertyType = propertyType;
  }

  public String getColumnName() {
    return this.columnName;
  }

  public String getColumnType() {
    return this.columnType;
  }

  public boolean isPrimaryKey() {
    return this.primaryKey;
  }

  public boolean isAutoincrement() {
    return this.pkAutoincrement;
  }

  public String getConstraints() {
    return this.constraints;
  }

  public boolean isUnique() {
    return this.unique;
  }

  public boolean isNotNull() {
    return this.notNull;
  }

  public boolean isComplexJavaType() {
    return this.complexJavaType;
  }

  public String getJavaType() {
    return this.javaType;
  }

  public int getOrdinal() {
    return this.ordinal;
  }

  public void setOrdinal(int ordinal) {
    this.ordinal = ordinal;
  }

  public Entity getEntity() {
    return this.entity;
  }

  void init2ndPass() {
    this.initConstraint();
    if (this.columnType == null) {
      this.columnType = this.schema.mapToDbType(this.propertyType);
    }
    if (this.columnName == null) {
      this.columnName = DaoUtil.dbName(this.propertyName);
    }
    if (this.notNull && !this.complexJavaType) {
      this.javaType = this.schema.mapToJavaTypeNotNull(this.propertyType);
    } else {
      this.javaType = this.schema.mapToJavaTypeNullable(this.propertyType);
      this.complexJavaType = true; // we deal with a complex data type
    }
    switch (this.propertyType) {
    case ByteArray: // can be null everytime
    case Date: // can be null everytime
    case String: // can be null everytime
      this.complexJavaType = true;
    default:
      break;
    }
  }

  private void initConstraint() {
    StringBuilder constraintBuilder = new StringBuilder();
    if (this.primaryKey) {
      constraintBuilder.append("PRIMARY KEY");
      if (this.pkAsc) {
        constraintBuilder.append(" ASC");
      }
      if (this.pkDesc) {
        constraintBuilder.append(" DESC");
      }
      if (this.pkAutoincrement) {
        constraintBuilder.append(" AUTOINCREMENT");
      }
    }
    // Always have String PKs NOT NULL because SQLite is pretty strange in this
    // respect:
    // One could insert multiple rows with NULL PKs
    if (this.notNull || (this.primaryKey && (this.propertyType == PropertyType.String))) {
      constraintBuilder.append(" NOT NULL");
    }
    if (this.unique) {
      constraintBuilder.append(" UNIQUE");
    }
    String newContraints = constraintBuilder.toString().trim();
    if (constraintBuilder.length() > 0) {
      this.constraints = newContraints;
    }
  }

  void init3ndPass() {
    // Nothing to do so far
  }

  @Override
  public String toString() {
    return "Property " + this.propertyName + " of " + this.entity.getClassName();
  }

}
