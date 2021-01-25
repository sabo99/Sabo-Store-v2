package com.sabo.sabostorev2.ui.Account.Menu.BottomSheet

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sabo.sabostorev2.EventBus.ActionPhotoProfileEvent
import com.sabo.sabostorev2.R
import org.greenrobot.eventbus.EventBus

class MenuPhotoProfile : BottomSheetDialogFragment(), View.OnClickListener {

    companion object {
        private var instance : MenuPhotoProfile ? = null
        fun getInstance() : MenuPhotoProfile? {
            if (instance == null)
                instance = MenuPhotoProfile()
            return instance
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root : View = inflater.inflate(R.layout.fragment_menu_photo_profile, container, false)

        initViews(root)
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (requireView().parent as View).setBackgroundColor(Color.TRANSPARENT)
    }

    private fun initViews(root: View) {
        root.findViewById<LinearLayout>(R.id.removePhoto).setOnClickListener(this)
        root.findViewById<LinearLayout>(R.id.changePhoto).setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.removePhoto -> {
                EventBus.getDefault().postSticky(ActionPhotoProfileEvent(true, false))
                instance!!.dismiss()
            }
            R.id.changePhoto -> {
                EventBus.getDefault().postSticky(ActionPhotoProfileEvent(false, true))
                instance!!.dismiss()
            }
        }
    }

}