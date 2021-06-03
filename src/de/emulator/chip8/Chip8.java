package de.emulator.chip8;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Repräsentation des Chip-8 Systems.
 * Da unsigned in Java nicht möglich ist, werden 8- und 16-bit als short implementiert
 * https://austinmorlan.com/posts/chip8_emulator/
 * @author Max
 *
 */
public class Chip8 {
	
	private static final short START_ADDRESS = 0x200;
	private static final short FONTSET_START_ADDRESS = 0x50;
	private static final int FONTSET_SIZE = 80;
	
	/**
	 * 16 8-bit Register labeled V0-VF. VF ist das 'carry-flag' = result of operations
	 */
	private short[] registers = new short[16];
	
	/**
	 * 4k (4096 bytes) Memory
	 * Addressen:
	 * - 0x000-0x1FF: Chip-8 Emulator selbst. Hier nicht benötigt
	 * - 0x050-0x0A0: 16 built-in characters (0 - F). Muss in Speicher geladen werden, weils ROMS
	 *                danach suchen
	 * - 0x200-0xFFF: Instructions von ROM + Daten
	 */
	private short[] memory = new short[4096];
	
	/**
	 * 16-bit Index-Regist 'I'. Pointer auf Addressen
	 */
	private short index;
	
	/**
	 * 16-bit Program-Counter 'PC'. Pointer auf nächste Instruction
	 */
	private short pc;
	
	/**
	 * 16 level Stack. Stack von 16-bit Addressen für function-calls, return etc.
	 */
	private short[] stack = new short[16];
	
	/**
	 * 8-bit stack-pointer. Pointer auf letzte ausgeführte value auf dem Stack
	 */
	private short sp;
	
	/**
	 * decrement bei 60Hz wenn > 0
	 */
	private short delayTimer;
	
	/**
	 * decrement bei 60Hz wenn > 0 und spiele Sound
	 */
	private short soundTimer;
	
	/**
	 * 16 input keys
	 */
	private short[] keypad = new short[16];
	
	/**
	 * 64x32 Monochrome Display Memory
	 */
	private int[] video = new int[64 * 32];
	
	/**
	 * Pointer auf OPCODE
	 */
	private short opcode;
	
	public short[] fontset = 
		{
			0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
			0x20, 0x60, 0x20, 0x20, 0x70, // 1
			0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
			0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
			0x90, 0x90, 0xF0, 0x10, 0x10, // 4
			0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
			0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
			0xF0, 0x10, 0x20, 0x40, 0x40, // 7
			0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
			0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
			0xF0, 0x90, 0xF0, 0x90, 0x90, // A
			0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
			0xF0, 0x80, 0x80, 0x80, 0xF0, // C
			0xE0, 0x90, 0x90, 0x90, 0xE0, // D
			0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
			0xF0, 0x80, 0xF0, 0x80, 0x80  // F
		};
	
	public Chip8() {
		// PC initialisieren an 0x200 = erste Instruction
		pc = START_ADDRESS;
		
		loadFontsIntoMemory();
	}
	
	public void loadROM(final String filename) {
		byte[] buffer = readBytesFromFile(filename);
		loadROMIntoMemory(buffer);
	}
	
	private byte[] readBytesFromFile(final String filename) {
		File romFile = new File(filename);
		final int romFileSize = (int) romFile.length();
		byte[] buffer = new byte[romFileSize];
		
		if (romFile.canRead()) {
			try {
				buffer = Files.readAllBytes(romFile.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return buffer;
	}
	
	/**
	 * ROM-Inhalt in Memory von Chip-8 laden, beginnend an Addresse 0x200
	 * @param romContent ROM als byte[]
	 */
	private void loadROMIntoMemory(final byte[] romContent) {
		for (int i = 0; i < romContent.length; i++) {
			memory[START_ADDRESS + i] = romContent[i];
		}
	}
	
	/**
	 * Fonts laden, beginnend an Addresse 0x50
	 */
	private void loadFontsIntoMemory() {
		for (int i = 0; i < FONTSET_SIZE; i++)
			memory[FONTSET_START_ADDRESS + i] = fontset[i];
	}

}
