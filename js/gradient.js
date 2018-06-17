// Extended list of CSS colornames s taken from
// http://www.w3.org/TR/css3-color/#svg-color
const cssColors = {
	aliceblue: "F0F8FF",
	antiquewhite: "FAEBD7",
	aqua: "00FFFF",
	aquamarine: "7FFFD4",
	azure: "F0FFFF",
	beige: "F5F5DC",
	bisque: "FFE4C4",
	black: "000000",
	blanchedalmond: "FFEBCD",
	blue: "0000FF",
	blueviolet: "8A2BE2",
	brown: "A52A2A",
	burlywood: "DEB887",
	cadetblue: "5F9EA0",
	chartreuse: "7FFF00",
	chocolate: "D2691E",
	coral: "FF7F50",
	cornflowerblue: "6495ED",
	cornsilk: "FFF8DC",
	crimson: "DC143C",
	cyan: "00FFFF",
	darkblue: "00008B",
	darkcyan: "008B8B",
	darkgoldenrod: "B8860B",
	darkgray: "A9A9A9",
	darkgreen: "006400",
	darkgrey: "A9A9A9",
	darkkhaki: "BDB76B",
	darkmagenta: "8B008B",
	darkolivegreen: "556B2F",
	darkorange: "FF8C00",
	darkorchid: "9932CC",
	darkred: "8B0000",
	darksalmon: "E9967A",
	darkseagreen: "8FBC8F",
	darkslateblue: "483D8B",
	darkslategray: "2F4F4F",
	darkslategrey: "2F4F4F",
	darkturquoise: "00CED1",
	darkviolet: "9400D3",
	deeppink: "FF1493",
	deepskyblue: "00BFFF",
	dimgray: "696969",
	dimgrey: "696969",
	dodgerblue: "1E90FF",
	firebrick: "B22222",
	floralwhite: "FFFAF0",
	forestgreen: "228B22",
	fuchsia: "FF00FF",
	gainsboro: "DCDCDC",
	ghostwhite: "F8F8FF",
	gold: "FFD700",
	goldenrod: "DAA520",
	gray: "808080",
	green: "008000",
	greenyellow: "ADFF2F",
	grey: "808080",
	honeydew: "F0FFF0",
	hotpink: "FF69B4",
	indianred: "CD5C5C",
	indigo: "4B0082",
	ivory: "FFFFF0",
	khaki: "F0E68C",
	lavender: "E6E6FA",
	lavenderblush: "FFF0F5",
	lawngreen: "7CFC00",
	lemonchiffon: "FFFACD",
	lightblue: "ADD8E6",
	lightcoral: "F08080",
	lightcyan: "E0FFFF",
	lightgoldenrodyellow: "FAFAD2",
	lightgray: "D3D3D3",
	lightgreen: "90EE90",
	lightgrey: "D3D3D3",
	lightpink: "FFB6C1",
	lightsalmon: "FFA07A",
	lightseagreen: "20B2AA",
	lightskyblue: "87CEFA",
	lightslategray: "778899",
	lightslategrey: "778899",
	lightsteelblue: "B0C4DE",
	lightyellow: "FFFFE0",
	lime: "00FF00",
	limegreen: "32CD32",
	linen: "FAF0E6",
	magenta: "FF00FF",
	maroon: "800000",
	mediumaquamarine: "66CDAA",
	mediumblue: "0000CD",
	mediumorchid: "BA55D3",
	mediumpurple: "9370DB",
	mediumseagreen: "3CB371",
	mediumslateblue: "7B68EE",
	mediumspringgreen: "00FA9A",
	mediumturquoise: "48D1CC",
	mediumvioletred: "C71585",
	midnightblue: "191970",
	mintcream: "F5FFFA",
	mistyrose: "FFE4E1",
	moccasin: "FFE4B5",
	navajowhite: "FFDEAD",
	navy: "000080",
	oldlace: "FDF5E6",
	olive: "808000",
	olivedrab: "6B8E23",
	orange: "FFA500",
	orangered: "FF4500",
	orchid: "DA70D6",
	palegoldenrod: "EEE8AA",
	palegreen: "98FB98",
	paleturquoise: "AFEEEE",
	palevioletred: "DB7093",
	papayawhip: "FFEFD5",
	peachpuff: "FFDAB9",
	peru: "CD853F",
	pink: "FFC0CB",
	plum: "DDA0DD",
	powderblue: "B0E0E6",
	purple: "800080",
	red: "FF0000",
	rosybrown: "BC8F8F",
	royalblue: "4169E1",
	saddlebrown: "8B4513",
	salmon: "FA8072",
	sandybrown: "F4A460",
	seagreen: "2E8B57",
	seashell: "FFF5EE",
	sienna: "A0522D",
	silver: "C0C0C0",
	skyblue: "87CEEB",
	slateblue: "6A5ACD",
	slategray: "708090",
	slategrey: "708090",
	snow: "FFFAFA",
	springgreen: "00FF7F",
	steelblue: "4682B4",
	tan: "D2B48C",
	teal: "008080",
	thistle: "D8BFD8",
	tomato: "FF6347",
	turquoise: "40E0D0",
	violet: "EE82EE",
	wheat: "F5DEB3",
	white: "FFFFFF",
	whitesmoke: "F5F5F5",
	yellow: "FFFF00",
	yellowgreen: "9ACD32"
};

const colorNames = Object.keys(cssColors).reduce((table, v) => {
	table[v] = v;
	return table;
}, {});


/**
 * 
 */
export class Gradient {

	/**
	 * 
	 * @param {string} startColor beginning of the gradient. One of the
	 *  values in this.color
	 * @param {string} endColor end of the gradient.  One of the values
	 *  in this.color
	 * @param {number|optional} min the lower end of a range
	 * @param {number|optional} max the upper end of a range
	 */
	constructor(startColor, endColor, min, max) {
		this.startColor = cssColors[startColor];
		if (!this.startColor) {
			throw new Error("Start color '" + startColor + "' not supported");
		}
		this.endColor = cssColors[endColor];
		if (!this.endColor) {
			throw new Error("End color '" + endColor + "' not supported");
		}
		this.table = [];
		this.colorTable = [];
		this.min = min | 0;
		this.max = max | 255;
		if (this.max <= this.min) {
			throw new Error("Max is not larger than min");
		}
		this.scale = 256.0 / (this.max - this.min);
		this.createTable();
	}

	/**
	 * The table of acceptable colors
	 */
	static get color() {
		return colorNames;
	}

	/**
	 * 
	 */
	createTable() {
		let c1 = this.startColor;
		let r1 = parseInt(c1.substring(0, 2), 16);
		let g1 = parseInt(c1.substring(2, 4), 16);
		let b1 = parseInt(c1.substring(4, 6), 16);
		let c2 = this.endColor;
		let r2 = parseInt(c2.substring(0, 2), 16);
		let g2 = parseInt(c2.substring(2, 4), 16);
		let b2 = parseInt(c2.substring(4, 6), 16);

		for (let i = 0 ; i < 256 ; i++) {
			let t = i / 256;
			let r = r1 + t * (r2 - r1);
			let g = g1 + t * (g2 - g1);
			let b = b1 + t * (b2 - b1);
			this.table.push({ r: r, g: g, b: b});
		}
		this.makeColors();
	}

	/**
	 * 
	 */
	makeColors() {
		function hex(v) {
			v = Math.floor(v);
			let s = v.toString(16);
			if (s.length < 2) {
				s = "0" + s;
			}
			return s;
		}
		for (let v of this.table) {
			let s = "#" + hex(v.r) + hex(v.g) + hex(v.b);
			this.colorTable.push(s);
		}
	}

	/**
	 * Get a color string for a value 0-255
	 * @param {number} v the number 0-255
	 */
	getColor(v) {
		return this.colorTable[v];
	}

	/**
	 * Get a color in the min..max range
	 * @param {number} v a color in the [min..max] range
	 */
	getFloatColor(v) {
		if (v < this.min) {
			return this.colorTable[0];
		} else if (v > this.max) {
			return this.colorTable[255];
		} else {
			let p = (v - this.min) * this.scale;
			let idx = Math.floor(p);
			return this.colorTable[idx];
		}
	}


}
