package barqsoft.footballscores.widget;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.Utilies;
import barqsoft.footballscores.scoresAdapter;


public class RemoteFetchService extends Service {
    public static final String LOG_TAG = "RemoteFetchServiceG";
	private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    public scoresAdapter mAdapter;
	public static ArrayList<ListItem> listItemList;

    private static final String[] NOTIFY_SCORES = new String[] {
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.LEAGUE_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL,
            DatabaseContract.scores_table.DATE_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.MATCH_DAY,
            DatabaseContract.scores_table.TIME_COL
    };

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	/*
	 * Retrieve appwidget id from intent it is needed to update widget later
	 * initialize our AQuery class
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent.hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID))
			appWidgetId = intent.getIntExtra(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());

        //getData("2015-12-04");
        getData(currentDateandTime);
		return super.onStartCommand(intent, flags, startId);
	}

    private void getData (String timeFrame)
    {
        Uri scoresUri = DatabaseContract.scores_table.buildScoreWithDate();
        String[] daterange = new String[1];//["2015-12-02","2015-12-10"];
        daterange[0] = timeFrame;
        //daterange[1] = "2015-12-10";
        Cursor cursor = getApplicationContext().getContentResolver().query(scoresUri, NOTIFY_SCORES, DatabaseContract.scores_table.DATE_COL + " LIKE ?", daterange, null);
        listItemList = new ArrayList<ListItem>();

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // iterate and build list data
            String awayname = cursor.getString(0);
            String homename = cursor.getString(1);
            String awaygoals = cursor.getString(3);
            String homegoals = cursor.getString(5);
            int leaguenum = Integer.valueOf(cursor.getString(2));
            String league = Utilies.getLeague(leaguenum);
            String matchday = Utilies.getMatchDay(Integer.valueOf(cursor.getString(6)), leaguenum);
            String datatextcontent = league+" "+matchday+" "+cursor.getString(7);

            ListItem listItem = new ListItem();
            listItem.away_name = awayname;
            listItem.data_textview = datatextcontent;
            listItem.home_name = homename;
            listItem.score_textview = Utilies.getScores(Integer.valueOf(homegoals),Integer.valueOf(awaygoals));
            listItemList.add(listItem);
            populateWidget();
        }
        //manualList();
        cursor.close();
    }

//    private void manualList()
//    {
//        listItemList = new ArrayList<ListItem>();
//        ListItem listItem = new ListItem();
//        listItem.away_name = "A";
//        listItem.away_crest = "B";
//        listItem.data_textview = "C";
//        listItem.home_crest = "D";
//        listItem.home_name = "E";
//        listItem.score_textview = "4 : 4";
//        listItemList.add(listItem);
//        populateWidget();
//    }

	/**
	 * Method which sends broadcast to WidgetProvider
	 * so that widget is notified to do necessary action
	 * and here action == WidgetProvider.DATA_FETCHED
	 */
	private void populateWidget() {

		Intent widgetUpdateIntent = new Intent();
		widgetUpdateIntent.setAction(WidgetProvider.DATA_FETCHED);
		widgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				appWidgetId);
		sendBroadcast(widgetUpdateIntent);

		this.stopSelf();
	}
}
