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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Model class for an Annotation that can be attached to classses, properties and their setter/getter/constructor methods
 * @author yigit@path.com
 */
public class Annotation {
    private String name;
    protected String _package;
    private Map<String, String> parameters = new HashMap<String, String>();
    public static Pattern QUOTE = Pattern.compile("\"");

    //we use this if Annotation has only 1 parameter
    public static final String NO_NAME = "__no_name";
    public static final String NULL = "null";

    public Annotation(String name, String... params) {
		this(name);
        this.parameters = new HashMap<String, String>();
        if(params.length > 1 && params.length % 2 != 0) {
            throw new RuntimeException("annotation parameters should be key value pairs");
        }
        if(params.length == 1) {
            this.parameters.put(NO_NAME, params[0] == null ? NULL : params[0]);
        } else {
            for(int i = 0; i < params.length; i += 2) {
                this.parameters.put(params[i], params[i + 1] == null ? NULL : params[i + 1]);
            }
        }
    }

    public String getPackage() {
        return _package;
    }

    public void setPackage(String _package) {
        this._package = _package;
    }

    public Annotation(String name) {
		//if name includes a ".", split it and write to package
		int dotIndex = name.lastIndexOf(".");
		if(dotIndex != -1) {
			_package = name.substring(0, dotIndex);
			name = name.substring(dotIndex + 1);
		}
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
