package com.mantis.customgallery

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mantis.infinitegallery.InfiniteGallery

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        val listOfImage = mutableListOf<Image>()
        val listOfView = mutableListOf<ChildView>()
        listOfImage.add(Image("https://4.img-dpreview.com/files/p/E~TS590x0~articles/3925134721/0266554465.jpeg"))
        listOfImage.add(Image("https://nikonrumors.com/wp-content/uploads/2014/03/Nikon-1-V3-sample-photo.jpg"))
        listOfImage.add(Image("https://analyteguru-prod.s3.amazonaws.com/uploads/2016/07/Whitepaper_72048-960x540.png"))
        listOfImage.add(Image("https://www.technobuffalo.com/wp-content/uploads/2016/10/google-pixel-sample-photos-edited-054-470x310@2x.jpg"))
//        listOfImage.add(Image("https://www.baslerweb.com/fp-1485687434/media/editorial/vision_campus/vc_teaser/vision_campus_topics_image-processing-systems_1380x1078px_612x_.jpg"))
//        listOfImage.add(Image("https://cdn.pixabay.com/photo/2015/06/19/17/58/sample-815141_960_720.jpg"))
        for (image in listOfImage) {
            listOfView.add(ChildView(this, image))
        }
        val igGallery = findViewById<InfiniteGallery<ChildView>>(R.id.igGallery)
        igGallery.addChilds(listOfView).display()

    }
}
