package cat.dam.pau.fotografia_plus;


import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView iv_photo;
    Button btn_photo;
    Button btn_rotate;
    Button btn_filter;
    Button btn_gallery;

    int rotation = 0;

    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<Intent> activityResultLauncherGallery;

    String currentPhotoPath;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setElements();

        //ActivityResult de la càmera
        activityResultLauncher = registerForActivityResult(new
                ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Bundle bundle = result.getData().getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");
                iv_photo.setImageBitmap(bitmap);
            }
        });

        //ActivityResult de la galeria
        activityResultLauncherGallery = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri imageUri = data.getData();
                        System.out.println("galeria: "+imageUri);
                        iv_photo.setImageURI(imageUri);
                    } else {
                        //En cas de cancel·lar
                        Toast.makeText(MainActivity.this, "Cancelled...", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }


    //Funció d'accés a la càmera
    public void makePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            activityResultLauncher.launch(intent);
        } else {
            Toast.makeText(MainActivity.this, "You need the right permissions to do that.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //Funció d'aplicació de filtre
    public void applyFilter(){
        int selectedColor = getResources().getColor(R.color.colorRed);
        iv_photo.setColorFilter(R.color.colorRed);
    }

    //Funció de rotació de la foto
    public void rotate(){
        rotation += 90;
        iv_photo.setRotation(rotation);
    }

    //Funció d'accés a la galeria
    public void gotoGallery(){
        try {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            if (intent.resolveActivity(getPackageManager()) != null) {
                activityResultLauncherGallery.launch(Intent.createChooser(intent, "Select File"));
            } else {
                Toast.makeText(MainActivity.this, "El seu dispositiu no permet accedir a la galeria",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File crearFitxerImatge() throws IOException {
        // Crea el fitxer per a la imatge amb nom únic
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String NomFitxerImatge = "JPEG_" + timeStamp + "_";
        File dirFitxers = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imatge = File.createTempFile(
                NomFitxerImatge,
                ".jpg",
                dirFitxers 
        );

        currentPhotoPath = imatge.getAbsolutePath();
        return imatge;
    }

    //Funció d'inicialització dels components
    public void setElements() {
        iv_photo = (ImageView) findViewById(R.id.iv_photo);
        btn_photo = (Button) findViewById(R.id.foto);
        btn_rotate = (Button) findViewById(R.id.rotar);
        btn_filter = (Button) findViewById(R.id.filtrar);
        btn_gallery = (Button) findViewById(R.id.galeria);

        assingButtonListeners();
    }

    //Listeners
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.foto): makePhoto(); break;
            case (R.id.rotar): rotate(); break;
            case (R.id.filtrar):  applyFilter(); break;
            case (R.id.galeria):  gotoGallery(); break;
        }
    }

    //Funció d'assignació de listeners per cada botó
    public void assingButtonListeners() {
        btn_photo.setOnClickListener(this);
        btn_rotate.setOnClickListener(this);
        btn_filter.setOnClickListener(this);
        btn_gallery.setOnClickListener(this);
    }

}