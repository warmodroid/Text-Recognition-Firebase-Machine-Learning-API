package com.example.textrecognition

import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URI


var RESULT_LOAD_IMAGE = 1

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            val i = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, RESULT_LOAD_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            var uri = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? = contentResolver.query(uri!!,
                    filePathColumn, null, null, null)
            cursor?.moveToFirst()
            val columnIndex: Int = cursor!!.getColumnIndex(filePathColumn[0])
            val picturePath: String = cursor.getString(columnIndex)
            cursor?.close()
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            runTextRecognition(uri)
        }
    }

    private fun runTextRecognition(uri: Uri) {
        val image = InputImage.fromFilePath(this, uri)
        val recognizer = TextRecognition.getClient()
        button.isEnabled = false
        recognizer.process(image)
                .addOnSuccessListener { texts ->
                    button.isEnabled = true
                    processTextRecognitionResult(texts)
                }
                .addOnFailureListener { e -> // Task failed with an exception
                    button.isEnabled = true
                    e.printStackTrace()
                }
    }

    private fun processTextRecognitionResult(texts: Text) {
        Log.i("my", texts.text)
        //Toast.makeText(this, texts.toString(), Toast.LENGTH_LONG).show();
        textView.text = texts.text
    }
}