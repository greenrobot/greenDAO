/*
 * Copyright (C) 2012 Markus Junginger, greenrobot (http://greenrobot.de)
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

package de.greenrobot.dao;

/**
 * Can be queued (future work).
 * 
 * @author Markus
 */
public class Command {
    public static enum CommandType {
        Insert, InsertInTx, AndSoOn
    }

    public CommandType type;
    public AbstractDao<?, ?> dao;
    public Object data;

    public Command(CommandType type, AbstractDao<?, ?> dao, Object data) {
        this.type = type;
        this.dao = dao;
        this.data = data;
    }

}
