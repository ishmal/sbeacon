

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

    val N = 512
    
    val maxFreq = 1200.0
    
    val stripCols  = 64
    
    val periods = 4
    val periodSamples = 1024
    val stripRows = periods * periodSamples

    def bins =
        (maxFreq / par.sampleRate * N).toInt
        
    //trace("fs:" + par.sampleRate + "  bins:" + bins)

    val trans = new DFft(N)


    val strip = Array.ofDim[Short](stripRows, stripCols)
    var stripRow = 0
    
    val frame = Array.fill(N)(0.0)


    val SKIP = 10
    var skipctr = 0
    var framePtr = 0
    
    def updateData =
        {
        val ps = trans.powerSpectrum(frame, bins)
        var pslen = ps.size
        //trace("stripCols: " + stripCols + "  pslen: " + pslen)
        val row = strip(stripRow)
        stripRow = (stripRow + 1) % stripRows
        var psptr = 0
        var acc = -pslen
        for (col <- 0 until stripCols)
            {
            val pix = (math.log1p(ps(psptr)) * 20.0).toShort
            //trace("pix: " + pix)
            row(col) = pix
            while (acc < 0)
                {
                acc += stripCols
                psptr += 1
                }
            acc -= pslen
            }
        }

    def update(data:  Array[Double]) =
        {
        //trace("#################")
        for (d <- data)
            {
            frame(framePtr) = d
            framePtr += 1
            if (framePtr >= N)
                {
                framePtr = 0
                updateData
                skipctr += 1
                if (skipctr >= SKIP)
                    {
                    skipctr = 0
                    wf.redraw
                    }
                }
            }
        }
        
    class Pixmap(width: Int, height: Int)
        {
        val nrPix  = width * height
        val pixels = Array.ofDim[Int](nrPix)
        val format = PixelFormat.getIntArgbInstance
        val img    = new WritableImage(width, height)
        val writer = img.getPixelWriter
        
        def poke(x: Int, y: Int, pix: Int) =
           {
           pixels(x + y * width) = pix
           }
           
        def draw(g2d: GraphicsContext, x: Double, y: Double, w: Double, h: Double) =
            {
            writer.setPixels(0, 0, width, height, format, pixels, 0, width)
            g2d.drawImage(img, x, y, w, h)
            }
        
        def peek(x: Int, y: Int) =
           {
           pixels(x + y * width)
           }
        
        }
    
    class Waterfall(width: Double, height: Double) extends Canvas(width, height)
        {
        val iwidth = width.toInt
        val iheight = height.toInt  
        val pixmap = new Pixmap(iwidth, iheight)
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
            override def run = 
                {
                //trace("redraw: " + width + " / " + height)
                pixmap.draw(g2d, 0.0, 0.0, width, height)
                //g2d.setFill(Color.BLUE)
                //g2d.strokeRect(100,100, 20,30)
                }
            }

            
        def redraw =
            {
            /** do our drawing here */
            var rowptr = stripRow
            for (x <- iwidth-1 to 0 by -1)
                {
                val row = strip(rowptr)
                rowptr -= 1
                if (rowptr < 0)
                    rowptr = stripRows-1
                for (col <- 0 until stripCols)
                    {
                    val idx = row(col)
                    //trace("idx: " + idx)
                    pixmap.poke(x, col, colors(idx&255))
                    }
                }
            Platform.runLater(refresher)
            }

        }//waterfall
        

    AnchorPane.setLeftAnchor(this, 0)
    AnchorPane.setTopAnchor(this, 0)
    AnchorPane.setRightAnchor(this, 0)
    AnchorPane.setBottomAnchor(this, 0)

    var wf = new Waterfall(500, 400)
    wf.relocate(0, 0)
    getChildren.addAll(wf)
        
    override def layoutChildren =
        {
        trace("layout")
        wf = new Waterfall(getWidth, getHeight)
        wf.relocate(0,0)
        getChildren.clear
        getChildren.addAll(wf)
        }





}
