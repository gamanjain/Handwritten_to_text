package com.example.handwritten_to_text;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainApp extends AppCompatActivity {
    private static final int PICK_IMAGE = 15;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int CAMERA_PERM_CODE = 101;
    Button logout,click,choose,convert,copy,share,pdfconvert;
    ImageView imageView;
    private long backPressedTime;
    private Toast backToast;
    private Uri outputFileUri;
    String currentPhotoPath;
    String convertedText=null;
    InputImage image;
    Boolean selectedImage=false,textIsConverted=false;
    TextView textView;

    @Override
    public void onBackPressed() {
        if(backPressedTime+2000>System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        }
        else{
            backToast=Toast.makeText(getBaseContext(),"Press back again to exit",Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime=System.currentTimeMillis();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_app);
        logout=findViewById(R.id.logout);
        click=findViewById(R.id.click);
        choose=findViewById(R.id.choose);
        imageView=findViewById(R.id.image_view);
        imageView.setImageResource(R.drawable.nikefootball);
        convert=findViewById(R.id.convert);
        textView=findViewById(R.id.textView2);
        copy=findViewById(R.id.copyText);
        share=findViewById(R.id.shareText);
        pdfconvert=findViewById(R.id.pdfconvert);

        if(!textIsConverted){
            copy.setVisibility(View.INVISIBLE);
            share.setVisibility(View.INVISIBLE);
            pdfconvert.setVisibility(View.INVISIBLE);
        }

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPictureIntent();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("remember",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("saveinfo","false");
                editor.apply();
                finish();
            }
        });

        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selectedImage){
                    Toast.makeText(MainApp.this,"Please select an Image first",Toast.LENGTH_SHORT).show();
                }
                else{
                    convertImageToText();
                }
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT,convertedText);
                shareIntent.setType("text/plain");
                Toast.makeText(MainApp.this,"Sharing text",Toast.LENGTH_SHORT).show();
                startActivity(shareIntent);
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copy Text",convertedText);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(MainApp.this,"Text copied to clipboard",Toast.LENGTH_SHORT).show();
            }
        });

        pdfconvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = getExternalFilesDir(null).toString()+"/user.pdf";
                File file = new File(path);
                if(!file.exists()){
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Document document = new Document(PageSize.A4);
                try {
                    PdfWriter.getInstance(document, new FileOutputStream(file.getAbsoluteFile()));
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                document.open();
                Font myFont = new Font(Font.FontFamily.TIMES_ROMAN, 18);
                Paragraph paragraph = new Paragraph();
                paragraph.add(new Paragraph(convertedText,myFont));

                try {
                    document.add(paragraph);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                document.close();
                Toast.makeText(MainApp.this,"PDF is created!",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(),PdfViewer.class);
                startActivity(intent);
            }
        });
    }

    private void selectPictureIntent() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
        Toast.makeText(MainApp.this,"Choosing image",Toast.LENGTH_SHORT).show();
        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            setPic();
        }
        else if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
            outputFileUri = data.getData();
            // Get the path from the Uri
            final String path = getPathFromURI(outputFileUri);
            if (path != null) {
                Toast.makeText(MainApp.this,"Displaying Image",Toast.LENGTH_SHORT).show();
                File f = new File(path);
                outputFileUri = Uri.fromFile(f);
            }
            // Set the image in ImageView
            try {
                Bitmap bitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(outputFileUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            selectedImage=true;
            imageView.setImageURI(outputFileUri);
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    private void convertImageToText() {
        InputImage image;
        try {
            Log.d("uri",outputFileUri.toString());
            image = InputImage.fromFilePath(getApplicationContext(), outputFileUri);
            Toast.makeText(MainApp.this,"Converting image to text",Toast.LENGTH_SHORT).show();
            TextRecognizer recognizer = TextRecognition.getClient();
            Task<Text> result =
                    recognizer.process(image)
                            .addOnSuccessListener(new OnSuccessListener<Text>() {
                                @Override
                                public void onSuccess(Text result) {
                                    convertedText = result.getText();
                                    for (Text.TextBlock block : result.getTextBlocks()) {
                                        String blockText = block.getText();
                                        Point[] blockCornerPoints = block.getCornerPoints();
                                        Rect blockFrame = block.getBoundingBox();
                                        for (Text.Line line : block.getLines()) {
                                            String lineText = line.getText();
                                            Point[] lineCornerPoints = line.getCornerPoints();
                                            Rect lineFrame = line.getBoundingBox();
                                            for (Text.Element element : line.getElements()) {
                                                String elementText = element.getText();
                                                Point[] elementCornerPoints = element.getCornerPoints();
                                                Rect elementFrame = element.getBoundingBox();
                                            }
                                        }
                                    }
                                    Log.d("result",convertedText);
                                    if(convertedText==null||convertedText.equals("")){
                                        Toast.makeText(MainApp.this,"No text detected",Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    textIsConverted=true;
                                    copy.setVisibility(View.VISIBLE);
                                    share.setVisibility(View.VISIBLE);
                                    pdfconvert.setVisibility(View.VISIBLE);
                                    textView.setMovementMethod(new ScrollingMovementMethod());
                                    textView.setText(convertedText);
                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Task failed with an exception
                                            Toast.makeText(MainApp.this,"Couldn't convert to text",Toast.LENGTH_SHORT).show();
                                        }
                                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                outputFileUri = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                Toast.makeText(MainApp.this,"Clicking image",Toast.LENGTH_SHORT).show();
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = 200;
        int targetH = 200;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Toast.makeText(MainApp.this,"Displaying image",Toast.LENGTH_SHORT).show();

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        selectedImage=true;
        imageView.setImageBitmap(bitmap);
    }
}
