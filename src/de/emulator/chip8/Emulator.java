package de.emulator.chip8;

public class Emulator {

	public static void main(String[] args) {
		Chip8 chip8 = new Chip8();
		chip8.loadROM("roms/lunarlander.ch8");
	}

}
