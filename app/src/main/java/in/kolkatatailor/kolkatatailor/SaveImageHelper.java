package in.kolkatatailor.kolkatatailor;

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

public class SaveImageHelper implements com.squareup.picasso.Target{
    Context context;
    private WeakReference<AlertDialog> alertDialogWeakReference;
private WeakReference<ContentResolver>contentResolverWeakReference;
private String name;
private  String desc;

public SaveImageHelper(Context context, AlertDialog alertDialog, ContentResolver contentResolver, String name, String desc) {
        this.context=context;
        this.alertDialogWeakReference = new WeakReference<AlertDialog>(alertDialog);
        this.contentResolverWeakReference = new WeakReference<ContentResolver>(contentResolver);
        this.name = name;
        this.desc = desc;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
    ContentResolver r=contentResolverWeakReference.get();
     AlertDialog dialog=alertDialogWeakReference.get();
   dialog.setCancelable(true);
    if (r!=null){
        //MediaStore.Images.Media.insertImage(r,bitmap,name,desc);
        String bitmappath= MediaStore.Images.Media.insertImage(r,bitmap,name,desc);
        dialog.dismiss();
        /*
        Intent intent=new Intent();
        intent.setType("images/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
         context.startActivity(Intent.createChooser(intent,"View Image"));
*/
        switch (desc) {
            case "Share":
                try {
                    Uri bitmapUri = Uri.parse(bitmappath);
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                    //this is to get the app link in the playstore without launching your app.
                    final String appPackageName = "in.kolkatatailor.kolkatatailor";
                    String strAppLink = "";
                    strAppLink = "https://play.google.com/store/apps/details?id=" + appPackageName;
                    String shareBody = "Kolkata Tailor" +
                            "\n" + "" + strAppLink;
                    intent.putExtra(Intent.EXTRA_TEXT,shareBody);
                    intent.setType("image/jpg");
                    context.startActivity(Intent.createChooser(intent, "Share Image Via"));

                } catch (Exception e) {

                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();


                }
                break;

            case "Download":
                Toast.makeText(context, "Image successfully saved to your gallery", Toast.LENGTH_LONG).show();
                break;
        }
    }

      }
    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

        Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show();

        }
    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        }

}
