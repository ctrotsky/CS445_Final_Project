/***************************************************************
* file: PolyDrawer.java
* author: Colin Trotter
* class: CS 445 – Computer Graphics
*
* assignment: final program
* date last modified: 5/4/2017
*
* purpose: This class renders cubes in a voxel engine.
*
****************************************************************/ 
package cs445.final_project;

import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

public class VoxelEngine {
    ArrayList<Cube> cubes;
    private static VoxelEngine INSTANCE;
    private final int windowWidth = 640;
    private final int windowHeight = 480;
    private final int cubeSize = 2;
    private final float mouseSensitivity = 0.09f;
    private final float movementSpeed = .35f;
    private DisplayMode displayMode;
    private CameraController camera;
    
    // method: getInstance()
    // purpose: Static method that returns a singleton instance of the class
    public static VoxelEngine getInstance(){
        if (INSTANCE == null){
            INSTANCE = new VoxelEngine();
        }
        
        return INSTANCE;
    }
    
    // method: VoxelEngine()
    // purpose: private constructor. Used to make a singleton instance of the class.
    private VoxelEngine(){
        cubes = new ArrayList<>();
        camera = new CameraController(2, -2, -4);
        
        Cube testCube = new Cube(0,0,0);
        cubes.add(testCube);
    }
    
    // method: start
    // purpose: this method creates the display window, and begins rendering to the display.
    public void start() {
        try {
            createWindow();
            initGL();
            gameLoop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // method: createWindow
    // purpose: this method creates the display window with size 640x480
    private void createWindow() throws Exception{
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++) {
                if (d[i].getWidth() == windowWidth && d[i].getHeight() == windowHeight && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle("CS 445 Voxel Engine");
        Display.create();
    }
    
    
    
    // method: initGL
    // purpose: initializes the display with a camera and background color.
    private void initGL() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        GLU.gluPerspective(100.0f, (float)displayMode.getWidth()/(float)displayMode.getHeight(), 0.1f, 300.0f);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);  
        glTranslatef(0f,1000f,-10f);
    }
    
    // method: render
    // purpose: clears the display, then updates it, drawing all shapes to it.
    // Closes the display when ESC is pressed, or when close is requested by hitting the close button.
    private void render() {
        try{
            glEnable(GL_DEPTH_TEST);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            //glLoadIdentity();

            glBegin(GL_QUADS);   
            for (Cube cube : cubes){
                
                drawCube(cube);               
            }
            glEnd();

            Display.update();
            Display.sync(60);
        }catch(Exception e){

        }
    }
    
    // method: drawCube
    // purpose: draws a single cube in the 3D display. It will be located at it's position.
    private void drawCube(Cube cube){
        Vector3f pos = cube.getPosition();
        float x = pos.x;
        float y = pos.y;
        float z = pos.z;
        int offset = cubeSize/2;
        
        //Top
        glColor3f(0f,0f,1f);
        glVertex3f(x + offset, y + offset, z - offset); 
        glVertex3f(x - offset, y + offset, z - offset);
        glVertex3f(x - offset, y + offset, z + offset);
        glVertex3f(x + offset, y + offset, z + offset);
        //Bottom
        glColor3f(1f,0f,1f);
        glVertex3f(x + offset, y - offset, z + offset); 
        glVertex3f(x - offset, y - offset, z + offset);
        glVertex3f(x - offset, y - offset, z - offset);
        glVertex3f(x + offset, y - offset, z - offset);
        //Front
        glColor3f(0f,1f,0f);
        glVertex3f(x + offset, y + offset, z + offset); 
        glVertex3f(x - offset, y + offset, z + offset);
        glVertex3f(x - offset, y - offset, z + offset);
        glVertex3f(x + offset, y - offset, z + offset);
        //Back
        glColor3f(0f,1f,1f);
        glVertex3f(x + offset, y - offset, z - offset); 
        glVertex3f(x - offset, y - offset, z - offset);
        glVertex3f(x - offset, y + offset, z - offset);
        glVertex3f(x + offset, y + offset, z - offset);
        //Left
        glColor3f(1f,1f,0f);
        glVertex3f(x - offset, y + offset, z + offset); 
        glVertex3f(x - offset, y + offset, z - offset);
        glVertex3f(x - offset, y - offset, z - offset);
        glVertex3f(x - offset, y - offset, z + offset);
        //Right
        glColor3f(1f,0f,0f);
        glVertex3f(x + offset, y - offset, z + offset); 
        glVertex3f(x + offset, y - offset, z - offset);
        glVertex3f(x + offset, y + offset, z - offset);
        glVertex3f(x + offset, y + offset, z + offset);
    }
    
    public void gameLoop()
    {
        //hide the mouse
        Mouse.setGrabbed(true);
        // keep looping till the display window is closed the ESC key is down
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
        {
            getInput();

            //set the modelview matrix back to the identity
            glLoadIdentity();
            //look through the camera before you draw anything
            camera.lookThrough();

            //you would draw your scene here.
            render();
        }
        Display.destroy();
    }
    
    private void getInput(){
        float dx = 0.0f;
        float dy = 0.0f;
        //distance in mouse movement
        //from the last getDX() call.
        dx = Mouse.getDX();
        //distance in mouse movement
        //from the last getDY() call.
        dy = Mouse.getDY();

        //control camera yaw from x movement fromt the mouse
        camera.yaw(dx * mouseSensitivity);
        //control camera pitch from y movement fromt the mouse
        camera.pitch(dy * mouseSensitivity);

        if (Keyboard.isKeyDown(Keyboard.KEY_W))//move forward
        {
            camera.walkForward(movementSpeed);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_S))//move backwards
        {
            camera.walkBackwards(movementSpeed);
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            camera.strafeLeft(movementSpeed);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)){
            camera.strafeRight(movementSpeed);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
            camera.moveUp(movementSpeed);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            camera.moveDown(movementSpeed);
        }
    }


}
