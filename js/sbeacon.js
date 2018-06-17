import { AudioInput } from "./audio.js";
import { Scope } from "./scope.js";

export class SBeaconWidget {
	constructor(canvasId) {
		let canvas = document.getElementById(canvasId);
		let scope = new Scope(canvas);
		let audioInput = new AudioInput(scope);
		audioInput.start();
	}
}