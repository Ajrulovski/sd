package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import java.util.ArrayList;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * If you are familiar with Adapter of ListView,this is the same as adapter
 * with few changes
 * here it now takes RemoteFetchService ArrayList<ListItem> for data
 * which is a static ArrayList
 * and this example won't work if there are multiple widgets and 
 * they update at same time i.e they modify RemoteFetchService ArrayList at same
 * time.
 * For that use Database or other techniquest
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ListProvider implements RemoteViewsFactory {
	private ArrayList<ListItem> listItemList = new ArrayList<ListItem>();
	private Context context = null;
	private int appWidgetId;

	public ListProvider(Context context, Intent intent) {
		this.context = context;
		appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);

		populateListItem();
	}

	private void populateListItem() {
		if(RemoteFetchService.listItemList !=null )
		listItemList = (ArrayList<ListItem>) RemoteFetchService.listItemList
				.clone();
		else
			listItemList = new ArrayList<ListItem>();

	}

	@Override
	public int getCount() {
		return listItemList.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 *Similar to getView of Adapter where instead of View
	 *we return RemoteViews 
	 * 
	 */
	@Override
	public RemoteViews getViewAt(int position) {
		final RemoteViews remoteView = new RemoteViews(
				context.getPackageName(), R.layout.scores_list_item);
		ListItem listItem = listItemList.get(position);

		remoteView.setTextViewText(R.id.data_textview, listItem.data_textview);
		remoteView.setTextViewText(R.id.away_name, listItem.away_name);
        remoteView.setImageViewResource(R.id.away_crest, Utilies.getTeamCrestByTeamName(listItem.away_crest));
        remoteView.setImageViewResource(R.id.home_crest, Utilies.getTeamCrestByTeamName(listItem.home_crest));
		remoteView.setTextViewText(R.id.home_name, listItem.home_name);
		remoteView.setTextViewText(R.id.score_textview, listItem.score_textview);
		Log.i("AAAA", String.valueOf(remoteView.getLayoutId()));
		return remoteView;
	}
	

	@Override
	public RemoteViews getLoadingView() {
		return null;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onDataSetChanged() {
	}

	@Override
	public void onDestroy() {
	}

}
