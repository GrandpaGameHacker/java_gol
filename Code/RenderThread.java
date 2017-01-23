import java.time.Instant;

/* A class to render the canvas from a seperate thread
 * for animation purpose.
 * It has support for specifying fps, 60 fps is default
 * Each frame is calculated and rendered together.  
 * This class can only be used with the original class 
 * it was written for, unless this code is adapted first.
 */

class RenderThread extends Thread {
	
	/* This main object exposes 
	 * the step and render functions
	 * to rendering thread
	 */
	private GameOfLife main;
	double fps;
	boolean isPlaying = false;
	
	public RenderThread(GameOfLife m) {
		fps = 60;
		main = m;
	}
	
	public RenderThread(GameOfLife m, int framesPerSecond) {
		fps = framesPerSecond;
		main = m;
	}


	@Override
	public void run() {
		isPlaying = true;
		// Calculate miliseconds per frame.
		long timeTilNextFrame = (long)(1000/fps);
		// Get current time
		Instant lastTick = Instant.now();
		while (isPlaying) {
			// FPS Control
			// if lastTick+timeTilNextFrame < now...do nextFrame
			// Not backwards compatible JRE < 1.8.0
			if (lastTick.plusMillis(timeTilNextFrame).isBefore(Instant.now())) {
				lastTick = Instant.now();
				main.step();
				main.repaint();
			}
		}
	}
}
