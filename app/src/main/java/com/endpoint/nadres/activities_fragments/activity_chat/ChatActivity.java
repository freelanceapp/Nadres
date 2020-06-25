package com.endpoint.nadres.activities_fragments.activity_chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.endpoint.nadres.R;
import com.endpoint.nadres.databinding.ActivityChatBinding;
import com.endpoint.nadres.interfaces.Listeners;
import com.endpoint.nadres.language.Language;
import com.endpoint.nadres.models.ChatUserModel;
import com.endpoint.nadres.models.MessageDataModel;
import com.endpoint.nadres.models.UserModel;
import com.endpoint.nadres.preferences.Preferences;
import com.endpoint.nadres.remote.Api;
import com.endpoint.nadres.tags.Tags;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityChatBinding binding;
    private String lang;
    private final int VID_REQ = 1;
    private final int IMG_REQ = 2;
    private final int CAMERA_REQ = 3;
    private final int MIC_REQ = 4;
    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String WRITE_PERM = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String CAMERA_PERM = Manifest.permission.CAMERA;
    private final String MIC_PERM = Manifest.permission.RECORD_AUDIO;
    private MediaRecorder recorder;
    private String audio_path = "";
    private Handler handler;
    private Runnable runnable;
    private long audio_total_seconds =0;
    private ChatUserModel chatUserModel;
    private UserModel userModel;
    private Preferences preferences;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        getDataFromIntent();
        initView();

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        chatUserModel = (ChatUserModel) intent.getSerializableExtra("data");

    }


    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        binding.setBackListener(this);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        binding.imageChooser.setOnClickListener(v -> {
            if (binding.expandedLayout.isExpanded()) {
                binding.expandedLayout.collapse(true);

            } else {
                binding.expandedLayout.expand(true);

            }
        });

        binding.btnHide.setOnClickListener(v -> {
            binding.expandedLayout.collapse(true);
        });

        binding.imgVideo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
            startActivityForResult(intent, VID_REQ);

        });

        binding.imgGallery.setOnClickListener(v -> {
            checkGalleryPermission();

        });

        binding.imageCamera.setOnClickListener(v -> {
            checkCameraPermission();
        });

        binding.imageRecord.setOnTouchListener((v, event) -> {
            if (event.getAction()==MotionEvent.ACTION_DOWN){
                if (isMicReady()){
                    createMediaRecorder();

                }else {
                    checkMicPermission();
                }
            }else if (event.getAction()==MotionEvent.ACTION_UP){

                if (isMicReady()){
                    try {
                        recorder.stop();
                        stopTimer();
                        sendAudio();
                    }catch (Exception e){
                        Log.e("error1",e.getMessage()+"___");
                    }

                }

            }

            return true;
        });

        binding.imageSend.setOnClickListener(v -> {
            String message = binding.edtMessage.getText().toString().trim();
            if (!message.isEmpty()){
                sendChatText(message);
            }
        });
        getAllMessages();
    }

    private void getAllMessages() {


    }

    private void sendAudio() {


    }

    private void sendChatText(String message){

        Api.getService(Tags.base_url)
                .sendChatMessage("Bearer " + userModel.getData().getToken(),chatUserModel.getRoom_id(),userModel.getData().getId(),"text",message)
                .enqueue(new Callback<MessageDataModel.MessageModel>() {
                    @Override
                    public void onResponse(Call<MessageDataModel.MessageModel> call, Response<MessageDataModel.MessageModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        /*if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                            *//*if (response.body().getData().size() > 0) {
                                list.clear();
                                list.addAll(response.body().getData());
                                binding.llConversation.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();

                            } else {
                                binding.llConversation.setVisibility(View.VISIBLE);
                            }*//*

                        }*/

                    }

                    @Override
                    public void onFailure(Call<MessageDataModel.MessageModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);
                            if (t.getMessage() != null) {
                                Log.e("Error", t.getMessage());

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(ChatActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                }else if (t.getMessage().contains("socket")){

                                }else {
                                    Toast.makeText(ChatActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                });
    }


    private void createMediaRecorder(){

        String audio_name = "AUD"+System.currentTimeMillis()+".mp3";

        File file = new File(Tags.audio_path);
        boolean isFolderCreate;

        if (!file.exists()){
            isFolderCreate = file.mkdir();
        }else {
            isFolderCreate = true;
        }


        if (isFolderCreate){
            startTimer();
            binding.recordTime.setVisibility(View.VISIBLE);
            createVibration();
            audio_path = file.getAbsolutePath()+"/"+audio_name;
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioChannels(1);
            recorder.setOutputFile(audio_path);
            try {
                recorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            recorder.start();
        }else {
            Toast.makeText(this, "Unable to create sound file on your device", Toast.LENGTH_SHORT).show();
        }


    }

    private void createVibration() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (vibrator!=null){
                vibrator.vibrate(VibrationEffect.createOneShot(200,VibrationEffect.DEFAULT_AMPLITUDE));

            }
        }else {
            if (vibrator!=null){
                vibrator.vibrate(new long[]{200,200},0);
            }
        }
    }

    private void checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, CAMERA_PERM) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, WRITE_PERM) == PackageManager.PERMISSION_GRANTED) {
            selectImage(CAMERA_REQ);

        } else {

            ActivityCompat.requestPermissions(this, new String[]{CAMERA_PERM, WRITE_PERM}, CAMERA_REQ);

        }

    }


    private void checkGalleryPermission() {
        if (ActivityCompat.checkSelfPermission(this, READ_PERM) == PackageManager.PERMISSION_GRANTED) {
            selectImage(IMG_REQ);

        } else {
            ActivityCompat.requestPermissions(this, new String[]{READ_PERM}, IMG_REQ);

        }

    }


    private void checkMicPermission() {
        if (ActivityCompat.checkSelfPermission(this, MIC_PERM) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, WRITE_PERM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{MIC_PERM, WRITE_PERM}, MIC_REQ);

        }

    }

    private boolean isMicReady(){

        if (ActivityCompat.checkSelfPermission(this, MIC_PERM) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, WRITE_PERM) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        return false;

    }

    private void selectImage(int req) {

        Intent intent = new Intent();
        if (req == IMG_REQ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.setFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

            } else {
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


            }
            intent.setType("image/*");


        } else if (req == CAMERA_REQ) {

            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        }

        startActivityForResult(intent, req);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == IMG_REQ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage(requestCode);
            } else {
                Toast.makeText(this, "Access image denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == CAMERA_REQ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                selectImage(requestCode);
            } else {
                Toast.makeText(this, "Access camera denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == MIC_REQ) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Access camera denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMG_REQ && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

        } else if (requestCode == CAMERA_REQ && resultCode == RESULT_OK && data != null) {

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            Uri uri = getUriFromBitmap(bitmap);
        }

    }


    private Uri getUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
        return Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", ""));

    }


    private void startTimer(){
        handler = new Handler();
        runnable = () -> {
            audio_total_seconds +=1;
            binding.recordTime.setText(getRecordTimeFormat(audio_total_seconds));
            startTimer();
            Log.e("total",audio_total_seconds+"__");
        };

        handler.postDelayed(runnable,1000);
    }

    private void stopTimer(){
        if (recorder!=null){
            recorder.release();
            recorder=null;
        }
        audio_total_seconds = 0;
        if (runnable!=null){
            handler.removeCallbacks(runnable);

        }
        handler = null;
        binding.recordTime.setText("00:00:00");
        binding.recordTime.setVisibility(View.GONE);
    }

    private String getRecordTimeFormat(long seconds){
        int hours = (int) (seconds/3600);
        int minutes = (int) ((seconds%3600)/60);
        int second = (int) (seconds%60);

        return String.format(Locale.ENGLISH,"%02d:%02d:%02d",hours,minutes,second);

    }


    private void deleteFile(){
        if (!audio_path.isEmpty()){
            File file = new File(audio_path);
            if (file.exists()){
                file.delete();
            }
        }
    }

    @Override
    public void back() {
        finish();
    }


}

