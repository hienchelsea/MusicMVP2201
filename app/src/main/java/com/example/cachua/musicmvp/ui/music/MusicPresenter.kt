package com.example.cachua.musicmvp.ui.music

import com.example.cachua.musicmvp.data.model.MusicModel
import com.example.cachua.musicmvp.data.model.OnDataLoadedCallback
import com.example.cachua.musicmvp.data.repository.MusicRepository
import kotlin.collections.ArrayList


class MusicPresenter(private var mMusicRepository: MusicRepository, private var mMusicActivity: MusicContract.View) :MusicContract.Presenter {

    override fun loadDisPlayListMusic() {
        mMusicRepository.getAllMusic(object:OnDataLoadedCallback<ArrayList<MusicModel>>{
            override fun onDataLoaded(data: ArrayList<MusicModel>) {
                mMusicActivity.displayListMusic(data)
            }
            override fun onDataNotAvailable(exception: Exception) {
                mMusicActivity.onError()
            }

        })
    }


}