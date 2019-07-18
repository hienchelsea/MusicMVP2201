package com.example.cachua.musicmvp.data.model

import android.net.Uri

data class MusicModel(var id:Int,
                      var artist:String,
                      var title:String,
                      var data:String,
                      var displayName:String,
                      var duration:Int,
                      var idAlbum: Long,
                      var uri: Uri)