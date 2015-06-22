package de.greenrobot.dao.loader;

import android.annotation.TargetApi;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Build;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.LazyList;
import de.greenrobot.dao.query.Query;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class LazyListLoader<T> extends AsyncTaskLoader<LazyList<T>> {
	
	private LazyList<T> list;
	private boolean cache;
	private AbstractDao<T, Long> dao;
	private Query<T> query;
	
	public LazyListLoader(Context context, boolean cache, Query<T> query, AbstractDao<T, Long> dao) {
		super(context);
		this.cache = cache;
		this.dao = dao;
		this.query = query;
	}

	@Override
	public LazyList<T> loadInBackground() {
		query = query.forCurrentThread();
		if (cache) {
			list = query.listLazy();
		} else {
			list = query.listLazyUncached();
		}
		return list;
	}
	
	@Override
	public void deliverResult(LazyList<T> data) {
		if (isReset()) {
			if (list != null) {
				list.close();
			}
			return;
		}
		
		LazyList<T> oldList = list;
		list = data;
		
		if (isStarted()) {
			super.deliverResult(data);
		}
		if (oldList != null && oldList != list && !oldList.isClosed()) {
			oldList.close();
		}
	}
	
	@Override
	protected void onStartLoading() {
		if (list != null) {
			deliverResult(list);
		}
		
		if (takeContentChanged() || list == null) {
			forceLoad();
		}
	}
	
	@Override
	protected void onStopLoading() {
		cancelLoad();
	}
	
	@Override
	public void onCanceled(LazyList<T> data) {
		if (data != null && !data.isClosed()) {
			data.close();
		}
	}
	
	@Override
	protected void onReset() {
		super.onReset();
		onStopLoading();
		if (list != null && !list.isClosed()) {
			list.close();
			list = null;
		}
	}

	public void insert(T... entities) {
		new InsertTask(null).execute(entities);
	}
	
	public void insert(InsertTaskCallback<T> callback, T... entities) {
		new InsertTask(callback).execute(entities);
	}
	
	public void update(T... entitis) {
		new UpdateTask(null).execute(entitis);
	}
	
	public void update(UpdateTaskCallback<T> callback, T... entities) {
		new UpdateTask(callback).execute(entities);
	}
	
	public void insertOrUpdate(T... entities) {
		new InsertOrUpdateTask(null).execute(entities);
	}
	
	public void insertOrUpdate(InsertOrUpdateTaskCallback<T> callback, T... entities) {
		new InsertOrUpdateTask(callback).execute(entities);
	}
	
	public void delete(T... entities) {
		new DeleteTask(null).execute(entities);
	}
	
	public void delete(DeleteTaskCallback<T> callback, T... entities) {
		new DeleteTask(callback).execute(entities);
	}
	
	public static interface InsertTaskCallback<T> {
		public void entitiesInserted(T[] entities);
	}
	
	public static interface UpdateTaskCallback<T> {
		public void entitiesUpdated(T[] entities);
	}
	
	public static interface InsertOrUpdateTaskCallback<T> {
		public void entitiesInsertedOrUpdated(T[] entities);
	}
	
	public static interface DeleteTaskCallback<T> {
		public void entitiesDeleted(T[] entities);
	}
	
	private class InsertTask extends ContentChangingTask<T, Void, T[]> {
		InsertTaskCallback<T> callback;
		
		InsertTask(InsertTaskCallback<T> callback) {
			super(LazyListLoader.this);
			this.callback = callback;
		}

		@Override
		protected T[] doInBackground(T... params) {
			if (params.length == 1) {
				dao.insert(params[0]);
			} else {
				dao.insertInTx(params);
			}
			return params;
		}
		
		@Override
		protected void onPostExecute(T[] param) {
			super.onPostExecute(param);
			if (callback != null) {
				callback.entitiesInserted(param);
			}
		}
	}
	
	private class UpdateTask extends ContentChangingTask<T, Void, T[]> {
		private UpdateTaskCallback<T> callback;
		
		UpdateTask(UpdateTaskCallback<T> callback) {
			super(LazyListLoader.this);
			this.callback = callback;
		}

		@Override
		protected T[] doInBackground(T... params) {
			if (params.length == 1) {
				dao.update(params[0]);
			} else {
				dao.updateInTx(params);
			}
			return params;
		}
		
		@Override
		protected void onPostExecute(T[] param) {
			super.onPostExecute(param);
			if (callback != null) {
				callback.entitiesUpdated(param);
			}
		}
	}
	
	private class InsertOrUpdateTask extends ContentChangingTask<T, Void, T[]> {
		InsertOrUpdateTaskCallback<T> callback;
		
		InsertOrUpdateTask(InsertOrUpdateTaskCallback<T> callback) {
			super(LazyListLoader.this);
			this.callback = callback;
		}

		@Override
		protected T[] doInBackground(T... params) {
			if (params.length == 1) {
				dao.insertOrReplace(params[0]);
			} else {
				dao.insertOrReplaceInTx(params);
			}
			return params;
		}
		
		@Override
		protected void onPostExecute(T[] param) {
			super.onPostExecute(param);
			if (callback != null) {
				callback.entitiesInsertedOrUpdated(param);
			}
		}
	}
	
	private class DeleteTask extends ContentChangingTask<T, Void, T[]> {
		private DeleteTaskCallback<T> callback;
		
		DeleteTask(DeleteTaskCallback<T> callback) {
			super(LazyListLoader.this);
			this.callback = callback;
		}

		@Override
		protected T[] doInBackground(T... params) {
			if (params.length == 1) {
				dao.delete(params[0]);
			} else {
				dao.deleteInTx(params);
			}
			return params;
		}
		
		@Override
		protected void onPostExecute(T[] param) {
			super.onPostExecute(param);
			if (callback != null) {
				callback.entitiesDeleted(param);
			}
		}
	}
	
}

