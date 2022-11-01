package com.devhch.asteroidradar.utils

import android.icu.number.NumberFormatter.with
import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.devhch.asteroidradar.R
import com.devhch.asteroidradar.models.PictureOfDay
import com.squareup.picasso.Picasso


@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}

@BindingAdapter("pictureOfDayImage")
fun bindPictureOfDay(imageView: ImageView, pictureOfDay: PictureOfDay?) {
    val context = imageView.context
    if (pictureOfDay != null && pictureOfDay.url.isNotBlank()) {
        Picasso.get()
            .load(pictureOfDay.url)
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.no_image)
            .fit()
            .centerCrop()
            .into(imageView)

        // .placeholder(R.drawable.placeholder_picture_of_day)


        val contentDescription =
            String.format(
                context.getString(R.string.nasa_picture_of_day_content_description_format),
                pictureOfDay.title
            )
        imageView.contentDescription = contentDescription
    } else {
        imageView.setImageResource(R.drawable.placeholder_picture_of_day)
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        imageView.contentDescription =
            context.getString(R.string.this_is_nasa_s_picture_of_day_showing_nothing_yet)
    }
}