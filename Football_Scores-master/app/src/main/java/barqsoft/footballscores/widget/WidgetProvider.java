package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;

public class WidgetProvider extends AppWidgetProvider {

	// String to be sent on Broadcast as soon as Data is Fetched
	// should be included on WidgetProvider manifest intent action
	// to be recognized by this WidgetProvider to receive broadcast
	public static final String DATA_FETCHED = "barqsoft.footballscores.DATA_FETCHED";

	/*
	 * this method is called every 1 hour as specified on widgetinfo.xml. This
	 * method is also called on every phone reboot, from this method nothing is
	 * updated, but instead RetmoteFetchService class is called, so this
	 * service will fetch data and send broadcast to WidgetProvider. In turn, this
	 * broadcast will be received by WidgetProvider onReceive which in turn
	 * updates the widget.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, views);
            } else {
                setRemoteAdapterV11(context, views);
            }
            views.setEmptyView(R.id.scores_list_widget, R.id.empty_view);

			Intent serviceIntent = new Intent(context, RemoteFetchService.class);
			//Intent serviceIntent = new Intent(context, myFetchService.class);
			serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					appWidgetIds[i]);
			context.startService(serviceIntent);


			// Create an Intent to launch MainActivity
			Intent launchIntent = new Intent(context, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
			views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            //Intent fillInIntent = new Intent();
            //fillInIntent.putExtra(Widget.EXTRA_LIST_VIEW_ROW_NUMBER, position);
            //views.setOnClickFillInIntent(R.id.scores_item, fillInIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetIds[i], views);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);


//        for (int appWidgetId : appWidgetIds) {
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
//
//            // Create an Intent to launch MainActivity
//            Intent intent = new Intent(context, MainActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//            views.setOnClickPendingIntent(R.id.scores_list_widget, pendingIntent);
//
//            // Set up the collection
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                setRemoteAdapter(context, views);
//            } else {
//                setRemoteAdapterV11(context, views);
//            }
//            boolean useDetailActivity = context.getResources()
//                    .getBoolean(R.bool.use_detail_activity);
//            Intent clickIntentTemplate = useDetailActivity
//                    ? new Intent(context, DetailActivity.class)
//                    : new Intent(context, MainActivity.class);
//            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
//                    .addNextIntentWithParentStack(clickIntentTemplate)
//                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//            views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);
//            views.setEmptyView(R.id.widget_list, R.id.widget_empty);
//
//            // Tell the AppWidgetManager to perform an update on the current app widget
//            appWidgetManager.updateAppWidget(appWidgetId, views);
//        }
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private RemoteViews updateWidgetListView(Context context, int appWidgetId) {

		// which layout to show on widget
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget_layout);

		// RemoteViews Service needed to provide adapter for ListView
		Intent svcIntent = new Intent(context, WidgetService.class);
		// passing app widget id to that RemoteViews Service
		svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		// setting a unique Uri to the intent
		// don't know its purpose to me right now
		svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
		// setting adapter to listview of the widget
		remoteViews.setRemoteAdapter(appWidgetId, R.id.scores_list_widget, svcIntent);
		// setting an empty view in case of no data
		remoteViews.setEmptyView(R.id.scores_list_widget, R.id.empty_view);
		return remoteViews;
	}

	/*
	 * It receives the broadcast as per the action set on intent filters on
	 * Manifest.xml once data is fetched from RemotePostService,it sends
	 * broadcast and WidgetProvider notifies to change the data the data change
	 * right now happens on ListProvider as it takes RemoteFetchService
	 * listItemList as data
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if (intent.getAction().equals(DATA_FETCHED)) {
			int appWidgetId = intent.getIntExtra(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
			AppWidgetManager appWidgetManager = AppWidgetManager
					.getInstance(context);
			RemoteViews remoteViews = updateWidgetListView(context, appWidgetId);
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		}

	}

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.scores_list_widget,
                new Intent(context, WidgetService.class));
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.scores_list_widget,
                new Intent(context, WidgetService.class));
    }

}
