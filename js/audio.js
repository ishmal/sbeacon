

export class AudioInput {

	constructor() {
		this.audioCtx = new (window.AudioContext || window.webkitAudioContext)();
	}

	start() {
		if (navigator.mediaDevices) {
			navigator.mediaDevices.getUserMedia ({audio: true, video: false})
			.then((stream) => {
				let audioCtx = this.audioCtx;
				let source = audioCtx.createMediaStreamSource(stream);

				let filter = audioCtx.createBiquadFilter();
				filter.frequency.value = 700;
				filter.type = "bandpass";
				filter.Q.value = 100;
				filter.gain.value = 25;
		
				let analyzer = audioCtx.createAnalyser();
				analyzer.fftSize = 1024;
				let bufferLength = analyzer.frequencyBinCount;
				this.dataArray = new Float32Array(bufferLength);	

				let finish = audioCtx.destination;
		
				source.connect(filter);
				filter.connect(analyzer);
				//analyzer.connect(finish);

				this.source = source;
				this.analyzer = analyzer;
			});
		} else {
			throw new Error("getUserMedia not supported on your device");
		}

	}

	getSpectrumData() {
		this.analyzer.getFloatFrequencyData(this.dataArray);
		return this.dataArray;
	}

	stop() {
		if (this.source) {
			this.source.disconnect();
			this.source = null;
		}
	}
}