import { AudioInput } from "./audio.js";
import { Scope } from "./scope.js";

export class SBeaconWidget{
	constructor(id) {
		let audioInput = new AudioInput();
		let scope = new Scope(id, audioInput);
		audioInput.start();
	}
}