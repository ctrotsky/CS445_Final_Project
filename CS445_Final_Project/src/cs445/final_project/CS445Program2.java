/***************************************************************
* file: CS445Program1.java
* author: Colin Trotter
* author: Cristian-Garcia
* author: Rocky Qiu
* author: Mirza Hasan Baig
* class: CS 445 – Computer Graphics
*
* assignment: program 1
* date last modified: 4/10/2017
*
* purpose: This class serves as the Main class for this project. It creates a ShapeDrawer and InputReader, and
* uses them to draw the shapes specified in a file.
*
****************************************************************/ 
package cs445.final_project;

public class CS445Program2 {

    // method: main()
    // purpose: Main method of the project. Creates a ShapwDrawer and InputReader. Reads the shapes to be drawn
    // from a file (coordinates.txt) and draws them to the display.
    public static void main(String[] args) {
        VoxelEngine renderer = VoxelEngine.getInstance();
        renderer.start();
    }
    
}
