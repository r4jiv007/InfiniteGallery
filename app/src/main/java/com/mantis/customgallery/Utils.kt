package com.mantis.customgallery

import android.content.Context
import android.util.DisplayMetrics

class Utils {
	
	companion object {
		fun convertDpToPixel(dp: Float, context: Context): Int {
			return dp.toInt() * (context.getResources().getDisplayMetrics().densityDpi.toInt() / (DisplayMetrics.DENSITY_DEFAULT.toInt()))
		}
		
		/**
		 * This method converts device specific pixels to density independent pixels.
		 *
		 * @param px A value in px (pixels) unit. Which we need to convert into db
		 * @param context Context to get resources and device specific display metrics
		 * @return A float value to represent dp equivalent to px value
		 */
		fun convertPixelsToDp(px: Float, context: Context): Float {
			return px / (context.getResources().getDisplayMetrics().densityDpi as Float / DisplayMetrics.DENSITY_DEFAULT)
		}
	}
	
}
