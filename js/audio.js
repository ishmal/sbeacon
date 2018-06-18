

export class AudioInput {

	constructor() {

		let audioCtx = new (window.AudioContext || window.webkitAudioContext)();
		this.audioCtx = audioCtx;
		this.sampleRate = this.audioCtx.sampleRate;

		let filter = audioCtx.createBiquadFilter();
		filter.frequency.value = 1000;
		filter.type = "bandpass";
		filter.Q.value = 100;
		filter.gain.value = 15;

		let analyzer = audioCtx.createAnalyser();
		analyzer.fftSize = 2048;
		analyzer.smoothingTimeConstant = 0.0;
		let bufferLength = analyzer.frequencyBinCount;
		this.dataArray = new Uint8Array(bufferLength);
		this.analyzer = analyzer;
		
		filter.connect(analyzer);
		analyzer.connect(audioCtx.destination);
		
		this.chain = filter;
}

	start() {
		if (navigator.mediaDevices) {
			navigator.mediaDevices.getUserMedia({audio: true, video: false})
			.then((stream) => {
				let source = this.audioCtx.createMediaStreamSource(stream);
				this.source = source;
		
				source.connect(this.chain);
			});
		} else {
			throw new Error("getUserMedia not supported on your device");
		}

	}

	getSpectrumData() {
		this.analyzer.getByteFrequencyData(this.dataArray);
		return this.dataArray;
	}

	stop() {
		if (this.source) {
			this.source.disconnect();
			this.source = null;
		}
	}
}