package com.wallpaintapp.paintwall

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import java.util.*

class QueueLinearFloodFiller {

    var image: Bitmap? = null
        protected set
    var tolerance = intArrayOf(0, 0, 0)
    protected var width = 0
    protected var height = 0
    protected var pixels: IntArray? = null
    var fillColor = 0
    protected var startColor = intArrayOf(0, 0, 0)
    protected lateinit var pixelsChecked: BooleanArray
    protected lateinit var ranges: Queue<FloodFillRange>

    // Construct using an image and a copy will be made to fill into,
    // Construct with BufferedImage and flood fill will write directly to
    // provided BufferedImage
    constructor(img: Bitmap) {
        copyImage(img)
    }

    constructor(img: Bitmap, targetColor: Int, newColor: Int) {
        useImage(img)

        fillColor = newColor
        setTargetColor(targetColor)
    }

    fun setTargetColor(targetColor: Int) {
        startColor[0] = Color.red(targetColor)
        startColor[1] = Color.green(targetColor)
        startColor[2] = Color.blue(targetColor)
    }

    fun setTolerance(value: Int) {
        tolerance = intArrayOf(value, value, value)
    }

    fun copyImage(img: Bitmap) {
        // Copy data from provided Image to a BufferedImage to write flood fill
        // to, use getImage to retrieve
        // cache data in member variables to decrease overhead of property calls
        width = img.width
        height = img.height

        image = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val canvas = Canvas(image!!)
        canvas.drawBitmap(img, 0.toFloat(), 0.toFloat(), null)

        pixels = IntArray(width * height)

        image!!.getPixels(pixels, 0, width, 1, 1, width - 1, height - 1)
    }

    fun useImage(img: Bitmap) {
        // Use a pre-existing provided BufferedImage and write directly to it
        // cache data in member variables to decrease overhead of property calls
        width = img.width
        height = img.height
        image = img

        pixels = IntArray(width * height)

        image!!.getPixels(pixels, 0, width, 1, 1, width - 1, height - 1)
    }

    protected fun prepare() {
        // Called before starting flood-fill
        pixelsChecked = BooleanArray(pixels!!.size)
        ranges = LinkedList<FloodFillRange>()
    }

    // Fills the specified point on the bitmap with the currently selected fill
    // color.
    // int x, int y: The starting coords for the fill
    fun floodFill(x: Int, y: Int) {
        // Setup
        prepare()

        if (startColor[0] == 0) {
            // ***Get starting color.
            val startPixel = pixels!![width * y + x]
            startColor[0] = startPixel shr 16 and 0xff
            startColor[1] = startPixel shr 8 and 0xff
            startColor[2] = startPixel and 0xff
        }

        // ***Do first call to floodfill.
        LinearFill(x, y)

        // ***Call floodfill routine while floodfill ranges still exist on the
        // queue
        var range: FloodFillRange

        while (ranges.size > 0) {
            // **Get Next Range Off the Queue
            range = ranges.remove()

            // **Check Above and Below Each Pixel in the Floodfill Range
            var downPxIdx = width * (range.Y + 1) + range.startX
            var upPxIdx = width * (range.Y - 1) + range.startX
            val upY = range.Y - 1// so we can pass the y coord by ref
            val downY = range.Y + 1

            for (i in range.startX..range.endX) {
                // *Start Fill Upwards
                // if we're not above the top of the bitmap and the pixel above
                // this one is within the color tolerance
                if (range.Y > 0 && !pixelsChecked[upPxIdx]
                    && CheckPixel(upPxIdx)
                )
                    LinearFill(i, upY)

                // *Start Fill Downwards
                // if we're not below the bottom of the bitmap and the pixel
                // below this one is within the color tolerance
                if (range.Y < height - 1 && !pixelsChecked[downPxIdx]
                    && CheckPixel(downPxIdx)
                )
                    LinearFill(i, downY)

                downPxIdx++
                upPxIdx++
            }
        }

        image!!.setPixels(pixels, 0, width, 1, 1, width - 1, height - 1)
    }

    // Finds the furthermost left and right boundaries of the fill area
    // on a given y coordinate, starting from a given x coordinate, filling as
    // it goes.
    // Adds the resulting horizontal range to the queue of floodfill ranges,
    // to be processed in the main loop.

    // int x, int y: The starting coords
    protected fun LinearFill(x: Int, y: Int) {
        // ***Find Left Edge of Color Area
        var lFillLoc = x // the location to check/fill on the left
        var pxIdx = width * y + x

        while (true) {
            // **fill with the color
            pixels?.set(pxIdx, fillColor)

            // **indicate that this pixel has already been checked and filled
            pixelsChecked[pxIdx] = true

            // **de-increment
            lFillLoc-- // de-increment counter
            pxIdx-- // de-increment pixel index

            // **exit loop if we're at edge of bitmap or color area
            if (lFillLoc < 0 || pixelsChecked[pxIdx] || !CheckPixel(pxIdx)) {
                break
            }
        }

        lFillLoc++

        // ***Find Right Edge of Color Area
        var rFillLoc = x // the location to check/fill on the left

        pxIdx = width * y + x

        while (true) {
            // **fill with the color
            pixels?.set(pxIdx, fillColor)

            // **indicate that this pixel has already been checked and filled
            pixelsChecked[pxIdx] = true

            // **increment
            rFillLoc++ // increment counter
            pxIdx++ // increment pixel index

            // **exit loop if we're at edge of bitmap or color area
            if (rFillLoc >= width || pixelsChecked[pxIdx] || !CheckPixel(pxIdx)) {
                break
            }
        }

        rFillLoc--

        // add range to queue
        val r = FloodFillRange(lFillLoc, rFillLoc, y)

        ranges.offer(r)
    }

    // Sees if a pixel is within the color tolerance range.
    protected fun CheckPixel(px: Int): Boolean {
        val red = pixels!![px].ushr(16) and 0xff
        val green = pixels!![px].ushr(8) and 0xff
        val blue = pixels!![px] and 0xff

        return (red >= startColor[0] - tolerance[0]
                && red <= startColor[0] + tolerance[0]
                && green >= startColor[1] - tolerance[1]
                && green <= startColor[1] + tolerance[1]
                && blue >= startColor[2] - tolerance[2] && blue <= startColor[2] + tolerance[2])
    }

    // Represents a linear range to be filled and branched from.
    protected inner class FloodFillRange(var startX: Int, var endX: Int, var Y: Int)
}