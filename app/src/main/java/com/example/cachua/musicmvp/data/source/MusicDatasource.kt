package com.example.cachua.musicmvp.data.source

import com.example.cachua.musicmvp.data.model.MusicModel
import com.example.cachua.musicmvp.data.model.OnDataLoadedCallback

interface MusicDatasource {
    interface Local{
        fun getAllMusic(onDataLoadedCallback: OnDataLoadedCallback<ArrayList<MusicModel>>)
    }
}