package org.black_matter.monospace.input;

public abstract class KeyAction {
	
	public boolean down = false;
	
	public abstract void onPress();
	public void onUp() { }
	public void onDown() { }
	public void onRelease() { }
}
