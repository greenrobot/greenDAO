/*
 * Copyright (C) 2011-2015 Markus Junginger, greenrobot (http://greenrobot.de)
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

package de.greenrobot.dao.converter;

/**
 * To use custom types in your entity, implement this to convert db values to entity values and back.
 * <p/>
 * Notes for implementations:
 * <ul>
 * <li>Converters are created by the default constructor</li>
 * <li>Converters must be implemented thread-safe</li>
 * </ul>
 */
public interface PropertyConverter<P, D> {
    P convertToEntityProperty(D databaseValue);

    D convertToDatabaseValue(P entityProperty);
}
