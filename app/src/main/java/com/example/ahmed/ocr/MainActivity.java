package com.example.ahmed.ocr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int GALLERY_INTENT = 1;
    ImageView imageView ;
    TextView detectedTextView ;
    private Uri imgUri;
    private Bitmap myUploadPhoto ;
    ProgressDialog progressDialog;

    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        detectedTextView = findViewById(R.id.textView);

        textToSpeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        progressDialog = new ProgressDialog(this);
    }

    public void getText(View view) throws IOException {
        progressDialog.setTitle("Wait Until Read ...");
        if(imgUri != null) {
            progressDialog.show();
            myUploadPhoto = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
            TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
            if (!textRecognizer.isOperational()) {
                Toast.makeText(this, "Cant find text", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            } else {
                Frame frame = new Frame.Builder().setBitmap(myUploadPhoto).build();
                SparseArray<TextBlock> items = textRecognizer.detect(frame);
                StringBuilder ab = new StringBuilder();
                for (int i = 0; i < items.size(); i++) {
                    TextBlock myItem = items.valueAt(i);
                    ab.append(myItem.getValue());
                    ab.append("\n");
                }
                detectedTextView.setMovementMethod(new ScrollingMovementMethod());
                detectedTextView.setGravity(0);
                detectedTextView.setText(ab.toString());
                progressDialog.dismiss();
            }
        }
        else
            Toast.makeText(this, "Please Select Photo First", Toast.LENGTH_LONG).show();

    }

    public void upload(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK )
        {
                imgUri = data.getData();
            try {
                myUploadPhoto = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(myUploadPhoto);

        }
    }

    public void speech(View view) {
        String toSpeak = detectedTextView.getText().toString();
        Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
        textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }
}
