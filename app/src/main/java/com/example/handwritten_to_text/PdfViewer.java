package com.example.handwritten_to_text;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class PdfViewer extends AppCompatActivity {

    PDFView pdfView;
    Button sharePdf,uploadpdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        pdfView = (PDFView) findViewById(R.id.view);
        sharePdf = findViewById(R.id.sharePdf);
        String path = getExternalFilesDir(null).toString() + "/user.pdf";

        File pdffile = new File(path);
        pdfView.fromFile(pdffile)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAnnotationRendering(false)
                .defaultPage(0)
                .password(null)
                .scrollHandle(null)
                .load();

        sharePdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                if(!pdffile.exists()){
                    Toast.makeText(PdfViewer.this,"Could not share the file!",Toast.LENGTH_SHORT).show();
                    return;
                }
                /*Uri uri = FileProvider.getUriForFile(PdfViewer.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        pdffile);*/

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("application/pdf");
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+pdffile));

                startActivity(Intent.createChooser(share,"choose an application"));
            }
        });

        /*uploadpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

                StorageReference pdfRef = storageRef.child("user.pdf");
                StorageReference pdfPathRef = storageRef.child(path);

                Uri file = Uri.fromFile(new File(path));
                StorageReference riversRef = storageRef.child(path);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] data = baos.toByteArray();
                UploadTask uploadTask = pdfRef.putBytes(data);
                uploadTask = riversRef.putFile(file);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(PdfViewer.this,"Couldn't upload PDF ):",Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(PdfViewer.this,"Uploading PDF ):",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });*/
    }
}