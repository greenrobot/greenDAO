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

package de.greenrobot.dao.async;

import de.greenrobot.dao.DaoException;

/**
 * Used here: {@link AsyncOperation#getResult()}.
 * 
 * @author Markus
 */
public class AsyncDaoException extends DaoException {

    private static final long serialVersionUID = 5872157552005102382L;

    private final AsyncOperation failedOperation;

    public AsyncDaoException(AsyncOperation failedOperation, Throwable cause) {
        super(cause);
        this.failedOperation = failedOperation;
    }

    public AsyncOperation getFailedOperation() {
        return failedOperation;
    }

}
