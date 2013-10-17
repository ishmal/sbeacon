


package beacon

import javafx.application.{Application, Platform}
import javafx.stage.{Stage,WindowEvent}
import javafx.scene.{Parent, Scene}
import javafx.scene.layout.{AnchorPane}
import javafx.fxml.{FXML,FXMLLoader}
import javafx.event.{Event,EventHandler}
import javafx.collections.{FXCollections,ObservableList}
import javafx.scene.control.{Button,CheckBox,ChoiceBox,TextArea,TextField,ToggleButton}


/**
 * Just add "extends Logged" to any class or trait that needs it
 */ 
trait Logged
{
    private val className = getClass.getName
    
    private def info =
        {
        val funcName = (new Throwable).getStackTrace()(3).getMethodName
        "[" + className + ":" + funcName + "]"
        }

    def error(msg: String) =
        println(info + " error: " + msg)

    def trace(msg: String) =
        println(info + " : " + msg)
}



class PrefsDialog(par: MainController) extends Stage
{

    @FXML var inputDeviceList : ChoiceBox[String] = _
    @FXML var outputDeviceList: ChoiceBox[String] = _
    
    private def seqToFxList(xs: Map[String, AudioDeviceInfo]) =
        {
        val list = FXCollections.observableArrayList[String]()
        for (dev <- xs)
            list.add(dev._1)
        list
        }

    @FXML def initialize =
        {
        inputDeviceList.setItems(seqToFxList(AudioDevice.inputDevices))
        inputDeviceList.setValue(par.config.inputDevice)
        outputDeviceList.setItems(seqToFxList(AudioDevice.outputDevices))
        outputDeviceList.setValue(par.config.outputDevice)
        }
        
    @FXML def doOk(evt: Event) =
        {
        var inp = inputDeviceList.getValue
        if (inp != par.config.inputDevice)
            {
            par.setInputDevice(inp) 
            }
        par.config.inputDevice = inp
        par.config.save
        close
        }

    @FXML def doCancel(evt: Event) =
        {
        close
        }

    try
        {
        val loader = new FXMLLoader(getClass.getResource("/prefs.fxml"))
        loader.setController(this)
        loader.load
        val scene = new Scene(loader.getRoot.asInstanceOf[Parent])
        setTitle("Preferences")
        setScene(scene)
        }
    catch
        {
        case e : Exception =>  e.printStackTrace
        }
}


class App extends Logged
{

    val self = this

    var inputDevice  : Option[AudioInputDevice]  = None
    var outputDevice : Option[AudioOutputDevice] = None

    def setInputDevice(deviceName: String) =
        {
        val newdev = AudioDevice.createInput(this, deviceName)
        if (newdev.isDefined)
            {
            receiver.abort
            inputDevice.foreach(_.close)
            inputDevice = newdev
            inputDevice.get.open
            receiver = new Receiver
            receiver.start
            //adjust
            }
        }
    
    def sampleRate =
        {
        if (inputDevice.isDefined) inputDevice.get.sampleRate else 8000.0
        }
		
    


    //########################################
    //# Settings
    //########################################
    
    class Config 
        {
        private val propFile = "beacon.ini"
    
        var call         = ""
        var name         = ""
        var locator      = ""
        var inputDevice  = ""
        var outputDevice = ""

		def load =
			{
			val props = Properties.loadFile(propFile)
			if (props.isDefined)
				{   
				val p = props.get
				call        = p("call")
				name        = p("name")
				locator     = p("locator")
				inputDevice = p("inputDevice")
				self.inputDevice = AudioDevice.createInput(self, inputDevice)
				self.inputDevice.foreach(_.open)
				}
			}

		def save =
			{
			val p = Map(
				"call" -> call,
				"name" -> name,
				"locator" -> locator,
				"inputDevice" -> inputDevice,
				"outputDevice" -> outputDevice
				)
			if (!Properties.saveFile(p, propFile))
				{
				error("configSave failed")
				}
			}

        }
    
    val config = new Config
    

    class Receiver extends Thread("beacon receiver thread")
        {
        private var keepGoing = true
        
        def abort =
            keepGoing = false
            
        override def run =
            {
            keepGoing = true
            while (keepGoing && inputDevice.isDefined)
                {
				val data = inputDevice.get.receive
				if (data.isDefined)
					{
					update(data.get)
					}
                }
            }
            
        }//receiver
        
    var receiver = new Receiver
        
    def start =
        {
        stop
        receiver = new Receiver
        receiver.start
        }

    def stop =
        {
        receiver.abort
        receiver.join
        }

    //override me
    def update(data: Array[Double]) = {}
    
    config.load
    
    start

}



class MainController(stage: Stage) extends App with Logged
{

    @FXML var fftBox : AnchorPane = _
    val fft = new Fft(this)

    val prefsDialog = new PrefsDialog(this)

    val aboutDialog = new Stage
        {
        val root = FXMLLoader.load(getClass.getResource("/about.fxml")).asInstanceOf[Parent]
        setTitle("About ScalaDigi")
        setScene(new Scene(root))
        }
        
    stage.setOnCloseRequest(new EventHandler[WindowEvent]
        {
        override def handle(evt: WindowEvent) 
            {
            doClose(evt)
            }
        })
        
    def doClose(evt : Event) = { stop ; Platform.exit }
    def doClear(evt : Event) = {  }
    def doAbout(evt : Event) = aboutDialog.show
    def doPrefs(evt : Event) = prefsDialog.show


    @FXML def initialize =
        {
        fftBox.getChildren.add(fft)
        }

    //overriden from App
    override def update(data: Array[Double]) =
        {
        if (fft != null)
            fft.update(data)
        }

}



class MainGui extends Application with Logged
{
    
    override def start(stage: Stage) =
        {
        try
            {
            val controller = new MainController(stage)
            val loader = new FXMLLoader(getClass.getResource("/main.fxml"))
            loader.setController(controller)
            val page = loader.load.asInstanceOf[Parent]
            val scene = new Scene(page)
            stage.setTitle("SBeacon")
            stage.setScene(scene)
            stage.show
            }
        catch
            {
            case e: java.io.IOException => error("error:" + e)
            }
        }     
}


object MainGui
{
    def main(argv: Array[String]) : Unit =
        {
        javafx.application.Application.launch(classOf[MainGui], argv:_*)
        }
}

