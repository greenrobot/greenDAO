/* Copyright (c) 2012 -- CommonsWare, LLC

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package de.greenrobot.dao.loader.acl;

import android.os.AsyncTask;
import android.support.v4.content.Loader;

public abstract class ContentChangingTask<T1, T2, T3> extends
		AsyncTask<T1, T2, T3> {
	private Loader<?> loader = null;

	ContentChangingTask(Loader<?> loader) {
		this.loader = loader;
	}

	@Override
	protected void onPostExecute(T3 param) {
		loader.onContentChanged();
	}
}

