# java_gol
Game of life implemented in java. Incl. report and some documentation
A personal pet project of mine, originally for my computer science class.
The project itself currently supports several things
 - Windowed Fullscreen
 - FPS Limiting
 - Custom Rules
 - Static Brushes
 - Cell erasure
 - Random Fill
 - Frame Stepping
 
 Although the new algorithm supports custom algorithms, it is much slower than a static algorithm.
 There will be a seperate source folder for the fast game of life code.
 Algorithms are in the format
#survive/#birth
where survive is the number of neighbors needed to survive, and birth is the number needed to give life to a dead cell
