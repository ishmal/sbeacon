

package beacon


import javafx.application.Platform
import javafx.beans.value.{ChangeListener,ObservableValue}
import javafx.scene.layout.{AnchorPane,HBox,VBox,Pane}
import javafx.scene.canvas.{Canvas, GraphicsContext}
import javafx.scene.image.{ImageView,WritableImage,PixelFormat}
import javafx.scene.shape.{Rectangle}
import javafx.scene.paint.Color
import javafx.event.{Event, EventHandler}
import javafx.scene.input.{KeyEvent,MouseEvent,ScrollEvent}


import edu.emory.mathcs.jtransforms.dht.DoubleDHT_1D
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D

class Fht(N: Int)
{
    private val trans = new DoubleDHT_1D(N)

    def forward(arr: Array[Double]) = trans.forward(arr)
    def inverse(arr: Array[Double]) = trans.inverse(arr, false)
}

class DFft(N: Int)
{
    private val trans = new DoubleFFT_1D(N)

    def forward(arr: Array[Double]) = trans.realForward(arr)
    def inverse(arr: Array[Double]) = trans.realInverse(arr, false)
    
    def powerSpectrum(in: Array[Double], bins: Int) : Array[Double] =
        {
        forward(in)
        var idx = 0
        val out = Array.fill(bins)
            {
            val r = in(idx)
            val i = in(idx+1)
            idx += 2
            r * r + i * i
            }
        out
        }
        
    def powerSpectrum(in: Array[Double]) : Array[Double] =
        {
        powerSpectrum(in, in.size / 2)
        }
}



class Fft(par: App) extends Pane with Logged
{

    val N = 2048
    
    val maxFreq = 2500.0
    
    val width  = 64
    
    val periods = 4
    val periodSamples = 1024
    val length = periods * periodSamples

    def bins =
        (maxFreq / par.sampleRate * N).toInt

    val trans = new DFft(N)


    val strip = Array.ofDim[Short](width, length)
    var stripptr = 0
    
    val frame = Array.fill(N)(0.0)


    var skip = 2
    var framePtr = 0
    
    
    def updateData =
        {
        val ps = trans.powerSpectrum(frame, bins)
        var pslen = ps.size
        val row = strip(stripptr)
        var rowptr = 0
        stripptr = (stripptr + 1) % length
        var acc = -pslen
        for (p <- ps)
            {
            acc += width
            if (acc >= 0)
                {
                acc -= pslen
                row(rowptr) = (math.log1p(p) * 10.0).toShort
                wf.redraw
                }
            }
        }

    def update(data:  Array[Double]) =
        {
        for (d <- data)
            {
            frame(framePtr) = d
            framePtr += 1
            if (framePtr >= N)
                {
                framePtr = 0
                }
            }
        }
        
    class Waterfall(width: Double, height: Double) extends Canvas(width, height)
        {
        val iwidth = width.toInt
        val iheight = height.toInt
    
        val img = new WritableImage(iwidth, iheight)
        val nrPix = iwidth * iheight
        val pixels = Array.ofDim[Int](nrPix)
        val lastRow = nrPix - iwidth
        val writer = img.getPixelWriter
        val format = PixelFormat.getIntArgbInstance
        val g2d = getGraphicsContext2D
                
        /**
         * Make a palette. tweak this often
         */                 
        private val colors = Array.tabulate(256)( i=>
            {
            val r = if (i < 170) 0 else (i-170) * 3
            val g = if (i <  85) 0 else if (i < 170) (i-85) * 3 else 255
            val b = if (i <  85) i * 3 else 255
            var col = 0xff
            col = (col << 8) + r;
            col = (col << 8) + g;
            col = (col << 8) + b;
            col
            })
            
        val refresher = new Runnable
            {
            override def run = g2d.drawImage(img, 0.0, 0.0, width, height)
            }

            
        def redraw =
            {
            Platform.runLater(refresher)
            }

        }//waterfall
        
    var wf = new Waterfall(500, 400)
    wf.relocate(0, 0)
    getChildren.addAll(wf)
        
    override def layoutChildren =
        {
        val width = getWidth
        val height = getHeight
        wf = new Waterfall(getWidth, getHeight)
        wf.relocate(0,0)
        getChildren.clear
        getChildren.addAll(wf)
        }


    AnchorPane.setLeftAnchor(this, 0)
    AnchorPane.setTopAnchor(this, 0)
    AnchorPane.setRightAnchor(this, 0)
    AnchorPane.setBottomAnchor(this, 0)



}
