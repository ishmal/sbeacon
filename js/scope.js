import { Gradient } from "./gradient.js"

const RIBBON_WIDTH = 64;
const RIBBON_LENGTH = 2048;

/**
 * 
 */
export class Scope {

	/**
	 * 
	 */
	constructor(canvasId) {
		let canvas = document.getElementById(canvasId);
		this.canvas = canvas;

		this.ctx = canvas.getContext('2d');
		this.canvas = canvas;
		this.ribbon = [];
		for (let i = 0 ; i < RIBBON_LENGTH ; i++) {
			let row = new Array(RIBBON_WIDTH);
			row.fill(0);
			this.ribbon.push(row);
		}
		this.ribbonIndex = 0;
		this.skipCounter = 0;
		this.gradient = new Gradient(
			Gradient.color.midnightblue,
			Gradient.color.white
		);
		this.updateRate = 44100/2048;  //first guess
		this.skipCount = 0;
	}

	setUpdateRate(val) {
		this.updateRate = val; //actual
	}

	redraw() {
		let ctx = this.ctx;
		let gradient = this.gradient;
		let width = this.canvas.width;
		let height = this.canvas.height;

		ctx.fillStyle = "#000000";
		ctx.fillRect(0, 0, width, height);

		let ribbon = this.ribbon;
		let top = RIBBON_WIDTH;
		top += RIBBON_WIDTH;

		let leftIndex = this.ribbonIndex;

		for (let stripe = 0 ; stripe < 4 ; stripe++) {
			leftIndex -= width;
			if (leftIndex < 0) {
				leftIndex += RIBBON_LENGTH;
			}
			let idx = leftIndex;
			for (let x = 0 ; x < width ; x++) {
				let col = ribbon[idx++];
				if (idx >= RIBBON_LENGTH) {
					idx = 0;
				}
				for (let row = 0 ; row < RIBBON_WIDTH ; row++) {
					let y = top - row;
					let v = Math.abs(col[row]);
					ctx.fillStyle = gradient.getFloatColor(v);
					ctx.fillRect(x, y, 1, 1);
				}
			}
			top += RIBBON_WIDTH;	
		}
		let pos = RIBBON_WIDTH;
		ctx.strokeStyle = "red";
		ctx.beginPath();
		ctx.moveTo(0, pos);
		ctx.lineTo(width, pos);
		ctx.stroke();
		ctx.strokeStyle = "lightgreen";
		for (let i = 0 ; i < 3 ; i++) {
			pos += RIBBON_WIDTH;
			ctx.beginPath();
			ctx.moveTo(0, pos);
			ctx.lineTo(width, pos);
			ctx.stroke();	
		}


	}

	update(data) {
		if (this.skipCount < 10) {
			this.skipCount++;
			return;
		}
		this.skipCount = 0;
		let row = this.ribbon[this.ribbonIndex];
		for (let i = 0 ; i < RIBBON_WIDTH ; i++) {
			row[i] = data[i];
		}
		this.ribbonIndex = (this.ribbonIndex + 1) % RIBBON_LENGTH;
		requestAnimationFrame(() => {
			this.redraw();
		});
	}
}