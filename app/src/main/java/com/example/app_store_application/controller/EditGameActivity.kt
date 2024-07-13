package com.example.app_store_application.controller

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.app_store_application.R
import com.example.app_store_application.database.GameEntity
import com.example.app_store_application.ViewModel.EditGameViewModel
import java.io.ByteArrayOutputStream

class EditActivity : AppCompatActivity() {

    private lateinit var viewModel: EditGameViewModel
    private lateinit var game: GameEntity
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var ivSelectedImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_game)
        ivSelectedImage = findViewById(R.id.ivimageViewGameedit)

        val gameId = intent.getIntExtra("GAME_ID", -1)

        // Set up ActivityResultLauncher
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                handleImageSelectionResult(data)
            }
        }

        val selectImageButton = findViewById<Button>(R.id.btnbuttonSelectImageedit)
        selectImageButton.setOnClickListener {
            selectImage()
        }

        if (gameId == -1) {
            Toast.makeText(this, "Invalid Game ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        viewModel = ViewModelProvider(this)[EditGameViewModel::class.java]
        viewModel.getGameById(gameId).observe(this) { gameEntity ->
            gameEntity?.let {
                game = it
                populateGameDetails(game)
            }
        }

        val updateButton = findViewById<Button>(R.id.btnbuttonUpdateGame)
        updateButton.setOnClickListener {
            updateGameDetails()
        }
    }

    private fun populateGameDetails(game: GameEntity) {
        val gameNameEditText = findViewById<EditText>(R.id.etTextGameNameedit)
        val gameUrlEditText = findViewById<EditText>(R.id.etTextGameUrledit)
        val gameImageView = findViewById<ImageView>(R.id.ivimageViewGameedit)

        gameNameEditText.setText(game.gameName)
        gameUrlEditText.setText(game.gameUrl)

        val bitmap = BitmapFactory.decodeByteArray(game.imageGame, 0, game.imageGame.size)
        gameImageView.setImageBitmap(bitmap)
    }

    private fun updateGameDetails() {
        val gameNameEditText = findViewById<EditText>(R.id.etTextGameNameedit)
        val gameUrlEditText = findViewById<EditText>(R.id.etTextGameUrledit)

        game.gameName = gameNameEditText.text.toString()
        game.gameUrl = gameUrlEditText.text.toString()

        // Convert ImageView to Bitmap if needed
        val bitmap = (ivSelectedImage.drawable as BitmapDrawable).bitmap
        // Convert Bitmap to ByteArray
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray: ByteArray = stream.toByteArray()

        game.imageGame = byteArray


        viewModel.updateGame(game)

        Toast.makeText(this, "Update successfully", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    //Allow user to choose another image from their device
    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }

    private fun handleImageSelectionResult(data: Intent?) {
        val selectedImageUri = data?.data
        if (selectedImageUri != null) {
            ivSelectedImage.visibility = View.VISIBLE
            ivSelectedImage.setImageURI(selectedImageUri)
            // You may want to save or use selectedImageUri as needed
        }
    }
}