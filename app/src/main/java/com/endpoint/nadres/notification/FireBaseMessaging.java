package com.endpoint.nadres.notification;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.endpoint.nadres.R;
import com.endpoint.nadres.activities_fragments.activity_chat.ChatActivity;
import com.endpoint.nadres.activities_fragments.activity_notification.NotificationsActivity;
import com.endpoint.nadres.models.ChatUserModel;
import com.endpoint.nadres.models.UserModel;
import com.endpoint.nadres.preferences.Preferences;
import com.endpoint.nadres.remote.Api;
import com.endpoint.nadres.tags.Tags;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FireBaseMessaging extends FirebaseMessagingService {

    Preferences preferences = Preferences.getInstance();


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        updateToken(s);
    }

    private void updateToken(String token) {
        UserModel userModel = getUserData();
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        task.getResult().getId();
                        Api.getService(Tags.base_url)
                                .updateFireBaseToken(userModel.getData().getToken(),userModel.getData().getId(), 1)
                                .enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                        if (response.isSuccessful()) {
                                            try {
                                                userModel.getData().setFireBaseToken(token);
                                                preferences.create_update_userdata(FireBaseMessaging.this,userModel);

                                                Log.e("Success", "token updated");
                                            } catch (Exception e) {
                                                //  e.printStackTrace();
                                            }
                                        } else {
                                            try {
                                                Log.e("error", response.code() + "_" + response.errorBody().string());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }


                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        try {
                                            Log.e("Error", t.getMessage());
                                        } catch (Exception e) {
                                        }
                                    }
                                });
                    }
                });

    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> map = remoteMessage.getData();

        for (String key : map.keySet()) {
            Log.e("key", key + "    value " + map.get(key));
        }

        if (getSession().equals(Tags.session_login)) {
            int to_id = Integer.parseInt(map.get("to_user_id"));

            if (getCurrentUser_id() == to_id) {
                manageNotification(map);
            }
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void manageNotification(Map<String, String> map) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNewNotificationVersion(map);
        } else {
            createOldNotificationVersion(map);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void createNewNotificationVersion(Map<String, String> map) {

        String sound_Path = "android.resource://" + getPackageName() + "/" + R.raw.not;

        String not_type = map.get("notification_type");
        if (not_type != null && not_type.equals("chat")) {
            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            String current_class = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();

            String room_id = "";
            String name = "";
            String image = "";
            String chat_type = "";
            String message_type = "";
            String message = "";
            ChatUserModel chatUserModel = new ChatUserModel(Integer.parseInt(room_id), name, image, chat_type);
            chatUserModel.setMessage_type(message_type);
            chatUserModel.setMessage(message);

            if (current_class.equals("com.endpoint.nadres.activities_fragments.activity_chat.ChatActivity")) {

                String current_room_id = getRoom_id();
                if (current_room_id.equals(room_id)) {
                    EventBus.getDefault().post(chatUserModel);
                } else {
                    LoadChatImage(chatUserModel, sound_Path, 1);
                }


            } else {
                EventBus.getDefault().post(chatUserModel);

                LoadChatImage(chatUserModel, sound_Path, 1);

            }

        } else {
            ChatUserModel chatUserModel = new ChatUserModel();
            sendNotification_VersionNew(chatUserModel, sound_Path);
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void createOldNotificationVersion(Map<String, String> map) {


        String sound_Path = "android.resource://" + getPackageName() + "/" + R.raw.not;

        String not_type = map.get("notification_type");

        if (not_type != null && not_type.equals("chat")) {


            String room_id = "";
            String name = "";
            String image = "";
            String chat_type = "";
            String message_type = "";
            String message = "";

            ChatUserModel chatUserModel = new ChatUserModel(Integer.parseInt(room_id), name, image, chat_type);
            chatUserModel.setMessage_type(message_type);
            chatUserModel.setMessage(message);

            ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
            String current_class = activityManager.getRunningTasks(1).get(0).topActivity.getClassName();

            if (current_class.equals("com.endpoint.nadres.activities_fragments.activity_chat.ChatActivity")) {

                String current_room_id = getRoom_id();

                if (current_room_id.equals(room_id)) {
                    EventBus.getDefault().post(chatUserModel);
                } else {
                    LoadChatImage(chatUserModel, sound_Path, 2);
                }


            } else {
                EventBus.getDefault().post(chatUserModel);

                LoadChatImage(chatUserModel, sound_Path, 2);

            }

        } else {
            ChatUserModel chatUserModel = new ChatUserModel();
            sendNotification_VersionOld(chatUserModel, sound_Path);
        }

    }


    private void LoadChatImage(ChatUserModel chatUserModel, String sound_path, int type) {


        Target target = new Target() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                if (type == 1) {
                    sendChatNotification_VersionNew(chatUserModel, sound_path, bitmap);
                } else {
                    sendChatNotification_VersionOld(chatUserModel, sound_path, bitmap);

                }
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                Bitmap bitmap ;

                if (chatUserModel.getChat_type().equals("single")){
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_avatar);
                }else {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_group);

                }

                if (type == 1) {
                    sendChatNotification_VersionNew(chatUserModel, sound_path, bitmap);

                } else {
                    sendChatNotification_VersionOld(chatUserModel, sound_path, bitmap);

                }
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        new Handler(Looper.getMainLooper()).postDelayed(() -> Picasso.get().load(Uri.parse(Tags.IMAGE_URL + chatUserModel.getImage())).into(target), 100);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendChatNotification_VersionNew(ChatUserModel chatUserModel, String sound_path, Bitmap bitmap) {


        String CHANNEL_ID = "my_channel_02";
        CharSequence CHANNEL_NAME = "my_channel_name";
        int IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        final NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE);
        channel.setShowBadge(true);
        channel.setSound(Uri.parse(sound_path), new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                .build()
        );
        builder.setChannelId(CHANNEL_ID);
        builder.setSound(Uri.parse(sound_path), AudioManager.STREAM_NOTIFICATION);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentTitle(chatUserModel.getName());
        builder.setLargeIcon(bitmap);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("data", chatUserModel);
        intent.putExtra("from_fire", true);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.createNotificationChannel(channel);
            manager.notify(Tags.not_tag,Tags.not_id, builder.build());

        }


    }

    private void sendChatNotification_VersionOld(ChatUserModel chatUserModel, String sound_path, Bitmap bitmap) {

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSound(Uri.parse(sound_path), AudioManager.STREAM_NOTIFICATION);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentTitle(chatUserModel.getName());

        Intent intent = new Intent(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("data", chatUserModel);
        intent.putExtra("from_fire", true);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        if (chatUserModel.getMessage_type().equals("img")) {
            builder.setContentText(getString(R.string.image));

        } else if (chatUserModel.getMessage_type().equals("sound")) {
            builder.setContentText(getString(R.string.voice));

        } else if (chatUserModel.getMessage_type().equals("video")) {
            builder.setContentText(getString(R.string.video));

        } else {
            builder.setContentText(chatUserModel.getMessage());

        }

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(Tags.not_tag, Tags.not_id, builder.build());
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendNotification_VersionNew(ChatUserModel chatUserModel, String sound_path) {


        String CHANNEL_ID = "my_channel_02";
        CharSequence CHANNEL_NAME = "my_channel_name";
        int IMPORTANCE = NotificationManager.IMPORTANCE_HIGH;

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        final NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, IMPORTANCE);
        channel.setShowBadge(true);
        channel.setSound(Uri.parse(sound_path), new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                .build()
        );
        builder.setChannelId(CHANNEL_ID);
        builder.setSound(Uri.parse(sound_path), AudioManager.STREAM_NOTIFICATION);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentTitle(chatUserModel.getName());

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        builder.setLargeIcon(bitmap);
        Intent intent = new Intent(this, NotificationsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("not", true);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        builder.setContentText(chatUserModel.getMessage());


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.createNotificationChannel(channel);
            manager.notify(Tags.not_tag,Tags.not_id, builder.build());
        }


    }

    private void sendNotification_VersionOld(ChatUserModel chatUserModel, String sound_path) {


        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSound(Uri.parse(sound_path), AudioManager.STREAM_NOTIFICATION);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setContentTitle(chatUserModel.getName());

        Intent intent = new Intent(this, NotificationsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("not", true);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        builder.setContentText(chatUserModel.getMessage());



        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(Tags.not_tag,Tags.not_id, builder.build());
        }


    }


    private int getCurrentUser_id() {
        return preferences.getUserData(this).getData().getId();
    }

    private UserModel getUserData() {
        return preferences.getUserData(this);
    }
    private String getRoom_id() {
        return preferences.getRoomId(this);
    }

    private String getSession() {
        return preferences.getSession(this);
    }


}
