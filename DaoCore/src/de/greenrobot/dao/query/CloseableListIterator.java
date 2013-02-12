/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.greenrobot.dao.query;

import java.io.Closeable;
import java.util.ListIterator;

/**
 * A list iterator that needs to be closed (or the associated list) to free underlying resources like a database cursor.
 * Typically used with LazyList.
 * 
 * @author Markus
 * 
 * @param <T>
 */
public interface CloseableListIterator<T> extends ListIterator<T>, Closeable {

}