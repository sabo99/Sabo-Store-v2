package com.sabo.sabostorev2.Model.GradleUsed

class GradleUsedData {
    companion object {
        private val gradle =
                arrayOf(
                        "RxAndroid",
                        "RxJava",
                        "Room DB",
                        "Retrofit",
                        "CircleImageView",
                        "Picasso",
                        "Android-Image-Cropper",
                        "EventBus",
                        "CounterFAB",
                        "Android-Image-Slider",
                        "MaterialLetterIcon",
                        "NotificationBadge",
                        "SweetAlertDialog",
                        "IntentAnimation",
                        "SimpleSearchView",
                        "PhotoView",
                        "CountryCodePicker",
                        "LabelView",
                        "Stepper-Touch",
                        "ReadMoreOption",
                        "ExpandableTextView"

                        )

        private val link =
                arrayOf(
                        "https://github.com/ReactiveX/RxAndroid",
                        "https://github.com/ReactiveX/RxJava",
                        "https://developer.android.com/jetpack/androidx/releases/room",
                        "https://square.github.io/retrofit/",
                        "https://github.com/hdodenhof/CircleImageView",
                        "https://github.com/square/picasso",
                        "https://github.com/ArthurHub/Android-Image-Cropper",
                        "https://github.com/greenrobot/EventBus",
                        "https://github.com/andremion/CounterFab",
                        "https://github.com/smarteist/Android-Image-Slider",
                        "https://github.com/IvBaranov/MaterialLetterIcon",
                        "https://github.com/nex3z/NotificationBadge",
                        "https://jitpack.io/p/thomper/sweet-alert-dialog",
                        "https://github.com/hajiyevelnur92/intentanimation",
                        "https://github.com/Ferfalk/SimpleSearchView",
                        "https://github.com/chrisbanes/PhotoView",
                        "https://github.com/joielechong/CountryCodePicker",
                        "https://github.com/linger1216/labelview",
                        "https://github.com/DanielMartinus/Stepper-Touch",
                        "https://github.com/devendroid/ReadMoreOption",
                        "https://github.com/Manabu-GT/ExpandableTextView",
                )

        fun getListData(): ArrayList<GradleUsedModel> {
            val list: ArrayList<GradleUsedModel> = ArrayList()
            for (position in gradle.indices) {
                val g = GradleUsedModel()
                g.gradleName = gradle[position]
                g.gradleLink = link[position]
                list.add(g)
            }
            return list
        }
    }


}