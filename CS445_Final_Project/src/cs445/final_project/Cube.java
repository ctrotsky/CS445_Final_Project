/***************************************************************
* file: Cube.java
* author: Colin Trotter
* class: CS 445 – Computer Graphics
*
* assignment: final program
* date last modified: 5/4/2017
*
* purpose: This class represents a cube in 3D space. It has a position.
*
****************************************************************/ 
package cs445.final_project;

public class Cube {
    private Vector3f position;
    
    // method: Cube
    // purpose: constructor that instantiates this Cube with a position of the given x, y, and z values.
    public Cube(float x, float y, float z){
        position = new Vector3f(x,y,z);
    }

    // method: getPosition
    // purpose: returns the position of this Cube in the 3D display
    public Vector3f getPosition() {
        return position;
    }

    // method: getPosition
    // purpose: sets the position of this Cube in the 3D display
    public void setPosition(Vector3f position) {
        this.position = position;
    }
}
