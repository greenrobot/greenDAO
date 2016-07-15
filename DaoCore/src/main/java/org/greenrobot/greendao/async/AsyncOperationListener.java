/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
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
package org.greenrobot.greendao.async;

/** Listener being called after completion of {@link org.greenrobot.greendao.async.AsyncOperation}. */
public interface AsyncOperationListener {
    /**
     * Note, that the operation may not have been successful, check
     * {@link AsyncOperation#isFailed()} and/or {@link AsyncOperation#getThrowable()} for error situations.
     */
    void onAsyncOperationCompleted(AsyncOperation operation);
}
