package com.demo.btvideo.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.demo.btvideo.R

class LoaderAdapter() :
        LoadStateAdapter<LoaderAdapter.LoaderViewHolder>() {

    override fun onBindViewHolder(holder: LoaderViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoaderViewHolder {
        return LoaderViewHolder.getInstance(parent)
    }

    /**
     * view holder class for footer loader and error state handling
     */
    class LoaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        companion object {
            //get instance of the DoggoImageViewHolder
            fun getInstance(parent: ViewGroup): LoaderViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val view = inflater.inflate(R.layout.index_footer, parent, false)
                return LoaderViewHolder(view)
            }
        }

//        val motionLayout: MotionLayout = view.findViewById(R.id.mlLoader)

        init {
//            view.findViewById<Button>(R.id.btnRetry).setOnClickListener {
//                retry()
//            }
        }

        fun bind(loadState: LoadState) {
//            if (loadState is LoadState.Loading) {
//                motionLayout.transitionToEnd()
//            } else {
//                motionLayout.transitionToStart()
//            }
        }
    }
}