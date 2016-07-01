package com.esprit.redcrescentapp.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

import com.esprit.redcrescentapp.R;
import com.esprit.redcrescentapp.activites.ReportAccident_widget_Activity;

/**
 * Implementation of App Widget functionality.
 */
public class MyWidget extends AppWidgetProvider implements View.OnClickListener {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, ReportAccident_widget_Activity.class);

            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            // This is needed to make this intent different from its previous intents
            intent.setData(Uri.parse("tel:/" + (int) System.currentTimeMillis()));

            // Creating a pending intent, which will be invoked when the user
            // clicks on the widget
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);

            // Get the layout for the App Widget
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_widget);

            //  Attach an on-click listener to the clock
            views.setOnClickPendingIntent(R.id.WidgetLayout, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onClick(View v) {

    }
}

