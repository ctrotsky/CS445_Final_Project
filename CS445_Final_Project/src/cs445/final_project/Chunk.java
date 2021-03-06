/***************************************************************
* file: Chunk.java
* author: Colin Trotter
* author: Cristian-Garcia
* author: Rocky Qiu
* author: Mirza Hasan Baig
* class: CS 445 – Computer Graphics
*
* assignment: final program
* date last modified: 5/24/2017
*
* purpose: This class represents a cube in 3D space. It has a position.
*
****************************************************************/ 
package cs445.final_project;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk {
    
    static final int CHUNK_SIZE = 40;
    static final int CUBE_LENGTH = 2;
    static final int BASE_HEIGHT = 15;
    static final float PERSISTANCE = 0.3f;
    static final float NOISE_LEVEL = 25f;
    public int WATER_LEVEL = 14;
    private Cube[][][] Cubes;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int StartX, StartY, StartZ;
    private Random r;
    private int VBOTextureHandle;
    private Texture texture;
    
    public int seed;
    
    // method: render
    // purpose: pushes a matrix to be rendered that contains all of the Cubes within this chunk, then draws the arrays.
    public void render(){
        glPushMatrix();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glColorPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBindTexture(GL_TEXTURE_2D, 1);
        glTexCoordPointer(2,GL_FLOAT,0,0L);
        glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
        glPopMatrix();
    }
    
    // method: rebuildMesh
    // purpose: adds data from all cubes within this chunk to buffers to be rendered.
    public void rebuildMesh(int startX, int startY, int startZ) {
        r = new Random(seed);
        glDeleteBuffers(VBOColorHandle);
        glDeleteBuffers(VBOVertexHandle);
        glDeleteBuffers(VBOTextureHandle);
        glDeleteBuffers(GL_ARRAY_BUFFER);
        glDeleteBuffers(GL_STATIC_DRAW);
        
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        
        int height=0;
        int pheight[][]= new int[CHUNK_SIZE][CHUNK_SIZE];
        SimplexNoise noise;
        
        if (WATER_LEVEL > CHUNK_SIZE){
            WATER_LEVEL = CHUNK_SIZE;
        }
        if (WATER_LEVEL < 0){
            WATER_LEVEL = 0;
        }
        
        //int seed= 25*r.nextInt();
        noise=new SimplexNoise(CHUNK_SIZE,PERSISTANCE,seed);
        
        ClearChunk();
        
        for(int x=0;x<CHUNK_SIZE;x++)
        {
            for(int z=0;z<CHUNK_SIZE;z++)
            {   int i= (int)(startX+x*((CHUNK_SIZE-startX)/CHUNK_SIZE));
                int k= (int)(startZ+z*((CHUNK_SIZE-startZ)/CHUNK_SIZE));
             
                pheight[x][z]= (int)(BASE_HEIGHT + (NOISE_LEVEL *  noise.getNoise(x, z))); 
                if (pheight[x][z] > CHUNK_SIZE){
                    pheight[x][z] = CHUNK_SIZE;
                }
                for (int y = 0; y<pheight[x][z];y++){
                    pickBlockType(x,y,z,pheight[x][z]);
                }
                for (int y = 0; y<pheight[x][z];y++){
                    
                }
            }
        }
        
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE* CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE)* 6 * 12);
        
        for (int x = 0; x < CHUNK_SIZE; x += 1) {
            for (int z = 0; z < CHUNK_SIZE; z += 1) {              
                for(int y = 0; y < pheight[x][z]; y++){
                    VertexPositionData.put(createCube( (startX + x * CUBE_LENGTH), (startY + y * CUBE_LENGTH), (startZ + z * CUBE_LENGTH)));
                    VertexTextureData.put(createTexCube((float) 0, (float) 0, Cubes[(int)(x)][(int) (y)][(int) (z)], (startX + x * CUBE_LENGTH), (startY + y * CUBE_LENGTH),  (startZ + z * CUBE_LENGTH)));
                }
                for (int y = 0; y < WATER_LEVEL; y++){
                    if (Cubes[x][y][z] == null || Cubes[x][y][z].getBlockType() == Cube.BlockType.BlockType_Default){
                        //Fill empty space up to WATER_LEVEL with water
                        Cubes[x][y][z] = new Cube(Cube.BlockType.BlockType_Water); 
                        VertexPositionData.put(createCube( (startX + x * CUBE_LENGTH), (startY + y * CUBE_LENGTH),  (startZ + z * CUBE_LENGTH)));
                        VertexTextureData.put(createTexCube((float) 0, (float) 0, Cubes[(int)(x)][(int) (y)][(int) (z)], (startX + x * CUBE_LENGTH), (startY + y * CUBE_LENGTH),  (startZ + z * CUBE_LENGTH)));
                    }
                }
                pheight[x][z]=height;
            }
        }
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        VertexColorData.clear();
        VertexPositionData.clear();
        VertexTextureData.clear();
        VertexColorData = null;
        VertexPositionData = null;
        VertexTextureData = null;
    }
    
    // method: createCubeVertexCol
    // purpose: creates an array representing the colors of each cube within this chunk
    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
        }
        return cubeColors;
    }
    
    // method: createCube
    // purpose: returns a float array representing all faces of a new cube at the given position
    public float[] createCube(int x, int y, int z) {
    int offset = CUBE_LENGTH / 2;
    ArrayList<Integer> faces = new ArrayList<>();
    
    
    int posX = x / CUBE_LENGTH;
    int posY = y / CUBE_LENGTH;
    int posZ = z / CUBE_LENGTH;
    //System.out.println("x: " + posX + ", y: " + posY + ", z: " + posZ);
    
    
    //The following if statements decide whether to draw a face or not
    //Faces are drawn if they face towards an empty space, or they face out of the chunk
    //Faces are not drawn if they face a filled cube      
    if (posY + 1 >= CHUNK_SIZE || Cubes[posX][posY + 1][posZ] == null){
        // TOP QUAD
        faces.add(x + offset); faces.add(y + offset); faces.add(z);
        faces.add(x - offset); faces.add(y + offset); faces.add(z);
        faces.add(x - offset); faces.add(y + offset); faces.add(z - CUBE_LENGTH);
        faces.add(x + offset); faces.add(y + offset); faces.add(z - CUBE_LENGTH);
    }
    if (posY - 1 < 0 || Cubes[posX][posY - 1][posZ] == null){
        // BOTTOM QUAD
        faces.add(x + offset); faces.add(y - offset); faces.add(z - CUBE_LENGTH);
        faces.add(x - offset); faces.add(y - offset); faces.add(z - CUBE_LENGTH);
        faces.add(x - offset); faces.add(y - offset); faces.add(z);
        faces.add(x + offset); faces.add(y - offset); faces.add(z);
    }
    if (posZ - 1 < 0 || Cubes[posX][posY][posZ - 1] == null){
        // FRONT QUAD
        faces.add(x + offset); faces.add(y + offset); faces.add(z - CUBE_LENGTH);
        faces.add(x - offset); faces.add(y + offset); faces.add(z - CUBE_LENGTH);
        faces.add(x - offset); faces.add(y - offset); faces.add(z - CUBE_LENGTH);
        faces.add(x + offset); faces.add(y - offset); faces.add(z - CUBE_LENGTH);
    }
    if (posZ + 1 >= CHUNK_SIZE || Cubes[posX][posY][posZ + 1] == null){
        // BACK QUAD
        faces.add(x + offset); faces.add(y - offset); faces.add(z);
        faces.add(x - offset); faces.add(y - offset); faces.add(z);
        faces.add(x - offset); faces.add(y + offset); faces.add(z);
        faces.add(x + offset); faces.add(y + offset); faces.add(z);
    }
    if (posX - 1 < 0 || Cubes[posX - 1][posY][posZ] == null){
        // LEFT QUAD
        faces.add(x - offset); faces.add(y + offset); faces.add(z - CUBE_LENGTH);
        faces.add(x - offset); faces.add(y + offset); faces.add(z);
        faces.add(x - offset); faces.add(y - offset); faces.add(z);
        faces.add(x - offset); faces.add(y - offset); faces.add(z - CUBE_LENGTH);
    }
    if (posX + 1 >= CHUNK_SIZE || Cubes[posX + 1][posY][posZ] == null){
        // RIGHT QUAD
        faces.add(x + offset); faces.add(y + offset); faces.add(z);
        faces.add(x + offset); faces.add(y + offset); faces.add(z - CUBE_LENGTH);
        faces.add(x + offset); faces.add(y - offset); faces.add(z - CUBE_LENGTH);
        faces.add(x + offset); faces.add(y - offset); faces.add(z);
    }
    
    
    
    
    
    float[] facesArray = new float[faces.size()];
    
    for (int i = 0; i < faces.size(); i++){
        facesArray[i] = faces.get(i);
    }
    
    return facesArray;

    
    
//    return new float[] {
//        // TOP QUAD
//        x + offset, y + offset, z,
//        x - offset, y + offset, z,
//        x - offset, y + offset, z - CUBE_LENGTH,
//        x + offset, y + offset, z - CUBE_LENGTH,
//        // BOTTOM QUAD
//        x + offset, y - offset, z - CUBE_LENGTH,
//        x - offset, y - offset, z - CUBE_LENGTH,
//        x - offset, y - offset, z,
//        x + offset, y - offset, z,
//        // FRONT QUAD
//        x + offset, y + offset, z - CUBE_LENGTH,
//        x - offset, y + offset, z - CUBE_LENGTH,
//        x - offset, y - offset, z - CUBE_LENGTH,
//        x + offset, y - offset, z - CUBE_LENGTH,
//        // BACK QUAD
//        x + offset, y - offset, z,
//        x - offset, y - offset, z,
//        x - offset, y + offset, z,
//        x + offset, y + offset, z,
//        // LEFT QUAD
//        x - offset, y + offset, z - CUBE_LENGTH,
//        x - offset, y + offset, z,
//        x - offset, y - offset, z,
//        x - offset, y - offset, z - CUBE_LENGTH,
//        // RIGHT QUAD
//        x + offset, y + offset, z,
//        x + offset, y + offset, z - CUBE_LENGTH,
//        x + offset, y - offset, z - CUBE_LENGTH,
//        x + offset, y - offset, z };
    }
    
    // method: createTexCube
    // purpose: returns a float array representing all textured faces of a new cube at the given position
    public float[] createTexCube(float x, float y, Cube cube, int posX, int posY, int posZ) {
        float offset = (1024f/16)/1024f;
        
        float[] topFace, bottomFace, frontFace, backFace, leftFace, rightFace;
        
        switch (cube.getBlockType().getID()) {
            case 0:
                //Grass
                topFace = getTextureFace(x,y,offset,2,9,false);
                bottomFace = getTextureFace(x,y,offset,2,0,false);
                frontFace = getTextureFace(x,y,offset,3,0,false);
                backFace = getTextureFace(x,y,offset,3,0,true);
                leftFace = getTextureFace(x,y,offset,3,0,false);
                rightFace = getTextureFace(x,y,offset,3,0,false);
                break;
            case 1:
                //Sand
                topFace = getTextureFace(x,y,offset,2,1,false);
                bottomFace = getTextureFace(x,y,offset,2,1,false);
                frontFace = getTextureFace(x,y,offset,2,1,false);
                backFace = getTextureFace(x,y,offset,2,1,false);
                leftFace = getTextureFace(x,y,offset,2,1,false);
                rightFace = getTextureFace(x,y,offset,2,1,false);
                break;
            case 2:
                //Water
                topFace = getTextureFace(x,y,offset,13,12,false);
                bottomFace = getTextureFace(x,y,offset,13,12,false);
                frontFace = getTextureFace(x,y,offset,13,12,false);
                backFace = getTextureFace(x,y,offset,13,12,false);
                leftFace = getTextureFace(x,y,offset,13,12,false);
                rightFace = getTextureFace(x,y,offset,13,12,false);
                break;
            case 3:
                //Dirt
                topFace = getTextureFace(x,y,offset,2,0,false);
                bottomFace = getTextureFace(x,y,offset,2,0,false);
                frontFace = getTextureFace(x,y,offset,2,0,false);
                backFace = getTextureFace(x,y,offset,2,0,false);
                leftFace = getTextureFace(x,y,offset,2,0,false);
                rightFace = getTextureFace(x,y,offset,2,0,false);
                break;
            case 4:
                //Stone
                topFace = getTextureFace(x,y,offset,1,0,false);
                bottomFace = getTextureFace(x,y,offset,1,0,false);
                frontFace = getTextureFace(x,y,offset,1,0,false);
                backFace = getTextureFace(x,y,offset,1,0,false);
                leftFace = getTextureFace(x,y,offset,1,0,false);
                rightFace = getTextureFace(x,y,offset,1,0,false);
                break;
            case 5:
                //Bedrock
                topFace = getTextureFace(x,y,offset,1,1,false);
                bottomFace = getTextureFace(x,y,offset,1,1,false);
                frontFace = getTextureFace(x,y,offset,1,1,false);
                backFace = getTextureFace(x,y,offset,1,1,false);
                leftFace = getTextureFace(x,y,offset,1,1,false);
                rightFace = getTextureFace(x,y,offset,1,1,false);
                break;
            default:
                topFace = getTextureFace(x,y,offset,2,9,false);
                bottomFace = getTextureFace(x,y,offset,2,0,false);
                frontFace = getTextureFace(x,y,offset,3,0,false);
                backFace = getTextureFace(x,y,offset,3,0,false);
                leftFace = getTextureFace(x,y,offset,3,0,false);
                rightFace = getTextureFace(x,y,offset,3,0,false);
        }
        
    //System.out.println("x: " + posX + ", y: " + posY + ", z: " + posZ);
    
    posX = posX / CUBE_LENGTH;
    posY = posY / CUBE_LENGTH;
    posZ = posZ / CUBE_LENGTH;
        
    if (!(posY + 1 >= CHUNK_SIZE) && Cubes[posX][posY + 1][posZ] != null){
        // TOP QUAD
        topFace = new float[0];
    }
    if (!(posY - 1 < 0) && Cubes[posX][posY - 1][posZ] != null){
        // BOTTOM QUAD
        bottomFace = new float[0];
    }
    if (!(posZ - 1 < 0) && Cubes[posX][posY][posZ - 1] != null){
        // FRONT QUAD
        frontFace = new float[0];
    }
    if (!(posZ + 1 >= CHUNK_SIZE) && Cubes[posX][posY][posZ + 1] != null){
        // BACK QUAD
        backFace = new float[0];
    }
    if (!(posX - 1 < 0) && Cubes[posX - 1][posY][posZ] != null){
        // LEFT QUAD
        leftFace = new float[0];
    }
    if (!(posX + 1 >= CHUNK_SIZE) && Cubes[posX + 1][posY][posZ] != null){
        // RIGHT QUAD
        rightFace = new float[0];
    }
        
        return mergeFaceArrays(topFace, bottomFace, frontFace, backFace, leftFace, rightFace);
        
    }
    
    // method: getTextureFace
    // purpose: returns a float array representing a single textured face of a cube.
    // The texture will be taken from the texture file at the coordinates given by textureX and textureY
    private static float[] getTextureFace(float x, float y, float offset, int textureX, int textureY, boolean flipVertical){
        if (flipVertical){
            return new float[] {
                    // TOP TEXTURE
                    x + offset*(textureX+1), y + offset*(textureY+1),
                    x + offset*(textureX), y + offset*(textureY+1),
                    x + offset*(textureX), y + offset*(textureY),
                    x + offset*(textureX+1), y + offset*(textureY),
            };
        }
        else{
            return new float[] {
                    // TOP TEXTURE
                    x + offset*(textureX), y + offset*(textureY),
                    x + offset*(textureX+1), y + offset*(textureY),
                    x + offset*(textureX+1), y + offset*(textureY+1),
                    x + offset*(textureX), y + offset*(textureY+1),
            };
        }
        
    }
    
    // method: mergeFaceArrays
    // purpose: merges the given arrays of textured faces into one array representing an entire textured cube
    private static float[] mergeFaceArrays(float[] topFace, float[] bottomFace, float[] frontFace, float[] backFace, float[] leftFace, float[] rightFace){        
        ArrayList<Float> mergedFaces = new ArrayList<>();
        
        for (float f : topFace){
            mergedFaces.add(f);
        }
        for (float f : bottomFace){
            mergedFaces.add(f);
        }
        for (float f : frontFace){
            mergedFaces.add(f);
        }
        for (float f : backFace){
            mergedFaces.add(f);
        }
        for (float f : leftFace){
            mergedFaces.add(f);
        }
        for (float f : rightFace){
            mergedFaces.add(f);
        }
        
        float[] mergedFacesArray = new float[mergedFaces.size()];
        
        for (int i = 0; i < mergedFaces.size(); i++){
            mergedFacesArray[i] = mergedFaces.get(i);
        }
        
        return mergedFacesArray;
        
//        return new float[] {
//            // TOP TEXTURE
//            topFace[0], topFace[1],
//            topFace[2], topFace[3],
//            topFace[4], topFace[5],
//            topFace[6], topFace[7],
//            // BOTTOM TEXTURE
//            bottomFace[0], bottomFace[1],
//            bottomFace[2], bottomFace[3],
//            bottomFace[4], bottomFace[5],
//            bottomFace[6], bottomFace[7],
//            // FRONT TEXTURE
//            frontFace[0], frontFace[1],
//            frontFace[2], frontFace[3],
//            frontFace[4], frontFace[5],
//            frontFace[6], frontFace[7],
//            // BACK TEXTURE
//            backFace[0], backFace[1],
//            backFace[2], backFace[3],
//            backFace[4], backFace[5],
//            backFace[6], backFace[7],
//            // LEFT TEXTURE
//            leftFace[0], leftFace[1],
//            leftFace[2], leftFace[3],
//            leftFace[4], leftFace[5],
//            leftFace[6], leftFace[7],
//            // RIGHT TEXTURE
//            rightFace[0], rightFace[1],
//            rightFace[2], rightFace[3],
//            rightFace[4], rightFace[5],
//            rightFace[6], rightFace[7],
//        };
    }
    
    // method: getCubeColor
    // purpose: returns the color of a given cube as a float array representing the RBG values based on the BlockType
    private float[] getCubeColor(Cube cube) {
        return new float[] { 1, 1, 1 };
    }
    
    // method: Chunk
    // purpose: constructor that creates all cubes within this chunk with a random BlockType, then builds the mesh for this chunk
    public Chunk(int startX, int startY, int startZ, int seed) {
        this.seed = seed;
        try{
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain.png"));
        }
        catch(Exception e)
        {
            System.out.print("ER-ROAR!");
        }
        r = new Random(seed);
        Cubes = new Cube[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        rebuildMesh(startX, startY, startZ);
        
    }
    
    // method: pickBlockType
    // purpose: sets the cube at the given x,y,z location to a block type decided by it's height
    private void pickBlockType(int x, int y, int z, int height){
        if (y == 0){
            //bottom layer should be all bedrock
            Cubes[x][y][z] = new Cube(Cube.BlockType.BlockType_Bedrock); 
        }
        else if (y > 0 && y < height - 1){
            //middle layers should be stone or dirt
            float blockType = r.nextFloat();
            if (blockType > 0.5){
                Cubes[x][y][z] = new Cube(Cube.BlockType.BlockType_Stone); 
            }
            else{
                Cubes[x][y][z] = new Cube(Cube.BlockType.BlockType_Dirt); 
            }
        }
        else {
            //top layer should be grass unless it is at water level or below it
            if (y < WATER_LEVEL){
                Cubes[x][y][z] = new Cube(Cube.BlockType.BlockType_Sand); 
            }
            else{
                Cubes[x][y][z] = new Cube(Cube.BlockType.BlockType_Grass); 
            }
        }
    }
    
    private void ClearChunk(){
        for (int x = 0; x < CHUNK_SIZE; x++){
            for (int y = 0; y < CHUNK_SIZE; y++){
                for (int z = 0; z < CHUNK_SIZE; z++){
                    Cubes[x][y][z] = null;
                }
            }
        }
    }
    
}
