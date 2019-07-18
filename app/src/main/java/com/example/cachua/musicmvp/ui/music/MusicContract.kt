package com.example.cachua.musicmvp.ui.music

import com.example.cachua.musicmvp.data.model.MusicModel

interface MusicContract {
    interface View{
        fun displayListMusic(arrayMusic:ArrayList<MusicModel>)
        fun onError()
    }
    interface Presenter{
        fun loadDisPlayListMusic()
    }
}