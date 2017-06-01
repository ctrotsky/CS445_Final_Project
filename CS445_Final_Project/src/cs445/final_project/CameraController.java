/***************************************************************
* file: Cube.java
* author: Colin Trotter
* class: CS 445 – Computer Graphics
*
* assignment: final program
* date last modified: 5/4/2017
*
* purpose: This class controls the movement of the 'camera' within the 3D scene. It can 
* change its pitch and yaw based on mouse movement, and it's position with the WASD keys.
*
****************************************************************/ 

package cs445.final_project;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import static org.lwjgl.opengl.GL11.*;

public class CameraController {
    private Vector3f position;
    private Vector3f lPosition;
    private float yaw = 0.0f;
    private float pitch = 0.0f;
    
    // method: CameraController
    // purpose: constructor that sets the Camera's position to the given x, y, and z values.
    public CameraController(float x, float y, float z)
    {
        //instantiate position Vector3f to the x y z params.
        position = new Vector3f(x, y, z);
        lPosition = new Vector3f(x,y,z);
        lPosition.x = 30f;
        lPosition.y = 30f;
        lPosition.z = 30f;
    }
    
    // method: yaw
    // purpose: increment the camera's current yaw rotation
    public void yaw(float amount)
    {
        yaw += amount;
    }
    // method: pitch
    // purpose: increment the camera's current pitch rotation
    public void pitch(float amount)
    {
        pitch -= amount;
    }
    
    // method: walkForward
    // purpose: moves the camera forward relative to its current rotation (yaw) by an amount given by distance
    public void walkForward(float distance)
    {
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x -= xOffset;
        position.z += zOffset;
    }
    
    // method: walkBackwards
    // purpose: moves the camera backward relative to its current rotation (yaw) by an amount given by distance
    public void walkBackwards(float distance)
    {
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x += xOffset;
        position.z -= zOffset;
    }
    
    // method: strafeLeft
    // purpose: strafes the camera left relative to its current rotation (yaw) by an amount given by distance
    public void strafeLeft(float distance)
    {
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw-90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw-90));
        position.x -= xOffset;
        position.z += zOffset;
    }
    
    // method: strafeLeft
    // purpose: strafes the camera right relative to its current rotation (yaw) by an amount given by distance
    public void strafeRight(float distance)
    {
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw+90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw+90));
        position.x -= xOffset;
        position.z += zOffset;
    }
    
    // method: moveUp
    // purpose: moves the camera up
    public void moveUp(float distance)
    {
        position.y -= distance;
    }
    // method: moveDown
    // purpose: moves the camera down
    public void moveDown(float distance)
    {
        position.y += distance;
    }
    
    // method: lookThrough
    // purpose: translates and rotate the matrix so that it looks through the camera
    // (this does basically what gluLookAt() does)
    public void lookThrough()
    {
        //rotate the pitch around the X axis
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        //rotate the yaw around the Y axis
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        //translate to the position vector's location
        glTranslatef(position.x, position.y, position.z);
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x).put(lPosition.y).put(lPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

}
