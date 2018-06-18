import { AudioInput } from "./audio.js";
import { Scope } from "./scope.js";

export class SBeaconWidget{
	constructor(id) {
		let scope = new Scope(id);
		let audioInput = new AudioInput(scope);
		scope.audioInput = audioInput;
		audioInput.start();
	}
}