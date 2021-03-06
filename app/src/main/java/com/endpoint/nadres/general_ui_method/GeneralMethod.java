package com.endpoint.nadres.general_ui_method;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.endpoint.nadres.R;
import com.endpoint.nadres.models.RoomModel;
import com.endpoint.nadres.models.UserModel;
import com.endpoint.nadres.tags.Tags;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class GeneralMethod {

    @BindingAdapter("error")
    public static void errorValidation(View view, String error) {
        if (view instanceof EditText) {
            EditText ed = (EditText) view;
            ed.setError(error);
        } else if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setError(error);


        }
    }
    @BindingAdapter("rate")
    public static void rate (SimpleRatingBar simpleRatingBar, double rate)
    {
        SimpleRatingBar.AnimationBuilder builder = simpleRatingBar.getAnimationBuilder()
                .setRatingTarget((float) rate)
                .setDuration(1000)
                .setRepeatCount(0)
                .setInterpolator(new LinearInterpolator());
        builder.start();
    }
    @BindingAdapter("chat_user_image")
    public static void chat_user_image(View view, RoomModel roomModel) {
        if (view instanceof CircleImageView) {
            CircleImageView imageView = (CircleImageView) view;

            if (roomModel.getRoom_type().equals("single")){
                if (roomModel.getChat_room_image()!=null){
                    Picasso.get().load(Uri.parse(Tags.IMAGE_URL +roomModel.getChat_room_image())).placeholder(R.drawable.ic_avatar).into(imageView);

                }else {
                    Log.e("t","tt");
                    Picasso.get().load(R.drawable.ic_avatar).into(imageView);

                }

            }else {

                if (roomModel.getChat_room_image()!=null){
                    Picasso.get().load(R.drawable.ic_group).into(imageView);

                }else {
                    Picasso.get().load(R.drawable.ic_group).into(imageView);

                }

            }


        } else if (view instanceof RoundedImageView) {
            RoundedImageView imageView = (RoundedImageView) view;

            if (roomModel.getRoom_type().equals("single")){
                if (roomModel.getChat_room_image()!=null){
                    Picasso.get().load(Uri.parse(Tags.IMAGE_URL + roomModel.getChat_room_image())).placeholder(R.drawable.ic_avatar).into(imageView);

                }else {
                    Picasso.get().load(R.drawable.ic_avatar).into(imageView);

                }

            }else {

                if (roomModel.getChat_room_image()!=null){
                    Picasso.get().load(R.drawable.ic_group).into(imageView);

                }else {
                    Picasso.get().load(R.drawable.ic_group).into(imageView);

                }


            }
        } else if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;

            if (roomModel.getRoom_type().equals("single")){
                if (roomModel.getChat_room_image()!=null){
                    Picasso.get().load(Uri.parse(Tags.IMAGE_URL + roomModel.getChat_room_image())).placeholder(R.drawable.ic_avatar).into(imageView);

                }else {
                    Picasso.get().load(R.drawable.ic_avatar).into(imageView);

                }

            }else {

                if (roomModel.getChat_room_image()!=null){
                    Picasso.get().load(R.drawable.ic_group).into(imageView);

                }else {
                    Picasso.get().load(R.drawable.ic_group).into(imageView);

                }


            }
        }

    }


    @BindingAdapter("chat_image")
    public static void chat_image(View view, String endPoint) {
        if (view instanceof CircleImageView) {
            CircleImageView imageView = (CircleImageView) view;
            if (endPoint!=null){
                Glide.with(imageView.getContext()).load(Tags.IMAGE_URL+endPoint).into(imageView);

            }


        } else if (view instanceof RoundedImageView) {
            RoundedImageView imageView = (RoundedImageView) view;
            if (endPoint!=null){
                Glide.with(imageView.getContext()).load(Tags.IMAGE_URL+endPoint).into(imageView);

            }
        } else if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            if (endPoint!=null){
                Glide.with(imageView.getContext()).load(Tags.IMAGE_URL+endPoint).into(imageView);

            }

        }

    }


    @BindingAdapter("chat_video_frame")
    public static void chat_video_frame(View view, String endPoint) {
        if (view instanceof CircleImageView) {
            CircleImageView imageView = (CircleImageView) view;
            if (endPoint!=null){
                RequestOptions requestOptions = new RequestOptions().frame(5000000);
                Glide.with(imageView.getContext()).load(Uri.parse(Tags.IMAGE_URL+endPoint))
                        .apply(requestOptions)
                        .into(imageView);
            }


        } else if (view instanceof RoundedImageView) {
            RoundedImageView imageView = (RoundedImageView) view;
            if (endPoint!=null){
                RequestOptions requestOptions = new RequestOptions().frame(5000000);
                Glide.with(imageView.getContext()).load(Uri.parse(Tags.IMAGE_URL+endPoint))
                        .apply(requestOptions)
                        .into(imageView);
            }
        } else if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            if (endPoint!=null){
                RequestOptions requestOptions = new RequestOptions().frame(5000000);
                Glide.with(imageView.getContext()).load(Uri.parse(Tags.IMAGE_URL+endPoint))
                        .apply(requestOptions)
                        .into(imageView);
            }

        }

    }




    @BindingAdapter("request_image")
    public static void request_image(View view, String endPoint) {
        if (view instanceof CircleImageView) {
            CircleImageView imageView = (CircleImageView) view;
            if (endPoint != null) {

                Picasso.get().load(Uri.parse(Tags.IMAGE_URL + endPoint)).placeholder(R.drawable.ic_avatar).into(imageView);
            } else {
                Picasso.get().load(R.drawable.ic_avatar).into(imageView);

            }
        } else if (view instanceof RoundedImageView) {
            RoundedImageView imageView = (RoundedImageView) view;

            if (endPoint != null) {

                Picasso.get().load(Uri.parse(Tags.IMAGE_URL + endPoint)).placeholder(R.drawable.ic_avatar).fit().into(imageView);
            } else {
                Picasso.get().load(R.drawable.ic_avatar).into(imageView);

            }
        } else if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;

            if (endPoint != null) {

                Picasso.get().load(Uri.parse(Tags.IMAGE_URL + endPoint)).placeholder(R.drawable.ic_avatar).fit().into(imageView);
            } else {
                Picasso.get().load(R.drawable.ic_avatar).into(imageView);

            }
        }

    }


    @BindingAdapter("image")
    public static void image(View view, String endPoint) {
        if (view instanceof CircleImageView) {
            CircleImageView imageView = (CircleImageView) view;
            if (endPoint != null) {

                Picasso.get().load(Uri.parse(Tags.IMAGE_URL + endPoint)).placeholder(R.drawable.logo).into(imageView);
            } else {
                Picasso.get().load(R.drawable.logo).into(imageView);

            }
        } else if (view instanceof RoundedImageView) {
            RoundedImageView imageView = (RoundedImageView) view;

            if (endPoint != null) {

                Picasso.get().load(Uri.parse(Tags.IMAGE_URL + endPoint)).placeholder(R.drawable.logo).fit().into(imageView);
            } else {
                Picasso.get().load(R.drawable.logo).into(imageView);

            }
        } else if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;

            if (endPoint != null) {

                Picasso.get().load(Uri.parse(Tags.IMAGE_URL + endPoint)).placeholder(R.drawable.logo).fit().into(imageView);
            } else {
                Picasso.get().load(R.drawable.logo).into(imageView);

            }
        }

    }

    @BindingAdapter("usermodel")
    public static void errorValidation(View view, UserModel userModel) {
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            if (userModel.getData().getType().equals("student")) {
                tv.setText(userModel.getData().getClass_fk().get(0).getStage_class_name().getTitle());

            } else {
                String data = "";
                for (int i = 0; i < userModel.getData().getSkills_fk().size(); i++) {
                    if (i % 2 == 0 && i > 0) {
                        data += "\n";
                    }
                    data += userModel.getData().getSkills_fk().get(i).getSkill_type() + "  ";

                }
                tv.setText(data);
            }


        }
    }
}










