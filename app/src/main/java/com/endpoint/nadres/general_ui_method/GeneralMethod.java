package com.endpoint.nadres.general_ui_method;

import android.net.Uri;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.endpoint.nadres.R;
import com.endpoint.nadres.models.RoomModel;
import com.endpoint.nadres.models.UserModel;
import com.endpoint.nadres.tags.Tags;
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

    @BindingAdapter("chat_user_image")
    public static void chat_user_image(View view, RoomModel roomModel) {
        if (view instanceof CircleImageView) {
            CircleImageView imageView = (CircleImageView) view;


            if (roomModel.getRoom_type().equals("single")){
                if (roomModel.getRoom_users().get(0).getUser_data().getLogo()!=null){
                    Picasso.get().load(Uri.parse(Tags.IMAGE_URL + roomModel.getRoom_users().get(0).getUser_data().getLogo())).placeholder(R.drawable.ic_avatar).into(imageView);

                }

            }else {

                if (roomModel.getRoom_users().get(0).getUser_data().getLogo()!=null){
                    Picasso.get().load(R.drawable.ic_group).into(imageView);

                }

            }


        } else if (view instanceof RoundedImageView) {
            RoundedImageView imageView = (RoundedImageView) view;

            if (roomModel.getRoom_type().equals("single")){
                if (roomModel.getRoom_users().get(0).getUser_data().getLogo()!=null){
                    Picasso.get().load(Uri.parse(Tags.IMAGE_URL + roomModel.getRoom_users().get(0).getUser_data().getLogo())).placeholder(R.drawable.ic_avatar).into(imageView);

                }

            }else {

                if (roomModel.getRoom_users().get(0).getUser_data().getLogo()!=null){
                    Picasso.get().load(R.drawable.ic_group).into(imageView);

                }

            }
        } else if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;

            if (roomModel.getRoom_type().equals("single")){
                if (roomModel.getRoom_users().get(0).getUser_data().getLogo()!=null){
                    Picasso.get().load(Uri.parse(Tags.IMAGE_URL + roomModel.getRoom_users().get(0).getUser_data().getLogo())).placeholder(R.drawable.ic_avatar).into(imageView);

                }

            }else {

                if (roomModel.getRoom_users().get(0).getUser_data().getLogo()!=null){
                    Picasso.get().load(R.drawable.ic_group).into(imageView);

                }

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










