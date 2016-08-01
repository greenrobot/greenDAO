/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
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

package org.greenrobot.greendao.generator;

import java.util.List;

@SuppressWarnings("unused")
public class ContentProvider {
    private final List<Entity> entities;
    private String authority;
    private String basePath;
    private String className;
    private String javaPackage;
    private boolean readOnly;
    private Schema schema;

    public ContentProvider(Schema schema, List<Entity> entities) {
        this.schema = schema;
        this.entities = entities;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getJavaPackage() {
        return javaPackage;
    }

    public void setJavaPackage(String javaPackage) {
        this.javaPackage = javaPackage;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void readOnly() {
        this.readOnly = true;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void init2ndPass() {
        if (authority == null) {
            authority = schema.getDefaultJavaPackage() + ".provider";
        }
        if (basePath == null) {
            basePath = "";
        }
        if (className == null) {
            className = entities.get(0).getClassName() + "ContentProvider";
        }
        if (javaPackage == null) {
            javaPackage = schema.getDefaultJavaPackage();
        }

    }

}
