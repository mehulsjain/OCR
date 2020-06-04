package com.example.ocr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.List;

//The comented lines in this code re for reading text in an image.
public class MainActivity extends AppCompatActivity {
    private final static int REQUEST_CAMERA_CAPTURE = 124;
    Button btn,faceapp;
    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    private FirebaseVisionTextRecognizer textRecognizer;
    FirebaseVisionImage image;
    FirebaseVisionFaceDetector detector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        btn = findViewById(R.id.btn);
        faceapp = findViewById(R.id.faceapp);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               if (i.resolveActivity(getPackageManager())!=null) {
//                   startActivityForResult(i, REQUEST_CAMERA_CAPTURE);
//               }
//            }
//        });
        faceapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, REQUEST_CAMERA_CAPTURE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CAMERA_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");
//            recognizeMyText(bitmap);
            detectFace(bitmap);
        }
    }
//    private void recognizeMyText(Bitmap bitmap){
//        try{
//            image = FirebaseVisionImage.fromBitmap(bitmap);
//            textRecognizer = FirebaseVision
//                    .getInstance()
//                    .getOnDeviceTextRecognizer();
//        }catch (Exception e){
//            e.printStackTrace();
//        };
//        textRecognizer.processImage(image)
//                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
//                    @Override
//                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
//                        String resultText = firebaseVisionText.getText();
//                        if (resultText.isEmpty()){
//                            Toast.makeText(MainActivity.this,"No Text Recognised",Toast.LENGTH_SHORT).show();
//                        }else {
//                            Intent intent = new Intent(MainActivity.this,ResultActivity.class);
//                            intent.putExtra("resultData",resultText);
//                            startActivity(intent);
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(MainActivity.this,"Failed",Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
        private void detectFace(Bitmap bitmap){
            FirebaseVisionFaceDetectorOptions options = new FirebaseVisionFaceDetectorOptions.Builder()
            .setModeType(FirebaseVisionFaceDetectorOptions.ACCURATE_MODE)
            .setClassificationType(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
            .setClassificationType(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .setMinFaceSize(0.15f)
            .setTrackingEnabled(true)
            .build();
            try{
                image = FirebaseVisionImage.fromBitmap(bitmap);
                detector = FirebaseVision.getInstance()
                        .getVisionFaceDetector(options);
            }catch (Exception e){
                e.printStackTrace();
            }

            detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                @Override
                public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                    String   resultText ="";
                    int i =1;
                    for (FirebaseVisionFace face:firebaseVisionFaces){
                        resultText = resultText.concat("\n"+i+".")
                                .concat("\nSmiles : " + face.getSmilingProbability()*100+"%")
                                .concat("\nLeftEye : " + face.getLeftEyeOpenProbability()*100+"%")
                                .concat("\nRightEye : " +face.getRightEyeOpenProbability()*100+"%");
                        i++;
                    }
                    if (firebaseVisionFaces.size()==0){
                        Toast.makeText(MainActivity.this,"No Faces",Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent = new Intent(MainActivity.this,ResultActivity.class);
                            intent.putExtra("resultData",resultText);
                            startActivity(intent);
                    }
                }
            });
        }
}
