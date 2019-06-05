/**<ul>
 * <li>SimpleWidgetTuto</li>
 * <li>com.android2ee.tuto.widget.simple.sample1</li>
 * <li>2 mars 2012</li>
 * 
 * <li>======================================================</li>
 *
 * <li>Projet : Mathias Seguy Project</li>
 * <li>Produit par MSE.</li>
 *
 /**
 * <ul>
 * Android Tutorial, An <strong>Android2EE</strong>'s project.</br> 
 * Produced by <strong>Dr. Mathias SEGUY</strong>.</br>
 * Delivered by <strong>http://android2ee.com/</strong></br>
 *  Belongs to <strong>Mathias Seguy</strong></br>
 ****************************************************************************************************************</br>
 * This code is free for any usage except training and can't be distribute.</br>
 * The distribution is reserved to the site <strong>http://android2ee.com</strong>.</br>
 * The intelectual property belongs to <strong>Mathias Seguy</strong>.</br>
 * <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * 
 * *****************************************************************************************************************</br>
 *  Ce code est libre de toute utilisation mais n'est pas distribuable.</br>
 *  Sa distribution est reservée au site <strong>http://android2ee.com</strong>.</br> 
 *  Sa propriété intellectuelle appartient à <strong>Mathias Seguy</strong>.</br>
 *  <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * *****************************************************************************************************************</br>
 */
package com.android2ee.tuto.widget.simple.sample1;

import java.io.File;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * @author Mathias Seguy (Android2EE)
 * @goals
 *        This class aims to:
 *        <ul>
 *        <li></li>
 *        </ul>
 */
public class SimpleWidget extends AppWidgetProvider {
	/**
	 * 
	 */
	private static final String SIMPLE_WIDGET_NAME = "SimpleWidget_";
	/**
	 * 
	 */
	private static final String ON_UPDATE_CALLED_COUNTER = "onUpdateCalledCounter";
	private static final String INTENT_CLICK1 = "com.android.tuto.widget.simple.sample1.click1";
	private static final String INTENT_UPDATE = "com.android.tuto.widget.simple.sample1.update";
	/******************************************************************************************/
	/** Managing the Handler and the Thread *************************************************/
	/******************************************************************************************/
	/**
	 * The Handler
	 */
	private MHandler handler;
	/**
	 * The thread that update the progressbar
	 */
	Thread backgroundThread;
	/**
	 * The string for the log
	 */
	private final static String TAG = "HandlerTutoActivity";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.appwidget.AppWidgetProvider#onUpdate(android.content.Context,
	 * android.appwidget.AppWidgetManager, int[])
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		// mise en place de l'alarme de l'AlarmManager
		manageAlarm(context, SET_ALARM);
		// The name of the preference file
		String myPreferences;
		// The SharedPreferences object
		SharedPreferences preferences;
		// The
		SharedPreferences.Editor editor;
		// A dummy variable
		int onUpdateCalledCounter;
		final int N = appWidgetIds.length;
		RemoteViews views;
		// Perform this loop procedure for each App Widget that belongs to this provider
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			// Gestion des preferences
			// Pour des préférences stockées dans un fichier de votre application (ou dans
			// plusieurs) :
			myPreferences = SIMPLE_WIDGET_NAME + appWidgetId;
			//
			preferences = context.getSharedPreferences(myPreferences, Activity.MODE_PRIVATE);
			//
			onUpdateCalledCounter = preferences.getInt(ON_UPDATE_CALLED_COUNTER, 0);
			//
			onUpdateCalledCounter++;
			//
			editor = preferences.edit();
			//
			editor.putInt(ON_UPDATE_CALLED_COUNTER, onUpdateCalledCounter);
			//
			editor.commit();
			// Get the layout for the App Widget and attach an on-click listener
			// to the button

			Log.e("tag", "appWidgetId" + appWidgetId);
			views = new RemoteViews(context.getPackageName(), R.layout.widget_layout_two);
			// To change the value of element within your view, you need to call such methods
			// where the view where you specify what you do on which element
			// You can not directly retrieve an element
			// Affecte une image à un composant (equivalent à ImageView.setImageResource)
			views.setImageViewResource(R.id.imageView, R.drawable.statefull_image_view);
			// Affecte un texte à un TextView (équivalent de TextView.setText)
			views.setTextViewText(R.id.textView, context.getString(R.string.app_name) + " : " + i + " (id="
					+ appWidgetId + ") <=>"+onUpdateCalledCounter);
			// //Appele la méthode passée en paramètre (setClickable) sur le composant en lui
			// passant le paramètre true
			// views.setBoolean(R.id.checkBox, "setClickable", true);
			// //Appele la méthode passée en paramètre (setClickable) sur le composant en lui
			// passant le paramètre true
			// views.setString(R.id.imageButton, "setText", "La string a afficher");
			// //Défini la couleur du Texte
			// views.setTextColor(R.id.textView, Color.GREEN);
			// //Affiche ou cache un composant
			// views.setViewVisibility(R.id.imageButton, true);
			// Ajout d'un listener de click
			Intent intentClick1 = new Intent(INTENT_CLICK1);
			intentClick1.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intentClick1, 0);
			views.setOnClickPendingIntent(R.id.imageView, pendingIntent);
			// Tell the AppWidgetManager to perform an update on the current app widget
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
		/******************************************************************************************/
		/** Unn test de mise à jour par thread du widget (qui marche) **************************************************************************/
		/******************************************************************************************/

		// handler definition
		handler = new MHandler() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Log.d(TAG, "handle message called ");
				// be sure the handler is running before doing something

				updateWidget(msg.getData().getInt("loop"), context);
			}
		};
		handler.context = context;
		// use a random double to give a name to the thread
		final double random = Math.random();
		// Define the Thread and the link with the handler
		backgroundThread = new Thread(new Runnable() {
			/**
			 * The message exchanged between this thread and the handler
			 */
			Message myMessage;
			Bundle mybundle = new Bundle();

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Runnable#run()
			 */
			public void run() {
				try {
					Log.d(TAG, "NewThread " + random);
					for (int j = 0; j < 0; j++) {
						Log.d(TAG, "Thread isThreadRunnning true " + random);

						Log.d(TAG, "Thread isThreadPausing false " + random);
						// For example sleep 1 second
						Thread.sleep(1000);
						// Send the message to the handler (the
						// handler.obtainMessage is more
						// efficient that creating a message from scratch)
						// create a message, the best way is to use that
						// method:
						mybundle.putInt("loop", j);
						myMessage = handler.obtainMessage();
						myMessage.setData(mybundle);
						// then send the message to the handler
						handler.sendMessage(myMessage);

					}
				} catch (Throwable t) {
					// just end the background thread
				}
			}
		});
		// start the thread
		backgroundThread.start();
	}

	private class MHandler extends Handler {
		public Context context;
	}

	private static final Integer ALL_WIDGETS = -1;

	private void updateWidget(int loop, Context context) {
		updateWidget(loop, context, ALL_WIDGETS);
	}

	private void updateWidget(int loop, Context context, int widgetId) {
		Log.e("tag", "updateWidget");
		int drawableId;
		switch (loop % 8) {
		case 0:
			drawableId = R.drawable.statefull_image_view;
			break;
		case 1:
			drawableId = R.drawable.ic_android2ee_blc_inv;
			break;
		case 2:
			drawableId = R.drawable.ic_android2ee_bleu;
			break;
		case 3:
			drawableId = R.drawable.ic_android2ee_bleu_inv;
			break;
		case 4:
			drawableId = R.drawable.ic_android2ee_orange;
			break;
		case 5:
			drawableId = R.drawable.ic_android2ee_orange_inv;
			break;
		case 6:
			drawableId = R.drawable.ic_android2ee_violet;
			break;
		case 7:
			drawableId = R.drawable.ic_android2ee_violet_inv;
			break;
		default:
			drawableId = R.drawable.ic_android2ee_violet_inv;
			break;
		}
		Log.e("tag", "drawableId" + drawableId);
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		ComponentName componentName = new ComponentName(context, SimpleWidget.class);
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
		final int N = appWidgetIds.length;
		// Get the layout for the App Widget and attach an on-click listener
		// to the button
		RemoteViews views;
		Log.e("tag", "widgetId" + widgetId);
		// Perform this loop procedure for each App Widget that belongs to this provider
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			Log.e("tag", "appWidgetId" + appWidgetId);
			if ((widgetId == ALL_WIDGETS) || (widgetId == appWidgetId)) {
				views = new RemoteViews(context.getPackageName(), R.layout.widget_layout_two);
				// To change the value of element within your view, you need to call such methods
				// where the view where you specify what you do on which element
				// You can not directly retrieve an element
				views.setImageViewResource(R.id.imageView, drawableId);
				views.setTextViewText(R.id.textView, context.getString(R.string.app_name) + " : " + i + " (id="
						+ appWidgetId + ")");
				// Ajout d'un listener de click
				Intent intentClick1 = new Intent(INTENT_CLICK1);
				intentClick1.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, intentClick1, 0);
				views.setOnClickPendingIntent(R.id.imageView, pendingIntent);
				// Tell the AppWidgetManager to perform an update on the current app widget
				appWidgetManager.updateAppWidget(appWidgetId, views);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.appwidget.AppWidgetProvider#onDeleted(android.content.Context, int[])
	 */
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.e("tag", "onDeleted");
		super.onDeleted(context, appWidgetIds);
		//Le nom du fichier de préférence
		String myPreferences; 
		//L’objet SharedPreference
		File sharedPreferencefile,sharedPreferenceBackupfile;
		//Le nombre de widgets
		final int N = appWidgetIds.length;

		//Destruction des fichiers SharedPreference associés à ces instances
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			//Tout d'abord il faut vider les valeurs de l'objet			
			// Le nom du fichier de préférence
			myPreferences = SIMPLE_WIDGET_NAME+appWidgetId;
			//Vidage du fichier
			context.getSharedPreferences(myPreferences, Activity.MODE_PRIVATE).edit().clear().commit();
			//ensuite il faut détruire le fichier lui-même et son fichier backup
			sharedPreferencefile= new File("/data/data/"+context.getPackageName()+"/shared_prefs/"+myPreferences+".xml");
			sharedPreferenceBackupfile= new File("/data/data/"+context.getPackageName()+"/shared_prefs/"+myPreferences+".bak");
			//and then 
			sharedPreferencefile.delete();
			sharedPreferenceBackupfile.delete();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.appwidget.AppWidgetProvider#onDisabled(android.content.Context)
	 */
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		// Suppression de l'alarme
		//manageAlarm(context, CANCEL_ALARM);
	}

	/**
	 * The constant action to set the alarm
	 */
	private final int SET_ALARM = 1;
	/**
	 * The constant action to cancel the alarm
	 */
	private final int CANCEL_ALARM = 2;

	private void manageAlarm(Context context, int action) {
		// récupération de l'AlarmManager
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		// Définition de l'intent
		Intent intent = new Intent(INTENT_UPDATE);
		// définition du pendingIntent envoyé
		PendingIntent pendingIntentUpdate = PendingIntent.getBroadcast(context, 0, intent, 0);
		// Mise en place de l’action
		if (CANCEL_ALARM == action) {
			alarmManager.cancel(pendingIntentUpdate);
		} else if (SET_ALARM == action) {
			// Mise en place de l'alarme: Mode où le temps de veille ne compte pas
			// dont la première alarme est déclenchée dans une demi jour (12h)
			// se répetera toutes les douzes heures
			// et enverra le pendingIntent
			alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, AlarmManager.INTERVAL_HALF_DAY,
					AlarmManager.INTERVAL_HALF_DAY, pendingIntentUpdate);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.appwidget.AppWidgetProvider#onEnabled(android.content.Context)
	 */
	@Override
	public void onEnabled(Context context) {
		Log.e("tag", "onEnabled");
		super.onEnabled(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.appwidget.AppWidgetProvider#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("tag", "onReceive");
		super.onReceive(context, intent);
		// icui on traite les intents:
		if (INTENT_CLICK1.equals(intent.getAction())) {

			int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, ALL_WIDGETS);
			// appWidgetId=intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
			Log.e("tag", "appWidgetId" + appWidgetId);
			updateWidget(6, context, appWidgetId);
			// To use to update the right widget
			//
		} else if (INTENT_UPDATE.equals(intent.getAction())) {
			//forceUpdate(context);
		}
	}

}
