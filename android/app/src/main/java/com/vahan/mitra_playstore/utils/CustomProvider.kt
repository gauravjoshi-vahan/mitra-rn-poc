package com.vahan.mitra_playstore.utils

import androidx.core.content.FileProvider

// NOTE This class is used for removing the conflict for FileProvider btw ChatBot and DocumentUpload
class CustomProvider : FileProvider() {

}