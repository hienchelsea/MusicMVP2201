package com.example.cachua.musicmvp.ui.music.Adapater


import android.content.Context

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.support.constraint.ConstraintLayout
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.cachua.musicmvp.R
import com.example.cachua.musicmvp.data.model.MusicModel


class MusicAdapter (var mContext: Context,var mMusicArray: ArrayList<MusicModel>): RecyclerView.Adapter<MusicAdapter.ViewHolder>(){

    private lateinit var callBack: CallBack

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context).inflate(R.layout.item_layout, p0, false)
        return ViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return mMusicArray.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        var music= mMusicArray[p1]
        p0.mTextTitlePlay.text= music.title
        p0.mTextArtistPlay.text= music.artist
        Glide
                .with(mContext)
                .load((music.uri))
                .placeholder(R.drawable.icon_music_player).into(p0.mImageAvatarPlay)
        p0.mConstraintItem.setOnClickListener {
            callBack.loadMusic(mMusicArray,p1)
        }

    }


    class ViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        var mTextTitlePlay= itemView.findViewById(R.id.textItemTitlePlay) as TextView
        var mTextArtistPlay= itemView.findViewById(R.id.textItemArtistPlay) as TextView
        var mImageAvatarPlay= itemView.findViewById(R.id.imageItemAvatarPlay) as ImageView
        var mConstraintItem= itemView.findViewById(R.id.constraintItem) as ConstraintLayout
    }

    interface CallBack {
        fun loadMusic(music:ArrayList<MusicModel>,i:Int)
    }

    fun setCallBack(callBack: CallBack) {
        this.callBack = callBack
    }
    fun upDateAdapter( mContext: Context,mMusicArray: ArrayList<MusicModel>){
        this.mContext= mContext
        this.mMusicArray= mMusicArray
    }






}
