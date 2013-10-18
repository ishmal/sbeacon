/**
 * Scala SDR tool
 *
 * Authors:
 *   Bob Jamison
 *
 * Copyright (C) 2012 Bob Jamison
 * 
 *  This file is part of the Scala SDR library.
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package beacon


import javax.sound.sampled.{AudioFormat, AudioSystem, DataLine, 
    Line, Mixer, Port, SourceDataLine, TargetDataLine}





    
    

/**
 * Implementation of an AudioInputDevice on the JVM
 */
class AudioInputDevice(par: App, adi: AudioDeviceInfo)
{
    private val shortToDouble  = 1.0 / 32768.0

    val bytesToDouble =
        {
        val arr = Array.ofDim[Double](256, 256)
        for (hi <- 0 until 256)
            {
            for (lo <- 0 until 256)
                {
                val v = ((hi << 8) + lo).toDouble * shortToDouble
                arr(hi)(lo) = v
                }
            }
        arr
        }

    private val line            = AudioSystem.getTargetDataLine(adi.format, adi.mixerInfo)
    private val frameSize       = adi.format.getFrameSize
    private val framesPerBuffer = line.getBufferSize / 8


    def sampleRate =
        {
        adi.format.getSampleRate.toDouble
        }
        
    private def bufferSize =
        { 
        //framesPerBuffer * frameSize
        4096 * frameSize
        }
    //trace("frameSize: " + frameSize + "  bufferSize: " + bufferSize)
    
    def open : Boolean =
        {
        line.open(adi.format, bufferSize)
        line.start
        true
        }
        
    def close : Boolean =
        {
        line.close
        true
        }
        
            
    private val data = Array.ofDim[Byte](bufferSize)

    def receive : Option[Array[Double]] =
        {
        val numBytes = line.read(data, 0, data.length)
        if (numBytes < 0)
            None
        else
            {
            var idx = 0
            val siz = numBytes >> 1
            val raw = Array.fill(siz)
                {
                val cpx = bytesToDouble(data(idx) & 0xff)(data(idx+1) & 0xff)
                idx += 2
                cpx
                }
            Some(raw)
            }
        }
}


/**
 * Implementation of an AudioOutputDevice on the JVM
 */
class AudioOutputDevice(par: App, adis: AudioDeviceInfo)
{
    def sampleRate =
        0.0
        
}




/**
 * Data class describing available audio devices
 */
case class AudioDeviceInfo(format: AudioFormat, mixerInfo: Mixer.Info)



/**
 * Utility for listing and creating audio devices
 */
object AudioDevice extends Logged
{

    val sampleRate = 8000.0f
    
    /**
     * List conforming audio input devices
     */
    val inputDevices : Map[String, AudioDeviceInfo] =
        {
        val audioFormat = new AudioFormat(sampleRate, 16, 1, true, true)
        val info = new DataLine.Info(classOf[TargetDataLine], audioFormat)
        val buf = scala.collection.mutable.Map[String, AudioDeviceInfo]()
        for (mixerInfo <- AudioSystem.getMixerInfo)
            {
            val m = AudioSystem.getMixer(mixerInfo)
            if (m.isLineSupported(info))
                {
                buf +=  mixerInfo.getName -> AudioDeviceInfo(audioFormat, mixerInfo)
                }
             }
        buf.toMap   
        }

    /**
     * List conforming audio output devices
     */
    val outputDevices : Map[String, AudioDeviceInfo] =
        {
        val audioFormat = new AudioFormat(sampleRate, 16, 1, true, true)
        val info = new DataLine.Info(classOf[SourceDataLine], audioFormat)
        val buf = scala.collection.mutable.Map[String, AudioDeviceInfo]()
        for (mixerInfo <- AudioSystem.getMixerInfo)
            {
            val m = AudioSystem.getMixer(mixerInfo)
            if (m.isLineSupported(info))
                {
                buf +=  mixerInfo.getName -> AudioDeviceInfo(audioFormat, mixerInfo)
                }
             }
        buf.toMap   
        }
        


     
    /**
     * Create an audio input device by name.  If device is not in the list,
     * return an error
     */
    def createInput(par: App, name: String) : Option[AudioInputDevice] =
        {
        val dev = inputDevices.get(name)
        if (dev.isDefined)
            {
            Some(new AudioInputDevice(par, dev.get))
            }
        else
            {
            error("Input audio device not found: " + name)
            None
            }
        }
        
}





